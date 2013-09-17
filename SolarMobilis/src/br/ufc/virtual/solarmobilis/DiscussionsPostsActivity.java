package br.ufc.virtual.solarmobilis;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.client.ResourceAccessException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import br.ufc.virtual.solarmobilis.model.DiscussionPostList;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@OptionsMenu(R.menu.options_menu)
@EActivity
public class DiscussionsPostsActivity extends SherlockFragmentActivity {

	DiscussionPostList response;

	@Pref
	SolarMobilisPreferences_ preferences;

	@Bean
	SolarManager solarManager;

	@ViewById
	ListView listViewDiscussionsPosts;

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

	@ViewById
	TextView forum_title;

	@ViewById
	TextView forum_range;

	List<PostAdapter> posts = new ArrayList<PostAdapter>();
	private ProgressDialog dialog;
	private String oldDateString = "2000101010241010";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discussions_posts);
		dialog = ProgressDialog.show(this, "Aguarde", "Recebendo resposta",
				true);
		getPosts();
	}

	@OptionsItem(R.id.menu_logout)
	void logout() {
		preferences.token().put(null);
		Intent intent = new Intent(this, LoginActivity_.class);
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
			updateList();
		} catch (ResourceAccessException e) {
			solarManager.alertTimeout();
		}
	}

	@UiThread
	void updateList() {
		dialog.dismiss();
		forum_title.setText(discussionName);
		forum_range.setText(startDate + " - " + endDate);

		for (int i = 0; i < response.getPosts().size(); i++) {

			posts.add(new PostAdapter(response.getPosts().get(i).getUserNick(),
					response.getPosts().get(i).getContent(), response
							.getPosts().get(i).getUpdatedAt()));
		}

		ArrayAdapter<PostAdapter> adapter = new MyListAdapter();
		ListView list = (ListView) findViewById(R.id.listViewDiscussionsPosts);
		list.setAdapter(adapter);

	}

	private class MyListAdapter extends ArrayAdapter<PostAdapter> {
		public MyListAdapter() {
			super(DiscussionsPostsActivity.this, R.layout.discussion_list_item,
					posts);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View item = convertView;
			if (item == null) {
				item = getLayoutInflater().inflate(
						R.layout.discussion_list_item, parent, false);
			}

			PostAdapter currentCar = posts.get(position);

			TextView nomeText = (TextView) item.findViewById(R.id.user_nick);
			nomeText.setText(currentCar.getNome());

			TextView dataText = (TextView) item.findViewById(R.id.post_date);
			dataText.setText("" + currentCar.getData());

			TextView postText = (TextView) item.findViewById(R.id.post_content);
			postText.setText(currentCar.getPost());

			return item;

		}

	}
}
