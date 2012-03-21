package com.mobilis.controller;

import java.io.File;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilis.model.DBAdapter;
import com.mobilis.threads.RequestImagesThread;
import com.mobilis.threads.RequestNewPostsThread;
import com.mobilis.threads.RequestPostsThread;
import com.mobilis.threads.RequestTopicsThread;
import com.mobilis.util.ZipManager;

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
	private Dialogs dialogs;
	private RequestImages requestImages;
	private ZipManager zipManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.topic);
		zipManager = new ZipManager();
		dialogs = new Dialogs(this);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		adapter = new DBAdapter(this);
		updateList();
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

		adapter.open();
		jsonParser = new ParseJSON(this);

		String topics = adapter.getTopicsFromClasses(Long.parseLong(settings
				.getString("SelectedClass", null)));
		parsedValues = jsonParser.parseJSON(topics, Constants.PARSE_TOPICS_ID);
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

			Log.w("FORUMCLOSED", "TRUE");
			editor.putBoolean("isForumClosed", true);

		} else {
			editor.putBoolean("isForumClosed", false);
		}

		editor.putString("SelectedTopic", topicIdString);
		editor.putString("CurrentForumName", forumName);
		editor.commit();

		adapter.open();
		if (adapter.postExistsOnTopic(Long.parseLong(topicIdString))) {
			adapter.close();
			Log.w("TRUE", "TRUE");

			intent = new Intent(this, PostList.class);
			startActivity(intent);

			// getImages();

		} else {
			Log.w("FALSE", "FALSE");
			adapter.close();
			dialog = dialogs.getProgressDialog();
			dialog.show();
			String url = "discussions/" + topicIdString + "/posts/"
					+ Constants.oldDateString + "/news.json";
			obtainNewPosts(url);
		}

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

	public void getImages(String idPosts) {
		requestImages = new RequestImages(this);
		adapter.open();
		// String testeURL = "images/1,2,3,5,7/users";
		requestImages.setConnectionParameters(idPosts, adapter.getToken());
		adapter.close();
		requestImages.execute();
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
			adapter.updatePostsFromTopic(result,
					Long.parseLong(settings.getString("SelectedTopic", null)));
			adapter.close();

			intent = new Intent(getApplicationContext(), PostList.class);
			startActivity(intent);


			/*
			 * Checar se as imagens dos posts já existem na pasta Images
			 * Antes de baixar;
			 */

			// getImages();
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

	public class RequestImages extends RequestImagesThread {

		public RequestImages(Context context) {
			super(context);
		}

		@Override
		public void onRequestImagesSucceded(String result) {

			zipManager.unzipFile();
			intent = new Intent(getApplicationContext(), PostList.class);
			startActivity(intent);

		}

		@Override
		public void onRequestImagesFailed() {
			Toast.makeText(getApplicationContext(), "Failed",
					Toast.LENGTH_SHORT).show();
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
			return contentAtPosisiton;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			convertView = inflater.inflate(R.layout.topicitem, parent, false);

			if (values[position].getAsString("isClosed").equals("t")) {

				StateListDrawable states = new StateListDrawable();
				states.addState(
						new int[] {},
						getResources().getDrawable(
								R.drawable.course_list_closed_selector));
				convertView.setBackgroundDrawable(states);
				TextView topicTitle = (TextView) convertView
						.findViewById(R.id.topic_name);
				LinearLayout teste = (LinearLayout) convertView
						.findViewById(R.id.left_bar);
				teste.setBackgroundColor(R.color.very_dark_gray);

				topicTitle.setTextColor(R.color.very_dark_gray);
				topicTitle.setText(values[position].getAsString("name"));
				Log.w("isClosed", values[position].getAsString("isClosed"));
				return convertView;

			}

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
			dialog = dialogs.getProgressDialog();
			dialog.show();
			obtainTopics(Constants.URL_GROUPS_PREFIX + currentClass
					+ Constants.URL_DISCUSSION_SUFFIX);
		}
		return true;

	}
}
