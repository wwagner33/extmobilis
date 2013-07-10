package com.mobilis.util;

import android.content.Context;
import android.widget.Toast;

public class ErrorHandler {

	public static void handleStatusCode(Context context, int statusCode) {
		switch (statusCode) {
		case 401:
			Toast.makeText(context, "Falha de autenticação", Toast.LENGTH_SHORT)
					.show();
			break;
		case 0:
			Toast.makeText(context, "Erro de Conexão", Toast.LENGTH_SHORT)
					.show();
			break;
		case 699:
			Toast.makeText(context, "Tempo limite de conexão atingido",
					Toast.LENGTH_SHORT).show();
			break;
		case 400:
			Toast.makeText(context, "Erro desconhecido", Toast.LENGTH_SHORT)
					.show();
			break;
		case 500:
			Toast.makeText(context, "Servidor indisponível", Toast.LENGTH_SHORT)
					.show();
			break;
		case 404:
			Toast.makeText(context, "Endereço não encontrado",
					Toast.LENGTH_SHORT).show();
			break;
		default:
			Toast.makeText(context, "Erro desconhecido", Toast.LENGTH_SHORT)
					.show();
		}
	}
}