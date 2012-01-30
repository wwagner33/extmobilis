package com.paulo.android.solarmobile.controller;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class PostDetailController extends Activity {

	TextView body, userName, forumName;
	Bundle extras;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_detail);
		body = (TextView) findViewById(R.id.post_body_detail);
		forumName = (TextView) findViewById(R.id.nome_forum_detail);
		userName = (TextView) findViewById(R.id.user_name_detail);

		extras = getIntent().getExtras();
		if (extras != null) {
			body.setText(extras.getString("content"));
			forumName.setText(extras.getString("forumName"));
			userName.setText(extras.getString("username"));
		}

	}

}
