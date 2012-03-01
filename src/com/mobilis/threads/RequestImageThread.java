package com.mobilis.threads;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.mobilis.controller.Constants;

import android.content.Context;

public abstract class RequestImageThread extends ConnectionThread {

	private String token;
	private String url;

	public RequestImageThread(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onConnectionFailed() {
		onRequestImageConnectionFailed();

	}

	@Override
	public void onConnectionSucceded(String result) {
		onRequestImageConnectionSucceded(result);
	}

	public void setConnectionParameters(String url, String token) {
		this.url = url;
		this.token = token;
	}

	@Override
	public Object[] connectionMethod() {
	
		try {
			return super.connection.getImageFromServer(url, token);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int connectionType() {
		// TODO Auto-generated method stub
		return Constants.TYPE_CONNECTION_GET;
	}

	public abstract void onRequestImageConnectionFailed();

	public abstract void onRequestImageConnectionSucceded(String result);

}
