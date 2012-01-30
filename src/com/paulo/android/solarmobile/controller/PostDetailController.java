package com.paulo.android.solarmobile.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PostDetailController extends Activity implements OnClickListener {

	TextView body, userName, forumName;
	Bundle extras;
	Button response;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_detail);
		body = (TextView) findViewById(R.id.post_body_detail);
		forumName = (TextView) findViewById(R.id.nome_forum_detail);
		userName = (TextView) findViewById(R.id.user_name_detail);
		response = (Button)findViewById(R.id.answer_topic);
		response.setOnClickListener(this);

		extras = getIntent().getExtras();
		if (extras != null) {
			body.setText(extras.getString("content"));
			forumName.setText(extras.getString("forumName"));
			userName.setText(extras.getString("username"));
		}
		
		
		

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this,ResponseController.class);
		startActivity(intent);
	}

}
