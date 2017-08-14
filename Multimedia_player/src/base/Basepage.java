package base;

import android.content.Context;
import android.view.View;

public abstract class Basepage {
	public final Context context;
	public View rootview;
	public Basepage(Context context) {
		super();
		this.context = context;
	}
	public abstract View initview();
	public void initdata(){
		
	}
}
