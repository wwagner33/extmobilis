package com.paulo.android.solarmobile.controller;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.paulo.android.solarmobile.model.DBAdapter;
import com.paulo.android.solarmobile.ws.Connection;
import com.paulo.solarmobile.audio.PlayAudio;
import com.paulo.solarmobile.audio.RecordAudio;

public class ResponseController extends Activity implements OnClickListener,
		OnChronometerTickListener, OnCompletionListener {

	// Wedson: curl -v -H 'Content-Type: application/json' -H 'Accept:
	// application/json' -X POST
	// http://localhost:3000/discussions/1/posts?auth_token=B3BQ1twAooSXWY53hktp
	// --data '{"discussion_post":{"content":"estou criando um novo post dentro
	// de 1", "parent_id":""}}'

	// reponder um forum

	// File recordingsFolder;

	EditText message;
	Button submit, cancelar, deleteRecording, previewAudio;
	SeekBar audioProgress;
	TextView timeUp, charCount;
	ImageButton record;
	Connection connection;
	JSONObject responseJSON;
	ProgressDialog dialog;
	Bundle extras;
	long parentId;
	String noParent = "";
	SendNewPostThread thread;
	DBAdapter adapter;
	String token, JSONObjectString, URL, topicId,postId;
	ParseJSON jsonParser;
	ContentValues[] resultFromServer;

	RelativeLayout audioPreviewBar;

	ObtainPostListThread postThread;
	String forumName;

	public boolean existsRecording = false;

	File path = new File(Environment.getExternalStorageDirectory()
			.getAbsolutePath() + Constants.RECORDING_PATH);

	File recordedFilePath = new File(path.getAbsolutePath()
			+ Constants.RECORDING_FULLNAME);

	File currentRecordingPath;

	boolean imageChanger = true;

	// variáveis do cronômetro
	long countUp;
	long startTime;
	long time2 = 0;
	Chronometer stopWatch;
	//File audioFile;

	RecordAudio recorder;
	PlayAudio player;

	// Dialog de gravação

	Button start, stop, exitDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.responder_topico);
		extras = getIntent().getExtras();
		connection = new Connection(this);
		dialog = Dialogs.getProgressDialog(this);
		submit = (Button) findViewById(R.id.criar_topico_submit);
		submit.setOnClickListener(this);
		message = (EditText) findViewById(R.id.criar_topico_conteudo);
		record = (ImageButton) findViewById(R.id.btn_gravar);
		record.setOnClickListener(this);
		timeUp = (TextView) findViewById(R.id.recording_lenght);
		stopWatch = (Chronometer) findViewById(R.id.recording_chronometer);
		stopWatch.setOnChronometerTickListener(this);

		deleteRecording = (Button) findViewById(R.id.delete_recording);
		deleteRecording.setOnClickListener(this);

		cancelar = (Button) findViewById(R.id.resposta_btn_cancel);
		cancelar.setOnClickListener(this);

		audioPreviewBar = (RelativeLayout) findViewById(R.id.audio_preview_bar);

		previewAudio = (Button) findViewById(R.id.preview_recording);
		previewAudio.setOnClickListener(this);

		connection = new Connection(this);

		jsonParser = new ParseJSON();

		if (extras != null) {
			topicId = extras.getString("topicId");
			forumName = extras.getString("ForumName");

		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		recorder = new RecordAudio();
	}

	@Override
	protected void onStop() {

		super.onStop();
		if (adapter != null) {
			adapter.close();
		}
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
		if (recorder != null)
			recorder.releaseRecording();

	}

	public void deleteRecording() {
		recorder.getAudioFile().delete();
		Toast.makeText(this, "Gravação deletada com sucesso",
				Toast.LENGTH_SHORT).show();
		audioPreviewBar.setVisibility(View.GONE);
		existsRecording = false;
	}

	public void closeDialogIfItsVisible() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();

	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.preview_recording) {
			player = new PlayAudio();
			try {
				player.playOwnAudio();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (v.getId() == R.id.resposta_btn_cancel) {

			/*
			 * Log.w("OnClick", "YES"); if (previewFragment.isShown())
			 * previewFragment.setVisibility(View.GONE); else
			 * previewFragment.setVisibility(View.VISIBLE);
			 */

			// if existsRecording = true;
			// Dialog == mensagem será descartada, continuar ?
			// else finish ()

		}

		if (v.getId() == R.id.delete_recording) {
			deleteRecording();
		}

		if (v.getId() == R.id.criar_topico_submit) {

			responseJSON = new JSONObject();
			LinkedHashMap jsonMap = new LinkedHashMap<String, String>();
			jsonMap.put("content", message.getText().toString());
			if (extras.getLong("parentId") > 0) {
				jsonMap.put("parent_id",
						String.valueOf(extras.getLong("parentId")));

			} else {
				jsonMap.put("parent_id", noParent);
			}
			responseJSON.put("discussion_post", jsonMap);
			JSONObjectString = responseJSON.toJSONString();
			Log.w("JSONString", JSONObjectString);

			sendPost();

		}

		if (v.getId() == R.id.btn_gravar) {

			if (recorder.getRecordingState()) {
				Toast.makeText(this, "gravação concluída", Toast.LENGTH_SHORT)
						.show();
				recorder.stopRecording();
				stopWatch.stop();
				timeUp.setText("00:00");
				timeUp.setVisibility(View.GONE);
				record.setImageResource(R.drawable.gravar_off);
				if (!existsRecording) {
					audioPreviewBar.setVisibility(View.VISIBLE);
				}
			} else {
				try {
					Toast.makeText(this, "Gravando", Toast.LENGTH_SHORT).show();
					recorder.startRecording(Constants.RECORDING_FILENAME);
					timeUp.setVisibility(View.VISIBLE);
					startTime = System.currentTimeMillis();
					stopWatch.start();
					record.setImageResource(R.drawable.gravar_on);
				} catch (IllegalStateException e) {
					Log.w("EXCEPTION", "ILLEGAL STATE EXCEPTION");
					record.setImageResource(R.drawable.gravar_off);
					stopWatch.stop();
					timeUp.setText("00:00");
					e.printStackTrace();
				} catch (IOException e) {
					record.setImageResource(R.drawable.gravar_off);
					// ErrorHandler.handleError(// audio error);
					stopWatch.stop();
					timeUp.setText("00:00");
					e.printStackTrace();
				}
			}
		}

		/*
		 * if (v.getId() == R.id.start_recording) {
		 * 
		 * recorder = new MediaRecorder();
		 * 
		 * try { recorder.startOrStopRecording("teste"); } catch (Exception e) {
		 * // tratar erro }
		 * 
		 * startTime = System.currentTimeMillis(); stopWatch.start();
		 * 
		 * } if (v.getId() == R.id.stop_recording) {
		 * 
		 * recorder.stopRecording(); stopWatch.stop();
		 * contador.setText("00:00");
		 * 
		 * /* // muda a view que possue voz
		 * teste1[tagHolder].remove("hasVoice");
		 * teste1[tagHolder].put("hasVoice", true); // salva o nome do arquivo
		 * de áudio na lista teste1[tagHolder].put("voiceFileName",
		 * record.getAudioFilePath()); setListAdapter(listAdapter);
		 * 
		 * }
		 */
	}

	public void sendPost() {
		dialog.show();
		adapter = new DBAdapter(this);
		adapter.open();
		token = adapter.getToken();
		adapter.close();

		URL = "discussions/" + topicId + "/posts?auth_token=" + token;

		Log.w("URL", URL);

		thread = new SendNewPostThread();
		thread.execute();
	}

	public void getPosts() {
		postThread = new ObtainPostListThread();
		postThread.execute(token);
	}

	public class SendNewPostThread extends AsyncTask<Void, Void, Object[]> {

		@Override
		protected Object[] doInBackground(Void... params) {

			try {

				return connection.postToServer(JSONObjectString, URL);

			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Object[] result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (result == null) {
				closeDialogIfItsVisible();
				ErrorHandler.handleError(getApplicationContext(),
						Constants.ERROR_CONNECTION_REFUSED);
			}

			else {
				int statusCode = (Integer) result[1];
				if (statusCode == 201 || statusCode == 200) {

					String resultString = (String) result[0];
					Log.w("RESPONSE RESPONSE", resultString);

					resultFromServer = jsonParser.parseJSON(resultString,
							ParseJSON.PARSE_TEXT_RESPONSE);

					Log.w("RESULT",
							String.valueOf(resultFromServer[0].get("result")));
					Log.w("POST_ID",
							String.valueOf(resultFromServer[0].get("post_id")));

					if (existsRecording) {

						// sendRecording()

					}

					else {
						getPosts();
					}

				} else {
					closeDialogIfItsVisible();
					ErrorHandler.handleError(getApplicationContext(),
							statusCode);

				}

			}
		}

	}

	public class SendAudioFile extends AsyncTask<String, Void, Object[]> {

		@Override
		protected Object[] doInBackground(String... params) {
			try {
				return connection.postAudioToServer(URL, recorder.getAudioFile());
			} catch (IllegalStateException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

		}

		@Override
		protected void onPostExecute(Object[] result) {

			super.onPostExecute(result);
		}

	}

	public void handleError(int errorId) {
		Toast.makeText(getApplicationContext(),
				"Texto não pode ser enor do que 9 caracteres",
				Toast.LENGTH_LONG).show();
	}

	public class ObtainPostListThread extends AsyncTask<String, Void, Object[]> {

		Intent intent;

		@Override
		protected Object[] doInBackground(String... params) {

			try {

				return connection.getFromServer("discussions/" + topicId
						+ "/posts.json", params[0]);

			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return null;

			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

			catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}

		@Override
		protected void onPostExecute(Object[] result) {

			Log.w("PostExecute", "POST");
			
			super.onPostExecute(result);
			adapter.close();

			if (result == null) {

				ErrorHandler.handleError(getApplicationContext(),
						Constants.ERROR_CONNECTION_REFUSED);
			}

			else {
				Log.w("Teste", "TESTE3");
				int statusCode = (Integer) result[1];

				if (statusCode == 200) {

					intent = new Intent(getApplicationContext(), PostList.class);
					intent.putExtra("ForumName", forumName); // onDetails
					intent.putExtra("PostList", (String) result[0]); // OK
					intent.putExtra("topicId", topicId); // OK
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("TESTE", "TESTE");
					startActivity(intent);
				} else {

					closeDialogIfItsVisible();
					ErrorHandler.handleError(getApplicationContext(),
							statusCode);

				}
			}
		}
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

		timeUp.setText(asText);

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}

}
