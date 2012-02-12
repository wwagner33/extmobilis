package com.paulo.android.solarmobile.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
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

import com.paulo.android.solamobile.threads.RequestCoursesThread;
import com.paulo.android.solamobile.threads.RequestTokenThread;
import com.paulo.android.solarmobile.model.DBAdapter;
import com.paulo.android.solarmobile.ws.Connection;

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

	RequestToken requestToken;
	RequestCourses requestCourses;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		login = (EditText) findViewById(R.id.campo1);
		password = (EditText) findViewById(R.id.campo2);
		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(this);

		adapter = new DBAdapter(this);
	}

	@Override
	public void onClick(View v) {

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

				dialog = Dialogs.getProgressDialog(this);
				dialog.show();

				connection = new Connection(this);

				requestToken();
			}
		}
	}

	public void closeDialogIfItsVisible() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();

	}

	public void requestToken() {

		requestToken = new RequestToken(this);
		jsonParser = new ParseJSON();

		requestToken.setConnectionParameters(jsonParser.buildTokenObject(login
				.getText().toString(), password.getText().toString()),
				Constants.URL_TOKEN);
		requestToken.execute();

	}

	public void getCourseList(String token) {

		requestCourses = new RequestCourses(this);
		adapter.open();
		requestCourses.setConnectionParameters(Constants.URL_COURSES,
				adapter.getToken());
		adapter.close();
		requestCourses.execute();

	}

	@Override
	protected void onStop() {

		super.onStop();
		if (adapter != null) {
			adapter.close();
		}

	}

	@Override
	public void onBackPressed() {

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

	public class RequestToken extends RequestTokenThread {

		public RequestToken(Context context) {
			super(context);

		}

		@Override
		public void onTokenConnectionFailed() {
			closeDialogIfItsVisible();

		}

		@Override
		public void onTokenConnectionSucceded(String result) {

			jsonParser = new ParseJSON();
			ContentValues[] tokenParsed = jsonParser.parseJSON(result,
					Constants.PARSE_TOKEN_ID);

			Log.w("TOKENPARSED", tokenParsed[0].getAsString("token"));

			adapter.open();
			Log.w("UpdateToken", "updateToken");
			adapter.updateToken(tokenParsed[0].getAsString("token"));

			String token = adapter.getToken();
			adapter.close();
			getCourseList(token);

		}
	}

	public class RequestCourses extends RequestCoursesThread {

		public RequestCourses(Context context) {
			super(context);

		}

		@Override
		public void onCoursesConnectionFailed() {
			closeDialogIfItsVisible();

		}

		@Override
		public void onCoursesConnectionSucceded(String result) {
			adapter.open();
			adapter.updateCourses(result);
			adapter.close();
			intent = new Intent(getApplicationContext(),
					CourseListController.class);
			startActivity(intent);
		}
	}

}