package com.mobilis.ws;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import roboguice.util.Ln;
import android.os.Environment;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mobilis.component.MobilisHttpClient;

@Singleton
public class BingServices {

	private MobilisHttpClient client;

	private static final String AUDIO_FILE_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/Mobilis/TTS/";
	private static final String AUDIO_FILE_EXTENSION = ".wav";
	private static final String BING_URI = "http://api.microsofttranslator.com/v2/Http.svc/Speak?appId={appId}&text={text}";
	private static final String BING_APP_ID = "03CAF44417913E4B9D82BE6202DBFBD768B8C5E1";
	private static final String LANGUAGE = "&language=pt";

	@Inject
	public BingServices() {
	}

	public int downloadAudioFile(final String text, final int blockNumber)
			throws MobilisException {

		InputStream audioResponse = null;
		try {
			client = new MobilisHttpClient();
			final String encodedText = URLEncoder.encode(text, "UTF-8")
					.replace("+", "%20");
			final String requestUrl = createBingRequestUri(encodedText);

			final HttpGet get = new HttpGet(requestUrl);
			final HttpResponse response = client.execute(get);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {
				audioResponse = response.getEntity().getContent();
				return saveAudioFile(audioResponse, blockNumber);
			}

			else {
				Ln.e("Download de áudio falhou com status " + statusCode);
				throw new MobilisException("");
			}

		} catch (IOException e) {
			Ln.e(e, "Erro ao fazer o download de posts");
			throw new MobilisException(e);
		} catch (IllegalStateException e) {
			Ln.e(e, "Erro ao fazer o download de posts");
			throw new MobilisException(e);
		}

	}

	private String createBingRequestUri(final String message) {
		return BING_URI.replace("{appId}", BING_APP_ID).replace("{text}",
				message)
				+ LANGUAGE;
	}

	private int saveAudioFile(final InputStream stream, final int blockNumber)
			throws MobilisException {
		createFolderHierarchy();
		File audioFile = new File(AUDIO_FILE_PATH + blockNumber
				+ AUDIO_FILE_EXTENSION);

		try {
			FileOutputStream fos = new FileOutputStream(audioFile, false);
			OutputStream os = new BufferedOutputStream(fos);

			byte[] buffer = new byte[1024];

			int byteRead = 0;

			while ((byteRead = stream.read(buffer)) != -1) {
				os.write(buffer, 0, byteRead);
			}

			fos.close();

			return blockNumber;

		} catch (FileNotFoundException e) {
			Ln.e(e, "Erro ao savar arquivo de áudio");
			throw new MobilisException(e);
		} catch (IOException e) {
			Ln.e(e, "Erro ao savar arquivo de áudio");
			throw new MobilisException(e);
		}
	}

	private void createFolderHierarchy() {
		final File filePath = new File(AUDIO_FILE_PATH);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
	}

	public void deleteBingFiles() {
		File audioFolder = new File(AUDIO_FILE_PATH);
		if (audioFolder.exists()) {
			final File[] audioFiles = audioFolder.listFiles();
			for (final File audioFile : audioFiles) {
				audioFile.delete();
			}
		}
	}

	public String getAudioFilePath(final int blockId) {
		return AUDIO_FILE_PATH + blockId + AUDIO_FILE_EXTENSION;
	}

}