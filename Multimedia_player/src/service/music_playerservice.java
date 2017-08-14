package service;

import java.io.IOException;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.List;

import com.example.activity.music_playeractivity;
import com.example.mode.RadioModel;
import com.example.multimedia_player.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class music_playerservice extends Service{
	private static final String TAG = "music_playerservice";
	private MediaPlayer player;
	private List<RadioModel> list =null;
	private int position=0;
	private boolean isplaying = true;
	protected int READY=1;
	private boolean isnetrescour = false;
	private  NotificationManager manger;
	private Uri uri;
	private String songname;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void startplayer() {
		player.reset();
		try {
			if (isnetrescour) {
				
				player.setDataSource(getApplicationContext(), uri);
			}else {
				
				player.setDataSource(list.get(position).getPath());
			}
			player.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	       player.start(); 
	       if (!isnetrescour) {
	    	   Intent intents = new Intent();
		       intents.setClass(this, music_playeractivity.class);
		       intents.putExtra("notification", true);
		       
		       Bundle bundle =new Bundle();
				bundle.putSerializable("movielist", (Serializable) list);
				intents.putExtras(bundle);
				intents.putExtra("position", position);
//				intents.putExtra("current", player.getCurrentPosition());
//				Log.e(TAG, "current>>>>"+player.getCurrentPosition());
		       PendingIntent pIntent = PendingIntent.getActivity(this, 1, intents, PendingIntent.FLAG_CANCEL_CURRENT);
		       Notification notification = new Notification.Builder(this)
		    		   .setSmallIcon(R.drawable.player_launch)
		    		   .setContentTitle("蘑菇影音")
		    		   .setContentText("正在播放: "+list.get(position).getName())
		    		   .setContentIntent(pIntent)
		    		   .build();
		       manger = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		       manger.notify(1, notification);
		}else {
			Intent intents = new Intent();
		       intents.setClass(this, music_playeractivity.class);
		       intents.putExtra("notification", true);
		       PendingIntent pIntent = PendingIntent.getActivity(this, 1, intents, PendingIntent.FLAG_CANCEL_CURRENT);
		       Notification notification = new Notification.Builder(this)
		    		   .setSmallIcon(R.drawable.player_launch)
		    		   .setContentTitle("蘑菇影音")
		    		   .setContentText("正在播放: "+songname)
		    		   .setContentIntent(pIntent)
		    		   .build();
		       manger = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		       manger.notify(1, notification);
		}
	       
	       
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.e(TAG, "onBind");
		return new binder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		player = new MediaPlayer();
		player.setOnPreparedListener(new OnPreparedListener(){

			@Override
			public void onPrepared(MediaPlayer mp) {
				music_playeractivity.servicehander.sendEmptyMessage(READY);
			}
		});
		player.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				music_playeractivity.servicehander.sendEmptyMessage(READY);
			}
		});
	}

	public class binder extends Binder{
		public int getmaxtime(){
			return player.getDuration();
		}
		public int getcurrentpostion(){
			return player.getCurrentPosition();
		}
		public String  getmusicname(){
			return list.get(position).getName();
		}
		public String  getmusicartist(){
			return list.get(position).getArtist();
		}
		public void setdata(List<RadioModel> arrylist,int pos){
			list = arrylist;
			position = pos;
			isnetrescour = false;
			startplayer();
		}
		public void setnetdata(Uri data,String name){
			isnetrescour = true;
			songname = name ;
			uri = data;
			startplayer();
		}
		public void stopcontrlo(){
			if (isplaying==true) {
				player.pause();
				isplaying= false;
			}else {
				player.start();
				isplaying = true;
			}
		}
		public void setprogress(int progress) {
			player.seekTo(progress);
		}
		public void setlast(){
			if (list!=null&&list.size()>0) {
				position-=1;
				if (position<=0) {
					position =list.size()-1;
				}
				startplayer();
			}
		}
		public void setnext(){
			if (list!=null&&list.size()>0) {
				position+=1;
				if (position>=list.size()) {
					position =0;
				}
				startplayer();
			}
		}
		public int getposition(){
			return position;
			
		}
		public String getpath(){
			return list.get(position).getPath();
			
		}
	}
}
