package pager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.toolbox.NetworkImageView;
import com.example.activity.movie_playerActivity;
import com.example.mode.MoveMode;
import com.example.mode.MusicDownloadMode;
import com.example.multimedia_player.R;
import com.example.tools.ImageAdapter;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NetMoviePager extends Fragment{
	private static final int UPDATAVIEW = 0;
	private Context context;
	private ListView lv_netmovie_listview;
	private ProgressBar pb_processbar;
	private TextView tv_loading;
	private List<MoveMode> movieModes;
	public NetMoviePager(Context context) {
		super();
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = View.inflate(context, R.layout.netmoviepager, null);
		pb_processbar = (ProgressBar) view.findViewById(R.id.pb_processbar);
		tv_loading = (TextView) view.findViewById(R.id.tv_loading);
		lv_netmovie_listview = (ListView) view.findViewById(R.id.lv_netmovie_listview);
		lv_netmovie_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				intent.setClass(context, movie_playerActivity.class);
				intent.setDataAndType(Uri.parse(movieModes.get(position).getUrl()), "video/*");
				startActivity(intent);
			}
		});
		getdatafornet();
		return view;
	}
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATAVIEW:
				Log.e("movieModes>>>", movieModes.size()+"");
				pb_processbar.setVisibility(View.GONE);
				tv_loading.setVisibility(View.GONE);
				ImageAdapter imAdapter = new ImageAdapter(context, movieModes);
				lv_netmovie_listview.setAdapter(imAdapter);
//				ListAdapter listAdapter = new ListAdapter();
//				lv_netmovie_listview.setAdapter(listAdapter);
				handler.removeMessages(UPDATAVIEW);
				break;
			}
		}

	};
	private void getdatafornet() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer sBuffer = new StringBuffer("");
				String path = "http://api.m.mtime.cn/PageSubArea/TrailerList.api";
				try {
					URL url = new URL(path);
					Log.e("url>>>", url+"");
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					if (connection.getResponseCode() == 200) {
						InputStream inputStream = connection.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
						String line = "";
						while ((line = reader.readLine())!=null) {
							sBuffer.append(line);
							sBuffer.append("\n");
						}
						inputStream.close();
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//				Log.e("sBuffer>>>", sBuffer.toString());
				parsejsondata(sBuffer.toString());
			}
		}).start();
	}

	protected void parsejsondata(String json) {
		movieModes = new ArrayList<MoveMode>();
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray trailers = jsonObject.getJSONArray("trailers");
			MoveMode mode = new MoveMode();
			for (int i = 0; i < trailers.length(); i++) {
				JSONObject item = (JSONObject) trailers.get(i);
				mode.setMovieName(item.getString("movieName"));
				mode.setCoverImg(item.getString("coverImg"));
				mode.setSummary(item.getString("summary"));
				mode.setUrl(item.getString("url"));
				mode.setVideoLength(item.getInt("videoLength"));
				movieModes.add(mode);
				mode = new MoveMode();
			}
			handler.sendEmptyMessage(UPDATAVIEW);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	class ListAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return movieModes.size();
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
				view = View.inflate(context, R.layout.netmovie_listview_item, null);
				hander = new viewhander();
				hander.iv_movie_coverImg = (NetworkImageView) view.findViewById(R.id.iv_movie_coverImg);
				hander.tv_moviesummary = (TextView) view.findViewById(R.id.tv_moviesummary);
				hander.tv_moviename = (TextView) view.findViewById(R.id.tv_moviename);
				hander.tv_movielength = (TextView) view.findViewById(R.id.tv_movielength);
				view.setTag(hander);
			}
			//hander.tv_duration.setText(list.get(position).getDuration()+"");
			int leng = movieModes.get(position).getVideoLength();
			hander.tv_movielength.setText("时长:"+leng/60+":"+leng%60);
			hander.tv_moviename.setText(movieModes.get(position).getMovieName());
			hander.tv_moviesummary.setText(movieModes.get(position).getSummary());
			//			try {
			//				URL url;
			//				url = new URL(movieModes.get(position).getCoverImg());
			//				hander.iv_movie_coverImg.setImageBitmap(BitmapFactory.decodeStream(url.openStream()));
			//			} catch (MalformedURLException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			} catch (IOException e) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}
			final MoveMode mode=movieModes.get(position);
			final String img = mode.getCoverImg();
			// <span style="color:#ff0000;">//重点1，为每个ImageView设置一个tag，值为图片网址（保证tag的唯一性）。</span>
			hander.iv_movie_coverImg.setTag(img);
			hander.iv_movie_coverImg.setImageResource(R.drawable.movie_model);
			Bitmap nbitmap =returnBitMap(img);/*网络请求来的bitmap*/

			// <span style="color:#ff0000;">//重点2，获得tag的值，与该item中缩放图片的网址进行比较</span>
			String tag = (String) hander.iv_movie_coverImg.getTag();
			//  <span style="color:#ff0000;">//如果这个imageview的值，和他应该放的图片的地址值一样，说明这个图片是属于这个ImageView的，可以加载。</span>    
			if(tag!=null&&tag.equals(img)){
				Log.e("setImageBitmap", "setImageBitmap");
				hander.iv_movie_coverImg.setImageBitmap(nbitmap);
				//	        }	
			}
			return view;

		}
	}
		public  class viewhander{
			private com.android.volley.toolbox.NetworkImageView  iv_movie_coverImg;
			private TextView tv_moviename;
			private TextView tv_moviesummary;
			private TextView tv_movielength;
		}
		private   Bitmap bitmap; 
		public Bitmap returnBitMap(String coverImg) {

			final String path = coverImg;
			new Thread(new Runnable() {
				@Override
				public void run() {
					URL myFileUrl = null;    
					try {
						myFileUrl = new URL(path);
						HttpURLConnection conn = (HttpURLConnection) myFileUrl    
								.openConnection();    
						conn.setDoInput(true);    
						conn.connect();    
						InputStream is = conn.getInputStream();    
						bitmap = BitmapFactory.decodeStream(is);    
						is.close(); 
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}    

				}    
			}).start();
			return bitmap;
		}

}
