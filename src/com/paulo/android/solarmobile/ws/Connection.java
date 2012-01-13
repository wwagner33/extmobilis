package com.paulo.android.solarmobile.ws;

import java.io.BufferedReader;
import java.io.IOException;
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
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public abstract class Connection {
	Context context;
	HttpResponse response;

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

			response = client.execute(post);
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

		return (parseToken(builder.toString()));

	}

	public String parseToken(String jsonString) {

		try {

			JSONParser parser = new JSONParser();
			KeyFinder finder = new KeyFinder();
			finder.setMatchKey("auth_token");
		
				parser.parse(jsonString, finder, true);
				if (finder.isFound()) {
					 finder.setFound(false);
					return (String) finder.getValue();
				}
				 else
					return "keyNotFound";
		}
		catch (ParseException e) {
			return "ParserException";
			}	
		}

	class KeyFinder implements ContentHandler {
		private Object value;
		private boolean found = false;
		private boolean end = false;
		private String key;
		private String matchKey;

		public void setMatchKey(String matchKey) {
			this.matchKey = matchKey;
		}

		public Object getValue() {
			return value;
		}

		public boolean isEnd() {
			return end;
		}

		public void setFound(boolean found) {
			this.found = found;
		}

		public boolean isFound() {
			return found;
		}

		public void startJSON() throws ParseException, IOException {
			found = false;
			end = false;
		}

		public void endJSON() throws ParseException, IOException {
			end = true;
		}

		public boolean primitive(Object value) throws ParseException,
				IOException {
			if (key != null) {
				if (key.equals(matchKey)) {
					found = true;
					this.value = value;
					key = null;
					return false;
				}
			}
			return true;
		}

		public boolean startArray() throws ParseException, IOException {
			return true;
		}

		public boolean startObject() throws ParseException, IOException {
			return true;
		}

		public boolean startObjectEntry(String key) throws ParseException,
				IOException {
			this.key = key;
			return true;
		}

		public boolean endArray() throws ParseException, IOException {
			return false;
		}

		public boolean endObject() throws ParseException, IOException {
			return true;
		}

		public boolean endObjectEntry() throws ParseException, IOException {
			return true;
		}
	}
}

