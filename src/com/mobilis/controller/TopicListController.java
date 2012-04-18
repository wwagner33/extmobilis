package com.mobilis.controller;

import java.io.File;
import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilis.dialog.DialogMaker;
import com.mobilis.model.PostDAO;
import com.mobilis.model.TopicDAO;
import com.mobilis.util.ZipManager;
import com.mobilis.ws.Connection;

public class TopicListController extends ListActivity {

	private Intent intent;
	private ParseJSON jsonParser;
	private String forumName;
	private ProgressDialog dialog;
	private SharedPreferences settings;

	private ZipManager zipManager;
	private DialogMaker dialogMaker;
	private TopicDAO topicDAO;
	private PostDAO postDAO;
	private Cursor cursor;
	private TopicAdapter listAdapter;
	private TopicHandler handler;
	private Connection connection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.topic);

		handler = new TopicHandler();
		connection = new Connection(handler, this);
		jsonParser = new ParseJSON(this);
		postDAO = new PostDAO(this);
		topicDAO = new TopicDAO(this);
		zipManager = new ZipManager();
		dialogMaker = new DialogMaker(this);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		// adapter = new DBAdapter(this);
		updateList();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
		if (postDAO != null) {
			if (postDAO.isOpen())
				postDAO.close();
		}
		if (topicDAO != null) {
			if (postDAO.isOpen())
				topicDAO.close();
		}

	}

	public void closeDialogIfItsVisible() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();

	}

	public void updateList() {

		topicDAO.open();
		cursor = topicDAO.getTopicsFromClass(settings
				.getInt("SelectedClass", 0));
		topicDAO.close();
		listAdapter = new TopicAdapter(this, cursor);
		setListAdapter(listAdapter);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		Object content = l.getAdapter().getItem(position);
		ContentValues item = (ContentValues) content;

		int topicId = item.getAsInteger("_id");
		forumName = item.getAsString("name");

		SharedPreferences.Editor editor = settings.edit();

		Log.i("CLOSED", item.getAsString("closed"));

		if (item.getAsString("closed").equals("t")) {
			editor.putBoolean("isForumClosed", true);

		} else {
			editor.putBoolean("isForumClosed", false);
		}

		editor.putInt("SelectedTopic", topicId);
		editor.putString("CurrentForumName", forumName);
		editor.commit();

		postDAO.open();

		if (postDAO.postExistsOnTopic(topicId)) {
			postDAO.close();
			intent = new Intent(this, PostList.class);
			startActivity(intent);

		} else {
			postDAO.close();
			dialog = dialogMaker
					.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
			dialog.show();
			String url = "discussions/" + topicId + "/posts/"
					+ Constants.oldDateString + "/news.json";
			obtainNewPosts(url);
		}

	}

	public void obtainNewPosts(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_NEW_POSTS, url,
				settings.getString("token", null));
	}

	public void obtainTopics(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_TOPICS, url,
				settings.getString("token", null));

	}

	public void getImages(String url) {

		connection.getImages(Constants.CONNECTION_GET_IMAGES, url,
				settings.getString("token", null));

	}

	public class TopicAdapter extends CursorAdapter {

		LayoutInflater inflater;

		public TopicAdapter(Context context, Cursor c) {
			super(context, c);
			inflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View convertView, Context context, Cursor cursor) {

			if (cursor != null) {

				if (cursor.getString(cursor.getColumnIndex("closed")).equals(
						"t")) {

					StateListDrawable states = new StateListDrawable();
					states.addState(
							new int[] {},
							getResources().getDrawable(
									R.drawable.course_list_closed_selector));
					convertView.setBackgroundDrawable(states);

					TextView topicTitle = (TextView) convertView
							.findViewById(R.id.topic_name);
					topicTitle.setTextColor(R.color.very_dark_gray);

					LinearLayout teste = (LinearLayout) convertView
							.findViewById(R.id.left_bar);
					teste.setBackgroundColor(R.color.very_dark_gray);

					topicTitle.setTextColor(R.color.very_dark_gray);
					topicTitle.setText(cursor.getString(cursor
							.getColumnIndex("name")));
					Log.w("isClosed",
							cursor.getString(cursor.getColumnIndex("closed")));

				}

				TextView topicTitle = (TextView) convertView
						.findViewById(R.id.topic_name);

				topicTitle.setText(cursor.getString(cursor
						.getColumnIndex("name")));

				Log.w("isClosed",
						cursor.getString(cursor.getColumnIndex("closed")));
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return inflater.inflate(R.layout.topicitem, parent, false);
		}

		@Override
		public Object getItem(int position) {
			ContentValues item = new ContentValues();
			item.put("closed",
					getCursor().getString(getCursor().getColumnIndex("closed")));
			item.put("_id",
					getCursor().getInt(getCursor().getColumnIndex("_id")));
			item.put("name",
					getCursor().getString(getCursor().getColumnIndex("name")));
			return item;
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

			int currentClass = settings.getInt("SelectedClass", 0);
			dialog = dialogMaker
					.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
			dialog.show();
			obtainTopics(Constants.URL_GROUPS_PREFIX + currentClass
					+ Constants.URL_DISCUSSION_SUFFIX);
		}
		if (item.getItemId() == R.id.menu_logout) {

			SharedPreferences.Editor editor = settings.edit();
			editor.putString("token", null);
			editor.commit();
			intent = new Intent(this, Login.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

		}

		if (item.getItemId() == R.id.menu_config) {
			intent = new Intent(this, Config.class);
			startActivity(intent);
		}
		return true;

	}

	private class TopicHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			if (msg.what == Constants.MESSAGE_CONNECTION_FAILED) {
				closeDialogIfItsVisible();
			}

			if (msg.what == Constants.MESSAGE_TOPIC_CONNECTION_OK) {

				ContentValues[] values = jsonParser.parseJSON(msg.getData()
						.getString("content"), Constants.PARSE_TOPICS_ID);

				topicDAO.open();
				topicDAO.addTopics(values, settings.getInt("SelectedClass", 0));
				topicDAO.close();
				updateList();
				closeDialogIfItsVisible();

			}

			if (msg.what == Constants.MESSAGE_NEW_POST_CONNECTION_OK) {

				intent = new Intent(getApplicationContext(), PostList.class);

				ArrayList<ContentValues> parsedValues = jsonParser
						.parsePosts(msg.getData().getString("content"));

				postDAO.open();
				postDAO.addPosts(parsedValues,
						settings.getInt("SelectedTopic", 0));

				File file = new File(Constants.PATH_IMAGES);

				if (file.exists()) {

					if (file.list().length > 0) {

						String ids = postDAO.getUserIdsAbsentImage();

						if (ids != null) {
							getImages("images/" + ids + "/users");
						}

						else {
							startActivity(intent);
						}
					}
				}

				else {
					String ids = postDAO.getAllUserIds();
					postDAO.close();
					getImages("images/" + ids + "/users");
				}

			}

			if (msg.what == Constants.MESSAGE_IMAGE_CONNECTION_OK) {

				zipManager.unzipFile();
				intent = new Intent(getApplicationContext(), PostList.class);
				startActivity(intent);
			}
			if (msg.what == Constants.MESSAGE_IMAGE_CONNECION_FAILED) {
				Toast.makeText(getApplicationContext(), "Images Failed",
						Toast.LENGTH_SHORT).show();
				intent = new Intent(getApplicationContext(), PostList.class);
				startActivity(intent);
			}
		}
	}
}
