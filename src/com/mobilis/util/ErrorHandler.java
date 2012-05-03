package com.mobilis.util;

import android.content.Context;
import android.widget.Toast;

public class ErrorHandler {

	public static void handleError(Context context, int errorId) {

		if (errorId == Constants.ERROR_CONNECTION_FAILED) {
			Toast.makeText(context, "Erro de conexão,tente novamente ",
					Toast.LENGTH_SHORT).show();
		}

		if (errorId == Constants.ERROR_CONNECTION_TIMEOUT) {
			Toast.makeText(context, "Tempo limite de conexão atingido",
					Toast.LENGTH_SHORT).show();
		}

	}

	public static void handleStatusCode(Context context, int statusCode) {
		if (statusCode == 401) {
			Toast.makeText(context, "Falha de autenticação", Toast.LENGTH_SHORT)
					.show();
		}
	}
}