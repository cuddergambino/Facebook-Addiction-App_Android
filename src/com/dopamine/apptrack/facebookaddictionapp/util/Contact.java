package com.dopamine.apptrack.facebookaddictionapp.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.util.JsonReader;
import android.util.JsonWriter;

import com.dopamine.apptrack.facebookaddictionapp.BuildConfig;

public class Contact extends Data{
	static final String[] jsonFieldNames = {"lookupKey", "name", "number", "intervals"};
	
	public String lookupKey;
	public String name;
	public String number;
	private ArrayList<Long> intervals = new ArrayList<Long>();
	
	public Contact(String lookupKey, String name, String number){
		this.lookupKey = lookupKey;
		this.name = name;
		this.number = number;
	}
	
	// read from file
	Contact(JsonReader reader) throws IOException{
		reader.beginObject();
		
		while(reader.hasNext()){
			String fieldName = reader.nextName();
			if( fieldName.equals(jsonFieldNames[0]) )
				lookupKey = reader.nextString();
			else if( fieldName.equals(jsonFieldNames[1]) )
				name = reader.nextString();
			else if( fieldName.equals(jsonFieldNames[2]) )
				number = reader.nextString();
			else if( fieldName.equals(jsonFieldNames[3]) ){
				reader.beginArray();
				while(reader.hasNext())
					intervals.add(reader.nextLong());
				reader.endArray();
			}
			else
				reader.skipValue();
		}
		
		reader.endObject();
		
		Collections.sort(intervals);
		
	}
	
	public void addInterval(Long interval){
		// Keep a sorted list, from least to greatest
			
		int i = 0;
		while(i<intervals.size() && intervals.get(i) < interval)
			i++;
		
		intervals.add(i,interval);
	}
	
	public void clearIntervals(){
		intervals.clear();
	}
	
	public long getMedianInterval(){
		if(intervals.size() == 0)
			return -1;
		else{
			int index = intervals.size() / 2;
			return intervals.get(index);
		}
	}

	// write to file
	@Override
	public void writeTo(JsonWriter writer) throws IOException {
		writer.beginObject();

		writer.name(jsonFieldNames[0]).value(lookupKey);
		writer.name(jsonFieldNames[1]).value(name);
		writer.name(jsonFieldNames[2]).value(number);
		writer.name(jsonFieldNames[3]);
		writer.beginArray();
		for(Long l : intervals)
			writer.value(l);
		writer.endArray();
		
		writer.endObject();
	}
	
	public String getScrubbedPhoneNumber(){
		// xxxxxxxxxx
		
		String temp = number.trim().replaceAll("[- +]", "");
		
		switch(temp.length()){
		case 10:
			return temp;
		case 11:
			return temp.substring(1);
		case 4:
			// Service provider text message
			return temp;
		default:
			if(BuildConfig.DEBUG) System.out.println("UNUSUAL PHONE NUMBER --> " + name + ":" + temp);
			return temp;
		}
	}
	
	public static String scrubPhoneNumber(String str){
		String temp = str.trim().replaceAll("[- +]", "");
		
		switch(temp.length()){
		case 10:
			return temp;
		case 11:
			return temp.substring(1);
		case 4:
			// Service provider text message
			return temp;
		default:
			if(BuildConfig.DEBUG) System.out.println("UNUSUAL PHONE NUMBER --> " + temp);
			return temp;
		}
		
	}
	
}
