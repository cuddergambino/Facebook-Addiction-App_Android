package com.dopamine.apptrack.facebookaddictionapp.util;

import java.io.IOException;

import android.util.JsonWriter;

public abstract class Data {
	String[] jsonFieldNames;
	public abstract void writeTo(JsonWriter writer) throws IOException;
}
