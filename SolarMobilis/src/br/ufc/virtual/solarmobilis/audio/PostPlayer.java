package br.ufc.virtual.solarmobilis.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

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

	public PostPlayer() {
		audioDownloader = new BingAudioDownloader();
		audioDownloader.setListener(this);

	}

	@Background
	public void play(DiscussionPost post) {
		paused = false;
		stoped = false;
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

		// Pre define o tamanho do array pra que sempre procure tocar um próximo
		// se existir
		for (int i = 0; i < blockenizer.size() + audioAttachments.size(); i++) {
			fileDescriptors.add("");
		}

		int currentAudioAttrachmentPosition = blockenizer.size();

		for (DiscussionPostAttachment audioAttachment : audioAttachments) {
			String anexo = solarManager.getAttachmentUrl(audioAttachment
					.getLink());

			Log.i("link para download", anexo);
			audioDownloader
					.saveFile(anexo, (currentAudioAttrachmentPosition++));
		}

		for (String block = blockenizer.getFirst(); block != ""; block = blockenizer
				.getNext()) {
			audioDownloader.saveAudio(block,
					blockenizer.getCurrentBlockPosition() - 1);
		}

		Log.i("arquivos", "baixados e criado");
	}

	@Override
	public void onDownload(String name, final int i) {
		Log.i("ondownloadfinish", name + " " + String.valueOf(i));

		fileDescriptors.set(i, name);

		if (i == 0) { // blocoEsperado
			try {
				playAudio(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void playAudio(final int i) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {

		audioPlayer.reset();
		audioPlayer.play(fileDescriptors.get(i),
				new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {

						if (i == (fileDescriptors.size() - 1)) {
							Log.i("ultimo post", "Ultimo bloco tocado");
							// stop();
							stoped = true;
							deleteAudioData();
							postPlayerListener.onCompletion();
						} else if (fileDescriptors.get(i + 1) != null) {
							Log.i("Tocar", "Proximo bloco");

							try {
								playAudio(i + 1);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							Log.i("bloco", "bloco não baixado"); 
							// blocoEsperado =
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
