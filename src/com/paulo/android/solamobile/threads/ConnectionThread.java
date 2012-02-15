package com.paulo.android.solamobile.threads;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.paulo.android.solarmobile.controller.Constants;
import com.paulo.android.solarmobile.controller.ErrorHandler;
import com.paulo.android.solarmobile.controller.ParseJSON;
import com.paulo.android.solarmobile.model.DBAdapter;
import com.paulo.android.solarmobile.ws.Connection;

public abstract class ConnectionThread extends AsyncTask<Void, Void, Object[]> {

	Connection connection;
	private Context context;
	// private int statusCode;

	public ParseJSON jsonParser;
	private DBAdapter adapter;

	public ConnectionThread(Context context) {
		this.context = context;
		connection = new Connection(context);
		adapter = new DBAdapter(context);
	}

	@Override
	protected Object[] doInBackground(Void... params) {

		return connectionMethod();
		// return connection.postToServer(jsonObject.toJSONString(), URL);

	}

	public void stopConnectionThread() {

		super.cancel(true);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		startConnectionLimit();
	}

	public boolean isRunning() {
		if (super.getStatus() == AsyncTask.Status.RUNNING) {
			return true;
		} else
			return false;

	}

	@Override
	protected void onPostExecute(Object[] result) {
		super.onPostExecute(result);

		if (result == null) {
			onConnectionFailed();
			ErrorHandler.handleError(context,
					Constants.ERROR_CONNECTION_REFUSED);
		}

		else {
			int statusReturned = (Integer) result[1];

			if (statusReturned == 200 || statusReturned == 201) {
				Log.w("RESULT", (String) result[0]);

				onConnectionSucceded((String) result[0]);

			}

			else {
				ErrorHandler.handleError(context, (Integer) result[1]);
				onConnectionFailed();
			}

		}
	}

	public void startConnectionLimit() {
		StopConnectionThread stop = new StopConnectionThread();
		stop.execute(connectionType());
	}

	public abstract void onConnectionFailed();

	public abstract void onConnectionSucceded(String result);

	public abstract Object[] connectionMethod();

	public abstract int connectionType();

	public class StopConnectionThread extends AsyncTask<Integer, Void, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {

				e.printStackTrace();
				return 1;
			}

			if (params[0] == Constants.TYPE_CONNECTION_GET) {
				if (isRunning()) {

					connection.StopGet();
					stopConnectionThread();
					return 1;
				} else {
					return 0;
				}

			}
			if (params[0] == Constants.TYPE_CONNECTION_POST) {
				if (isRunning()) {

					connection.StopPost();
					stopConnectionThread();
					return 1;
				} else {
					return 0;
				}
			}

			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result != 0) {
				ErrorHandler.handleError(context,
						Constants.ERROR_CONNECTION_TIMEOUT);
				onConnectionFailed();
			}

			super.onPostExecute(result);
		}

	}

}
