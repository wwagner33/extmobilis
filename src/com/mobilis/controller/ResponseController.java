package com.mobilis.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilis.audio.AudioPlayer;
import com.mobilis.audio.AudioRecorder;
import com.mobilis.dao.DiscussionDAO;
import com.mobilis.dao.PostDAO;
import com.mobilis.dialog.AudioDialog;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.interfaces.MobilisActivity;
import com.mobilis.model.Discussion;
import com.mobilis.util.Constants;
import com.mobilis.util.MobilisStatus;
import com.mobilis.util.ParseJSON;
import com.mobilis.util.ZipManager;
import com.mobilis.ws.Connection;

public class ResponseController extends MobilisActivity implements
		OnClickListener, OnChronometerTickListener, OnCompletionListener,
		TextWatcher, OnInfoListener {

	private EditText message;
	private Button submit;
	private TextView timeUp, charCount;
	private ImageButton record;

	private ParseJSON jsonParser;
	private JSONObject postObject;
	private boolean existsRecording = false;
	private String charSequenceAfter;
	private DialogMaker dialogMaker;
	private ImageView recordImage;
	private long countUp;
	private long startTime;
	private Chronometer stopWatch;
	private AudioRecorder recorder;
	private AudioPlayer player;
	private float toastTimer = 0;
	private AlertDialog warningDialog;
	private ProgressDialog progressDialog;
	private AudioDialog audioDialog;
	private ResponseHandler handler;
	private Connection connection;
	private PostDAO postDAO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.response);
		postDAO = new PostDAO(this);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		handler = new ResponseHandler();
		connection = new Connection(handler);
		dialogMaker = new DialogMaker(this);
		progressDialog = dialogMaker
				.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);
		submit = (Button) findViewById(R.id.criar_topico_submit);
		submit.setOnClickListener(this);
		message = (EditText) findViewById(R.id.criar_topico_conteudo);
		message.addTextChangedListener(this);
		record = (ImageButton) findViewById(R.id.btn_gravar);
		record.setOnClickListener(this);
		timeUp = (TextView) findViewById(R.id.recording_lenght);
		stopWatch = (Chronometer) findViewById(R.id.recording_chronometer);
		stopWatch.setOnChronometerTickListener(this);
		recordImage = (ImageView) findViewById(R.id.record_image);
		recordImage.setOnClickListener(this);
		player = new AudioPlayer();
		charCount = (TextView) findViewById(R.id.char_number);
		charCount.setText("0/" + Constants.TEXT_MAX_CHARACTER_LENGHT);
		jsonParser = new ParseJSON(this);
		restoreDialog();

	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		Object dialogStorer[] = new Object[3];

		if (progressDialog != null) {
			if (progressDialog.isShowing()) {
				closeDialog(progressDialog);
				dialogStorer[0] = progressDialog;
			}
		}
		if (warningDialog != null) {
			if (warningDialog.isShowing()) {
				warningDialog.dismiss();
				dialogStorer[1] = warningDialog;
			}
		}

		if (audioDialog != null) {
			if (audioDialog.isShowing()) {
				audioDialog.dismiss();
				dialogStorer[2] = audioDialog;
			}
		}
		return dialogStorer;
	}

	@SuppressWarnings("deprecation")
	public void restoreDialog() {
		Log.i("OnRestore", "TRUE");
		if (getLastNonConfigurationInstance() != null) {
			Object restoredObjects[] = (Object[]) getLastNonConfigurationInstance();

			if (restoredObjects[0] != null) {
				progressDialog = (ProgressDialog) restoredObjects[0];
				progressDialog.show();
			}

			if (restoredObjects[1] != null) {
				warningDialog = (AlertDialog) restoredObjects[1];
				warningDialog.show();
			}

			if (restoredObjects[2] != null) {
				audioDialog = (AudioDialog) restoredObjects[2];
				audioDialog.show();
			}

		}
	}

	@Override
	protected void onResume() {

		super.onResume();
		recorder = new AudioRecorder();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (recorder != null)
			recorder.releaseRecording();
	}

	@Override
	public void onBackPressed() {
		if (message.length() > 0 || existsRecording) {
			warningDialog = dialogMaker.makeAlertDialog(
					Constants.DIALOG_ALERT_DISCARD, handler);
			warningDialog.show();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.record_image) {

			player = new AudioPlayer();
			audioDialog = new AudioDialog(this, player, handler);
			audioDialog.show();
		}

		if (v.getId() == R.id.criar_topico_submit) {

			if (!existsRecording && message.getText().length() == 0) {

				Toast.makeText(this, "Mensagem não pode ser vazia",
						Toast.LENGTH_SHORT).show();

			}

			else {
				if (getPreferences().getLong("SelectedPost", 0) > 0) {
					postObject = jsonParser.buildTextResponseWithParentObject(
							message.getText().toString(), getPreferences()
									.getLong("SelectedPost", 0));

				} else {
					postObject = jsonParser
							.buildTextResponseWithoutParent(message.getText()
									.toString());
				}

				sendPost(postObject.toJSONString());
			}
		}

		if (v.getId() == R.id.btn_gravar) {

			if (recorder.getRecordingState()) {

				try {

					getWindow()
							.clearFlags(
									android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

					Toast.makeText(this, "gravação concluída",
							Toast.LENGTH_SHORT).show();
					recorder.stopRecording();
					stopWatch.stop();
					timeUp.setText("00:00");
					timeUp.setVisibility(View.GONE);
					record.setImageResource(R.drawable.gravador_parado);
					if (!existsRecording) {
						recordImage.setVisibility(View.VISIBLE);
						existsRecording = true;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				try {

					getWindow().addFlags(
							WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

					Toast.makeText(this, "Gravando", Toast.LENGTH_SHORT).show();
					recorder.startRecording(Constants.RECORDING_FILENAME);
					timeUp.setVisibility(View.VISIBLE);
					startTime = System.currentTimeMillis();
					stopWatch.start();
					record.setImageResource(R.drawable.gravador_gravando);
				} catch (IllegalStateException e) {
					Log.w("EXCEPTION", "ILLEGAL STATE EXCEPTION");
					record.setImageResource(R.drawable.gravador_parado);
					stopWatch.stop();
					timeUp.setText("00:00");

					getWindow()
							.clearFlags(
									android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
					e.printStackTrace();

				} catch (IOException e) {
					record.setImageResource(R.drawable.gravar_off);
					stopWatch.stop();
					timeUp.setText("00:00");

					getWindow()
							.clearFlags(
									android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
					e.printStackTrace();
				}

				catch (Exception e) {
					record.setImageResource(R.drawable.gravar_off);
					stopWatch.stop();
					timeUp.setText("00:00");

					getWindow()
							.clearFlags(
									android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
					e.printStackTrace();
				}
			}
		}
	}

	public void sendPost(String jsonString) {
		progressDialog.show();
		String token = getPreferences().getString("token", null);

		String url = "discussions/"
				+ getPreferences().getInt("SelectedTopic", 0)
				+ "/posts?auth_token=" + token;

		connection.postToServer(Constants.CONNECTION_POST_TEXT_RESPONSE,
				jsonString, url);

	}

	public void getNewPosts(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_NEW_POSTS, url,
				getPreferences().getString("token", null));

	}

	public void sendAudioPost(String url, File audioFile, String token) {

		connection.postToServer(Constants.CONNECTION_POST_AUDIO, url,
				audioFile, token);
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

	}

	@Override
	public void afterTextChanged(Editable editable) {

		if (charSequenceAfter.length() > Constants.TEXT_MAX_CHARACTER_LENGHT) {

			Log.w("Time Difference",
					String.valueOf(toastTimer - System.currentTimeMillis()));

			if ((toastTimer - System.currentTimeMillis()) <= -2500) {
				Toast.makeText(
						this,
						"Mensagem deve conter no máximo "
								+ Constants.TEXT_MAX_CHARACTER_LENGHT
								+ " caracteres", Toast.LENGTH_SHORT).show();
				toastTimer = System.currentTimeMillis();

			}
			message.getText().delete(message.getText().length() - 1,
					message.getText().length());

		}

		charCount.setText(String.valueOf(charSequenceAfter.length()) + "/"
				+ Constants.TEXT_MAX_CHARACTER_LENGHT);

	}

	public void deleteRecording() {

		File file = new File(Constants.PATH_RECORDINGS
				+ Constants.RECORDING_FULLNAME);
		file.delete();
		existsRecording = false;
		recordImage.setVisibility(View.GONE);
		Toast.makeText(this, "Gravação deletada com sucesso",
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		charSequenceAfter = s.toString();
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
	}

	private class ResponseHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == Constants.DIALOG_ALERT_POSITIVE_BUTTON_CLICKED) {
				Toast.makeText(getApplicationContext(), "Mensagem descartada",
						Toast.LENGTH_SHORT).show();
				finish();
			}

			if (msg.what == Constants.DIALOG_ALERT_NEGATIVE_BUTTON_CLICKED) {
				Toast.makeText(getApplicationContext(), "onHandlerNegative",
						Toast.LENGTH_SHORT).show();

			}

			if (msg.what == Constants.DIALOG_DELETE_AREA_CLICKED) {
				deleteRecording();
			}

			if (msg.what == Constants.MESSAGE_CONNECTION_FAILED) {
				closeDialog(progressDialog);
			}

			if (msg.what == Constants.MESSAGE_TEXT_RESPONSE_OK) {

				Log.w("TEXT RESPONSE RESULT", msg.getData()
						.getString("content"));

				ContentValues[] resultFromServer;
				jsonParser = new ParseJSON(getApplicationContext());
				resultFromServer = jsonParser
						.parseJSON(msg.getData().getString("content"),
								Constants.PARSE_TEXT_RESPONSE_ID);

				if (existsRecording) {
					long postId = (Long) resultFromServer[0].get("post_id");
					sendAudioPost(
							Constants.generateAudioResponseURL((int) postId),
							recorder.getAudioFile(), getPreferences()
									.getString("token", null));

				} else {
					DiscussionDAO discussionDAO = new DiscussionDAO(
							getApplicationContext());
					discussionDAO.open();
					Discussion currentDiscussion = discussionDAO
							.getDiscussion(getPreferences().getInt(
									"SelectedTopic", 0));

					discussionDAO.setNextPosts(
							getPreferences().getInt("SelectedTopic", 0),
							(currentDiscussion.getNextPosts() + 1));
					discussionDAO.close();
					Intent intent = new Intent(getApplicationContext(),
							ExtMobilisTTSActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

					startActivity(intent);
				}
			}

			if (msg.what == Constants.MESSAGE_AUDIO_POST_OK) {

				DiscussionDAO discussionDAO = new DiscussionDAO(
						getApplicationContext());
				discussionDAO.open();
				Discussion currentDiscussion = discussionDAO
						.getDiscussion(getPreferences().getInt("SelectedTopic",
								0));

				discussionDAO.setNextPosts(
						getPreferences().getInt("SelectedTopic", 0),
						(currentDiscussion.getNextPosts() + 1));
				discussionDAO.close();
				Intent intent = new Intent(getApplicationContext(),
						ExtMobilisTTSActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				MobilisStatus status = MobilisStatus.getInstance();
				postDAO.open();
				status.ids = postDAO.getIdsOfPostsWithoutImage(getPreferences()
						.getInt("SelectedTopic", 0));
				postDAO.close();
				startActivity(intent);

			}

			if (msg.what == Constants.MESSAGE_AUDIO_POST_FAILED) {
				Toast.makeText(getApplicationContext(),
						"Erro no envio de áudio", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getApplicationContext(),
						ExtMobilisTTSActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				MobilisStatus status = MobilisStatus.getInstance();
				postDAO.open();
				status.ids = postDAO.getIdsOfPostsWithoutImage(getPreferences()
						.getInt("SelectedTopic", 0));
				postDAO.close();
				startActivity(intent);
			}
		}
	}
}
