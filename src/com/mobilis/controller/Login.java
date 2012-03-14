package com.mobilis.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.mobilis.model.DBAdapter;
import com.mobilis.threads.RequestCoursesThread;
import com.mobilis.threads.RequestTokenThread;


public class Login extends Activity implements OnClickListener {

	private EditText login, password;
	private Button submit;
	private Intent intent;
	private DBAdapter adapter;
	private ProgressDialog dialog;
	private ParseJSON jsonParser;
	private RequestToken requestToken;
	private RequestCourses requestCourses;

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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (dialog!=null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
		
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
		jsonParser = new ParseJSON(this);

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
		intent.putExtra(Constants.REQUEST_FINISH_ACTIVITY_ON_RETURN_TEXT,
				Constants.REQUEST_FINISH_ACTIVITY_ON_RETURN_VALUE);
		startActivity(intent);
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

			jsonParser = new ParseJSON(getApplicationContext());
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