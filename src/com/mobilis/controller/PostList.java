package com.mobilis.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
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

import com.mobilis.model.DBAdapter;
import com.mobilis.threads.RequestHistoryPostsThread;
import com.mobilis.threads.RequestImageThread;
import com.mobilis.threads.RequestNewPostsThread;
import com.mobilis.threads.RequestPostsThread;

public class PostList extends ListActivity implements OnClickListener,
		OnScrollListener {

	// http://apolo11teste.virtual.ufc.br/ws_solar/images/7/users
	// /discussions/:id/posts/:date/history

	// private static final int itemsPerPage = 20;

	private int postHistoryCount;

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
	private RequestNewPosts requestNewPosts;
	private RequestHistoryPosts requestHistoryPosts;

	private ContentValues[] sessionPosts;

	// history
	private boolean loadingMore = false;
	private String oldestPostDate;
	private View footerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);

		/*
		 * footerView = footerView = ((LayoutInflater) this
		 * .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
		 * R.layout.post_list_footer, null, false);
		 */

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

		// getImageFormServer();
		// Unzip File

		adapter.open();
		updateList(adapter.getPosts());
		adapter.close();

		// dialog = Dialogs.getProgressDialog(this);
		// dialog.show();
		// unzipFile();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void closeDialogIfItsVisible() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();

	}

	private void createDir(File dir) {
		if (dir.exists()) {
			return;
		}
		if (!dir.mkdirs()) {
			throw new RuntimeException("Cannot create dir " + dir);
		}
	}

	private void unzipEntry(ZipFile zipfile, ZipEntry entry, String outputDir)
			throws IOException {

		if (entry.isDirectory()) {
			createDir(new File(outputDir, entry.getName()));
			return;
		}

		File outputFile = new File(outputDir, entry.getName());
		if (!outputFile.getParentFile().exists()) {
			createDir(outputFile.getParentFile());
		}

		BufferedInputStream inputStream = new BufferedInputStream(
				zipfile.getInputStream(entry));
		BufferedOutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(outputFile));

		try {
			IOUtils.copy(inputStream, outputStream);
		} finally {
			outputStream.close();
			inputStream.close();
		}

	}

	@SuppressWarnings("rawtypes")
	public void unzipFile() {
		String destinationPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Mobilis/Recordings/";
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Mobilis/Recordings/teste.zip");
		try {
			ZipFile zipFile = new ZipFile(file);

			for (Enumeration e = zipFile.entries(); e.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				unzipEntry(zipFile, entry, destinationPath);

			}

		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

		/*
		 * if (parsedValues.length == 20) {
		 * 
		 * ((LayoutInflater) this
		 * .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
		 * .inflate(R.layout.post_list_footer, null, false);
		 * this.getListView().addFooterView(footerView); }
		 */

		// oldestPostDate = parsedValues[parsedValues.length - 1]
		// .getAsString("updated");
		// Log.w("OLDEST DATE ON LIST", oldestPostDate);//

		listAdapter = new PostAdapter(this, parsedValues);

		/*
		 * adding the footer view to the screen View footerView =
		 * ((LayoutInflater) this
		 * .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
		 * R.layout.post_list_footer, null, false);
		 * this.getListView().addFooterView(footerView);
		 */

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
			adapter.open();
			adapter.updatePostsString(result);
			adapter.close();
			updateList(result);
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
			// TODO Auto-generated method stub

		}

		@Override
		public void onRequestHistoryPostsConnectionSucceded(String result) {

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

			// String currentTopic = settings.getString("SelectedTopic", null);
			dialog = Dialogs.getProgressDialog(this);
			dialog.show();
			String url = "discussions/"
					+ settings.getString("SelectedTopic", null) + "/posts/"
					+ Constants.oldDateString + "/news.json";

			obtainNewPosts(url);

			// Old call
			/*
			 * obtainPosts(Constants.URL_DISCUSSION_PREFIX + currentTopic +
			 * Constants.URL_POSTS_SUFFIX);
			 */
		}
		return true;

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		/*
		 * if (this.getListView().getAdapter().getCount() > 20) { if
		 * (!footerView.isVisible()) { footerView.setVisibility(View.VISIBLE); }
		 * 
		 * }
		 */

		int lastInScreen = firstVisibleItem + visibleItemCount;
		if ((lastInScreen == totalItemCount) && !(loadingMore)) {

			requestHistoryPosts = new RequestHistoryPosts(this);
			adapter.open();

			String url = "/discussions/"
					+ settings.getString("SelectedTopic", null) + "/posts/"
					+ oldestPostDate + "/history";
			requestHistoryPosts
					.setConnectionParameters(url, adapter.getToken());
			adapter.close();
			requestHistoryPosts.execute();
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

}
