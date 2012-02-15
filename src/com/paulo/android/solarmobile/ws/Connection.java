package com.paulo.android.solarmobile.ws;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.simple.parser.ParseException;

import com.paulo.android.solarmobile.controller.Constants;

import android.content.Context;

import android.os.Environment;
import android.util.Log;

public class Connection {
	Context context;
	HttpResponse response;
	HttpPost post;
	HttpGet get;

	public Connection(Context context) {
		this.context = context;
	}

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

	public void StopPost() {
		post.abort();
	}

	public void StopGet() {
		get.abort();
	}

}
