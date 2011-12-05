package com.paulo.android.solarmobile;

import java.io.File;
import java.io.IOException;

import com.paulo.android.solarmobile.ListaPosts.RecordOnBackground;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ListaPosts extends ListActivity implements OnClickListener,
		OnChronometerTickListener, OnCompletionListener {

	String[] from = { "teste", "teste2" };
	int[] to = { R.id.item_nome_pessoa, R.id.item_hora_envio };
	Button stop, start, pause, exitDialog, vf;
	TextView contador;
	ImageButton button;
	PostAdapter adapter;
	private static final int REQ_CODE_1 = 1;
	private static final int DIALOG_GRAVAR = 2;
	Dialog myDialog;

	// variáveis de gravar audio
	long countUp;
	long startTime;
	long time2 = 0;
	Chronometer stopWatch;
	MediaRecorder recorder;
	MediaPlayer player;
	File audioFile;
	RecordOnBackground task = new RecordOnBackground();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);
		button = (ImageButton) findViewById(R.id.nova_mensagem);
		button.setOnClickListener(this);

		ContentValues[] teste1 = new ContentValues[2];
		teste1[0] = new ContentValues();
		teste1[1] = new ContentValues();

		teste1[0].put("teste", "teste");
		teste1[1].put("teste", "teste");
		adapter = new PostAdapter(this, teste1);

		setListAdapter(adapter);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.VoiceForum) {
			myDialog = onCreateDialog(DIALOG_GRAVAR);
			myDialog.show();
		}
		if (v.getId() == R.id.img_close) {
			myDialog.dismiss();
		}
		if (v.getId() == R.id.start_recording) {
			// gravar aqui
			startTime = System.currentTimeMillis();
			recorder = new MediaRecorder();

			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

			File path = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/Mobilis/Recordings/");
			path.mkdirs();

			try {
				audioFile = File.createTempFile("recording", ".3gp", path);
			} catch (IOException e) {
				throw new RuntimeException(
						"Couldn't create recording audio file", e);

			}
			recorder.setOutputFile(audioFile.getAbsolutePath());

			try {
				recorder.prepare();
			} catch (IllegalStateException e) {
				throw new RuntimeException(
						"IllegalStateException on MediaRecorder.prepare", e);
			} catch (IOException e) {
				throw new RuntimeException(
						"IOException on MediaRecorder.prepare", e);
			}
			// começa a gravar em background e começa o cronômetro na main
			// thread

			 task.doInBackground();
			 stopWatch.start();

		}
		if (v.getId() == R.id.stop_recording) {
			// parar a gravação
			recorder.stop();
			recorder.release();
			 task.cancel(true);// para a thread
			stopWatch.stop();
			contador.setText("00:00");
			player = new MediaPlayer();
			player.setOnCompletionListener(this);
			try {
				player.setDataSource(audioFile.getAbsolutePath());
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(
						"Illegal Argument to MediaPlayer.setDataSource", e);
			} catch (IllegalStateException e) {
				throw new RuntimeException(
						"Illegal State in MediaPlayer.setDataSource", e);
			} catch (IOException e) {
				throw new RuntimeException(
						"IOException in MediaPalyer.setDataSource", e);
			}
			try {
				player.prepare();
			} catch (IllegalStateException e) {
				throw new RuntimeException(
						"IllegalStateException in MediaPlayer.prepare", e);
			} catch (IOException e) {
				throw new RuntimeException(
						"IOException in MediaPlayer.prepare", e);
			}
		}
		if (v.getId() == R.id.pause_recording) {
			// ?????
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
		stopWatch = (Chronometer)layout.findViewById(R.id.chrono);
		stopWatch.setOnChronometerTickListener(this);
		contador = (TextView)layout.findViewById(R.id.contador);
		
		builder = new AlertDialog.Builder(this);
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
		// countUp = (SystemClock.currentThreadTimeMillis() -
		// chronometer.getBase()) / 1000;
		// countUp = (SystemClock.elapsedRealtime() - chronometer.getBase()) /
		// 1000;
		// countUp = (endTime - startTime) /1000;
		countUp = (endTime - startTime) / 1000;
		// time2 = endTime;

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

		// asText = (countUp / 60) + ":" + (countUp % 60);
		contador.setText(asText);

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
			// TODO Auto-generated method stub
			return data.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;
			if (v == null) {

				v = inflater.inflate(R.layout.postitem, parent, false);

				vf = (Button) v.findViewById(R.id.VoiceForum);
				vf.setOnClickListener(ListaPosts.this);

			}

			return v;
		}

	}

	public class RecordOnBackground extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			recorder.start();
			return null;
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Toast.makeText(this, "Gravação bem sucedida", Toast.LENGTH_SHORT)
				.show();

	}
}
