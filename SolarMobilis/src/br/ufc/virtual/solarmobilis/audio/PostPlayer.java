package br.ufc.virtual.solarmobilis.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import br.ufc.virtual.solarmobilis.DiscussionsPostsActivity;
import br.ufc.virtual.solarmobilis.model.DiscussionPost;
import br.ufc.virtual.solarmobilis.util.TextBlockenizer;
import br.ufc.virtual.solarmobilis.webservice.BingAudioDownloader;
import br.ufc.virtual.solarmobilis.webservice.DownloaderListener;
import br.ufc.virtual.solarmobilis.webservice.PostPlayerListener;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class PostPlayer implements DownloaderListener {

	private boolean paused;
	private boolean stoped = true;
	public TextBlockenizer blockenizer;
	public MediaPlayer mp = new MediaPlayer();
	public BingAudioDownloader audioDownloader;
	public PostPlayerListener postPlayerListener;
	public DiscussionsPostsActivity postsActivity;
	public List<String> fileDescriptors = new ArrayList<String>();
	public File file = new File(Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/Mobilis/TTS/");

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
				+ post.getContent();

		blockenizer = new TextBlockenizer(textToBreak);
		for (String block = blockenizer.getFirst(); block != ""; block = blockenizer
				.getNext()) {

			fileDescriptors.add("");
			audioDownloader.saveAudio(block,
					blockenizer.getCurrentBlockPosition() - 1);
		}

		Log.i("arquivos", "baixados e criado");
	}

	@Override
	public void onDowloadFinish(String name, int i) {
		Log.i("ondownloadfinish", name + " " + String.valueOf(i));

		fileDescriptors.set(i, name);

		if (i == 0) {

			try {
				playAudio(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void playAudio(final int i) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {

		mp.reset();
		mp.setDataSource(fileDescriptors.get(i));
		mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {

				if (i == (fileDescriptors.size() - 1)) {
					Log.i("ultimo post", "Ultimo post tocado");
					stop();
					postPlayerListener.onCompletion();

				} else if (fileDescriptors.get(i + 1) != null) {
					Log.i("Tocar", "Proximo bloco");

					try {
						playAudio(i + 1);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					Log.i("bloco", "bloco n�o baixado");
				}

			}
		});

		mp.prepare();
		mp.start();

	}

	public void play() {
		mp.start();
		paused = false;
		stoped = false;
	}

	public void pause() {
		if (mp.isPlaying()) {
			mp.pause();
			paused = true;
		}
	}

	public boolean isPaused() {
		return paused;
	}

	public void stop() {
		mp.stop();
		stoped = true;
		deleteAudioData();

	}

	public boolean isStoped() {
		return stoped;
	}

	public boolean isPlaying() {
		if (mp != null) {
			return mp.isPlaying();
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
