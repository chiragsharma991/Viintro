package com.lkland.videocompressor.parser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lkland.util.Logger;

public class ProgressPaser {
	private int mTime=0;
	private int mTotalTime = 0;
	
	public ArrayList<Integer> parse(String str){
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(0);
		list.add(0);
		Pattern p = Pattern.compile(".*time=(.*?)\\s.*totalDuration=(.*?).\\s.*");
		Matcher mat = p.matcher(str);
		if(mat.matches()){
			String timestr = mat.group(1);
			String[] time = timestr.split(":");
			int h = Integer.parseInt(time[0]);
			int m = Integer.parseInt(time[1]);
			String[] second = time[2].split("\\.");
			int s = Integer.parseInt(second[0]);
			int cs = Integer.parseInt(second[1]);
			int curDuration = (((((h*60+m)*60)+s)*100)+cs)*10;
			mTime = curDuration*100;
			mTotalTime = Integer.valueOf(mat.group(2));
		}	
		return list;
	}
	
	public int getTime(){
		return mTime;
	}
	
	public int getTotalTime(){
		return mTotalTime;
	}
}
