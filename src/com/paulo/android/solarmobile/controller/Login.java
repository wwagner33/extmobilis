package com.paulo.android.solarmobile.controller;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
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

//import com.paulo.android.solarmobile.ws.Connection.KeyFinder;

public class Login extends Activity implements OnClickListener {
	public EditText login, password;
	public Button submit;
	public Button skipValidation;
	public Intent intent;
	JSONObject json;
	DBAdapter adapter;
	Connection connection;
	ProgressDialog dialog;
	String authToken;
	ParseJSON jsonParser;

	RequestTokenThread requestTokenThread;
	ObtainCourseListThread obtainCourseListThread;
	ConnectionLimit threadLimit;

	private static final int CONNECTION_ERROR_CODE = 1;
	private static final int PARSE_COURSES_ID = 222;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		skipValidation = (Button) findViewById(R.id.login_skip);
		skipValidation.setOnClickListener(this);
		skipValidation.setVisibility(View.GONE);

		login = (EditText) findViewById(R.id.campo1);
		password = (EditText) findViewById(R.id.campo2);
		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(this);

		adapter = new DBAdapter(this);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.login_skip) {

			intent = new Intent(Login.this, CourseListController.class);
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

				// talvez realocar para dentro da thread
				json = new JSONObject();
				LinkedHashMap jsonMap = new LinkedHashMap<String, String>();
				jsonMap.put("username", login.getText().toString());
				jsonMap.put("password", password.getText().toString());
				json.put("user", jsonMap);

				Log.w("JsonObject", json.toString());

				 dialog = Dialogs.getProgressDialog(this);
				 dialog.show();

				connection = new Connection(this);

				requestToken();
			}
		}
	}

	public void requestToken() {
		threadLimit = new ConnectionLimit();
		requestTokenThread = new RequestTokenThread();
		requestTokenThread.execute();
		threadLimit.execute("token");
	}

	public void getCourseList(String token) {
		threadLimit = new ConnectionLimit();
		obtainCourseListThread = new ObtainCourseListThread();
		obtainCourseListThread.execute(token);
		threadLimit.execute("GET");
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
		Intent intent = new Intent(this, InitialConfig.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("FinishActivity", "YES");
		startActivity(intent);
	}

	public ProgressDialog createDialog() {
		ProgressDialog dialog;
		dialog = new ProgressDialog(this);
		dialog.setMessage("Carregando, Por favor aguarde...");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		return dialog;
	}

	public void handleError(int errorCode) {
		dialog.dismiss();
		if (errorCode == CONNECTION_ERROR_CODE) {
			Toast.makeText(this, "Erro de conexão,tente novamente ",
					Toast.LENGTH_SHORT).show();
			password.setText("");
		}
		if (errorCode == 2) {
			dialog.dismiss();
			Toast.makeText(this, "Tempo limite de resposta atingido",
					Toast.LENGTH_SHORT).show();
		}
	}

	public class RequestTokenThread extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			try {
				authToken = connection.postToServer(json.toJSONString(),
						Constants.URL_TOKEN);
				if (authToken == null) {
					return null;
				}

				// adapter.close();

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

				Log.w("RESULT", result);

				jsonParser = new ParseJSON();
				ContentValues[] tokenParsed = jsonParser.parseJSON(result,
						Constants.PARSE_TOKEN_ID);

				Log.w("TOKENPARSED", tokenParsed[0].getAsString("token"));

				adapter.open();
				Log.w("UpdateToken", "updateToken");
				adapter.updateToken(tokenParsed[0].getAsString("token"));
				adapter.close();
				getCourseList(tokenParsed[0].getAsString("token"));
			}
		}
	}

	public class ObtainCourseListThread extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				String result = connection.getFromServer(
						"curriculum_units.json", params[0]);
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

				Log.w("TokenResult", result);

				adapter.open();

				// adapter.insertCourses(result);

				adapter.updateCourses(result);

				// ContentValues[] parsedValues = connection.parse(result);
				intent = new Intent(getApplicationContext(),
						CourseListController.class);
				intent.putExtra("CourseList", result);

				dialog.dismiss();
				startActivityForResult(intent, 10);

			}
		}
	}

	public class ConnectionLimit extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... arg0) {
			if (arg0[0].equals("token")) {
				try {
					Thread.sleep(5000);
					if (requestTokenThread.getStatus() == AsyncTask.Status.RUNNING) {
						connection.StopPost();
						return 1;
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				try {
					Thread.sleep(5000);
					if (obtainCourseListThread.getStatus() == AsyncTask.Status.RUNNING) {
						connection.StopPost();
						return 1;
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result == 1) {
				handleError(2);
			}

		}

	}

}