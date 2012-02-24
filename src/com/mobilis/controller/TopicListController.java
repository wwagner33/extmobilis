package com.mobilis.controller;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilis.model.DBAdapter;
import com.mobilis.threads.RequestPostsThread;
//import com.paulo.android.solarmobile.controller.R;

public class TopicListController extends ListActivity {

	private String[] topics = { "Assunto1", "Assunto2", "Assunto3", "Assunto4",
			"Assunto5", "Assunto6" };

	private String topicIdString;
	private DBAdapter adapter;
	private Intent intent;
	private ContentValues[] parsedValues;
	private ParseJSON jsonParser;
	private TopicAdapter listAdapter;
	private RequestPosts requestPosts;
	private String forumName;
	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topic);
		adapter = new DBAdapter(this);
		Bundle extras = getIntent().getExtras();
		String extraString = extras.getString("TopicList");

		if (extraString != null) {
			updateList(extraString);
		}

		else {

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.topicitem, topics);
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
		parsedValues = jsonParser.parseJSON(topicJSON,
				Constants.PARSE_TOPICS_ID);
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
		obtainPosts(Constants.URL_DISCUSSION_PREFIX + topicIdString
				+ Constants.URL_POSTS_SUFFIX);

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

		public TopicAdapter(Activity activity, ContentValues[] values) {
			this.activity = activity;
			this.values = values;
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
				convertView = inflater.inflate(R.layout.topicitem, parent,
						false);

				TextView topicTitle = (TextView) convertView
						.findViewById(R.id.topic_name);
				topicTitle.setText(values[position].getAsString("name"));
				Log.w("isClosed", values[position].getAsString("isClosed"));
			}

			return convertView;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// return super.onOptionsItemSelected(item);
		if (item.getItemId() == R.id.menu_refresh) {
			// TBI
		}
		return true;

	}
}
