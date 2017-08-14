package view;

import java.util.ArrayList;
import java.util.List;

import com.example.mode.LrcModel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.icu.text.BreakIterator;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class LrcShowView  extends TextView {


	public LrcShowView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initview (context);
	}

	public LrcShowView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		// TODO Auto-generated constructor stub
	}

	private int width;
	private int higth;
	private static List<LrcModel> lrclist;
	private Paint paint;
	private Paint whitepaint; 
	private static int index;
	private int texthight ;
	public List<LrcModel> getLrclist() {
		return lrclist;
	}

	public void setLrclist(List<LrcModel> lrclist) {
		this.lrclist = lrclist;
	}

	public LrcShowView(Context context) {
		this(context,null);
		
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		width=w;
		higth=h;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (lrclist!=null&&lrclist.size()>0) {
			//绘制歌词
			//绘制当前部分
			String currentcontext = lrclist.get(index).getContent();
			canvas.drawText(currentcontext, width/2, higth/2, paint);
			//绘制前面部分
			int tempY = higth/2;
			for (int i = index-1; i >=0; i--) {
				String precontext = lrclist.get(i).getContent();
				tempY = tempY - texthight;
				if (tempY<0) {
					break;
				}
				canvas.drawText(precontext, width/2, tempY, whitepaint);
			}
			//绘制前面部分
			tempY = higth/2;
			for (int i = index+1; i<lrclist.size(); i++) {
				String nextcontext = lrclist.get(i).getContent();
				tempY = tempY +texthight;
				if (tempY>=higth) {
					break;
				}
				canvas.drawText(nextcontext, width/2, tempY, whitepaint);
			}
			
			
		}else {
			//没有歌词
			canvas.drawText("无匹配歌词,点击手动匹配", width/2, higth/2, paint);
		}
	}

	private void initview(Context context) {
		texthight = dip2px(context, 40);
		paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setTextSize(dip2px(context, 25));
		paint.setAntiAlias(true);
		paint.setTextAlign(Align.CENTER);
		
		
		whitepaint = new Paint();
		whitepaint.setColor(Color.BLACK);
		whitepaint.setTextSize(dip2px(context, 20));
		whitepaint.setAntiAlias(true);
		whitepaint.setTextAlign(Align.CENTER);
		
//		lrclist = new ArrayList<LrcModel>();
//		for (int i = 0; i < 100; i++) {
//			LrcModel lrcModel = new LrcModel();
//			lrcModel.setContent(i+"西瓜"+i);
//			lrcModel.setPointtime(i*1000);
//			lrcModel.setSleeptime((i+1500));
//			lrclist.add(lrcModel);
//		}
		
	}

	public  void getcurrentlrc(int currentpostion) {
		if (lrclist==null||lrclist.size()==0) {
			return;
		}
		for (int i = 1; i < lrclist.size(); i++) {
			if (currentpostion<lrclist.get(i).getPointtime()) {
				
				int j=i-1;
				if (currentpostion>lrclist.get(j).getPointtime()) {
					index = j;
				}
			}
		}
		invalidate();
	}
    /** 
    * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
    */  
    public static int dip2px(Context context, float dpValue) {  
      final float scale = context.getResources().getDisplayMetrics().density;  
      return (int) (dpValue * scale + 0.5f);  
    }  
      
    /** 
    * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
    */  
    public static int px2dip(Context context, float pxValue) {  
      final float scale = context.getResources().getDisplayMetrics().density;  
      return (int) (pxValue / scale + 0.5f);  
    }   

}
