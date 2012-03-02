package com.mobilis.threads;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.mobilis.controller.Constants;

import android.content.Context;

public abstract class RequestHistoryPostsThread extends ConnectionThread {

	private String url;
	private String token;

	public RequestHistoryPostsThread(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onConnectionFailed() {
		onRequestHistoryPostsConnectionFailed();
	}

	@Override
	public void onConnectionSucceded(String result) {
		onRequestHistoryPostsConnectionSucceded(result);

	}

	public void setConnectionParameters(String url, String token) {
		this.url = url;
		this.token = token;

	}

	@Override
	public Object[] connectionMethod() {
		// TODO Auto-generated method stub
		try {
			return super.connection.getFromServer(url, token);
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

	public abstract void onRequestHistoryPostsConnectionFailed();

	public abstract void onRequestHistoryPostsConnectionSucceded(String result);

}
