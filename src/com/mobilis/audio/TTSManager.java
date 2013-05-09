package com.mobilis.audio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import roboguice.util.Ln;

import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.google.inject.Inject;
import com.mobilis.model.Post;
import com.mobilis.ws.BingServices;
import com.mobilis.ws.MobilisException;

/**
 * @author Paulo Costa
 */

public class TTSManager {

	@Inject
	private BingServices bingServices;

	private List<String> blocks;
	private static final int MIN_BLOCK_LENGTH = 200;
	private static final int MAX_BLOCK_LENGTH = 400;
	private AudioPlayer player;
	boolean[] blocksAvaliability;
	private int playedLast = -1;
	private boolean isPlaying = false;
	private TTSRequestsTask requestTask;
	private TTSEventListener eventListener;

	public static interface TTSEventListener {

		void onFinishedPlaying();

		void onError();

	}

	private void notifyOnBlockFinished() {
		if (eventListener != null) {
			eventListener.onFinishedPlaying();
		}
	}

	public TTSManager() {
		player = new AudioPlayer();
		bingServices = new BingServices();
	}

	public TTSManager(TTSEventListener eventListener) {
		this.eventListener = eventListener;
		player = new AudioPlayer();
		bingServices = new BingServices();
	}

	public void start(Post post) {
		releaseResources();
		createBlocks(post);
		makeRequests(post);
	}

	public void releaseResources() {
		if (requestTask != null) {
			requestTask.cancel(true);
		}
		player.reset();
		bingServices.deleteBingFiles();
	}

	private void createBlocks(Post post) {
		blocks = new ArrayList<String>();
		String content = post.header() + post.getContent().trim();
		int end = content.length();
				
		String blockContent = "";

		while (end > 0) {
			int cut = Math.min(content.length(), MIN_BLOCK_LENGTH);

			blockContent += content.substring(0, cut);

			content = content.substring(cut);
			int occurrenceOfDot = content.indexOf(".") == -1 ? 999 : content
					.indexOf(".") + 1;
			int occurrenceOfInterrogation = content.indexOf("?") == -1 ? 999
					: content.indexOf("?") + 1;
			int occurrenceOfExclamation = content.indexOf("!") == -1 ? 999
					: content.indexOf("!") + 1;
			int occurrenceOfSemi = content.indexOf(";") == -1 ? 999 : content
					.indexOf(";") + 1;
			int occurenceOfPause = Math.min(Math.min(Math.min(Math.min(
					Math.min(content.length(), MAX_BLOCK_LENGTH),
					occurrenceOfDot), occurrenceOfExclamation),
					occurrenceOfInterrogation), occurrenceOfSemi);
			blockContent = blockContent
					+ content.substring(0, occurenceOfPause);

			if (occurenceOfPause == content.length()) {
				content = "";
				end = 0;
			} else {
				content = content.substring(occurenceOfPause);
				if (occurenceOfPause == MAX_BLOCK_LENGTH) {
					blockContent = blockContent
							+ content.substring(0, content.indexOf(" "));
					content = content.substring(content.indexOf(" "));
				}
				end = content.length();
			}

			if (blockContent != null) {
				blocks.add(blockContent);
			}
		}

		Ln.i("Block Size = " + blocks.size());
		blocksAvaliability = new boolean[blocks.size()];
	}

	private void makeRequests(Post post) {
		requestTask = new TTSRequestsTask();
		requestTask.execute();
	}

	private void playAudioBlock(final int blockId) {
		isPlaying = true;
		playedLast = blockId;
		try {
			player.play(bingServices.getAudioFilePath(blockId),
					new MediaPlayer.OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer mp) {
							if (blockId == (blocks.size() - 1)) {
								// última posição.
								Ln.w("Última posição tocada, parando áudio.");
								isPlaying = false;
								releaseResources();
								notifyOnBlockFinished();
							} else if (blocksAvaliability[(blockId + 1)]) {
								// Toca o próximo bloco
								Ln.w("Bloco " + blockId
										+ " Tocado, Tocando bloco "
										+ (blockId + 1));
								playAudioBlock((blockId + 1));
							} else {
								// Bloco não baixado ainda
								Ln.w("Próximo bloco ainda não foi baixado. Aguardando.");
								isPlaying = false;
							}
						}
					});
		} catch (IllegalStateException e) {
			// TODO Tratar erro do player
		} catch (IOException e) {
			// TODO Tratar erro do player
		}
	}

	private class TTSRequestsTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			for (int i = 0; i < blocks.size(); i++) {
				try {
					final int blockId = bingServices.downloadAudioFile(
							blocks.get(i), i);
					publishProgress(blockId);
				} catch (MobilisException e) {
					return null;
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			int blockId = values[0];
			blocksAvaliability[blockId] = true;
			if (blockId == 0 || ((blockId - 1 == playedLast) && !isPlaying)) {
				playAudioBlock(blockId);
			}
		}
	}

}
