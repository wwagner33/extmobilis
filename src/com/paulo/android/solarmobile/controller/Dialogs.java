package com.paulo.android.solarmobile.controller;

import android.app.ProgressDialog;
import android.content.Context;

public class Dialogs {

	public static ProgressDialog getProgressDialog(Context c) {
		ProgressDialog dialog;
		dialog = new ProgressDialog(c);
		dialog.setMessage(Constants.TEXT_PROGRESS_DIALOG);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		return dialog;

	}

}
