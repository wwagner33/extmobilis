package com.mobilis.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilis.dao.DiscussionDAO;
import com.mobilis.dao.PostDAO;
import com.mobilis.interfaces.MobilisActivity;
import com.mobilis.model.Discussion;
import com.mobilis.model.DiscussionPost;
import com.mobilis.util.MobilisStatus;

public class PostDetailController extends MobilisActivity implements
		OnClickListener {

	private TextView body, userName, forumName;
	private Bundle extras;
	private ImageView response, avatar;
	private Discussion discussion;
	private DiscussionPost post;
	private DiscussionDAO discussionDAO;
	private PostDAO postDAO;
	private MobilisStatus appState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.post_detail);
		appState = MobilisStatus.getInstance();
		discussionDAO = new DiscussionDAO(this);
		postDAO = new PostDAO(this);
		body = (TextView) findViewById(R.id.post_body_detail);
		body.setMovementMethod(new ScrollingMovementMethod());
		forumName = (TextView) findViewById(R.id.nome_forum_detail);
		userName = (TextView) findViewById(R.id.user_name_detail);
		response = (ImageView) findViewById(R.id.answer_topic);
		response.setOnClickListener(this);
		if (appState.forumClosed == true) {
			response.setVisibility(View.GONE);
		}
		avatar = (ImageView) findViewById(R.id.avatar);
		extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.get("image") != null) {
				avatar.setImageBitmap((Bitmap) extras.get("image"));
			}
		}
		discussionDAO.open();
		discussion = discussionDAO.getDiscussion(appState.selectedDiscussion);
		discussionDAO.close();
		postDAO.open();
		post = postDAO.getPost(appState.selectedPost);
		postDAO.close();

		body.setText(post.getContent());
		userName.setText(post.getUserNick());
		forumName.setText(discussion.getName());
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, ResponseController.class);
		startActivity(intent);
	}
}
