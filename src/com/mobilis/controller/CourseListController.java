package com.mobilis.controller;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilis.dialog.DialogMaker;
import com.mobilis.model.ClassDAO;
import com.mobilis.model.CourseDAO;
import com.mobilis.threads.RequestCoursesThread;
import com.mobilis.threads.RequestCurriculumUnitsThread;

public class CourseListController extends ListActivity {

	private Intent intent;
	private ParseJSON jsonParser;
	private ProgressDialog dialog;
	private RequestCurriculumUnits requestCurriculumUnits;
	private RequestCourses requestCourses;
	private SharedPreferences settings;
	private DialogMaker dialogMaker;
	private LayoutInflater inflater;
	private CourseListAdapter2 listAdapter;
	private CourseDAO courseDAO;
	private Cursor cursor;
	private ClassDAO classDAO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.course);
		courseDAO = new CourseDAO(this);
		classDAO = new ClassDAO(this);
		dialogMaker = new DialogMaker(this);
		jsonParser = new ParseJSON(this);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
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
	protected void onStop() {
		super.onStop();
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}

	}

	public void updateList() {

		courseDAO.open();
		cursor = courseDAO.getAllCourses();
		courseDAO.close();
		listAdapter = new CourseListAdapter2(this, cursor);
		setListAdapter(listAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		Object courseItem = l.getAdapter().getItem(position);
		// courseId = String.valueOf(courseItem);
		int courseId = (Integer) courseItem;

		settings = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("SelectedCourse", courseId);
		editor.commit();

		Log.w("GroupID", String.valueOf(courseId));

		classDAO.open();

		if (classDAO.existClasses(settings.getInt("SelectedCourse", 0))) {
			classDAO.close();
			intent = new Intent(this, ClassListController.class);
			startActivity(intent);
		}

		else {
			classDAO.close();
			dialog = dialogMaker
					.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
			dialog.show();
			obtainCurriculumUnits(Constants.URL_CURRICULUM_UNITS_PREFIX
					+ courseId + Constants.URL_GROUPS_SUFFIX);
		}
	}

	public void obtainCurriculumUnits(String URLString) {

		requestCurriculumUnits = new RequestCurriculumUnits(this);
		requestCurriculumUnits.setConnectionParameters(URLString,
				settings.getString("token", null));
		requestCurriculumUnits.execute();

	}

	public void obtainCourses(String URLString) {
		requestCourses = new RequestCourses(this);
		requestCourses.setConnectionParameters(URLString,
				settings.getString("token", null));
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

			Log.i("RESULT", result);

			ContentValues[] values = jsonParser.parseJSON(result,
					Constants.PARSE_CLASSES_ID);

			classDAO.open();

			classDAO.addClasses(values, settings.getInt("SelectedCourse", 0));
			classDAO.close();

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

			ContentValues[] values = jsonParser.parseJSON(result,
					Constants.PARSE_COURSES_ID);

			courseDAO.open();
			courseDAO.addCourses(values);
			courseDAO.close();
			updateList();
			closeDialogIfItsVisible();
		}
	}

	public class CourseListAdapter2 extends CursorAdapter {

		public CourseListAdapter2(Context context, Cursor c) {
			super(context, c);
			inflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View convertView, Context context, Cursor cursor) {

			if (cursor != null) {

				TextView courseName = (TextView) convertView
						.findViewById(R.id.item);
				courseName.setText(cursor.getString(cursor
						.getColumnIndex("name")));
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {

			return inflater.inflate(R.layout.course_item, parent, false);
		}

		@Override
		public Object getItem(int position) {
			return getCursor().getInt(getCursor().getColumnIndex("_id"));
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

			SharedPreferences.Editor editor = settings.edit();
			editor.putString("token", null);
			editor.commit();
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