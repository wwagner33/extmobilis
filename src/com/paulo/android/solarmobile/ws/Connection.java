package com.paulo.android.solarmobile.ws;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public abstract class Connection {
	Context context;
	
	// Parse Variables
	String result;
	JSONArray jsonArray;
	

	public Connection(Context context) {
		this.context = context;
	}

	public abstract void parse();
	
	

	public String getJSON(String URL, String authToken, String username,
			String password) {
		StringBuilder builder = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://10.0.2.2:3000/" + URL);
		Log.w("URL",String.valueOf(post.getURI()));
		List<NameValuePair> nameValuesPairs = new ArrayList<NameValuePair>();
		nameValuesPairs.add(new BasicNameValuePair("username", username));
		nameValuesPairs.add(new BasicNameValuePair("password", password));
		nameValuesPairs.add(new BasicNameValuePair("authenticity_token",
				authToken));

		
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuesPairs));
			HttpResponse response = client.execute(post);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			
			
				Log.w("StatusCode",String.valueOf(statusCode));
				if (statusCode==200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) !=null) {
						builder.append(line);
					}
				}
				
				else {
					Toast.makeText(context, "Erro ao baixar o arquivo", Toast.LENGTH_SHORT).show();
				}

		} 	catch (Exception e) {
				Toast.makeText(context, "Erro de conexão", Toast.LENGTH_SHORT).show();
		}
		return builder.toString();

	}

	public abstract String requestAuthenticityToken();
}
