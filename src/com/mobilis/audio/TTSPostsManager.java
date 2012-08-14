package com.mobilis.audio;

import java.io.File;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mobilis.audio.PostPlayer;
import com.mobilis.controller.PostsActivity.PostManagerHandler;
import com.mobilis.model.BlockQueue;
import com.mobilis.model.Post;
import com.mobilis.util.Constants;
import com.mobilis.ws.WebServiceBing;

public class TTSPostsManager implements Runnable {
	private ArrayList<Post> posts;
	private BlockQueue blocks;
	private int currentPostIndex;
	private PostPlayer postPlayer;
	private Thread threadPlayer;
	private WebServiceBing webServiceBing;
	private PostManagerHandler comWithActivity;
	private Context context;
	private static String TAG = "TTS";

	public TTSPostsManager(ArrayList<Post> posts, int postIndex,
			PostManagerHandler comWithActivity, Context context) {
		super();
		this.context = context;
		this.comWithActivity = comWithActivity;
		this.posts = posts;
		currentPostIndex = postIndex;
		play(currentPostIndex);
	}

	private void play(int postIndex) {
		comWithActivity.togglePostPlayingStatus(postIndex);
		createBlocks(postIndex);
	}

	Handler playAllPosts = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.PLAY_NEXT_POST:
				playNextPost();
				break;
			case Constants.ERROR_PLAYING:
				errorPlaying();
				break;
			}
		}

		private void errorPlaying() {
			Log.e("Erro", "Reprodução");
			comWithActivity.togglePostPlayingStatus(currentPostIndex);
			deleteDir(new File(Constants.AUDIO_DEFAULT_PATH));
			generateError(Constants.ERROR_PLAYING);
		}

		private void playNextPost() {
			postPlayer.playSoundEffect();

			while (postPlayer.isPlaying()) {
			}

			if (threadPlayer != null) {
				threadPlayer.interrupt();
				threadPlayer = null;
			}

			comWithActivity.untogglePostPlayingStatus(currentPostIndex);

			if (currentPostIndex < posts.size() - 1) {
				comWithActivity.playNext();
			} else {
				deleteDir(new File(Constants.AUDIO_DEFAULT_PATH));
				currentPostIndex = posts.size() - 1;
				comWithActivity.playedAllPosts();
			}
		}
	};

	void createBlocks(int index) {
		blocks = new BlockQueue();
		generateHeader(index);
		String content = posts.get(index).getContent().trim();
		int end = content.length();

		while (end > 0) {
			int cut = Math.min(content.length(), Constants.MIN_BLOCK_LENGTH);
			if (cut == content.length()) {
				if (containsLetter(content)) {
					Log.i(TAG, "BlockContent = " + content);
					blocks.addBlock(content);
				}
				break;
			}
			String blockContent = content.substring(0, cut);
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
					Math.min(content.length(), Constants.MAX_BLOCK_LENGTH),
					occurrenceOfDot), occurrenceOfExclamation),
					occurrenceOfInterrogation), occurrenceOfSemi);
			blockContent = blockContent.concat(content.substring(0,
					occurenceOfPause));
			if (occurenceOfPause == content.length()) {
				content = "";
				end = 0;
			} else {
				content = content.substring(occurenceOfPause);
				if (occurenceOfPause == Constants.MAX_BLOCK_LENGTH) {
					blockContent = blockContent.concat(content.substring(0,
							content.indexOf(" ")));
					content = content.substring(content.indexOf(" "));
				}
				end = content.length();
			}
			Log.i(TAG, "BlockContent = " + blockContent);

			if (blockContent != null) {
				blocks.addBlock(blockContent);
			} else {
				Log.i(TAG, "BlockContent IS NULL");
				break;
			}
		}

		Log.i(TAG, "Number of Blocks  = " + blocks.getNumberOfBlocks());

	}

	public static boolean containsLetter(String s) {
		if (s == null)
			return false;
		boolean letterFound = false;
		for (int i = 0; !letterFound && i < s.length(); i++) {
			letterFound = Character.isLetter(s.charAt(i))
					&& !Character.isSpaceChar(s.charAt(i));
			if (letterFound)
				return true;
		}
		return letterFound;
	}

	private void generateHeader(int postIndex) {
		String header = posts.get(postIndex).getUserNick();
		String date = posts.get(postIndex).getDate();
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7));
		int day = Integer.parseInt(date.substring(8, 10));
		int hour = Integer.parseInt(date.substring(11, 13));
		int minute = Integer.parseInt(date.substring(14, 16));
		Calendar c = Calendar.getInstance();
		if (year == c.get(Calendar.YEAR)) {
			if (month == c.get(Calendar.MONTH) + 1) {
				if (day == c.get(Calendar.DATE)) {
					if (hour == c.get(Calendar.HOUR_OF_DAY)) {
						header = header.concat(", há "
								+ (c.get(Calendar.MINUTE) - minute)
								+ " minutos");
					} else {
						header = header.concat(", às " + hour + " horas");
					}
				} else {
					if (day == c.get(Calendar.DATE) - 1)
						header = header.concat(", ontem");
					else
						header = header.concat(", no dia " + day + " às "
								+ hour + " horas");
				}
			} else {
				header = header.concat(", no dia "
						+ day
						+ " de "
						+ new DateFormatSymbols(Locale.getDefault())
								.getMonths()[month - 1]);
			}
		} else {
			header = header
					.concat(", no dia "
							+ day
							+ " de "
							+ new DateFormatSymbols(Locale.getDefault())
									.getMonths()[month - 1] + " de " + year);
		}
		Log.w("Content do Header", header);
		blocks.addBlock(header);
	}

	private boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	public void pause() {
		try {
			postPlayer.pause();
		} catch (Exception e) {

		}
	}

	public void stop() {
		try {
			postPlayer.stop();
			if (threadPlayer != null) {
				threadPlayer.interrupt();
				threadPlayer = null;
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void next() {
		stop();
		comWithActivity.untogglePostPlayingStatus(currentPostIndex);
		currentPostIndex++;
		play(currentPostIndex);
		run();
	}

	public void previous() {
		stop();
		comWithActivity.untogglePostPlayingStatus(currentPostIndex);
		currentPostIndex--;
		play(currentPostIndex);
		run();
	}

	public void playAfterPause() {
		postPlayer.playAfterPause();
	}

	public void playAfterStop() {
		play(currentPostIndex);
		run();
	}

	@Override
	public void run() {
		boolean isLastPost = currentPostIndex == (posts.size() - 1);
		webServiceBing = new WebServiceBing(posts.get(currentPostIndex));
		postPlayer = new PostPlayer(blocks.getNumberOfBlocks(), playAllPosts,
				posts.get(currentPostIndex), context, isLastPost);
		threadPlayer = new Thread(postPlayer);

		threadPlayer.start();

		int i = 0;
		while (blocks.peek() != null && !Thread.interrupted()) {
			Log.w("Gerando audio", "" + i);
			String blockAux;
			blockAux = blocks.poll();
			if (webServiceBing.getAudioAsync(blockAux, i)) {
				i++;
				notifyPostPlayer();
			} else {
				Log.e("Stop", "Erro de Conexão!!!");
				generateError(Constants.CONNECTION_ERROR);
				return;
			}
		}
		if (Thread.interrupted()) {
			if (threadPlayer != null)
				threadPlayer.interrupt();
			deleteDir(new File(Constants.AUDIO_DEFAULT_PATH));
		}
		Log.w("Thread Manager", "Finalizada");
	}

	private void notifyPostPlayer() {
		synchronized (postPlayer) {
			postPlayer.addBlock();
			postPlayer.notify();
		}
	}

	private void generateError(int errorType) {
		stop();
		threadPlayer = null;
		Log.e("Stop", "Erro de Conexão!!!");
		switch (errorType) {
		case Constants.CONNECTION_ERROR:
			comWithActivity.sendEmptyMessage(Constants.CONNECTION_ERROR);
			break;
		case Constants.ERROR_PLAYING:
			comWithActivity.sendEmptyMessage(Constants.ERROR_PLAYING);
			break;
		}
	}

	public int getCurrentPostIndex() {
		return currentPostIndex;
	}

	public int getPostsSize() {
		return posts.size();
	}
}
