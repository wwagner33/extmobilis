package com.mobilis.threads;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;

import android.content.Context;

import com.mobilis.controller.Constants;

public abstract class SubmitTextResponseThread extends ConnectionThread {

	private String URLString, JSONString;

	public SubmitTextResponseThread(Context context) {
		super(context);
	}

	@Override
	public void onConnectionFailed() {
		onTextResponseConnectionFailed();
	}

	@Override
	public void onConnectionSucceded(String result) {
		onTextResponseConnectionSucceded(result);
	}

	@Override
	public Object[] connectionMethod() {
		try {
			return super.connection.postToServer(JSONString, URLString);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setConnectionParameters(String URLString, String JSONString) {
		this.URLString = URLString;
		this.JSONString = JSONString;
	}

	@Override
	public int connectionType() {
		return Constants.TYPE_CONNECTION_POST;
	}

	public abstract void onTextResponseConnectionFailed();

	public abstract void onTextResponseConnectionSucceded(String result);

}
