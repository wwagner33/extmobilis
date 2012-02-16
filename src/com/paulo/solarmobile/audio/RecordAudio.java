package com.paulo.solarmobile.audio;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.paulo.android.solarmobile.controller.Constants;

public class RecordAudio {

	public static final int RECORDING_STARTED = 1;
	public static final int RECORDING_STOPPED = 2;
	private File path = new File(Environment.getExternalStorageDirectory()
			.getAbsolutePath() + Constants.RECORDING_PATH);
	private File audioFile;
	private MediaRecorder recorder;
	private RecordOnBackground recordOnBackgroundThread;
	public boolean isRecording = false;
	public boolean isPrepared = false;
	public boolean isReseted = false;

	public RecordAudio() {

		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recordOnBackgroundThread = new RecordOnBackground();

	}

	public void startRecording(String fileName) throws IllegalStateException,
			IOException {

		if (!path.exists()) {
			path.mkdir();
		}

		if (isReseted) {
			recorder.reset();
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			recordOnBackgroundThread = new RecordOnBackground();
		}

		audioFile = new File(path, Constants.RECORDING_FILENAME + ".3gp");
		recorder.setOutputFile(audioFile.getAbsolutePath());

		recorder.prepare();
		recordOnBackgroundThread.execute();
		isRecording = true;
	}

	public void stopRecording() {
		recorder.stop();
		Log.w("RECORDER", "STOP");
		// recorder.release();
		isReseted = true;
		recordOnBackgroundThread.cancel(true);
		isRecording = false;

	}

	public void releaseRecording() {
		recorder.release();
	}

	public String getAudioFilePath() {
		return audioFile.getAbsolutePath();
	}

	public boolean getRecordingState() {
		return isRecording;
	}

	public class RecordOnBackground extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			recorder.start();
			return null;
		}
	}

	public File getAudioFile() {
		return audioFile;
	}

}
