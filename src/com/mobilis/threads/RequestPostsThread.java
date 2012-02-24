package com.mobilis.threads;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.mobilis.controller.Constants;

import android.content.Context;

public abstract class RequestPostsThread extends ConnectionThread {

	String URLString, token;

	public RequestPostsThread(Context context) {
		super(context);
	}

	@Override
	public void onConnectionFailed() {
		onPostsConnectionFailed();
	}

	@Override
	public void onConnectionSucceded(String result) {
		onPostsConnectionSucceded(result);

	}

	@Override
	public Object[] connectionMethod() {

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

		return Constants.TYPE_CONNECTION_GET;
	}

	public abstract void onPostsConnectionFailed();

	public abstract void onPostsConnectionSucceded(String result);

}
