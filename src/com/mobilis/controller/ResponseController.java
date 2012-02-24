package com.mobilis.controller;

import java.io.File;
import java.io.IOException;

import org.json.simple.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilis.audio.PlayAudio;
import com.mobilis.audio.RecordAudio;
import com.mobilis.model.DBAdapter;
import com.mobilis.threads.RequestPostsThread;
import com.mobilis.threads.SubmitAudioResponseThread;
import com.mobilis.threads.SubmitTextResponseThread;
//import com.paulo.android.solarmobile.controller.R;

public class ResponseController extends Activity implements OnClickListener,
		OnChronometerTickListener, OnCompletionListener, TextWatcher,
		OnInfoListener {

	private EditText message;
	private Button submit, cancelar, deleteRecording, previewAudio;
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
	private RelativeLayout audioPreviewBar;
	private RequestPosts requestPosts;
	private boolean existsRecording = false;

	private String charSequenceAfter, charSequenceBefore;

	private long countUp;
	private long startTime;
	private Chronometer stopWatch;

	private RecordAudio recorder;
	private PlayAudio player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.answer_topic);
		extras = getIntent().getExtras();
		dialog = Dialogs.getProgressDialog(this);
		submit = (Button) findViewById(R.id.criar_topico_submit);
		submit.setOnClickListener(this);
		message = (EditText) findViewById(R.id.criar_topico_conteudo);
		message.addTextChangedListener(this);
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

		charCount = (TextView) findViewById(R.id.char_number);
		// charCount.addTextChangedListener(this);

		jsonParser = new ParseJSON();

		if (extras != null) {

			topicId = extras.getString("topicId");

			forumName = extras.getString("ForumName");

		}

	}

	@Override
	protected void onResume() {

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
		File recordedFile = new File(Constants.RECORDING_PATH + Constants.RECORDING_FULLNAME);
		recordedFile.delete();
//		recorder.getAudioFile().delete();
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
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
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

			if (extras.getLong("parentId") > 0) {

				postObject = jsonParser.buildTextResponseWithParentObject(
						message.getText().toString(),
						extras.getLong("parentId"));

			} else {
				postObject = jsonParser.buildTextResponseWithoutParent(message
						.getText().toString());
			}

			sendPost(postObject.toJSONString());

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
					existsRecording = true;
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
					stopWatch.stop();
					timeUp.setText("00:00");
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

		URL = "discussions/" + topicId + "/posts?auth_token=" + token;

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
			jsonParser = new ParseJSON();
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
				getPosts(Constants.URL_DISCUSSION_PREFIX + topicId
						+ Constants.URL_POSTS_SUFFIX);
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
			getPosts(Constants.URL_DISCUSSION_PREFIX + topicId
					+ Constants.URL_POSTS_SUFFIX);
			closeDialogIfItsVisible();

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
		/*
		 * if (sizeBefore>sizeAfter) { charCount++;
		 * charCountText.setText(String.valueOf(charCount)); } else {
		 * charcount-- charcountText.setText(String.valueOf(charCount)); }
		 * 
		 * // charCount.setText()
		 */

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		Log.w("charSequenceOnBefore", s.toString());
		Log.w("start", String.valueOf(start));
		Log.w("count", String.valueOf(count));
		Log.w("after", String.valueOf(after));
		charSequenceBefore = s.toString();
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
