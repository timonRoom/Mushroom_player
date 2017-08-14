package com.example.mode;

public class MoveMode {
	private String movieName;
	private String coverImg;
	private String url;
	private String summary;
	private int videoLength;
	public String getMovieName() {
		return movieName;
	}
	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}
	public String getCoverImg() {
		return coverImg;
	}
	public void setCoverImg(String coverImg) {
		this.coverImg = coverImg;
	}
	@Override
	public String toString() {
		return "MoveMode [movieName=" + movieName + ", coverImg=" + coverImg + ", url=" + url + ", summary=" + summary
				+ ", videoLength=" + videoLength + "]";
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public int getVideoLength() {
		return videoLength;
	}
	public void setVideoLength(int videoLength) {
		this.videoLength = videoLength;
	}
	
}
