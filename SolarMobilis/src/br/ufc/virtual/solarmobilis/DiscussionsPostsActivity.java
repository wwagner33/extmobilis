package br.ufc.virtual.solarmobilis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.springframework.web.client.HttpStatusCodeException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import br.ufc.virtual.solarmobilis.audio.PostPlayer;
import br.ufc.virtual.solarmobilis.model.DiscussionPost;
import br.ufc.virtual.solarmobilis.model.DiscussionPostList;
import br.ufc.virtual.solarmobilis.model.PostAdapter;
import br.ufc.virtual.solarmobilis.util.Toaster;
import br.ufc.virtual.solarmobilis.webservice.PostPlayerListener;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

@EActivity
public class DiscussionsPostsActivity extends SherlockFragmentActivity
		implements PostPlayerListener {

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

	@ViewById(R.id.button_play)
	ImageButton play;

	@ViewById(R.id.button_stop)
	ImageButton stop;

	@ViewById(R.id.button_prev)
	ImageButton prev;

	@ViewById(R.id.button_next)
	ImageButton next;

	@StringRes(R.string.dialog_wait)
	String dialogWait;

	@StringRes(R.string.dialog_message)
	String dialogMessage;

	@StringRes(R.string.post_list_last)
	String lastPostMessage;

	@StringRes(R.string.post_list_first)
	String firstPostMessage;

	@StringRes(R.string.ERROR_CONECTION)
	String conectinErrortMessage;

	@StringRes(R.string.ERROR_WHILE_PLAYING_ATTACH)
	String errorWhilePlayingAttach;

	@StringRes(R.string.ERROR_AUDIO_DOWNLOAD)
	String audioDownloadError;

	@Bean
	Toaster toaster;

	@Bean
	PostPlayer postPlayer;

	List<String> fileDescriptors = new ArrayList<String>();
	MediaPlayer mp = new MediaPlayer();

	View footerRefresh;
	View footerFuturePosts;

	int unloadedFuturePostsCount;
	int selectedPosition = -1;
	boolean footerUnloadedFuturePostsState;
	boolean footerFuturePostsState;
	List<DiscussionPost> posts = new ArrayList<DiscussionPost>();
	List<DiscussionPost> newPosts = new ArrayList<DiscussionPost>();
	private ProgressDialog dialog;
	private String oldDateString = "20001010102410";
	private ActionBar actionBar;
	private boolean postSelected = false;

	PostAdapter adapter;

	File file = new File(Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/Mobilis/TTS/");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discussions_posts);
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
				refreshPostsList();
			}
		});

		footerFuturePosts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshPostsList();
			}
		});

		postPlayer.setPostPlayerListener(this);
	}

	@Override
	protected void onResume() {
		posts.clear();
		getPosts();
		super.onResume();
	}

	@UiThread
	public void makeDialog() {
		dialog = ProgressDialog.show(this, dialogWait, dialogMessage, true);
	}

	// @UiThread
	void setFooter() {

		if (!footerFuturePostsState) {

			listVieWDiscussionPosts.addFooterView(footerRefresh, null, true);
			footerFuturePostsState = true;

		}

		Log.i("Dentro do setFooter", String.valueOf(unloadedFuturePostsCount));
		/*
		 * if (unloadedFuturePostsCount > 0) { ((TextView) footerFuturePosts
		 * .findViewById(R.id.load_available_posts))
		 * .setText(unloadedFuturePostsCount + " " +
		 * getApplicationContext().getResources().getString(
		 * R.string.not_loaded_posts_count));
		 * 
		 * ((ImageView) footerFuturePosts.findViewById(R.id.blue_line))
		 * .setVisibility(View.VISIBLE); footerUnloadedFuturePostsState = true;
		 * listVieWDiscussionPosts .addFooterView(footerFuturePosts, null,
		 * true);
		 * 
		 * } else {
		 * 
		 * if (footerUnloadedFuturePostsState == true && footerFuturePostsState
		 * == false) {
		 * listVieWDiscussionPosts.removeFooterView(footerFuturePosts);
		 * listVieWDiscussionPosts .addFooterView(footerRefresh, null, true);
		 * footerUnloadedFuturePostsState = false; footerFuturePostsState =
		 * true; } else if (footerUnloadedFuturePostsState == false &&
		 * footerFuturePostsState == false) { listVieWDiscussionPosts
		 * .addFooterView(footerRefresh, null, true); footerFuturePostsState =
		 * true; } }
		 */

	}

	@Click({ R.id.refresh_button })
	@Background
	void refreshPostsList() {
		makeDialog();
		try {

			int discussionSize = posts.size();
			/*
			 * if (discussionSize == 0) { oldDateString = "20001010102410"; }
			 * else { oldDateString = posts.get(0).getDateToString(); }
			 */

			oldDateString = "20001010102410";

			posts.clear();

			discussionPostList = solarManager.getPosts(discussionId,
					oldDateString, preferences.groupSelected().get());
			unloadedFuturePostsCount = discussionPostList.getOlder();

			for (int i = 0; i < discussionPostList.getPosts().size(); i++) {
				Log.i("#" + i + " " + oldDateString, discussionPostList
						.getPosts().get(i).getContent());
			}

			posts = discussionPostList.getPosts();

			for (DiscussionPost discussionPost : posts) {
				discussionPost.setUserImageURL(solarManager
						.getUserImageUrl(discussionPost.getUserId()));
			}

			for (int i = 0; i < posts.size(); i++) {
				Log.i("#" + i + " " + oldDateString, posts.get(i).getContent());
			}

		} catch (HttpStatusCodeException e) {
			Log.i("ERRO HttpClientErrorException", e.getStatusCode().toString());
			solarManager.errorHandler(e.getStatusCode());
		} catch (Exception e) {
			Log.i("ERRO ResourceAccessException", e.getMessage());
			solarManager.alertNoConnection();
		} finally {
			dialog.dismiss();
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
		solarManager.logout();
	}

	@OptionsItem(R.id.menu_response)
	void response() {
		Intent intent = new Intent(this, ResponseActivity_.class);
		intent.putExtra("discussionId", discussionId);
		startActivity(intent);
	}

	@OptionsItem(R.id.showPlayerControls)
	void showPlayControls() {
		play.setVisibility(View.VISIBLE);
		prev.setVisibility(View.VISIBLE);
		next.setVisibility(View.VISIBLE);
		stop.setVisibility(View.VISIBLE);
	}

	@Background
	void getPosts() {

		try {
			makeDialog();
			discussionPostList = solarManager.getPosts(discussionId,
					oldDateString, preferences.groupSelected().get());
			List<DiscussionPost> posts = discussionPostList.getPosts();
			for (DiscussionPost discussionPost : posts) {
				discussionPost.setUserImageURL(solarManager
						.getUserImageUrl(discussionPost.getUserId()));
			}

			unloadedFuturePostsCount = discussionPostList.getOlder();
			Log.i("after", String.valueOf(discussionPostList.getOlder()));

			updateList();
			setFooter();
		} catch (HttpStatusCodeException e) {
			Log.i("ERRO HttpStatusCodeException", e.getStatusCode().toString());
			solarManager.errorHandler(e.getStatusCode());
		} catch (Exception e) {
			Log.i("ERRO Exception", e.getMessage());
			solarManager.alertNoConnection();
		} finally {
			if (dialog != null)
				dialog.dismiss();
		}
	}

	@UiThread
	void updateList() {
		dialog.dismiss();
		forumTitle.setText(discussionName);
		forumRange.setText(startDate + " - " + endDate);

		posts = discussionPostList.getPosts();
		for (int i = 0; i < discussionPostList.getPosts().size(); i++) {

			Log.i("#" + i, discussionPostList.getPosts().get(i).getUpdatedAt()
					+ " "
					+ discussionPostList.getPosts().get(i).getDateToString());
		}
		// Collections.reverse(posts);
		adapter = new PostAdapter(this, R.layout.discussion_list_item,
				R.id.user_nick, posts);

		listVieWDiscussionPosts.setAdapter(adapter);

		if (postSelected) {
			setMarketPost(selectedPosition, true);
		}

	}

	@UiThread
	void reUpdateList() {
		dialog.dismiss();
		adapter = new PostAdapter(this, R.layout.discussion_list_item,
				R.id.user_nick, posts);

		listVieWDiscussionPosts.setAdapter(adapter);

		if (postSelected) {
			setMarketPost(selectedPosition, true);
		}

	}

	@ItemClick
	void listViewDiscussionsPosts(int position) {

		Log.i("clicado (activity de posts)", "ENTROU NO LISTNER");
		Log.i("clicado (activity de posts)", "clicado no " + position);
		togglePostMarked(position);
	}

	@UiThread
	public void togglePostMarked(int position) {

		if (selectedPosition == position) {
			setMarketPost(position, false);

			postSelected = false;
			setActionBarNotSelected();
			removePlayControls();
			stop();
			selectedPosition = -1;
		} else {
			setMarketPost(position, true);

			if (selectedPosition != -1) {
				setMarketPost(selectedPosition, false);
			}

			selectedPosition = position;
			postSelected = true;
			setActionBarSelected();

			if (posts.get(selectedPosition).getFiles().isEmpty()) {
				Log.w("Anexo", "n�o preenchido");

			} else {
				Log.w("Anexo", "preenchido");
				Log.w("Anexo", posts.get(selectedPosition).getFiles()
						.toString());
			}
		}

		listVieWDiscussionPosts.setSelection(position);
		// listVieWDiscussionPosts.smoothScrollToPosition(position);
		Log.i("smoothScrollToPosition", String.valueOf(position));

		Log.i("toglle-marked", String.valueOf(selectedPosition));
	}

	void setActionBarSelected() {
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

	void setActionBarNotSelected() {
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(R.color.action_bar_idle)));
		invalidateOptionsMenu();
	}

	@Click(R.id.button_play)
	void play() {

		if (postPlayer.isPlaying()) {
			Log.i("estava tocando", "agora pausar");
			postPlayer.pause();
			setImagePlayer();

		} else {
			if (postPlayer.isPaused()) {
				Log.i("estava pausado", "agora continuar");
				postPlayer.play();
				setImagePlayer();
			}
			if (postPlayer.isStoped()) {
				Log.i("estava parado", "agora iniciar");
				postPlayer.play(posts.get(selectedPosition));
				setImagePlayer();
			}
		}
	}

	@UiThread
	void setImagePlayer() {

		Log.i("setImagePlayer teste",
				"IsPaused : " + String.valueOf(postPlayer.isPaused()));
		Log.i("setImagePlayer teste",
				"IsStoped : " + String.valueOf(postPlayer.isStoped()));
		if (postPlayer.isPaused() || postPlayer.isStoped()) {
			play.setImageResource(R.drawable.playback_play);
		} else {
			play.setImageResource(R.drawable.playback_pause);
		}

	}

	@Click(R.id.button_next)
	void next() {
		Log.i("Teste bot�o", "botao next");

		if (selectedPosition == posts.size() - 1) {
			toaster.showToast(lastPostMessage);
			Log.i("Toast", lastPostMessage);
		} else if (selectedPosition < posts.size()) {

			togglePostMarked(selectedPosition + 1);
			postPlayer.play(posts.get(selectedPosition + 1));
			Log.i("#selected-position-atual", String.valueOf(selectedPosition));
			setImagePlayer();
		} else {
			stop();
			Log.e("PAROU AO TENTAR O NEXT E FALTAR LISTA", "STOP()");
		}

	}

	@Click(R.id.button_prev)
	void previous() {
		Log.i("Teste bot�o", "botao previous");

		if (selectedPosition == 0) {
			toaster.showToast(firstPostMessage);
			Log.i("Toast", firstPostMessage);
		} else {
			Log.i("#bfselected-position-atual",
					String.valueOf(selectedPosition));

			togglePostMarked(selectedPosition - 1);
			postPlayer.play(posts.get(selectedPosition - 1));
			Log.i("#selected-position-atual", String.valueOf(selectedPosition));
			setImagePlayer();
		}
	}

	@Click(R.id.button_stop)
	@Background
	void stop() {
		postPlayer.stop();
		setImagePlayer();
	}

	@Override
	public void onBackPressed() {
		if (!postSelected) {
			super.onBackPressed();

		} else {
			setMarketPost(selectedPosition, false);
			selectedPosition = -1;
			postSelected = false;
			setActionBarNotSelected();
			invalidateOptionsMenu();
			removePlayControls();
			stop();
		}
	}

	private void setMarketPost(final int position, final boolean isMarked) {
		if ((position >= 0) && (position < posts.size())) {
			posts.get(position).setMarked(isMarked);
			adapter.notifyDataSetChanged();
		} else {
			selectedPosition = -1;
			postSelected = false;
		}
	}

	void removePlayControls() {
		play.setVisibility(View.GONE);
		prev.setVisibility(View.GONE);
		next.setVisibility(View.GONE);
		stop.setVisibility(View.GONE);
	}

	public void onCompletion() {
		setImagePlayer();
		next();
	}

	@Override
	@UiThread
	public void onPostPlayException(Exception exception) {
		stop();
		toaster.showToast(errorWhilePlayingAttach);
		next();
	}

	@Override
	@UiThread
	public void onPostPlayDownloadException(Exception exception) {
		toaster.showToast(audioDownloadError);
	}
}
