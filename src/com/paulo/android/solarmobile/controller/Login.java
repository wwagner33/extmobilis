package com.paulo.android.solarmobile.controller;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.paulo.android.solarmobile.model.DBAdapter;
import com.paulo.android.solarmobile.ws.Connection;

public class Login extends Activity implements OnClickListener {
	public EditText login, password;
	public Button submit;
	public Button skipValidation;
	public Intent intent;
	JSONObject json;
	DBAdapter adapter;
	AndroidConnection connection;
	ProgressDialog dialog;
	String authToken;

	RequestTokenThread requestTokenThread;
	ObtainCourseListThread obtainCourseListThread;

	private static final int CONNECTION_ERROR_CODE = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		skipValidation = (Button) findViewById(R.id.login_skip);
		skipValidation.setOnClickListener(this);

		login = (EditText) findViewById(R.id.campo1);
		password = (EditText) findViewById(R.id.campo2);
		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(this);
		adapter = new DBAdapter(this);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.login_skip) {

			intent = new Intent(Login.this, ListaCursos.class);
			startActivity(intent);
		}

		if (v.equals(submit)) {

			/*
			 * Validação do Login
			 * 
			 * if (LoginField.getText().toString().equals("") ||
			 * PassField.getText().toString().equals("")) {
			 * 
			 * Toast.makeText(this,
			 * " campos login ou senha não podem ser vazios",
			 * Toast.LENGTH_SHORT).show(); }
			 * 
			 * else {
			 * 
			 * 
			 * /* adapter.open(); ContentValues valores = new ContentValues();
			 * valores.put("nome", PassField.getText().toString());
			 * adapter.updateTable("config", 1, valores);
			 */

			boolean ok = true;
			if (ok) {
				json = new JSONObject();
				LinkedHashMap jsonMap = new LinkedHashMap<String, String>();
				jsonMap.put("username", login.getText().toString());
				jsonMap.put("password", password.getText().toString());
				json.put("user", jsonMap);

				Log.w("JsonObject", json.toString());

				// adapter.open();

				dialog = createDialog();
				dialog.show();
				connection = new AndroidConnection(this);

				// authToken = connection.requestAuthenticityToken(json);
				requestToken();

			}

		}
		// }
	}

	public void requestToken() {

		requestTokenThread = new RequestTokenThread();
		requestTokenThread.execute();
	}

	public void getCourseList(String token) {
		obtainCourseListThread = new ObtainCourseListThread();
		obtainCourseListThread.execute(token);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (adapter != null) {
			adapter.close();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		moveTaskToBack(true);
	}

	public ProgressDialog createDialog() {
		ProgressDialog dialog;
		dialog = new ProgressDialog(this);
		dialog.setMessage("Carregando, Por favor aguarde...");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		return dialog;
	}

	public class AndroidConnection extends Connection {

		KeyFinder kf;

		public AndroidConnection(Context context) {
			super(context);
		}

		@Override
		public String[] parse(String source) {
			kf = new KeyFinder();
			JSONArray jsonArray = new JSONArray();

			return null;
		}

	}

	public void handleError(int errorCode) {
		dialog.dismiss();
		if (errorCode == CONNECTION_ERROR_CODE) {
			Toast.makeText(this, "Erro de conexão,tente novamente ",
					Toast.LENGTH_SHORT).show();
			password.setText("");
		}
	}

	public class RequestTokenThread extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			try {
				authToken = connection.requestAuthenticityToken(json);
				return authToken;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == (null)) {
				handleError(CONNECTION_ERROR_CODE);
			} else {
				getCourseList(result);
			}
		}
	}

	public class ObtainCourseListThread extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				String result = connection.requestJSON("curriculum_units.json",
						params[0]);
				return result;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return null;

			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == null) {
				handleError(CONNECTION_ERROR_CODE);

			} else {
				intent = new Intent(getApplicationContext(), ListaCursos.class);
				intent.putExtra("CourseList", result);
				startActivity(intent);
			}
		}
	}
}