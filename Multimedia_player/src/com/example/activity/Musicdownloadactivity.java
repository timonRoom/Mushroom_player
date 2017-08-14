package com.example.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mode.MusicDownloadMode;
import com.example.mode.Search_lrcModel;
import com.example.multimedia_player.R;
import com.example.tools.TranslateTime;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;

public class Musicdownloadactivity extends Activity {
	protected static final int UPDATAVIEW = 0;
	protected static final int STARTDOWNLOAD = 1;
	protected static final int UPDATAPROCESS = 2;
	protected static final int STOP = 3;
	private TextView tv_result_songname;
	private TextView tv_result_artistname;
	private TextView tv_result_albumName;
	private TextView tv_result_songtime;
	private TextView tv_result_songsize;
	private Button btn_result_player;
	private Button btn_result_download;
	private String songid;
	private  NotificationManager manger;
	private TranslateTime translateTime = new TranslateTime();
	private MusicDownloadMode sModel ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_download_activity);
		tv_result_songname = (TextView) findViewById(R.id.tv_result_songname);
		tv_result_artistname = (TextView) findViewById(R.id.tv_result_artistname);
		tv_result_albumName = (TextView) findViewById(R.id.tv_result_albumName);
		tv_result_songtime = (TextView) findViewById(R.id.tv_result_songtime);
		tv_result_songsize = (TextView) findViewById(R.id.tv_result_songsize);
		btn_result_player = (Button) findViewById(R.id.btn_result_player);
		btn_result_download = (Button) findViewById(R.id.btn_result_download);
		btn_result_download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handler.sendEmptyMessage(STARTDOWNLOAD);
			}
		});
		btn_result_player.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Musicdownloadactivity.this, music_playeractivity.class);
				intent.setData(Uri.parse(sModel.getSongLink()));
				intent.putExtra("time", sModel.getTime());
				intent.putExtra("songname", sModel.getSongName());
				intent.putExtra("songartist", sModel.getArtistName());
				intent.putExtra("lrclink", sModel.getLrcLink());
				startActivity(intent);
			}
		});
		getsongid();
		try {
			getsonginfo();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATAVIEW:
				if (sModel!=null) {
					tv_result_songname.setText(sModel.getSongName());
					tv_result_artistname.setText(sModel.getArtistName());
					tv_result_albumName.setText(sModel.getAlbumName());
					tv_result_songtime.setText((sModel.getTime()/60+":"+(sModel.getTime()%60)));
					tv_result_songsize.setText(Formatter.formatFileSize(Musicdownloadactivity.this, sModel.getSize()));
				}
				break;
			case STARTDOWNLOAD:
				if (sModel!=null) {
					shownotification();
					download();
				}
				break;
				case UPDATAPROCESS:
					notification.contentView.setTextViewText(R.id.content_view_text1, "正在下载:   "+sModel.getSongName()+"_"+sModel.getArtistName()+""+(complete*100)/contentLength + "%");
					notification.contentView.setProgressBar(R.id.content_view_progress, contentLength, complete, false);
					 manger.notify(1, notification);
					 handler.removeMessages(UPDATAPROCESS);
					 handler.sendEmptyMessageDelayed(UPDATAPROCESS, 500);
					 break;
				case STOP:
					manger.cancel(1); 
					handler.removeMessages(STOP);
					handler.removeMessages(UPDATAPROCESS);
					break;
			}
		}
	};
	private  Notification notification;
	protected void shownotification() {
		Intent intents = new Intent();
	       intents.setClass(Musicdownloadactivity.this, Musicdownloadactivity.class);
	       intents.putExtra("songid", songid);
//			intents.putExtra("current", player.getCurrentPosition());
//			Log.e(TAG, "current>>>>"+player.getCurrentPosition());
	       PendingIntent pIntent = PendingIntent.getActivity(Musicdownloadactivity.this, 1, intents, PendingIntent.FLAG_CANCEL_CURRENT);
	        notification = new Notification();
	        notification.icon=R.drawable.player_launch;
	        notification.tickerText=("正在下载"+sModel.getSongName()+"_"+sModel.getArtistName());
	        notification.contentIntent= pIntent;
	        notification.contentView= new RemoteViews(getPackageName(), R.layout.download_notification);
	       manger = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	       manger.notify(1, notification);
	}

	private void getsonginfo() throws IOException {
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer sBuffer= new StringBuffer("");
				String lrcpath = "http://music.baidu.com/data/music/links?songIds={"+songid+"}";
				URL url;
				try {
					url = new URL(lrcpath);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					InputStream inputStream = connection.getInputStream();
					if (connection.getResponseCode()==200) {
						BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));
						String line="";
						while ((line = bReader.readLine())!=null) {
							sBuffer.append(line);
							sBuffer.append("\n");
						}
					}
					inputStream.close();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				parsejson(sBuffer.toString());
				handler.sendEmptyMessage(UPDATAVIEW);
			}
		}).start();
		
	}
	private int contentLength;
	private int complete;
	protected void download() {
		Log.e("download", "download");
		final String appPath = Environment.getExternalStorageDirectory().getPath()+"/mushroom/";
		Log.e("appPath", appPath);
		File appfile = new File(appPath);
		Log.e("appfile", appfile+"");
		if (!appfile.exists()) {
			appfile.mkdirs();
			Log.e("创建文件夹", appfile+"");
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (sModel.getSongLink().equals("")!=true) {
					String songlink = sModel.getSongLink();
					URL url;
					try {
						url = new URL(songlink);
						File songfile = new File(appPath+sModel.getArtistName()+"_"+sModel.getSongName()+".mp3");
						Log.e("歌地址", songfile+"");
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
						InputStream inputStream = connection.getInputStream();
						 contentLength = connection.getContentLength();
						if (connection.getResponseCode()==200) {
							OutputStream outputStream = new FileOutputStream(songfile);
							byte[] buffer=new byte[4*1024]; 
							int line = -1;
							complete = inputStream.read(buffer);
							handler.sendEmptyMessage(UPDATAPROCESS);
							while((line = inputStream.read(buffer))!=-1){  
								outputStream.write(buffer, 0, line);  
								complete+=line;
							}  
							outputStream.flush();
							outputStream.close();
							inputStream.close();
						}
						Log.e("歌地址", "ok");
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (sModel.getLrcLink().equals("")!=true) {
					String lrclink = sModel.getLrcLink();
					URL url;
					try {
						url = new URL(lrclink);
						File lrcfile = new File(appPath+sModel.getArtistName()+"_"+sModel.getSongName()+".lrc");
						Log.e("歌chi地址", lrcfile+"");
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
						InputStream inputStream = connection.getInputStream();
						if (connection.getResponseCode()==200) {
							OutputStream outputStream = new FileOutputStream(lrcfile);
							byte[] buffer=new byte[4*1024]; 
							int line = -1;
							while((line = inputStream.read(buffer))!=-1){  
								outputStream.write(buffer, 0, line);  
							}  
							outputStream.flush();
							outputStream.close();
							inputStream.close();
						}
						Log.e("歌chi地址", "ok");
						handler.sendEmptyMessage(STOP);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();;
		
		
	}
	public void  parsejson(String lrcjson) {
		 sModel = new MusicDownloadMode();
		try {
			JSONObject jsonObject = new JSONObject(lrcjson);
			JSONObject data = jsonObject.getJSONObject("data");
			JSONArray songlist = data.getJSONArray("songList");
			JSONObject songmesage = songlist.getJSONObject(0);
			 sModel.setAlbumName(songmesage.getString("albumName"));
			 sModel.setArtistName(songmesage.getString("artistName"));
			 sModel.setSongName(songmesage.getString("songName"));
			 sModel.setTime(songmesage.getInt("time"));
			 sModel.setSize(songmesage.getInt("size"));
			 sModel.setLrcLink(songmesage.getString("lrcLink"));
			 sModel.setSongLink(songmesage.getString("songLink"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void getsongid() {
		 songid = getIntent().getStringExtra("songid");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


}
