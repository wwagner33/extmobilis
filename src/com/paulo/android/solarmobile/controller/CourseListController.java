package com.paulo.android.solarmobile.controller;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.paulo.android.solamobile.threads.RequestCurriculumUnitsThread;
import com.paulo.android.solarmobile.model.DBAdapter;
import com.paulo.android.solarmobile.ws.Connection;

public class CourseListController extends ListActivity {

	public Intent intent;

	private static final int PARSE_COURSES_ID = 222;

	String[] courseListString;
	String authToken;
	Connection connection;
	DBAdapter adapter;
	ParseJSON jsonParser;
	ContentValues[] courseList;
	String jsonString;
	CourseListAdapter customAdapter;
	String result;

	ProgressDialog dialog;

	String semesterString;
	String groupsResult;

	int itemPosition;

	// Threads
	RequestCurriculumUnits requestCurriculumUnits;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cursos);

		connection = new Connection(this);
		adapter = new DBAdapter(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			jsonString = extras.getString("CourseList");
			updateList();

		} else {

			updateList();
		}
	}

	public void closeDialogIfItsVisible() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();

	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();

		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}

		Intent intent = new Intent(this, InitialConfig.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("FinishActivity", "YES");
		startActivity(intent);
	}

	@Override
	protected void onResume() {

		super.onResume();
		if (adapter == null) {
			adapter.open();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (adapter != null) {
			adapter.close();
		}
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}

	}

	public void updateList() {

		adapter.open();
		jsonParser = new ParseJSON();
		courseList = jsonParser.parseJSON(adapter.getCourseList(),
				PARSE_COURSES_ID);
		adapter.close();
		customAdapter = new CourseListAdapter(this, courseList);
		setListAdapter(customAdapter);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		Log.w("Item position", String.valueOf(position));
		Log.w("item id", String.valueOf(id));

		Object semester = l.getAdapter().getItem(position);
		semesterString = (String) semester;
		Log.w("GroupID", semesterString);

		adapter.open();
		authToken = adapter.getToken();
		adapter.close();
		Log.w("TOKEN", authToken);

		dialog = Dialogs.getProgressDialog(this);

		dialog.show();

		obtainCurriculumUnits("curriculum_units/" + semesterString
				+ "/groups.json");

	}

	public void getCourseList() {
		// implementado quando houver refresh
	}

	public void obtainCurriculumUnits(String URLString) {

		requestCurriculumUnits = new RequestCurriculumUnits(this);
		adapter.open();
		requestCurriculumUnits.setConnectionParameters(URLString,
				adapter.getToken());
		adapter.close();
		requestCurriculumUnits.execute();

		// curriculumThread = new ObtainCurriculumUnitsThread();
		// curriculumThread.execute(token);
	}

	public class RequestCurriculumUnits extends RequestCurriculumUnitsThread {

		public RequestCurriculumUnits(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCurriculumUnitsConnectionFailed() {
			closeDialogIfItsVisible();

		}

		@Override
		public void onCurriculumUnitsConnectionSuccedded(String result) {
			intent = new Intent(getApplicationContext(),
					ClassListController.class);
			intent.putExtra("GroupList", result);
			startActivity(intent);

		}
	}

	public class CourseListAdapter extends BaseAdapter {

		Activity activity;
		ContentValues[] values;
		LayoutInflater inflater = null;

		public CourseListAdapter(Activity a, ContentValues[] v) {
			activity = a;
			values = v;
			inflater = LayoutInflater.from(activity);
		}

		@Override
		public int getCount() {
			return values.length;
		}

		@Override
		public Object getItem(int position) {
			return values[position].getAsString("group_id");

		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.itemcurso, parent,
						false);
				TextView courseName = (TextView) convertView
						.findViewById(R.id.item);
				courseName.setText(values[position].getAsString("name"));
			}
			return convertView;
		}

	}
}