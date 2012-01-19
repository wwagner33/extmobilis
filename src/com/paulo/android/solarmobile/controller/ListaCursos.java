package com.paulo.android.solarmobile.controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

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

public class ListaCursos extends Activity implements OnItemClickListener {
	public Intent intent;
	private static final String[] nomeCursos = { "Química", "Matemática",
			"Biologia", "História", "Engenharia", "Mecânica", "Engenharia",
			"Química", "Computação", "Estatística" };
	String courseList;
	String[] teste;
	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cursos);

		AndroidConnection connection = new AndroidConnection(this);

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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		intent = new Intent(this, TopicListController.class);
		startActivity(intent);
	}

	public class AndroidConnection extends Connection {
		KeyFinder finder;

		public AndroidConnection(Context context) {
			super(context);
		}

		@Override
		public ContentValues[] parse(String source) {

			/*
			 * ContentValues parseContainer[]; JSONObject jsonObjects[];
			 * Log.w("Parse", "InsideParse"); finder = new KeyFinder(); Object
			 * object = JSONValue.parse(source); JSONArray jsonArray =
			 * (JSONArray) object; Log.w("Tamanho Array",
			 * String.valueOf(jsonArray.size())); jsonObjects = new
			 * JSONObject[jsonArray.size()]; parseContainer = new
			 * ContentValues[jsonArray.size()];
			 * 
			 * for (int i = 0; i < jsonArray.size(); i++) { jsonObjects[i] =
			 * (JSONObject) jsonArray.get(i); }
			 */
			return null;
		}
	}
}