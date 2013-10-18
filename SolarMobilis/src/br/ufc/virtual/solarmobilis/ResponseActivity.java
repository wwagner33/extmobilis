package br.ufc.virtual.solarmobilis;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import br.ufc.virtual.solarmobilis.model.DiscussionPost;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity
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
		setContentView(R.layout.activity_response);

		Log.i("id", String.valueOf(discussionId));
	}

	@Click(R.id.replySubmit)
	void submit() {

		discussionPost.setContent(reply.getText().toString());
		discussionPost.setDiscussionId(discussionId);

		Log.i("1", discussionPost.getContent());
		Log.i("2", String.valueOf(discussionPost.getDiscussionId()));
		// Log.i("3", String.valueOf(discussionPost.getParentId()));

		postSender.setDiscussionPost(discussionPost);

	}

	@Background
	void sendPost() {
		solarManager.sendPosts(postSender, discussionId);
	}

}
