package com.paulo.android.solarmobile.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.paulo.android.solamobile.threads.RequestTokenThread;
import com.paulo.android.solarmobile.model.DBAdapter;
import com.paulo.android.solarmobile.ws.Connection;

public class Login extends Activity implements OnClickListener {
	public EditText login, password;
	public Button submit;
	public Button skipValidation;
	public Intent intent;
	JSONObject json;
	DBAdapter adapter;
	Connection connection;
	ProgressDialog dialog;
	String authToken;
	ParseJSON jsonParser;

	RequestToken requestToken;
	ObtainCourseListThread obtainCourseListThread;

	// ConnectionLimit threadLimit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		login = (EditText) findViewById(R.id.campo1);
		password = (EditText) findViewById(R.id.campo2);
		submit = (Button) findViewById(R.id.submit);
		submit.setOnClickListener(this);

		adapter = new DBAdapter(this);
	}

	@Override
	public void onClick(View v) {

		if (v.equals(submit)) {

			/*
			 * Validação do Login
			 * 
			 * if (LoginField.getText().toString().equals("") ||
			 * PassField.getText().toString().equals("")) {
			 * 
			 * Toast.makeText(this,
			 * " campos login ou senha não podem ser vazios",
			 * Toast.LENGTH_SHORT).show(); }
			 * 
			 * else {
			 * 
			 * 
			 * /* adapter.open(); ContentValues valores = new ContentValues();
			 * valores.put("nome", PassField.getText().toString());
			 * adapter.updateTable("config", 1, valores);
			 */

			boolean ok = true;
			if (ok) {

				dialog = Dialogs.getProgressDialog(this);
				dialog.show();

				connection = new Connection(this);

				requestToken();
			}
		}
	}

	public void closeDialogIfItsVisible() {
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();

	}

	public void requestToken() {
		// threadLimit = new ConnectionLimit();
		requestToken = new RequestToken(this);
		jsonParser = new ParseJSON();

		requestToken.setConnectionParameters(jsonParser.buildTokenObject(login
				.getText().toString(), password.getText().toString()),
				Constants.URL_TOKEN);
		requestToken.execute();
		// threadLimit.execute("token");
	}

	public void getCourseList(String token) {
		// threadLimit = new ConnectionLimit();
		obtainCourseListThread = new ObtainCourseListThread();
		obtainCourseListThread.execute(token);
		// threadLimit.execute("GET");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (adapter != null) {
			adapter.close();
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(this, InitialConfig.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("FinishActivity", "YES");
		startActivity(intent);
	}

	public ProgressDialog createDialog() {
		ProgressDialog dialog;
		dialog = new ProgressDialog(this);
		dialog.setMessage("Carregando, Por favor aguarde...");
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		return dialog;
	}

	public class RequestToken extends RequestTokenThread {

		public RequestToken(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onTokenConnectionFailed() {
			closeDialogIfItsVisible();

		}

		@Override
		public void onTokenConnectionSucceded() {
			adapter.open();
			String token = adapter.getToken();
			adapter.close();
			getCourseList(token);

		}
	}

	public class ObtainCourseListThread extends
			AsyncTask<String, Void, Object[]> {

		@Override
		protected Object[] doInBackground(String... params) {
			try {
				return connection.getFromServer("curriculum_units.json",
						params[0]);
				// return result;

			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return null;

			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Object[] result) {
			super.onPostExecute(result);

			if (result == null) {
				closeDialogIfItsVisible();
				ErrorHandler.handleError(getApplicationContext(),
						Constants.ERROR_CONNECTION_REFUSED);
			}

			else {
				int statusCode = (Integer) result[1];
				if (statusCode == 200) {
					Log.w("TokenResult", (String) result[0]);

					adapter.open();

					// adapter.insertCourses(result);

					adapter.updateCourses((String) result[0]);

					// ContentValues[] parsedValues = connection.parse(result);
					intent = new Intent(getApplicationContext(),
							CourseListController.class);
					intent.putExtra("CourseList", (String) result[0]);

					dialog.dismiss();
					startActivityForResult(intent, 10);

				} else {
					closeDialogIfItsVisible();
					ErrorHandler.handleError(getApplicationContext(),
							statusCode);
				}
			}
		}
	}

	/*
	 * public class ConnectionLimit extends AsyncTask<String, Void, Integer> {
	 * 
	 * @Override protected Integer doInBackground(String... arg0) { if
	 * (arg0[0].equals("token")) { try { Thread.sleep(5000); if
	 * (requestTokenThread.getStatus() == AsyncTask.Status.RUNNING) {
	 * connection.StopPost(); return 1; }
	 * 
	 * } catch (InterruptedException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * } else { try { Thread.sleep(5000); if (obtainCourseListThread.getStatus()
	 * == AsyncTask.Status.RUNNING) { connection.StopPost(); return 1; }
	 * 
	 * } catch (InterruptedException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 * 
	 * return 0; }
	 * 
	 * @Override protected void onPostExecute(Integer result) { // TODO
	 * Auto-generated method stub super.onPostExecute(result); if (result == 1)
	 * { ErrorHandler.handleError(getApplicationContext(),
	 * Constants.ERROR_CONNECTION_TIMEOUT); }
	 * 
	 * }
	 * 
	 * }
	 */

}