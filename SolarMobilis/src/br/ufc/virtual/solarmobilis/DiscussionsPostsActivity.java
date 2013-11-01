package br.ufc.virtual.solarmobilis;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import br.ufc.virtual.solarmobilis.model.DiscussionPost;
import br.ufc.virtual.solarmobilis.model.DiscussionPostList;
import br.ufc.virtual.solarmobilis.model.PostAdapter;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@EActivity
public class DiscussionsPostsActivity extends SherlockFragmentActivity {

	DiscussionPostList discussionPostList;

	@Pref
	SolarMobilisPreferences_ preferences;

	@Bean
	SolarManager solarManager;

	@ViewById(R.id.listViewDiscussionsPosts)
	ListView listVieWDiscussionPosts;

	@Extra("discussionId")
	int discussionId;

	@Extra("discussionName")
	String discussionName;

	@Extra("discussionLastPostDate")
	String discussionLastPostDate;

	@Extra("startDate")
	String startDate;

	@Extra("endDate")
	String endDate;

	@ViewById(R.id.forum_title)
	TextView forumTitle;

	@ViewById(R.id.forum_range)
	TextView forumRange;

	View footerRefresh;
	View footerFuturePosts;

	int UnloadedFuturePosts;
	boolean footerUnloadedFuturePostsState;
	boolean footerFuturePostsState;
	List<DiscussionPost> posts = new ArrayList<DiscussionPost>();
	List<DiscussionPost> newPosts = new ArrayList<DiscussionPost>();
	private ProgressDialog dialog;
	private String oldDateString = "20001010102410";
	private ActionBar actionBar;
	private Boolean postSelected = false;
	private Bitmap userImage;
	private Bitmap userImageEmpty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discussions_posts);
		dialog = ProgressDialog.show(this, "Aguarde", "Recebendo resposta",
				true);
		actionBar = getSupportActionBar();
		LayoutInflater inflater = getLayoutInflater();
		footerFuturePosts = inflater.inflate(
				R.layout.load_available_posts_item, listVieWDiscussionPosts,
				false);
		footerRefresh = inflater.inflate(R.layout.refresh_discussion_list_item,
				listVieWDiscussionPosts, false);
		footerRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refresh_button();
			}
		});

		footerFuturePosts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refresh_button();
			}
		});

	}
	
	@Override
	protected void onResume() {
		posts.clear();
		getPosts();
		super.onResume();
	}

	void setFooter() {

		Log.i("Dentro do setFooter", String.valueOf(UnloadedFuturePosts));

		if (UnloadedFuturePosts > 0) {
			((TextView) footerFuturePosts
					.findViewById(R.id.load_available_posts))
					.setText(UnloadedFuturePosts
							+ " "
							+ getApplicationContext().getResources().getString(
									R.string.not_loaded_posts_count));

			((ImageView) footerFuturePosts.findViewById(R.id.blue_line))
					.setVisibility(View.VISIBLE);
			footerUnloadedFuturePostsState = true;
			listVieWDiscussionPosts
					.addFooterView(footerFuturePosts, null, true);

		} else {

			if (footerUnloadedFuturePostsState == true
					&& footerFuturePostsState == false) {
				listVieWDiscussionPosts.removeFooterView(footerFuturePosts);
				listVieWDiscussionPosts
						.addFooterView(footerRefresh, null, true);
				footerUnloadedFuturePostsState = false;
				footerFuturePostsState = true;
			} else if (footerUnloadedFuturePostsState == false
					&& footerFuturePostsState == false) {
				listVieWDiscussionPosts
						.addFooterView(footerRefresh, null, true);
				footerFuturePostsState = true;
			}
		}

	}

	@Background
	void refresh_button() {

		int discussionSize = posts.size();

		if (discussionSize == 0) {
			oldDateString = "20001010102410";

		} else {
			oldDateString = posts.get(posts.size() - 1).getDateToString();
		}

		discussionPostList = solarManager.getPosts(preferences.token().get(),
				discussionId, oldDateString);
		UnloadedFuturePosts = discussionPostList.getAfter();

		for (int i = 0; i < discussionPostList.getPosts().size(); i++) {

			Log.i("#" + i, discussionPostList.getPosts().get(i).getContent());
		}

		newPosts = discussionPostList.getPosts();
		Collections.reverse(newPosts);
		posts.addAll(posts.size(), newPosts);

		for (int i = 0; i < posts.size(); i++) {

			Log.i("#" + i, posts.get(i).getContent());
		}

		reUpdateList();
		setFooter();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		MenuInflater inflater = getSupportMenuInflater();
		if (!postSelected) {
			inflater.inflate(R.menu.options_menu_action, menu);
		} else {
			inflater.inflate(R.menu.action_bar_selected, menu);
		}

		return true;

	}

	@OptionsItem(R.id.menu_logout)
	void logout() {
		preferences.token().put(null);
		Intent intent = new Intent(this, LoginActivity_.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	@OptionsItem(R.id.menu_refresh)
	void refresh() {
		dialog = ProgressDialog.show(this, "Aguarde", "Recebendo resposta",
				true);
		posts.clear();
		getPosts();
	}

	@OptionsItem(R.id.menu_response)
	void response() {
		Intent intent = new Intent(this, ResponseActivity_.class);
		intent.putExtra("discussionId", discussionId);
		startActivity(intent);
	}

	@Background
	void getPosts() {

		try {
			discussionPostList = solarManager.getPosts(preferences.token()
					.get(), discussionId, oldDateString);
			List<DiscussionPost> posts = discussionPostList.getPosts();
			for (DiscussionPost discussionPost2 : posts) {
				getUserImage(discussionPost2);
			}

			UnloadedFuturePosts = discussionPostList.getAfter();
			Log.i("after", String.valueOf(discussionPostList.getAfter()));
			Log.i("id usuario",
					String.valueOf(discussionPostList.getPosts().get(1)
							.getUserId()));
			updateList();
			setFooter();

		} catch (HttpClientErrorException e) {
			Log.i("ERRO", e.getStatusCode().toString());
			dialog.dismiss();
			solarManager.errorHandler(e.getStatusCode());

		} catch (ResourceAccessException e) {
			dialog.dismiss();
			solarManager.alertTimeout();
		}
	}

	@Background
	public void getUserImage(DiscussionPost discussionPost) {
		userImage = null;
		userImageEmpty = BitmapFactory.decodeResource(getResources(),
				R.drawable.no_picture);

		String url = solarManager.getUserImageUrl(discussionPost.getUserId(),
				preferences.token().get());

		try {
			URL aURL = new URL(url);
			URLConnection connection = aURL.openConnection();
			connection.connect();
			InputStream iS = connection.getInputStream();
			BufferedInputStream bIS = new BufferedInputStream(iS);
			userImage = BitmapFactory.decodeStream(bIS);
			bIS.close();
			iS.close();

		} catch (IOException e) {
			Log.e("User Image", "Error download");
			userImage = null;
			
		}

		if (userImage != null) {
			discussionPost.setUserImage(userImage);
		} else {
			discussionPost.setUserImage(userImageEmpty);
		}

	}

	@UiThread
	void updateList() {
		dialog.dismiss();
		forumTitle.setText(discussionName);
		forumRange.setText(startDate + " - " + endDate);

		posts = discussionPostList.getPosts();
		for (int i = 0; i < discussionPostList.getPosts().size(); i++) {

			Log.i("#" + i, discussionPostList.getPosts().get(i).getUpdatedAt() + " "
					+ discussionPostList.getPosts().get(i).getDateToString());
		}
		Collections.reverse(posts);
		PostAdapter adapter = new PostAdapter(this,
				R.layout.discussion_list_item, R.id.user_nick, posts);

		listVieWDiscussionPosts.setAdapter(adapter);

	}

	@UiThread
	void reUpdateList() {
		PostAdapter adapter = new PostAdapter(this,
				R.layout.discussion_list_item, R.id.user_nick, posts);

		listVieWDiscussionPosts.setAdapter(adapter);
	}

	@ItemClick
	void listViewDiscussionsPosts() {
		Log.i("clicado", "clicado");
		postSelected = true;
		actionBarSelected();

	}

	void actionBarSelected() {
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(R.color.action_bar_active)));
		invalidateOptionsMenu();
	}

	void actionBarNotSelected() {
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(R.color.action_bar_idle)));
	}

	@Override
	public void onBackPressed() {
		if (!postSelected) {
			super.onBackPressed();

		} else {
			postSelected = false;
			actionBarNotSelected();
			invalidateOptionsMenu();
		}
	}
}
