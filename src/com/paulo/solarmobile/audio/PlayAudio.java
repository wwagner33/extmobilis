package com.paulo.solarmobile.audio;

import java.io.File;
import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Environment;

import com.paulo.android.solarmobile.controller.Constants;

public class PlayAudio {
	MediaPlayer player;
	// File directoryPath;
	boolean isPrepared = false;
	playOnBackgroundThread playerThread;
	volatile int progress = 0;

	/*
	 * public PlayAudio(String fileName) throws IllegalStateException,
	 * IOException { File path = new
	 * File(Environment.getExternalStorageDirectory() .getAbsolutePath() +
	 * "/Mobilis/Recordings/");
	 * 
	 * player.setDataSource(path + "/recording" + fileName + ".3gp");
	 * player.prepare(); isPrepared = true;
	 * player.setOnCompletionListener(this); }
	 */

	public void playOwnAudio() throws IllegalArgumentException,
			IllegalStateException, IOException {
		File recordedAudioPath = new File(Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ Constants.RECORDING_PATH + Constants.RECORDING_FULLNAME);
		player = new MediaPlayer();
		player.setDataSource(recordedAudioPath.getAbsolutePath());
		player.prepare();
		play();
		// player.setOnCompletionListener(this);

	}

	public void dispose() {
		if (player.isPlaying() == true) {
			player.stop();
		}
		player.release();
	}

	public boolean isLooping() {
		return player.isLooping();
	}

	public boolean isStopped() {
		return !isPrepared;
	}

	public boolean isPlaying() {
		return player.isPlaying();
	}

	public void play() throws IllegalStateException, IOException {
		if (player.isPlaying())

			return;

		synchronized (this) {
			playerThread = new playOnBackgroundThread();
			player.start();
		}
	}

	public void setLooping(boolean isLooping) {
		player.setLooping(isLooping);
	}

	public void setVolume(float volume) {
		player.setVolume(volume, volume);
	}

	public void stop() {
		if (player.isPlaying()) {
			player.stop();
			// cancel thread
			synchronized (this) {
				isPrepared = false;
			}
		}
	}

	public void pause() {
		player.pause();
	}

	public int getProgress() {
		return progress;
	}

	private class playOnBackgroundThread extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			player.start();
			while (player.isPlaying()) {
				onProgressUpdate(player.getCurrentPosition());
			}
			return null;

		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			progress = values[0];
		}

	}
}
