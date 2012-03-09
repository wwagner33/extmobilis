package com.mobilis.controller;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
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
import com.mobilis.threads.RequestNewPostsThread;
import com.mobilis.threads.RequestPostsThread;
import com.mobilis.threads.RequestTopicsThread;

public class TopicListController extends ListActivity {

	private String topicIdString;
	private DBAdapter adapter;
	private Intent intent;
	private ContentValues[] parsedValues;
	private ParseJSON jsonParser;
	private TopicAdapter listAdapter;
	private RequestPosts requestPosts;
	private String forumName;
	private ProgressDialog dialog;
	private SharedPreferences settings;
	private RequestTopics requestTopics;
	private RequestNewPosts requestNewPosts;

	private boolean updated = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// DEBUG
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
				.build());

		setContentView(R.layout.topic);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		adapter = new DBAdapter(this);
		// Bundle extras = getIntent().getExtras();
		// String extraString = extras.getString("TopicList");

		// if (extraString != null) {
		updateList();
		// }

		// else {
		// }
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

	public void updateList() {
		// Log.w("onUpdateList", "TRUE");

		adapter.open();
		jsonParser = new ParseJSON();
		parsedValues = jsonParser.parseJSON(adapter.getTopics(),
				Constants.PARSE_TOPICS_ID);
		adapter.close();
		Log.w("parsedLenght", String.valueOf(parsedValues.length));
		listAdapter = new TopicAdapter(this, parsedValues);
		setListAdapter(listAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		Object teste = l.getAdapter().getItem(position);
		ContentValues valuesSelected = (ContentValues) teste;

		long TopicIdLong = valuesSelected.getAsLong("id");
		topicIdString = String.valueOf(TopicIdLong);

		SharedPreferences.Editor editor = settings.edit();

		if (valuesSelected.getAsString("isClosed").equals("t")) {

			editor.putBoolean("isForumClosed", true);

		} else {
			editor.putBoolean("isForumClosed", false);
		}

		editor.putString("SelectedTopic", topicIdString);
		editor.putString("CurrentForumName", forumName);
		editor.commit();

		// Log.w("TOPIC ID", topicIdString);

		dialog = Dialogs.getProgressDialog(this);
		dialog.show();

		// chamada velha
		// obtainPosts(Constants.URL_DISCUSSION_PREFIX + topicIdString
		// + Constants.URL_POSTS_SUFFIX);

		// Nova chamada

		// adapter.open();
		// if (!adapter.postsStringExists()) {
		// adapter.close();
		String url = "discussions/" + topicIdString + "/posts/"
				+ Constants.oldDateString + "/news.json";
		// Log.w("NEW POSTS URL", url);
		obtainNewPosts(url);
		// } else {
		// adapter.close();
		// intent = new Intent(this, PostList.class);
		// startActivity(intent);
		// }

	}

	public void obtainNewPosts(String urlString) {
		requestNewPosts = new RequestNewPosts(this);
		adapter.open();
		requestNewPosts.setConnectionParameters(urlString, adapter.getToken());
		Log.w("Posts String", urlString);
		adapter.close();
		requestNewPosts.execute();
	}

	public void obtainPosts(String URLString) {

		requestPosts = new RequestPosts(this);
		adapter.open();
		requestPosts.setConnectionParameters(URLString, adapter.getToken());
		adapter.close();
		requestPosts.execute();

	}

	public void obtainTopics(String URLString) {
		requestTopics = new RequestTopics(this);
		adapter.open();
		requestTopics.setConnectionParameters(URLString, adapter.getToken());
		adapter.close();
		requestTopics.execute();
	}

	public class RequestNewPosts extends RequestNewPostsThread {

		public RequestNewPosts(Context context) {
			super(context);
		}

		@Override
		public void onNewPostsConnectionFalied() {
			closeDialogIfItsVisible();
		}

		@Override
		public void onNewPostConnectionSecceded(String result) {
			Log.w("NEW POSTS RESULT", result);
			adapter.open();
			adapter.updatePostsString(result);
			adapter.close();
			intent = new Intent(getApplicationContext(), PostList.class);
			startActivity(intent);

		}

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
			adapter.open();
			adapter.updatePostsString(result);
			adapter.close();
			startActivity(intent);

		}
	}

	public class RequestTopics extends RequestTopicsThread {

		public RequestTopics(Context context) {
			super(context);
		}

		@Override
		public void onTopicsConnectionFailed() {
			closeDialogIfItsVisible();

		}

		@Override
		public void onTopicsConnectionSucceded(String result) {
			adapter.open();
			adapter.updateTopics(result);
			adapter.close();
			updateList();
			closeDialogIfItsVisible();

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
			ContentValues contentAtPosisiton = values[position];
			forumName = values[position].getAsString("name");
			// return values[position].getAsLong("id");
			return contentAtPosisiton;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			convertView = inflater.inflate(R.layout.topicitem, parent, false);

			TextView topicTitle = (TextView) convertView
					.findViewById(R.id.topic_name);
			topicTitle.setText(values[position].getAsString("name"));
			Log.w("isClosed", values[position].getAsString("isClosed"));
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

		if (item.getItemId() == R.id.menu_refresh) {

			String currentClass = settings.getString("SelectedClass", null);
			dialog = Dialogs.getProgressDialog(this);
			dialog.show();
			obtainTopics(Constants.URL_GROUPS_PREFIX + currentClass
					+ Constants.URL_DISCUSSION_SUFFIX);
		}
		return true;

	}
}
