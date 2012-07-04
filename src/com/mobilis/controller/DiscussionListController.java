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

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mobilis.dao.DatabaseHelper;
import com.mobilis.dao.DiscussionDAO;
import com.mobilis.dao.PostDAO;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.interfaces.ConnectionCallback;
import com.mobilis.interfaces.MobilisMenuListActivity;
import com.mobilis.model.Discussion;
import com.mobilis.model.Post;
import com.mobilis.util.Constants;
import com.mobilis.util.ErrorHandler;
import com.mobilis.util.MobilisPreferences;
import com.mobilis.util.ParseJSON;
import com.mobilis.ws.Connection;

public class DiscussionListController extends MobilisMenuListActivity implements
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
	private MobilisPreferences appState;
	private DatabaseHelper helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.discussion);
		helper = getHelper();
		appState = MobilisPreferences.getInstance(this);
		connection = new Connection(this);
		jsonParser = new ParseJSON();
		postDAO = new PostDAO(helper);
		discussionDAO = new DiscussionDAO(helper);
		dialogMaker = new DialogMaker(this);
		progressDialog = dialogMaker
				.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
		restoreDialog();
		updateList();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (helper != null) {
			OpenHelperManager.releaseHelper();
			helper = null;
		}
	}

	private DatabaseHelper getHelper() {
		if (helper == null) {
			helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		return helper;
	}

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
				progressDialog.dismiss();
				return progressDialog;
			}
		}
		return null;
	}

	public void updateList() {

		cursor = discussionDAO
				.getDiscussionsFromClassAsCursor(appState.selectedClass);
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

		if (postDAO.postsExistOnDiscusison(discussionId)) {
			intent = new Intent(this, ExtMobilisTTSActivity.class);
			progressDialog.dismiss();
			startActivityForResult(intent, 0);

		} else {
			Log.w("Não Existem posts no banco", " ");
			progressDialog.show();
			obtainNewPosts(Constants.generateNewPostsTTSURL(discussionId,
					Constants.oldDateString));
		}
	}

	public void obtainNewPosts(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_NEW_POSTS, url,
				appState.getToken());
	}

	public void obtainTopics(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_TOPICS, url,
				appState.getToken());

	}

	public class DiscussionsAdapter extends CursorAdapter {

		LayoutInflater inflater;

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
	@SuppressWarnings("unchecked")
	public void resultFromConnection(int connectionId, String result,
			int statusCode) {
		if (statusCode != 200 && statusCode != 201) {

			progressDialog.dismiss();
			ErrorHandler.handleStatusCode(this, statusCode);

		} else {
			switch (connectionId) {

			case Constants.CONNECTION_GET_NEW_POSTS:
				progressDialog.dismiss();

				int[] beforeAfter = new int[2];
				final int beforeIndex = 0;
				final int afterIndex = 1;

				beforeAfter = jsonParser.parseBeforeAndAfter(result);

				Discussion currentDiscussion = discussionDAO
						.getDiscussion(appState.selectedDiscussion);
				currentDiscussion.setNextPosts(beforeAfter[afterIndex]);
				currentDiscussion.setPreviousPosts(beforeAfter[beforeIndex]);
				discussionDAO.updateDiscussion(currentDiscussion);

				ArrayList<Post> loadedPosts = (ArrayList<Post>) jsonParser
						.parseJSON(result, Constants.PARSE_POSTS_ID);

				postDAO.insertPosts(
						loadedPosts.toArray(new Post[loadedPosts.size()]),
						appState.selectedDiscussion);

				intent = new Intent(getApplicationContext(),
						ExtMobilisTTSActivity.class);

				ArrayList<Integer> ids = null;
				ids = postDAO
						.getIdsOfPostsWithoutImage(appState.selectedDiscussion);

				progressDialog.dismiss();
				Log.i("USER IDS", "" + ids.size());
				MobilisPreferences status = MobilisPreferences
						.getInstance(this);
				status.ids = ids;
				startActivityForResult(intent, 0);
				break;

			case Constants.CONNECTION_GET_TOPICS:

				ArrayList<Discussion> values = (ArrayList<Discussion>) jsonParser
						.parseJSON(result, Constants.PARSE_TOPICS_ID);

				for (int i = 0; i < values.size(); i++) {
					if (postDAO.hasNewPosts(values.get(i).getId(), values
							.get(i).getLastPostDate())) {
						Log.i("TAG", "Existem posts novos");

						values.get(i).setHasNewPosts(true);

					} else {
						Log.v("TAG", "Não há posts novos");
					}
				}

				discussionDAO.addDiscussions(
						values.toArray(new Discussion[values.size()]),
						appState.selectedClass);
				updateList();
				progressDialog.dismiss();
				break;

			default:
				break;
			}
		}
	}
}
