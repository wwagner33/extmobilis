package com.mobilis.dialog;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;

import com.mobilis.util.Constants;

public class DialogMaker implements OnClickListener {

	private Context context;
	private Handler handler;

	public DialogMaker(Context context) {
		this.context = context;
	}

	public ProgressDialog makeProgressDialog(int id) {

		ProgressDialog dialog = new ProgressDialog(context);

		if (id == Constants.DIALOG_PROGRESS_STANDART) {
			dialog.setMessage(Constants.TEXT_PROGRESS_DIALOG);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setCancelable(false);
			return dialog;
		}

		return dialog;
	}

	public AlertDialog makeAlertDialog(int id, Handler handler) {

		this.handler = handler;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setPositiveButton("OK", this);
		builder.setNeutralButton("Cancel", this);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage("Tem certeza que deseja descartar essa mensagem?");
		builder.setTitle("Descartar?");
		AlertDialog dialog = builder.create();
		return dialog;

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		if (which == AlertDialog.BUTTON_POSITIVE) {
			handler.sendEmptyMessage(Constants.DIALOG_ALERT_POSITIVE_BUTTON_CLICKED);
		}

		if (which == AlertDialog.BUTTON_NEGATIVE) {
			handler.sendEmptyMessage(Constants.DIALOG_ALERT_NEGATIVE_BUTTON_CLICKED);
		}

	}

}
