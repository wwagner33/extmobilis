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

	private static final int PARSE_CLASSES = 223;

	DBAdapter adapter;
	ParseJSON jsonParser;
	ContentValues[] parsedValues;
	ClassAdapter listAdapter;

	String result;
	String connectionResult;
	Connection connection;

	ProgressDialog dialog;

	String classIdString;

	Intent intent;

	ObtainTopicListThread thread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.turmas);
		adapter = new DBAdapter(this);
		connection = new Connection(this);
		// adapter.open();

		// Log.w("teste", "lista de turmas");

		Bundle extras = getIntent().getExtras();
		String extrasString = extras.getString("GroupList");
		if (extrasString != null) {
			updateList();

		} else {

			// nada mesmo
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (adapter != null) {
			adapter.close();
		}
		if (dialog!=null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}
	

	public void handleError(int errorCode) {
		if (dialog!=null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
		
		if (errorCode == Constants.CONNECTION_ERROR_ID) {
			Toast.makeText(this, "Erro de conexão,tente novamente ",
					Toast.LENGTH_SHORT).show();
					}
	}

	public void obtainTopics(String authToken) {
		thread = new ObtainTopicListThread();
		thread.execute(authToken);
	}

	public void updateList() {
		adapter.open();
		String classesFromDB = adapter.getGroups();
		Log.w("Turmas", classesFromDB);
		jsonParser = new ParseJSON();
		parsedValues = jsonParser.parseJSON(classesFromDB, PARSE_CLASSES);
		listAdapter = new ClassAdapter(this, parsedValues);
		setListAdapter(listAdapter);
		adapter.close();

	}

	public void updateList(String temp) {
		// String classesFromDB = adapter.getGroups();
		// Log.w("Turmas", classesFromDB);
		jsonParser = new ParseJSON();
		parsedValues = jsonParser.parseJSON(temp, PARSE_CLASSES);
		listAdapter = new ClassAdapter(this, parsedValues);
		setListAdapter(listAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		// pegar o id da classe
		// Object classId = l.getAdapter().getItem(position);
		// String classId = (String)classId;

		// obtainTopics(adapter.getToken());

		// Intent intent = new Intent(this, TopicListController.class);
		// startActivity(intent);
		classIdString = (String) l.getAdapter().getItem(position);
		adapter.open();
		dialog = Dialogs.getProgressDialog(this);
		dialog.show();
		obtainTopics(adapter.getToken());

	}

	public class ObtainTopicListThread extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				// result = connection.requestJSON("curriculum_units/"
				// + semesterString + "/groups.json", authToken);

				result = connection.requestJSON("groups/" + classIdString
						+ "/discussions.json", params[0]);
				return result;

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
		protected void onPostExecute(String finalResult) {
			// TODO Auto-generated method stub
			super.onPostExecute(finalResult);
			adapter.close();

			if (finalResult == null) {
				//Toast.makeText(getApplicationContext(), "erro de conexão",
						//Toast.LENGTH_SHORT).show();
				handleError(Constants.CONNECTION_ERROR_ID);

			} else {
				intent = new Intent(getApplicationContext(),
						TopicListController.class);
				// adapter.updateGroups(finalResult);
				intent.putExtra("TopicList", result);
				startActivity(intent);
			}
			// Log.w("Turmas", groupsResult);
		}

	}

	public class ClassAdapter extends BaseAdapter {

		// Adapter geral enquanto não sabe-se quais elementos vão ficar
		// realmente na lista
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
			// TODO Auto-generated method stub
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
