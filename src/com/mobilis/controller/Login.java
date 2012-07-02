package com.mobilis.controller;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mobilis.dao.CourseDAO;
import com.mobilis.dao.DatabaseHelper;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.interfaces.ConnectionCallback;
import com.mobilis.model.Course;
import com.mobilis.util.Constants;
import com.mobilis.util.ErrorHandler;
import com.mobilis.util.MobilisPreferences;
import com.mobilis.util.ParseJSON;
import com.mobilis.ws.Connection;

public class Login extends Activity implements OnClickListener,
		ConnectionCallback {

	private EditText login, password;
	private Button submit;
	private Intent intent;
	private ProgressDialog dialog;
	private ParseJSON jsonParser;
	private DialogMaker dialogMaker;
	private CourseDAO courseDAO;
	private MobilisPreferences prefs;
	private DatabaseHelper helper = null;

	private Connection connection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init(R.layout.login);
	}

	private DatabaseHelper getHelper() {
		if (helper == null) {
			helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		return helper;
	}

	public void init(int layoutId) {

		setContentView(layoutId);
		helper = getHelper();
		dialogMaker = new DialogMaker(this);
		dialog = dialogMaker
				.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
		prefs = MobilisPreferences.getInstance(this);
		connection = new Connection(this);

		jsonParser = new ParseJSON();

		courseDAO = new CourseDAO(helper);

		login = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(this);
		TextView footerText = (TextView) findViewById(R.id.footer_text);
		Typeface footerFont = Typeface.createFromAsset(getAssets(),
				"fonts/ubuntucondensed.ttf");
		footerText.setTypeface(footerFont);
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
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
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
				Constants.URL_COURSES, prefs.getToken());

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
		super.onStop();
		dialog.dismiss();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (helper != null) {
			OpenHelperManager.releaseHelper();
			helper = null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void resultFromConnection(int connectionId, String result,
			int statusCode) {

		if (statusCode != 200 && statusCode != 201) {

			dialog.dismiss();
			ErrorHandler.handleStatusCode(this, statusCode);
		} else {

			switch (connectionId) {

			case Constants.CONNECTION_POST_TOKEN:
				Log.w("Token Connection", "OK");

				ArrayList<String> tokenParsed = (ArrayList<String>) jsonParser
						.parseJSON(result, Constants.PARSE_TOKEN_ID);
				prefs.setToken(tokenParsed.get(0));
				getCourseList(prefs.getToken());
				break;

			case Constants.CONNECTION_GET_COURSES:

				ArrayList<Course> courses = (ArrayList<Course>) jsonParser
						.parseJSON(result, Constants.PARSE_COURSES_ID);

				courseDAO.clearCourses();
				courseDAO
						.addCourse(courses.toArray(new Course[courses.size()]));

				intent = new Intent(getApplicationContext(),
						CourseListController.class);

				startActivity(intent);
				break;

			default:
				break;
			}
		}
	}
}