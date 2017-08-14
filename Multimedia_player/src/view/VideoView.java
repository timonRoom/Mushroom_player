package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class VideoView extends android.widget.VideoView {
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	}

	public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public VideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VideoView(Context context) {
		super(context);
	}
	public void setSize(int videowidth,int videohigth ) {
		ViewGroup.LayoutParams params = getLayoutParams();
		params.width = videowidth;
		params.height =videohigth;
		setLayoutParams(params);
	}

}
