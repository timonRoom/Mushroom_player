package pager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import com.example.activity.Musicdownloadactivity;
import com.example.mode.MusicDownloadMode;
import com.example.mode.Search_lrcModel;
import com.example.tools.SearchLrcAndDownload;

import android.R;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import base.Basepage;
import pager.Musicpager.viewhander;

public class MessPager extends Fragment{
	protected static final int UPDATAVIEW = 0;
	protected static final int SHOWSEARCHRESULT=1;
	private ListView lv_netmusicpage_list;
	private Context context;
	private EditText et_search_context;
	private Button btn_search;
	private ImageView iv_pictop;
	private ArrayList<Search_lrcModel> list = null;
	private List<MusicDownloadMode> searchlist;
	private boolean issearch = false;
	private TextView tv_search_schedule;
	public MessPager(Context context) {
		super();
		this.context = context;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = View.inflate(context, com.example.multimedia_player.R.layout.netmusicpager, null);
		et_search_context = (EditText) view.findViewById(com.example.multimedia_player.R.id.et_search_context);
		iv_pictop = (ImageView) view.findViewById(com.example.multimedia_player.R.id.iv_pictop);
		tv_search_schedule= (TextView) view.findViewById(com.example.multimedia_player.R.id.tv_search_schedule);
		lv_netmusicpage_list = (ListView) view.findViewById(com.example.multimedia_player.R.id.lv_netmusicpage_list);
		btn_search =(Button) view.findViewById(com.example.multimedia_player.R.id.btn_search);
		lv_netmusicpage_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (issearch) {
					String songid = searchlist.get(position).getSongid();
					Intent intent = new Intent();
					intent.setClass(context, Musicdownloadactivity.class);
					intent.putExtra("songid", songid);
					startActivity(intent);
				}else {
					String songid = list.get(position).getSongid();
					Intent intent = new Intent();
					intent.setClass(context, Musicdownloadactivity.class);
					intent.putExtra("songid", songid);
					startActivity(intent);
				}
			}
		});

		btn_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchlist = new ArrayList<MusicDownloadMode>();
				iv_pictop.setVisibility(View.GONE);
				tv_search_schedule.setVisibility(View.VISIBLE);
				tv_search_schedule.setText("正在搜索...");
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (et_search_context.getText()!=null) {
							String key = et_search_context.getText().toString();
							SearchLrcAndDownload searchLrcAndDownload = new SearchLrcAndDownload();
							String xml = searchLrcAndDownload.searchkey(key);
							try {
								List<String >songidlist = searchLrcAndDownload.parserequexml(xml);
								for (int i = 2; i < songidlist.size(); i++) {
									MusicDownloadMode mode = getsonginfo(songidlist.get(i));
									searchlist.add(mode);
									mode = new MusicDownloadMode();
								}
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
						handler.sendEmptyMessage(SHOWSEARCHRESULT);
					}
				}).start();
			}
		});
		setdataforlistview();
		return view;
	}
	private void setdataforlistview() {
		// TODO Auto-generated method stub
		getdatafromnet();

	}
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATAVIEW:
				NetMusicAdapter adapter = new NetMusicAdapter();
				lv_netmusicpage_list.setAdapter(adapter);
				break;
			case SHOWSEARCHRESULT:
				tv_search_schedule.setText("搜索完成!");
				Searchadapter sadapter = new Searchadapter();
				lv_netmusicpage_list.setAdapter(sadapter);
				issearch=true;
				
				break;
			}
		}

	};
	private void getdatafromnet() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				StringBuffer sBuffer =new StringBuffer();
				String path = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.billboard.billList&format=json&type=2&offset=0&size=50";
				try {
					URL url = new URL(path);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					InputStream inputStream = connection.getInputStream();
					if (connection.getResponseCode()==200) {
						BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));
						String line = "";
						while ((line = bReader.readLine())!=null) {
							sBuffer.append(line);
							sBuffer.append("\n");
						}
						inputStream.close();
					}else{
						Log.e("TAG", "网络访问失败"+connection.getResponseCode());
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (sBuffer!=null) {
					String result  = sBuffer.toString();
					parsejsondata(result);
					handler.sendEmptyMessage(UPDATAVIEW);
				}
			}
		}).start();
	}

	protected void parsejsondata(String result) {
		list = new ArrayList<Search_lrcModel>();
		try {
			JSONObject jObject = new JSONObject(result);
			JSONArray songlist = jObject.getJSONArray("song_list");
			for (int i = 0; i < 50; i++) {
				Search_lrcModel sModel = new Search_lrcModel();
				JSONObject item =(JSONObject) songlist.get(i);
				sModel.setArtistName(item.getString("artist_name"));
				sModel.setSongName(item.getString("title"));
				sModel.setSongid(item.getString("song_id"));
				sModel.setHot("热度值："+item.getString("hot"));
				list.add(sModel);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private MusicDownloadMode getsonginfo(String songid) throws IOException {
		final String id = songid;
		StringBuffer sBuffer= new StringBuffer("");
		String lrcpath = "http://music.baidu.com/data/music/links?songIds={"+id+"}";
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
		return parsejson(sBuffer.toString());
	}

	public MusicDownloadMode  parsejson(String lrcjson) {
		MusicDownloadMode sModel = new MusicDownloadMode();
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
			sModel.setSongid(songmesage.getString("songId"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sModel;
	}
	class NetMusicAdapter extends BaseAdapter{

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
				view = View.inflate(context, com.example.multimedia_player.R.layout.netmusic_listview_adapter, null);
				hander = new viewhander();
				hander.tv_top_artistname = (TextView) view.findViewById(com.example.multimedia_player.R.id.tv_top_artistname);
				hander.tv_top_songname = (TextView) view.findViewById(com.example.multimedia_player.R.id.tv_top_songname);
				hander.tv_top_number = (TextView) view.findViewById(com.example.multimedia_player.R.id.tv_top_number);
				hander.tv_top_hot = (TextView) view.findViewById(com.example.multimedia_player.R.id.tv_top_hot);
				view.setTag(hander);
			}
			//hander.tv_duration.setText(list.get(position).getDuration()+"");
			hander.tv_top_artistname.setText(list.get(position).getArtistName());
			hander.tv_top_songname.setText(list.get(position).getSongName());
			hander.tv_top_number.setText(position+1+"");
			hander.tv_top_hot.setText(list.get(position).getHot());
			return view;
		}
		public  class viewhander{
			private TextView tv_top_artistname;
			private TextView tv_top_songname;
			private TextView tv_top_number;
			private TextView tv_top_hot;
		}

	}
	class Searchadapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return searchlist.size();
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
				view = View.inflate(context, com.example.multimedia_player.R.layout.netmusic_listview_adapter, null);
				hander = new viewhander();
				hander.tv_top_artistname = (TextView) view.findViewById(com.example.multimedia_player.R.id.tv_top_artistname);
				hander.tv_top_songname = (TextView) view.findViewById(com.example.multimedia_player.R.id.tv_top_songname);
				hander.tv_top_number = (TextView) view.findViewById(com.example.multimedia_player.R.id.tv_top_number);
				hander.tv_top_hot = (TextView) view.findViewById(com.example.multimedia_player.R.id.tv_top_hot);
				view.setTag(hander);
			}
			//hander.tv_duration.setText(list.get(position).getDuration()+"");
			hander.tv_top_artistname.setText(searchlist.get(position).getArtistName());
			hander.tv_top_songname.setText(searchlist.get(position).getSongName());
			hander.tv_top_number.setText(position+1+"");
			hander.tv_top_hot.setText(searchlist.get(position).getAlbumName());
			return view;
		}
		public  class viewhander{
			private TextView tv_top_artistname;
			private TextView tv_top_songname;
			private TextView tv_top_number;
			private TextView tv_top_hot;
		}
	}



}
