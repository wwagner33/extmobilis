package com.mobilis.threads;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mobilis.controller.Constants;
import com.mobilis.controller.ErrorHandler;
import com.mobilis.ws.Connection;

public abstract class ConnectionThread extends AsyncTask<Void, Void, Object[]> {

	protected Connection connection;
	private Context context;

	public ConnectionThread(Context context) {
		this.context = context;
		connection = new Connection();

	}

	@Override
	protected Object[] doInBackground(Void... params) {
		return connectionMethod();
	}

	public void stopConnectionThread() {
		super.cancel(true);
	}

	@Override
	protected void onPreExecute() {
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
				Thread.sleep(20000);
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
