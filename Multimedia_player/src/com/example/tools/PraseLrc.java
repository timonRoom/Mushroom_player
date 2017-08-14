package com.example.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.mode.LrcModel;

public class PraseLrc {
	private boolean isexistlrc = false;
	
	public boolean getisexistlrc() {
		return isexistlrc;
	}
	public void setIsexistlrc(boolean isexistlrc) {
		this.isexistlrc = isexistlrc;
	}
	private List<LrcModel> list;
	public List<LrcModel> getList() {
		return list;
	}
	public void setList(List<LrcModel> list) {
		this.list = list;
	}
	/**
	 * 读取歌词文件
	 * */
	public void readLrcFile(File file){
		
		if (file==null||!file.exists()) {//判断文件是否为空
			//文件不存在
			list = null;
			isexistlrc = false;
		}else {
			//文件存在
			isexistlrc= true;
			list = new ArrayList<LrcModel>();
			//1.解析歌词
			try {
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), getCharset(file)));
				String line = "";
					while ((line = reader.readLine())!=null) {
						if ((line.indexOf("[ar:") != -1) || (line.indexOf("[ti:") != -1)  
								|| (line.indexOf("[by:") != -1)  
								|| (line.indexOf("[al:") != -1) || line.equals(" ")||(line.indexOf("[offset:") != -1)||line.indexOf("[")==-1) { 
							
							continue;  
						} 
						line = parseline(line);
					}
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			//2.按照时间排序
			
			Collections.sort(list, new Comparator<LrcModel>() {

				@Override
				public int compare(LrcModel o1, LrcModel o2) {
					if (o1.getPointtime()<o2.getPointtime()) {
						return -1;
					}else if (o1.getPointtime()>o2.getPointtime()) {
						return 1 ;
					}else {
						return 0 ;
					}
				}
			});
			//3.计算每句歌词 显示时间
			
			for (int i = 0; i < list.size(); i++) {
				LrcModel one = list.get(i);
				if (i+1<list.size()) {
					LrcModel two = list.get(i+1);
					one.setSleeptime(two.getPointtime()-one.getPointtime());
				}
			}
		}
	}
	/**
	 *解析一行 
	 * */
	private String parseline(String line) {
		int pos1 = line.indexOf("[");//第一次出现【 位置  如果没有返回-1  如果有返回出现的位置0
		int pos2 = line.indexOf("]");////第一次出现】位置  如果没有返回-1 如果有返回出现的位置9
		if (pos1==0&&pos2!=-1) {//存在歌词
			long[] times = new long[getcounttag(line)]; //取出时间点放入集合中
			String strtime = line.substring(pos1+1,pos2);//01:31.04
			times[0] = stringtimetolong(strtime);
			
			String content = line;
			int i = 1;
			while (pos1 == 0&&pos2!=-1) {
				content = content.substring(pos2+1);
				pos1 = content.indexOf("[");
				pos2 = content.indexOf("]");
				
				if (pos2!=-1) {
					
					strtime = content.substring(pos1+1, pos2);
					times[i] = stringtimetolong(strtime);
					
					if (times[i]!=-1) {
						return "";
					}
					i++;
				}
			}
			//将解析的文本和时间点存到数组中
			LrcModel lrcModel = new LrcModel();
			for (int j = 0; j < times.length; j++) {
				if (times[j]!=0) {//有时间点
					lrcModel.setContent(content);
					lrcModel.setPointtime(times[j]);
					list.add(lrcModel);
					lrcModel = new LrcModel();
				}
			}
			return content;
		}
		return "";
	}
	/**
	 * 将String时间转换为long类型
	 * [01:31.04] = 01*60*1000+31.04*1000
	 * */
	private long stringtimetolong(String strtime) {
		String[] s1 = strtime.split(":");
		String[] s2 = s1[1].split("\\.");
		
		long min = Long.parseLong(s1[0]);
		long second = Long.parseLong(s2[0]);
		long mm = Long.parseLong(s2[1]);
		return min*60*1000+second*1000+mm*10;
	}
	/**
	 * 判断一行中存在几个时间点
	 * */
	private int getcounttag(String line) {
		int result = -1;
		String[] left  = line.split("\\[");//按照【 进行切分 有多少个【  就有多少个时间点
		String[] right = line.split("\\]");
		
		if (left.length==0&&right.length==0) { //只有一个时间点
			return result = 1;
		}else if (left.length>right.length) {
			result = left.length;
		}else {
			result = right.length;
		}
		return result;
	}
	/**
	 * 判断文件编码格式
	 * */
    public static String getCharset(File file) {  
        String charset = "GBK";  
        byte[] first3Bytes = new byte[3];  
        try {  
            boolean checked = false;  
            BufferedInputStream bis = new BufferedInputStream(  
                  new FileInputStream(file));  
            bis.mark(0);  
            int read = bis.read(first3Bytes, 0, 3);  
            if (read == -1)  
                return charset;  
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {  
                charset = "UTF-16LE";  
                checked = true;  
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1]  
                == (byte) 0xFF) {  
                charset = "UTF-16BE";  
                checked = true;  
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1]  
                    == (byte) 0xBB  
                    && first3Bytes[2] == (byte) 0xBF) {  
                charset = "UTF-8";  
                checked = true;  
            }  
            bis.reset();  
            if (!checked) {  
                int loc = 0;  
                while ((read = bis.read()) != -1) {  
                    loc++;  
                    if (read >= 0xF0)  
                        break;  
                    //单独出现BF以下的，也算是GBK  
                    if (0x80 <= read && read <= 0xBF)  
                        break;  
                    if (0xC0 <= read && read <= 0xDF) {  
                        read = bis.read();  
                        if (0x80 <= read && read <= 0xBF)// 双字节 (0xC0 - 0xDF)  
                            // (0x80 -  
                            // 0xBF),也可能在GB编码内  
                            continue;  
                        else  
                            break;  
                     // 也有可能出错，但是几率较小  
                    } else if (0xE0 <= read && read <= 0xEF) {  
                        read = bis.read();  
                        if (0x80 <= read && read <= 0xBF) {  
                            read = bis.read();  
                            if (0x80 <= read && read <= 0xBF) {  
                                charset = "UTF-8";  
                                break;  
                            } else  
                                break;  
                        } else  
                            break;  
                    }  
                }  
                System.out.println(loc + " " + Integer.toHexString(read));  
            }  
            bis.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return charset;  
    }  
}

