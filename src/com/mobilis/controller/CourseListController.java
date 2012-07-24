package com.mobilis.controller;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mobilis.dao.ClassDAO;
import com.mobilis.dao.CourseDAO;
import com.mobilis.dao.DatabaseHelper;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.interfaces.ConnectionCallback;
import com.mobilis.model.Class;
import com.mobilis.model.Course;
import com.mobilis.util.Constants;
import com.mobilis.util.ErrorHandler;
import com.mobilis.util.MobilisPreferences;
import com.mobilis.util.ParseJSON;
import com.mobilis.ws.Connection;

public class CourseListController extends SherlockFragmentActivity implements
		ConnectionCallback, OnItemClickListener {

	private Intent intent;
	private ParseJSON jsonParser;
	private CourseDAO courseDAO;
	private Cursor cursor;
	private ClassDAO classDAO;
	private Connection connection;
	private ProgressDialog progressDialog;
	private DialogMaker dialogMaker;
	private MobilisPreferences appState;
	private SimpleCursorAdapter simpleAdapter;
	private DatabaseHelper helper = null;
	private ListView list;
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.course);
		helper = getHelper();
		appState = MobilisPreferences.getInstance(this);
		dialogMaker = new DialogMaker(this);
		progressDialog = dialogMaker
				.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
		connection = new Connection(this);
		courseDAO = new CourseDAO(helper);
		classDAO = new ClassDAO(helper);
		jsonParser = new ParseJSON();
		TextView emptyText = new TextView(this);
		emptyText.setText("Nenhum Curso Disponível");
		emptyText.setGravity(Gravity.CENTER);
		list = (ListView) findViewById(R.id.list);
		list.setOnItemClickListener(this);
		list.setEmptyView(emptyText);
		restoreActivityState();
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("Cursos");
		updateList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_refresh:
			progressDialog.show();
			obtainCourses(Constants.URL_COURSES);
			return true;

		case R.id.menu_config:
			intent = new Intent(this, Config.class);
			startActivity(intent);
			return true;

		case R.id.menu_logout:
			appState.setToken(null);
			intent = new Intent(this, Login.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;

		default:
			return false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (helper != null) {
			OpenHelperManager.releaseHelper();
			helper = null;
		}
	}

	private DatabaseHelper getHelper() {
		if (helper == null) {
			helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		return helper;
	}

	public void restoreActivityState() {
		if (getLastCustomNonConfigurationInstance() != null) {
			progressDialog = ((ProgressDialog) getLastCustomNonConfigurationInstance());
			progressDialog.show();
		}
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
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
		cursor = courseDAO.getCoursesAsCursor();
		simpleAdapter = new SimpleCursorAdapter(this, R.layout.course_item,
				cursor, new String[] { "name" }, new int[] { R.id.item });
		list.setAdapter(simpleAdapter);

	}

	public void obtainCurriculumUnits(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_CLASSES, url,
				appState.getToken());

	}

	public void obtainCourses(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_COURSES,
				Constants.URL_COURSES, appState.getToken());

	}

	@Override
	@SuppressWarnings("unchecked")
	public void resultFromConnection(int connectionId, String result,
			int statusCode) {
		if (statusCode != 201 && statusCode != 200) {

			progressDialog.dismiss();
			ErrorHandler.handleStatusCode(this, statusCode);

		} else {
			switch (connectionId) {

			case Constants.CONNECTION_GET_COURSES:

				ArrayList<Course> courseValues = (ArrayList<Course>) jsonParser
						.parseJSON(result, Constants.PARSE_COURSES_ID);
				courseDAO.clearCourses();
				courseDAO.addCourse(courseValues
						.toArray(new Course[courseValues.size()]));
				updateList();
				progressDialog.dismiss();
				break;

			case Constants.CONNECTION_GET_CLASSES:

				ArrayList<Class> classValues = (ArrayList<Class>) jsonParser
						.parseJSON(result, Constants.PARSE_CLASSES_ID);

				classDAO.addClass(
						classValues
								.toArray(new com.mobilis.model.Class[classValues
										.size()]), appState.selectedCourse);

				intent = new Intent(getApplicationContext(),
						ClassListController.class);
				progressDialog.dismiss();
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Cursor itemCursor;
		itemCursor = (Cursor) list.getAdapter().getItem(position);
		int courseId = itemCursor.getInt(itemCursor.getColumnIndex("_id"));
		appState.selectedCourse = courseId;

		if (classDAO.existClassesOnCourse(appState.selectedCourse)) {
			intent = new Intent(this, ClassListController.class);
			progressDialog.dismiss();
			startActivity(intent);
		}

		else {
			progressDialog.show();
			obtainCurriculumUnits(Constants.URL_CURRICULUM_UNITS_PREFIX
					+ courseId + Constants.URL_GROUPS_SUFFIX);
		}
	}
}