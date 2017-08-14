package com.example.tools;

import android.content.Context;
import android.net.TrafficStats;

public class GetNetSpeed {
	Context context;
	private long lastTotalRxBytes = 0;
	private long lastTimeStamp = 0;
	public GetNetSpeed(Context context) {
		super();
		this.context = context;
	}
	public  String  showNetSpeed() {
		long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid)==TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);
		long nowTimeStamp = System.currentTimeMillis();
		long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//ºÁÃë×ª»»
		lastTimeStamp = nowTimeStamp;
		lastTotalRxBytes = nowTotalRxBytes;
		return String.valueOf(speed) + " kb/s";
	}
}
