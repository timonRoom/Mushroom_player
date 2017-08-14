package com.example.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.example.mode.Search_lrcModel;

import android.util.Log;

public class SearchLrcAndDownload {
	/**
	 * 搜索歌曲 得到返回的xml文件
	 * **/
	public String searchlrc (String musicname,String musicartist){
		StringBuffer sBuffer = new StringBuffer("");
		try {
			musicname = URLEncoder.encode(musicname, "UTF-8");
			musicartist = URLEncoder.encode(musicartist, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String path = "http://musicmini.baidu.com/app/search/searchList.php?qword={"+musicname+","+musicartist+"}&ie=utf-8&page={1}";
		try {
			URL url = new URL(path);
			try {
				Log.e("searchlrc", "搜索地址"+path);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.connect();
				InputStream input= connection.getInputStream();
				if (connection.getResponseCode() == 200) {
					BufferedReader bReader= new BufferedReader(new InputStreamReader(input));
					String line;
					int i = 0;
					boolean isbody = false;
					while ((line = bReader.readLine())!=null) {
						i++;
						if (line.indexOf("<body")!=-1) {
							isbody = true;
						}
						if (line.indexOf("<")!=-1&&line.equals("<script>")==false
								&&line.equals("</script>")==false&&line.indexOf("<html")==-1&&line.indexOf("</html")==-1&&isbody) {
							sBuffer.append(line);
							sBuffer.append("\n");
						}
						if (line.indexOf("</body>")!=-1) {
							continue;
						}
					}
					input.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sBuffer.toString();
	}
	/**
	 * 解析返回的xml文件 得到歌曲ID数组
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 * **/
	public List<String> parserequexml(String result) throws IOException, SAXException, ParserConfigurationException{
		List<String > list ;
		SAXParserFactory sFactory = SAXParserFactory.newInstance();

		SAXParser saxParser = null;
		saxParser = sFactory.newSAXParser();

		XMLReader xmlReader = null;
		xmlReader = saxParser.getXMLReader();
		parsexml parsexml = new parsexml();
		xmlReader.setContentHandler(parsexml);
		result= result.replaceAll("&", "&amp;");
		InputSource inputSource = new InputSource();
		inputSource.setEncoding("utf-8");
		inputSource.setCharacterStream(new StringReader(result));

		Log.e("saxparsexml", "开始解析");

		xmlReader.parse(inputSource);
		Log.e("parse", "解析ok");
//		Toast.makeText(getApplicationContext(), "解析ok", Toast.LENGTH_LONG).show();
		list= parsexml.getList();
		Log.e("TAG", "list"+list);
		return list;
	}
	
	public String  getlrcmesage(String  id) throws IOException {
		StringBuffer sBuffer= new StringBuffer("");
		Log.e("id", "搜索id"+id);
		String lrcpath = "http://music.baidu.com/data/music/links?songIds={"+id+"}";
		URL url = new URL(lrcpath);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream inputStream = connection.getInputStream();
		if (connection.getResponseCode()==200) {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));
			String line="";
			while ((line = bReader.readLine())!=null) {
				sBuffer.append(line);
				sBuffer.append("\n");
			}
		}
		inputStream.close();
		return sBuffer.toString();
	}
	/**
	 * 解析返回的json文件 得到歌词下载地址 
	 * **/
	public Search_lrcModel  parsejson(String lrcjson) {
		Search_lrcModel sModel = new Search_lrcModel();
		try {
			JSONObject jsonObject = new JSONObject(lrcjson);
			JSONObject data = jsonObject.getJSONObject("data");
			JSONArray songlist = data.getJSONArray("songList");
			JSONObject songmesage = songlist.getJSONObject(0);
			 sModel.setLrcLink(songmesage.getString("lrcLink"));
			 sModel.setArtistName(songmesage.getString("artistName"));
			 sModel.setSongName(songmesage.getString("songName"));
			 sModel.setSongLink(songmesage.getString("songLink"));
			Log.e("sModel", sModel.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sModel;
	}
	/**
	 * 下载歌词，以字符串返回
	 * **/
	public String downloadlrc(String lrcuri) throws IOException {
		StringBuffer sBuffer= new StringBuffer("");
		Log.e("歌词地址", lrcuri);
		URL url = new URL(lrcuri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream inputStream = connection.getInputStream();
		if (connection.getResponseCode()==200) {
			BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream));
			String line="";
			while ((line = bReader.readLine())!=null) {
				sBuffer.append(line);
				sBuffer.append("\n");
			}
		}
		inputStream.close();
		return sBuffer.toString();
		
	}
	public String downloadlrctosdcard(String lrcuri,String path) throws IOException {
		Log.e("歌词地址", lrcuri);
		URL url = new URL(lrcuri);
		File file = new File(path);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream inputStream = connection.getInputStream();
		if (connection.getResponseCode()==200) {
			OutputStream outputStream = new FileOutputStream(file);
			byte[] buffer=new byte[4*1024];  
			int len = -1;
            while((len = inputStream.read(buffer))!=-1){  
                outputStream.write(buffer, 0, len);  
            }  
            outputStream.flush();
            outputStream.close();
            inputStream.close();
		}
	
		return file.toString();
		
	}
	/**
	 * 搜索歌曲 得到返回的xml文件
	 * **/
	public String searchkey (String key){
		StringBuffer sBuffer = new StringBuffer("");
		try {
			key = URLEncoder.encode(key, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String path = "http://musicmini.baidu.com/app/search/searchList.php?qword={"+key+"}&ie=utf-8&page={1}";
		try {
			URL url = new URL(path);
			try {
				Log.e("searchlrc", "搜索地址"+path);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.connect();
				InputStream input= connection.getInputStream();
				if (connection.getResponseCode() == 200) {
					BufferedReader bReader= new BufferedReader(new InputStreamReader(input));
					String line;
					int i = 0;
					boolean isbody = false;
					while ((line = bReader.readLine())!=null) {
						i++;
						if (line.indexOf("<body")!=-1) {
							isbody = true;
						}
						if (line.indexOf("<")!=-1&&line.equals("<script>")==false
								&&line.equals("</script>")==false&&line.indexOf("<html")==-1&&line.indexOf("</html")==-1&&isbody) {
							sBuffer.append(line);
							sBuffer.append("\n");
						}
						if (line.indexOf("</body>")!=-1) {
							continue;
						}
					}
					input.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sBuffer.toString();
	}
}
