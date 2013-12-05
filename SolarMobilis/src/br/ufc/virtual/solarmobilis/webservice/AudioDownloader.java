package br.ufc.virtual.solarmobilis.webservice;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Environment;
import android.util.Log;

public class AudioDownloader {

	String stringUrl;
	URL url;
	HttpURLConnection urlConnection;
	String audioFilePath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/Mobilis/TTS/";

	private DownloaderListener downloaderListener;

	public void saveFile(String stringUrl, int i) {
		try {

			url = new URL(stringUrl);
			urlConnection = (HttpURLConnection) url.openConnection();
			InputStream stream = urlConnection.getInputStream();

			File audioFile = new File(audioFilePath + i + ".wav");
			FileOutputStream fos;

			fos = new FileOutputStream(audioFile, false);
			OutputStream os = new BufferedOutputStream(fos);

			byte[] buffer = new byte[1024];
			int byteRead = 0;

			while ((byteRead = stream.read(buffer)) != -1) {
				os.write(buffer, 0, byteRead);
			}

			fos.close();

			downloaderListener.onDowloadFinish(audioFile.toString(), i);

			Log.i("Salvar arquivo", "Arquivo " + i + " salvo");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
