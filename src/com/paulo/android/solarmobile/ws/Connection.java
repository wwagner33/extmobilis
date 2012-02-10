package com.paulo.android.solarmobile.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.paulo.android.solarmobile.controller.Constants;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Connection {
	Context context;
	HttpResponse response;
	HttpPost post;
	HttpGet get;

	public Connection(Context context) {
		this.context = context;
	}

	// public abstract Object parse(String result);

	public Object[] getFromServer(String URL, String authToken)
			throws ClientProtocolException, IOException
	// GET

	{

		Object[] resultSet = new Object[2];
		StringBuilder builder = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();
		// HttpGet get = new HttpGet("http://10.0.2.2:3000/" + URL
		// + "?auth_token=" + authToken);
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

		// POST

		Object[] resultSet = new Object[2];

		StringBuilder builder = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();
		// HttpPost post = new HttpPost("http://10.0.2.2:3000/sessions");

		post = new HttpPost(Constants.URL_SERVER + URL);

		// String teste = json.toJSONString();
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

	public Object[] postAudioToServer(String jsonString, String URL) {
		
		
		return null;
	}
	
	
	public void StopPost() {
		post.abort();
	}

	public void StopGet() {
		get.abort();
	}
	

}
