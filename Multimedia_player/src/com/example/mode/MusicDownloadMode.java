package com.example.mode;

public class MusicDownloadMode {
	private String songName;
	private String artistName;
	private String albumName;
	private int time;
	private int size;
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	private String songLink;
	private String lrcLink;
	private String  songid;
	public String getSongid() {
		return songid;
	}
	public void setSongid(String songid) {
		this.songid = songid;
	}
	@Override
	public String toString() {
		return "MusicDownloadMode [songName=" + songName + ", artistName=" + artistName + ", albumName=" + albumName
				+ ", time=" + time + ", size=" + size + ", songLink=" + songLink + ", lrcLink=" + lrcLink + ", songid="
				+ songid + "]";
	}
	public String getSongLink() {
		return songLink;
	}
	public void setSongLink(String songLink) {
		this.songLink = songLink;
	}
	public String getLrcLink() {
		return lrcLink;
	}
	public void setLrcLink(String lrcLink) {
		this.lrcLink = lrcLink;
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
	public String getAlbumName() {
		return albumName;
	}
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
}
