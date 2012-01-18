package com.paulo.android.solarmobile.controller;

import com.paulo.android.solarmobile.ws.Connection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListaCursos extends Activity implements OnItemClickListener {
	public Intent intent;
	private static final String[] nomeCursos = { "Química", "Matemática",
			"Biologia", "História", "Engenharia", "Mecânica", "Engenharia",
			"Química", "Computação", "Estatística" };
	String courseList;
	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cursos);
		
		AndroidConnection connection = new AndroidConnection(this);
		
		Bundle extras = getIntent().getExtras();
			if (extras!=null) {
		String courseList = extras.getString("CourseList");
			}
		connection.parse(courseList);
		// Log.w()		
		
		
		lv = (ListView) findViewById(R.id.list);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.itemcurso, nomeCursos);
		lv.setOnItemClickListener(this);
		lv.setAdapter(adapter);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		intent = new Intent(this, TopicListController.class);
		startActivity(intent);
	}
	
	public class AndroidConnection extends Connection {

		public AndroidConnection(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public String[] parse(String result) {
				return null;
		}
	}
}