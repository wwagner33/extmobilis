package com.paulo.android.solamobile.threads;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.paulo.android.solarmobile.controller.Constants;

import android.content.Context;

public abstract class RequestCurriculumUnitsThread extends ConnectionThread {

	private String URLString, token;

	public RequestCurriculumUnitsThread(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onConnectionFailed() {
		onCurriculumUnitsConnectionFailed();

	}

	@Override
	public void onConnectionSucceded(String result) {
		onCurriculumUnitsConnectionSuccedded(result);

	}

	@Override
	public Object[] connectionMethod() {
		// TODO Auto-generated method stub
		try {
			return super.connection.getFromServer(URLString, token);
		} catch (ClientProtocolException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	public void setConnectionParameters(String URLString,String token) {
		this.URLString = URLString;
		this.token = token;
	}

	@Override
	public int connectionType() {
		// TODO Auto-generated method stub
		return Constants.TYPE_CONNECTION_GET;
	}

	public abstract void onCurriculumUnitsConnectionFailed();

	public abstract void onCurriculumUnitsConnectionSuccedded(String result);

}
