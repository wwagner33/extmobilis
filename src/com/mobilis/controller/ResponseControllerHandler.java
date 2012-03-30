package com.mobilis.controller;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class ResponseControllerHandler extends Handler {

	private ResponseController activity;

	public ResponseControllerHandler(ResponseController activity) {
		this.activity = activity;
	}

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);

		if (msg.what == Constants.DIALOG_ALERT_POSITIVE_BUTTON_CLICKED) {
			Toast.makeText(activity, "Mensagem descartada", Toast.LENGTH_SHORT)
					.show();
			activity.finish();
		}

		if (msg.what == Constants.DIALOG_ALERT_NEGATIVE_BUTTON_CLICKED) {
			Toast.makeText(activity, "onHandlerNegative", Toast.LENGTH_SHORT)
					.show();

		}

		if (msg.what == Constants.DIALOG_DELETE_AREA_CLICKED) {
			activity.deleteRecording();
		}
	}

}
