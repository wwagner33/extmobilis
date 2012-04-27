package com.mobilis.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Dialog;
import android.app.ListActivity;
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
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilis.dao.PostDAO;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.exception.ImageFileNotFoundException;
import com.mobilis.util.Constants;
import com.mobilis.util.DateUtils;
import com.mobilis.util.ParseJSON;
import com.mobilis.util.ZipManager;
import com.mobilis.ws.Connection;

public class PostList extends ListActivity implements OnClickListener,
		OnScrollListener {

	private boolean forceListToRedraw = true;
	private static final long noParentId = 0;
	private PostAdapter listAdapter;
	private ArrayList<ContentValues> parsedValues;
	private ParseJSON jsonParser;
	private TextView textName;
	private int currentDay, currentMonth, currentYear;
	private Intent intent;
	private ImageView answerForum;
	public SharedPreferences settings;
	private Dialog dialog;

	private String prefix;

	private boolean stopLoadingMore = false;
	private boolean loadingMore = false;
	private String oldestPostDate = Constants.oldDateString;
	private View footerView;
	private DialogMaker dialogMaker;
	private PostDAO postDAO;
	private Cursor cursor;

	private PostHandler handler;
	private Connection connection;

	private ZipManager zipManager;

	private boolean newPosts = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);

		handler = new PostHandler();
		connection = new Connection(handler, this);
		zipManager = new ZipManager();

		dialogMaker = new DialogMaker(this);
		postDAO = new PostDAO(this);
		jsonParser = new ParseJSON(this);
		settings = PreferenceManager.getDefaultSharedPreferences(this);

		answerForum = (ImageView) findViewById(R.id.answer_topic_image);
		answerForum.setOnClickListener(this);
		answerForum.setClickable(true);

		if (settings.getBoolean("isForumClosed", false) == true) {
			answerForum.setVisibility(View.GONE);
		}

		Calendar calendar = Calendar.getInstance();
		Log.w("ANO ATUAL", String.valueOf(calendar.get(Calendar.YEAR)));
		currentYear = calendar.get(Calendar.YEAR);

		Log.w("DIA ATUAL", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
		currentDay = calendar.get(Calendar.DAY_OF_MONTH);

		Log.w("MẼS Atual", String.valueOf(calendar.get(Calendar.MONTH)));
		currentMonth = calendar.get(Calendar.MONTH) + 1;

		textName = (TextView) findViewById(R.id.nome_forum);

		textName.setText(settings.getString("CurrentForumName", null));

		getListView().setOnScrollListener(this);

		postDAO.open();
		cursor = postDAO.getPostsFromTopic(settings.getInt("SelectedTopic", 0));
		postDAO.close();
		updateList(cursor);

	}

	public Bitmap getUserImage(int user_id) throws ImageFileNotFoundException {

		try {
			prefix = String.valueOf(user_id);

			File file = new File(Constants.PATH_IMAGES);

			File[] image = file.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String filename) {
					// TODO Auto-generated method stub
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
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		try {
			postDAO.close();
		} catch (NullPointerException e) {

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

		if (listValue.getAsString("content_last").equals("")) {
			intent.putExtra("content", listValue.getAsString("content_first"));
		} else {
			intent.putExtra("content", listValue.getAsString("content_first")
					+ listValue.getAsString("content_last"));
		}

		intent.putExtra("topicId", settings.getInt("SelectedTopic", 0));

		SharedPreferences.Editor editor = settings.edit();
		editor.putLong("SelectedPost", listValue.getAsInteger("_id"));
		editor.commit();

		try {
			Bitmap userImage = getUserImage(listValue.getAsInteger("user_id"));
			intent.putExtra("image", userImage);
		} catch (ImageFileNotFoundException e) {
			// A lista de detalhes vai exibir a imagem padrão
		}

		startActivity(intent);

	}

	public void refreshList() {

		forceListToRedraw = false;
		listAdapter.notifyDataSetChanged();
		loadingMore = false;
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
				// oldestPostDate = parsedValues.get(parsedValues.size() - 1)
				// .getAsString("updated");
				oldestPostDate = postDAO.getOldestPost(settings.getInt(
						"SelectedTopic", 0));
				postDAO.close();
			}

			if (parsedValues.size() == 20) {

				footerView = ((LayoutInflater) this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
						.inflate(R.layout.post_list_footer, null, false);
				this.getListView().addFooterView(footerView);
			}

			listAdapter = new PostAdapter(this, parsedValues);
			setListAdapter(listAdapter);
		}
	}

	public boolean postedToday(int post_day, int post_month, int post_year) {

		return (post_day == currentDay && post_month == currentMonth && post_year == currentYear) ? true
				: false;
	}

	public void obtainHistoryPosts(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_HISTORY_POSTS, url,
				settings.getString("token", null));

	}

	public void obtainNewPosts(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_NEW_POSTS, url,
				settings.getString("token", null));

	}

	public void getImages(String url) {

		connection.getImages(Constants.CONNECTION_GET_IMAGES, url,
				settings.getString("token", null));

	}

	public class PostAdapter extends BaseAdapter {

		Context context;
		ArrayList<ContentValues> data;
		LayoutInflater inflater = null;

		// static ImageView image;

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
							"updated"));
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
				/*
				 * if (postDAO.postedToday(
				 * data.get(position).getAsInteger("discussion_id"), data
				 * .get(position).getAsInteger("_id"))) {
				 * 
				 * Log.w("POSTED TODAY", "TRUE");
				 * postDate.setText(postDAO.getPostedTodayDateFormat(
				 * (data.get(position).getAsInteger("discussion_id")),
				 * data.get(position).getAsInteger("_id"))); postDAO.close();
				 * 
				 * }
				 * 
				 * else {
				 * 
				 * postDate.setText(postDAO.getPostedBeforeTodayDateFormat(
				 * data.get(position).getAsInteger("discussion_id"),
				 * data.get(position).getAsInteger("_id"))); postDAO.close(); }
				 */
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
				postBody.setText(data.get(position)
						.getAsString("content_first"));

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
			intent.putExtra("topicId", "");
			intent.putExtra("parentId", noParentId);
			startActivity(intent);

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

			dialog = dialogMaker
					.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
			dialog.show();
			String url = "discussions/" + settings.getInt("SelectedTopic", 0)
					+ "/posts/" + Constants.oldDateString + "/news.json";

			obtainNewPosts(url);

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

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		int lastInScreen = firstVisibleItem + visibleItemCount;
		if ((lastInScreen == totalItemCount) && !(loadingMore)
				&& getListView().getCount() >= 20 && !stopLoadingMore) {
			loadingMore = true;

			String url = "discussions/" + settings.getInt("SelectedTopic", 0)
					+ "/posts/" + oldestPostDate + "/history.json";
			Log.w("OLD-POSTS-URL", url);
			obtainHistoryPosts(url);

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

				newPosts = true;

				if (msg.getData().getString("content").length() > 2) {

					postDAO.open();
					postDAO.addPosts(
							jsonParser.parsePosts(msg.getData().getString(
									"content")),
							settings.getInt("SelectedTopic", 0));

					cursor = postDAO.getPostsFromTopic(settings.getInt(
							"SelectedTopic", 0));
					oldestPostDate = postDAO.getOldestPost(settings.getInt(
							"SelectedTopic", 0));

					try {
						String ids = postDAO.getUserIdsAbsentImage(settings
								.getInt("SelectedTopic", 0));
						postDAO.close();
						getImages("images/" + ids + "/users");
						Log.i("Alguns usuários não possuem imagens", "TRUE");
					} catch (StringIndexOutOfBoundsException e) {
						closeDialogIfItsVisible();
						postDAO.close();
						loadingMore = false;
						stopLoadingMore = false;
						updateList(cursor);
						Log.i("Não é preciso Baixar novas imagens", "TRUE");
					} catch (NullPointerException e) {
						Log.i("É preciso baixar todas as imagens", "TRUE");
						String ids = postDAO.getAllUserIds();
						postDAO.close();
						getImages("images/" + ids + "/users");
					}

				} else {
					closeDialogIfItsVisible();
					Toast.makeText(getApplicationContext(),
							"Não existem posts novos", Toast.LENGTH_SHORT)
							.show();
				}
			}

			if (msg.what == Constants.MESSAGE_HISTORY_POST_CONNECTION_OK) {

				ArrayList<ContentValues> temp = jsonParser.parsePosts(msg
						.getData().getString("content"));
				// oldestPostDate = temp.get(temp.size() - 1).getAsString(
				// "updated");
				try {
					oldestPostDate = DateUtils.convertDateToServerFormat(temp
							.get(temp.size() - 1).getAsString("updated"));
				} catch (ParseException e1) {
					e1.printStackTrace();
				}

				parsedValues.addAll(temp);

				newPosts = false;

				if (parsedValues.size() % 20 != 0) {
					// Se vier menos de 20 posts do servidor eles são os
					// ultimos.
					getListView().removeFooterView(footerView);
					stopLoadingMore = true;
					loadingMore = true;
				}

				try {
					postDAO.open();
					String ids = postDAO.getUserIdsAbsentImage(settings.getInt(
							"SelectedTopic", 0));
					postDAO.close();
					getImages("images/" + ids + "/users");
					Log.i("Alguns usuários não possuem imagens", "TRUE");
				} catch (StringIndexOutOfBoundsException e) {
					closeDialogIfItsVisible();
					postDAO.close();
					loadingMore = false;
					closeDialogIfItsVisible();
					refreshList();
					Log.i("Não é preciso Baixar novas imagens", "TRUE");
				} catch (NullPointerException e) {
					Log.i("É preciso baixar todas as imagens", "TRUE");
					String ids = postDAO.getAllUserIds();
					postDAO.close();
					getImages("images/" + ids + "/users");
				}

			}

			if (msg.what == Constants.MESSAGE_IMAGE_CONNECTION_OK) {
				zipManager.unzipFile();
				loadingMore = false;
				stopLoadingMore = false;
				closeDialogIfItsVisible();

				if (newPosts)
					updateList(cursor);
				else
					refreshList();

			}
			if (msg.what == Constants.MESSAGE_IMAGE_CONNECION_FAILED) {
				loadingMore = false;
				stopLoadingMore = false;
				closeDialogIfItsVisible();

				if (newPosts)
					updateList(cursor);
				else
					refreshList();
			}
		}
	}
}
