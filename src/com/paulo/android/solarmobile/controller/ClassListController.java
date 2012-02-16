package com.paulo.android.solarmobile.controller;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.paulo.android.solamobile.threads.RequestTopicsThread;
import com.paulo.android.solarmobile.model.DBAdapter;

public class ClassListController extends ListActivity {

	private DBAdapter adapter;
	private ParseJSON jsonParser;
	private ContentValues[] parsedValues;
	private ClassAdapter listAdapter;
	private String classIdString, extrasString;;
	private ProgressDialog dialog;
	private Intent intent;
	private RequestTopics requestTopics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.curriculum_units);
		adapter = new DBAdapter(this);

		Bundle extras = getIntent().getExtras();
		extrasString = extras.getString("GroupList");
		if (extrasString != null) {
			updateList();

		} else {
			// futura atualização da lista com os valores banco
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

	public void updateList() {

		jsonParser = new ParseJSON();
		parsedValues = jsonParser.parseJSON(extrasString,
				Constants.PARSE_CLASSES_ID);
		listAdapter = new ClassAdapter(this, parsedValues);
		setListAdapter(listAdapter);

	}

	public void updateList(String temp) {
		jsonParser = new ParseJSON();
		parsedValues = jsonParser.parseJSON(temp, Constants.PARSE_CLASSES_ID);
		listAdapter = new ClassAdapter(this, parsedValues);
		setListAdapter(listAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		classIdString = (String) l.getAdapter().getItem(position);
		dialog = Dialogs.getProgressDialog(this);
		dialog.show();

		obtainTopics(Constants.URL_GROUPS_PREFIX + classIdString
				+ Constants.URL_DISCUSSION_SUFFIX);

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
			intent = new Intent(getApplicationContext(),
					TopicListController.class);
			intent.putExtra("TopicList", result);
			startActivity(intent);

		}

	}

	public class ClassAdapter extends BaseAdapter {

		Activity activity;
		ContentValues[] values;
		LayoutInflater inflater = null;

		public ClassAdapter(Activity a, ContentValues[] v) {
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

			return values[position].getAsString("id");

		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.turmas_item, parent,
						false);
				TextView courseName = (TextView) convertView
						.findViewById(R.id.turmas_item);
				courseName.setText(values[position].getAsString("code"));
			}
			return convertView;
		}

	}

}
