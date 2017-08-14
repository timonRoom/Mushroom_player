package com.example.mode;

public class Search_lrcModel {
	private String songName;
	private String artistName;
	private String lrcLink;
	private String songLink;
	private String songid;
	private String hot;
	public String getHot() {
		return hot;
	}
	public void setHot(String hot) {
		this.hot = hot;
	}
	public String getSongid() {
		return songid;
	}
	public void setSongid(String songid) {
		this.songid = songid;
	}
	public String getSongName() {
		return songName;
	}
	public void setSongName(String songName) {
		this.songName = songName;
	}
	public String getArtistName() {
		return artistName;
	}
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}
	public String getLrcLink() {
		return lrcLink;
	}
	public void setLrcLink(String lrcLink) {
		this.lrcLink = lrcLink;
	}
	public String getSongLink() {
		return songLink;
	}
	public void setSongLink(String songLink) {
		this.songLink = songLink;
	}
	@Override
	public String toString() {
		return "Search_lrcModel [songName=" + songName + ", artistName=" + artistName + ", lrcLink=" + lrcLink
				+ ", songLink=" + songLink + ", songid=" + songid + ", hot=" + hot + "]";
	}
	
}
