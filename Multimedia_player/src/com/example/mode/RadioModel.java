package com.example.mode;

import java.io.Serializable;

import android.provider.MediaStore;

public class RadioModel implements Serializable {
	private String name;//��Ƶ�ļ�����
	private long duration;//��Ƶʱ��
	private long size;//��Ƶ��С
	private String path;//��Ƶ·��
	@Override
	public String toString() {
		return "RadioModel [name=" + name + ", duration=" + duration + ", size=" + size + ", path=" + path + ", artist="
				+ artist + "]";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	private String artist;//��Ƶ����
}
