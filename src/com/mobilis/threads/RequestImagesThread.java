package com.mobilis.threads;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.mobilis.controller.Constants;

import android.content.Context;

public abstract class RequestImagesThread extends ConnectionThread {

	public String token;
	public String url;

	public RequestImagesThread(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onConnectionFailed() {
		onRequestImagesFailed();
	}

	@Override
	public void onConnectionSucceded(String result) {
		onRequestImagesSucceded(result);
	}

	public void setConnectionParameters(String url, String token) {
		this.token = token;
		this.url = url;
	}

	@Override
	public Object[] connectionMethod() {
		// TODO Auto-generated method stub
		try {
			return super.connection.getZippedImages(url, token);
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
		return Constants.TYPE_CONNECTION_GET;
	}

	public abstract void onRequestImagesSucceded(String result);

	public abstract void onRequestImagesFailed();

}
