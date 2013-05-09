package com.mobilis.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
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

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mobilis.audio.AudioPlayer;
import com.mobilis.audio.AudioRecorder;
import com.mobilis.dao.DatabaseHelper;
import com.mobilis.dao.DiscussionDAO;
import com.mobilis.dao.PostDAO;
import com.mobilis.dialog.AudioDialog;
import com.mobilis.dialog.AudioDialog.onDeleteListener;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.interfaces.AudioDialogListener;
import com.mobilis.interfaces.ConnectionCallback;
import com.mobilis.model.Discussion;
import com.mobilis.util.Constants;
import com.mobilis.util.ErrorHandler;
import com.mobilis.util.MobilisPreferences;
import com.mobilis.util.ParseJSON;
import com.mobilis.ws.Connection;

public class ResponseActivity extends SherlockActivity implements
		OnClickListener, OnChronometerTickListener, OnCompletionListener,
		TextWatcher, OnInfoListener, ConnectionCallback, AudioDialogListener,
		onDeleteListener {

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
	private Connection connection;
	private PostDAO postDAO;
	private MobilisPreferences appState;
	private DiscussionDAO discussionDAO;
	private Discussion currentDiscussion;
	private Intent intent;
	private DatabaseHelper helper = null;
	private ActionBar actionBar;
	private String TAG = "RESPONSE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.response);
		helper = getHelper();
		discussionDAO = new DiscussionDAO(helper);
		appState = MobilisPreferences.getInstance(this);
		postDAO = new PostDAO(helper);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		connection = new Connection(this);
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
		charCount = (TextView) findViewById(R.id.char_number);
		charCount.setText("0/" + Constants.TEXT_MAX_CHARACTER_LENGHT);
		jsonParser = new ParseJSON();
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("Responder");
		restoreDialog();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (helper != null) {
			OpenHelperManager.releaseHelper();
			helper = null;
		}
	}

	private DatabaseHelper getHelper() {
		if (helper == null) {
			helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		return helper;
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		Object dialogStorer[] = new Object[3];

		if (progressDialog != null) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
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
					Constants.DIALOG_ALERT_DISCARD, this);
			warningDialog.show();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.record_image) {

			player = new AudioPlayer(Constants.PATH_RECORDINGS
					+ Constants.RECORDING_FULLNAME);
			audioDialog = new AudioDialog(this, player);
			audioDialog.show();
		}

		if (v.getId() == R.id.criar_topico_submit) {

			if (existsRecording) {
				message.append("\n[Áudio em anexo]");
			}

			if (message.getText().length() == 0) {
				Toast.makeText(this, "Mensagem não pode ser vazia",
						Toast.LENGTH_SHORT).show();
			} else {
				if (appState.selectedPost > -1) {
					postObject = jsonParser
							.buildTextResponseWithParentObject(message
									.getText().toString(),
									appState.selectedPost);

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
					record.setImageResource(R.drawable.gravador_parado);
					stopWatch.stop();
					timeUp.setText("00:00");

					getWindow()
							.clearFlags(
									android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
					e.printStackTrace();
				}

				catch (Exception e) {
					record.setImageResource(R.drawable.gravador_parado);
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

		String url = "discussions/" + appState.selectedDiscussion
				+ "/posts?auth_token=" + appState.getToken();

		connection.postToServer(Constants.CONNECTION_POST_TEXT_RESPONSE,
				jsonString, url);

	}

	public void getNewPosts(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_NEW_POSTS, url,
				appState.getToken());

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

	@Override
	@SuppressWarnings("unchecked")
	public void resultFromConnection(int connectionId, String result,
			int statusCode) {

		if (statusCode != 200 && statusCode != 201) {
			switch (connectionId) {
			case Constants.CONNECTION_POST_AUDIO:
				Toast.makeText(getApplicationContext(),
						"Erro no envio de áudio", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getApplicationContext(),
						PostsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				MobilisPreferences status = MobilisPreferences
						.getInstance(this);
				status.ids = postDAO
						.getIdsOfPostsWithoutImage(appState.selectedDiscussion);
				startActivity(intent);
				break;

			default:
				ErrorHandler.handleStatusCode(this, statusCode);
				progressDialog.dismiss();
				break;
			}

		} else {
			switch (connectionId) {

			case Constants.CONNECTION_POST_AUDIO:
				currentDiscussion = discussionDAO
						.getDiscussion(appState.selectedDiscussion);

				currentDiscussion
						.setNextPosts(currentDiscussion.getNextPosts() + 1);
				discussionDAO.updateDiscussion(currentDiscussion);

				intent = new Intent(getApplicationContext(),
						PostsActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				MobilisPreferences status = MobilisPreferences
						.getInstance(this);
				status.ids = postDAO
						.getIdsOfPostsWithoutImage(appState.selectedDiscussion);
				startActivity(intent);
				break;

			case Constants.CONNECTION_POST_TEXT_RESPONSE:
				Log.w("TEXT RESPONSE RESULT", result);

				ArrayList<Integer> resultFromServer;

				resultFromServer = (ArrayList<Integer>) jsonParser.parseJSON(
						result, Constants.PARSE_TEXT_RESPONSE_ID);

				if (existsRecording) {
					Log.i(TAG, "Exists Recording");
					int postId = resultFromServer.get(1);
					sendAudioPost(Constants.generateAudioResponseURL(postId),
							recorder.getAudioFile(), appState.getToken());

				} else {
					Log.i(TAG, "No recording");
					currentDiscussion = discussionDAO
							.getDiscussion(appState.selectedDiscussion);
					currentDiscussion.setNextPosts(currentDiscussion
							.getNextPosts() + 1);

					discussionDAO.updateDiscussion(currentDiscussion);

					intent = new Intent(getApplicationContext(),
							PostsActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

					startActivity(intent);
				}
				break;

			default:
				break;
			}
		}
	}

	@Override
	public void positiveButtonClicked() {
		Toast.makeText(getApplicationContext(), "Mensagem descartada",
				Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	public void negativeButtonClicked() {
		Toast.makeText(getApplicationContext(), "onHandlerNegative",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRecordingDeleted() {
		deleteRecording();
	}
}
