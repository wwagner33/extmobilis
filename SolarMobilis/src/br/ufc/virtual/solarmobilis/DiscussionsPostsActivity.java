package br.ufc.virtual.solarmobilis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import br.ufc.virtual.solarmobilis.model.DiscussionPost;
import br.ufc.virtual.solarmobilis.model.DiscussionPostList;
import br.ufc.virtual.solarmobilis.model.PostAdapter;
import br.ufc.virtual.solarmobilis.util.TextBlockenizer;
import br.ufc.virtual.solarmobilis.util.Toaster;
import br.ufc.virtual.solarmobilis.webservice.BingAudioDownloader;
import br.ufc.virtual.solarmobilis.webservice.DownloaderListener;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.StringRes;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@EActivity
public class DiscussionsPostsActivity extends SherlockFragmentActivity
		implements DownloaderListener {

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

	@Bean
	Toaster toaster;

	List<String> fileDescriptors = new ArrayList<String>();
	BingAudioDownloader audioDownloader;
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
	private boolean paused;
	private boolean stoped = true;

	PostAdapter adapter;

	File file = new File(Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/Mobilis/TTS/");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_discussions_posts);
		makeDialog();
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

		audioDownloader = new BingAudioDownloader();
		audioDownloader.setListener(this);
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

	void setFooter() {

		Log.i("Dentro do setFooter", String.valueOf(unloadedFuturePostsCount));

		if (unloadedFuturePostsCount > 0) {
			((TextView) footerFuturePosts
					.findViewById(R.id.load_available_posts))
					.setText(unloadedFuturePostsCount
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

	@Click({ R.id.refresh_button })
	@Background
	void refreshPostsList() {
		makeDialog();
		try {

			int discussionSize = posts.size();
			if (discussionSize == 0) {
				oldDateString = "20001010102410";
			} else {
				oldDateString = posts.get(posts.size() - 1).getDateToString();
			}

			discussionPostList = solarManager.getPosts(discussionId,
					oldDateString);
			unloadedFuturePostsCount = discussionPostList.getAfter();

			for (int i = 0; i < discussionPostList.getPosts().size(); i++) {
				Log.i("#" + i, discussionPostList.getPosts().get(i)
						.getContent());
			}

			newPosts = discussionPostList.getPosts();
			Collections.reverse(newPosts);
			posts.addAll(posts.size(), newPosts);

			for (int i = 0; i < posts.size(); i++) {
				Log.i("#" + i, posts.get(i).getContent());
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
			discussionPostList = solarManager.getPosts(discussionId,
					oldDateString);
			List<DiscussionPost> posts = discussionPostList.getPosts();
			for (DiscussionPost discussionPost : posts) {
				discussionPost.setUserImageURL(solarManager
						.getUserImageUrl(discussionPost.getUserId()));
			}

			unloadedFuturePostsCount = discussionPostList.getAfter();
			Log.i("after", String.valueOf(discussionPostList.getAfter()));

			updateList();
			setFooter();
		} catch (HttpStatusCodeException e) {
			Log.i("ERRO HttpStatusCodeException", e.getStatusCode().toString());
			solarManager.errorHandler(e.getStatusCode());
		} catch (Exception e) {
			Log.i("ERRO Exception", e.getMessage());
			solarManager.alertNoConnection();
		} finally {
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
		Collections.reverse(posts);
		adapter = new PostAdapter(this, R.layout.discussion_list_item,
				R.id.user_nick, posts);

		listVieWDiscussionPosts.setAdapter(adapter);

	}

	@UiThread
	void reUpdateList() {
		dialog.dismiss();
		adapter = new PostAdapter(this, R.layout.discussion_list_item,
				R.id.user_nick, posts);

		listVieWDiscussionPosts.setAdapter(adapter);
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

			posts.get(position).setMarked(false);
			adapter.notifyDataSetChanged();

			postSelected = false;
			setActionBarNotSelected();
			selectedPosition = -1;

		} else {

			posts.get(position).setMarked(true);
			adapter.notifyDataSetChanged();

			if (selectedPosition != -1) {
				posts.get(selectedPosition).setMarked(false);
				adapter.notifyDataSetChanged();
			}

			selectedPosition = position;
			setActionBarSelected();
			postSelected = true;

		}

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

		if (mp.isPlaying()) {
			paused = true;
			setImagePlayer();
			mp.pause();

		} else {
			if (paused == true) {
				paused = false;
				setImagePlayer();
				mp.start();
			}
			if (stoped == true) {
				stoped = false;
				setImagePlayer();
				play(selectedPosition);
			}
		}
	}

	@Background
	void play(int pos) {

		Log.i("---->ultimo clicado", String.valueOf(pos));
		String textToBreak = posts.get(pos).getUserNick() + ", "
				+ posts.get(pos).getDateToPost() + ", "
				+ posts.get(pos).getContent();

		TextBlockenizer text = new TextBlockenizer(textToBreak);
		for (String block = text.getFirst(); block != ""; block = text
				.getNext()) {

			fileDescriptors.add("");
			audioDownloader
					.saveAudio(block, text.getCurrentBlockPosition() - 1);
		}
	}

	@UiThread
	void setImagePlayer() {
		if (paused || stoped) {
			play.setImageResource(R.drawable.playback_play);
		} else {
			play.setImageResource(R.drawable.playback_pause);
		}

	}

	@Click(R.id.button_next)
	void next() {
		Log.i("Teste bot�o", "botao next");

		if (selectedPosition == posts.size() - 1) {

			toaster.showToast("n�o existe posterior");
			Log.i("Toast", "n�o existe post posterior");

		} else {
			deleteAudioData();
			togglePostMarked(selectedPosition + 1);

			play(selectedPosition + 1);

			Log.i("#selected-position-atual", String.valueOf(selectedPosition));

		}
	}

	@Click(R.id.button_prev)
	void previous() {
		Log.i("Teste bot�o", "botao previous");

		if (selectedPosition == 0) {

			toaster.showToast("n�o existe anterior");
			Log.i("Toast", "n�o existe post anterior");

		} else {
			deleteAudioData();
			Log.i("#bfselected-position-atual",
					String.valueOf(selectedPosition));

			togglePostMarked(selectedPosition - 1);

			play(selectedPosition - 1);

			Log.i("#selected-position-atual", String.valueOf(selectedPosition));

		}
	}

	@Click(R.id.button_stop)
	@Background
	void stop() {
		if (mp.isPlaying()) {
			stoped = true;
			setImagePlayer();
			mp.stop();
			deleteAudioData();

		}
	}

	@Override
	public void onBackPressed() {
		if (!postSelected) {
			super.onBackPressed();

		} else {
			posts.get(selectedPosition).setMarked(false);
			adapter.notifyDataSetChanged();
			selectedPosition = -1;
			postSelected = false;
			setActionBarNotSelected();
			invalidateOptionsMenu();
			removePlayControls();
		}
	}

	void removePlayControls() {
		play.setVisibility(View.GONE);
		prev.setVisibility(View.GONE);
		next.setVisibility(View.GONE);
		stop.setVisibility(View.GONE);
	}

	@Override
	public void onDowloadFinish(String name, int i) {

		fileDescriptors.set(i, name);

		if (i == 0) {

			try {
				playAudio(i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteAudioData() {
		fileDescriptors.clear();
		Log.i("filedescriptors", "dados apagados apagados");

		if (file.exists()) {
			final File[] audioFiles = file.listFiles();
			for (final File audioFile : audioFiles) {
				audioFile.delete();
			}
			Log.i("Arquivos", "dados apagados apagados");
		}

	}

	void playAudio(final int i) throws IllegalArgumentException,
			SecurityException, IllegalStateException, IOException {

		mp.reset();
		mp.setDataSource(fileDescriptors.get(i));
		mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {

				if (i == (fileDescriptors.size() - 1)) {
					Log.i("ultimo post", "Ultimo post tocado");
					mp.stop();
					stoped = true;
					setImagePlayer();
					deleteAudioData();

				} else if (fileDescriptors.get(i + 1) != null) {
					Log.i("Tocar", "Proximo bloco");

					try {
						playAudio(i + 1);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					Log.i("bloco", "bloco n�o baixado");
				}

			}
		});

		mp.prepare();
		mp.start();

	}
}
