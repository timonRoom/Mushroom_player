package com.example.tools;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class parsexml extends DefaultHandler{
	private List<String > list = new ArrayList<String>();;
	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	private String content;
	 /** 
     * ��SAX������������ĳ������ֵʱ������õķ��� 
     * ���в���ch��¼���������ֵ������ 
     */ 
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		content = new String(ch, start, length);
//		Log.e("content", content);
	}
	/** 
     * ��SAX������������XML�ĵ�����ʱ������õķ��� 
     */ 
	@Override
	public void endDocument() throws SAXException {
//		Log.e("endDocument", "endDocument");
		super.endDocument();
//		Log.e("endDocument", "endDocument");
//		for (int i = 0; i < list.size(); i++) {
//			Log.e("list", list.get(i));
//		}
	}
	 /** 
     * ��SAX������������ĳ��Ԫ�ؿ�ʼʱ������õķ��� 
     * ����localName��¼����Ԫ�������� 
     */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
//		Log.e("startElement", localName);
//		Log.e("localName", localName);
//		Log.e("attributes", attributes.toString());
		if (localName.equals("input")) {
			
			for (int i = 0; i < attributes.getLength(); i++) {
//				Log.e("attributes", attributes.getLocalName(i)+"="+attributes.getValue(i));
				if (attributes.getLocalName(i).equals("id")) {
					list.add(attributes.getValue(i));
//					Log.e("list+", attributes.getValue(i));
				}
			}
		}
		
	}
	 /** 
     * ��SAX������������XML�ĵ���ʼʱ������õķ��� 
     */ 
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
//		Log.e("startDocument", "startDocument");
		
	}
	  /** 
     * ��SAX������������ĳ��Ԫ�ؽ���ʱ������õķ��� 
     * ����localName��¼����Ԫ�������� 
     */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
//		Log.e("endElement", localName);
//		Log.e("localName", localName);
//		if (localName.equals("id")) {
//			list.add(content);
//		}
		
	}
	
}
