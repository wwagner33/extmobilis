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

public class CourseListController extends ListActivity {

	public Intent intent;

	private static final int PARSE_COURSES_ID = 222;

	String[] courseListString;
	String authToken;
	Connection connection;
	DBAdapter adapter;
	ParseJSON jsonParser;
	ContentValues[] courseList;
	String jsonString;
	CourseListAdapter customAdapter;
	String result;

	ProgressDialog dialog;

	String semesterString;
	String groupsResult;

	int itemPosition;

	// Threads
	ObtainCurriculumUnitsThread curriculumThread;
	ObtainCourseListThread courseListThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cursos);

		connection = new Connection(this);
		adapter = new DBAdapter(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			jsonString = extras.getString("CourseList");
			updateList();

		} else {

			updateList();
		}
	}

	public void handleError(int errorCode) {
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
		if (errorCode == Constants.ERROR_CONNECTION_FAILED) {
			Toast.makeText(this, "Erro de conexão,tente novamente ",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
		
		Intent intent = new Intent(this,InitialConfig.class);
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

	public String getBankCourseList() {

		adapter.open();
		String bankCourseList = adapter.getCourseList();
		Log.w("Lista de Cursos", bankCourseList);
		adapter.close();
		return bankCourseList;

	}

	public void updateList() {

		adapter.open();
		jsonParser = new ParseJSON();
		courseList = jsonParser.parseJSON(adapter.getCourseList(),
				PARSE_COURSES_ID);
		adapter.close();
		customAdapter = new CourseListAdapter(this, courseList);
		setListAdapter(customAdapter);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		// Intent intent = new Intent(this, ClassListController.class);

		Log.w("Item position", String.valueOf(position));
		Log.w("item id", String.valueOf(id));

		Object semester = l.getAdapter().getItem(position);
		semesterString = (String) semester;
		Log.w("GroupID", semesterString);

		// adapter.open();

		adapter.open();
		authToken = adapter.getToken();
		Log.w("TOKEN", authToken);

		// if (adapter.groupsExist()) {
		// startActivity(intent);
		// }

		// else {
		// adapter.close();

		// obtainCurriculumUnits(adapter.getToken());
		dialog = Dialogs.getProgressDialog(this);

		dialog.show();
		obtainCurriculumUnits(authToken);

		// }

		// Log.w("GROUP_ID", String.valueOf(l.getAdapter().getItem(position)));

		// EditText teste = (EditText)v.findViewById(R.id.item);
		// teste.setText("teste");
		// authToken)

		// startActivity(intent);
	}

	public void getCourseList() {
		// implementado quando houver refresh
	}

	public void obtainCurriculumUnits(String token) {
		// adapter.open();
		// authToken = adapter.getToken();
		// adapter.close();

		curriculumThread = new ObtainCurriculumUnitsThread();
		curriculumThread.execute(token);
	}

	public class ObtainCourseListThread extends AsyncTask<String, Void, String> {
		//

		// thread que será chamada quando houver um botão de refresh

		@Override
		protected String doInBackground(String... params) {

			try {
				result = connection.getFromServer("curriculum_units/:"
						+ semesterString + "/groups", params[0]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

		}
	}

	public class ObtainCurriculumUnitsThread extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				groupsResult = connection.getFromServer("curriculum_units/"
						+ semesterString + "/groups.json", authToken);
				return groupsResult;
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
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (result == null) {
				// Toast.makeText(getApplicationContext(), "erro de conexão",
				// Toast.LENGTH_SHORT).show();
				handleError(Constants.ERROR_CONNECTION_FAILED);

			} else {
				intent = new Intent(getApplicationContext(),
						ClassListController.class);
				adapter.updateGroups(result);
				intent.putExtra("GroupList", result);
				adapter.close();
				startActivity(intent);
			}
			// Log.w("Turmas", groupsResult);
		}

	}

	public class CourseListAdapter extends BaseAdapter {

		// Adapter geral enquanto não sabe-se quais elementos vão ficar
		// realmente na lista
		Activity activity;
		ContentValues[] values;
		LayoutInflater inflater = null;

		public CourseListAdapter(Activity a, ContentValues[] v) {
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

			return values[position].getAsString("group_id");

		}

		@Override
		public long getItemId(int position) {
			// values[position].get(key)

			// int teste =
			// Integer.parseInt(values[position].getAsString("curriculum_units"));
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.itemcurso, parent,
						false);
				TextView courseName = (TextView) convertView
						.findViewById(R.id.item);
				courseName.setText(values[position].getAsString("name"));
			}
			return convertView;
		}

	}
}