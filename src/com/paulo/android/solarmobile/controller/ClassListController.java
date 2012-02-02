package com.paulo.android.solarmobile.controller;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
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

import com.paulo.android.solarmobile.model.DBAdapter;
import com.paulo.android.solarmobile.ws.Connection;

public class ClassListController extends ListActivity {

	DBAdapter adapter;
	ParseJSON jsonParser;
	ContentValues[] parsedValues;
	ClassAdapter listAdapter;

	String result;
	String connectionResult;
	Connection connection;

	ProgressDialog dialog;

	String classIdString;
	String extrasString;

	Intent intent;

	ObtainTopicListThread thread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.turmas);
		adapter = new DBAdapter(this);
		connection = new Connection(this);

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


	public void obtainTopics(String authToken) {
		thread = new ObtainTopicListThread();
		thread.execute(authToken);
	}

	public void updateList() {
		// adapter.open();
		// String classesFromDB = adapter.getGroups();
		// Log.w("Turmas", classesFromDB);
		jsonParser = new ParseJSON();
		parsedValues = jsonParser.parseJSON(extrasString,
				Constants.PARSE_CLASSES);
		listAdapter = new ClassAdapter(this, parsedValues);
		setListAdapter(listAdapter);
		// adapter.close();

	}

	public void updateList(String temp) {
		jsonParser = new ParseJSON();
		parsedValues = jsonParser.parseJSON(temp, Constants.PARSE_CLASSES);
		listAdapter = new ClassAdapter(this, parsedValues);
		setListAdapter(listAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		classIdString = (String) l.getAdapter().getItem(position);
		adapter.open();
		dialog = Dialogs.getProgressDialog(this);
		dialog.show();
		obtainTopics(adapter.getToken());

	}

	public class ObtainTopicListThread extends
			AsyncTask<String, Void, Object[]> {

		@Override
		protected Object[] doInBackground(String... params) {
			try {

				return connection.getFromServer("groups/" + classIdString
						+ "/discussions.json", params[0]);

			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return null;

			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

			catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}

		@Override
		protected void onPostExecute(Object[] result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			adapter.close();
			int statusCode = (Integer) result[1];

			if (statusCode == 200) {

				intent = new Intent(getApplicationContext(),
						TopicListController.class);

				intent.putExtra("TopicList", (String)result[0]);
				startActivity(intent);

			} else {
				closeDialogIfItsVisible();
				ErrorHandler.handleError(getApplicationContext(), statusCode);
			}
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
