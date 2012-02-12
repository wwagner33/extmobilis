package com.paulo.android.solarmobile.controller;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.paulo.android.solarmobile.model.DBAdapter;
import com.paulo.android.solarmobile.ws.Connection;
import com.paulo.solarmobile.audio.PlayAudio;
import com.paulo.solarmobile.audio.RecordAudio;

public class PostList extends ListActivity {

	Button ouvir, stop, start, pause, exitDialog, vf, response;
	TextView contador;
	ImageButton button;
	PostAdapter listAdapter;
	Dialog myDialog;
	String extrasString, forumName;
	public int parentId, tagHolder, previousSelected;
	public String topicId;
	ContentValues parsedValues[];
	Bundle extras;

	File path = new File(Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/Mobilis/Recordings/");

	// quando a lista for vazia
	ContentValues[] emptyListFiller;

	private static final int PARSE_POSTS = 225;
	DBAdapter adapter;
	Connection connection;
	ParseJSON jsonParser;

	TextView textName;

	int currentDay, currentMonth, currentYear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);

		Calendar calendar = Calendar.getInstance();

		Log.w("ANO ATUAL", String.valueOf(calendar.get(Calendar.YEAR)));
		currentYear = calendar.get(Calendar.YEAR);

		Log.w("DIA ATUAL", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
		currentDay = calendar.get(Calendar.DAY_OF_MONTH);

		Log.w("MẼS Atual", String.valueOf(calendar.get(Calendar.MONTH)));
		currentMonth = calendar.get(Calendar.MONTH) + 1;

		textName = (TextView) findViewById(R.id.nome_forum);
		extras = getIntent().getExtras();
		extrasString = extras.getString("PostList");

		adapter = new DBAdapter(this);
		connection = new Connection(this);

		if (extrasString != null) {

			forumName = extras.getString("ForumName");
			textName.setText(forumName);
			topicId = extras.getString("topicId");

		}

		else {
			emptyListFiller = new ContentValues[10];

			for (int i = 1; i < emptyListFiller.length; i++) {
				emptyListFiller[i] = new ContentValues();
				emptyListFiller[i].put("nada", "nada");
			}

			listAdapter = new PostAdapter(this, emptyListFiller);
			setListAdapter(listAdapter);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (extrasString != null) {
			updateList(extrasString);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		ContentValues listValue = (ContentValues) l.getAdapter().getItem(
				position);
		Intent intent = new Intent(this, PostDetailController.class);
		intent.putExtra("username", listValue.getAsString("username"));
		intent.putExtra("content", listValue.getAsString("content"));
		intent.putExtra("forumName", forumName); // OK
		intent.putExtra("topicId", topicId); // OK
		intent.putExtra("parentId", listValue.getAsLong("id"));
		Log.w("ID ON POSTS", String.valueOf(listValue.getAsLong("id")));

		// Log.w("Data", listValue.getAsString("postDate"));

		startActivity(intent);
	}

	public void updateList(String source) {
		jsonParser = new ParseJSON();
		parsedValues = jsonParser.parseJSON(source, PARSE_POSTS);
		listAdapter = new PostAdapter(this, parsedValues);
		setListAdapter(listAdapter);
	}

	public void updateList(ContentValues[] values) {
		Log.w("teste", "teste");
		PostAdapter newAdapter = new PostAdapter(this, parsedValues);
		setListAdapter(newAdapter);
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

	public class PostAdapter extends BaseAdapter {

		Activity activity;
		ContentValues[] data;
		LayoutInflater inflater = null;

		public PostAdapter(Activity a, ContentValues[] d) {
			activity = a;
			data = d;
			inflater = LayoutInflater.from(activity);
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
			postBody.setText(data[position].getAsString("content"));

			TextView userName = (TextView) convertView
					.findViewById(R.id.post_title);
			userName.setText(String.valueOf(data[position]
					.getAsString("username")));

			return convertView;
		}
	}
}
