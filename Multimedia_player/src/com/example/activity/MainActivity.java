package com.example.activity;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import base.Basepage;
import pager.MessPager;
import pager.MoviePager;
import pager.Musicpager;
import pager.NetMoviePager;

public class MainActivity extends Activity{
	private RadioGroup fg_bottom_item;
	private FrameLayout  fl_main;
	private List<Fragment> framlist;
	private ImageView img_search;
	private ImageView img_history;
	private int position = 0;
	private MoviePager moviepager;
	private Musicpager musicpager;
	private NetMoviePager netmoviepager;
	private MessPager messpager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(com.example.multimedia_player.R.layout.mainactivity);
		fg_bottom_item = (RadioGroup) findViewById(com.example.multimedia_player.R.id.fg_bottom_item);
		fg_bottom_item.check(com.example.multimedia_player.R.id.rb_moive);
		fl_main = (FrameLayout) findViewById(com.example.multimedia_player.R.id.fl_mian);
		img_search = (ImageView) findViewById(com.example.multimedia_player.R.id.img_search);
		framlist = new ArrayList<Fragment>();
		 moviepager = new MoviePager(this);
		 musicpager=new Musicpager(this);
		 netmoviepager=new NetMoviePager(this);
		 messpager=new MessPager(this);
		framlist.add(moviepager);
		framlist.add(musicpager);
		framlist.add(netmoviepager);
		framlist.add(messpager);
		setfragment();
		fg_bottom_item.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case com.example.multimedia_player.R.id.rb_moive:
					position = 0;
					break;
				case com.example.multimedia_player.R.id.rb_music:
					position = 1;
					break;
				case com.example.multimedia_player.R.id.rb_netmovie:
					position = 2;
					break;
				case com.example.multimedia_player.R.id.rb_message:
					position = 3;
					break;

				}
				//添加页面到Fragment中
				setfragment();
			}
		});
		img_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, AboutApp.class);
				startActivity(intent);
			}
		});
	}
	private  void setfragment() {
		//得到fragmentmanger对象
		FragmentManager manager = getFragmentManager();
		//开启事物
		FragmentTransaction beginTransaction = manager.beginTransaction();
		//替换
		beginTransaction.replace(com.example.multimedia_player.R.id.fl_mian, framlist.get(position)).commit();
		//提交事物

	}

}
