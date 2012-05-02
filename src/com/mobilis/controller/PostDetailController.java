package com.mobilis.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class PostDetailController extends Activity implements OnClickListener {

	private TextView body, userName, forumName;
	private Bundle extras;
	private ImageView response, avatar;
	private String topicId;
	private long parentId;
	private SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.post_detail);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		body = (TextView) findViewById(R.id.post_body_detail);
		body.setMovementMethod(new ScrollingMovementMethod());
		forumName = (TextView) findViewById(R.id.nome_forum_detail);
		userName = (TextView) findViewById(R.id.user_name_detail);
		response = (ImageView) findViewById(R.id.answer_topic);
		response.setOnClickListener(this);
		if (settings.getBoolean("isForumClosed", false) == true) {
			response.setVisibility(View.GONE);
		}

		avatar = (ImageView) findViewById(R.id.avatar);

		extras = getIntent().getExtras();
		if (extras != null) {
			body.setText(extras.getString("content"));
			forumName.setText(settings.getString("CurrentForumName", null));
			userName.setText(extras.getString("username"));
			topicId = extras.getString("topicId");
			parentId = extras.getLong("parentId");
			if (extras.get("image") != null) {
				avatar.setImageBitmap((Bitmap) extras.get("image"));
			}
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, ResponseController.class);
		intent.putExtra("topicId", topicId);
		intent.putExtra("parentId", parentId);
		startActivity(intent);
	}

}
