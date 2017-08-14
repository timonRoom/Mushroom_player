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
     * 当SAX解析器解析到某个属性值时，会调用的方法 
     * 其中参数ch记录了这个属性值的内容 
     */ 
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		content = new String(ch, start, length);
//		Log.e("content", content);
	}
	/** 
     * 当SAX解析器解析到XML文档结束时，会调用的方法 
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
     * 当SAX解析器解析到某个元素开始时，会调用的方法 
     * 其中localName记录的是元素属性名 
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
     * 当SAX解析器解析到XML文档开始时，会调用的方法 
     */ 
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
//		Log.e("startDocument", "startDocument");
		
	}
	  /** 
     * 当SAX解析器解析到某个元素结束时，会调用的方法 
     * 其中localName记录的是元素属性名 
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
