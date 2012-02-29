package com.mobilis.threads;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.mobilis.controller.Constants;

import android.content.Context;

/*`-- Novos posts
 `- Representam os posts que ainda não foram carregados na aplicação móvel
 `- /discussions/:id/posts/:date/news
 `-- Últimos 20 posts, considerando o último post recebido na aplicacao
 `-- formato data: YYYYMMDDHH24MMSS*/

public abstract class RequestNewPostsThread extends ConnectionThread {

	private String URLString;
	private String token;

	public RequestNewPostsThread(Context context) {
		super(context);
	}

	@Override
	public void onConnectionFailed() {
		onNewPostsConnectionFalied();

	}

	@Override
	public void onConnectionSucceded(String result) {
		onNewPostConnectionSecceded(result);

	}

	public void setConnectionParameters(String URLString, String token) {
		this.URLString = URLString;
		this.token = token;

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

	@Override
	public int connectionType() {
		return Constants.TYPE_CONNECTION_GET;
	}

	public abstract void onNewPostsConnectionFalied();

	public abstract void onNewPostConnectionSecceded(String result);

}
