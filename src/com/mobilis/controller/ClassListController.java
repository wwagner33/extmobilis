package com.mobilis.controller;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilis.dialog.DialogMaker;
import com.mobilis.model.DBAdapter;
import com.mobilis.threads.RequestCurriculumUnitsThread;
import com.mobilis.threads.RequestTopicsThread;

public class ClassListController extends ListActivity {

	private DBAdapter adapter;
	private ParseJSON jsonParser;
	private ContentValues[] parsedValues;
	private ClassAdapter listAdapter;
	private String classIdString;
	private ProgressDialog dialog;
	private Intent intent;
	private RequestTopics requestTopics;
	private RequestCurriculumUnits requestClasses;
	private SharedPreferences settings;
	private DialogMaker dialogMaker;

	// private Dialogs dialogs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.curriculum_units);
		adapter = new DBAdapter(this);
		dialogMaker = new DialogMaker(this);
		settings = PreferenceManager.getDefaultSharedPreferences(this);

		updateList();
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

	public void closeDialogIfItsVisible() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();

	}

	public void obtainTopics(String URLString) {
		requestTopics = new RequestTopics(this);
		adapter.open();
		requestTopics.setConnectionParameters(URLString, adapter.getToken());
		adapter.close();
		requestTopics.execute();
	}

	public void obtainClasses(String URLString) {
		requestClasses = new RequestCurriculumUnits(this);
		adapter.open();
		requestClasses.setConnectionParameters(URLString, adapter.getToken());
		adapter.close();
		requestClasses.execute();
	}

	public void updateList() {

		jsonParser = new ParseJSON(this);
		adapter.open();
		String classes = adapter.getClassesFromCourse(Long.parseLong(settings
				.getString("SelectedCourse", null)));

		parsedValues = jsonParser
				.parseJSON(classes, Constants.PARSE_CLASSES_ID);
		adapter.close();

		listAdapter = new ClassAdapter(this, parsedValues);
		setListAdapter(listAdapter);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		classIdString = (String) l.getAdapter().getItem(position);

		SharedPreferences.Editor editor = settings.edit();
		editor.putString("SelectedClass", classIdString);
		editor.commit();

		adapter.open();

		if (adapter.existsTopicsOnClass(Long.parseLong(settings.getString(
				"SelectedClass", null)))) {
			intent = new Intent(this, TopicListController.class);
			startActivity(intent);

		}

		else {

			adapter.close();
			dialog = dialogMaker
					.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
			dialog.show();
			obtainTopics(Constants.URL_GROUPS_PREFIX + classIdString
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

			if (result.length() <= 2) {
				Toast.makeText(getApplicationContext(), "Fórum Vazio",
						Toast.LENGTH_SHORT).show();
				closeDialogIfItsVisible();
			}

			else {

				adapter.open();
				adapter.updateTopicsFromClasses(result, Long.parseLong(settings
						.getString("SelectedClass", null)));
				adapter.close();

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

			adapter.open();
			adapter.updateClassesFromCourse(result,
					Long.parseLong(settings.getString("SelectedCourse", null)));
			adapter.close();

			ContentValues[] classValues = jsonParser.parseJSON(result,
					Constants.PARSE_CLASSES_ID);
			parsedValues = classValues;
			listAdapter.notifyDataSetChanged();
			closeDialogIfItsVisible();
		}

	}

	public class ClassAdapter extends BaseAdapter {

		Context context;
		ContentValues[] values;
		LayoutInflater inflater = null;

		public ClassAdapter(Context c, ContentValues[] v) {
			context = c;
			values = v;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {

			return values.length;
		}

		@Override
		public Object getItem(int position) {

			return values[position].getAsString("id");

		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.curriculum_units_item,
						parent, false);
				TextView courseName = (TextView) convertView
						.findViewById(R.id.turmas_item);
				courseName.setText(values[position].getAsString("code"));
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

			String selectedCourse = settings.getString("SelectedCourse", null);
			dialog = dialogMaker
					.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
			dialog.show();
			obtainClasses(Constants.URL_CURRICULUM_UNITS_PREFIX
					+ selectedCourse + Constants.URL_GROUPS_SUFFIX);

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

	public class DialogHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

		}
	}
}
