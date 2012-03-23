package com.mobilis.controller;

import org.apache.http.client.ResponseHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.IntentSender.SendIntentException;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Dialogs implements OnClickListener,
		android.view.View.OnClickListener {

	private Activity activity;
	private DialogHandler handler;
	private LinearLayout listen, delete;
	private ResponseControllerHandler responseHandler;

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

	public Dialog getAudioDialog() {

		ResponseController dialogController = (ResponseController) activity;
		responseHandler = new ResponseControllerHandler(dialogController);
		Dialog dialog = new Dialog(dialogController);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_audio);

		listen = (LinearLayout) dialog.findViewById(R.id.listen_area);
		listen.setOnClickListener(this);

		delete = (LinearLayout) dialog.findViewById(R.id.delete_area);
		delete.setOnClickListener(this);

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

	@Override
	public void onClick(View view) {

		if (view.getId() == R.id.listen_area) {

			responseHandler
					.sendEmptyMessage(ResponseControllerHandler.LISTEN_BUTTON_CLICKED);
		}
		if (view.getId() == R.id.delete_area) {
			responseHandler
					.sendEmptyMessage(ResponseControllerHandler.DELETE_BUTTON_CLICKED);
		}
	}
}
