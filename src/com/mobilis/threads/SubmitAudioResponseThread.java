package com.mobilis.threads;

import java.io.File;
import java.io.IOException;

import android.content.Context;

import com.mobilis.controller.Constants;

public abstract class SubmitAudioResponseThread extends ConnectionThread {

	File audioFile;
	String URLString;

	public SubmitAudioResponseThread(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onConnectionFailed() {
		onAudioResponseConnectionFailed();
	}

	@Override
	public void onConnectionSucceded(String result) {
		onAudioResponseConnectionSucceded(result);

	}

	public void setConnectionParameters(String URLString, File audioFile) {
		this.audioFile = audioFile;
		this.URLString = URLString;

	}

	@Override
	public Object[] connectionMethod() {
		// TODO Auto-generated method stub
		try {
			return super.connection.postAudioToServer(URLString, audioFile);
		} catch (IllegalStateException e) {
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
		return Constants.TYPE_CONNECTION_POST;
	}

	public abstract void onAudioResponseConnectionFailed();

	public abstract void onAudioResponseConnectionSucceded(String result);

}
