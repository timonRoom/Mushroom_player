package com.example.mode;

public class LrcModel {
	private String content;
	private long sleeptime;
	private long pointtime;
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getSleeptime() {
		return sleeptime;
	}

	public void setSleeptime(long sleeptime) {
		this.sleeptime = sleeptime;
	}

	public long getPointtime() {
		return pointtime;
	}

	public void setPointtime(long pointtime) {
		this.pointtime = pointtime;
	}

	@Override
	public String toString() {
		return "LrcModel [content=" + content + ", sleeptime=" + sleeptime + ", pointtime=" + pointtime + "]";
	}
	
}
