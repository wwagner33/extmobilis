package com.paulo.android.solarmobile;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListaCursos extends Activity implements OnItemClickListener {
			public Intent intent;
			private String[] nomeCursos = {"Química","Matemática","Biologia","História","Engenharia","Mecânica","Engenharia" ,"Química","Computação","Estatística"};
			public ListaCursosAdapter adapter;
			private ListView lv;
	/*
	 * 
	 * 
	 * 
	 * */
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.cursos);
			lv = (ListView) findViewById(R.id.list);
	
			lv.setOnItemClickListener(this);
			adapter = new ListaCursosAdapter(this,nomeCursos);
			lv.setAdapter(adapter);
			
	
			
}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// Toast.makeText(this, "teste", Toast.LENGTH_SHORT).show();
			// intent = new Intent(ListaCursos.this,ListaPosts.class); // udar pra lista de Tópicos
			intent = new Intent(ListaCursos.this,ListaPosts.class);
			startActivity(intent);
	
			
		}

}