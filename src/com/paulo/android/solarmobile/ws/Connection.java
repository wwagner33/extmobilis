package com.paulo.android.solarmobile.ws;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public abstract class Connection {
	Context context;

	public Connection(Context context) {
		this.context = context;
	}

	public abstract void parse(String result);

	public String getJSON(String URL, String authToken)

	// GET

	{
		StringBuilder builder = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet("http://10.0.2.2:3000/" + URL
				+ "?auth_token=" + authToken);
		Log.w("URL", String.valueOf(get.getURI()));

		try {

			HttpResponse response = client.execute(get);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();

			Log.w("StatusCode", String.valueOf(statusCode));
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			}

			else {
				Toast.makeText(context, "Erro ao baixar o arquivo",
						Toast.LENGTH_SHORT).show();
			}

		} catch (Exception e) {
			Toast.makeText(context, "Erro de conexão", Toast.LENGTH_SHORT)
					.show();
		}
		return builder.toString();

	}

	public String requestAuthenticityToken(JSONObject json) {

		// POST

		StringBuilder builder = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://10.0.2.2:3000/sessions");

		try {

			String teste = json.toJSONString();
			StringEntity se = new StringEntity(teste);

			post.setEntity(se);
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-type", "application/json");

			Log.w("URL", String.valueOf(post.getURI()));

			HttpResponse response = client.execute(post);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();

			Log.w("StatusCode", String.valueOf(statusCode));

			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			}

			else {
				Toast.makeText(context, "Erro de conexão", Toast.LENGTH_SHORT)
						.show();
			}

		} catch (Exception e) {
			Toast.makeText(context, "Erro de conexão", Toast.LENGTH_SHORT)
					.show();
		}
		return builder.toString();

	}
}
