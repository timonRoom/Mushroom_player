package com.example.activity;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.example.mode.LrcModel;
import com.example.mode.RadioModel;
import com.example.mode.Search_lrcModel;
import com.example.multimedia_player.R;
import com.example.tools.PraseLrc;
import com.example.tools.SearchLrcAndDownload;
import com.example.tools.TranslateTime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import service.music_playerservice;
import service.music_playerservice.binder;
import view.LrcShowView;

public class music_playeractivity extends Activity implements OnClickListener, OnSeekBarChangeListener ,ServiceConnection{
	protected static final int MESGPROGRESS = 0;
	protected static final int BEGIN = 1;
	protected static final int SETDATA = 2;
	protected static final int SHOWLRC = 3;
	private static final int SEARCHLRC = 4;
	private static final int SELECTRESULT = 5;
	private static final int DOWNLOADLRC= 6;
	private static final String TAG = "music_playeractivity";
	private TextView tv_auther;
	public static Servicehander servicehander;
	private List<RadioModel> list =null;
	private  SeekBar music_player_seekbar;
	private ImageButton ib_music_start;
	private ImageButton iv_music_last;
	private ImageButton ib_music_next;
	private TextView tv_music_current;
	private TextView tv_music_toaltime;
	private view.LrcShowView tv_music_src;
	private TextView tv_music_name;
	private binder mybinder;
	private int position=0;
	private int current = 0;
	private String search_musicname;
	private String search_musicartist;
	private boolean isPlaying = true;
	protected static final int READY=1;
	

