package com.mobilis.controller;

import org.json.simple.JSONObject;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobilis.dao.CourseDAO;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.interfaces.MobilisActivity;
import com.mobilis.util.Constants;
import com.mobilis.util.ErrorHandler;
import com.mobilis.util.ParseJSON;
import com.mobilis.ws.Connection;

public class Login extends MobilisActivity implements OnClickListener {

	private EditText login, password;
	private Button submit;
	private Intent intent;
	private ProgressDialog dialog;
	private ParseJSON jsonParser;
	private DialogMaker dialogMaker;
	private CourseDAO courseDAO;

	private Connection connection;
	private LoginHandler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			init(R.layout.login);
		}

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			init(R.layout.login_landscape);
		}
	}

	public void init(int layoutId) {

		setContentView(layoutId);
		handler = new LoginHandler();
		connection = new Connection(handler, this);
		jsonParser = new ParseJSON(this);
		courseDAO = new CourseDAO(this);
		dialogMaker = new DialogMaker(this);
		login = (EditText) findViewById(R.id.campo1);
		password = (EditText) findViewById(R.id.campo2);
		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(this);
		restoreDialog();

	}

	@SuppressWarnings("deprecation")
	public void restoreDialog() {
		if (getLastNonConfigurationInstance() != null) {

			dialog = (ProgressDialog) getLastNonConfigurationInstance();
			dialog.show();
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// TODO Auto-generated method stub
		if (dialog != null) {
			if (dialog.isShowing()) {
				closeDialog(dialog);
				return dialog;
			}
		}
		return null;
	}

	@Override
	public void onClick(View v) {

		if (v.equals(submit)) {

			if (!(login.getText().toString().trim().length() == 0 || password
					.getText().toString().length() == 0)) {
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

	public void requestToken() {

		JSONObject jsonObject = jsonParser.buildTokenObject(login.getText()
				.toString().trim(), password.getText().toString());

		connection.postToServer(Constants.CONNECTION_POST_TOKEN, jsonObject
				.toString().trim(), Constants.URL_TOKEN);

	}

	public void getCourseList(String token) {

		connection.getFromServer(Constants.CONNECTION_GET_COURSES,
				Constants.URL_COURSES, getPreferences()
						.getString("token", null));

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

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		closeDialog(dialog);
	}

	private class LoginHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			if (msg.what == Constants.MESSAGE_TOKEN_CONNECTION_OK) {

				Log.w("Token Connection", "OK");

				jsonParser = new ParseJSON(getApplicationContext());
				ContentValues[] tokenParsed = jsonParser.parseJSON(msg
						.getData().getString("content"),
						Constants.PARSE_TOKEN_ID);
				SharedPreferences.Editor editor = getPreferences().edit();
				editor.putString("token", tokenParsed[0].getAsString("token"));
				commit(editor);
				String token = getPreferences().getString("token", null);
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
				int statusCode = msg.getData().getInt("statusCode");
				ErrorHandler.handleStatusCode(getApplicationContext(),
						statusCode);
				closeDialog(dialog);
			}
		}
	}

}