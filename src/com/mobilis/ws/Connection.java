package com.mobilis.ws;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mobilis.util.Constants;

public class Connection {

	private Handler handler;

	public Connection(Handler handler, Context context) {
		this.handler = handler;

	}

	public void getFromServer(int connectionId, String url, String token) {
		ExecuteConnection connection = new ExecuteConnection();
		ConnectionWatcher watcher = new ConnectionWatcher();

		connection.connectionType = Constants.TYPE_CONNECTION_GET;
		connection.connectionId = connectionId;
		connection.url = url;
		connection.token = token;

		connection.execute();
		watcher.execute(connection);

	}

	public void postToServer(int connectionId, String jsonString, String url) {
		ExecuteConnection connection = new ExecuteConnection();
		ConnectionWatcher watcher = new ConnectionWatcher();

		connection.connectionType = Constants.TYPE_CONNECTION_POST;
		connection.connectionId = connectionId;
		connection.url = url;
		connection.jsonString = jsonString;

		connection.execute();
		watcher.execute(connection);
	}

	public void postToServer(int connectionId, String url, File audioFile,
			String token) {
		ExecuteConnection connection = new ExecuteConnection();
		ConnectionWatcher watcher = new ConnectionWatcher();

		connection.connectionType = Constants.TYPE_CONNECTION_POST;
		connection.connectionId = connectionId;
		connection.url = url;
		connection.token = token;
		connection.file = audioFile;

		connection.execute();
		watcher.execute(connection);
	}

	public void getImages(ArrayList<Integer> userIds, String token) {
		int count = userIds.size();

		for (int i = 0; i < count; i++) {
			ExecuteConnection imageConnection = makeImageConnection(
					userIds.get(i), token);
			ConnectionWatcher connectionWatcher = new ConnectionWatcher();
			imageConnection.execute();
			connectionWatcher.execute(imageConnection);
		}
	}

	private ExecuteConnection makeImageConnection(int userId, String token) {
		ExecuteConnection imageConnection = new ExecuteConnection();
		imageConnection.url = Constants.getImageURL(userId);
		imageConnection.connectionType = Constants.TYPE_CONNECTION_GET;
		imageConnection.connectionId = Constants.CONNECTION_GET_IMAGES;
		imageConnection.user_id = userId;
		imageConnection.token = token;
		return imageConnection;
	}

	private void sendPositiveMessage(String result, int connectionId) {
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

	private void sendNegativeMessage(int connectionId, int statusCode) {

		Message message = Message.obtain();
		Bundle bundle = new Bundle();

		if (connectionId == Constants.CONNECTION_GET_IMAGES) {
			handler.sendEmptyMessage(Constants.MESSAGE_IMAGE_CONNECION_FAILED);
		}

		else if (connectionId == Constants.CONNECTION_GET_HISTORY_POSTS) {
			handler.sendEmptyMessage(Constants.MESSAGE_HISTORY_POST_CONNECTION_FAILED);
		}

		else if (connectionId == Constants.CONNECTION_POST_AUDIO) {
			handler.sendEmptyMessage(Constants.MESSAGE_AUDIO_POST_FAILED);
		}

		else {
			message.what = Constants.MESSAGE_CONNECTION_FAILED;
			bundle.putInt("statusCode", statusCode);
			message.setData(bundle);
			handler.sendMessage(message);
		}
	}

	private Object[] executeGet(String URL, String authToken, HttpGet get)
			throws ClientProtocolException, IOException

	{

		Object resultSet[] = new Object[2];

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
		} else {
			resultSet[0] = null;
			resultSet[1] = statusCode;
			return resultSet;
		}
	}

