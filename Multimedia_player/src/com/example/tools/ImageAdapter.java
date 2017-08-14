package com.example.tools;

import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.example.mode.MoveMode;
import com.example.multimedia_player.R;
import com.android.volley.toolbox.Volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {  
	private Context context;
	ImageLoader mImageLoader;  
	private List<MoveMode> movieModes;
    public ImageAdapter(Context context, List<MoveMode> movieModes) {
		super();
		this.context = context;
		this.movieModes = movieModes;
		RequestQueue queue = Volley.newRequestQueue(context);  
		mImageLoader = new ImageLoader(queue, new BitmapCache());  
	}
    
  
    /** 
     * 使用LruCache来缓存图片 
     */  
    public class BitmapCache implements ImageCache {  
  
        private LruCache<String, Bitmap> mCache;  
  
        public BitmapCache() {  
            // 获取应用程序最大可用内存  
            int maxMemory = (int) Runtime.getRuntime().maxMemory();  
            int cacheSize = maxMemory / 8;  
            mCache = new LruCache<String, Bitmap>(cacheSize) {  
                @Override  
                protected int sizeOf(String key, Bitmap bitmap) {  
                    return bitmap.getRowBytes() * bitmap.getHeight();  
                }  
            };  
        }

		@Override
		public Bitmap getBitmap(String url) {
			return mCache.get(url); 
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {
			mCache.put(url, bitmap);
		}  
  
    }

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
		String url = movieModes.get(position).getCoverImg();
		 NetworkImageView image = (NetworkImageView) view.findViewById(R.id.iv_movie_coverImg);  
	        image.setDefaultImageResId(R.drawable.movie_model);  
	        image.setErrorImageResId(R.drawable.movie_model);  
	        image.setImageUrl(url, mImageLoader);  
		return view;
	}  
	public  class viewhander{
		private com.android.volley.toolbox.NetworkImageView  iv_movie_coverImg;
		private TextView tv_moviename;
		private TextView tv_moviesummary;
		private TextView tv_movielength;
	}
  
}  
