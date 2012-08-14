package com.mobilis.audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Handler;
import com.mobilis.controller.R;
import com.mobilis.model.Post;
import com.mobilis.util.Constants;

public class PostPlayer implements Runnable, OnCompletionListener,
		OnErrorListener {
	private MediaPlayer mediaPlayer;
	private Context context;
	private int currentBlockIndex = 0;
	private int lastAvailableBlockIndex = -1;
	private int blocksNumber;
	private boolean isPaused = false;
	private Handler handler;
	private boolean isLastPost;
	public static final String TAG = "POSTPLAYER";
	public static final int MAX_TIME_TO_SLEEP = 1800;

	public static final int PLAYER_STOPPED = 1;
	public static final int PLAYER_PAUSED = 2;
	public static final int PLAYER_UNITIALIZED = 3;
	public boolean isPrepared = false;
	private long playingDuration = 0;

	String getBlockAtIndex(int index) {
		if (index == 0)
			return getHeader();
		else
			return "Paga a parte do blockpositions";

	}

	public PostPlayer(int numberOfBlocks, Handler handler, Post post,
			Context context, boolean isLastPost) {
		super();
		this.isLastPost = isLastPost;
		this.context = context;
		this.handler = handler;
		blocksNumber = numberOfBlocks - 1;
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
	}

	int blocksCount() {
		return 0;
	}

	void playEndSignalAudio() {
	}

	void playWaitingSignalAudio() {
	}

	String getHeader() {
		return null;
	}

	public void play() {
		synchronized (this) {
			try {
				if (currentBlockIndex <= lastAvailableBlockIndex) {
					if (!mediaPlayer.isPlaying()) {
						this.wait();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		try {
			if (mediaPlayer.isPlaying())
				mediaPlayer.stop();
			currentBlockIndex = 0;
			lastAvailableBlockIndex = -1;
		} catch (IllegalStateException e) {
		}
	}

	public void pause() {
		mediaPlayer.pause();
		isPaused = true;
	}

	@Override
	public void run() {

		playingDuration = System.currentTimeMillis();
		while (blocksNumber >= currentBlockIndex && !Thread.interrupted()) {
			if (!mediaPlayer.isPlaying() && !isPaused) {
				try {
					this.play();
					Thread.sleep(MAX_TIME_TO_SLEEP);
				} catch (IllegalStateException e) {
					handler.sendEmptyMessage(Constants.ERROR_PLAYING);
					e.printStackTrace();
					return;
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (lastAvailableBlockIndex != -1 && !Thread.interrupted()) {
			handler.sendEmptyMessage(Constants.PLAY_NEXT_POST);
		} else
			handler.sendEmptyMessage(Constants.STOP_AUDIO);
	}

	public void playSoundEffect() {
		if (isLastPost) {
			mediaPlayer = MediaPlayer.create(context, R.raw.fimdalista);
			mediaPlayer.start();
		} else {
			mediaPlayer = MediaPlayer.create(context, R.raw.proxpost);
			mediaPlayer.start();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		currentBlockIndex++;
		mp.stop();
		mp.reset();
		if (currentBlockIndex > blocksNumber) {
			playingDuration = System.currentTimeMillis() - playingDuration;
			mp.release();
		}
	}

	public void addBlock() {
		lastAvailableBlockIndex++;
	}

	public void playAfterPause() {
		isPaused = false;
		mediaPlayer.start();
	}

	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		return false;
	}

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
}
