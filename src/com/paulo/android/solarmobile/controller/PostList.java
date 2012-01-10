package com.paulo.android.solarmobile.controller;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer.OnCompletionListener;
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

import com.paulo.solarmobile.audio.PlayAudio;
import com.paulo.solarmobile.audio.RecordAudio;

public class PostList extends ListActivity implements OnClickListener,
		OnChronometerTickListener  {

	String[] from = { "teste", "teste2" };
	int[] to = { R.id.item_nome_pessoa, R.id.item_hora_envio };
	Button ouvir,stop,start,pause,exitDialog,vf,response;
	TextView contador;
	ImageButton button;
	PostAdapter adapter;
	private static final int REQ_CODE_1 = 1;
	private static final int DIALOG_GRAVAR = 2;
	Dialog myDialog;
	public int tagHolder;
	
	
	
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
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);
		
		
		
		button = (ImageButton) findViewById(R.id.nova_mensagem);
		button.setOnClickListener(this);

		teste1 = new ContentValues[2];
		teste1[0] = new ContentValues();
		teste1[1] = new ContentValues();

		teste1[0].put("teste", "teste");
		teste1[0].put("hasVoice",false);
		teste1[1].put("teste", "teste");
		teste1[1].put("hasVoice",false);
		
		adapter = new PostAdapter(this, teste1);
		setListAdapter(adapter);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.VoiceForum) {
		//	Toast.makeText(this, "teste", Toast.LENGTH_SHORT).show();
			Toast.makeText(this, path.getAbsolutePath(),Toast.LENGTH_SHORT).show();
			myDialog = onCreateDialog(DIALOG_GRAVAR);
			tagHolder  = (Integer)v.getTag(R.id.change);
			myDialog.show();
			
		//	String[] teste = path.list();
		//	Toast.makeText(this, teste[0], Toast.LENGTH_SHORT).show();
			
		}
		if (v.getId() == R.id.img_close) {
			myDialog.dismiss();
		}
		if (v.getId() == R.id.start_recording) {
			// gravar aqui
			record = new MediaRecorder();
			record.startRecording("teste");
			startTime = System.currentTimeMillis();
			 stopWatch.start();

		}
		if (v.getId() == R.id.stop_recording) {
			
			record.stopRecording();
			stopWatch.stop();
			contador.setText("00:00");
		
			// update view 
			// muda a view que possue voz
			teste1[tagHolder].remove("hasVoice");
			teste1[tagHolder].put("hasVoice", true);		
			//salva o nome do arquivo de áudio na lista
			teste1[tagHolder].put("voiceFileName", record.getAudioFilePath());	
			setListAdapter(adapter);
			
			
		}
		
		if (v.getId() == R.id.ouvir) {
				
		
		}
		if (v.getId()==R.id.Responder) {
		 Intent	intent = new Intent(this,ResponseController.class);
		 startActivity(intent);
			
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
	

	public class MediaRecorder extends RecordAudio {

		@Override
		public void updateRecordingDialog(int protocol) {
				if (protocol ==1) {
							// disable gravar
							// enable stop
							
				}
				
				if (protocol ==2) {
						// disable stop
						// enable record	
			}
		}	
	}
	
	public class AudioPlayer extends PlayAudio {

		public AudioPlayer(String fileName) {
			super(fileName);
			
		}

		@Override
		public void onCompletion(android.media.MediaPlayer mp) {
				// faz nada por eunquanto
			
		}

		@Override
		public void updatePlayingDialog() {
			// TODO Auto-generated method stub
			
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

		
			if (convertView == null) {
					if (data[position].getAsBoolean("hasVoice")==false) {				
					convertView = inflater.inflate(R.layout.postitem, parent, false);
					convertView.setId(1);
					}
					else  {
					convertView = inflater.inflate(R.layout.postitemvoice,parent,false);
					convertView.setId(2);
					}
						
			}
				if (convertView.getId()==2) {
					ouvir = (Button)convertView.findViewById(R.id.ouvir);
					ouvir.setTag(R.id.change,position);
					ouvir.setOnClickListener(PostList.this);
				}
		
				vf = (Button) convertView.findViewById(R.id.VoiceForum);
				response = (Button)convertView.findViewById(R.id.Responder);
				response.setOnClickListener(PostList.this);
				vf.setTag(R.id.change,position);
				vf.setOnClickListener(PostList.this);
			

			return convertView;
		}
	}
 }
