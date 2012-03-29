package com.mobilis.controller;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.mobilis.dialog.DialogMaker;
import com.mobilis.model.DBAdapter;
import com.mobilis.threads.RequestCoursesThread;
import com.mobilis.threads.RequestCurriculumUnitsThread;

public class CourseListController extends ListActivity {

	private Intent intent;
	private DBAdapter adapter;
	private ParseJSON jsonParser;
	private ContentValues[] courseList;
	private String courseId;
	private CourseListAdapter customAdapter;
	private ProgressDialog dialog;
	private RequestCurriculumUnits requestCurriculumUnits;
	private RequestCourses requestCourses;
	private SharedPreferences settings;
	// private Dialogs dialogs;
	private DialogMaker dialogMaker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dialogMaker = new DialogMaker(this);
		// dialogs = new Dialogs(this);
		setContentView(R.layout.course);
		adapter = new DBAdapter(this);
		updateList();
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
		jsonParser = new ParseJSON(this);
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
		courseId = (String) semester;

		settings = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("SelectedCourse", courseId);
		editor.commit();

		Log.w("GroupID", courseId);

		adapter.open();

		if (adapter.existsClassesOnCourse(Long.parseLong(settings.getString(
				"SelectedCourse", null)))) {
			adapter.close();
			intent = new Intent(this, ClassListController.class);
			startActivity(intent);
		}

		else {
			adapter.close();
			dialog = dialogMaker
					.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
			dialog.show();
			obtainCurriculumUnits(Constants.URL_CURRICULUM_UNITS_PREFIX
					+ courseId + Constants.URL_GROUPS_SUFFIX);
		}

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
			adapter.open();

			// adapter.updateGroups(result);
			adapter.updateClassesFromCourse(result,
					Long.parseLong(settings.getString("SelectedCourse", null)));

			adapter.close();
			intent = new Intent(getApplicationContext(),
					ClassListController.class);
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
			dialog = dialogMaker
					.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
			dialog.show();
			obtainCourses(Constants.URL_COURSES);
		}
		if (item.getItemId() == R.id.menu_logout) {
			adapter.open();
			adapter.updateToken(null);
			intent = new Intent(this, Login.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

		}
		if (item.getItemId() == R.id.menu_config) {
			intent = new Intent(this, Config.class);
			startActivity(intent);
		}
		return true;
	}
}