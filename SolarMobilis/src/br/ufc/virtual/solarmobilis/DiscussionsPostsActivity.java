package br.ufc.virtual.solarmobilis;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.client.ResourceAccessException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import br.ufc.virtual.solarmobilis.model.DiscussionPostList;
import br.ufc.virtual.solarmobilis.model.DiscussionPosts;
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

//@OptionsMenu(R.menu.options_menu)
@EActivity
public class DiscussionsPostsActivity extends SherlockFragmentActivity {

	DiscussionPostList response;

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

	boolean UnloadedFuturePostsState;

	List<DiscussionPosts> posts = new ArrayList<DiscussionPosts>();
	private ProgressDialog dialog;
	private String oldDateString = "20001010102410";
	private ActionBar actionBar;
	private Boolean postSelected = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discussions_posts);
		dialog = ProgressDialog.show(this, "Aguarde", "Recebendo resposta",
				true);
		actionBar = getSupportActionBar();

		getPosts();

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

				Log.i("old date string do ultimo", oldDateString);

			}
		});

		footerFuturePosts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				refresh_button();

				Log.i("old date string do ultimo", oldDateString);

			}
		});

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

			UnloadedFuturePostsState = true;
			listVieWDiscussionPosts
					.addFooterView(footerFuturePosts, null, true);

		} else {

			if (UnloadedFuturePostsState == true) {
				listVieWDiscussionPosts.removeFooterView(footerFuturePosts);
				listVieWDiscussionPosts
						.addFooterView(footerRefresh, null, true);
				UnloadedFuturePostsState = false;
			} else {
				listVieWDiscussionPosts
						.addFooterView(footerRefresh, null, true);
			}
		}

	}

	@Background
	void refresh_button() {

		int discussionSize = posts.size();

		if (discussionSize == 0) {
			oldDateString = "20001010102410";

		} else {
			oldDateString = posts.get(0).getDateToString();
		}

		response = solarManager.getPosts(preferences.token().get(),
				discussionId, oldDateString);
		UnloadedFuturePosts = response.getAfter();

		for (int i = 0; i < response.getPosts().size(); i++) {

			Log.i("#" + i, response.getPosts().get(i).getContent());
		}

		posts.addAll(0, response.getPosts());

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

	@Background
	void getPosts() {

		try {
			response = solarManager.getPosts(preferences.token().get(),
					discussionId, oldDateString);

			UnloadedFuturePosts = response.getAfter();

			Log.i("after", String.valueOf(response.getAfter()));
			updateList();
			setFooter();
		} catch (ResourceAccessException e) {
			solarManager.alertTimeout();
		}
	}

	@UiThread
	void updateList() {
		dialog.dismiss();
		forumTitle.setText(discussionName);
		forumRange.setText(startDate + " - " + endDate);

		posts = response.getPosts();
		for (int i = 0; i < response.getPosts().size(); i++) {

			Log.i("#" + i, response.getPosts().get(i).getUpdatedAt() + " "
					+ response.getPosts().get(i).getDateToString());
		}

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
		actionBar.setHomeButtonEnabled(false);// --------> Pesquisar
		actionBar.setDisplayShowHomeEnabled(false); // tira logo
		actionBar.setDisplayShowTitleEnabled(false); // Tira titulo
		actionBar.setDisplayUseLogoEnabled(false); // tira logo
		actionBar.setDisplayHomeAsUpEnabled(false); // --------> Pesquisar
		actionBar.setDisplayShowCustomEnabled(true); // Permite a customização
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(R.color.action_bar_active))); // Muda a cor
		invalidateOptionsMenu(); // troca de menus xml --------> Pesquisar
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
