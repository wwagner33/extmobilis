package br.ufc.virtual.solarmobilis.webservice;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import android.os.Environment;
import android.util.Log;
import br.ufc.virtual.solarmobilis.util.HttpDownloader;

@EBean
public class AudioDownloader {

	String stringUrl;
	URL url;
	HttpURLConnection urlConnection;
	String audioFilePath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/Mobilis/TTS/";

	private DownloaderListener downloaderListener;

	@Background
	public void saveFile(String stringUrl, int i) {
		HttpDownloader httpDownloader = new HttpDownloader();
		httpDownloader.downFile(stringUrl, "Mobilis/TTS/", i + ".wav");

		File audioFile = new File(audioFilePath + i + ".wav");
		downloaderListener.onDownload(audioFile.getAbsolutePath(), i);
	}

	public void deleteFiles() {
		File file = new File(audioFilePath);
		if (file.exists()) {
			final File[] audioFiles = file.listFiles();
			for (final File audioFile : audioFiles) {
				audioFile.delete();
			}
		}
		Log.i("Arquivos", "apagados");
	}

	public void setListener(DownloaderListener downloaderListener) {
		this.downloaderListener = downloaderListener;
	}
}
