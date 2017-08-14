package pager;

import java.util.ArrayList;

import com.example.activity.movie_playerActivity;
import com.example.activity.music_playeractivity;
import com.example.mode.RadioModel;
import com.example.multimedia_player.R;
import com.example.tools.TranslateTime;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import base.Basepage;
import pager.MoviePager.myadapter;
import pager.MoviePager.viewhander;

public class Musicpager extends Fragment{
	private TextView tv_movie_text;
	private ListView lv_music_list;
	private ProgressBar pb_movie_bar;
	private Context context;
	private ArrayList<RadioModel> list = null;
	private myadapter madapter;
	TranslateTime  translateTime =new TranslateTime();
	public Musicpager(Context context) {
		super();
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = View.inflate(context, R.layout.music_pager, null);
		tv_movie_text = (TextView) view.findViewById(R.id.tv_movie_text);
		lv_music_list = (ListView) view.findViewById(R.id.lv_music_list);
		pb_movie_bar = (ProgressBar) view.findViewById(R.id.pb_movie_bar);
		lv_music_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				RadioModel radioModel = list.get(position);
				Intent intent= new Intent();
				intent.setClass(context, music_playeractivity.class);
				Bundle bundle =new Bundle();
				bundle.putSerializable("movielist", list);
				intent.putExtras(bundle);
				intent.putExtra("position", position);
//				intent.setDataAndType(Uri.parse(radioModel.getPath()), "video/*");
				startActivity(intent);
			}
		});
		getDataToListview();
		return view;
	}
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (list!=null&&list.size()>0) {
				madapter = new myadapter();
				lv_music_list.setAdapter(madapter);
				tv_movie_text.setVisibility(View.GONE);
			}else {
				tv_movie_text.setVisibility(View.VISIBLE);
			}
			pb_movie_bar.setVisibility(View.GONE);
		}
		
	};
	class myadapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			viewhander hander;
			View view;
			if (convertView !=null) {
				view = convertView;
				hander = (viewhander) view.getTag();
			}else {
				view = View.inflate(context, R.layout.musicpager_listview_item, null);
				hander = new viewhander();
				hander.iv_movie_model = (ImageView) view.findViewById(R.id.iv_movie_model);
				hander.tv_duration = (TextView) view.findViewById(R.id.tv_duration);
				hander.tv_name = (TextView) view.findViewById(R.id.tv_name);
				hander.tv_size = (TextView) view.findViewById(R.id.tv_size);
				view.setTag(hander);
			}
			//hander.tv_duration.setText(list.get(position).getDuration()+"");
			hander.tv_duration.setText(translateTime.calculatTime((int) list.get(position).getDuration()));
			hander.tv_name.setText(list.get(position).getName());
			hander.tv_size.setText(Formatter.formatFileSize(context, list.get(position).getSize()));
			return view;
		}
		
	}
	//时间转换
	//将毫秒转化为 时：分：秒 格式 ，例如  00:05:23

	 
	 
	public  class viewhander{
		private ImageView iv_movie_model;
		private TextView tv_name;
		private TextView tv_duration;
		private TextView tv_size;
	}
	private void getDataToListview() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				list = new ArrayList<RadioModel>();
				ContentResolver resolver = context.getContentResolver();
				Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				String[] projection = {
						MediaStore.Audio.Media.DISPLAY_NAME,//视频文件名称
						MediaStore.Audio.Media.DURATION,//视频时长
						MediaStore.Audio.Media.SIZE,//视频大小
						MediaStore.Audio.Media.DATA,//视频路径
						MediaStore.Audio.Media.ARTIST//视频作者
						};
				Cursor cursor = resolver.query(uri, projection, null, null, null);
				if (cursor!=null) {
					while (cursor.moveToNext()) {
						if (cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))>1024*1024) {
							RadioModel radioModel = new RadioModel();
							radioModel.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
							radioModel.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
							radioModel.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
							radioModel.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
							radioModel.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ARTIST)));
							list.add(radioModel);
						}
					}
				}
				cursor.close();
				handler.sendEmptyMessage(1);
			}
		});
		thread.start();
	}

}