	private Object[] executePost(String jsonString, String URL, HttpPost post)
			throws ClientProtocolException, IOException, ParseException {

		Object resultSet[] = new Object[2];

		StringBuilder builder = new StringBuilder();
		DefaultHttpClient client = new DefaultHttpClient();

		post = new HttpPost(Constants.URL_SERVER + URL);

		StringEntity se = new StringEntity(jsonString, "UTF-8");
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
				"application/json"));
		se.setContentType("application/json");
		post.setEntity(se);
		post.setHeader("Accept", "application/json");
		post.setHeader("Content-type", "application/json");

		Log.w("URL", String.valueOf(post.getURI()));

		HttpResponse response = client.execute(post);
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

		else {
			resultSet[0] = null;
			resultSet[1] = statusCode;
			return resultSet;
		}
	}

	private Object[] executeAudioPost(String URL, File audioFile,
			HttpPost post, String token) throws IllegalStateException,
			IOException {

		Object resultSet[] = new Object[2];

		if (audioFile == null) {

		}

		DefaultHttpClient client = new DefaultHttpClient();

		StringBuilder builder = new StringBuilder();

		post = new HttpPost(Constants.URL_SERVER + URL + "?auth_token=" + token);

		MultipartEntity entity = new MultipartEntity();

		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Mobilis/Recordings/recording.3gp");

		entity.addPart("post_file", new FileBody(file, "audio/3gpp", "UTF-8"));

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

			Log.w("AUDIORESULT", builder.toString());

			resultSet[0] = builder.toString();
			resultSet[1] = statusCode;
			return resultSet;
		}

		else {
			resultSet[0] = null;
			resultSet[1] = statusCode;
			return resultSet;
		}
	}

	private Object[] getImage(HttpGet get, String token, String url, int userId)
			throws ClientProtocolException, IOException {

		Object resultSet[] = new Object[2];

		FileOutputStream fileOutputStream = null;
		DefaultHttpClient client = new DefaultHttpClient();
		get = new HttpGet(Constants.URL_SERVER + url + "?auth_token=" + token);

		Log.w("IMAGE URL", get.getURI().toString());

		HttpResponse response = client.execute(get);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();

		Log.w("statusCode", String.valueOf(statusCode));

		if (statusCode == 200) {

			File imagesPath = new File(Constants.PATH_IMAGES);
			if (!imagesPath.exists()) {
				imagesPath.mkdir();
			}

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 5;
			Bitmap myImage = BitmapFactory.decodeStream(response.getEntity()
					.getContent());
			fileOutputStream = new FileOutputStream(Constants.PATH_IMAGES
					+ userId + ".png");

			BufferedOutputStream bos = new BufferedOutputStream(
					fileOutputStream);

			myImage.compress(CompressFormat.PNG, 0, fileOutputStream);

			bos.flush();
			bos.close();

			resultSet[0] = new String();
			resultSet[1] = statusCode;
			return resultSet;
		} else
			resultSet[0] = null;
		resultSet[1] = statusCode;
		return resultSet;
	}

	private void abortConnection(ExecuteConnection conn) {
		if (conn.connectionType == Constants.TYPE_CONNECTION_GET) {
			if (conn.get != null)
				conn.get.abort();
		} else if (conn.connectionType == Constants.TYPE_CONNECTION_POST) {
			if (conn != null)
				conn.post.abort();
		}
	}

	private class ExecuteConnection extends AsyncTask<Void, Void, Object[]> {

		public int connectionId;
		public HttpGet get = null;
		public HttpPost post = null;
		public int connectionType;
		public String url = null;
		public String token = null;
		public File file = null;
		public String jsonString = null;
		public int user_id;
		public int statusCode = 0;

		@Override
		protected Object[] doInBackground(Void... params) {

			try {
				if (connectionType == Constants.TYPE_CONNECTION_GET) {

					if (connectionId == Constants.CONNECTION_GET_IMAGES) {
						return getImage(get, token, url, user_id);
					} else
						return executeGet(url, token, get);
				}

				if (connectionType == Constants.TYPE_CONNECTION_POST) {

					if (connectionId == Constants.CONNECTION_POST_AUDIO) {
						return executeAudioPost(url, file, post, token);

					} else
						return executePost(jsonString, url, post);
				}
			} catch (ClientProtocolException e) {
				return null;
			} catch (IOException e) {
				return null;
			} catch (ParseException e) {
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object[] result) {

			if (statusCode != 699)
				statusCode = (Integer) result[1];

			if (result[0] != null) {
				// Conexão bem sucedida
				sendPositiveMessage((String) result[0], connectionId);
			}

			else if (result[0] == null && statusCode != 0) {
				sendNegativeMessage(connectionId, statusCode);
			}

			else {

				if (connectionId != Constants.CONNECTION_GET_IMAGES) {
					sendNegativeMessage(connectionId, statusCode);
				}

			}

			super.onPostExecute(result);

		}
	}

	private class ConnectionWatcher extends AsyncTask<Object, Void, Integer> {

		private ExecuteConnection toWatch = null;

		@Override
		protected Integer doInBackground(Object... params) {
			// TODO Auto-generated method stub
			try {
				toWatch = (ExecuteConnection) params[0];
				toWatch.get(15, TimeUnit.SECONDS);
				return 0;
			} catch (InterruptedException e) {
				return 2;
			} catch (ExecutionException e) {
				toWatch.statusCode = 699;
				abortConnection(toWatch);
				e.printStackTrace();
				return 1;
			} catch (TimeoutException e) {
				toWatch.statusCode = 699;
				abortConnection(toWatch);
				e.printStackTrace();
				return 1;
			} catch (NullPointerException e) {
				toWatch.statusCode = 699;
				return 1;
			}

		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result == 1) {
			}
		}
	}
}
