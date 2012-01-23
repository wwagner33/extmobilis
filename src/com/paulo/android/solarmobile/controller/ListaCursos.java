package com.paulo.android.solarmobile.controller;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.paulo.android.solarmobile.model.DBAdapter;
import com.paulo.android.solarmobile.ws.Connection;

// curriculum_units/:id/groups       
// lista todas as turmas do aluno dentro desta unidade curricular

public class ListaCursos extends ListActivity {

	public Intent intent;

	private static final int PARSE_COURSES_ID = 222;

	String[] courseListString;
	String authToken;
	private ListView lv;
	Connection connection;
	DBAdapter adapter;
	ParseJSON jsonParser;
	ContentValues[] courseList;
	String jsonString;
	CourseListAdapter customAdapter;

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

			jsonParser = new ParseJSON();
			courseList = jsonParser.parseCourses(jsonString, PARSE_COURSES_ID);

			// Log.w("JSONS SIZE", String.valueOf(jsons.length));

			courseListString = new String[courseList.length];

			for (int i = 0; i < courseList.length; i++) {
				courseListString[i] = courseList[i].getAsString("name");
			}
			// lv = (ListView) findViewById(R.id.list);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.itemcurso, courseListString);
			// lv.setOnItemClickListener(this);
			setListAdapter(adapter);

		}

		else {
			adapter.open();
			jsonString = adapter.getCourseList();
			adapter.close(); // fechar no onStop
			updateList();

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
		jsonParser = new ParseJSON();
		courseList = jsonParser.parseCourses(getBankCourseList(),
				PARSE_COURSES_ID);
		customAdapter = new CourseListAdapter(this, courseList);
		setListAdapter(customAdapter);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this, TopicListController.class);

		Log.w("Item position", String.valueOf(position));
		Log.w("item id", String.valueOf(id));

		// connection.requestJSON("curriculum_units/:+"+position+"/groups",
		// authToken)

		startActivity(intent);
	}

	public void getCourseList() {
		// implementado quando houver refresh
	}

	public void obtainCurriculumUnits() {
		curriculumThread = new ObtainCurriculumUnitsThread();
		curriculumThread.execute();
	}

	public class ObtainCourseListThread extends AsyncTask<Void, Void, Void> {

		// thread que será chamada quando houver um botão de refresh

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}
	}

	public class ObtainCurriculumUnitsThread extends
			AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			return null;
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
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
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