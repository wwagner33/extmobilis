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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import br.ufc.virtual.solarmobilis.audio.PostPlayer;
import br.ufc.virtual.solarmobilis.model.DiscussionPost;
import br.ufc.virtual.solarmobilis.model.DiscussionPostList;
import br.ufc.virtual.solarmobilis.util.Toaster;
import br.ufc.virtual.solarmobilis.webservice.PostPlayerListener;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;
import br.virtual.solarmobilis.view.PostAdapter;

@EActivity
public class DiscussionPostsActivity extends ActionBarActivity implements
		PostPlayerListener {

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

	@Extra("status")
	String status;

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

	@StringRes(R.string.closed_discussion_message)
	String closedDiscussionMessage;

	@StringRes(R.string.not_allow_direct_response)
	String notAllowDirectResponse;

	@StringRes(R.string.post_delete)
	String postDelete;

	@Bean
	Toaster toaster;

	@Bean
	PostPlayer postPlayer;

	@Bean
	PostAdapter postAdapter;

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
	private boolean canDelete = true;

	PostAdapter adapter;

	File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discussion_posts);

		file = new File(getApplicationContext().getCacheDir() + "/Mobilis/TTS/");

		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
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

		postPlayer.setDir(file);
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

	@UiThread
	void setFooter() {
		if (!footerFuturePostsState) {
			listVieWDiscussionPosts.addFooterView(footerRefresh, null, true);
			footerFuturePostsState = true;
		}

		Log.i("Dentro do setFooter", String.valueOf(unloadedFuturePostsCount));
	}

	@Click({ R.id.refresh_button })
	@Background
	@UiThread
	void refreshPostsList() {
		getPosts();
		setFooter();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		MenuInflater inflater = getMenuInflater();
		if (!postSelected) {
			inflater.inflate(R.menu.options_menu_action, menu);
		} else {
			inflater.inflate(R.menu.action_bar_selected, menu);
			menu.findItem(R.id.menu_post_delete).setEnabled(canDelete);
			menu.findItem(R.id.menu_post_delete).setVisible(canDelete);
		}

		return true;
	}

	@OptionsItem(R.id.menu_logout)
	void logout() {
		solarManager.logout();
	}

	@OptionsItem(R.id.menu_response)
	void response() {
		if (isDiscussionClosed()) {
			toaster.showToast(closedDiscussionMessage);
		} else {
			Intent intent = new Intent(this, ResponseActivity_.class);
			intent.putExtra("discussionId", discussionId);
			int parentPostId = 0;
			if (postSelected) {
				if (posts.get(selectedPosition).getLevel() == 4) {
					toaster.showToast(notAllowDirectResponse);
					parentPostId = posts.get(selectedPosition).getParentId();
				} else {
					parentPostId = posts.get(selectedPosition).getId();
				}
			}
			intent.putExtra("parentPostId", parentPostId);
			startActivity(intent);
		}
	}

	@OptionsItem(R.id.menu_post_response)
	void responseMessage() {
		response();
	}

	@OptionsItem(R.id.menu_post_delete)
	void deleteMessage() {
		deletePost();
		toaster.showToast(postDelete);
		onBackPressed();
	}

	@UiThread
	void updatePost() {
		getPosts();
	}

	@Background
	void deletePost() {
		try {
			DiscussionPost dp = null;
			dp = solarManager.deletePost(posts.get(selectedPosition).getId());
			int i = 0;
			while ((dp == null) && i < 10) {
				wait(100);
				i++;
			}
			updatePost();
		} catch (HttpStatusCodeException e) {
			Log.i("ERRO HttpStatusCodeException", e.getStatusCode().toString());
			solarManager.errorHandler(e.getStatusCode());
		} catch (Exception e) {
			solarManager.alertNoConnection();
		} finally {
			dialogDismiss();
		}
	}

	@Background
	void checkParentId() {
		int position = selectedPosition - 1;
		int postId = posts.get(selectedPosition).getId();
		int parentId = 0;
		canDelete = true;
		if (preferences.userId().get() == posts.get(selectedPosition).userId) {
			if (posts.get(selectedPosition).getLevel() == 4) {
				canDelete = true;
			} else {
				while (canDelete && position >= 0) {
					if (posts.get(position).getLevel() == 1) {
						Log.i("teste", "não tem parent");
					} else {
						parentId = posts.get(position).getParentId();
					}
					if (postId == parentId) {
						canDelete = false;
						Log.i("teste", "encontrou");
					} else {
						canDelete = true;
					}
					position--;
					Log.i("teste", "" + position);
				}
			}
		} else {
			canDelete = false;
		}
	}

	@UiThread
	protected void dialogDismiss() {
		if (dialog != null)
			dialog.dismiss();
	}

	private boolean isDiscussionClosed() {
		return status.equals("2");
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
			posts = discussionPostList.getPosts();
			updateList();
		} catch (HttpStatusCodeException e) {
			Log.i("ERRO HttpStatusCodeException", e.getStatusCode().toString());
			solarManager.errorHandler(e.getStatusCode());
		} catch (Exception e) {
			Log.i("ERRO Exception", e.getMessage());
			solarManager.alertNoConnection();
		} finally {
			if (dialog != null) {
				dialog.dismiss();
			}
		}

		for (DiscussionPost discussionPost : posts) {
			discussionPost.setUserImageURL(solarManager
					.getUserImageUrl(discussionPost.getUserId()));
		}

		setFooter();
	}

	@UiThread
	void updateList() {
		getActionBar().setTitle(discussionName);
		getActionBar().setSubtitle(startDate + " - " + endDate);

		postAdapter.setPosts(posts);
		bindAdapter();
		if (postSelected) {
			setMarkedPost(selectedPosition, true);
		}
	}

	void bindAdapter() {
		listVieWDiscussionPosts.setAdapter(postAdapter);
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
			setMarkedPost(position, false);

			postSelected = false;
			setActionBarNotSelected();
			removePlayControls();
			stop();
			selectedPosition = -1;
		} else {
			if (selectedPosition != -1) {
				setMarkedPost(selectedPosition, false);
			}

			setMarkedPost(position, true);

			selectedPosition = position;
			postSelected = true;
			checkParentId();
			setActionBarSelected();

			if (posts.get(selectedPosition).getFiles().isEmpty()) {
				Log.w("Anexo", "nao preenchido");
			} else {
				Log.w("Anexo", "preenchido");
				Log.w("Anexo", posts.get(selectedPosition).getFiles()
						.toString());
			}
		}
	}

	void setActionBarSelected() {
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(R.color.action_bar_active)));
		invalidateOptionsMenu();
	}

	void setActionBarNotSelected() {
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
		Log.i("Teste botao", "botao next");

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
		Log.i("Teste botao", "botao previous");

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
	@UiThread
	public void onBackPressed() {
		if (!postSelected) {
			super.onBackPressed();
		} else {
			setMarkedPost(selectedPosition, false);
			selectedPosition = -1;
			postSelected = false;
			setActionBarNotSelected();
			invalidateOptionsMenu();
			removePlayControls();
			stop();
		}
	}

	private void setMarkedPost(final int position, final boolean isMarked) {
		if ((position >= 0) && (position < posts.size())) {
			posts.get(position).setMarked(isMarked);
			postAdapter.notifyDataSetChanged();

			listVieWDiscussionPosts.setItemChecked(position, isMarked);
			listVieWDiscussionPosts.setSelection(listVieWDiscussionPosts
					.getCheckedItemPosition());

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

	@Override
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

	@OptionsItem(android.R.id.home)
	void homeSelected() {
		onBackPressed();
	}
}