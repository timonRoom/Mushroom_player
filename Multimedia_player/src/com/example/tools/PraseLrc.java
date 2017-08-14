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
	 * ��ȡ����ļ�
	 * */
	public void readLrcFile(File file){
		
		if (file==null||!file.exists()) {//�ж��ļ��Ƿ�Ϊ��
			//�ļ�������
			list = null;
			isexistlrc = false;
		}else {
			//�ļ�����
			isexistlrc= true;
			list = new ArrayList<LrcModel>();
			//1.�������
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
			//2.����ʱ������
			
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
			//3.����ÿ���� ��ʾʱ��
			
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
	 *����һ�� 
	 * */
	private String parseline(String line) {
		int pos1 = line.indexOf("[");//��һ�γ��֡� λ��  ���û�з���-1  ����з��س��ֵ�λ��0
		int pos2 = line.indexOf("]");////��һ�γ��֡�λ��  ���û�з���-1 ����з��س��ֵ�λ��9
		if (pos1==0&&pos2!=-1) {//���ڸ��
			long[] times = new long[getcounttag(line)]; //ȡ��ʱ�����뼯����
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
			//���������ı���ʱ���浽������
			LrcModel lrcModel = new LrcModel();
			for (int j = 0; j < times.length; j++) {
				if (times[j]!=0) {//��ʱ���
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
	 * ��Stringʱ��ת��Ϊlong����
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
	 * �ж�һ���д��ڼ���ʱ���
	 * */
	private int getcounttag(String line) {
		int result = -1;
		String[] left  = line.split("\\[");//���ա� �����з� �ж��ٸ���  ���ж��ٸ�ʱ���
		String[] right = line.split("\\]");
		
		if (left.length==0&&right.length==0) { //ֻ��һ��ʱ���
			return result = 1;
		}else if (left.length>right.length) {
			result = left.length;
		}else {
			result = right.length;
		}
		return result;
	}
	/**
	 * �ж��ļ������ʽ
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
                    //��������BF���µģ�Ҳ����GBK  
                    if (0x80 <= read && read <= 0xBF)  
                        break;  
                    if (0xC0 <= read && read <= 0xDF) {  
                        read = bis.read();  
                        if (0x80 <= read && read <= 0xBF)// ˫�ֽ� (0xC0 - 0xDF)  
                            // (0x80 -  
                            // 0xBF),Ҳ������GB������  
                            continue;  
                        else  
                            break;  
                     // Ҳ�п��ܳ������Ǽ��ʽ�С  
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

