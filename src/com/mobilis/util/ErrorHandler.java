package com.mobilis.util;

import com.mobilis.controller.Constants;

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

		if (errorId == Constants.ERROR_SERVER_DOWN) {
			Toast.makeText(context, "Servidor Indisponível", Toast.LENGTH_SHORT)
					.show();
		}
		if (errorId == Constants.ERROR_TOKEN_EXPIRED) {

			Toast.makeText(context, "Senha ou Login inválidos",
					Toast.LENGTH_SHORT).show();
		}

		if (errorId == Constants.ERROR_UNKNOWN) {

			Toast.makeText(context, "Erro desconhecido", Toast.LENGTH_SHORT)
					.show();
		}

		if (errorId == Constants.ERROR_PAGE_NOT_FOUND) {
			Toast.makeText(context, "Página não encontrada", Toast.LENGTH_SHORT)
					.show();
		}

		if (errorId == Constants.ERROR_CONNECTION_REFUSED) {
			Toast.makeText(context, "Conexão recusada", Toast.LENGTH_SHORT)
					.show();
		}

	}
}