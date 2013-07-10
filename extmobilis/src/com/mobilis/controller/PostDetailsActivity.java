package com.mobilis.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mobilis.dao.DatabaseHelper;
import com.mobilis.dao.DiscussionDAO;
import com.mobilis.dao.PostDAO;
import com.mobilis.model.Discussion;
import com.mobilis.model.Post;
import com.mobilis.util.MobilisPreferences;

public class PostDetailsActivity extends Activity implements OnClickListener {

	private TextView body, userName, forumName;
	private Bundle extras;
	private ImageView response, avatar;
	private Discussion discussion;
	private Post post;
	private DiscussionDAO discussionDAO;
	private PostDAO postDAO;
	private MobilisPreferences appState;
	private DatabaseHelper helper = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.post_detail);
		helper = getHelper();
		appState = MobilisPreferences.getInstance(this);
		discussionDAO = new DiscussionDAO(helper);
		postDAO = new PostDAO(helper);
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
			Log.i("EXTRA", "NOT NULL");
			if (extras.get("image") != null) {
				Log.i("IMAGE", "NOT NULL");
				avatar.setImageBitmap((Bitmap) extras.get("image"));
			}
		}
		discussion = discussionDAO.getDiscussion(appState.selectedDiscussion);
		post = postDAO.getPost(appState.selectedPost);

		body.setText(post.getContent());
		userName.setText(post.getUserNick());
		forumName.setText(discussion.getName());
	}

	@Override
	protected void onDestroy() {
		if (helper != null) {
			OpenHelperManager.releaseHelper();
			helper = null;
		}
		super.onDestroy();
	}

	private DatabaseHelper getHelper() {
		if (helper == null) {
			helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		return helper;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, ResponseActivity.class);
		startActivity(intent);
	}
}
