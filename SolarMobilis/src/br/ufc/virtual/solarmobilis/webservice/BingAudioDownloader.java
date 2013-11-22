package br.ufc.virtual.solarmobilis.webservice;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;
import br.ufc.virtual.solarmobilis.DiscussionsPostsActivity;
import br.ufc.virtual.solarmobilis.util.BingURLCreator;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;

@EBean
public class BingAudioDownloader {

	@RootContext
	DiscussionsPostsActivity activity;

	String stringUrl;
	URL url;
	HttpURLConnection urlConnection;
	String audioFilePath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/Mobilis/TTS/";

	private DownloaderListener downloaderListener;

	public BingAudioDownloader() {

	}

	@Background
	public void saveAudio(String text, int i) {

		try {
			stringUrl = BingURLCreator.getURL(text);
			url = new URL(stringUrl);
			urlConnection = (HttpURLConnection) url.openConnection();

			InputStream stream = urlConnection.getInputStream();
			saveFile(stream, i);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void saveFile(InputStream stream, int i) {

		File audioFile = new File(audioFilePath + i + ".wav");
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(audioFile, false);
			OutputStream os = new BufferedOutputStream(fos);

			byte[] buffer = new byte[1024];
			int byteRead = 0;

			while ((byteRead = stream.read(buffer)) != -1) {
				os.write(buffer, 0, byteRead);
			}

			fos.close();

			downloaderListener.onDowloadFinish(audioFile.toString(), i);

			// Log.i("Salvar arquivo", "Arquivo " + i + " salvo");

			// tocarAudio(i);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setListener(DownloaderListener downloaderListener) {
		this.downloaderListener = downloaderListener;
	}
}
