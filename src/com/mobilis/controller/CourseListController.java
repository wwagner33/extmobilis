package com.mobilis.controller;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.mobilis.dao.ClassDAO;
import com.mobilis.dao.CourseDAO;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.interfaces.MobilisListActivity;
import com.mobilis.util.Constants;
import com.mobilis.util.MobilisStatus;
import com.mobilis.util.ParseJSON;
import com.mobilis.ws.Connection;

public class CourseListController extends MobilisListActivity {

	private Intent intent;
	private ParseJSON jsonParser;
	private CourseDAO courseDAO;
	private Cursor cursor;
	private ClassDAO classDAO;
	private Connection connection;
	private CourseHandler handler;
	private ProgressDialog progressDialog;
	private DialogMaker dialogMaker;
	private MobilisStatus appState;
	private SimpleCursorAdapter simpleAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.course);
		appState = MobilisStatus.getInstance();
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

	@SuppressWarnings("deprecation")
	public void updateList() {

		courseDAO.open();
		cursor = courseDAO.getAllCourses();
		courseDAO.close();
		simpleAdapter = new SimpleCursorAdapter(this, R.layout.course_item,
				cursor, new String[] { "name" }, new int[] { R.id.item });
		setListAdapter(simpleAdapter);
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position,
			long id) {

		super.onListItemClick(listView, view, position, id);
		Cursor itemCursor;
		itemCursor = (Cursor) listView.getAdapter().getItem(position);
		int courseId = itemCursor.getInt(itemCursor.getColumnIndex("_id"));
		appState.selectedCourse = courseId;
		classDAO.open();

		if (classDAO.existClasses(appState.selectedCourse)) {
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

				classDAO.addClasses(values, appState.selectedCourse);
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