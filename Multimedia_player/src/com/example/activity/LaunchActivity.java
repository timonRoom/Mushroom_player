package com.example.activity;

import com.example.multimedia_player.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

public class LaunchActivity extends Activity {
	private Handler hander;
	private boolean isenter = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		hander = new Handler();
		hander.postDelayed(new Runnable() {
			
			@Override
			public void run() {

				enter();
			}
		}, 2000);
	}
	protected void enter() {
		if (!isenter) {
			isenter=true;
			Intent intent = new Intent();
			intent.setClass(LaunchActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		enter();
		hander.removeCallbacksAndMessages(null);
		return super.onTouchEvent(event);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}
}
