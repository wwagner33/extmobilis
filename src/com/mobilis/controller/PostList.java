package com.mobilis.controller;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
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

import com.mobilis.dialog.DialogMaker;
import com.mobilis.model.DBAdapter;
import com.mobilis.model.PostDAO;
import com.mobilis.threads.RequestHistoryPostsThread;
import com.mobilis.threads.RequestImageThread;
import com.mobilis.threads.RequestNewPostsThread;
import com.mobilis.threads.RequestPostsThread;
import com.mobilis.util.Time;

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
	private RequestPosts requestPosts;
	private DBAdapter adapter;
	private RequestImage requestImage;
	private RequestNewPosts requestNewPosts;
	private RequestHistoryPosts requestHistoryPosts;
	private boolean stopLoadingMore = false;
	private boolean loadingMore = false;
	private String oldestPostDate;
	private View footerView;
	private DialogMaker dialogMaker;
	private PostDAO postDAO;
	private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		dialogMaker = new DialogMaker(this);

		postDAO = new PostDAO(this);
		jsonParser = new ParseJSON(this);

		setContentView(R.layout.post);
		settings = PreferenceManager.getDefaultSharedPreferences(this);

		adapter = new DBAdapter(this);

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

		/*
		 * adapter.open();
		 * updateList(adapter.getPostsFromTopic(Long.valueOf(settings.getString(
		 * "SelectedTopic", null)))); adapter.close();
		 */
		postDAO.open();
		cursor = postDAO.getPostsFromTopic(Integer.parseInt(settings.getString(
				"SelectedTopic", null)));
		updateList(cursor);
		postDAO.close();

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

		intent.putExtra("topicId", settings.getString("SelectedTopic", null));

		SharedPreferences.Editor editor = settings.edit();
		editor.putLong("SelectedPost", listValue.getAsInteger("_id"));
		editor.commit();

		intent.putExtra("parentId", listValue.getAsLong("id"));
		startActivity(intent);

	}

	public void updateList(String source) {

		jsonParser = new ParseJSON(this);
		parsedValues = jsonParser.parsePosts(source);

		if (parsedValues.size() > 1) {
			oldestPostDate = parsedValues.get(parsedValues.size() - 1)
					.getAsString("updated");
		}

		if (parsedValues.size() == 20) {

			footerView = ((LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.post_list_footer, null, false);
			this.getListView().addFooterView(footerView);
		}

		/*
		 * listAdapter = new PostAdapter(this, parsedValues);
		 * setListAdapter(listAdapter);
		 */

	}

	public void updateList(Cursor cursor) {

		postDAO.open();
		parsedValues = postDAO.cursorToContentValues(cursor);
		postDAO.close();

		if (parsedValues.size() > 0) {
			oldestPostDate = parsedValues.get(parsedValues.size() - 1)
					.getAsString("updated");
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

	public boolean postedToday(int post_day, int post_month, int post_year) {

		return (post_day == currentDay && post_month == currentMonth && post_year == currentYear) ? true
				: false;
	}

	public void obtainPosts(String URLString) {
		requestPosts = new RequestPosts(this);
		adapter.open();
		requestPosts.setConnectionParameters(URLString, adapter.getToken());
		adapter.close();
		requestPosts.execute();
	}

	public void obtainNewPosts(String url) {
		requestNewPosts = new RequestNewPosts(this);
		adapter.open();
		requestNewPosts.setConnectionParameters(url, adapter.getToken());
		adapter.close();
		requestNewPosts.execute();
	}

	public void getImageFormServer() {
		Log.w("InsidePullImage", "TRUE");
		requestImage = new RequestImage(this);
		adapter.open();
		requestImage.setConnectionParameters("images/1/users",
				adapter.getToken());
		adapter.close();
		requestImage.execute();

	}

	public class RequestImage extends RequestImageThread {

		public RequestImage(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onRequestImageConnectionFailed() {

		}

		@Override
		public void onRequestImageConnectionSucceded(String result) {
			Log.w("RESULT_OK", "TRUE");

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
			updateList(result);
			closeDialogIfItsVisible();
		}

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

			// adapter.open();
			// adapter.updatePostsFromTopic(result,
			// Long.parseLong(settings.getString("SelectedTopic", null)));
			// adapter.close();

			postDAO.open();
			postDAO.addPosts(jsonParser.parsePosts(result),
					Integer.parseInt(settings.getString("SelectedTopic", null)));

			Cursor cursor = postDAO.getPostsFromTopic(Integer.parseInt(settings
					.getString("SelectedTopic", null)));

			updateList(cursor);
			postDAO.close();

			loadingMore = false;
			stopLoadingMore = false;
			closeDialogIfItsVisible();
		}
	}

	public class RequestHistoryPosts extends RequestHistoryPostsThread {

		public RequestHistoryPosts(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onRequestHistoryPostsConnectionFailed() {
			// edit footer maybe
		}

		@Override
		public void onRequestHistoryPostsConnectionSucceded(String result) {
			ArrayList<ContentValues> temp = jsonParser.parsePosts(result);
			parsedValues.addAll(temp);
			if (parsedValues.size() % 20 != 0) {

				getListView().removeFooterView(footerView);
				stopLoadingMore = true;
				loadingMore = true;
			}
			forceListToRedraw = false;
			listAdapter.notifyDataSetChanged();
			loadingMore = false;
			forceListToRedraw = true;
		}
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

				if (postedToday(data.get(position).getAsInteger("post_day"),
						data.get(position).getAsInteger("post_month"), data
								.get(position).getAsInteger("post_year")))

				{
					Log.w("POSTED TODAY", "TRUE");
					postDate.setText(data.get(position).getAsInteger(
							"post_hour")
							+ ":"
							+ data.get(position).getAsInteger("post_minute"));

				} else {

					postDate.setText(data.get(position)
							.getAsInteger("post_day")
							+ " "
							+ Time.getMonthAsText(data.get(position)
									.getAsInteger("post_month")));
					Log.w("POSTED TODAY", "FALSE");
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
			String url = "discussions/"
					+ settings.getString("SelectedTopic", null) + "/posts/"
					+ Constants.oldDateString + "/news.json";

			obtainNewPosts(url);

		}

		if (item.getItemId() == R.id.menu_logout) {
			adapter.open();
			adapter.updateToken(null);
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

			requestHistoryPosts = new RequestHistoryPosts(this);
			adapter.open();

			String url = "/discussions/"
					+ settings.getString("SelectedTopic", null) + "/posts/"
					+ oldestPostDate + "/history.json";
			requestHistoryPosts
					.setConnectionParameters(url, adapter.getToken());
			adapter.close();
			requestHistoryPosts.execute();

		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// Nothing here
	}

}
