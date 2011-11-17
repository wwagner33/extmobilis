package com.paulo.android.solarmobile;



import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ResponderTopico extends Activity implements OnClickListener, OnCompletionListener {
		private Button gravar,submit,start,stop,exit;
		private EditText nomeTopico,textoTopico;
		private TextView contador,status;
		private Dialog myDialog;
		private static final int DIALOG_CODE_1 = 1;
		
		MediaRecorder recorder;
		MediaPlayer player;
		
		File audioFile;
		
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
				setContentView(R.layout.responder_topico);
				myDialog = onCreateDialog(DIALOG_CODE_1);
				submit = (Button)findViewById(R.id.criar_topico_submit);
				submit.setOnClickListener(this);
				gravar = (Button) findViewById(R.id.nova_mensagem_de_voz);
				gravar.setOnClickListener(this);
				nomeTopico = (EditText)findViewById(R.id.criar_topico_nome);
				textoTopico = (EditText)findViewById(R.id.criar_topico_conteudo);
	}

	@Override
	public void onClick(View v) {
			if (v.equals(gravar)) {
				myDialog.show();
			}
			
			if (v.equals(submit)) {
				
				if (nomeTopico.getText().toString().matches("") || textoTopico.getText().toString().matches("")) {
						Toast.makeText(this, "Validação fail", Toast.LENGTH_SHORT).show();
			}
				else 	{
					Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
				}
		}
			if (v.equals(start)) {
				
	recorder = new MediaRecorder();
				
				recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				
				File path = new File (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mobilis/Recordings/");
				path.mkdirs();
				
				try {
					audioFile = File.createTempFile("recording", ".3gp",path);
				}
					catch(IOException e)	
					{	throw new RuntimeException("Couldn't create recording audio file",e);	}
				
				recorder.setOutputFile(audioFile.getAbsolutePath());
				
				try {
					recorder.prepare();
					} catch (IllegalStateException e) {
					throw new RuntimeException(
					"IllegalStateException on MediaRecorder.prepare", e);
					} catch (IOException e) {
					throw new RuntimeException("IOException on MediaRecorder.prepare",e);
					}
					recorder.start();
				
			}
			
			if (v.equals(stop)) {
				recorder.stop();
				recorder.release();
				
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
					throw new RuntimeException("IOException in MediaPlayer.prepare", e);
					}
				
			}
			
			if (v.equals(exit)) {
				
				myDialog.dismiss();
				
			}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog alertDialog;
				
					AlertDialog.Builder builder;
					
					Context mContext = getApplicationContext();
					LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
					View layout = inflater.inflate(R.layout.dialog_gravar, (ViewGroup)findViewById(R.layout.responder_topico));
					/*OPERATIONS*/
					stop = (Button)layout.findViewById(R.id.stop_recording);
					stop.setOnClickListener(this);
					start = (Button)layout.findViewById(R.id.start_recording);
					start.setOnClickListener(this);
					exit = (Button)layout.findViewById(R.id.sair_dialog);
					exit.setOnClickListener(this);
					contador = (TextView)layout.findViewById(R.id.contador);
					status = (TextView)findViewById(R.id.status);
					
					
					builder = new AlertDialog.Builder(this);
					builder.setView(layout);
					alertDialog = builder.create();
					
					
					return alertDialog;
	} 		
	
	@Override
	public void onCompletion(MediaPlayer mp) {
			Toast.makeText(this, "Fim", Toast.LENGTH_SHORT).show();
		
	}	

}
