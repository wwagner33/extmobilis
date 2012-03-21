package com.mobilis.controller;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class DialogHandler extends Handler {

	private Activity activity;

	public DialogHandler(Activity a) {
		activity = a;
	}

	private static final int POSITIVE_BUTTON = 1;
	private static final int NEGATIVE_BUTTON = 0;

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);

		if (msg.what == POSITIVE_BUTTON) {
			Toast.makeText(activity, "Mensagem descartada", Toast.LENGTH_SHORT)
					.show();
			activity.finish();
		}

		if (msg.what == NEGATIVE_BUTTON) {
			Toast.makeText(activity, "onHandlerNegative", Toast.LENGTH_SHORT)
					.show();
		}
	}
}
