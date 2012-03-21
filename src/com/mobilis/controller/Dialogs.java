package com.mobilis.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class Dialogs implements OnClickListener {

	private Activity activity;
	DialogHandler handler;

	public Dialogs(Activity activity) {
		this.activity = activity;
	}

	public ProgressDialog getProgressDialog() {
		ProgressDialog dialog;
		dialog = new ProgressDialog(activity);
		dialog.setMessage(Constants.TEXT_PROGRESS_DIALOG);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		return dialog;

	}

	public AlertDialog getAlerDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setPositiveButton("OK", this);
		builder.setNeutralButton("Cancel", this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage("Tem certeza que deseja descartar essa mensagem?");
		builder.setTitle("Descartar?");
		AlertDialog dialog = builder.create();
		handler = new DialogHandler(activity);
		return dialog;
	}

	@Override
	public void onClick(DialogInterface dialog, int button) {
		if (button == AlertDialog.BUTTON_POSITIVE) {
			handler.sendEmptyMessage(1);
		}
		if (button == AlertDialog.BUTTON_NEGATIVE) {
			handler.sendEmptyMessage(2);
		}
	}
}
