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

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.mobilis.interfaces.ConnectionCallback;
import com.mobilis.util.Constants;

public class Connection {

	private ConnectionCallback callback;

	public Connection(ConnectionCallback callback) {
		this.callback = callback;
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
			Log.i("Connection", "Audio is Null");
		} else {
			Log.i("Connection", "Audio not Null");
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
			Log.i("TESTE1", "");
			if (conn.get != null) {
				conn.get.abort();
				conn.cancel(true);
				conn.get = null;
			} else {
				conn.cancel(true);
			}

		} else if (conn.connectionType == Constants.TYPE_CONNECTION_POST) {
			Log.i("TESTE2", "");
			if (conn.post != null) {
				conn.post.abort();
				conn.cancel(true);
				conn.post = null;
			} else {
				conn.cancel(true);
			}
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

			String content = null;

			if (result != null) {
				if (result[1] != null) {
					if (statusCode != 699) {
						statusCode = (Integer) result[1];
					}
				}
				if (result[0] != null) {
					content = (String) result[0];
				}
			}

			Log.w("STATUS CODE", "" + statusCode);
			callback.resultFromConnection(connectionId, content, statusCode);
			super.onPostExecute(result);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			callback.resultFromConnection(connectionId, null, statusCode);
		}
	}

	private class ConnectionWatcher extends AsyncTask<Object, Void, Integer> {

		private ExecuteConnection toWatch = null;

		@Override
		protected Integer doInBackground(Object... params) {
			try {
				toWatch = (ExecuteConnection) params[0];
				toWatch.get(10, TimeUnit.SECONDS);
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
			super.onPostExecute(result);
			if (result == 1) {
			}
		}
	}
}
