package com.paulo.android.solarmobile.controller;



import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.paulo.android.solarmobile.model.DBAdapter;

public class Main extends Activity implements OnClickListener {
	public EditText LoginField, PassField;
	public Button submit;
	public Intent intent;
	DBAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		LoginField = (EditText) findViewById(R.id.campo1);
		PassField = (EditText) findViewById(R.id.campo2);
		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(this);
		adapter = new DBAdapter(this);
	}

	@Override
	public void onClick(View v) {
		if (v.equals(submit)) {
			
				/*
		
				if (LoginField.getText().toString().equals("") || PassField.getText().toString().equals("")) {
					
					Toast.makeText(this, " campos login ou senha não podem ser vazios", Toast.LENGTH_SHORT).show();
				}
				
				else {
					
					
					/*
					adapter.open();
					ContentValues valores = new ContentValues();
					valores.put("nome", PassField.getText().toString());
					adapter.updateTable("config", 1, valores);
					*/
				
			
			intent = new Intent(Main.this, ListaCursos.class);
			startActivity(intent);
			
			}
	//	}
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (adapter!=null) {
			adapter.close();
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		moveTaskToBack(true);
	}
	
}