package br.ufc.virtual.solarmobilis;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import android.app.Activity;
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
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_response)
public class ResponseActivity extends Activity {

	@Bean
	SolarManager solarManager;

	DiscussionPost discussionPost = new DiscussionPost();
	PostSender postSender = new PostSender();

	@ViewById(R.id.editTextReply)
	EditText reply;

	@Extra("discussionId")
	Integer discussionId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Click(R.id.submitReply)
	void submit() {
		discussionPost.setContent(reply.getText().toString());
		discussionPost.setDiscussionId(discussionId);
		postSender.setDiscussionPost(discussionPost);

		sendPost();
//		Toast.makeText(this, R.string.send_post_sucess, Toast.LENGTH_SHORT)
//				.show();
//		finish();
	}

	@Background
	void sendPost() {

		try {
			solarManager.sendPosts(postSender, discussionId);
			Log.i("Enviou", "sim");

		} catch (HttpClientErrorException e) {
			Log.i("ERRO", e.getStatusCode().toString());
			solarManager.errorHandler(e.getStatusCode());

		} catch (ResourceAccessException e) {
			solarManager.alertTimeout();
		}

	}

}
