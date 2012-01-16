package com.paulo.solarmobile.audio;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;

public abstract class RecordAudio {

	public static final int RECORDING_STARTED = 1;
	public static final int RECORDING_STOPPED = 2;

	File path = new File(Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/Mobilis/Recordings/");
	File audioFile;
	MediaRecorder recorder;
	RecordOnBackground recordOnBackgroundThread;

	public void startRecording(String fileName) throws IllegalStateException,
			IOException {
		updateRecordingDialog(RECORDING_STARTED);
		recordOnBackgroundThread = new RecordOnBackground();
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		if (!path.exists()) {
			path.mkdir();
		}

		audioFile = new File(path, fileName + ".3gp");
		recorder.setOutputFile(audioFile.getAbsolutePath());

		recorder.prepare();
		recordOnBackgroundThread.execute();
	}

	public void stopRecording() {
		recorder.stop();
		recorder.release();
		recordOnBackgroundThread.cancel(true);
		updateRecordingDialog(RECORDING_STOPPED);

	}

	public String getAudioFilePath() {
		return audioFile.getAbsolutePath();
	}

	public abstract void updateRecordingDialog(int protocol);

	public class RecordOnBackground extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			recorder.start();
			return null;
		}
	}
}
