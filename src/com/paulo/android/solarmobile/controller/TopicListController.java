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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.paulo.android.solarmobile.model.DBAdapter;
import com.paulo.android.solarmobile.ws.Connection;

public class TopicListController extends ListActivity {

	public String[] topics = { "Assunto1", "Assunto2", "Assunto3", "Assunto4",
			"Assunto5", "Assunto6" };

	private static final int PARSE_TOPICS = 224;

	String topicIdString;
	String result;
	DBAdapter adapter;
	Intent intent;
	ContentValues[] parsedValues;
	ParseJSON jsonParser;
	TopicAdapter listAdapter;
	Connection connection;
	ObtainPostListThread thread;
	String forumName;
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topico);

		connection = new Connection(this);
		adapter = new DBAdapter(this);

		Bundle extras = getIntent().getExtras();

		String extraString = extras.getString("TopicList");

		if (extraString != null) {
			updateList(extraString);
		}

		else {

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.topicoitem, topics);
			setListAdapter(adapter);

		}
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

	public void updateList(String topicJSON) {
		Log.w("onUpdateList", "TRUE");

		jsonParser = new ParseJSON();
		parsedValues = jsonParser.parseJSON(topicJSON, PARSE_TOPICS);
		Log.w("parsedLenght", String.valueOf(parsedValues.length));
		listAdapter = new TopicAdapter(this, parsedValues);
		setListAdapter(listAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		Object teste = l.getAdapter().getItem(position);
		long TopicIdLong = (Long) teste;
		topicIdString = String.valueOf(TopicIdLong);
		Log.w("TOPIC ID", topicIdString);

		dialog = Dialogs.getProgressDialog(this);
		dialog.show();
		obtainPosts();

		// Intent intent = new Intent(this, PostList.class);
		// startActivity(intent);

	}

	public void obtainPosts() {
		adapter.open();
		String token = adapter.getToken();
		adapter.close();
		thread = new ObtainPostListThread();
		thread.execute(token);

	}

	public class ObtainPostListThread extends AsyncTask<String, Void, Object[]> {

		@Override
		protected Object[] doInBackground(String... params) {
			try {

				return connection.getFromServer("discussions/" + topicIdString
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

		}

		@Override
		protected void onPostExecute(Object[] result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			adapter.close();

			if (result == null) {
				// CONNECTION REFUSED
				closeDialogIfItsVisible();
				ErrorHandler.handleError(getApplicationContext(),
						Constants.ERROR_CONNECTION_REFUSED);
			}

			else {
				int statusCode = (Integer) result[1];

				if (statusCode == 200) {

					intent = new Intent(getApplicationContext(), PostList.class);
					intent.putExtra("ForumName", forumName);
					intent.putExtra("PostList", (String) result[0]);
					intent.putExtra("topicId", topicIdString);
					startActivity(intent);
				} else {

					closeDialogIfItsVisible();
					ErrorHandler.handleError(getApplicationContext(),
							statusCode);
				}
			}
		}
	}

	public class TopicAdapter extends BaseAdapter {

		Activity activity;
		ContentValues[] values;
		LayoutInflater inflater = null;

		public TopicAdapter(Activity a, ContentValues[] v) {
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

			forumName = values[position].getAsString("name");

			return values[position].getAsLong("id");
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.topicoitem, parent,
						false);

				// Log.w("TopicTitle", values[position].getAsString("name"));

				TextView topicTitle = (TextView) convertView
						.findViewById(R.id.topic_name);
				topicTitle.setText(values[position].getAsString("name"));
			}

			return convertView;
		}

	}

}
