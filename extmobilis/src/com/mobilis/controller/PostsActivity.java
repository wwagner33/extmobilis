package com.mobilis.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mobilis.audio.TTSManager;
import com.mobilis.dao.DatabaseHelper;
import com.mobilis.dao.DiscussionDAO;
import com.mobilis.dao.PostDAO;
import com.mobilis.dialog.DialogMaker;
import com.mobilis.interfaces.ConnectionCallback;
import com.mobilis.model.Discussion;
import com.mobilis.model.Post;
import com.mobilis.util.Constants;
import com.mobilis.util.DateUtils;
import com.mobilis.util.ErrorHandler;
import com.mobilis.util.MobilisPreferences;
import com.mobilis.util.ParseJSON;
import com.mobilis.ws.Connection;

public class PostsActivity extends SherlockFragmentActivity implements
		 ConnectionCallback, OnItemClickListener {

	private Discussion discussion;
	private ArrayList<Post> posts;
	private ListView postsList;
	private PostAdapter postsAdapter;
	private View header;
	private View footerFuturePosts;
	private View footerRefresh;
	private int footerId = 0;
	private final int FUTURE_POST_ID = 1;
	private final int REFRESH_ID = 2;
	public int selectedPosition = -1;
	private boolean headerClicked = false;
	private boolean footerClicked = false;
	private Connection wsSolar;
	private PostDAO postDAO;
	private DiscussionDAO discussionDAO;
	private ImageButton play, prev, next, stop;
	boolean playAfterStop = false;
	private DialogMaker dialogMaker;
	private ProgressDialog dialog;
	private int previous;
	private MobilisPreferences appState;
	private Intent intent;
	private ParseJSON jsonParser;
	private DatabaseHelper helper = null;
	private boolean actionBarSelected = false;
	private ActionBar actionBar;
	private boolean headerIsAttached;
	private TTSManager manager;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.posts_new);

		manager = new TTSManager(ttsEventListener);
		
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("Postagens");

		TextView forumTitle = (TextView) findViewById(R.id.forum_title);
		forumTitle.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/Roboto-Bold.ttf"));

		TextView forumRange = (TextView) findViewById(R.id.forum_range);

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
		posts = new ArrayList<Post>();

		discussion = discussionDAO.getDiscussion(appState.selectedDiscussion);
		
		SimpleDateFormat exhibFormat = DateUtils.getExhibitionFormat();
		String startDate = exhibFormat.format(discussion.getStartDate());
		String endDate = exhibFormat.format(discussion.getEndDate());
		forumRange.setText(startDate + " - " + endDate);

		forumTitle.setText(discussion.getName().toUpperCase());

		dialog = dialogMaker
				.makeProgressDialog(Constants.DIALOG_PROGRESS_STANDART);

		postsList = (ListView) findViewById(R.id.list);
		postsList.setOnItemClickListener(this);

		LayoutInflater inflater = getLayoutInflater();

		header = inflater.inflate(R.layout.load_available_posts_item,
				postsList, false);

		footerFuturePosts = inflater.inflate(
				R.layout.load_available_posts_item, postsList, false);

		footerRefresh = inflater.inflate(R.layout.refresh_discussion_list_item,
				postsList, false);
		
		play = (ImageButton) findViewById(R.id.button_play);
		play.setOnClickListener(onClickPlay);

		prev = (ImageButton) findViewById(R.id.button_prev);
		prev.setOnClickListener(onClickPrevious);

		next = (ImageButton) findViewById(R.id.button_next);
		next.setOnClickListener(onClickNext);

		stop = (ImageButton) findViewById(R.id.button_stop);
		stop.setOnClickListener(onClickStop);

		if (getLastCustomNonConfigurationInstance() != null) {
			loadPostsFromRetainedState();
		} else
			loadPostsFromDatabase();
				
		setHeader();
		setFooter();
	}
	
	final TTSManager.TTSEventListener ttsEventListener= new TTSManager.TTSEventListener() {
		
		@Override
		public void onFinishedPlaying() {
			   if (selectedPosition != posts.size() - 1)
	            {
				   togglePostMarked(selectedPosition + 1);
	               manager.start(posts.get(selectedPosition));
	            }
	            else
	            {
	            	togglePostMarked(selectedPosition);
	            	removePlayControl();
	            	setActionBarNotSelected();
	            	actionBarSelected = false;
	            }
		}
		
		@Override
		public void onError() {
			// TODO
		}
	}; 
	
	public void togglePostMarked(int position) {
		   
           if (selectedPosition == position)
           {
               postsAdapter.untogglePostMarkedStatus(position);
               setActionBarNotSelected();
               actionBarSelected = false;
               selectedPosition = -1;
           }
           else 
           {
        	   postsAdapter.togglePostMarkedStatus(position);
               if (selectedPosition!=-1)
            	   postsAdapter.untogglePostMarkedStatus(selectedPosition);
               selectedPosition = position;
               setActionBarSelected();
               actionBarSelected = true;
           }
	}
	
	final View.OnClickListener onClickPlay = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			manager.start(posts.get(selectedPosition));
		}
	}; 
	
	final View.OnClickListener onClickPrevious = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			play.setImageResource(R.drawable.playback_pause);
			  if (selectedPosition != 0)
	            {
				    togglePostMarked(selectedPosition -1);
	                manager.releaseResources();
	                manager.start(posts.get(selectedPosition));
	            }
		}
	}; 
	
	final View.OnClickListener onClickNext = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			play.setImageResource(R.drawable.playback_pause);
		     if (selectedPosition != posts.size() - 1)
	            {
	                togglePostMarked(selectedPosition + 1);
	                manager.releaseResources();
	                manager.start(posts.get(selectedPosition));
	            }
		}
	}; 
	
	final View.OnClickListener onClickStop = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			play.setImageResource(R.drawable.playback_play);
			removePlayControl();
			manager.releaseResources();
			postsAdapter.untogglePostMarkedStatus(selectedPosition);
			setActionBarNotSelected();
		}
	}; 
	
	public void removePlayControl() {

			play.setContentDescription(getResources().getString(
					R.string.play));
			play.setImageResource(R.drawable.playback_play);
			play.setVisibility(View.GONE);
			prev.setVisibility(View.GONE);
			next.setVisibility(View.GONE);
			prev.setVisibility(View.GONE);
			stop.setVisibility(View.GONE);
		}

	@Override
	public void onBackPressed() {

		if (selectedPosition != -1) {
			postsAdapter.untogglePostMarkedStatus(selectedPosition);
			actionBarSelected = false;
			selectedPosition = -1;
			setActionBarNotSelected();
			invalidateOptionsMenu();
			removePlayControl();

		} else
			super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		MenuInflater inflater = getSupportMenuInflater();
		if (!actionBarSelected) {
			inflater.inflate(R.menu.options_menu_action, menu);

		} else {
			inflater.inflate(R.menu.action_bar_selected, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.reply:
			intent = new Intent(this, ResponseActivity.class);
			startActivity(intent);
			return true;

		case R.id.play:
			showPlayerControls();
			return true;

		case R.id.expand:
			if (posts.get(selectedPosition).isExpanded()) {
				posts.get(selectedPosition).setExpanded(false);
			} else {
				posts.get(selectedPosition).setExpanded(true);
			}
			postsAdapter.notifyDataSetChanged();
			return true;

		case R.id.menu_config:
			intent = new Intent(this, ConfigActivity.class);
			startActivity(intent);
			return true;

		case R.id.menu_logout:
			appState.setToken(null);
			intent = new Intent(this, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;

		case R.id.mark:
			// TODO mark
			return true;

		case R.id.details:
			// TODO details
			return true;

		default:
			return false;
		}
	}

	public void setActionBarSelected() {
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

	public void setActionBarNotSelected() {
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle("Postagens");
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
				.getColor(R.color.action_bar_idle)));
		invalidateOptionsMenu();
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
		Object[] retainedState = (Object[]) getLastCustomNonConfigurationInstance();

		if (retainedState[0] != null) {
			dialog = (ProgressDialog) retainedState[0];
			dialog.show();
		}
		if (retainedState[1] != null) {
			posts = (ArrayList<Post>) retainedState[1];
		}

		if (retainedState[2] != null) {
			discussion = (Discussion) retainedState[2];
		}

		if (retainedState[3] != null) {
			discussion.setPreviousPosts((Integer) retainedState[3]);
			previous = discussion.getPreviousPosts();
		}

		postsAdapter = new PostAdapter(posts, PostsActivity.this);

		setHeader();
		setFooter();
		postsList.setAdapter(postsAdapter);

	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {

		Object[] retainedObjects = new Object[4];

		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
				retainedObjects[0] = dialog;
			}
		}

		if (posts != null) {
			retainedObjects[1] = posts;
		}

		if (discussion != null) {
			retainedObjects[2] = discussion;
		}

		retainedObjects[3] = previous;

		return retainedObjects;

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

			play.setContentDescription(getResources().getString(
					R.string.play));
			play.setImageResource(R.drawable.playback_play);
			break;
		case Constants.ERROR_PLAYING:
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.error_playing),
					Toast.LENGTH_SHORT).show();
			play.setContentDescription(getResources().getString(
					R.string.play));
			play.setImageResource(R.drawable.playback_play);
			break;
		}
	}

	private void loadPostsFromDatabase() {
		discussion = discussionDAO.getDiscussion(discussion.getId());
		previous = discussion.getPreviousPosts();
		posts = postDAO.getAllPostsFromDiscussion(discussion.getId());
		postsAdapter = new PostAdapter(posts, PostsActivity.this);

		setHeader();
		setFooter();
		postsList.setAdapter(postsAdapter);
	}

	private void setHeader() {
		if (discussion.getPreviousPosts() > 0) {
			showHeader();
			headerIsAttached = true;
		} else {
			hideHeader();
			headerIsAttached = false;
		}
	}

	private void showHeader() {

		if (postsList.getHeaderViewsCount() > 0) {
			((TextView) header.findViewById(R.id.load_available_posts))
					.setText(discussion.getPreviousPosts()
							+ " "
							+ getApplicationContext().getResources().getString(
									R.string.not_loaded_posts_count));
			return;
		}
		postsList.addHeaderView(header);

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
			}
		});
	}

	private void hideHeader() {
		postsList.removeHeaderView(header);
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
			postsList.removeFooterView(footerRefresh);
		}

		postsList.addFooterView(footerFuturePosts, null, true);
		footerId = FUTURE_POST_ID;
		footerFuturePosts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.show();
				loadFuturePosts();
				footerClicked = true;
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
			postsList.removeFooterView(footerFuturePosts);
		}

		postsList.addFooterView(footerRefresh, null, true);
		footerId = REFRESH_ID;
		footerRefresh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.show();
				loadFuturePosts();
				footerClicked = true;
			}
		});
	}

	private void loadPreviousPosts() {
		String date = posts.get(0).getDateToString();
		wsSolar.getFromServer(Constants.CONNECTION_GET_HISTORY_POSTS,
				Constants.generateHistoryPostTTSURL(discussion.getId(), date),
				appState.getToken());
	}

	private void loadFuturePosts() {
		int discussionSize = posts.size();
		String date;
		if (discussionSize == 0) {
			date = "19800217111000";

		} else {
			date = posts.get(discussionSize - 1).getDateToString();
		}
		wsSolar.getFromServer(Constants.CONNECTION_GET_NEW_POSTS,
				Constants.generateNewPostsTTSURL(discussion.getId(), date),
				appState.getToken());
	}

	public void downloadImages() {
		wsSolar.getImages(
				postDAO.getIdsOfPostsWithoutImage(appState.selectedDiscussion),
				appState.getToken());
	}

	public void showPlayerControls() {
		play.setVisibility(View.VISIBLE);
		prev.setVisibility(View.VISIBLE);
		next.setVisibility(View.VISIBLE);
		prev.setVisibility(View.VISIBLE);
		stop.setVisibility(View.VISIBLE);
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

			posts = loadedposts;
			postsAdapter = new PostAdapter(posts, PostsActivity.this);
			postDAO.insertPosts(
					loadedposts.toArray(new Post[loadedposts.size()]),
					appState.selectedDiscussion);
			discussion.setNextPosts(beforeAfter[afterIndex]);
			setFooter();
			discussion.getNextPosts();
			setHeader();
			discussion.getPreviousPosts();
			discussionDAO.updateDiscussion(discussion);
			postsList.setAdapter(postsAdapter);

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

				posts.addAll(0, loadedPosts);
				postsAdapter = new PostAdapter(posts,
						PostsActivity.this);
				setHeader();
				headerClicked = false;
				postsList.setAdapter(postsAdapter);

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

				posts.addAll(loadedposts);
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
		postsAdapter.notifyDataSetChanged();
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
				postsAdapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		int arrayPosition = position;
		if (headerIsAttached) {
			arrayPosition--;
		}

		if (selectedPosition == -1) {
			postsAdapter.togglePostMarkedStatus(arrayPosition);
			selectedPosition = arrayPosition;
			actionBarSelected = true;
			appState.selectedPost = (Integer) postsAdapter
					.getItem(arrayPosition);
			setActionBarSelected();
		} else if (selectedPosition == arrayPosition) {
			postsAdapter.untogglePostMarkedStatus(arrayPosition);
			actionBarSelected = false;
			selectedPosition = -1;
			appState.selectedPost = -1;
			setActionBarNotSelected();
		} else {
			postsAdapter.togglePostMarkedStatus(arrayPosition);
			postsAdapter.untogglePostMarkedStatus(selectedPosition);
			selectedPosition = arrayPosition;
			appState.selectedPost = (Integer) postsAdapter
					.getItem(arrayPosition);
			actionBarSelected = true;
			setActionBarSelected();
		}

		invalidateOptionsMenu();
	}
}