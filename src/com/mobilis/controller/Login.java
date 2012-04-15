package com.mobilis.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobilis.dialog.DialogMaker;
import com.mobilis.model.CourseDAO;
import com.mobilis.threads.RequestCoursesThread;
import com.mobilis.threads.RequestTokenThread;

public class Login extends Activity implements OnClickListener {

	private EditText login, password;
	private Button submit;
	private Intent intent;
	private ProgressDialog dialog;
	private ParseJSON jsonParser;
	private RequestToken requestToken;
	private RequestCourses requestCourses;
	private DialogMaker dialogMaker;
	private CourseDAO courseDAO;
	private SharedPreferences settings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);
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
		// TODO Auto-generated method stub
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

		requestToken = new RequestToken(this);
		jsonParser = new ParseJSON(this);

		requestToken.setConnectionParameters(jsonParser.buildTokenObject(login
				.getText().toString(), password.getText().toString()),
				Constants.URL_TOKEN);
		requestToken.execute();

	}

	public void getCourseList(String token) {

		requestCourses = new RequestCourses(this);
		requestCourses.setConnectionParameters(Constants.URL_COURSES,
				settings.getString("token", null));
		requestCourses.execute();

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
			// adapter.open();
			// adapter.updateToken(tokenParsed[0].getAsString("token"));
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("token", tokenParsed[0].getAsString("token"));
			editor.commit();

			String token = settings.getString("token", null);
			// adapter.close();
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

			ContentValues[] values = jsonParser.parseJSON(result,
					Constants.PARSE_COURSES_ID);
			courseDAO.open();
			courseDAO.addCourses(values);
			courseDAO.close();

			intent = new Intent(getApplicationContext(),
					CourseListController.class);
			startActivity(intent);
		}
	}
}