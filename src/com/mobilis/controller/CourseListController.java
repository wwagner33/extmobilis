package com.mobilis.controller;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilis.dao.ClassDAO;
import com.mobilis.dao.CourseDAO;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.interfaces.MobilisListActivity;
import com.mobilis.util.Constants;
import com.mobilis.util.ParseJSON;
import com.mobilis.ws.Connection;

public class CourseListController extends MobilisListActivity {

	private Intent intent;
	private ParseJSON jsonParser;
	private LayoutInflater inflater;
	private CourseListAdapter listAdapter;
	private CourseDAO courseDAO;
	private Cursor cursor;
	private ClassDAO classDAO;
	private Connection connection;
	private CourseHandler handler;
	private ProgressDialog progressDialog;
	private DialogMaker dialogMaker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.course);
		dialogMaker = new DialogMaker(this);
		progressDialog = dialogMaker
				.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
		handler = new CourseHandler();
		connection = new Connection(handler);
		courseDAO = new CourseDAO(this);
		classDAO = new ClassDAO(this);
		jsonParser = new ParseJSON(this);
		restoreActivityState();
		updateList();
	}

	@SuppressWarnings("deprecation")
	public void restoreActivityState() {
		if (getLastNonConfigurationInstance() != null) {
			progressDialog = ((ProgressDialog) getLastNonConfigurationInstance());
			progressDialog.show();
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		if (progressDialog.isShowing()) {
			closeDialog(progressDialog);
			return progressDialog;
		}
		return null;
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();

		Intent intent = new Intent(this, InitialConfig.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("FinishActivity", "YES");
		startActivity(intent);
	}

	public void updateList() {

		courseDAO.open();
		cursor = courseDAO.getAllCourses();
		courseDAO.close();
		listAdapter = new CourseListAdapter(this, cursor);
		setListAdapter(listAdapter);
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position,
			long id) {

		super.onListItemClick(listView, view, position, id);

		Object courseItem = listView.getAdapter().getItem(position);
		int courseId = (Integer) courseItem;

		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putInt("SelectedCourse", courseId);
		commit(editor);

		Log.w("COURSE_ID", "" + courseId);

		classDAO.open();

		if (classDAO.existClasses(getPreferences().getInt("SelectedCourse", 0))) {
			classDAO.close();
			intent = new Intent(this, ClassListController.class);
			closeDialog(progressDialog);
			startActivity(intent);
		}

		else {
			classDAO.close();
			progressDialog.show();
			obtainCurriculumUnits(Constants.URL_CURRICULUM_UNITS_PREFIX
					+ courseId + Constants.URL_GROUPS_SUFFIX);
		}
	}

	public void obtainCurriculumUnits(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_CLASSES, url,
				getPreferences().getString("token", null));

	}

	public void obtainCourses(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_COURSES,
				Constants.URL_COURSES, getPreferences()
						.getString("token", null));

	}

	@Override
	public void menuRefreshItemSelected() {
		progressDialog.show();
		obtainCourses(Constants.URL_COURSES);
	}

	private class CourseListAdapter extends CursorAdapter {

		@SuppressWarnings("deprecation")
		public CourseListAdapter(Context context, Cursor c) {
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

	private class CourseHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			if (msg.what == Constants.MESSAGE_COURSE_CONNECTION_OK) {

				Log.i("Teste", "Teste");
				ContentValues[] values = jsonParser.parseJSON(msg.getData()
						.getString("content"), Constants.PARSE_COURSES_ID);
				courseDAO.open();
				courseDAO.addCourses(values);
				courseDAO.close();
				updateList();
				closeDialog(progressDialog);

			}

			if (msg.what == Constants.MESSAGE_CLASS_CONNECTION_OK) {

				Log.i("RESULT", msg.getData().getString("content"));

				ContentValues[] values = jsonParser.parseJSON(msg.getData()
						.getString("content"), Constants.PARSE_CLASSES_ID);

				classDAO.open();

				classDAO.addClasses(values,
						getPreferences().getInt("SelectedCourse", 0));
				classDAO.close();

				intent = new Intent(getApplicationContext(),
						ClassListController.class);
				closeDialog(progressDialog);
				startActivity(intent);
			}

			if (msg.what == Constants.MESSAGE_CONNECTION_FAILED) {
				closeDialog(progressDialog);
			}
		}
	}
}