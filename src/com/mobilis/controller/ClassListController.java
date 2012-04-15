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
import android.widget.Toast;

import com.mobilis.dialog.DialogMaker;
import com.mobilis.model.ClassDAO;
import com.mobilis.model.TopicDAO;
import com.mobilis.threads.RequestCurriculumUnitsThread;
import com.mobilis.threads.RequestTopicsThread;

public class ClassListController extends ListActivity {

	private ParseJSON jsonParser;
	private ProgressDialog dialog;
	private Intent intent;
	private RequestTopics requestTopics;
	private RequestCurriculumUnits requestClasses;
	private SharedPreferences settings;
	private DialogMaker dialogMaker;
	private ClassDAO classDAO;
	private Cursor cursor;
	private ClassAdapter listAdapter;
	private TopicDAO topicDAO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.curriculum_units);
		jsonParser = new ParseJSON(this);
		classDAO = new ClassDAO(this);
		topicDAO = new TopicDAO(this);
		dialogMaker = new DialogMaker(this);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		updateList();
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

	public void closeDialogIfItsVisible() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();

	}

	public void obtainTopics(String URLString) {
		requestTopics = new RequestTopics(this);
		requestTopics.setConnectionParameters(URLString,
				settings.getString("token", null));
		requestTopics.execute();
	}

	public void obtainClasses(String URLString) {
		requestClasses = new RequestCurriculumUnits(this);
		requestClasses.setConnectionParameters(URLString,
				settings.getString("token", null));
		requestClasses.execute();
	}

	public void updateList() {

		classDAO.open();
		cursor = classDAO.getClasses(settings.getInt("SelectedCourse", 0));
		classDAO.close();
		listAdapter = new ClassAdapter(this, cursor);
		setListAdapter(listAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		int classId = (Integer) l.getAdapter().getItem(position);

		Log.i("SelectedClass", String.valueOf(classId));

		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("SelectedClass", classId);
		editor.commit();

		topicDAO.open();

		if (topicDAO.existsTopic(classId)) {
			topicDAO.close();
			intent = new Intent(this, TopicListController.class);
			startActivity(intent);

		}

		else {
			topicDAO.close();
			dialog = dialogMaker
					.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
			dialog.show();
			obtainTopics(Constants.URL_GROUPS_PREFIX + classId
					+ Constants.URL_DISCUSSION_SUFFIX);
		}
	}

	public class RequestTopics extends RequestTopicsThread {

		public RequestTopics(Context context) {
			super(context);

		}

		@Override
		public void onTopicsConnectionFailed() {
			closeDialogIfItsVisible();

		}

		@Override
		public void onTopicsConnectionSucceded(String result) {

			Log.w("result", result);

			if (result.length() <= 2) {

				Toast.makeText(getApplicationContext(), "Fórum Vazio",
						Toast.LENGTH_SHORT).show();
				closeDialogIfItsVisible();
			}

			else {

				ContentValues[] values = jsonParser.parseJSON(result,
						Constants.PARSE_TOPICS_ID);

				topicDAO.open();
				topicDAO.addTopics(values, settings.getInt("SelectedClass", 0));
				topicDAO.close();

				intent = new Intent(getApplicationContext(),
						TopicListController.class);
				startActivity(intent);

			}
		}
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

			ContentValues[] values = jsonParser.parseJSON(result,
					Constants.PARSE_CLASSES_ID);
			classDAO.open();
			classDAO.addClasses(values, settings.getInt("SelectedClass", 0));
			classDAO.close();
			listAdapter.notifyDataSetChanged();
			closeDialogIfItsVisible();

		}
	}

	private class ClassAdapter extends CursorAdapter {

		LayoutInflater inflater;

		public ClassAdapter(Context context, Cursor c) {
			super(context, c);
			inflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View convertView, Context context, Cursor cursor) {
			if (cursor != null) {

				TextView courseName = (TextView) convertView
						.findViewById(R.id.turmas_item);
				courseName.setText(cursor.getString(cursor
						.getColumnIndex("code")));
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return inflater.inflate(R.layout.curriculum_units_item, parent,
					false);
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

			int selectedCourse = settings.getInt("SelectedCourse", 0);
			dialog = dialogMaker
					.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
			dialog.show();
			obtainClasses(Constants.URL_CURRICULUM_UNITS_PREFIX
					+ selectedCourse + Constants.URL_GROUPS_SUFFIX);

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
