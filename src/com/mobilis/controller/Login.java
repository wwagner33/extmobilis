package com.mobilis.controller;

import org.json.simple.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobilis.dao.CourseDAO;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.util.ParseJSON;
import com.mobilis.ws.Connection;

public class Login extends Activity implements OnClickListener {

	private EditText login, password;
	private Button submit;
	private Intent intent;
	private ProgressDialog dialog;
	private ParseJSON jsonParser;
	private DialogMaker dialogMaker;
	private CourseDAO courseDAO;
	private SharedPreferences settings;

	private Connection connection;
	private LoginHandler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);

		handler = new LoginHandler();
		connection = new Connection(handler, this);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		jsonParser = new ParseJSON(this);
		courseDAO = new CourseDAO(this);
		dialogMaker = new DialogMaker(this);
		login = (EditText) findViewById(R.id.campo1);
		password = (EditText) findViewById(R.id.campo2);
		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		closeDialogIfItsVisible();
	}

	@Override
	public void onClick(View v) {

		if (v.equals(submit)) {

			if (!(login.getText().length() == 0 || password.getText().length() == 0)) {
				dialog = dialogMaker
						.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
				dialog.show();
				requestToken();
			}

			else {
				Toast.makeText(this, "Campos não podem ser vazios",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void closeDialogIfItsVisible() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();

	}

	public void requestToken() {

		JSONObject jsonObject = jsonParser.buildTokenObject(login.getText()
				.toString(), password.getText().toString());

		connection.postToServer(Constants.CONNECTION_POST_TOKEN,
				jsonObject.toString(), Constants.URL_TOKEN);

	}

	public void getCourseList(String token) {

		connection.getFromServer(Constants.CONNECTION_GET_COURSES,
				Constants.URL_COURSES, settings.getString("token", null));

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

	private class LoginHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			if (msg.what == Constants.MESSAGE_TOKEN_CONNECTION_OK) {

				jsonParser = new ParseJSON(getApplicationContext());
				ContentValues[] tokenParsed = jsonParser.parseJSON(msg
						.getData().getString("content"),
						Constants.PARSE_TOKEN_ID);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("token", tokenParsed[0].getAsString("token"));
				editor.commit();

				String token = settings.getString("token", null);
				getCourseList(token);

			}

			if (msg.what == Constants.MESSAGE_COURSE_CONNECTION_OK) {

				ContentValues[] values = jsonParser.parseJSON(msg.getData()
						.getString("content"), Constants.PARSE_COURSES_ID);
				courseDAO.open();
				courseDAO.addCourses(values);
				courseDAO.close();

				intent = new Intent(getApplicationContext(),
						CourseListController.class);

				startActivity(intent);

			}

			if (msg.what == Constants.MESSAGE_CONNECTION_FAILED) {
				closeDialogIfItsVisible();
			}
		}
	}

}