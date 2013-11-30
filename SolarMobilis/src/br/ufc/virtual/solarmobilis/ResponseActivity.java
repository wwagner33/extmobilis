package br.ufc.virtual.solarmobilis;

import java.io.File;
import java.io.IOException;

import org.springframework.web.client.HttpStatusCodeException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import br.ufc.virtual.solarmobilis.audio.AudioPlayer;
import br.ufc.virtual.solarmobilis.model.DiscussionPost;
import br.ufc.virtual.solarmobilis.model.SendPostResponse;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@EActivity(R.layout.activity_response)
public class ResponseActivity extends Activity {

	@Pref
	SolarMobilisPreferences_ preferences;

	@Bean
	SolarManager solarManager;

	DiscussionPost discussionPost = new DiscussionPost();
	PostSender postSender = new PostSender();

	@ViewById(R.id.editTextReply)
	EditText reply;

	@ViewById(R.id.record_button)
	ImageButton recordButton;

	@ViewById(R.id.play_button)
	ImageButton playButton;

	@Extra("discussionId")
	Integer discussionId;

	private ProgressDialog dialog;

	AudioPlayer player = new AudioPlayer();
	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;
	private static String mFileName = null;
	private static final String LOG_TAG = "AudioRecordTest";
	Boolean start = true;
	boolean mStartPlaying = true;
	boolean mStartRecording = true;
	File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		recorderConfig();
	}

	void recorderConfig() {
		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		mFileName += "/Mobilis/Recordings/mobilis_audio.mp4";
		file = new File(mFileName);

		if (file.exists()) {
			file.delete();
		}
	}

	@Click(R.id.submitReply)
	void submit() {
		discussionPost.setContent(reply.getText().toString());
		discussionPost.setDiscussionId(discussionId);
		postSender.setDiscussionPost(discussionPost);

		dialog = ProgressDialog.show(this, getString(R.string.dialog_wait),
				getString(R.string.dialog_sending), true);

		sendPost();
	}

	@Click(R.id.record_button)
	void onRecordClick() {
		onRecord(mStartRecording);
		if (mStartRecording) {
			// setText("Stop recording");
		} else {
			// setText("Start recording");
		}
		mStartRecording = !mStartRecording;
	}

	void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
		}
	}

	private void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		mRecorder.setOutputFile(mFileName);
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}

		mRecorder.start();
	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.reset();
		mRecorder.release();
		mRecorder = null;
	}

	@Click(R.id.play_button)
	public void onPlayClick() {
		onPlay(mStartPlaying);
		if (mStartPlaying) {
			// setText("Stop playing");
		} else {
			// setText("Start playing");
		}
		mStartPlaying = !mStartPlaying;
	}

	private void onPlay(boolean start) {
		if (start) {
			startPlaying();
		} else {
			stopPlaying();
		}
	}

	private void startPlaying() {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(mFileName);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
	}

	private void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
	}

	@Background
	void sendPost() {
		try {
			SendPostResponse sendPostResponse = solarManager.sendPost(
					postSender, discussionId);
			if (file.exists()) {
				sendPostAudio(sendPostResponse.getPostId());
				file.delete(); // TODO: Verificar se essa lógica fica
			}
			toast();
		} catch (HttpStatusCodeException e) {
			Log.i("ERRO HttpStatusCodeException", e.getStatusCode().toString());
			solarManager.errorHandler(e.getStatusCode());
		} catch (Exception e) {
			Log.i("ERRO Exception", e.getMessage());
			solarManager.alertNoConnection();
		} finally {
			dialog.dismiss();
		}
	}

	private void sendPostAudio(Integer postId) {
		Object object = solarManager.sendPostAudio(file, postId);
		object.toString();
	}

	@UiThread
	void toast() {
		Toast.makeText(this, R.string.send_post_sucess, Toast.LENGTH_SHORT)
				.show();
		finish();
	}
}