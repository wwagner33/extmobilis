package com.mobilis.util;

import android.content.Context;
import android.widget.Toast;

public class ErrorHandler {

	public static void handleStatusCode(Context context, int statusCode) {

		if (statusCode == 401) {
			Toast.makeText(context, "Falha de autenticação", Toast.LENGTH_SHORT)
					.show();
		}
		if (statusCode == 0) {
			Toast.makeText(context, "Erro de Conexão", Toast.LENGTH_SHORT)
					.show();
		}
		if (statusCode == 699) {
			Toast.makeText(context, "Tempo limite de conexão atingido",
					Toast.LENGTH_SHORT).show();
		}
	}
}