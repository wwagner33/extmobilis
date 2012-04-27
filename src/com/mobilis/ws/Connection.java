package com.mobilis.ws;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.simple.parser.ParseException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mobilis.util.Constants;
import com.mobilis.util.ErrorHandler;

public class Connection {

	private HttpResponse response;
	private HttpPost post;
	private HttpGet get;
	private int connectionType;
	private Handler handler;
	private ExecuteConnection executeConnection;
	private WaitForConnection waitConnection;
	private int connectionId;
	private String url;
	private File file;
	private String token;
	private Context context;
	private String jsonString;

	public Connection(Handler handler, Context context) {
		this.handler = handler;
		this.context = context;

	}

	public void startConnection() {
		executeConnection = new ExecuteConnection();
		executeConnection.execute();
		waitConnection = new WaitForConnection();
		waitConnection.execute();
	}

	public void getFromServer(int connectionId, String url, String token) {
		connectionType = Constants.TYPE_CONNECTION_GET;
		this.connectionId = connectionId;
		this.url = url;
		this.token = token;
		startConnection();

	}

	public void postToServer(int connectionId, String jsonString, String url) {
		connectionType = Constants.TYPE_CONNECTION_POST;
		this.connectionId = connectionId;
		this.url = url;
		this.jsonString = jsonString;
		startConnection();

	}

	public void postToServer(int connectionId, String url, File audioFile) {
		// Audio Post
		connectionType = Constants.TYPE_CONNECTION_POST;
		this.connectionId = connectionId;
		this.url = url;
		file = audioFile;
		startConnection();
	}

	public void getImages(int connectionId, String url, String token) {
		connectionType = Constants.TYPE_CONNECTION_GET;
		this.connectionId = connectionId;
		this.url = url;
		startConnection();

	}

	private void sendPositiveMessage(String result) {
		Message message = Message.obtain();
		Bundle bundle = new Bundle();

		if (connectionId == Constants.CONNECTION_POST_TOKEN) {
			message.what = Constants.MESSAGE_TOKEN_CONNECTION_OK;
		}

		if (connectionId == Constants.CONNECTION_GET_COURSES) {
			message.what = Constants.MESSAGE_COURSE_CONNECTION_OK;
		}

		if (connectionId == Constants.CONNECTION_GET_CLASSES) {
			message.what = Constants.MESSAGE_CLASS_CONNECTION_OK;
		}

		if (connectionId == Constants.CONNECTION_GET_TOPICS) {
			message.what = Constants.MESSAGE_TOPIC_CONNECTION_OK;
		}

		if (connectionId == Constants.CONNECTION_GET_NEW_POSTS) {
			message.what = Constants.MESSAGE_NEW_POST_CONNECTION_OK;
		}
		if (connectionId == Constants.CONNECTION_GET_HISTORY_POSTS) {
			message.what = Constants.MESSAGE_HISTORY_POST_CONNECTION_OK;
		}

		if (connectionId == Constants.CONNECTION_POST_AUDIO) {
			message.what = Constants.MESSAGE_AUDIO_POST_OK;
		}

		if (connectionId == Constants.CONNECTION_POST_TEXT_RESPONSE) {
			message.what = Constants.MESSAGE_TEXT_RESPONSE_OK;
		}
		if (connectionId == Constants.CONNECTION_GET_IMAGES) {
			message.what = Constants.MESSAGE_IMAGE_CONNECTION_OK;
		}

		bundle.putString("content", result);
		message.setData(bundle);
		handler.sendMessage(message);
	}

	private void sendNegativeMessage() {

		if (connectionId == Constants.CONNECTION_GET_IMAGES) {
			handler.sendEmptyMessage(Constants.MESSAGE_IMAGE_CONNECION_FAILED);
		} else
			handler.sendEmptyMessage(Constants.MESSAGE_CONNECTION_FAILED);
	}

	private Object[] executeGet(String URL, String authToken)
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

	private Object[] executePost(String jsonString, String URL)
			throws ClientProtocolException, IOException, ParseException {

		Object[] resultSet = new Object[2];

		StringBuilder builder = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();

		post = new HttpPost(Constants.URL_SERVER + URL);

		// StringEntity se = new StringEntity(jsonString);
		StringEntity se = new StringEntity(jsonString, "UTF-8");
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
				"application/json"));
		se.setContentType("application/json");
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

		resultSet[1] = statusCode;
		return resultSet;

	}

	private Object[] executeAudioPost(String URL, File audioFile)
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

		resultSet[1] = statusCode;
		return resultSet;

	}

	private Object[] getZippedImages(String url, String token)
			throws ClientProtocolException, IOException {

		Object[] resultSet = new Object[2];

		FileOutputStream fileOutputStream = null;
		DefaultHttpClient client = new DefaultHttpClient();
		get = new HttpGet(Constants.URL_SERVER + url + "?auth_token=" + token);

		Log.w("IMAGE URL", get.getURI().toString());

		HttpResponse response = client.execute(get);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();

		Log.w("statusCode", String.valueOf(statusCode));

		if (statusCode == 200) {

			InputStream content = response.getEntity().getContent();
			fileOutputStream = new FileOutputStream(Constants.PATH_IMAGESZIP);

			byte[] buffer = new byte[4096];
			int bytesRead = -1;
			while ((bytesRead = content.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, bytesRead);
			}
			fileOutputStream.flush();
			fileOutputStream.close();

			resultSet[1] = statusCode;
			resultSet[0] = "stub";
			return resultSet;

		} else {
			resultSet[1] = statusCode;
			return resultSet;
		}

	}

	private void abortConnection() {
		if (connectionType == Constants.TYPE_CONNECTION_GET)
			get.abort();
		else
			post.abort();
	}

	private class ExecuteConnection extends AsyncTask<Void, Void, Object[]> {

		@Override
		protected Object[] doInBackground(Void... params) {

			try {

				if (connectionType == Constants.TYPE_CONNECTION_GET) {

					if (connectionId == Constants.CONNECTION_GET_IMAGES) {
						return getZippedImages(url, token);
					} else
						return executeGet(url, token);
				}

				if (connectionType == Constants.TYPE_CONNECTION_POST) {

					if (connectionId == Constants.CONNECTION_POST_AUDIO) {
						return executeAudioPost(url, file);

					} else
						return executePost(jsonString, url);
				}
			} catch (ClientProtocolException e) {
				return null;
			} catch (IOException e) {
				return null;
			} catch (ParseException e) {
				return null;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Object[] result) {

			waitConnection.cancel(true);

			if (result != null) {

				if (result[0] != null) {
					// Conexão bem sucedida
					sendPositiveMessage((String) result[0]);

				}

				else {
					// Não caiu em exceção mas o status não foi o desejado
					sendNegativeMessage();
				}

			}

			else {
				// Caiu em alguma Exception
				ErrorHandler.handleError(context,
						Constants.ERROR_CONNECTION_FAILED);
				sendNegativeMessage();
			}

			super.onPostExecute(result);

		}
	}

	private class WaitForConnection extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(20000);

				if (executeConnection.getStatus() == AsyncTask.Status.RUNNING) {
					abortConnection();
					return 1;
				} else {
					return 0;
				}
			} catch (InterruptedException e) {
				return 2;
			}

		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != 0 && result != 2) {
				ErrorHandler.handleError(context,
						Constants.ERROR_CONNECTION_TIMEOUT);
			}
		}
	}

}
