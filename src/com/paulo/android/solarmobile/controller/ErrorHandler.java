package com.paulo.android.solarmobile.controller;

import android.content.Context;
import android.widget.Toast;

public class ErrorHandler {

	public static void handleError(Context context, int errorId) {

		if (errorId == Constants.ERROR_CONNECTION_FAILED) {
			Toast.makeText(context, "Erro de conexão,tente novamente ",
					Toast.LENGTH_SHORT).show();
		}

		if (errorId == Constants.ERROR_CONNECTION_TIMEOUT) {
					//TBI
		}
	}

	/*
	 * public void handleError(int errorCode) { dialog.dismiss(); if (errorCode
	 * == Constants.ERROR_CONNECTION_FAILED) { Toast.makeText(this,
	 * "Erro de conexão,tente novamente ", Toast.LENGTH_SHORT).show();
	 * 
	 * } if (errorCode == 2) { dialog.dismiss(); Toast.makeText(this,
	 * "Tempo limite de resposta atingido", Toast.LENGTH_SHORT).show(); }
	 * password.setText(""); }
	 */

}