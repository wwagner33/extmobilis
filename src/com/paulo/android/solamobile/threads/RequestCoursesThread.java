package com.paulo.android.solamobile.threads;

import java.io.IOException;
import org.apache.http.client.ClientProtocolException;
import android.content.Context;
import com.paulo.android.solarmobile.controller.Constants;

public abstract class RequestCoursesThread extends ConnectionThread {

	private String URL, token;

	public RequestCoursesThread(Context context) {
		super(context);

	}

	@Override
	public int connectionType() {
		return Constants.TYPE_CONNECTION_GET;
	}

	@Override
	public Object[] connectionMethod() {

		try {
			return super.connection.getFromServer(URL, token);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setConnectionParameters(String URL, String token) {
		this.token = token;
		this.URL = URL;
	}

	@Override
	public void onConnectionFailed() {
		onCoursesConnectionFailed();

	}

	@Override
	public void onConnectionSucceded(String result) {
		onCoursesConnectionSucceded(result);

	}

	public abstract void onCoursesConnectionFailed();

	public abstract void onCoursesConnectionSucceded(String result);

}
