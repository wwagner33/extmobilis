package br.ufc.virtual.solarmobilis;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import br.ufc.virtual.solarmobilis.model.DiscussionPost;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@EActivity(R.layout.activity_response)
public class ResponseActivity extends Activity {

	@Pref
	SolarMobilisPreferences_ preferences;

	@Bean
	SolarManager solarManager;

	DiscussionPost discussionPost = new DiscussionPost();
	PostSender postSender = new PostSender();

	@ViewById(R.id.editTextReply)
	EditText reply;

	@Extra("discussionId")
	Integer discussionId;

	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Click(R.id.submitReply)
	void submit() {
		discussionPost.setContent(reply.getText().toString());
		discussionPost.setDiscussionId(discussionId);
		postSender.setDiscussionPost(discussionPost);

		dialog = ProgressDialog.show(this, "Aguarde", "Enviando", true);

		sendPost();
	}

	@Background
	void sendPost() {
		try {
			solarManager.sendPost(postSender, discussionId);
			toast();
		} catch (HttpStatusCodeException e) {
			Log.i("ERRO HttpStatusCodeException", e.getStatusCode().toString());
			solarManager.errorHandler(e.getStatusCode());
		} catch (Exception e) {
			Log.i("ERRO Exception", e.getMessage());
			solarManager.alertNoConnection();
		} finally {
			dialog.dismiss();
		}
	}

	@UiThread
	void toast() {
		Toast.makeText(this, R.string.send_post_sucess, Toast.LENGTH_SHORT)
				.show();
		finish();
	}

}
