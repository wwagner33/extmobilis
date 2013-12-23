package br.ufc.virtual.solarmobilis.webservice.mobilis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class Connection {

	private ConnectionCallback callback;

	public Connection(ConnectionCallback callback) {
		this.callback = callback;
	}

	public void postToServer(int connectionId, String jsonString, String url) {
		ExecuteConnection connection = new ExecuteConnection();
		ConnectionWatcher watcher = new ConnectionWatcher();

		connection.connectionType = Constants.TYPE_CONNECTION_POST;
		connection.connectionId = connectionId;
		connection.url = url;

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
		connection.file = audioFile;

		connection.execute();
		watcher.execute(connection);
	}

	private Object[] executeAudioPost(String URL, File audioFile)
			throws IllegalStateException, IOException {

		Object resultSet[] = new Object[2];

		if (audioFile == null) {
			Log.i("Connection", "Audio is Null");
		} else {
			Log.i("Connection", "Audio not Null");
		}

		DefaultHttpClient client = new DefaultHttpClient();

		StringBuilder builder = new StringBuilder();

		HttpPost post = new HttpPost(URL);

		MultipartEntity entity = new MultipartEntity();
		File file = audioFile;

		entity.addPart("post_file", new FileBody(file, "audio/aac", "UTF-8"));

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

	private void abortConnection(ExecuteConnection conn) {
		if (conn.connectionType == Constants.TYPE_CONNECTION_POST) {
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
		public int connectionType;
		public String url = null;
		public File file = null;
		public int statusCode = 0;
		public HttpPost post = null;

		@Override
		protected Object[] doInBackground(Void... params) {

			try {

				if (connectionType == Constants.TYPE_CONNECTION_POST) {

					if (connectionId == Constants.CONNECTION_POST_AUDIO) {
						return executeAudioPost(url, file);

					}
				}
			} catch (ClientProtocolException e) {
				return null;
			} catch (IOException e) {
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
		}
	}
}
