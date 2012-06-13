package com.mobilis.controller;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.mobilis.dao.ClassDAO;
import com.mobilis.dao.DiscussionDAO;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.interfaces.MobilisListActivity;
import com.mobilis.util.Constants;
import com.mobilis.util.MobilisStatus;
import com.mobilis.util.ParseJSON;
import com.mobilis.ws.Connection;

public class ClassListController extends MobilisListActivity {

	private ParseJSON jsonParser;
	private ProgressDialog dialog;
	private Intent intent;
	private DialogMaker dialogMaker;
	private ClassDAO classDAO;
	private Cursor cursor;
	private DiscussionDAO topicDAO;
	private ClassHandler handler;
	private Connection connection;
	private MobilisStatus appState;
	private SimpleCursorAdapter simpleAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.curriculum_units);
		appState = MobilisStatus.getInstance();
		handler = new ClassHandler();
		connection = new Connection(handler);
		jsonParser = new ParseJSON(this);
		classDAO = new ClassDAO(this);
		topicDAO = new DiscussionDAO(this);
		dialogMaker = new DialogMaker(this);
		restoreDialog();
		updateList();
	}

	@SuppressWarnings("deprecation")
	public void restoreDialog() {
		if (getLastNonConfigurationInstance() != null) {
			dialog = (ProgressDialog) getLastNonConfigurationInstance();
			dialog.show();
		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		if (dialog != null) {
			if (dialog.isShowing()) {
				closeDialog(dialog);
				return dialog;
			}
		}
		return null;
	}

	public void obtainTopics(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_TOPICS, url,
				getPreferences().getString("token", null));
	}

	public void obtainClasses(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_CLASSES, url,
				getPreferences().getString("token", null));

	}

	@SuppressWarnings("deprecation")
	public void updateList() {

		classDAO.open();
		cursor = classDAO.getClasses(appState.selectedCourse);
		classDAO.close();
		simpleAdapter = new SimpleCursorAdapter(this,
				R.layout.curriculum_units_item, cursor,
				new String[] { "code" }, new int[] { R.id.turmas_item });
		setListAdapter(simpleAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		Cursor itemCursor = (Cursor) l.getAdapter().getItem(position);
		int classId = itemCursor.getInt(itemCursor.getColumnIndex("_id"));

		appState.selectedClass = classId;

		topicDAO.open();

		if (topicDAO.existsDiscussion(classId)) {
			topicDAO.close();
			intent = new Intent(this, DiscussionListController.class);
			startActivity(intent);

		}

		else {
			topicDAO.close();
			dialog = dialogMaker
					.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
			dialog.show();
			obtainTopics(Constants.URL_GROUPS_PREFIX + classId
					+ Constants.URL_DISCUSSION_SUFFIX);
		}
	}

	private class ClassHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == Constants.MESSAGE_CONNECTION_FAILED) {
				closeDialog(dialog);
			}

			if (msg.what == Constants.MESSAGE_CLASS_CONNECTION_OK) {

				ContentValues[] values = jsonParser.parseJSON(msg.getData()
						.getString("content"), Constants.PARSE_CLASSES_ID);
				classDAO.open();
				classDAO.addClasses(values, appState.selectedCourse);
				classDAO.close();
				simpleAdapter.notifyDataSetChanged();
				closeDialog(dialog);

			}

			if (msg.what == Constants.MESSAGE_TOPIC_CONNECTION_OK) {

				Log.w("result", msg.getData().getString("content"));

				if (msg.getData().getString("content").length() <= 2) {

					Toast.makeText(getApplicationContext(), "Fórum Vazio",
							Toast.LENGTH_SHORT).show();
					closeDialog(dialog);
				}

				else {

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

					topicDAO.addDiscussions(values, appState.selectedClass);
					topicDAO.close();

					intent = new Intent(getApplicationContext(),
							DiscussionListController.class);
					closeDialog(dialog);
					startActivity(intent);

				}
			}
		}
	}

	@Override
	public void menuRefreshItemSelected() {
		int selectedCourse = appState.selectedCourse;
		dialog = dialogMaker
				.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
		dialog.show();
		obtainClasses(Constants.URL_CURRICULUM_UNITS_PREFIX + selectedCourse
				+ Constants.URL_GROUPS_SUFFIX);

	}
}
