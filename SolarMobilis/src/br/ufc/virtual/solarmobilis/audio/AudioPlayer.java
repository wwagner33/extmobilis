package br.ufc.virtual.solarmobilis.audio;

import java.io.IOException;

import android.media.MediaPlayer;

public class AudioPlayer {
	
	private MediaPlayer.OnCompletionListener onCompletion;
	private boolean isPrepared = false, isPaused = false;
	private String filePath;
    private MediaPlayer player = new MediaPlayer();
	
	
	@Deprecated
	public AudioPlayer(String filePath) {
		this.filePath = filePath;
	}

	public AudioPlayer() {
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void playOwnAudio() throws IllegalArgumentException,
			IllegalStateException, IOException {

		if (!isPrepared)
			prepare();
		play();
	}

	public void prepare() throws IllegalArgumentException,
			IllegalStateException, IOException {
        /*
		if (player == null) {
			player = new MediaPlayer();
		} */
		player.setDataSource(filePath);
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

		if (!isPrepared)
			prepare();

		if (player.isPlaying())
			return;

		else {
			synchronized (this) {
				if (hasCompletionListener()) {
					player.setOnCompletionListener(onCompletion);
				}
				player.start();
			}
		}
	}

	public void play(String filePath,
			MediaPlayer.OnCompletionListener onCompletion)
			throws IllegalStateException, IOException {
		setFilePath(filePath);
		this.onCompletion = onCompletion;
		play();
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

	public void reset() {
		if (player != null) {
			player.reset();
			isPrepared = false;
		}
	}

	public int getProgress() {
		return player.getCurrentPosition() / 1000;
	}

	private boolean hasCompletionListener() {
		return onCompletion != null;
	}

}
