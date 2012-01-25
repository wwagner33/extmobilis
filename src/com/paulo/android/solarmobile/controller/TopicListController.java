package com.paulo.android.solarmobile.controller;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

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

	public class ObtainPostListThread extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {

				result = connection.requestJSON("discussions/" + topicIdString
						+ "/posts.json", params[0]);

				return result;

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
		protected void onPostExecute(String finalResult) {
			// TODO Auto-generated method stub
			super.onPostExecute(finalResult);
			adapter.close();

			if (finalResult == null) {
				Toast.makeText(getApplicationContext(), "erro de conexão",
						Toast.LENGTH_SHORT).show();

			} else {
				 intent = new Intent(getApplicationContext(),
				 TopicListController.class);
				 adapter.updateGroups(finalResult);
				 intent.putExtra("TopicList", finalResult);
				 startActivity(intent);
			}
			// Log.w("Turmas", groupsResult);
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
			// TODO Auto-generated method stub
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
