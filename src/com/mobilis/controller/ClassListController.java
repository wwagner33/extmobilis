package com.mobilis.controller;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilis.dao.ClassDAO;
import com.mobilis.dao.DiscussionDAO;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.interfaces.MobilisListActivity;
import com.mobilis.util.Constants;
import com.mobilis.util.ParseJSON;
import com.mobilis.ws.Connection;

public class ClassListController extends MobilisListActivity {

	private ParseJSON jsonParser;
	private ProgressDialog dialog;
	private Intent intent;

	private DialogMaker dialogMaker;
	private ClassDAO classDAO;
	private Cursor cursor;
	private ClassAdapter listAdapter;
	private DiscussionDAO topicDAO;
	private ClassHandler handler;
	private Connection connection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.curriculum_units);
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

	public void updateList() {

		classDAO.open();
		cursor = classDAO.getClasses(getPreferences().getInt("SelectedCourse",
				0));
		classDAO.close();
		listAdapter = new ClassAdapter(this, cursor);
		setListAdapter(listAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		int classId = (Integer) l.getAdapter().getItem(position);

		Log.i("SelectedClass", String.valueOf(classId));

		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putInt("SelectedClass", classId);
		commit(editor);

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

	private class ClassAdapter extends CursorAdapter {

		LayoutInflater inflater;

		@SuppressWarnings("deprecation")
		public ClassAdapter(Context context, Cursor c) {
			super(context, c);
			inflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View convertView, Context context, Cursor cursor) {
			if (cursor != null) {

				TextView courseName = (TextView) convertView
						.findViewById(R.id.turmas_item);
				courseName.setText(cursor.getString(cursor
						.getColumnIndex("code")));
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return inflater.inflate(R.layout.curriculum_units_item, parent,
					false);
		}

		@Override
		public Object getItem(int position) {
			return getCursor().getInt(getCursor().getColumnIndex("_id"));
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
				// classDAO.addClasses(values,
				// getPreferences().getInt("SelectedClass", 0));
				classDAO.addClasses(values,
						getPreferences().getInt("SelectedCourse", 0));
				classDAO.close();
				listAdapter.notifyDataSetChanged();
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

					topicDAO.addDiscussions(values,
							getPreferences().getInt("SelectedClass", 0));
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
		int selectedCourse = getPreferences().getInt("SelectedCourse", 0);
		dialog = dialogMaker
				.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
		dialog.show();
		obtainClasses(Constants.URL_CURRICULUM_UNITS_PREFIX + selectedCourse
				+ Constants.URL_GROUPS_SUFFIX);

	}
}
