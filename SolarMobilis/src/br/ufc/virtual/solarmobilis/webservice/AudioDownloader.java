package br.ufc.virtual.solarmobilis.webservice;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import android.util.Log;
import br.ufc.virtual.solarmobilis.util.HttpDownloader;

@EBean
public class AudioDownloader {

	String stringUrl;
	URL url;
	HttpURLConnection urlConnection;
	String audioFilePath;

	private DownloaderListener downloaderListener;

	@Background
	public void saveFile(String stringUrl, int i) {

		try {
			String fileName = i + ".wav";
			String audioFileFullPath = audioFilePath + fileName;
			HttpDownloader httpDownloader = new HttpDownloader();
			httpDownloader.downFile(stringUrl, audioFilePath, fileName);
			File audioFile = new File(audioFileFullPath);
			downloaderListener.onDownload(audioFile.getAbsolutePath(), i);
		} catch (Exception e) {
			downloaderListener.onDownloadException(e, i);
		}
	}

	public void setAudioPathFile(String path) {
		audioFilePath = path;
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
