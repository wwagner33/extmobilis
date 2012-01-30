package com.paulo.android.solarmobile.controller;

import org.json.simple.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.paulo.android.solarmobile.ws.Connection;

public class ResponseController extends Activity implements OnClickListener {
	
	
	
	//Wedson: curl -v -H 'Content-Type: application/json' -H 'Accept: application/json' -X POST 
	//http://localhost:3000/discussions/1/posts?auth_token=B3BQ1twAooSXWY53hktp --data '{"discussion_post":{"content":"estou criando um novo post dentro de 1", "parent_id":""}}'
		  
		//reponder um forum
	
	
	

	// File recordingsFolder;
	EditText message;
	Button submit;
	Connection connection;
	JSONObject responseJSON;
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.responder_topico);

		// submit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	public class SendNewPost extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			connection = new Connection(getApplicationContext());

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			Intent intent = new Intent(getApplicationContext(), PostList.class);
			startActivity(intent);
		}

	}
}
