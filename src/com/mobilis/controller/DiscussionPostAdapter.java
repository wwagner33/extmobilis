package com.mobilis.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilis.dao.PostDAO;
import com.mobilis.exception.ImageFileNotFoundException;
import com.mobilis.model.Post;
import com.mobilis.util.Constants;
import com.mobilis.util.EllipsizingTextView;

public class DiscussionPostAdapter extends BaseExpandableListAdapter {

	private List<Post> posts;
	private ExtMobilisTTSActivity activity;
	private boolean isPlayExpanded = false;
	private PostDAO postDAO;
	public static final String TAG = "DiscussionPostsAdapter";

	static class ChildViewHolder {
		View play, reply, details, expandButton, markButton;
	}

	static class ParentViewHolder {
		TextView userNick, postDate;
		ImageView userPhoto;
		int originalPosition;
	}

	public DiscussionPostAdapter(List<Post> posts,
			ExtMobilisTTSActivity extMobilisTTSActivity) {
		this.posts = posts;
		activity = extMobilisTTSActivity;
		postDAO = new PostDAO(activity.getHelper());
	}

	public void setPosts(List<Post> newPosts) {
		posts = newPosts;
	}

	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		ChildViewHolder childHolder;

		if (convertView == null) {

			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.discussion_list_item_menu,
					null);
			childHolder = new ChildViewHolder();
			childHolder.expandButton = convertView.findViewById(R.id.expand);
			childHolder.markButton = convertView.findViewById(R.id.mark);
			childHolder.play = convertView.findViewById(R.id.play);
			childHolder.details = convertView.findViewById(R.id.details);
			childHolder.reply = convertView.findViewById(R.id.reply);

			convertView.setTag(childHolder);
		}

		else {
			childHolder = (ChildViewHolder) convertView.getTag();
		}

		childHolder.expandButton.setOnClickListener(activity);
		childHolder.markButton.setOnClickListener(activity);
		childHolder.play.setOnClickListener(activity);
		childHolder.reply.setOnClickListener(activity);
		childHolder.details.setOnClickListener(activity);

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return null;
	}

	@Override
	public int getGroupCount() {
		return posts.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.discussion_list_item, null);

		ImageView userPhoto = (ImageView) convertView
				.findViewById(R.id.user_photo);

		try {
			userPhoto.setImageBitmap(getUserImage((int) posts
					.get(groupPosition).getUserId()));
			Log.i(TAG, "USER NAME =" + posts.get(groupPosition).getUserNick());
		} catch (ImageFileNotFoundException e) {
			Log.e(TAG, "No image");
		}

		Post post = posts.get(groupPosition);

		if (post != null) {
			TextView userNick = (TextView) convertView
					.findViewById(R.id.user_nick);
			TextView postDate = (TextView) convertView
					.findViewById(R.id.post_date);
			EllipsizingTextView postContent = (EllipsizingTextView)convertView
					.findViewById(R.id.post_content);

			if (userNick != null) {
				userNick.setText(post.getUserNick());
			}
			if (postDate != null) {
				postDate.setText(generateDateHeader(groupPosition));
			}
			if (postContent != null) {
				String content = post.getContent();
				content = content.replaceAll("\\s", " ").trim();
				postContent.setText(content);
				if (groupPosition == activity.positionExpanded
						&& activity.contentPostIsExpanded) {
					postContent.setMaxLines(500);
				} else {
					postContent.setMaxLines(5);
				}
			}

			if (post.isJustLoaded()) {// highlight
				convertView
						.setBackgroundResource(R.drawable.highlight_transition);
				((TransitionDrawable) convertView.getBackground())
						.startTransition(750);
				((TransitionDrawable) convertView.getBackground())
						.reverseTransition(750);
				post.setJustLoaded(false);
			} else

			if (post.isMarked()) {
				convertView.setBackgroundColor(0x6600FFFF);
			} else {
				convertView.setBackgroundColor(0xFFFFFFFF);
			}
			if (post.isPlaying()) {
				convertView.setBackgroundColor(0xFFF0E68C);
			}
		}
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public void toggleExpandedPostMarkedStatus() {
		Post post = posts.get(activity.positionExpanded);
		post.setMarked(!post.isMarked());
		postDAO.updatePost(post);
	}

	public void untogglePostPlayingStatus(int postIndex) {
		Post post = posts.get(postIndex);
		post.setPlaying(false);
		notifyDataSetChanged();
	}

	public void togglePostPlayingStatus(int postIndex) {
		Post post = posts.get(postIndex);
		post.setPlaying(true);
		notifyDataSetChanged();
	}

	private String generateDateHeader(int postIndex) {
		String header = "";
		String date = posts.get(postIndex).getDate();
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7));
		int day = Integer.parseInt(date.substring(8, 10));
		int hour = Integer.parseInt(date.substring(11, 13));
		int minute = Integer.parseInt(date.substring(14, 16));
		Calendar c = Calendar.getInstance();
		if (year == c.get(Calendar.YEAR)) {
			if (month == c.get(Calendar.MONTH) + 1) {
				if (day == c.get(Calendar.DATE)) {
					if (hour == c.get(Calendar.HOUR_OF_DAY)) {
						header = header.concat("Há "
								+ (c.get(Calendar.MINUTE) - minute)
								+ " minutos");
					} else {
						header = header.concat("Às " + hour + " horas");
					}
				} else {
					if (day == c.get(Calendar.DATE) - 1)
						header = header.concat("Ontem");
					else
						header = header.concat("Dia " + day + " às " + hour
								+ " horas");
				}
			} else {
				header = header.concat("Dia "
						+ day
						+ " de "
						+ new DateFormatSymbols(Locale.getDefault())
								.getMonths()[month - 1]);
			}
		} else {
			header = header
					.concat("Dia "
							+ day
							+ " de "
							+ new DateFormatSymbols(Locale.getDefault())
									.getMonths()[month - 1] + " de " + year);
		}
		return header;
	}

	public void setPlayExpanded(boolean b) {
		isPlayExpanded = b;
	}

	public boolean getPlayExpanded() {
		return isPlayExpanded;
	}

	public Bitmap getUserImage(int userId) throws ImageFileNotFoundException {

		try {
			final String prefix = String.valueOf(userId);

			File file = new File(Constants.PATH_IMAGES);

			File[] image = file.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String filename) {
					return filename.startsWith(prefix);
				}
			});

			Bitmap userImage = BitmapFactory.decodeFile(image[0]
					.getAbsolutePath());
			return userImage;
		} catch (NullPointerException e) {
			throw new ImageFileNotFoundException();
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ImageFileNotFoundException();
		}
	}
}