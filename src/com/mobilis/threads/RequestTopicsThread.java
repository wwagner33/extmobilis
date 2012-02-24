package com.mobilis.threads;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.mobilis.controller.Constants;

import android.content.Context;

public abstract class RequestTopicsThread extends ConnectionThread {

	private String URLString, token;

	public RequestTopicsThread(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onConnectionFailed() {
		onTopicsConnectionFailed();

	}

	@Override
	public void onConnectionSucceded(String result) {
		onTopicsConnectionSucceded(result);
	}

	@Override
	public Object[] connectionMethod() {
		// TODO Auto-generated method stub
		try {
			return super.connection.getFromServer(URLString, token);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setConnectionParameters(String URLString, String token) {
		this.URLString = URLString;
		this.token = token;
	}

	@Override
	public int connectionType() {
		// TODO Auto-generated method stub
		return Constants.TYPE_CONNECTION_GET;
	}

	public abstract void onTopicsConnectionFailed();

	public abstract void onTopicsConnectionSucceded(String result);

}
