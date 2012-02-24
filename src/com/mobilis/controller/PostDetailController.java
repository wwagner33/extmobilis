package com.mobilis.controller;

//import com.paulo.android.solarmobile.controller.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PostDetailController extends Activity implements OnClickListener {

	private TextView body, userName, forumName;
	private Bundle extras;
	private Button response;
	private String topicId, forumNameString;
	private long parentId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_detail);
		body = (TextView) findViewById(R.id.post_body_detail);
		forumName = (TextView) findViewById(R.id.nome_forum_detail);
		userName = (TextView) findViewById(R.id.user_name_detail);
		response = (Button) findViewById(R.id.answer_topic);
		response.setOnClickListener(this);

		extras = getIntent().getExtras();
		if (extras != null) {
			body.setText(extras.getString("content"));
			forumName.setText(extras.getString("forumName"));
			forumNameString = extras.getString("forumName");
			userName.setText(extras.getString("username"));
			topicId = extras.getString("topicId");
			parentId = extras.getLong("parentId");
			Log.w("ParentId on DETAILS", String.valueOf(parentId));
		}

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, ResponseController.class);
		intent.putExtra("topicId", topicId);
		intent.putExtra("parentId", parentId);
		intent.putExtra("ForumName", forumNameString);
		startActivity(intent);
	}

}
