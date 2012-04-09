package com.mobilis.controller;

import java.io.File;
import java.io.IOException;

import org.json.simple.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
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
import com.mobilis.dialog.AudioDialog;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.model.DBAdapter;
import com.mobilis.threads.RequestNewPostsThread;
import com.mobilis.threads.RequestPostsThread;
import com.mobilis.threads.SubmitAudioResponseThread;
import com.mobilis.threads.SubmitTextResponseThread;

public class ResponseController extends Activity implements OnClickListener,
		OnChronometerTickListener, OnCompletionListener, TextWatcher,
		OnInfoListener {

	private EditText message;
	private Button submit;
	private TextView timeUp, charCount;
	private ImageButton record;
	private ProgressDialog dialog;
	private Bundle extras;
	private SubmitTextResponse submitTextResponse;
	private DBAdapter adapter;
	private String token, URL, topicId, forumName;
	private ParseJSON jsonParser;
	private Intent intent;
	private JSONObject postObject;
	private SubmitAudio submitAudio;
	private RequestPosts requestPosts;
	private boolean existsRecording = false;
	private RequestNewPosts requestNewPosts;
	public SharedPreferences settings;
	private String charSequenceAfter;
	private DialogMaker dialogMaker;
	private ImageView recordImage;

	private long countUp;
	private long startTime;
	private Chronometer stopWatch;

	private AudioRecorder recorder;
	private AudioPlayer player;
	public static DialogInterface.OnClickListener dialogClick;

	ResponseControllerHandler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		handler = new ResponseControllerHandler(this);
		dialogMaker = new DialogMaker(this);

		setContentView(R.layout.response);
		extras = getIntent().getExtras();
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

		jsonParser = new ParseJSON(this);

		if (extras != null) {

			topicId = extras.getString("topicId");
			Log.w("TopicId", topicId);
			forumName = extras.getString("ForumName");

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

				if (extras.getLong("parentId") > 0) {

					postObject = jsonParser.buildTextResponseWithParentObject(
							message.getText().toString(),
							extras.getLong("parentId"));

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
		adapter = new DBAdapter(this);
		adapter.open();
		token = adapter.getToken();
		adapter.close();

		URL = "discussions/" + settings.getString("SelectedTopic", null)
				+ "/posts?auth_token=" + token;

		submitTextResponse = new SubmitTextResponse(this);
		submitTextResponse.setConnectionParameters(URL, jsonString);
		submitTextResponse.execute();

	}

	public void getPosts(String URLString) {

		requestPosts = new RequestPosts(this);
		adapter.open();
		requestPosts.setConnectionParameters(URLString, adapter.getToken());
		adapter.close();
		requestPosts.execute();

	}

	public void getNewPosts(String urlString) {
		requestNewPosts = new RequestNewPosts(this);
		adapter.open();

		requestNewPosts.setConnectionParameters(urlString, adapter.getToken());
		adapter.close();
		requestNewPosts.execute();
	}

	public void sendAudioPost(String URLString, File audioFile) {
		Log.w("ONSENDINGAUDIO", "TRUE");
		submitAudio = new SubmitAudio(this);
		submitAudio.setConnectionParameters(URLString, audioFile);
		submitAudio.execute();

	}

	public class SubmitTextResponse extends SubmitTextResponseThread {

		public SubmitTextResponse(Context context) {
			super(context);

		}

		@Override
		public void onTextResponseConnectionFailed() {
			closeDialogIfItsVisible();
		}

		@Override
		public void onTextResponseConnectionSucceded(String result) {

			Log.w("RESULT", result);

			ContentValues[] resultFromServer;
			jsonParser = new ParseJSON(getApplicationContext());

			resultFromServer = jsonParser.parseJSON(result,
					Constants.PARSE_TEXT_RESPONSE_ID);
			Log.w("RESULTNEW",
					String.valueOf(resultFromServer[0].get("result")));
			Log.w("POST_IDNEW",
					String.valueOf(resultFromServer[0].get("post_id")));

			if (existsRecording) {

				adapter.open();
				String postURL = "posts/"
						+ String.valueOf(resultFromServer[0].get("post_id"))
						+ "/attach_file?auth_token=" + adapter.getToken();
				adapter.close();
				Log.w("PostURL", postURL);
				sendAudioPost(postURL, recorder.getAudioFile());

			} else {
				Log.w("getPosts", "TRUE");

				getNewPosts("discussions/"
						+ settings.getString("SelectedTopic", null) + "/posts/"
						+ Constants.oldDateString + "/news.json");
			}
		}
	}

	public class SubmitAudio extends SubmitAudioResponseThread {

		public SubmitAudio(Context context) {
			super(context);

		}

		@Override
		public void onAudioResponseConnectionFailed() {
			closeDialogIfItsVisible();

		}

		@Override
		public void onAudioResponseConnectionSucceded(String result) {
			getNewPosts("discussions/"
					+ settings.getString("SelectedTopic", null) + "/posts/"
					+ Constants.oldDateString + "/news.json");
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
			adapter.updatePostsFromTopic(result,
					Long.parseLong(settings.getString("SelectedTopic", null)));
			adapter.close();

			intent = new Intent(getApplicationContext(), PostList.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
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
			intent = new Intent(getApplicationContext(), PostList.class);
			intent.putExtra("ForumName", forumName);
			intent.putExtra("PostList", (String) result);
			intent.putExtra("topicId", topicId);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("TESTE", "TESTE");
			startActivity(intent);

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

	}

	@Override
	public void afterTextChanged(Editable editable) {

		charCount.setText(String.valueOf(charSequenceAfter.length()));

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
		Log.w("charSequenceOnBefore", s.toString());
		Log.w("start", String.valueOf(start));
		Log.w("count", String.valueOf(count));
		Log.w("after", String.valueOf(after));
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Log.w("start", String.valueOf(start));
		Log.w("before", String.valueOf(before));
		Log.w("count", String.valueOf(count));
		Log.w("charSequenceOnAfter", s.toString());
		charSequenceAfter = s.toString();
	}

	@Override
	public void onInfo(MediaRecorder mr, int what, int extra) {
		Log.w("OnInfoListener", "TRUE");
	}

}
