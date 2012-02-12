package com.paulo.android.solarmobile.controller;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.paulo.android.solamobile.threads.RequestPostsThread;
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
	RequestPosts requestPosts;
	String forumName;
	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		
		super.onListItemClick(l, v, position, id);
		Object teste = l.getAdapter().getItem(position);
		long TopicIdLong = (Long) teste;
		topicIdString = String.valueOf(TopicIdLong);
		Log.w("TOPIC ID", topicIdString);

		dialog = Dialogs.getProgressDialog(this);
		dialog.show();
		obtainPosts("discussions/" + topicIdString + "/posts.json");

	}

	public void obtainPosts(String URLString) {

		requestPosts = new RequestPosts(this);
		adapter.open();
		requestPosts.setConnectionParameters(URLString, adapter.getToken());
		adapter.close();
		requestPosts.execute();

	}

	public class RequestPosts extends RequestPostsThread {

		public RequestPosts(Context context) {
			super(context);

		}

		@Override
		public void onPostsConnectionFailed() {
			closeDialogIfItsVisible();

		}

		@Override
		public void onPostsConnectionSucceded(String result) {
			intent = new Intent(getApplicationContext(), PostList.class);
			intent.putExtra("ForumName", forumName);
			intent.putExtra("PostList", (String) result);
			intent.putExtra("topicId", topicIdString);
			startActivity(intent);

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
			return values.length;
		}

		@Override
		public Object getItem(int position) {
			forumName = values[position].getAsString("name");
			return values[position].getAsLong("id");
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.topicoitem, parent,
						false);

				TextView topicTitle = (TextView) convertView
						.findViewById(R.id.topic_name);
				topicTitle.setText(values[position].getAsString("name"));
			}

			return convertView;
		}

	}

}
