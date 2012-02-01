package com.paulo.android.solarmobile.controller;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.paulo.android.solarmobile.model.DBAdapter;
import com.paulo.android.solarmobile.ws.Connection;

public class ResponseController extends Activity implements OnClickListener {

	// Wedson: curl -v -H 'Content-Type: application/json' -H 'Accept:
	// application/json' -X POST
	// http://localhost:3000/discussions/1/posts?auth_token=B3BQ1twAooSXWY53hktp
	// --data '{"discussion_post":{"content":"estou criando um novo post dentro
	// de 1", "parent_id":""}}'

	// reponder um forum

	// File recordingsFolder;

	EditText message;
	Button submit;
	Connection connection;
	JSONObject responseJSON;
	ProgressDialog dialog;
	public String topicId;
	Bundle extras;
	long parentId;
	String noParent = "";
	String URL;
	SendNewPostThread thread;
	String JSONObjectString;
	DBAdapter adapter;
	String token;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.responder_topico);
		extras = getIntent().getExtras();
		connection = new Connection(this);
		dialog = Dialogs.getProgressDialog(this);
		submit = (Button) findViewById(R.id.criar_topico_submit);
		submit.setOnClickListener(this);
		message = (EditText) findViewById(R.id.criar_topico_conteudo);
		if (extras != null) {
			topicId = extras.getString("topicId");

		}
		// submit.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

	//	if (message.getText().toString().length() < 9) {
	//		handleError(Constants.BELOW_CHARACTER_LIMIT);
	//	} else {
			responseJSON = new JSONObject();
			LinkedHashMap<String,String> jsonMap = new LinkedHashMap<String, String>();
			jsonMap.put("content", message.getText().toString());
			if (extras.getLong("parentId") > 0) {
				jsonMap.put("parent_id",
						String.valueOf(extras.getLong("parentId")));
				
			} else {
				jsonMap.put("parent_id", noParent);
			}
			responseJSON.put("discussion_post", jsonMap);
			JSONObjectString = responseJSON.toJSONString();
			Log.w("JSONString", JSONObjectString);
			
			sendPost();
//		}
	}

	public void sendPost() {
		adapter = new DBAdapter(this);
		adapter.open();
		token = adapter.getToken();
		adapter.close();
		
		 URL = "discussions/" + topicId +"/posts?auth_token=" + token;
		 Log.w("URL", URL);
		 
		thread = new SendNewPostThread();
		thread.execute();
	}

	public class SendNewPostThread extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {

		
			try {
				connection = new Connection(getApplicationContext());
				String result = connection.postToServer(JSONObjectString, URL);
				Log.w("POSTResult",result);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

		//	Intent intent = new Intent(getApplicationContext(), PostList.class);
		//	startActivity(intent);
		}

	}

	public void handleError(int errorId) {
		Toast.makeText(getApplicationContext(),
				"Texto não pode ser enor do que 9 caracteres",
				Toast.LENGTH_LONG).show();
	}
}
