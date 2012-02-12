package com.paulo.android.solamobile.threads;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.paulo.android.solarmobile.controller.Constants;
import com.paulo.android.solarmobile.controller.ErrorHandler;

import android.content.Context;

public abstract class RequestTokenThread extends ConnectionThread {

	private JSONObject jsonObject;
	private String URL;

	public RequestTokenThread(Context context) {
		super(context);

	}

	public void setConnectionParameters(JSONObject jsonObject, String URL) {
		this.jsonObject = jsonObject;
		this.URL = URL;
	}

	@Override
	public void ConnectionFailed() {
		onTokenConnectionFailed();
	}

	@Override
	public void ConnectionSucceded() {
		onTokenConnectionSucceded();
	}

	@Override
	public Object[] connectionMethod() {
		try {
			return super.connection
					.postToServer(jsonObject.toJSONString(), URL);
		} catch (ClientProtocolException e) {
			return null;
		} catch (IOException e) {
			return null;
		} catch (ParseException e) {
			return null;
		}
	}

	@Override
	public int connectionType() {
		return Constants.TYPE_CONNECTION_POST;
	
	}

	public abstract void onTokenConnectionFailed();

	public abstract void onTokenConnectionSucceded();

}