	private boolean isnotification = false;
	TranslateTime translat = new TranslateTime();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "onCreate");
		setContentView(R.layout.music_activity);
		tv_auther = (TextView) findViewById(R.id.tv_auther);
		music_player_seekbar = (SeekBar) findViewById(R.id.music_player_seekbar);
		iv_music_last =(ImageButton) findViewById(R.id.iv_music_last);
		ib_music_next = (ImageButton) findViewById(R.id.ib_music_next);
		tv_music_src = (LrcShowView) findViewById(R.id.tv_music_src);
		tv_music_src.setOnClickListener(this);
		ib_music_next.setOnClickListener(this);
		iv_music_last.setOnClickListener(this);
		tv_music_name = (TextView) findViewById(R.id.tv_music_name);
		tv_music_toaltime =(TextView) findViewById(R.id.tv_music_toaltime);
		tv_music_current = (TextView) findViewById(R.id.tv_music_current);
		music_player_seekbar.setOnSeekBarChangeListener(this);
		ib_music_start =(ImageButton) findViewById(R.id.ib_music_start);
		ib_music_start.setOnClickListener(this);
		getData();
		startservice();

	}
	@Override
	protected void onDestroy() {
		unbindService(this);
		hander.removeCallbacksAndMessages(null);
		super.onDestroy();
	}
	private void startservice() {
		Intent intent= new Intent();
		intent.setClass(music_playeractivity.this, music_playerservice.class);
		if (isnotification) {
			bindService(intent, this, Context.BIND_AUTO_CREATE);
		}else {
			Bundle bundle =new Bundle();
			bundle.putSerializable("movielist", (Serializable) list);
			intent.putExtras(bundle);
			intent.setData(Uri);
			intent.putExtra("position", position);
			startService(intent);
			bindService(intent, this, Context.BIND_AUTO_CREATE);
		}
		//		intent.setDataAndType(Uri.parse(radioModel.getPath()), "video/*");
	}
	private Uri Uri;
	private boolean isnetresouce =false;
	private String time;
	private String songname;
	private String songartist;
	private String lrclink;
	private void getData() {
		isnotification = getIntent().getBooleanExtra("notification", false);
		if (isnotification) {
			if (isnetresouce) {
				
			}else {
				list = (List<RadioModel>) getIntent().getSerializableExtra("movielist");
				position = getIntent().getIntExtra("position", 0);
			}
		}else {
			if ((Uri= getIntent().getData())!=null) {
				isnetresouce = true;
				time= getIntent().getStringExtra("time");
				songname= getIntent().getStringExtra("songname");
				songartist = getIntent().getStringExtra("songartist");
				lrclink = getIntent().getStringExtra("lrclink");
			}else {
				list = (List<RadioModel>) getIntent().getSerializableExtra("movielist");
				position = getIntent().getIntExtra("position", 0);
				isnetresouce = false;
			}
		}
//		else if (isnotification) {
//			current = getIntent().getIntExtra("current", 0);
//		}else {
//			if (!isnetresouce) {
//				list = (List<RadioModel>) getIntent().getSerializableExtra("movielist");
//				position = getIntent().getIntExtra("position", 0);
//			}
//		}
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_music_start:
			if (isPlaying) {
				ib_music_start.setImageResource(R.drawable.start);
				isPlaying= false;
				hander.removeMessages(MESGPROGRESS);
				hander.removeMessages(SHOWLRC);
				
			}else {
				ib_music_start.setImageResource(R.drawable.pause);
				isPlaying = true;
				hander.sendEmptyMessage(MESGPROGRESS);
				hander.sendEmptyMessage(SHOWLRC);
			}
			mybinder.stopcontrlo();
			break;
		case R.id.iv_music_last:
			mybinder.setlast();
			break;
		case R.id.ib_music_next:
			mybinder.setnext();
			break;
			case R.id.tv_music_src:
//				Toast.makeText(getApplicationContext(), "点击搜索歌词", Toast.LENGTH_LONG).show();
				showsearchdialog();
				
				break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (seekBar==music_player_seekbar&&fromUser==true) {
			mybinder.setprogress(progress);
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

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		Log.e(TAG, "onServiceConnected>>>>");
		mybinder = (binder) service;
		hander.sendEmptyMessage(SETDATA);
		servicehander = new Servicehander();
	}
	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub

	}
	 List<Search_lrcModel> search_lrcModels = null;
	private Handler hander = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case DOWNLOADLRC:
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						String path = mybinder.getpath();
//						String lrcPath = path.substring(0, path.lastIndexOf("."))+".lrc";
						String lrcPath = path.substring(0, path.lastIndexOf("."))+"1.mp3";
						SearchLrcAndDownload searcher = new SearchLrcAndDownload();
						String file;
						try {
//							Log.e("yourChoice>>>", yourChoice+"");
//							file = searcher.downloadlrctosdcard(search_lrcModels.get(yourChoice).getLrcLink(), lrcPath);
							file = searcher.downloadlrctosdcard(search_lrcModels.get(yourChoice).getSongLink(), lrcPath);
							Log.e(TAG, "下载lrc成功");
							Log.e(TAG, "lrc==="+file);
							search_lrcModels = null;
							yourChoice = -1;
							hander.sendEmptyMessage(BEGIN);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				break;
			case SELECTRESULT:
				showSelectSearchResult(search_lrcModels);
				break;
			case SEARCHLRC:
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						search_lrcModels = new ArrayList<Search_lrcModel>();
						SearchLrcAndDownload searcher = new SearchLrcAndDownload();
						String xml=searcher.searchlrc(search_musicartist, search_musicname);
						Log.e(TAG, "获取搜索xml结果");
						try {
							List<String > songidlist = searcher.parserequexml(xml);
							Log.e(TAG, "解析xml得到歌曲id信息");
							Search_lrcModel sModel =null;
							for (int i = 2; i < songidlist.size(); i++) {
								String lrcjson = searcher.getlrcmesage(songidlist.get(i));
								Log.e(TAG, "根据id获取歌曲信息json");
								sModel = searcher.parsejson(lrcjson);
								Log.e(TAG, "解析json得到歌词下载地址");
								search_lrcModels.add(sModel);
								sModel = new Search_lrcModel();
							}
							hander.sendEmptyMessage(SELECTRESULT);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SAXException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ParserConfigurationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				thread.start();
				break;
			case SHOWLRC:
				tv_music_src.getcurrentlrc(mybinder.getcurrentpostion());
				hander.removeMessages(SHOWLRC);
				hander.sendEmptyMessage(SHOWLRC);
				break;
			case MESGPROGRESS:
				//得到播放器的当前进度
				tv_music_current.setText(translat.calculatTimetominute(mybinder.getcurrentpostion()));
				music_player_seekbar.setProgress(mybinder.getcurrentpostion());
				hander.sendEmptyMessageDelayed(MESGPROGRESS, 1000);
				break;
			case BEGIN:
				initdata();
				hander.sendEmptyMessage(MESGPROGRESS);
				final PraseLrc praseLrc = new PraseLrc();
				
				if(isnetresouce){
					new Thread(new  Runnable() {
						public void run() {
							try {
								final String appPath = Environment.getExternalStorageDirectory().getPath()+"/mushroom/";
								Log.e("appPath", appPath);
								File appfile = new File(appPath);
								Log.e("appfile", appfile+"");
								if (!appfile.exists()) {
									appfile.mkdirs();
									Log.e("创建文件夹", appfile+"");
								}
								String lrcpath = appPath+songartist+"_"+songname+".lrc";
								SearchLrcAndDownload searcher = new SearchLrcAndDownload();
							String result = searcher.downloadlrctosdcard(lrclink, lrcpath);
							Log.e("result>>>", result);
							File  file = new File(result);
							praseLrc.readLrcFile(file);
							tv_music_src.setLrclist(praseLrc.getList());
							if (praseLrc.getisexistlrc()) {
								hander.sendEmptyMessage(SHOWLRC);
							}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}).start();
				}else{
					String path = mybinder.getpath();
//				Toast.makeText(getApplicationContext(), "path"+path, Toast.LENGTH_LONG).show();
					String lrcPathString = path.substring(0, path.lastIndexOf("."))+".lrc";
					int index = lrcPathString.lastIndexOf("/");
					String parentPath;  
					String lrcName;  
//		        if(index!=-1){  
					parentPath = lrcPathString.substring(0, index);  
					lrcName = lrcPathString.substring(index);  
//		         }  
					File  file = new File(lrcPathString);
					if (!file.exists()) {
						file = new File(path+".txt");
					}
					// 匹配Lyrics  
					if (!file.exists()) {  
						file = new File(parentPath + "/../" + "Lyrics/" + lrcName);  
					}  
//		        Log.i("Path", file.getAbsolutePath().toString());  
					
					// 匹配lyric  
					if (!file.exists()) {  
						file = new File(parentPath + "/../" + "lyric/" + lrcName);  
					}  
					Log.i("Path", file.getAbsolutePath().toString());  
					
					// 匹配Lyric  
					if (!file.exists()) {  
						file = new File(parentPath + "/../" + "Lyric/" + lrcName);  
					}  
					
					Log.i("Path", file.getAbsolutePath().toString());  
					
					// 匹配lyrics  
					if (!file.exists()) {  
						file = new File(parentPath + "/../" + "lyrics/" + lrcName);  
					}  
					
					praseLrc.readLrcFile(file);
					tv_music_src.setLrclist(praseLrc.getList());
					if (praseLrc.getisexistlrc()) {
						hander.sendEmptyMessage(SHOWLRC);
					}
				}
				break;
			case SETDATA:
				
				if (isnotification==false&&position!=mybinder.getposition()&&isnetresouce==false) {
					mybinder.setdata(list, position);
				}else if(isnetresouce){
					mybinder.setnetdata(Uri,songname);
				}
				hander.sendEmptyMessage(BEGIN);
				break;
			}
		}
	};
	public class Servicehander extends Handler{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case READY:
				hander.sendEmptyMessage(BEGIN);
				break;
			}
		}
	}
	/**
	 * 
	 * 初始化数据
	 * 
	 * */
	private void initdata(){
		//初始化总时间
		tv_music_toaltime.setText(translat.calculatTimetominute(mybinder.getmaxtime()));
		//初始化seekbar 最大值
		music_player_seekbar.setMax(mybinder.getmaxtime());
		if (isnetresouce) {
			tv_music_name.setText(songname);
			tv_auther.setText(songartist);
		}else {
		
			tv_music_name.setText(mybinder.getmusicname());
			tv_auther.setText(mybinder.getmusicartist());
		}

	}
	
	private void showsearchdialog() {
		/* @setView 装入自定义View ==> R.layout.dialog_customize
	     * 由于dialog_customize.xml只放置了一个EditView，因此和图8一样
	     * dialog_customize.xml可自定义更复杂的View
	     */
		AlertDialog.Builder customizeDialog = 
		new AlertDialog.Builder(music_playeractivity.this);
		final View dialogView = LayoutInflater.from(music_playeractivity.this)
				.inflate(R.layout.show_lrc_search_dialog,null);
		final EditText et_dialog_musicname = (EditText) dialogView.findViewById(R.id.et_dialog_musicname);
		et_dialog_musicname.setText("歌曲");
		final EditText et_dialog_musicartist = (EditText) dialogView.findViewById(R.id.et_dialog_musicartist);
		et_dialog_musicartist.setText("歌手");
	    customizeDialog.setTitle("请确认搜索内容");
	    customizeDialog.setView(dialogView);
	    customizeDialog.setPositiveButton("确定",
	        new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            // 获取EditView中的输入内容
//	            Toast.makeText(getApplicationContext(),
//	            		et_dialog_musicname.getText().toString()+"   "+et_dialog_musicartist.getText().toString(),Toast.LENGTH_SHORT).show();
	            search_musicname = et_dialog_musicname.getText().toString();
	            search_musicartist = et_dialog_musicartist.getText().toString();
	            hander.sendEmptyMessage(SEARCHLRC);
	        }
	    });
	    customizeDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
	    customizeDialog.show();
	}
	
	int yourChoice ;
	private void showSelectSearchResult(List<Search_lrcModel> search_lrcModels){
		Search_lrcModel sModel = null;
		final String[] items =new String[search_lrcModels.size()] ;
		for (int i = 0; i < search_lrcModels.size(); i++) {
			items[i]=search_lrcModels.get(i).getArtistName()+""+search_lrcModels.get(i).getSongName();
//			Log.e("items", items[i]);
//			Log.e(TAG, search_lrcModels.get(i).getArtistName()+""+search_lrcModels.get(i).getSongName());
		}
	    AlertDialog.Builder singleChoiceDialog = 
	        new AlertDialog.Builder(music_playeractivity.this);
	    singleChoiceDialog.setTitle("请选择最匹配的结果");
	    // 第二个参数是默认选项，此处设置为0
	    singleChoiceDialog.setSingleChoiceItems(items, 0, 
	        new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            yourChoice = which;
	            Log.e("which>>>", which+"");
	        }
	    });
	    singleChoiceDialog.setPositiveButton("确定", 
	        new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	Log.e("yourChoice>>>", yourChoice+"");
	            if (yourChoice != -1) {
	                Toast.makeText(getApplicationContext(), 
	                "开始下载歌词" + items[yourChoice], 
	                Toast.LENGTH_SHORT).show();
	                hander.sendEmptyMessage(DOWNLOADLRC);
	            }
	        }
	    });
	    singleChoiceDialog.show();
	}
	
}
