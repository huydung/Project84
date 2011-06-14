package com.huydung.utils;

import play.Logger;
import play.Play;

public class MiscUtil {
	public static final String GOOGLE_MAP_API = "ABQIAAAAr66EAdiPtOceNAj29TnhkhTb-vLQlFZmc2N8bgWI8YDPp5FEVBSrh8_gCrVgtIJD8zqOA8Y0YKyM6g";
	public static void ConsoleLog(String mess){
		if( Play.mode.isDev() ){
			//System.out.println(mess);
			Logger.info(mess);
		}
	}
}
