package com.mobilis.controller;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobilis.dao.DiscussionDAO;
import com.mobilis.dao.PostDAO;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.interfaces.MobilisExpandableListActivity;
import com.mobilis.model.Discussion;
import com.mobilis.model.DiscussionPost;
import com.mobilis.util.Constants;
import com.mobilis.util.ParseJSON;
import com.mobilis.ws.Connection;

public class ExtMobilisTTSActivity extends MobilisExpandableListActivity
		implements OnClickListener {

	private Discussion discussion;
	private ArrayList<DiscussionPost> discussionPosts;
	private DiscussionPostAdapter discussionPostAdapter;
	private ExpandableListView expandableListView;
	private View header;
	private View footerFuturePosts;
	private View footerRefresh;
	private int footerId = 0;
	private final int FUTURE_POST_ID = 1;
	private final int REFRESH_ID = 2;
	private PostHandler postHandler;

	public int positionExpanded = -1;
	public boolean contentPostIsExpanded = false;
	private boolean headerClicked = false;
	private boolean footerClicked = false;
	private Connection wsSolar;
	private View replyButton;
	private TextView forumName;
	private PostDAO postDAO;
	private DiscussionDAO discussionDAO;
	private ImageButton play, prev, next;
	private TTSPostsManager ttsPostsManager;
	private Thread threadTTSPostsManager;
	boolean playAfterStop = false;
	private PostManagerHandler handlerPostManager = new PostManagerHandler();
	private DialogMaker dialogMaker;
	private ProgressDialog dialog;

	private int previous;

	// posts anteriores que serão mandados para a nova activity quando
	// o estado mudar
	// Será modificado aprenas se forem carregadas postagens anteriores a estas

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posts_new);

		dialogMaker = new DialogMaker(this);
		postDAO = new PostDAO(this);
		discussionDAO = new DiscussionDAO(this);
		discussionPosts = new ArrayList<DiscussionPost>();
		postHandler = new PostHandler();
		wsSolar = new Connection(postHandler, this);

		discussionDAO.open();
		discussion = discussionDAO.getDiscussion(getPreferences().getInt(
				"SelectedTopic", 0));
		discussionDAO.close();

		forumName = (TextView) findViewById(R.id.discussion_name);
		forumName.setText(discussion.getName());

		dialog = dialogMaker
				.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);

		replyButton = findViewById(R.id.reply_button);
		replyButton.setOnClickListener(this);

		expandableListView = getExpandableListView();
		LayoutInflater inflater = getLayoutInflater();

		header = inflater.inflate(R.layout.load_available_posts_item,
				expandableListView, false);
		footerFuturePosts = inflater.inflate(
				R.layout.load_available_posts_item, expandableListView, false);

		footerRefresh = inflater.inflate(R.layout.refresh_discussion_list_item,
				expandableListView, false);

		// getExpandableListView().addFooterView(footerFuturePosts);
		// getExpandableListView().addFooterView(footerRefresh);
		// footerRefresh.setVisibility(View.GONE);
		// getExpandableListView().addHeaderView(header);

		play = (ImageButton) findViewById(R.id.button_play);
		play.setOnClickListener(this);

		prev = (ImageButton) findViewById(R.id.button_prev);
		prev.setOnClickListener(this);

		next = (ImageButton) findViewById(R.id.button_next);
		next.setOnClickListener(this);

		if (getLastNonConfigurationInstance() != null) {
			loadPostsFromRetainedState();
		} else
			loadPostsFromDatabase();
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public void loadPostsFromRetainedState() {
		Object[] retainedState = (Object[]) getLastNonConfigurationInstance();
		// ArrayList<DiscussionPost> retainedPosts;
		// DiscussionPost retainedDiscussion;

		if (retainedState[0] != null) {
			// Dialog estava ativado na hora da rotação
			dialog = (ProgressDialog) retainedState[0];
			dialog.show();
		}
		if (retainedState[1] != null) {
			discussionPosts = (ArrayList<DiscussionPost>) retainedState[1];
		}

		if (retainedState[2] != null) {
			discussion = (Discussion) retainedState[2];
		}

		if (retainedState[3] != null) {
			discussion.setPreviousPosts((Integer) retainedState[3]);
			previous = discussion.getPreviousPosts();
		}

		// pega os posts do banco e mostra na lista
		discussionPostAdapter = new DiscussionPostAdapter(
				getApplicationContext(), discussionPosts,
				ExtMobilisTTSActivity.this);

		setHeader();
		setFooter();
		setListAdapter(discussionPostAdapter);

	}

	@Override
	public Object onRetainNonConfigurationInstance() {

		Object[] retainedObjects = new Object[4];

		if (dialog != null) {
			if (dialog.isShowing()) {
				closeDialog(dialog);
				retainedObjects[0] = dialog;
			}
		}

		if (discussionPosts != null) {
			retainedObjects[1] = discussionPosts;
		}

		if (discussion != null) {
			retainedObjects[2] = discussion;
		}

		retainedObjects[3] = previous;

		return retainedObjects;
	}

	private void play(int position) {
		play.setContentDescription(getResources().getString(R.string.pause));
		play.setImageResource(R.drawable.playback_pause);
		if (ttsPostsManager == null && !playAfterStop) {
			ttsPostsManager = new TTSPostsManager(discussionPosts, position,
					handlerPostManager, getApplicationContext());
			threadTTSPostsManager = new Thread(ttsPostsManager);
			threadTTSPostsManager.start();
		} else if (ttsPostsManager != null) {
			ttsPostsManager.playAfterPause();
		} else if (playAfterStop) {
			ttsPostsManager.playAfterStop();
		}
		playAfterStop = false;
	}

	protected void generateError(int what) {
		switch (what) {
		case Constants.CONNECTION_ERROR:
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.error_connection_failed),
					Toast.LENGTH_SHORT).show();
			ttsPostsManager.stop();
			ttsPostsManager = null;
			play.setContentDescription(getResources().getString(R.string.play));
			play.setImageResource(R.drawable.playback_play);
			break;
		case Constants.ERROR_PLAYING:
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.error_playing),
					Toast.LENGTH_SHORT).show();
			ttsPostsManager.stop();
			ttsPostsManager = null;
			play.setContentDescription(getResources().getString(R.string.play));
			play.setImageResource(R.drawable.playback_play);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reply_button:
			Intent intent = new Intent(this, ResponseController.class);
			startActivity(intent);
			break;

		case R.id.button_next:
			synchronized (this) {
				if (ttsPostsManager != null) {
					int position = ttsPostsManager.getCurrentPostIndex();
					if (position < ttsPostsManager.getPostsSize() - 1) {
						play.setContentDescription(getResources().getString(
								R.string.pause));
						play.setImageResource(R.drawable.playback_pause);
						stop();
						play(position + 1);
					}
				}
			}
			break;

		case R.id.button_prev:
			synchronized (this) {
				if (ttsPostsManager != null) {
					int position = ttsPostsManager.getCurrentPostIndex();
					if (position > 0) {
						play.setContentDescription(getResources().getString(
								R.string.pause));
						play.setImageResource(R.drawable.playback_pause);
						stop();
						play(position - 1);
					}
				}
			}
			break;

		case R.id.button_play:
			if (play.getContentDescription().toString()
					.equals(getResources().getString(R.string.play))) {
				play(positionExpanded);
			} else if (play.getContentDescription().toString()
					.equals(getResources().getString(R.string.pause))) {
				play.setContentDescription(getResources().getString(
						R.string.play));
				play.setImageResource(R.drawable.playback_play);
				ttsPostsManager.pause();
			}
			break;

		default:
			break;
		}
	}

	private void loadPostsFromDatabase() {
		discussionDAO.open();
		discussion = discussionDAO.getDiscussion(discussion.getId());
		previous = discussion.getPreviousPosts();
		discussionDAO.close();
		// pega os posts do banco e mostra na lista
		postDAO.open();
		discussionPosts = new ArrayList<DiscussionPost>(Arrays.asList(postDAO
				.getAllPosts(discussion.getId())));
		postDAO.close();
		discussionPostAdapter = new DiscussionPostAdapter(
				getApplicationContext(), discussionPosts,
				ExtMobilisTTSActivity.this);

		// pega o total de previous posts e seta o header
		setHeader();
		// pega o total de future posts e seta o footer
		setFooter();
		setListAdapter(discussionPostAdapter);
	}

	private void setHeader() {
		if (discussion.getPreviousPosts() > 0) {
			showHeader();
		} else {
			hideHeader();
		}
	}

	private void collapse() {
		expandableListView.collapseGroup(positionExpanded);
		positionExpanded = -1;
		contentPostIsExpanded = false;
	}

	private void showHeader() {

		getExpandableListView().addHeaderView(header);

		((TextView) header.findViewById(R.id.load_available_posts))
				.setText(discussion.getPreviousPosts()
						+ " "
						+ getApplicationContext().getResources().getString(
								R.string.not_loaded_posts_count));
		header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.show();
				Log.e("Header", "Clicado");
				headerClicked = true;
				loadPreviousPosts();
				collapse();
			}
		});
	}

	private void hideHeader() {
		expandableListView.removeHeaderView(header);
		// header.setVisibility(View.GONE);
	}

	private void setFooter() {
		if (discussion.getNextPosts() > 0) {
			setUnloadedFuturePostsFooter();
		} else {
			setRefreshPostsFooter();
		}
	}

	private void setUnloadedFuturePostsFooter() {

		((TextView) footerFuturePosts.findViewById(R.id.load_available_posts))
				.setText(discussion.getNextPosts()
						+ " "
						+ getApplicationContext().getResources().getString(
								R.string.not_loaded_posts_count));

		((ImageView) footerFuturePosts.findViewById(R.id.blue_line))
				.setVisibility(View.VISIBLE);

		if (footerId == FUTURE_POST_ID) {
			return;
		}
		if (footerId == REFRESH_ID) {
			// expandableListView.removeFooterView(footerRefresh);
			// footerRefresh.setVisibility(View.GONE);
		}

		expandableListView.addFooterView(footerFuturePosts, null, true);
		footerId = FUTURE_POST_ID;
		footerFuturePosts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.show();
				loadFuturePosts();
				footerClicked = true;
				collapse();
			}
		});
	}

	private void setRefreshPostsFooter() {
		Log.d("refresh footer", "set refresh footer");
		if (footerId == REFRESH_ID) {
			return;
		}
		if (footerId == FUTURE_POST_ID) {
			Log.d("refresh", "remove footerFuturePosts");
			expandableListView.removeFooterView(footerFuturePosts);
		}

		expandableListView.addFooterView(footerRefresh, null, true);
		footerId = REFRESH_ID;
		footerRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.show();
				loadFuturePosts();
				footerClicked = true;
				collapse();
			}
		});
	}

	private void loadPreviousPosts() {
		String date = discussionPosts.get(0).getDateToString();
		loadPreviousPosts(date);
	}

	private void loadPreviousPosts(String date) {
		wsSolar.getFromServer(Constants.CONNECTION_GET_HISTORY_POSTS,
				Constants.generateHistoryPostTTSURL(discussion.getId(), date),
				getPreferences().getString("token", null));
	}

	private void loadFuturePosts() {
		int discussionSize = discussionPosts.size();
		// pega a data do post mais novo que está sendo mostrado
		String date;
		if (discussionSize == 0) {
			date = "19800217111000"; // se possível mudar para a data de início
										// da discussion
		} else {
			date = discussionPosts.get(discussionSize - 1).getDateToString();
		}
		// faz a chamada dos posts posteriores a essa data
		loadFuturePosts(date);
	}

	private void loadFuturePosts(String date) {
		wsSolar.getFromServer(Constants.CONNECTION_GET_NEW_POSTS,
				Constants.generateNewPostsTTSURL(discussion.getId(), date),
				getPreferences().getString("token", null));
	}

	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		Log.w("Post n", "" + groupPosition);
		return super.onChildClick(parent, v, groupPosition, childPosition, id);
	}

	public void onGroupCollapse(int groupPosition) {
		super.onGroupCollapse(groupPosition);
		positionExpanded = -1;
	}

	public void onGroupExpand(int groupPosition) {
		super.onGroupExpand(groupPosition);
		if (hasPositionExpanded()) {
			collapse();
		}
		positionExpanded = groupPosition;
		Log.w("Posição expandida: ", "" + positionExpanded);
	}

	private boolean hasPositionExpanded() {
		return expandableListView.isGroupExpanded(positionExpanded);
	}

	private class PostHandler extends Handler {
		ParseJSON jsonParser = new ParseJSON(getApplicationContext());

		@Override
		public void handleMessage(Message message) {
			super.handleMessage(message);

			switch (message.what) {
			case Constants.MESSAGE_CONNECTION_FAILED:
				closeDialog(dialog);
				break;
			case Constants.MESSAGE_NEW_POST_CONNECTION_OK:
				closeDialog(dialog);
				parsePosts(message.getData().getString("content"));
				Log.w("Deu", "certo");
				break;
			case Constants.MESSAGE_HISTORY_POST_CONNECTION_OK:
				closeDialog(dialog);
				parsePosts(message.getData().getString("content"));
				Log.w("History POSTS", "OK");
				break;
			default:
				break;
			}
		}

		private void parsePosts(String content) {
			int[] beforeAfter = new int[2];
			final int beforeIndex = 0;
			final int afterIndex = 1;

			beforeAfter = jsonParser.parseBeforeAndAfter(content);

			ArrayList<DiscussionPost> loadedposts = jsonParser
					.parsePostsTTS(content);

			if (!(headerClicked || footerClicked)) {

				Log.i("MARK 1", "MARK 1");

				discussionPosts = loadedposts;
				discussionPostAdapter = new DiscussionPostAdapter(
						getApplicationContext(), discussionPosts,
						ExtMobilisTTSActivity.this);
				postDAO.open();
				postDAO.insertPostsToDB(loadedposts,
						getPreferences().getInt("SelectedTopic", 0));
				postDAO.close();
				discussionDAO.open();
				discussion.setNextPosts(beforeAfter[afterIndex]);
				setFooter();
				discussionDAO.setNextPosts(discussion.getId(),
						discussion.getNextPosts());
				discussion.setPreviousPosts(beforeAfter[beforeIndex]);
				setHeader();
				discussionDAO.setPreviousPosts(discussion.getId(),
						discussion.getPreviousPosts());
				discussionDAO.close();

				setListAdapter(discussionPostAdapter);
			} else {
				if (headerClicked) {

					Log.i("MARK 2", "MARK 2");

					ArrayList<DiscussionPost> invertedLoadedPosts = jsonParser
							.parseInvertedPosts(content);

					discussion.setPreviousPosts(beforeAfter[beforeIndex]);

					if (discussion.getPreviousPosts() < previous) {
						previous = discussion.getPreviousPosts();
					}

					discussionPosts.addAll(0, invertedLoadedPosts);
					discussionPostAdapter = new DiscussionPostAdapter(
							getApplicationContext(), discussionPosts,
							ExtMobilisTTSActivity.this);
					setHeader();
					headerClicked = false;
					setListAdapter(discussionPostAdapter);

				} else if (footerClicked) {

					Log.i("MARK 3", "MARK 3");

					footerClicked = false;
					if (loadedposts.size() == 0) {
						Toast.makeText(
								getApplicationContext(),
								getApplication().getResources().getString(
										R.string.no_new_posts),
								Toast.LENGTH_SHORT).show();
						return;
					}

					// pega o total de future posts e seta o footer

					Log.i("POSTS AFTER", "" + beforeAfter[afterIndex]);
					Log.i("POSTS BEFORE", "" + beforeAfter[beforeIndex]);

					Log.i("DISCUSSION_ID", "" + discussion.getId());
					Log.i("DISCUSSION_ID_2",
							"" + getPreferences().getInt("SelectedTopic", 0));

					// atualiza o valor de next_posts no banco

					// coloca os posts no final da lista
					discussionPosts.addAll(loadedposts);
					// salva os posts no banco apagando os excedentes

					postDAO.open();

					ArrayList<DiscussionPost> postsFromDB = new ArrayList<DiscussionPost>(
							Arrays.asList(postDAO.getAllPosts(discussion
									.getId())));
					postDAO.close();
					if (postsFromDB.size() - loadedposts.size() != 0) {
						for (int i = 0; i < loadedposts.size(); i++) {
							postsFromDB
									.remove((postsFromDB.size() - loadedposts
											.size()) + i);
							postsFromDB.add(loadedposts.get(i));
						}
						discussionDAO.open();
						discussionDAO.setPreviousPosts(
								discussion.getId(),
								discussion.getPreviousPosts()
										+ loadedposts.size());
						discussionDAO.close();
					} else {

						discussion.setPreviousPosts(beforeAfter[beforeIndex]);
						discussionDAO.open();

						discussionDAO.setPreviousPosts(discussion.getId(),
								discussion.getPreviousPosts());
						discussionDAO.close();
					}
					discussionDAO.open();
					discussion.setNextPosts(beforeAfter[afterIndex]);
					discussionDAO.setNextPosts(discussion.getId(),
							discussion.getNextPosts());
					discussionDAO.close();
					setFooter();

					postDAO.open();
					postDAO.insertPostsToDB(loadedposts, getPreferences()
							.getInt("SelectedTopic", 0));
					postDAO.close();
				}
			}
			discussionPostAdapter.notifyDataSetChanged();
		}
	}

	public void includePlayControll() {
		play.setVisibility(View.VISIBLE);
		prev.setVisibility(View.VISIBLE);
		next.setVisibility(View.VISIBLE);
		prev.setVisibility(View.VISIBLE);
		discussionPostAdapter.setPlayExpanded(true);
	}

	public void removePlayControll() {
		play.setContentDescription(getResources().getString(R.string.play));
		play.setImageResource(R.drawable.playback_play);
		play.setVisibility(View.GONE);
		prev.setVisibility(View.GONE);
		next.setVisibility(View.GONE);
		prev.setVisibility(View.GONE);
		discussionPostAdapter.setPlayExpanded(false);
		stop();
	}

	private void stop() {
		if (threadTTSPostsManager != null) {
			threadTTSPostsManager.interrupt();
			threadTTSPostsManager = null;
			ttsPostsManager.stop();
			discussionPostAdapter.untogglePostPlayingStatus(ttsPostsManager
					.getCurrentPostIndex());
			ttsPostsManager = null;
			playAfterStop = false;
			Log.w("playAfterStop", "FALSE");
		}
	}

	public class PostManagerHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what < 0)
				generateError(msg.what);
		}

		public void togglePostPlayingStatus(int postIndex) {
			discussionPostAdapter.togglePostPlayingStatus(postIndex);
		}

		public void untogglePostPlayingStatus(int postIndex) {
			discussionPostAdapter.untogglePostPlayingStatus(postIndex);
		}

		public void playedAllPosts() {
			removePlayControll();
		}
	}
}