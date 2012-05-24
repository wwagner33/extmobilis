package com.mobilis.controller;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilis.dao.PostDAO;
import com.mobilis.dao.TopicDAO;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.interfaces.MobilisListActivity;
import com.mobilis.util.Constants;
import com.mobilis.util.ParseJSON;
import com.mobilis.util.ZipManager;
import com.mobilis.ws.Connection;

public class TopicListController extends MobilisListActivity {

	private Intent intent;
	private ParseJSON jsonParser;
	private String forumName;
	private ProgressDialog dialog;
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
		restoreDialog();
		updateList();
	}

	@SuppressWarnings("deprecation")
	public void restoreDialog() {
		Log.i("OnRestore", "TRUE");
		if (getLastNonConfigurationInstance() != null) {
			Log.i("OnRestore2", "TRUE");
			dialog = (ProgressDialog) getLastNonConfigurationInstance();
			dialog.show();
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		Log.i("OnRetain", "True");
		if (dialog != null) {
			if (dialog.isShowing()) {
				Log.i("OnRetain2", "True");
				closeDialogIfItsVisible();
				return dialog;
			}
		}
		return null;
	}

	public void closeDialogIfItsVisible() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();

	}

	public void updateList() {

		topicDAO.open();
		cursor = topicDAO.getTopicsFromClass(getPreferences().getInt(
				"SelectedClass", 0));
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

		SharedPreferences.Editor editor = getPreferences().edit();

		if (item.getAsString("closed").equals("t")) {
			editor.putBoolean("isForumClosed", true);

		} else {
			editor.putBoolean("isForumClosed", false);
		}

		editor.putInt("SelectedTopic", topicId);
		editor.putString("CurrentForumName", forumName);
		commit(editor);

		postDAO.open();
		topicDAO.open();

		if (postDAO.postExistsOnTopic(topicId)
				&& !topicDAO.hasNewPostsFlag(topicId)) {
			postDAO.close();
			topicDAO.close();
			intent = new Intent(this, PostList.class);
			closeDialogIfItsVisible();
			startActivity(intent);

		} else {
			postDAO.close();
			topicDAO.close();
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
				getPreferences().getString("token", null));
	}

	public void obtainTopics(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_TOPICS, url,
				getPreferences().getString("token", null));

	}

	public void getImages(String url) {

		connection.getImages(Constants.CONNECTION_GET_IMAGES, url,
				getPreferences().getString("token", null));
	}

	public class TopicAdapter extends CursorAdapter {

		LayoutInflater inflater;

		@SuppressWarnings("deprecation")
		public TopicAdapter(Context context, Cursor c) {
			super(context, c);
			inflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View convertView, Context context, Cursor cursor) {

			if (cursor.getString(cursor.getColumnIndex("closed")).equals("t")) {

				LinearLayout leftBar = (LinearLayout) convertView
						.findViewById(R.id.left_bar);

				StateListDrawable states = new StateListDrawable();
				states.addState(
						new int[] {},
						getResources().getDrawable(
								R.drawable.course_list_closed_selector));
				convertView.setBackgroundDrawable(states);

				TextView topicTitle = (TextView) convertView
						.findViewById(R.id.topic_name);
				topicTitle.setTextColor(getResources().getColor(
						R.color.very_dark_gray));

				leftBar = (LinearLayout) convertView
						.findViewById(R.id.left_bar);

				leftBar.setBackgroundColor(getResources().getColor(
						R.color.very_dark_gray));

				topicTitle.setTextColor(getResources().getColor(
						R.color.very_dark_gray));

				topicTitle.setText(cursor.getString(cursor
						.getColumnIndex("name")));
				Log.w("isClosed",
						cursor.getString(cursor.getColumnIndex("closed")));
			}

			if (cursor.getInt(cursor.getColumnIndex("has_new_posts")) == 1
					&& cursor.getString(cursor.getColumnIndex("closed"))
							.equals("f")) {

				LinearLayout leftBar = (LinearLayout) convertView
						.findViewById(R.id.left_bar);

				Log.i("HASNEWPOSTS", "TRUE");

				leftBar.setBackgroundColor(Color.YELLOW);
			}

			TextView topicTitle = (TextView) convertView
					.findViewById(R.id.topic_name);

			topicTitle.setText(cursor.getString(cursor.getColumnIndex("name")));

			Log.w("isClosed", cursor.getString(cursor.getColumnIndex("closed")));
			// }
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.w("OnActivityResult", "OK");
		updateList();
	}

	private class TopicHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {

			case Constants.MESSAGE_CONNECTION_FAILED:
				closeDialogIfItsVisible();
				break;

			case Constants.MESSAGE_TOPIC_CONNECTION_OK:

				ContentValues[] values = jsonParser.parseJSON(msg.getData()
						.getString("content"), Constants.PARSE_TOPICS_ID);

				topicDAO.open();

				for (int i = 0; i < values.length; i++) {
					if (topicDAO.hasNewPosts(values[i].getAsInteger("_id"),
							values[i].getAsString("last_post_date"))) {
						Log.i("TAG", "Existem posts novos");
						values[i].put("has_new_posts", true);
					} else {
						Log.v("TAG", "Não há posts novos");
					}
				}

				topicDAO.addTopics(values,
						getPreferences().getInt("SelectedClass", 0));
				topicDAO.close();
				updateList();
				closeDialogIfItsVisible();
				break;

			case Constants.MESSAGE_NEW_POST_CONNECTION_OK:

				topicDAO.open();
				if (topicDAO.hasNewPostsFlag(getPreferences().getInt(
						"SelectedTopic", 0))) {
					ContentValues newFlag = new ContentValues();
					newFlag.put("has_new_posts", 0);
					topicDAO.updateFlag(newFlag,
							getPreferences().getInt("SelectedTopic", 0));
				}
				topicDAO.close();

				intent = new Intent(getApplicationContext(), PostList.class);

				ArrayList<ContentValues> parsedValues = jsonParser
						.parsePosts(msg.getData().getString("content"));

				if (parsedValues.size() == 0) {
					closeDialogIfItsVisible();
					startActivityForResult(intent, 1);
				}

				else {
					Log.i("Content", msg.getData().getString("content"));

					parsedValues = jsonParser.parsePosts(msg.getData()
							.getString("content"));

					postDAO.open();
					postDAO.addPosts(parsedValues,
							getPreferences().getInt("SelectedTopic", 0));

					try {
						String ids = postDAO
								.getUserIdsAbsentImage(getPreferences().getInt(
										"SelectedTopic", 0));
						postDAO.close();
						getImages("images/" + ids + "/users");
						Log.i("Alguns usuários não possuem imagens", "TRUE");
					} catch (StringIndexOutOfBoundsException e) {
						postDAO.close();
						Log.i("Não precisa Baixar novas imagens", "TRUE");
						closeDialogIfItsVisible();
						startActivityForResult(intent, 1);
					} catch (NullPointerException e) {
						Log.i("Baixar todas as imagens", "TRUE");
						String ids = postDAO.getAllUserIds();
						postDAO.close();
						getImages("images/" + ids + "/users");
					}
				}
				break;

			case Constants.MESSAGE_IMAGE_CONNECTION_OK:

				zipManager.unzipFile();
				intent = new Intent(getApplicationContext(), PostList.class);
				closeDialogIfItsVisible();
				startActivityForResult(intent, 1);
				break;

			case Constants.MESSAGE_IMAGE_CONNECION_FAILED:

				intent = new Intent(getApplicationContext(), PostList.class);
				closeDialogIfItsVisible();
				startActivityForResult(intent, 1);
				break;

			}
		}
	}

	@Override
	public void menuRefreshItemSelected() {
		int currentClass = getPreferences().getInt("SelectedClass", 0);
		dialog = dialogMaker
				.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
		dialog.show();
		obtainTopics(Constants.URL_GROUPS_PREFIX + currentClass
				+ Constants.URL_DISCUSSION_SUFFIX);
	}
}
