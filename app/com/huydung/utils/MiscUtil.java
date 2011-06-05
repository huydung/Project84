package com.huydung.utils;

import play.Play;

public class MiscUtil {
	public static void ConsoleLog(String mess){
		if( Play.mode.isDev() ){
			System.out.println(mess);
		}
	}
}
