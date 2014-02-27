package br.ufc.virtual.solarmobilis.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import android.media.MediaPlayer;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import br.ufc.virtual.solarmobilis.DiscussionsPostsActivity;
import br.ufc.virtual.solarmobilis.model.DiscussionPost;
import br.ufc.virtual.solarmobilis.model.DiscussionPostAttachment;
import br.ufc.virtual.solarmobilis.util.TextBlockenizer;
import br.ufc.virtual.solarmobilis.webservice.BingAudioDownloader;
import br.ufc.virtual.solarmobilis.webservice.DownloaderListener;
import br.ufc.virtual.solarmobilis.webservice.PostPlayerListener;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

@EBean
public class PostPlayer implements DownloaderListener {

	@Bean
	SolarManager solarManager;

	private boolean paused = false;
	private boolean stoped = true;
	public TextBlockenizer blockenizer;
	public MediaPlayer mp = new MediaPlayer();
	public BingAudioDownloader audioDownloader;
	public PostPlayerListener postPlayerListener;
	public DiscussionsPostsActivity postsActivity;
	public List<String> fileDescriptors = new ArrayList<String>();
	public File file = new File(Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/Mobilis/TTS/");
	public AudioPlayer audioPlayer = new AudioPlayer();

	int downloadWaitingToPlayAudioBlockIndex = 0;

	public PostPlayer() {
		audioDownloader = new BingAudioDownloader();
		audioDownloader.setListener(this);
	}

	@Background
	public void play(DiscussionPost post) {
		paused = false;
		stoped = false;
		downloadWaitingToPlayAudioBlockIndex = 0;
		deleteAudioData();

		String textToBreak = post.userNick + ", " + post.getDateToPost() + ", "
				+ Html.fromHtml(post.getContent()).toString();

		blockenizer = new TextBlockenizer(textToBreak);

		List<DiscussionPostAttachment> audioAttachments = new ArrayList<DiscussionPostAttachment>();

		for (DiscussionPostAttachment discussionPostAttachment : post
				.getAttachments()) {
			if (discussionPostAttachment.getType().equals(
					"audio/aac; charset=UTF-8")) {
				audioAttachments.add(discussionPostAttachment);
			}
		}

		setFileDescriptorsSize(blockenizer.size() + audioAttachments.size());

		for (String block = blockenizer.getFirst(); block != ""; block = blockenizer
				.getNext()) {
			audioDownloader.saveAudio(block,
					blockenizer.getCurrentBlockPosition() - 1);
		}

		int currentAudioAttrachmentPosition = blockenizer.size();

		for (DiscussionPostAttachment audioAttachment : audioAttachments) {
			String anexo = solarManager.getAttachmentUrl(audioAttachment
					.getLink());

			Log.i("link para download", anexo);
			audioDownloader
					.saveFile(anexo, (currentAudioAttrachmentPosition++));
		}
	}

	private void setFileDescriptorsSize(int filedescriptorsSize) {
		for (int i = 0; i < filedescriptorsSize; i++) {
			fileDescriptors.add("");
		}
	}

	@Override
	public void onDownload(String name, final int downloadedAudioBlockIndex) {
		Log.i("ondownloadfinish",
				name + " " + String.valueOf(downloadedAudioBlockIndex));

		if (downloadedAudioBlockIndex >= fileDescriptors.size()) {
			final File audioFile = new File(name);
			audioFile.delete();
			return;
		}

		fileDescriptors.set(downloadedAudioBlockIndex, name);
		Log.i("++++download", "+++++");
		

		if (downloadedAudioBlockIndex == downloadWaitingToPlayAudioBlockIndex) {
			try {
				playAudio(downloadedAudioBlockIndex);
			} catch (Exception e) {
				postPlayerListener.onPostPlayException(e);
			  
			}
		}

	}

	@Override
	public void onDownloadException(Exception exception) {
		postPlayerListener.onPostPlayException(exception);
	      
	}

	public void playAudio(final int audioBlockIndex)
			throws IllegalArgumentException, SecurityException,
			IllegalStateException, IOException {
           Log.i("++++++audio", "++++");
		
		audioPlayer.reset();
		audioPlayer.play(fileDescriptors.get(audioBlockIndex),
				new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						if (audioBlockIndex == (fileDescriptors.size() - 1)) {
							Log.i("ultimo post", "Ultimo bloco tocado");
							// stop();
							stoped = true;
							deleteAudioData();
							postPlayerListener.onCompletion();
						} else if (fileDescriptors.get(audioBlockIndex + 1)
								.equals("")) {
							Log.i("Bloco", "Bloco não disponível "
									+ (audioBlockIndex + 1));
							downloadWaitingToPlayAudioBlockIndex = audioBlockIndex + 1;
                             Log.i("+++++audio normal", "+++++");
							      
						} else if (fileDescriptors.get(audioBlockIndex + 1) != null) {
							Log.i("Tocar", "Proximo bloco");
							try {
								playAudio(audioBlockIndex + 1);
								Log.i("+++++audio normal", "normal");
							} catch (Exception e) {
								postPlayerListener.onPostPlayException(e);
							    e.printStackTrace();
							    
							    Log.i("erro ", "aqui");
							   // Log.i("causa da excess�o",e.getCause().toString());
							}
						} else {
							downloadWaitingToPlayAudioBlockIndex = audioBlockIndex;
						}
					}
				});
	}

	public void play() {
		try {
			audioPlayer.play();

		} catch (Exception e) {
			e.printStackTrace();
		}
		paused = false;
		stoped = false;
	}

	public void pause() {
		audioPlayer.pause();
		paused = true;
	}

	public boolean isPaused() {
		return paused;
	}

	public void stop() {
		audioPlayer.stop();
		stoped = true;
		paused = false;
		deleteAudioData();
	}

	public boolean isStoped() {
		return stoped;
	}

	public boolean isPlaying() {
		if (audioPlayer != null) {
			return audioPlayer.isPlaying();
		}
		return false;
	}

	public void setPostPlayerListener(PostPlayerListener postPlayerListener) {
		this.postPlayerListener = postPlayerListener;
	}

	public void deleteAudioData() {
		fileDescriptors.clear();
		Log.i("filedescriptors", "dados apagados apagados");

		if (file.exists()) {
			final File[] audioFiles = file.listFiles();
			for (final File audioFile : audioFiles) {
				audioFile.delete();
			}
			Log.i("Arquivos", "dados apagados apagados");
		}
	}

}
