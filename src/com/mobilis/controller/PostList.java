package com.mobilis.controller;

import java.util.Calendar;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilis.model.DBAdapter;
import com.mobilis.threads.RequestImageThread;
import com.mobilis.threads.RequestNewPostsThread;
import com.mobilis.threads.RequestPostsThread;

public class PostList extends ListActivity implements OnClickListener {

	// http://apolo11teste.virtual.ufc.br/ws_solar/images/7/users

	private static final long noParentId = 0;
	private PostAdapter listAdapter;
	private String forumName;
	private ContentValues parsedValues[];
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);

		settings = PreferenceManager.getDefaultSharedPreferences(this);

		adapter = new DBAdapter(this);

		answerForum = (ImageView) findViewById(R.id.answer_topic_image);
		answerForum.setOnClickListener(this);
		answerForum.setClickable(true);

		Calendar calendar = Calendar.getInstance();

		Log.w("ANO ATUAL", String.valueOf(calendar.get(Calendar.YEAR)));
		currentYear = calendar.get(Calendar.YEAR);

		Log.w("DIA ATUAL", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
		currentDay = calendar.get(Calendar.DAY_OF_MONTH);

		Log.w("MẼS Atual", String.valueOf(calendar.get(Calendar.MONTH)));
		currentMonth = calendar.get(Calendar.MONTH) + 1;

		textName = (TextView) findViewById(R.id.nome_forum);

		textName.setText(settings.getString("CurrentForumName", null));

		//	getImageFormServer();
		
		 adapter.open();
		 updateList(adapter.getPosts());
		 adapter.close();
		 

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
		intent.putExtra("username", listValue.getAsString("user_name"));

		if (listValue.getAsString("content_last").equals("")) {
			intent.putExtra("content", listValue.getAsString("content_first"));
		} else {
			intent.putExtra("content", listValue.getAsString("content_first")
					+ listValue.getAsString("content_last"));
		}

		intent.putExtra("topicId", settings.getString("SelectedTopic", null));
		intent.putExtra("parentId", listValue.getAsLong("id"));
		Log.w("ID ON POSTS", String.valueOf(listValue.getAsLong("id")));
		startActivity(intent);
	}

	public void updateList(String source) {
		jsonParser = new ParseJSON();
		parsedValues = jsonParser.parseJSON(source,
				Constants.PARSE_NEW_POSTS_ID);
		listAdapter = new PostAdapter(this, parsedValues);
		setListAdapter(listAdapter);

	}

	public boolean postedToday(int postDay, int postMonth, int postYear) {

		if (postDay == currentDay && postMonth == currentMonth
				&& postYear == currentYear)
			return true;
		else
			return false;
	}

	public String getMonthAsText(int postMonth) {
		if (postMonth == 1)

			return "Jan";
		if (postMonth == 2)

			return "Fev";
		if (postMonth == 3)

			return "Mar";
		if (postMonth == 4)

			return "Abr";
		if (postMonth == 5)

			return "Mai";
		if (postMonth == 6)

			return "Jun";
		if (postMonth == 7)

			return "Jul";
		if (postMonth == 8)

			return "Ago";
		if (postMonth == 9)

			return "Set";
		if (postMonth == 10)

			return "Out";
		if (postMonth == 11)

			return "Nov";
		if (postMonth == 12)

			return "Dez";

		return "?";

	}

	public void obtainPosts(String URLString) {
		requestPosts = new RequestPosts(this);
		adapter.open();
		requestPosts.setConnectionParameters(URLString, adapter.getToken());
		adapter.close();
		requestPosts.execute();
	}

	public void getImageFormServer() {
		Log.w("InsidePullImage", "TRUE");
		requestImage = new RequestImage(this);
		adapter.open();
		requestImage.setConnectionParameters(
		"images/1/users",
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
			updateList(result);
		}
	}

	public class PostAdapter extends BaseAdapter {

		Context context;
		ContentValues[] data;
		LayoutInflater inflater = null;

		public PostAdapter(Context context, ContentValues[] data) {
			this.context = context;
			this.data = data;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return data.length;
		}

		@Override
		public Object getItem(int position) {
			return data[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			convertView = inflater.inflate(R.layout.postitem, parent, false);

			TextView postDate = (TextView) convertView
					.findViewById(R.id.post_date);

			if (postedToday(data[position].getAsInteger("postDay"),
					data[position].getAsInteger("postMonth"),
					data[position].getAsInteger("postYear")))

			{
				Log.w("POSTED TODAY", "TRUE");
				postDate.setText(data[position].getAsString("postHour") + ":"
						+ data[position].getAsString("postMinute"));

			} else {

				postDate.setText(data[position].getAsString("postDayString")
						+ " "
						+ getMonthAsText(data[position]
								.getAsInteger("postMonth")));
				Log.w("POSTED TODAY", "FALSE");
			}

			TextView postBody = (TextView) convertView
					.findViewById(R.id.post_body);
			postBody.setText(data[position].getAsString("content_first"));

			TextView userName = (TextView) convertView
					.findViewById(R.id.post_title);
			userName.setText(String.valueOf(data[position]
					.getAsString("user_name")));

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

			String currentTopic = settings.getString("SelectedTopic", null);
			dialog = Dialogs.getProgressDialog(this);
			dialog.show();
			obtainPosts(Constants.URL_DISCUSSION_PREFIX + currentTopic
					+ Constants.URL_POSTS_SUFFIX);

		}
		return true;

	}

}
