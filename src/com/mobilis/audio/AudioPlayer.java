package com.mobilis.audio;

import java.io.File;
import java.io.IOException;

import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.mobilis.util.Constants;

public class AudioPlayer {
	private MediaPlayer player;
	private boolean isPrepared = false, isPaused = false;
	public playOnBackgroundThread playerThread;
	File recordedAudioPath = new File(Constants.PATH_RECORDINGS
			+ Constants.RECORDING_FULLNAME);

	public void playOwnAudio() throws IllegalArgumentException,
			IllegalStateException, IOException {

		if (!isPrepared)
			prepare();
		play();
	}

	public void prepare() throws IllegalArgumentException,
			IllegalStateException, IOException {

		if (player == null) {
			player = new MediaPlayer();
			player.setDataSource(recordedAudioPath.getAbsolutePath());
		}
		player.prepare();
		isPrepared = true;

	}

	public MediaPlayer getPlayerInstance() {
		return player;
	}

	public int getAudioDuration() {
		if (isPrepared)
			return player.getDuration() / 1000;
		else
			return 0;
	}

	public int getCurrentPosition() {
		return player.getCurrentPosition();
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

	public boolean isPaused() {
		return isPaused;
	}

	public boolean isPlaying() {
		return player.isPlaying();
	}

	public void play() throws IllegalStateException, IOException {
		if (player.isPlaying())

			return;

		else {
			synchronized (this) {
				playerThread = new playOnBackgroundThread();
				player.start();
			}
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
			synchronized (this) {
				isPrepared = false;
			}
		}
	}

	public void pause() {

		if (isPaused) {
			player.start();
			isPaused = false;
		} else {
			player.pause();
			isPaused = true;
		}
	}

	public int getProgress() {
		return player.getCurrentPosition() / 1000;
	}

	private class playOnBackgroundThread extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			player.start();
			while (player.isPlaying() || isPaused()) {
				onProgressUpdate(player.getCurrentPosition());
			}
			return null;

		}
	}
}
