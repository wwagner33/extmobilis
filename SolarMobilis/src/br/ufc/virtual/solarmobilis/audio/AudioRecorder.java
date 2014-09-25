package br.ufc.virtual.solarmobilis.audio;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class AudioRecorder {
	private static final String PATH_RECORDINGS = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/Mobilis/Recordings/";
	public static final String RECORDING_FILE_NAME = "mobilis_audio";
	public static final String RECORDING_EXTENSION = ".aac";
	public static final String RECORDING_FULL_NAME = RECORDING_FILE_NAME
			+ RECORDING_EXTENSION;
	public static final String RECORDING_FULL_FILE_PATH = PATH_RECORDINGS
			+ RECORDING_FULL_NAME;
	public static final String RECORDING_MIME_TYPE = "audio/aac";

	public static final int RECORDING_STARTED = 1;
	public static final int RECORDING_STOPPED = 2;
	// private File path = new File(Environment.getExternalStorageDirectory()
	// .getAbsolutePath() + Constants.PATH_RECORDINGS);
	private File path = new File(PATH_RECORDINGS);
	private File audioFile;
	private MediaRecorder recorder;
	private RecordOnBackground recordOnBackgroundThread;
	public boolean isRecording = false;
	public boolean isPrepared = false;
	public boolean isReseted = false;

	public AudioRecorder() {
		recorder = new MediaRecorder();
		setRecordOnBackground();
	}

	private void setRecordOnBackground() {
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		recordOnBackgroundThread = new RecordOnBackground();
	}

	public void startRecording(String fileName) throws IllegalStateException,
			IOException {

		if (!path.exists()) {
			path.mkdirs();
		}

		if (isReseted) {
			recorder.reset();
			setRecordOnBackground();
		}

		audioFile = new File(path, RECORDING_FULL_NAME);
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
