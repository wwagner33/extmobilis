package com.mobilis.ws;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.parser.ParseException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.mobilis.controller.Constants;

public class Connection {
	private HttpResponse response;
	private HttpPost post;
	private HttpGet get;
	int byteCount;

	public Object[] getFromServer(String URL, String authToken)
			throws ClientProtocolException, IOException

	{

		Object[] resultSet = new Object[2];
		StringBuilder builder = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();

		get = new HttpGet(Constants.URL_SERVER + URL + "?auth_token="
				+ authToken);

		Log.w("URL", String.valueOf(get.getURI()));

		HttpResponse response = client.execute(get);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();

		Log.w("StatusCode", String.valueOf(statusCode));
		if (statusCode == 200) {
			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					content));
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}

			Log.w("resultFromServer", builder.toString());

			resultSet[0] = builder.toString();
			resultSet[1] = statusCode;
			return resultSet;

		}
		if (statusCode == 500) {
			resultSet[1] = Constants.ERROR_SERVER_DOWN;
			return resultSet;
		}

		if (statusCode >= 400 && statusCode < 500) {
			resultSet[1] = Constants.ERROR_TOKEN_EXPIRED;
			return resultSet;
		}

		resultSet[1] = Constants.ERROR_UNKNOWN;
		return resultSet;

	}

	public Object[] postToServer(String jsonString, String URL)
			throws ClientProtocolException, IOException, ParseException {

		Object[] resultSet = new Object[2];

		StringBuilder builder = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();

		post = new HttpPost(Constants.URL_SERVER + URL);

		StringEntity se = new StringEntity(jsonString);
		Log.w("ENTITY", jsonString);
		post.setEntity(se);
		post.setHeader("Accept", "application/json");
		post.setHeader("Content-type", "application/json");

		Log.w("URL", String.valueOf(post.getURI()));

		response = client.execute(post);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();

		Log.w("StatusCode", String.valueOf(statusCode));

		if (statusCode == 200 || statusCode == 201) {
			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					content));
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);

			}

			Log.w("resultFromServer", builder.toString());

			resultSet[0] = builder.toString();
			resultSet[1] = statusCode;
			return resultSet;

		}

		if (statusCode == 500) {
			resultSet[1] = Constants.ERROR_SERVER_DOWN;
			return resultSet;
		}

		if (statusCode == 401) {
			resultSet[1] = Constants.ERROR_TOKEN_EXPIRED;
			return resultSet;
		}

		if (statusCode == 404) {
			resultSet[1] = Constants.ERROR_PAGE_NOT_FOUND;
			return resultSet;
		}

		resultSet[1] = Constants.ERROR_UNKNOWN;
		return resultSet;
	}

	public Object[] postAudioToServer(String URL, File audioFile)
			throws IllegalStateException, IOException {

		if (audioFile == null) {

			Log.w("AUDIONULL", "YES");
		}

		DefaultHttpClient client = new DefaultHttpClient();

		Object[] resultSet = new Object[2];

		StringBuilder builder = new StringBuilder();

		HttpPost post = new HttpPost(Constants.URL_SERVER + URL);

		MultipartEntity entity = new MultipartEntity();

		File teste2 = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Mobilis/Recordings/recording.3gp");

		entity.addPart("attachment",
				new FileBody(teste2, "audio/3gpp", "UTF-8"));

		post.setEntity(entity);

		Log.w("URL", String.valueOf(post.getURI()));

		HttpResponse teste = client.execute(post);

		StatusLine statusLine = teste.getStatusLine();
		int statusCode = statusLine.getStatusCode();

		Log.w("StatusCode", String.valueOf(statusCode));

		if (statusCode == 200 || statusCode == 201) {
			HttpEntity httpEntity = teste.getEntity();
			InputStream content = httpEntity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					content));
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);

			}

			Log.w("resultFromServer", builder.toString());

			resultSet[0] = builder.toString();
			resultSet[1] = statusCode;
			return resultSet;

		}

		if (statusCode == 500) {
			resultSet[1] = Constants.ERROR_SERVER_DOWN;
			return resultSet;
		}

		if (statusCode == 401) {
			resultSet[1] = Constants.ERROR_TOKEN_EXPIRED;
			return resultSet;
		}

		if (statusCode == 404) {
			resultSet[1] = Constants.ERROR_PAGE_NOT_FOUND;
			return resultSet;
		}

		resultSet[1] = Constants.ERROR_UNKNOWN;
		return resultSet;

	}

	public Object[] getImageFromServer(String url, String authToken)
			throws ClientProtocolException, IOException {

		Object[] resultSet = new Object[2];

		FileOutputStream fileOutputStream = null;
		DefaultHttpClient client = new DefaultHttpClient();
		get = new HttpGet(Constants.URL_SERVER + url + "?auth_token="
				+ authToken);

		Log.w("IMAGE UL", get.getURI().toString());

		HttpResponse response = client.execute(get);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();

		Log.w("statusCode", String.valueOf(statusCode));

		if (statusCode == 200) {

			// response.getEntity().getContent().;
			/*
			 * Header teste = response.getEntity().getContentType();
			 * teste.getName();
			 */

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 5;
			Bitmap myImage = BitmapFactory.decodeStream(response.getEntity()
					.getContent());
			// response.getEntity()
			// BitmapFactory.decodeByteArray(bytes, 0,
			// bytes.length,options);

			fileOutputStream = new FileOutputStream(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/Mobilis/Recordings/" + "teste3" + ".png");

			BufferedOutputStream bos = new BufferedOutputStream(
					fileOutputStream);

			// myImage.
			myImage.compress(CompressFormat.PNG, 0, fileOutputStream);

			bos.flush();
			bos.close();

			resultSet[1] = statusCode;
			resultSet[0] = "teste";
			return resultSet;

		} else
			return null;

	}

	public Object[] getZippedImages(String url, String token)
			throws ClientProtocolException, IOException {

		Object[] resultSet = new Object[2];

		FileOutputStream fileOutputStream = null;
		DefaultHttpClient client = new DefaultHttpClient();
		get = new HttpGet(Constants.URL_SERVER + url + "?auth_token=" + token);

		Log.w("IMAGE UL", get.getURI().toString());

		HttpResponse response = client.execute(get);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();

		Log.w("statusCode", String.valueOf(statusCode));

		if (statusCode == 200) {

			InputStream content = response.getEntity().getContent();
			fileOutputStream = new FileOutputStream(Constants.PATH_IMAGESZIP);

			byteCount = 0;

			byte[] buffer = new byte[4096];
			int bytesRead = -1;
			while ((bytesRead = content.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, bytesRead);
				byteCount += bytesRead;
			}
			fileOutputStream.flush();
			fileOutputStream.close();

			resultSet[1] = statusCode;
			resultSet[0] = "teste";
			return resultSet;

		} else
			return null;

	}

	public void StopPost() {
		post.abort();
	}

	public void StopGet() {
		get.abort();
	}

}
