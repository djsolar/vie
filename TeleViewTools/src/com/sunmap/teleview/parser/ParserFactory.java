package com.sunmap.teleview.parser;


import java.util.HashMap;
import java.util.Map;

import com.sunmap.teleview.element.mm.DrawEleType;
import com.sunmap.teleview.parser.tele.MapMatchTeleParser;
import com.sunmap.teleview.parser.tele.ViewTeleParser;

public class ParserFactory {
	private static ParserFactory uniqueInstance = null;
	public static ParserFactory getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new ParserFactory();
		}
		return uniqueInstance;
	}
	private   Map<ParserType,Parser> mapping=new HashMap<ParserType,Parser>();
	private 	ParserFactory(){
		mapping.put(ParserType.VIEW, new ViewTeleParser());
		if (DrawEleType.drawMM == true) {
			mapping.put(ParserType.MAPMATCH, new MapMatchTeleParser());
		}
	}
	public  void againParser(){
		mapping.clear();
		mapping.put(ParserType.VIEW, new ViewTeleParser());
		if (DrawEleType.drawMM == true) {
			mapping.put(ParserType.MAPMATCH, new MapMatchTeleParser());
		}

	}
	public  Parser getParser(ParserType parserType){
		return mapping.get(parserType);
	}
}
