package com.paulo.android.solarmobile.controller;



import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.paulo.android.solarmobile.ws.Connection;

	// curriculum_units/:id/groups       
	// lista todas as turmas do aluno dentro desta unidade curricular


public class ListaCursos extends Activity implements OnItemClickListener {
	public Intent intent;
	private static final String[] nomeCursos = { "Química", "Matemática",
			"Biologia", "História", "Engenharia", "Mecânica", "Engenharia",
			"Química", "Computação", "Estatística" };
	
	String courseList;
	String[] teste;
	private ListView lv;
	AndroidConnection connection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cursos);

		 connection = new AndroidConnection(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			Object jsons[] = (Object[])extras.get("CourseList");
			Log.w("JSONS SIZE", String.valueOf(jsons.length));
			ContentValues receivedValues[] = new ContentValues[jsons.length];
			teste = new String[jsons.length];
			
			for (int i=0;i<jsons.length;i++) {
				receivedValues[i] = (ContentValues)jsons[i];
				teste[i] = receivedValues[i].getAsString("nomeCurso");
			}
		}

	
		
		lv = (ListView) findViewById(R.id.list);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.itemcurso, teste);
		lv.setOnItemClickListener(this);
		lv.setAdapter(adapter);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//intent = new Intent(this, TopicListController.class);
		
		Log.w("Item position", String.valueOf(position));
		Log.w("item id", String.valueOf(id));
		
		//connection.requestJSON("curriculum_units/:+"+position+"/groups", authToken)
		
		//startActivity(intent);
	}

	public class AndroidConnection extends Connection {
		

		public AndroidConnection(Context context) {
			super(context);
		}

		@Override
		public ContentValues[] parse(String source) {
				
			return null;
		}
	}
}