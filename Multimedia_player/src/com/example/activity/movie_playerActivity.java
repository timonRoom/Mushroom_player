package com.example.activity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.example.mode.RadioModel;
import com.example.multimedia_player.R;
import com.example.tools.GetNetSpeed;
import com.example.tools.TranslateTime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import view.VideoView;

public class movie_playerActivity extends Activity {
	GetNetSpeed getNetSpeed = new GetNetSpeed(this);
	private VideoView vv_radio;
	private Uri uri;
	private ImageButton iv_startcontrol;
	private ImageButton iv_voice;
	private boolean hasvoice = true;
	private boolean isvisibity = false;
	private  boolean isfull = false;
	private SeekBar player_seekbar;
	private static final int mesgprogress = 0;
	private  static final int PLAYERCONTROL = 1;
	private static final int NETSPEED = 3;
	private static final int BUFFER = 4;
	private TextView tv_toaltime;
	private TextView tv_currenttime;
	private ImageButton iv_last;
	private ImageButton ib_next;
	private ImageButton ib_full_screen;
	private TextView tv_battery;
	private TextView tv_time;
	int screenWidth ;      
	int screenHeight ;  
	int videoWidth ;
	int videoHeight;
	private TextView tv_move_name;
	private int position=0;
	private SeekBar seekbar_voice;
	private RelativeLayout rl_control;
	private LinearLayout player_loading;
	private TextView buffer_netspeed;
	private ImageButton ib_player_type;   //切换万能播放器
	private GestureDetector gDetector;
	private TextView loading_netspeed;
	private AudioManager audioManager;
	private List<RadioModel> list =null;
	private int voice = 0;
	buatteryreceiver butreceiver =new buatteryreceiver();
	private IntentFilter iFilter = new IntentFilter();
	float starty =0;
	int touchrang = 0;
	TranslateTime  translateTime =new TranslateTime();
	private static int currentvoice;
	private int maxvoice;
	private  boolean isnet=false;
	private LinearLayout ll_buffer;
	private static final int NORMAL = 0;
	private static final int FULL = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movie_playeractivity);
		DisplayMetrics dm = new DisplayMetrics();       
		getWindowManager().getDefaultDisplay().getMetrics(dm);       
		screenWidth = dm.widthPixels;       
		screenHeight = dm.heightPixels; 
		player_loading = (LinearLayout) findViewById(R.id.player_loading);
		player_loading.setVisibility(View.VISIBLE);
		vv_radio = (VideoView) findViewById(R.id.vv_radio);
		tv_toaltime = (TextView) findViewById(R.id.tv_toaltime);
		tv_battery = (TextView) findViewById(R.id.tv_battery);
		iv_last = (ImageButton) findViewById(R.id.iv_last);
		ib_next = (ImageButton) findViewById(R.id.ib_next);
		ib_player_type = (ImageButton) findViewById(R.id.ib_player_type);
		buffer_netspeed = (TextView) findViewById(R.id.buffer_netspeed);
		loading_netspeed =(TextView) findViewById(R.id.loading_netspeed);
		seekbar_voice = (SeekBar) findViewById(R.id.seekbar_voice);
		ib_full_screen = (ImageButton) findViewById(R.id.ib_full_screen);
		tv_move_name = (TextView) findViewById(R.id.tv_move_name);
		tv_time = (TextView) findViewById(R.id.tv_time);
		ll_buffer = (LinearLayout) findViewById(R.id.ll_buffer);
		rl_control = (RelativeLayout) findViewById(R.id.rl_control);
		player_seekbar = (SeekBar) findViewById(R.id.player_seekbar);
		player_seekbar.setOnSeekBarChangeListener(new seekbarchange());
		tv_currenttime = (TextView) findViewById(R.id.tv_currenttime);
		iv_startcontrol = (ImageButton) findViewById(R.id.iv_startcontrol);
		/**
		 * 音量初始化
		 * */
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		currentvoice = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		maxvoice = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		seekbar_voice.setMax(maxvoice);
		seekbar_voice.setProgress(currentvoice);
		hander.sendEmptyMessage(NETSPEED);

		/**
		 * 播放和暂停控制按钮
		 * */
		iv_startcontrol.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				start_pursecontrol();
				delayed_controler_hid();
			}
		});

		/**
		 * 静音控制按钮
		 * */
		iv_voice = (ImageButton) findViewById(R.id.iv_voice);
		iv_voice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (hasvoice) {
					hasvoice = false;
					seekbar_voice.setProgress(0);
					iv_voice.setImageResource(R.drawable.voice_no);
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
					hasvoice = false;
				}else {
					hasvoice = true;
					iv_voice.setImageResource(R.drawable.voice);
					audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentvoice, 0);
					seekbar_voice.setProgress(currentvoice);
				}
				delayed_controler_hid();
			}
		});
		/**
		 * 切换播放器类型
		 * */
		ib_player_type.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "功能已屏蔽", Toast.LENGTH_SHORT).show();
