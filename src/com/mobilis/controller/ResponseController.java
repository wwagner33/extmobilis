package com.mobilis.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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
import com.mobilis.dao.PostDAO;
import com.mobilis.dialog.AudioDialog;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.util.ParseJSON;
import com.mobilis.util.ZipManager;
import com.mobilis.ws.Connection;

public class ResponseController extends Activity implements OnClickListener,
		OnChronometerTickListener, OnCompletionListener, TextWatcher,
		OnInfoListener {

	private EditText message;
	private Button submit;
	private TextView timeUp, charCount;
	private ImageButton record;
	private ProgressDialog dialog;
	private ParseJSON jsonParser;
	private Intent intent;
	private JSONObject postObject;
	private boolean existsRecording = false;
	public SharedPreferences settings;
	private String charSequenceAfter;
	private DialogMaker dialogMaker;
	private ImageView recordImage;
	private long countUp;
	private long startTime;
	private Chronometer stopWatch;
	private AudioRecorder recorder;
	private AudioPlayer player;
	float toastTimer = 0;
	private PostDAO postDAO;
	private ZipManager zipManager;

	private ResponseHandler handler;
	private Connection connection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.response);
		zipManager = new ZipManager();
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		handler = new ResponseHandler();
		connection = new Connection(handler, this);

		dialogMaker = new DialogMaker(this);
		postDAO = new PostDAO(this);

		dialog = dialogMaker
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

		settings = PreferenceManager.getDefaultSharedPreferences(this);

		charCount = (TextView) findViewById(R.id.char_number);

		charCount.setText("0/" + Constants.TEXT_MAX_CHARACTER_LENGHT);

		jsonParser = new ParseJSON(this);

	}

	@Override
	protected void onResume() {

		super.onResume();
		recorder = new AudioRecorder();
	}

	@Override
	protected void onStop() {

		super.onStop();
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
		if (recorder != null)
			recorder.releaseRecording();

	}

	@Override
	public void onBackPressed() {

		if (message.length() > 0 || existsRecording) {
			AlertDialog alertDialog = dialogMaker.makeAlertDialog(
					Constants.DIALOG_ALERT_DISCARD, handler);
			alertDialog.show();
		} else {
			super.onBackPressed();
		}
	}

	public void closeDialogIfItsVisible() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();

	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.record_image) {

			player = new AudioPlayer();
			AudioDialog audioDialog = new AudioDialog(this, player, handler);
			audioDialog.show();
		}

		if (v.getId() == R.id.criar_topico_submit) {

			if (!existsRecording && message.getText().length() == 0) {

				Toast.makeText(this, "Mensagem não pode ser vazia",
						Toast.LENGTH_SHORT).show();

			}

			else {

				if (settings.getLong("SelectedPost", 0) > 0) {

					postObject = jsonParser.buildTextResponseWithParentObject(
							message.getText().toString(),
							settings.getLong("SelectedPost", 0));

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
		dialog.show();
		String token = settings.getString("token", null);

		String url = "discussions/" + settings.getInt("SelectedTopic", 0)
				+ "/posts?auth_token=" + token;

		connection.postToServer(Constants.CONNECTION_POST_TEXT_RESPONSE,
				jsonString, url);

	}

	public void getNewPosts(String url) {

		connection.getFromServer(Constants.CONNECTION_GET_NEW_POSTS, url,
				settings.getString("token", null));

	}

	public void sendAudioPost(String url, File audioFile) {

		connection
				.postToServer(Constants.CONNECTION_POST_AUDIO, url, audioFile);
	}

	public void getImages(String url) {
		connection.getImages(Constants.CONNECTION_GET_IMAGES, url,
				settings.getString("token", null));
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
				closeDialogIfItsVisible();
			}

			if (msg.what == Constants.MESSAGE_TEXT_RESPONSE_OK) {

				Log.w("RESULT", msg.getData().getString("content"));

				ContentValues[] resultFromServer;
				jsonParser = new ParseJSON(getApplicationContext());
				resultFromServer = jsonParser
						.parseJSON(msg.getData().getString("content"),
								Constants.PARSE_TEXT_RESPONSE_ID);

				if (existsRecording) {

					String postURL = "posts/"
							+ String.valueOf(resultFromServer[0].get("post_id"))
							+ "/attach_file?auth_token="
							+ settings.getString("token", null);
					Log.w("PostURL", postURL);
					sendAudioPost(postURL, recorder.getAudioFile());

				} else {

					getNewPosts("discussions/"
							+ settings.getInt("SelectedTopic", 0) + "/posts/"
							+ Constants.oldDateString + "/news.json");
				}

			}

			if (msg.what == Constants.MESSAGE_AUDIO_POST_OK) {

				getNewPosts("discussions/"
						+ settings.getInt("SelectedTopic", 0) + "/posts/"
						+ Constants.oldDateString + "/news.json");
				closeDialogIfItsVisible();

			}

			if (msg.what == Constants.MESSAGE_NEW_POST_CONNECTION_OK) {

				ArrayList<ContentValues> values = jsonParser.parsePosts(msg
						.getData().getString("content"));

				postDAO.open();
				postDAO.addPosts(values, settings.getInt("SelectedTopic", 0));

				intent = new Intent(getApplicationContext(), PostList.class);

				try {
					String ids = postDAO.getUserIdsAbsentImage(settings.getInt(
							"SelectedTopic", 0));
					postDAO.close();
					getImages("images/" + ids + "/users");
					Log.i("Alguns usuários não possuem imagens", "TRUE");
				} catch (StringIndexOutOfBoundsException e) {
					closeDialogIfItsVisible();
					postDAO.close();
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					Log.i("Não é preciso Baixar novas imagens", "TRUE");
				} catch (NullPointerException e) {
					Log.i("É preciso baixar todas as imagens", "TRUE");
					String ids = postDAO.getAllUserIds();
					postDAO.close();
					getImages("images/" + ids + "/users");
				}
			}

			if (msg.what == Constants.MESSAGE_IMAGE_CONNECTION_OK) {
				zipManager.unzipFile();
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		}
	}
}
