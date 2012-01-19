package com.paulo.solarmobile.audio;

import java.io.File;
import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;

public abstract class PlayAudio implements OnCompletionListener {
	MediaPlayer player;
	File directoryPath;
	boolean isPrepared = false;

	public PlayAudio(String fileName) throws IllegalStateException, IOException {
		File path = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Mobilis/Recordings/");

		player.setDataSource(path + "/recording" + fileName + ".3gp");
		player.prepare();
		isPrepared = true;
		player.setOnCompletionListener(this);
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

	public void play() throws IllegalStateException, IOException {
		if (player.isPlaying())
			return;

		synchronized (this) {
			if (!isPrepared)
				player.prepare();
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
			synchronized (this) {
				isPrepared = false;
			}
		}
	}

	public void pause() {
		player.pause();
	}

	public abstract void updatePlayingDialog();
}