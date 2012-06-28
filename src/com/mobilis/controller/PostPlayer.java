package com.mobilis.controller;

import java.io.IOException;

import com.mobilis.model.DiscussionPost;
import com.mobilis.util.Constants;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.util.Log;

public class PostPlayer implements Runnable, OnCompletionListener {
	private DiscussionPost post;
	private MediaPlayer mediaPlayer;
	private Context context;
	private int currentBlockIndex = 0; // Deve ser thread safe
	private int lastAvailableBlockIndex = -1; // Deve ser thread safe
	private int blocksNumber;
	private boolean isPaused = false;
	private Handler handler;
	private boolean isLastPost;

	String getBlockAtIndex(int index) {
		if (index == 0)
			return getHeader();
		else
			return "Paga a parte do blockpositions";
	}

	public PostPlayer(int numberOfBlocks, Handler handler, DiscussionPost post,
			Context context, boolean isLastPost) {
		super();
		this.isLastPost = isLastPost;
		this.context = context;
		this.handler = handler;
		this.post = post;
		blocksNumber = numberOfBlocks - 1;
		Log.w("Blocks Number", "" + blocksNumber);
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
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

	void play() throws IllegalStateException, IOException, InterruptedException {
		synchronized (this) {
			if (currentBlockIndex <= lastAvailableBlockIndex) {
				Log.w("Current", "" + currentBlockIndex);
				Log.w("last", "" + lastAvailableBlockIndex);
				Log.w("Number total", "" + blocksNumber);
				if (!mediaPlayer.isPlaying()) {
					Log.e("Player", "Tocando");
					mediaPlayer.setDataSource(Constants.AUDIO_DEFAULT_PATH
							+ post.getId() + "/" + currentBlockIndex + ".mp3");
					mediaPlayer.prepare();
					mediaPlayer.start();

				}
			} else {
				if (currentBlockIndex < blocksNumber) {
					Log.e("Player", "Aguardando novo audio");
					this.wait();
				}
			}
		}
	}

	void stop() {
		try {
			if (mediaPlayer.isPlaying())
				mediaPlayer.stop();
			currentBlockIndex = 0;
			lastAvailableBlockIndex = -1;
		} catch (IllegalStateException e) {
		}
	}

	void pause() {
		mediaPlayer.pause();
		isPaused = true;
	}

	@Override
	public void run() {
		while (blocksNumber >= currentBlockIndex && !Thread.interrupted()) {
			if (!mediaPlayer.isPlaying() && !isPaused) {
				try {
					this.play();
					Thread.sleep(Constants.MAX_TIME_TO_SLEEP);
				} catch (IllegalStateException e) {
					handler.sendEmptyMessage(Constants.ERROR_PLAYING);
					e.printStackTrace();
					return;
				} catch (IOException e) {
					handler.sendEmptyMessage(Constants.ERROR_PLAYING);
					e.printStackTrace();
					return;
				} catch (InterruptedException e) {
					break;
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
}
