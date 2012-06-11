package com.mobilis.controller;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.mobilis.dao.PostDAO;
import com.mobilis.model.DiscussionPost;

public class DiscussionPostAdapter extends BaseExpandableListAdapter implements
		OnClickListener {

	private Context context;
	private List<DiscussionPost> posts;
	private View expandButton;
	private View play;
	private ExtMobilisTTSActivity extMobilisTTSActivity;
	private View markButton;
	private boolean isPlayExpanded = false;

	public DiscussionPostAdapter(Context context, List<DiscussionPost> posts,
			ExtMobilisTTSActivity extMobilisTTSActivity) {
		this.context = context;
		this.posts = posts;
		this.extMobilisTTSActivity = extMobilisTTSActivity;
	}

	public void setPosts(List<DiscussionPost> newPosts) {
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

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.discussion_list_item_menu,
					null);
		}
		// TODO Adicionar Eventos dos botões da Child

		expandButton = convertView.findViewById(R.id.expand);
		expandButton.setOnClickListener(this);

		markButton = convertView.findViewById(R.id.mark);
		markButton.setOnClickListener(this);

		play = convertView.findViewById(R.id.play);
		play.setOnClickListener(this);

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

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) extMobilisTTSActivity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.discussion_list_item, null);
		}

		DiscussionPost post = posts.get(groupPosition);

		if (post != null) {
			TextView userNick = (TextView) convertView
					.findViewById(R.id.user_nick);
			TextView postDate = (TextView) convertView
					.findViewById(R.id.post_date);
			TextView postContent = (TextView) convertView
					.findViewById(R.id.post_content);

			if (userNick != null) {
				userNick.setText(post.getUserNick());
			}
			if (postDate != null) {
				postDate.setText(generateDateHeader(groupPosition));
				postDate.setText(post.getDate());
			}
			if (postContent != null) {
				String content = post.getContent();
				if (content.length() <= 150)
					postContent.setText(content);
				else
					postContent.setText(content.substring(0, 149));

				if (groupPosition == extMobilisTTSActivity.positionExpanded
						&& extMobilisTTSActivity.contentPostIsExpanded) {
					postContent.setMaxLines(500);
					if (content.length() > 150)
						postContent.append(content.substring(150));
				} else {
					postContent.setMaxLines(3);
				}
			}
			
			if (post.isJustLoaded()){// highlight
				convertView.setBackgroundResource(R.drawable.highlight_transition);
				((TransitionDrawable)convertView.getBackground()).startTransition(750);
				((TransitionDrawable)convertView.getBackground()).reverseTransition(750);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.expand:
			extMobilisTTSActivity.contentPostIsExpanded = !extMobilisTTSActivity.contentPostIsExpanded;
			notifyDataSetChanged();
			break;
		case R.id.mark:
			Log.i("Mark", "ok");
			toggleExpandedPostMarkedStatus();
			notifyDataSetChanged();
			break;
		case R.id.play:
			includeOrRemovePlayController();
			break;
		default:
			break;
		}
	}

	private void includeOrRemovePlayController() {
		if (isPlayExpanded) {
			extMobilisTTSActivity.removePlayControll();
			setPlayExpanded(false);
		} else {
			extMobilisTTSActivity.includePlayControll();
			setPlayExpanded(true);
		}
	}

	private void toggleExpandedPostMarkedStatus() {
		DiscussionPost post = posts.get(extMobilisTTSActivity.positionExpanded);
		post.setMarked(!post.isMarked());
		// atualizar post no banco
		PostDAO postDAO = new PostDAO(context);
		postDAO.open();
		postDAO.setMarked((int) post.getId(), post.isMarked());
		postDAO.close();
	}

	public void untogglePostPlayingStatus(int postIndex) {
		DiscussionPost post = posts.get(postIndex);
		post.setPlaying(false);
		notifyDataSetChanged();
	}

	public void togglePostPlayingStatus(int postIndex) {
		DiscussionPost post = posts.get(postIndex);
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
}