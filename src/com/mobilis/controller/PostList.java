package com.mobilis.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilis.dao.DiscussionDAO;
import com.mobilis.dao.PostDAO;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.exception.ImageFileNotFoundException;
import com.mobilis.interfaces.MobilisListActivity;
import com.mobilis.model.Discussion;
import com.mobilis.util.Constants;
import com.mobilis.util.DateUtils;
import com.mobilis.util.MobilisStatus;
import com.mobilis.util.ParseJSON;
import com.mobilis.ws.Connection;

public class PostList extends MobilisListActivity implements OnClickListener,
		OnScrollListener {

	private boolean forceListToRedraw = true;
	private PostAdapter listAdapter;
	private ArrayList<ContentValues> parsedValues;
	private ParseJSON jsonParser;
	private TextView textName;
	private int currentDay, currentMonth, currentYear;
	private Intent intent;
	private ImageView answerForum;
	private Dialog dialog;

	private String prefix;

	private boolean stopLoading = false;
	private boolean isLoading = false;

	private boolean loadingHasFailed = false;

	private String oldestPostDate = Constants.oldDateString;
	private PostDAO postDAO;
	private Cursor cursor;

	private PostHandler handler;
	private Connection connection;

	private View connectionFooterView;
	private View warningFooterView;

	private Button footerButton;
	private DialogMaker dialogMaker;
	private MobilisStatus appState;
	private DiscussionDAO discussionDAO;
	private Discussion discussion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);

		discussionDAO = new DiscussionDAO(this);
		discussionDAO.open();
		discussion = discussionDAO.getDiscussion(appState.selectedDiscussion);
		discussionDAO.close();

		appState = MobilisStatus.getInstance();
		dialogMaker = new DialogMaker(this);

		warningFooterView = ((LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.footer_no_connection, null, false);

		connectionFooterView = ((LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.post_list_footer, null, false);

		footerButton = (Button) warningFooterView
				.findViewById(R.id.warning_button);
		footerButton.setOnClickListener(this);

		handler = new PostHandler();
		connection = new Connection(handler);

		postDAO = new PostDAO(this);
		jsonParser = new ParseJSON(this);

		answerForum = (ImageView) findViewById(R.id.answer_topic_image);
		answerForum.setOnClickListener(this);
		answerForum.setClickable(true);

		if (appState.forumClosed == true) {
			answerForum.setVisibility(View.GONE);
		}

		MobilisStatus status = MobilisStatus.getInstance();
		if (status.ids != null) {
			ArrayList<Integer> ids = status.ids;
			status.ids = null;
			connection
					.getImages(ids, getPreferences().getString("token", null));
		}

		Calendar calendar = Calendar.getInstance();
		Log.w("ANO ATUAL", String.valueOf(calendar.get(Calendar.YEAR)));
		currentYear = calendar.get(Calendar.YEAR);

		Log.w("DIA ATUAL", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
		currentDay = calendar.get(Calendar.DAY_OF_MONTH);

		Log.w("MẼS Atual", String.valueOf(calendar.get(Calendar.MONTH)));
		currentMonth = calendar.get(Calendar.MONTH) + 1;

		textName = (TextView) findViewById(R.id.nome_forum);

		textName.setText(discussion.getName());

		getListView().setOnScrollListener(this);

		postDAO.open();
		cursor = postDAO.getPostsFromTopic(appState.selectedDiscussion);
		postDAO.close();

		restoreActivitySettings(savedInstanceState);
		restoreActivityObjects();

	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public void restoreActivityObjects() {
		if (getLastNonConfigurationInstance() != null) {
			Object restoredObjects[] = (Object[]) getLastNonConfigurationInstance();
			if (restoredObjects[0] != null) {
				dialog = (ProgressDialog) restoredObjects[0];
				dialog.show();
			}

			if (restoredObjects[1] == null) {
				updateList(cursor);
			}

			if (restoredObjects[1] != null) {
				// lembrar de atualizar o lastDate
				parsedValues = ((ArrayList<ContentValues>) restoredObjects[1]);
				updateList(parsedValues);
			}
		} else {
			updateList(cursor);
		}
	}

	public void restoreActivitySettings(Bundle bundle) {

		if (bundle != null) {
			if (bundle.getString("oldestPostDate") != null) {
				oldestPostDate = bundle.getString("oldestPostDate");
			}

			if (bundle.getBoolean("loadingHasFailed")) {
				loadingHasFailed = true;
				stopLoading = true;
				setListFooter(warningFooterView);
			}

			if (!bundle.getBoolean("loadingHasFailed")) {

				Log.i("TAG", "LOADING NOT FAILED");

				if (bundle.getBoolean("stopLoading")) {
					stopLoading = true;
					removeListFooter();
					Log.i("TAG", "REMOVE ALL FOOTERS");
				}

				else {
					setListFooter(connectionFooterView);
					Log.i("TAG", "ADD CONNECTION FOOTER");
				}
			}

		}
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		Object values[] = new Object[2];

		if (dialog != null) {
			if (dialog.isShowing()) {
				closeDialogIfItsVisible();
				values[0] = dialog;
			}
		}

		if (parsedValues != null) {
			values[1] = parsedValues;
		}

		return values;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString("oldestPostDate", oldestPostDate);
		outState.putBoolean("stopLoading", stopLoading);
		outState.putBoolean("loadingHasFailed", loadingHasFailed);

	}

	public Bitmap getUserImage(int user_id) throws ImageFileNotFoundException {

		try {
			prefix = String.valueOf(user_id);

			File file = new File(Constants.PATH_IMAGES);

			File[] image = file.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String filename) {
					return filename.startsWith(prefix);
				}
			});

			Bitmap userImage = BitmapFactory.decodeFile(image[0]
					.getAbsolutePath());
			return userImage;
		} catch (NullPointerException e) {
			throw new ImageFileNotFoundException();
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ImageFileNotFoundException();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void closeDialogIfItsVisible() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);
		ContentValues listValue = (ContentValues) l.getAdapter().getItem(
				position);
		Intent intent = new Intent(this, PostDetailController.class);
		intent.putExtra("username", listValue.getAsString("user_nick"));

		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putLong("SelectedPost", listValue.getAsInteger("_id"));
		commit(editor);

		try {
			Bitmap userImage = getUserImage(listValue.getAsInteger("user_id"));
			intent.putExtra("image", userImage);
		} catch (ImageFileNotFoundException e) {
			// A lista de detalhes vai exibir a imagem padrão
		}

		closeDialogIfItsVisible();
		startActivity(intent);
	}

	public void refreshList() {

		forceListToRedraw = false;
		listAdapter.notifyDataSetChanged();
		isLoading = false;
		forceListToRedraw = true;

	}

	public void updateList(Cursor cursor) {

		Log.i("Cursor Count", String.valueOf(cursor.getCount()));

		if (cursor.getCount() > 0) {

			postDAO.open();
			parsedValues = postDAO.cursorToContentValues(cursor);
			Log.i("ParsedValuesSize", String.valueOf(parsedValues.size()));
			postDAO.close();

			if (parsedValues.size() > 0) {
				postDAO.open();
				oldestPostDate = postDAO.getOldestPost(getPreferences().getInt(
						"SelectedTopic", 0));
				postDAO.close();
			}

			if (parsedValues.size() == 30) {
				setListFooter(connectionFooterView);
			}

			listAdapter = new PostAdapter(this, parsedValues);
			setListAdapter(listAdapter);
		}
	}

	public void updateList(ArrayList<ContentValues> values) {
		listAdapter = new PostAdapter(this, values);
		setListAdapter(listAdapter);
	}

	public void setListFooter(View footer) {
		removeListFooter();
		if (getListView().getFooterViewsCount() < 1)
			getListView().addFooterView(footer);
	}

	public void removeListFooter() {
		if (this.getListView().getFooterViewsCount() > 0) {
			if (warningFooterView != null)
				this.getListView().removeFooterView(warningFooterView);
			if (connectionFooterView != null) {
				this.getListView().removeFooterView(connectionFooterView);
			}
		}
	}

	public boolean postedToday(int post_day, int post_month, int post_year) {

		return (post_day == currentDay && post_month == currentMonth && post_year == currentYear) ? true
				: false;
	}

	public void obtainHistoryPosts(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_HISTORY_POSTS, url,
				getPreferences().getString("token", null));

	}

	public void obtainNewPosts(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_NEW_POSTS, url,
				getPreferences().getString("token", null));

	}

	public class PostAdapter extends BaseAdapter {

		Context context;
		ArrayList<ContentValues> data;
		LayoutInflater inflater = null;

		public PostAdapter(Context context, ArrayList<ContentValues> data) {
			this.context = context;
			this.data = data;
			inflater = LayoutInflater.from(context);
		}

		public void getPostImage() {

		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (forceListToRedraw == true) {

				convertView = inflater
						.inflate(R.layout.postitem, parent, false);

				TextView postDate = (TextView) convertView
						.findViewById(R.id.post_date);

				SimpleDateFormat format = DateUtils.getDbFormat();
				try {

					Date date = format.parse(data.get(position).getAsString(
							"updated_at"));
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);

					if (DateUtils.isToday(date)) {

						postDate.setText(DateUtils.getFormatedValue(calendar
								.get(Calendar.HOUR))
								+ ":"
								+ DateUtils.getFormatedValue(calendar
										.get(Calendar.MINUTE)));

					} else {

						postDate.setText(calendar.get(Calendar.DAY_OF_MONTH)
								+ " "
								+ DateUtils.getMonthAsText(calendar
										.get(Calendar.MONTH)));
					}

				} catch (ParseException e1) {
					e1.printStackTrace();
				}

				prefix = String.valueOf(data.get(position).getAsInteger(
						"user_id"));

				ImageView avatar = (ImageView) convertView
						.findViewById(R.id.avatar);

				try {
					avatar.setImageBitmap(getUserImage(data.get(position)
							.getAsInteger("user_id")));
				} catch (ImageFileNotFoundException e) {
					// Será exibido a imagem default
				}

				TextView postBody = (TextView) convertView
						.findViewById(R.id.post_body);

				// Tratar aqui se o texto for muito grande
				String content = data.get(position).getAsString("content")
						.trim();
				if (content.length() > 150) {
					postBody.setText(content.substring(0, 150) + "...");
				}

				else {
					postBody.setText(content);
				}

				TextView userName = (TextView) convertView
						.findViewById(R.id.post_title);
				userName.setText(String.valueOf(data.get(position).getAsString(
						"user_nick")));

			}
			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.answer_topic_image) {

			intent = new Intent(this, ResponseController.class);
			SharedPreferences.Editor editor = getPreferences().edit(); // mover
																		// para
																		// o
																		// singleton
			editor.putLong("SelectedPost", 0);
			commit(editor);
			closeDialogIfItsVisible();
			startActivity(intent);

		}

		if (v.getId() == R.id.warning_button) {
			Log.i("WarningButton", "Clicked");
			loadingHasFailed = false;
			stopLoading = false;
			setListFooter(connectionFooterView);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		int lastInScreen = firstVisibleItem + visibleItemCount;
		if ((lastInScreen == totalItemCount) && !(isLoading)
				&& getListView().getCount() >= 30 && !stopLoading) {
			isLoading = true;

			obtainHistoryPosts(Constants
					.generateHistoryPostURL(
							getPreferences().getInt("SelectedTopic", 0),
							oldestPostDate));
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// Nothing here
	}

	private class PostHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == Constants.MESSAGE_CONNECTION_FAILED) {
				closeDialogIfItsVisible();
			}

			if (msg.what == Constants.MESSAGE_NEW_POST_CONNECTION_OK) {

				ArrayList<ContentValues> values = jsonParser.parsePosts(msg
						.getData().getString("content"));

				Log.w("NUMERO DE POSTS", "" + values.size());

				if (values.size() == 0) {

					closeDialogIfItsVisible();
					Toast.makeText(getApplicationContext(),
							"Não existem posts novos", Toast.LENGTH_SHORT)
							.show();
				}

				else {
					postDAO.open();
					postDAO.addPosts(
							jsonParser.parsePosts(msg.getData().getString(
									"content")),
							getPreferences().getInt("SelectedTopic", 0));

					cursor = postDAO.getPostsFromTopic(getPreferences().getInt(
							"SelectedTopic", 0));
					oldestPostDate = postDAO.getOldestPost(getPreferences()
							.getInt("SelectedTopic", 0));
					postDAO.close();

					closeDialog(dialog);
					updateList(cursor);
					downloadImages();
				}
			}

			if (msg.what == Constants.MESSAGE_HISTORY_POST_CONNECTION_OK) {

				isLoading = false;

				ArrayList<ContentValues> temp = jsonParser.parsePosts(msg
						.getData().getString("content"));
				try {
					oldestPostDate = DateUtils.convertDateToServerFormat(temp
							.get(temp.size() - 1).getAsString("updated_at"));
				} catch (ParseException e1) {
					e1.printStackTrace();
				} catch (ArrayIndexOutOfBoundsException e) {
					// não há posts
				}

				parsedValues.addAll(temp);

				if (parsedValues.size() % 30 != 0) {
					removeListFooter();
					stopLoading = true;
					isLoading = false;
				}

				refreshList();
				downloadImages();

			}

			if (msg.what == Constants.MESSAGE_HISTORY_POST_CONNECTION_FAILED) {
				Log.i("HistoryPosts", "FAILED");
				loadingHasFailed = true;
				isLoading = false;
				stopLoading = true;
				setListFooter(warningFooterView);
			}

			if (msg.what == Constants.MESSAGE_IMAGE_CONNECTION_OK) {
				Log.w("IMAGE CONNECTION", "OK");
				refreshList();

			}
			if (msg.what == Constants.MESSAGE_IMAGE_CONNECION_FAILED) {
				Log.w("IMAGE CONNECTION", "FAILED");
				// NADA
			}
		}
	}

	public void downloadImages() {

		ArrayList<Integer> ids = null;
		File imageDirectory = new File(Constants.PATH_IMAGES);
		int numberOfImages = imageDirectory.listFiles().length;

		postDAO.open();

		if (imageDirectory.exists()) {
			if (numberOfImages > 0) {
				Log.i("BAIXAR ALGUMAS", "SIM");
				ids = postDAO.getUserIdsAbsentImage(getPreferences().getInt(
						"SelectedTopic", 0));
			} else {
				Log.i("BAIXAR TUDO 1", "SIM");
				ids = postDAO.getAllUserIds();
			}
		}

		else {
			Log.i("BAIXAR TUDO 2", "SIM");
			imageDirectory.mkdir();
			ids = postDAO.getAllUserIds();
		}

		if (ids.size() > 0) {
			connection
					.getImages(ids, getPreferences().getString("token", null));
		}
		postDAO.close();

	}

	@Override
	public void menuRefreshItemSelected() {
		dialog = dialogMaker
				.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
		dialog.show();
		obtainNewPosts(Constants.generateNewPostsURL(getPreferences().getInt(
				"SelectedTopic", 0)));

	}
}
