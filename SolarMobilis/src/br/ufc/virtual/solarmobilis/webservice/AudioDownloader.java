package br.ufc.virtual.solarmobilis.webservice;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.ufc.virtual.solarmobilis.util.HttpDownloader;

import android.os.Environment;
import android.util.Log;

public class AudioDownloader {

	String stringUrl;
	URL url;
	HttpURLConnection urlConnection;
	String audioFilePath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/Mobilis/TTS/";

	private DownloaderListener downloaderListener;

	// public void saveFile(String stringUrl, int i) {
	// try {
	//
	// url = new URL(stringUrl);
	// urlConnection = (HttpURLConnection) url.openConnection();
	// InputStream stream = urlConnection.getInputStream();
	//
	// File audioFile = new File(audioFilePath + i + ".wav");
	// FileOutputStream fos;
	//
	// fos = new FileOutputStream(audioFile);
	// OutputStream os = new BufferedOutputStream(fos);
	//
	// byte[] buffer = new byte[4*1024];
	// int byteRead = 0;
	//
	// while ((byteRead = stream.read(buffer)) >0) {
	// os.write(buffer, 0, byteRead);
	// }
	//
	// fos.flush();
	// fos.close();
	//
	// downloaderListener.onDowloadFinish(audioFile.getAbsolutePath(), i);
	//
	// Log.i("Salvar arquivo", "Arquivo " + i + " salvo");
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	public void saveFile(String stringUrl, int i) {
		HttpDownloader.initStrictMode();
		HttpDownloader httpDownloader = new HttpDownloader();
		httpDownloader.downFile(stringUrl, "Mobilis/TTS/", i
				+ ".wav");
		
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
