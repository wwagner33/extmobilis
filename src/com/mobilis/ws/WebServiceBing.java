package com.mobilis.ws;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;
import com.mobilis.model.Post;
import com.mobilis.util.Constants;

public class WebServiceBing {

	private HttpGet get;
	private String addressAudio;
	private Post post;
	private HttpResponse response = null;
	private DefaultHttpClient client;
	public static final String BING_URI = "http://api.microsofttranslator.com/v2/Http.svc/Speak?appId=03CAF44417913E4B9D82BE6202DBFBD768B8C5E1&text=";
	public static final String TAG = "WSBING";

	private long connectionTime = 0;
	private long connectionTimePlusFileSaving = 0;

	public WebServiceBing(Post post) {
		this.post = post;
	}

	public void setAddress(String address) {
		addressAudio = address;
	}

	public boolean getAudioAsync(String text, int index) {

		Log.v(TAG, "Post Index = " + index);

		connectionTimePlusFileSaving = System.currentTimeMillis();
		setAddress(Constants.AUDIO_DEFAULT_PATH + post.getId() + "/" + index
				+ ".mp3");
		File parentDirecotory = new File(Constants.AUDIO_DEFAULT_PATH
				+ post.getId() + "/");
		if (!parentDirecotory.exists())
			parentDirecotory.mkdirs();
		InputStream audioInput = getAudio(text);
		if (audioInput == null) {
			return false;
		}
		handleResponse(audioInput);
		return true;
	}

	private InputStream getAudio(String text) {

		client = new DefaultHttpClient();
		InputStream audioResponse = null;
		try {
			String sentence = URLEncoder.encode(text, "UTF-8").replace(" ",
					"%20");
			String uriOld = BING_URI + sentence + "&language=pt";

			get = new HttpGet(uriOld);

			connectionTime = System.currentTimeMillis();
			// Log.e(TAG, "Starting Bing Connection at " + connectionTime);
			response = client.execute(get);
			// Log.e(TAG, "Connection End at " + System.currentTimeMillis());
			connectionTime = (System.currentTimeMillis() - connectionTime);
			// Log.w(TAG, " Bing Connection Time in Seconds = " + connectionTime
			// / 1000);
			Log.w(TAG, "Connection Time in Millis = " + connectionTime);

			int connectionStatus = response.getStatusLine().getStatusCode();
			Log.w(TAG, "Status Code = " + String.valueOf(connectionStatus));
			audioResponse = response.getEntity().getContent();
			// Log.e("audioResponse", response.toString());
			return audioResponse;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void handleResponse(InputStream stream) {
		File audioFile = new File(addressAudio);
		try {
			FileOutputStream fos = new FileOutputStream(audioFile, false);
			OutputStream os = new BufferedOutputStream(fos);

			byte[] buffer = new byte[1024];

			int byteRead = 0;

			while ((byteRead = stream.read(buffer)) != -1) {
				os.write(buffer, 0, byteRead);

			}

			fos.close();
			connectionTimePlusFileSaving = System.currentTimeMillis()
					- connectionTimePlusFileSaving;
			Log.e(TAG, "Áudio baixado e salvo no cartão SD");
			// Log.i(TAG, "Connection time & file saving in seconds="
			// + connectionTimePlusFileSaving / 1000);
			Log.i(TAG, "Connection time & file saving in millis = "
					+ connectionTimePlusFileSaving);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}