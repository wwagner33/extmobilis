package com.mobilis.controller;

import java.util.ArrayList;

import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
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

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mobilis.dao.DatabaseHelper;
import com.mobilis.dao.DiscussionDAO;
import com.mobilis.dao.PostDAO;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.exception.ImageFileNotFoundException;
import com.mobilis.interfaces.ConnectionCallback;
import com.mobilis.model.Discussion;
import com.mobilis.model.Post;
import com.mobilis.util.Constants;
import com.mobilis.util.ErrorHandler;
import com.mobilis.util.MobilisPreferences;
import com.mobilis.util.ParseJSON;
import com.mobilis.ws.Connection;

public class ExtMobilisTTSActivity extends ExpandableListActivity implements
		OnClickListener, ConnectionCallback {

	private Discussion discussion;
	private ArrayList<Post> discussionPosts;
	private DiscussionPostAdapter discussionPostAdapter;
	private ExpandableListView expandableListView;
	private View header;
	private View footerFuturePosts;
	private View footerRefresh;
	private int footerId = 0;
	private final int FUTURE_POST_ID = 1;
	private final int REFRESH_ID = 2;
	public int positionExpanded = -1;
	public boolean contentPostIsExpanded = false;
	private boolean headerClicked = false;
	private boolean footerClicked = false;
	private Connection wsSolar;
	private View replyButton;
	private TextView forumName;
	private PostDAO postDAO;
	private DiscussionDAO discussionDAO;
	private ImageButton barButtonPlay, prev, next;
	private TTSPostsManager ttsPostsManager;
	private Thread threadTTSPostsManager;
	boolean playAfterStop = false;
	private PostManagerHandler handlerPostManager = new PostManagerHandler();
	private DialogMaker dialogMaker;
	private ProgressDialog dialog;
	private int previous;
	private MobilisPreferences appState;
	private Intent intent;
	private ParseJSON jsonParser;
	private DatabaseHelper helper = null;
	private static final String TAG = "TTS-ACTIVITY";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posts_new);
		helper = getHelper();
		jsonParser = new ParseJSON();
		appState = MobilisPreferences.getInstance(this);
		wsSolar = new Connection(this);

		if (appState.ids != null) {
			ArrayList<Integer> ids = appState.ids;
			appState.ids = null;
			wsSolar.getImages(ids, appState.getToken());
		}

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		dialogMaker = new DialogMaker(this);
		postDAO = new PostDAO(helper);
		discussionDAO = new DiscussionDAO(helper);
		discussionPosts = new ArrayList<Post>();

		discussion = discussionDAO.getDiscussion(appState.selectedDiscussion);

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

		barButtonPlay = (ImageButton) findViewById(R.id.button_play);
		barButtonPlay.setOnClickListener(this);

		prev = (ImageButton) findViewById(R.id.button_prev);
		prev.setOnClickListener(this);

		next = (ImageButton) findViewById(R.id.button_next);
		next.setOnClickListener(this);

		if (getLastNonConfigurationInstance() != null) {
			loadPostsFromRetainedState();
		} else
			loadPostsFromDatabase();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (helper != null) {
			OpenHelperManager.releaseHelper();
			helper = null;
		}
	}

	public DatabaseHelper getHelper() {
		if (helper == null) {
			helper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		return helper;
	}

	@SuppressWarnings("unchecked")
	public void loadPostsFromRetainedState() {
		Object[] retainedState = (Object[]) getLastNonConfigurationInstance();

		if (retainedState[0] != null) {
			dialog = (ProgressDialog) retainedState[0];
			dialog.show();
		}
		if (retainedState[1] != null) {
			discussionPosts = (ArrayList<Post>) retainedState[1];
		}

		if (retainedState[2] != null) {
			discussion = (Discussion) retainedState[2];
		}

		if (retainedState[3] != null) {
			discussion.setPreviousPosts((Integer) retainedState[3]);
			previous = discussion.getPreviousPosts();
		}

		discussionPostAdapter = new DiscussionPostAdapter(discussionPosts,
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
				dialog.dismiss();
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

		Log.i(TAG, "Posotion of post = " + position);

		barButtonPlay.setContentDescription(getResources().getString(
				R.string.pause));
		barButtonPlay.setImageResource(R.drawable.playback_pause);
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

		MediaPlayer mp = MediaPlayer.create(getApplicationContext(),
				R.raw.errodeconexao);
		mp.start();
		mp.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}
		});

		switch (what) {
		case Constants.CONNECTION_ERROR:
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.error_connection_failed),
					Toast.LENGTH_SHORT).show();

			barButtonPlay.setContentDescription(getResources().getString(
					R.string.play));
			barButtonPlay.setImageResource(R.drawable.playback_play);
			break;
		case Constants.ERROR_PLAYING:
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.error_playing),
					Toast.LENGTH_SHORT).show();
			barButtonPlay.setContentDescription(getResources().getString(
					R.string.play));
			barButtonPlay.setImageResource(R.drawable.playback_play);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reply_button:
			intent = new Intent(this, ResponseController.class);
			startActivity(intent);
			break;

		case R.id.button_next:
			synchronized (this) {

				if (ttsPostsManager != null) {
					int position = ttsPostsManager.getCurrentPostIndex();
					if (position < ttsPostsManager.getPostsSize() - 1) {
						stop();
						play(position + 1);
					}
				}
				break;
			}

		case R.id.button_prev:
			synchronized (this) {
				if (ttsPostsManager != null) {
					int position = ttsPostsManager.getCurrentPostIndex();
					if (position > 0) {
						stop();
						play(position - 1);
					}
				}
				break;
			}

		case R.id.button_play:
			playClick();
			break;

		case R.id.expand:
			contentPostIsExpanded = !contentPostIsExpanded;
			discussionPostAdapter.notifyDataSetChanged();
			break;
		case R.id.mark:
			discussionPostAdapter.toggleExpandedPostMarkedStatus();
			discussionPostAdapter.notifyDataSetChanged();
			break;
		case R.id.play:
			playPost();
			break;

		case R.id.reply:
			intent = new Intent(this, ResponseController.class);
			startActivity(intent);
			break;

		case R.id.details:

			Post post = postDAO.getPost(appState.selectedPost);
			intent = new Intent(this, PostDetailController.class);
			try {
				Bitmap userImage = discussionPostAdapter
						.getUserImage((int) post.getUserId());
				intent.putExtra("image", userImage);
			} catch (ImageFileNotFoundException e) {
				Log.i("Exception", "Exception");
			}

			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void playPost() {
		// se player não estiver visível mostrar
		if (!discussionPostAdapter.getPlayExpanded()) {
			// incluir o player e tocar o post
			includePlayControll();
		} else {
			// se outro post estiver tocando deve-se pará-lo
			stop();

			// tocar o post
			play(positionExpanded);
		}
	}

	private void playClick() {
		synchronized (this) {
			if (barButtonPlay.getContentDescription().toString()
					.equals(getResources().getString(R.string.play))) {
				play(positionExpanded);
			} else if (barButtonPlay.getContentDescription().toString()
					.equals(getResources().getString(R.string.pause))) {
				barButtonPlay.setContentDescription(getResources().getString(
						R.string.play));
				barButtonPlay.setImageResource(R.drawable.playback_play);
				ttsPostsManager.pause();
			}
		}
	}

	private void loadPostsFromDatabase() {
		discussion = discussionDAO.getDiscussion(discussion.getId());
		previous = discussion.getPreviousPosts();
		discussionPosts = postDAO.getAllPostsFromDiscussion(discussion.getId());
		discussionPostAdapter = new DiscussionPostAdapter(discussionPosts,
				ExtMobilisTTSActivity.this);

		setHeader();
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

		if (expandableListView.getHeaderViewsCount() > 0) {
			((TextView) header.findViewById(R.id.load_available_posts))
					.setText(discussion.getPreviousPosts()
							+ " "
							+ getApplicationContext().getResources().getString(
									R.string.not_loaded_posts_count));
			return;
		}
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
			expandableListView.removeFooterView(footerRefresh);
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
		wsSolar.getFromServer(Constants.CONNECTION_GET_HISTORY_POSTS,
				Constants.generateHistoryPostTTSURL(discussion.getId(), date),
				appState.getToken());
	}

	private void loadFuturePosts() {

		int discussionSize = discussionPosts.size();
		String date;
		if (discussionSize == 0) {
			date = "19800217111000";

		} else {
			date = discussionPosts.get(discussionSize - 1).getDateToString();
		}
		wsSolar.getFromServer(Constants.CONNECTION_GET_NEW_POSTS,
				Constants.generateNewPostsTTSURL(discussion.getId(), date),
				appState.getToken());
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
		appState.selectedPost = (int) discussionPosts.get(groupPosition)
				.getId();
		Log.i("POST ID", "" + discussionPosts.get(groupPosition).getId());
	}

	private boolean hasPositionExpanded() {
		return expandableListView.isGroupExpanded(positionExpanded);
	}

	public void downloadImages() {
		wsSolar.getImages(
				postDAO.getIdsOfPostsWithoutImage(appState.selectedDiscussion),
				appState.getToken());
	}

	public void includePlayControll() {
		barButtonPlay.setVisibility(View.VISIBLE);
		prev.setVisibility(View.VISIBLE);
		next.setVisibility(View.VISIBLE);
		prev.setVisibility(View.VISIBLE);
		playClick();
		discussionPostAdapter.setPlayExpanded(true);
	}

	public void removePlayControll() {
		barButtonPlay.setContentDescription(getResources().getString(
				R.string.play));
		barButtonPlay.setImageResource(R.drawable.playback_play);
		barButtonPlay.setVisibility(View.GONE);
		prev.setVisibility(View.GONE);
		next.setVisibility(View.GONE);
		prev.setVisibility(View.GONE);
		discussionPostAdapter.setPlayExpanded(false);
		stop();
	}

	private void stop() {
		Log.i("Stop", "Stopped");
		if (threadTTSPostsManager != null && ttsPostsManager != null) {
			discussionPostAdapter.untogglePostPlayingStatus(ttsPostsManager
					.getCurrentPostIndex());
			threadTTSPostsManager.interrupt();
			ttsPostsManager.stop();
			threadTTSPostsManager = null;
			ttsPostsManager = null;
			playAfterStop = false;
			Log.w("playAfterStop", "FALSE");
		}
	}

	public class PostManagerHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			untogglePostPlayingStatus(ttsPostsManager.getCurrentPostIndex());
			removePlayControll();
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

		public void playNext() {
			int position = ttsPostsManager.getCurrentPostIndex();
			if (position < ttsPostsManager.getPostsSize() - 1) {
				stop();
				play(position + 1);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void parsePosts(String content) {
		int[] beforeAfter = new int[2];
		final int beforeIndex = 0;
		final int afterIndex = 1;

		beforeAfter = jsonParser.parseBeforeAndAfter(content);

		ArrayList<Post> loadedposts = (ArrayList<Post>) jsonParser.parseJSON(
				content, Constants.PARSE_POSTS_ID);

		if (!(headerClicked || footerClicked)) {

			discussionPosts = loadedposts;
			discussionPostAdapter = new DiscussionPostAdapter(discussionPosts,
					ExtMobilisTTSActivity.this);
			postDAO.insertPosts(
					loadedposts.toArray(new Post[loadedposts.size()]),
					appState.selectedDiscussion);
			discussion.setNextPosts(beforeAfter[afterIndex]);
			setFooter();
			discussion.getNextPosts();
			setHeader();
			discussion.getPreviousPosts();
			discussionDAO.updateDiscussion(discussion);
			setListAdapter(discussionPostAdapter);
		} else {
			if (headerClicked) {

				ArrayList<Post> loadedPosts = (ArrayList<Post>) jsonParser
						.parseJSON(content, Constants.PARSE_POSTS_ID);

				if (discussion.getPreviousPosts() % 20 != 0) {
					discussion.setPreviousPosts(discussion.getPreviousPosts()
							- loadedPosts.size());
				}

				else {
					discussion.setPreviousPosts(beforeAfter[beforeIndex]);
				}

				if (discussion.getPreviousPosts() < previous) {
					previous = discussion.getPreviousPosts();
				}

				discussionPosts.addAll(0, loadedPosts);
				discussionPostAdapter = new DiscussionPostAdapter(
						discussionPosts, ExtMobilisTTSActivity.this);
				setHeader();
				headerClicked = false;
				setListAdapter(discussionPostAdapter);

			} else if (footerClicked) {

				if (beforeAfter[afterIndex] == 0) {
					if (discussion.HasNewPosts()) {
						discussion.setHasNewPosts(false);
					}
				}

				footerClicked = false;
				if (loadedposts.size() == 0) {
					Toast.makeText(
							getApplicationContext(),
							getApplication().getResources().getString(
									R.string.no_new_posts), Toast.LENGTH_SHORT)
							.show();
					return;
				}

				discussionPosts.addAll(loadedposts);
				ArrayList<Post> postsFromDB = postDAO
						.getAllPostsFromDiscussion(discussion.getId());
				if (postsFromDB.size() - loadedposts.size() != 0) {

					ArrayList<Post> postsToRemain = new ArrayList<Post>();

					for (int i = loadedposts.size(); i < postsFromDB.size(); i++) {
						postsToRemain.add(postsFromDB.get(i));
					}

					for (Post disc : loadedposts) {
						postsToRemain.add(disc);
					}

					discussion.setPreviousPosts(discussion.getPreviousPosts()
							+ loadedposts.size());
					loadedposts = postsToRemain;
				} else {
					discussion.setPreviousPosts(discussion.getPreviousPosts()
							+ loadedposts.size());
				}
				discussion.setNextPosts(beforeAfter[afterIndex]);
				discussionDAO.updateDiscussion(discussion);

				setFooter();
				postDAO.insertPosts(
						loadedposts.toArray(new Post[loadedposts.size()]),
						appState.selectedDiscussion);
			}
		}
		discussionPostAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (ttsPostsManager != null)
			ttsPostsManager.stop();
	}

	@Override
	public void resultFromConnection(int connectionId, String result,
			int statusCode) {
		if (statusCode != 200 && statusCode != 201) {

			switch (connectionId) {
			case Constants.CONNECTION_GET_IMAGES:
				break;
			default:
				ErrorHandler.handleStatusCode(this, statusCode);
				dialog.dismiss();
				break;
			}

		} else {
			switch (connectionId) {

			case Constants.CONNECTION_GET_NEW_POSTS:
				downloadImages();
				dialog.dismiss();
				parsePosts(result);
				break;

			case Constants.CONNECTION_GET_HISTORY_POSTS:
				downloadImages();
				dialog.dismiss();
				parsePosts(result);
				break;

			case Constants.CONNECTION_GET_IMAGES:
				discussionPostAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}
	}
}