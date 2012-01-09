package com.paulo.solarmobile.audio;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Environment;

public abstract class RecordAudio {
	
		public static final int RECORDING_STARTED = 1;
		public static final int RECORDING_STOPPED = 2;
	
	File path = new File(Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/Mobilis/Recordings/");
		File audioFile;
		MediaRecorder recorder;
		
	public void startRecording(String fileName) {
		updateRecordingDialog(RECORDING_STARTED);
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		
		if (!path.exists()) {
			path.mkdir();
		}
		
		audioFile = new File(path + "arquivo"+fileName+".3gp");
		recorder.setOutputFile(audioFile.getAbsolutePath());
		
		try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stopRecording() {
		updateRecordingDialog(2);
		
	}
	
	public abstract void updateRecordingDialog(int protocol);
}