//				changetypenotice();
			}
		});
		/**
		 * 播放上一个资源控制按钮
		 * */
		iv_last.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				player_loading.setVisibility(View.VISIBLE);
				if (list!=null&&list.size()>0) {
					position-=1;
					if (position<=0) {
						position =list.size()-1;
					}
					vv_radio.setVideoPath(list.get(position).getPath());
					tv_move_name.setText(list.get(position).getName());
				}else if (isnet) {
					vv_radio.setVideoURI(uri);
					tv_move_name.setText(uri.toString());
				}
				delayed_controler_hid();
			}
		});

		/**
		 * 播放下一个资源控制按钮
		 * */
		ib_next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				player_loading.setVisibility(View.VISIBLE);
				if (list!=null&&list.size()>0) {
					position+=1;
					if (position>=list.size()) {
						position =0;
					}
					vv_radio.setVideoPath(list.get(position).getPath());
					tv_move_name.setText(list.get(position).getName());
				}else if (isnet) {
					vv_radio.setVideoURI(uri);
					tv_move_name.setText(uri.toString());
				}
				delayed_controler_hid();
			}
		});
		/**
		 * 全屏播放控制按钮
		 * */
		ib_full_screen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isfull ==false) {
					setvideotype(FULL);
				}else {
					setvideotype(NORMAL);
				}
				delayed_controler_hid();
			}
		});
		/**
		 * 音量进度条
		 * */
		seekbar_voice.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (hasvoice) {
					currentvoice = progress;
				}else {
					hasvoice = true;
					iv_voice.setImageResource(R.drawable.voice);
				}
				if (progress ==0) {
					iv_voice.setImageResource(R.drawable.voice_no);
					hasvoice=false;
				}
				seekbar_voice.setProgress(progress);
				//第三个参数如果是1 则调用系统的调节进度条
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
				delayed_controler_hid();
			}
		});
		/**
		 * 视频缓冲处理
		 * **/
		vv_radio.setOnInfoListener(new OnInfoListener() {
			@Override
			public boolean onInfo(MediaPlayer mp, int what, int extra) {
				if (isnet) {
					
					switch (what) {
					case MediaPlayer.MEDIA_INFO_BUFFERING_START:
						ll_buffer.setVisibility(View.VISIBLE);
						hander.sendEmptyMessage(BUFFER);
						break;
						
					case MediaPlayer.MEDIA_INFO_BUFFERING_END:
						ll_buffer.setVisibility(View.GONE);
						hander.removeMessages(BUFFER);
						break;
					}
				}
				return true;
			}
		});
	
		//加载系统自带的控制条
		//vv_radio.setMediaController(new MediaController(this));
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				/**
				 * 获取视频列表
				 * */
				list = (List<RadioModel>) getIntent().getSerializableExtra("movielist");
				position = getIntent().getIntExtra("position", 0);

				if (list!=null&&list.size()>0) {
					/**
					 * 列表播放
					 * */

					vv_radio.setVideoPath(list.get(position).getPath());
					tv_move_name.setText(list.get(position).getName());
				}else if ((uri = getIntent().getData())!=null) {
					/**
					 * uri播放
					 * */

					isnet = isneturi(uri.toString());
					vv_radio.setVideoURI(uri);
					tv_move_name.setText(uri.toString());
				} else {
					Toast.makeText(getApplicationContext(), "无播放资源", Toast.LENGTH_LONG).show();
				}
				hid_playcontroler();
				//准备好监听
				vv_radio.setOnPreparedListener(new OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						hander.removeMessages(NETSPEED);
						player_loading.setVisibility(View.GONE);
						vv_radio.start();
						int duration = vv_radio.getDuration();
						player_seekbar.setMax(duration);
						tv_toaltime.setText(translateTime.calculatTime(duration));
						hander.sendEmptyMessage(mesgprogress);
						videoWidth = mp.getVideoWidth();
						videoHeight = mp.getVideoHeight();
						setvideotype(NORMAL);
					}
				});

				//播放错误监听
				vv_radio.setOnErrorListener(new OnErrorListener() {
					@Override
					public boolean onError(MediaPlayer mp, int what, int extra) {
						startallpurposeplayer();
					
						return true;
					}

				
				});
				
				//播放完成监听
				vv_radio.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						player_loading.setVisibility(View.VISIBLE);
						if (list!=null&&list.size()>0) {
							position+=1;
							if (position>=list.size()) {
								position =0;
							}
							vv_radio.setVideoPath(list.get(position).getPath());
							tv_move_name.setText(list.get(position).getName());
						}else if (isnet) {
							vv_radio.setVideoURI(uri);
							tv_move_name.setText(uri.toString());
						}

					}
				});

			}
		});
		thread.start();
		
	
		//注册电量广播
		iFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(butreceiver, iFilter);

		/**
		 * 手势识别器
		 * **/
		gDetector =new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){

			@Override
			public void onLongPress(MotionEvent e) {
				start_pursecontrol();
				delayed_controler_hid();
				super.onLongPress(e);
			}

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				if (isfull ==false) {
					setvideotype(FULL);
				}else {
					setvideotype(NORMAL);
				}
				delayed_controler_hid();
				return super.onDoubleTap(e);
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				if (isvisibity==true) {
					hid_playcontroler();
				}else {
					visibity_playcontroler();
				}
				//				delayed_controler_hid();
				return super.onSingleTapConfirmed(e);
			}

		});
	}
	protected void changetypenotice() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("将切换播放器解码类型重新播放");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startallpurposeplayer();
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}
	//判断是否是网络资源
	protected boolean isneturi(String uri) {
		boolean result =false;
		if (uri!=null) {
			if (uri.toLowerCase().startsWith("http")||uri.toLowerCase().startsWith("rtsp")||uri.toLowerCase().startsWith("mms")) {
				result = true;
			}
		}
		return result;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		DisplayMetrics dm = new DisplayMetrics();       
		getWindowManager().getDefaultDisplay().getMetrics(dm);       
		screenWidth = dm.widthPixels;       
		screenHeight = dm.heightPixels;  
		super.onConfigurationChanged(newConfig);
	}
	/**
	 * 控制面板延迟隐藏
	 * */
	protected void delayed_controler_hid() {
		hander.removeMessages(PLAYERCONTROL);
		visibity_playcontroler();
		hander.sendEmptyMessageDelayed(PLAYERCONTROL, 5000);
	}

	/**
	 * 屏幕双击控制播放暂停
	 * */
	protected void start_pursecontrol() {
		if (vv_radio.isPlaying()) {
			vv_radio.pause();
			iv_startcontrol.setImageResource(R.drawable.start);
			//iv_startcontrol.setBackgroundResource(R.drawable.start);
		}else {
			vv_radio.start();
			iv_startcontrol.setImageResource(R.drawable.pause);
			//iv_startcontrol.setBackgroundResource(R.drawable.pause);
		}
	}
	/**
	 * 全屏播放
	 * 
	 * */
	private void setvideotype(int type){
		switch (type) {
		case NORMAL:
			int width = screenWidth,height = screenHeight;
			if (videoWidth*height<width*videoHeight) {
				width = height*videoWidth/videoHeight;
			}else if (videoWidth*screenHeight>screenWidth*videoHeight) {
				height = width*videoHeight/videoWidth;
			}
			vv_radio.setSize(width, height);
			isfull=false;
			break;
		case FULL:
			vv_radio.setSize(screenWidth, screenHeight);
			isfull=true;
			break;
		}
	}
	/**
	 * 屏幕触摸事件监听
	 * */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gDetector.onTouchEvent(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			starty = event.getY();
			voice = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			touchrang = Math.min(screenHeight, screenWidth);
			hander.removeMessages(PLAYERCONTROL);
			break;
		case MotionEvent.ACTION_MOVE:
			float endy  =event.getY();
			float distancey = starty-endy;
			float delta = (distancey/touchrang)*maxvoice;
			int mvoice = (int) Math.min(Math.max(voice+delta, 0), maxvoice);
			if (delta!=0) {
				seekbar_voice.setProgress(mvoice);
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mvoice, 0);
			}
			break;
		case MotionEvent.ACTION_UP:
			hander.sendEmptyMessageDelayed(PLAYERCONTROL, 4000);
			break;
		}
		return super.onTouchEvent(event);
	}
	/**
	 * 手机按键监听
	 * */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode ==KeyEvent.KEYCODE_VOLUME_DOWN) {
			currentvoice--;
			seekbar_voice.setProgress(currentvoice);
			return true;
		}else if (keyCode ==KeyEvent.KEYCODE_VOLUME_UP) {
			currentvoice++;
			seekbar_voice.setProgress(currentvoice);
			return true;
		}
		delayed_controler_hid();
		return super.onKeyDown(keyCode, event);
	}


	@Override
	protected void onDestroy() {
		unregisterReceiver(butreceiver);
		hander.removeCallbacksAndMessages(null);
		super.onDestroy();
	}
	/**
	 * 电量监听及显示
	 * **/
	class buatteryreceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			int batterlevel = intent.getIntExtra("level",0);
			tv_battery.setText(batterlevel+"%");
		}

	}
	/**
	 * 视频进度
	 * **/
	class seekbarchange implements SeekBar.OnSeekBarChangeListener{

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (fromUser) {
				vv_radio.seekTo(progress);
				delayed_controler_hid();
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

	}
	/**
	 * 消息处理
	 * */
	private Handler hander = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case mesgprogress:
				//得到播放器的当前进度
				int currentPosition = vv_radio.getCurrentPosition();
				player_seekbar.setProgress(currentPosition);
				if (isnet) {
					int buffer = vv_radio.getBufferPercentage();
					int totalbuffer = buffer*player_seekbar.getMax();
					int	secondaryProgress = totalbuffer/100;
					player_seekbar.setSecondaryProgress(secondaryProgress);
				}
				removeMessages(mesgprogress);
				sendEmptyMessageDelayed(mesgprogress, 1000);
				tv_time.setText(getsysytemtime());
				tv_currenttime.setText(translateTime.calculatTime(currentPosition));
				break;

			case PLAYERCONTROL:
				hid_playcontroler();
				break;
			case NETSPEED:
				loading_netspeed.setText(getNetSpeed.showNetSpeed());
				hander.sendEmptyMessageDelayed(NETSPEED, 2000);
				break;
			case BUFFER:
				buffer_netspeed.setText(getNetSpeed.showNetSpeed());
				hander.sendEmptyMessageDelayed(BUFFER, 2000);
				break;
			}
		}

		private  String  getsysytemtime() {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
			return simpleDateFormat.format(new Date());
		}

	};
	
	/**启动万能播放器
	 * */
	public void startallpurposeplayer() {
		if (vv_radio!=null) {
			vv_radio.stopPlayback();
		}
		Intent intent = new Intent();
//		intent.setClass(getApplicationContext(), allpurpose_player_Activity.class);
		if (list!=null&&list.size()>0) {
			Bundle bundle =new Bundle();
			bundle.putSerializable("movielist", (Serializable) list);
			intent.putExtras(bundle);
			intent.putExtra("position", position);
		}else if (uri!=null) {
			intent.setData(uri);
		}
		startActivity(intent);
		finish();
	}
	
	protected void hid_playcontroler() {
		rl_control.setVisibility(View.GONE);
		isvisibity = false;
	}
	protected void visibity_playcontroler() {
		rl_control.setVisibility(View.VISIBLE);
		isvisibity = true;
	}
}
