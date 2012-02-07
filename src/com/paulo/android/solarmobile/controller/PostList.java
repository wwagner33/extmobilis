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

public class PostList extends ListActivity implements OnClickListener,
		OnChronometerTickListener {

	Button ouvir, stop, start, pause, exitDialog, vf, response;
	TextView contador;
	ImageButton button;
	PostAdapter listAdapter;

	Dialog myDialog;
	public int tagHolder;
	String forumName;
	String extrasString;

	public int parentId;

	public String topicId;

	public int previousSelected;

	ContentValues parsedValues[];

	Bundle extras;

	File path = new File(Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/Mobilis/Recordings/");

	// Valores da lista
	ContentValues[] teste1;

	// variáveis de gravar audio
	long countUp;
	long startTime;
	long time2 = 0;
	Chronometer stopWatch;
	File audioFile;

	// recorder
	MediaRecorder record;
	AudioPlayer player;

	private static final int PARSE_POSTS = 225;
	DBAdapter adapter;
	Connection connection;
	ParseJSON jsonParser;

	TextView textName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);

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
			teste1 = new ContentValues[10];

			for (int i = 1; i < teste1.length; i++) {
				teste1[i] = new ContentValues();
				teste1[i].put("nada", "nada");
			}

			listAdapter = new PostAdapter(this, teste1);
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

		//Log.w("Data", listValue.getAsString("postDate"));
		
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

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.img_close) {
			myDialog.dismiss();
		}
		if (v.getId() == R.id.start_recording) {

			record = new MediaRecorder();

			try {
				record.startRecording("teste");
			} catch (Exception e) {
				// tratar erro
			}
			startTime = System.currentTimeMillis();
			stopWatch.start();

		}
		if (v.getId() == R.id.stop_recording) {

			record.stopRecording();
			stopWatch.stop();
			contador.setText("00:00");

			// muda a view que possue voz
			teste1[tagHolder].remove("hasVoice");
			teste1[tagHolder].put("hasVoice", true);
			// salva o nome do arquivo de áudio na lista
			teste1[tagHolder].put("voiceFileName", record.getAudioFilePath());
			setListAdapter(listAdapter);

		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog alertDialog;

		AlertDialog.Builder builder;

		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.gravar,
				(ViewGroup) findViewById(R.layout.post));

		start = (Button) layout.findViewById(R.id.start_recording);
		start.setOnClickListener(this);
		stop = (Button) layout.findViewById(R.id.stop_recording);
		stop.setOnClickListener(this);
		pause = (Button) layout.findViewById(R.id.pause_recording);
		pause.setOnClickListener(this);
		exitDialog = (Button) layout.findViewById(R.id.img_close);
		exitDialog.setOnClickListener(this);
		stopWatch = (Chronometer) layout.findViewById(R.id.chrono);
		stopWatch.setOnChronometerTickListener(this);
		contador = (TextView) layout.findViewById(R.id.contador);

		builder = new AlertDialog.Builder(new ContextThemeWrapper(this,
				R.style.CustomDialog));
		builder.setView(layout);
		alertDialog = builder.create();
		return alertDialog;
	}

	@Override
	public void onChronometerTick(Chronometer chronometer) {
		long endTime = System.currentTimeMillis();
		String asText = "";
		String Text1 = "";
		String Text2 = "";
		countUp = (endTime - startTime) / 1000;

		if (countUp / 60 <= 9) {
			Text1 = "0" + (countUp / 60);
		} else {
			Text1 += (countUp / 60);
		}

		if (countUp % 60 <= 9) {
			Text2 = "0" + (countUp % 60);
		} else {
			Text2 += (countUp % 60);
		}

		asText = Text1 + ":" + Text2;

		contador.setText(asText);

	}

	public class MediaRecorder extends RecordAudio {

		@Override
		public void updateRecordingDialog(int protocol) {
			if (protocol == 1) {
				// disable gravar
				// enable stop

			}

			if (protocol == 2) {
				// disable stop
				// enable record
			}
		}
	}

	public class AudioPlayer extends PlayAudio {

		public AudioPlayer(String fileName) throws IllegalStateException,
				IOException {
			super(fileName);
		}

		@Override
		public void onCompletion(android.media.MediaPlayer mp) {
		}

		@Override
		public void updatePlayingDialog() {

		}
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

			// if (convertView == null) {

			convertView = inflater.inflate(R.layout.postitem, parent, false);
			TextView postBody = (TextView) convertView
					.findViewById(R.id.post_body);
			postBody.setText(data[position].getAsString("content"));

			TextView userName = (TextView) convertView
					.findViewById(R.id.post_title);
			userName.setText(String.valueOf(data[position]
					.getAsString("username")));

			// }

			return convertView;
		}
	}

	/*
	 * Backup if (v.getId() == R.id.VoiceForum) {
	 * 
	 * myDialog = onCreateDialog(DIALOG_GRAVAR); tagHolder = (Integer)
	 * v.getTag(R.id.change); myDialog.show();
	 * 
	 * }
	 * 
	 * 
	 * if (v.getId() == R.id.ouvir) {
	 * 
	 * } if (v.getId() == R.id.Responder) { Intent intent = new Intent(this,
	 * ResponseController.class); startActivity(intent); }
	 */

}
