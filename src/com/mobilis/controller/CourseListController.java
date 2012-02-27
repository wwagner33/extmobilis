package com.mobilis.controller;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilis.model.DBAdapter;
import com.mobilis.threads.RequestCoursesThread;
import com.mobilis.threads.RequestCurriculumUnitsThread;

//import com.paulo.android.solarmobile.controller.R;

public class CourseListController extends ListActivity {

	private Intent intent;
	private DBAdapter adapter;
	private ParseJSON jsonParser;
	private ContentValues[] courseList;
	private String semesterString, authToken;
	private CourseListAdapter customAdapter;
	private ProgressDialog dialog;
	private RequestCurriculumUnits requestCurriculumUnits;
	private RequestCourses requestCourses;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.course);

		adapter = new DBAdapter(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

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
				Constants.PARSE_COURSES_ID);
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

		obtainCurriculumUnits(Constants.URL_CURRICULUM_UNITS_PREFIX
				+ semesterString + Constants.URL_GROUPS_SUFFIX);

	}

	public void obtainCurriculumUnits(String URLString) {

		requestCurriculumUnits = new RequestCurriculumUnits(this);
		adapter.open();
		requestCurriculumUnits.setConnectionParameters(URLString,
				adapter.getToken());
		adapter.close();
		requestCurriculumUnits.execute();

	}

	public void obtainCourses(String URLString) {
		requestCourses = new RequestCourses(this);
		adapter.open();
		requestCourses.setConnectionParameters(URLString, adapter.getToken());
		adapter.close();
		requestCourses.execute();
	}

	public class RequestCurriculumUnits extends RequestCurriculumUnitsThread {

		public RequestCurriculumUnits(Context context) {
			super(context);

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
			updateList();
			closeDialogIfItsVisible();
			
		}
	}

	public class CourseListAdapter extends BaseAdapter {

		Activity activity;
		ContentValues[] values;
		LayoutInflater inflater = null;

		public CourseListAdapter(Activity activity, ContentValues[] values) {
			this.activity = activity;
			this.values = values;
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
				convertView = inflater.inflate(R.layout.course_item, parent,
						false);
				TextView courseName = (TextView) convertView
						.findViewById(R.id.item);
				courseName.setText(values[position].getAsString("name"));
			}
			return convertView;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {
			dialog = Dialogs.getProgressDialog(this);
			dialog.show();
			obtainCourses(Constants.URL_COURSES);
		}
		return true;
	}
}