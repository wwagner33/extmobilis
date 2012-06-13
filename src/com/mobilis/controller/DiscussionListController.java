package com.mobilis.controller;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilis.dao.DiscussionDAO;
import com.mobilis.dao.PostDAO;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.interfaces.ConnectionCallback;
import com.mobilis.interfaces.MobilisListActivity;
import com.mobilis.model.DiscussionPost;
import com.mobilis.util.Constants;
import com.mobilis.util.ErrorHandler;
import com.mobilis.util.MobilisStatus;
import com.mobilis.util.ParseJSON;
import com.mobilis.ws.Connection;

public class DiscussionListController extends MobilisListActivity implements
		ConnectionCallback {

	private Intent intent;
	private ParseJSON jsonParser;
	private ProgressDialog progressDialog;
	private DialogMaker dialogMaker;
	private DiscussionDAO discussionDAO;
	private PostDAO postDAO;
	private Cursor cursor;
	private DiscussionsAdapter listAdapter;
	private Connection connection;
	private MobilisStatus appState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.discussion);
		appState = MobilisStatus.getInstance();
		connection = new Connection(this);
		jsonParser = new ParseJSON(this);
		postDAO = new PostDAO(this);
		discussionDAO = new DiscussionDAO(this);
		dialogMaker = new DialogMaker(this);
		restoreDialog();
		updateList();
	}

	@SuppressWarnings("deprecation")
	public void restoreDialog() {
		if (getLastNonConfigurationInstance() != null) {
			progressDialog = (ProgressDialog) getLastNonConfigurationInstance();
			progressDialog.show();
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		Log.i("OnRetain", "True");
		if (progressDialog != null) {
			if (progressDialog.isShowing()) {
				Log.i("OnRetain2", "True");
				closeDialog(progressDialog);
				return progressDialog;
			}
		}
		return null;
	}

	public void updateList() {

		discussionDAO.open();
		cursor = discussionDAO.getDiscussionsFromClass(appState.selectedClass);
		discussionDAO.close();
		listAdapter = new DiscussionsAdapter(this, cursor);
		setListAdapter(listAdapter);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		Object content = l.getAdapter().getItem(position);
		ContentValues item = (ContentValues) content;

		int discussionId = item.getAsInteger("_id");

		if (item.getAsString("status").equals("0")
				|| item.getAsString("status").equals("2")) {
			appState.forumClosed = true;

		} else {
			appState.forumClosed = false;
		}

		appState.selectedDiscussion = discussionId;

		postDAO.open();
		discussionDAO.open();

		if (postDAO.postExistsOnTopic(discussionId)) {
			postDAO.close();
			discussionDAO.close();
			intent = new Intent(this, ExtMobilisTTSActivity.class);
			closeDialog(progressDialog);
			startActivityForResult(intent, 0);

		} else {
			Log.w("Não Existem posts no banco", " ");
			postDAO.close();
			discussionDAO.close();
			progressDialog = dialogMaker
					.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
			progressDialog.show();
			obtainNewPosts(Constants.generateNewPostsTTSURL(discussionId,
					Constants.oldDateString));
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

	public class DiscussionsAdapter extends CursorAdapter {

		LayoutInflater inflater;

		@SuppressWarnings("deprecation")
		public DiscussionsAdapter(Context context, Cursor c) {
			super(context, c);
			inflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View convertView, Context context, Cursor cursor) {

			if (cursor.getString(cursor.getColumnIndex("status")).equals("0")
					|| cursor.getString(cursor.getColumnIndex("status"))
							.equals("2")) {

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
			}

			if (cursor.getInt(cursor.getColumnIndex("has_new_posts")) == 1
					&& cursor.getString(cursor.getColumnIndex("status"))
							.equals("1")) {

				LinearLayout leftBar = (LinearLayout) convertView
						.findViewById(R.id.left_bar);

				Log.i("HASNEWPOSTS", "TRUE");

				leftBar.setBackgroundColor(Color.YELLOW);
			}

			TextView topicTitle = (TextView) convertView
					.findViewById(R.id.topic_name);

			topicTitle.setText(cursor.getString(cursor.getColumnIndex("name")));
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return inflater.inflate(R.layout.discussion_item, parent, false);
		}

		@Override
		public Object getItem(int position) {
			ContentValues item = new ContentValues();
			item.put("status",
					getCursor().getString(getCursor().getColumnIndex("status")));
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

	@Override
	public void menuRefreshItemSelected() {
		int currentClass = appState.selectedClass;
		progressDialog = dialogMaker
				.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
		progressDialog.show();
		obtainTopics(Constants.URL_GROUPS_PREFIX + currentClass
				+ Constants.URL_DISCUSSION_SUFFIX);
	}

	@Override
	public void resultFromConnection(int connectionId, String result,
			int statusCode) {
		if (statusCode != 200 && statusCode != 201) {

			closeDialog(progressDialog);
			ErrorHandler.handleStatusCode(this, statusCode);

		} else {
			switch (connectionId) {

			case Constants.CONNECTION_GET_NEW_POSTS:
				closeDialog(progressDialog);

				int[] beforeAfter = new int[2];
				final int beforeIndex = 0;
				final int afterIndex = 1;

				beforeAfter = jsonParser.parseBeforeAndAfter(result);

				Log.i("PostsBefore", "" + beforeAfter[beforeIndex]);
				Log.i("PostsAfter", "" + beforeAfter[afterIndex]);

				discussionDAO.open();
				discussionDAO.setNextPosts(appState.selectedDiscussion,
						beforeAfter[afterIndex]);
				discussionDAO.setPreviousPosts(appState.selectedDiscussion,
						beforeAfter[beforeIndex]);
				discussionDAO.close();

				ArrayList<DiscussionPost> loadedPosts = jsonParser
						.parsePostsTTS(result);

				postDAO.open();
				postDAO.insertPostsToDB(loadedPosts,
						appState.selectedDiscussion);
				postDAO.close();

				intent = new Intent(getApplicationContext(),
						ExtMobilisTTSActivity.class);

				ArrayList<Integer> ids = null;
				postDAO.open();
				ids = postDAO
						.getIdsOfPostsWithoutImage(appState.selectedDiscussion);
				postDAO.close();

				closeDialog(progressDialog);
				Log.i("USER IDS", "" + ids.size());
				postDAO.close();
				MobilisStatus status = MobilisStatus.getInstance();
				status.ids = ids;
				startActivityForResult(intent, 0);
				break;

			case Constants.CONNECTION_GET_TOPICS:
				ContentValues[] values = jsonParser.parseJSON(result,
						Constants.PARSE_TOPICS_ID);
				discussionDAO.open();
				for (int i = 0; i < values.length; i++) {
					if (discussionDAO.hasNewPosts(
							values[i].getAsInteger("_id"),
							values[i].getAsString("last_post_date"))) {
						Log.i("TAG", "Existem posts novos");
						values[i].put("has_new_posts", true);
					} else {
						Log.v("TAG", "Não há posts novos");
					}
				}

				discussionDAO.addDiscussions(values, appState.selectedClass);
				discussionDAO.close();
				updateList();
				closeDialog(progressDialog);
				break;

			default:
				break;
			}
		}
	}
}
