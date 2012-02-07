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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.paulo.android.solarmobile.model.DBAdapter;
import com.paulo.android.solarmobile.ws.Connection;

public class ResponseController extends Activity implements OnClickListener,
		OnKeyListener {

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

	ObtainPostListThread postThread;

	String forumName;

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
		message.setOnKeyListener(this);

		connection = new Connection(this);

		if (extras != null) {
			topicId = extras.getString("topicId");
			forumName = extras.getString("ForumName");

		}
		// submit.setOnClickListener(this);

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
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

	public void closeDialogIfItsVisible() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();

	}

	@Override
	public void onClick(View v) {

		// if (message.getText().toString().length() < 9) {
		// handleError(Constants.BELOW_CHARACTER_LIMIT);
		// } else {
		responseJSON = new JSONObject();
		LinkedHashMap jsonMap = new LinkedHashMap<String, String>();
		jsonMap.put("content", message.getText().toString());
		if (extras.getLong("parentId") > 0) {
			jsonMap.put("parent_id", String.valueOf(extras.getLong("parentId")));

		} else {
			jsonMap.put("parent_id", noParent);
		}
		responseJSON.put("discussion_post", jsonMap);
		JSONObjectString = responseJSON.toJSONString();
		Log.w("JSONString", JSONObjectString);

		sendPost();
		// }
	}

	public void sendPost() {
		dialog.show();
		adapter = new DBAdapter(this);
		adapter.open();
		token = adapter.getToken();
		adapter.close();

		URL = "discussions/" + topicId + "/posts?auth_token=" + token; // CERTA
		// URL = "discussions/" + topicId + "/posts?auth_token="; // token;
		Log.w("URL", URL);

		thread = new SendNewPostThread();
		thread.execute();
	}

	public void getPosts() {
		postThread = new ObtainPostListThread();
		postThread.execute(token);
	}

	public class SendNewPostThread extends AsyncTask<Void, Void, Object[]> {

		@Override
		protected Object[] doInBackground(Void... params) {

			try {
				// connection = new Connection(getApplicationContext());
				return connection.postToServer(JSONObjectString, URL);
				// Log.w("POSTResult", result);
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
		protected void onPostExecute(Object[] result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (result == null) {
				closeDialogIfItsVisible();
				ErrorHandler.handleError(getApplicationContext(),
						Constants.ERROR_CONNECTION_REFUSED);
			}

			else {
				int statusCode = (Integer) result[1];
				if (statusCode == 201 || statusCode == 200) {
					getPosts();
				} else {
					closeDialogIfItsVisible();
					ErrorHandler.handleError(getApplicationContext(),
							statusCode);

				}

				// Intent intent = new Intent(getApplicationContext(),tete
				// PostList.class);
				// startActivity(intent);
			}
		}

	}

	public void handleError(int errorId) {
		Toast.makeText(getApplicationContext(),
				"Texto não pode ser enor do que 9 caracteres",
				Toast.LENGTH_LONG).show();
	}

	public class ObtainPostListThread extends AsyncTask<String, Void, Object[]> {

		Intent intent;

		@Override
		protected Object[] doInBackground(String... params) {

			try {

				return connection.getFromServer("discussions/" + topicId
						+ "/posts.json", params[0]);

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

			// return null;

		}

		@Override
		protected void onPostExecute(Object[] result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			adapter.close();

			if (result == null) {

				ErrorHandler.handleError(getApplicationContext(),
						Constants.ERROR_CONNECTION_REFUSED);
			}

			else {

				int statusCode = (Integer) result[1];

				if (statusCode == 200) {

					intent = new Intent(getApplicationContext(), PostList.class);
					intent.putExtra("ForumName", forumName); // onDetails
					intent.putExtra("PostList", (String) result[0]); // OK
					intent.putExtra("topicId", topicId); // OK
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("TESTE", "TESTE");
					startActivity(intent);
				} else {

					closeDialogIfItsVisible();
					ErrorHandler.handleError(getApplicationContext(),
							statusCode);

					// Toast.makeText(getApplicationContext(), "posts failed",
					// Toast.LENGTH_SHORT).show();
					// intent = new Intent(getApl)
				}
				// Log.w("Turmas", groupsResult);
			}
		}

	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {

		int editTextRowCount;
		// if enter is pressed start calculating
		if (keyCode == KeyEvent.KEYCODE_ENTER
				&& event.getAction() == KeyEvent.ACTION_UP) {

			// get EditText text
			String text = ((EditText) v).getText().toString();

			// find how many rows it cointains
			editTextRowCount = text.split("\\n").length;

			// user has input more than limited - lets do something
			// about that
			if (editTextRowCount >= 3) {

				// find the last break
				int lastBreakIndex = text.lastIndexOf("\n");

				// compose new text
				String newText = text.substring(0, lastBreakIndex);

				// add new text - delete old one and append new one
				// (append because I want the cursor to be at the end)
				((EditText) v).setText("");
				((EditText) v).append(newText);

			}
		}

		return false;
	}
}
