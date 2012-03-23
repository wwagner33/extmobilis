package com.mobilis.controller;

import android.os.Handler;
import android.os.Message;

public class ResponseControllerHandler extends Handler {

	private ResponseController activity;

	public ResponseControllerHandler(ResponseController activity) {
		this.activity = activity;
	}

	public static final int LISTEN_BUTTON_CLICKED = 800;
	public static final int DELETE_BUTTON_CLICKED = 801;

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);

		if (msg.what == LISTEN_BUTTON_CLICKED) {
			activity.playRecording();
		}

		if (msg.what == DELETE_BUTTON_CLICKED) {
			activity.deleteRecording();
		}

	}

}
