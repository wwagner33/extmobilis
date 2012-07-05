package com.mobilis.controller;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mobilis.dao.ClassDAO;
import com.mobilis.dao.DatabaseHelper;
import com.mobilis.dao.DiscussionDAO;
import com.mobilis.dao.PostDAO;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.interfaces.ConnectionCallback;
import com.mobilis.interfaces.MobilisMenuListActivity;
import com.mobilis.model.Class;
import com.mobilis.model.Discussion;
import com.mobilis.util.Constants;
import com.mobilis.util.ErrorHandler;
import com.mobilis.util.MobilisPreferences;
import com.mobilis.util.ParseJSON;
import com.mobilis.ws.Connection;

public class ClassListController extends MobilisMenuListActivity implements
		ConnectionCallback, OnItemClickListener {

	private ParseJSON jsonParser;
	private ProgressDialog dialog;
	private Intent intent;
	private DialogMaker dialogMaker;
	private ClassDAO classDAO;
	private Cursor cursor;
	private DiscussionDAO topicDAO;
	private PostDAO postDAO;
	private Connection connection;
	private MobilisPreferences appState;
	private SimpleCursorAdapter simpleAdapter;
	private DatabaseHelper helper = null;
	private ListView list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.curriculum_units);
		helper = getHelper();
		appState = MobilisPreferences.getInstance(this);
		connection = new Connection(this);
		jsonParser = new ParseJSON();
		classDAO = new ClassDAO(helper);
		topicDAO = new DiscussionDAO(helper);
		postDAO = new PostDAO(helper);
		dialogMaker = new DialogMaker(this);
		list = (ListView) findViewById(R.id.list);
		list.setOnItemClickListener(this);

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
		if (getLastCustomNonConfigurationInstance() != null) {
			dialog = (ProgressDialog) getLastCustomNonConfigurationInstance();
			dialog.show();
		}
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
				return dialog;
			}
		}
		return null;
	}

	public void obtainTopics(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_TOPICS, url,
				appState.getToken());
	}

	public void obtainClasses(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_CLASSES, url,
				appState.getToken());

	}

	public void updateList() {
		cursor = classDAO.getClassesAsCursor(appState.selectedCourse);
		simpleAdapter = new SimpleCursorAdapter(this,
				R.layout.curriculum_units_item, cursor,
				new String[] { "code" }, new int[] { R.id.turmas_item });
		list.setAdapter(simpleAdapter);
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

	@Override
	@SuppressWarnings("unchecked")
	public void resultFromConnection(int connectionId, String result,
			int statusCode) {
		if (statusCode != 200 && statusCode != 201) {
			dialog.dismiss();
			ErrorHandler.handleStatusCode(this, statusCode);

		} else {

			switch (connectionId) {
			case Constants.CONNECTION_GET_CLASSES:

				ArrayList<Class> classValues = (ArrayList<Class>) jsonParser
						.parseJSON(result, Constants.PARSE_CLASSES_ID);

				classDAO.addClass(
						classValues.toArray(new Class[classValues.size()]),
						appState.selectedCourse);

				simpleAdapter.notifyDataSetChanged();
				dialog.dismiss();
				break;

			case Constants.CONNECTION_GET_TOPICS:

				Log.w("result", result);

				if (result.length() <= 2) {

					Toast.makeText(getApplicationContext(), "Fórum Vazio",
							Toast.LENGTH_SHORT).show();
					dialog.dismiss();
				}

				else {

					ArrayList<Discussion> discussionValues = (ArrayList<Discussion>) jsonParser
							.parseJSON(result, Constants.PARSE_TOPICS_ID);

					for (int i = 0; i < discussionValues.size(); i++) {
						if (postDAO.hasNewPosts(
								discussionValues.get(i).getId(),
								discussionValues.get(i).getLastPostDate())) {
							// Existem novos posts
							discussionValues.get(i).setHasNewPosts(true);
						}
					}
					topicDAO.addDiscussions(discussionValues
							.toArray(new Discussion[discussionValues.size()]),
							appState.selectedClass);

					intent = new Intent(getApplicationContext(),
							DiscussionListController.class);
					dialog.dismiss();
					startActivity(intent);

				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		Cursor itemCursor = (Cursor) list.getAdapter().getItem(position);
		int classId = itemCursor.getInt(itemCursor.getColumnIndex("_id"));

		appState.selectedClass = classId;

		if (topicDAO.existsDiscussionOnClass(classId)) {
			intent = new Intent(this, DiscussionListController.class);
			startActivity(intent);

		}

		else {
			dialog = dialogMaker
					.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
			dialog.show();
			obtainTopics(Constants.URL_GROUPS_PREFIX + classId
					+ Constants.URL_DISCUSSION_SUFFIX);
		}
	}
}
