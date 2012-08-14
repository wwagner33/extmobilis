package com.mobilis.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilis.dao.PostDAO;
import com.mobilis.model.Post;
import com.mobilis.util.Constants;
import com.mobilis.util.EllipsizingTextView;

public class PostAdapter extends BaseAdapter {

	private List<Post> posts;
	private PostsActivity activity;
	private PostDAO postDao;
	private final String TAG = "PostAdapter";
	private Drawable noImage;
	private LayoutInflater inflater;

	private Typeface robotoRegular;
	private Typeface robotoLight;

	static class ViewHolder {
		TextView userNick, postDate;
		EllipsizingTextView postContent;
		ImageView avatar;
	}

	public PostAdapter(List<Post> posts, PostsActivity activity) {
		this.posts = posts;
		this.activity = activity;
		inflater = LayoutInflater.from(activity);
		postDao = new PostDAO(activity.getHelper());
		robotoRegular = Typeface.createFromAsset(activity.getAssets(),
				"fonts/Roboto-Regular.ttf");

		robotoLight = Typeface.createFromAsset(activity.getAssets(),
				"fonts/Roboto-Light.ttf");
		noImage = activity.getResources().getDrawable(R.drawable.no_picture);

	}

	@Override
	public int getCount() {
		return posts.size();
	}

	@Override
	public Object getItem(int position) {
		return posts.get(position).getId();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.discussion_list_item, null);

			holder = new ViewHolder();

			holder.avatar = (ImageView) convertView
					.findViewById(R.id.user_photo);
			holder.userNick = (TextView) convertView
					.findViewById(R.id.user_nick);
			holder.postDate = (TextView) convertView
					.findViewById(R.id.post_date);
			holder.postContent = (EllipsizingTextView) convertView
					.findViewById(R.id.post_content);

			convertView.setTag(holder);
		}

		else {
			holder = (ViewHolder) convertView.getTag();
		}

		try {
			Bitmap userImage = getUserImage(posts.get(position).getUserId());
			holder.avatar.setImageBitmap(userImage);

			if (userImage != null) {

				if (posts.get(position).isPlaying()) {

					Log.i(TAG, "HAVE image and is playing");
					Drawable[] layers = new Drawable[2];
					layers[0] = new BitmapDrawable(userImage);
					layers[1] = activity.getResources().getDrawable(
							R.drawable.action_playback);
					LayerDrawable layerDrawable = new LayerDrawable(layers);
					holder.avatar.setImageDrawable(layerDrawable);

				} else {
					Log.i(TAG, "HAVE image and is NOT playing");
					holder.avatar.setImageBitmap(userImage);
				}
			}

			else {
				if (posts.get(position).isPlaying()) {
					Log.i(TAG, "NO image and is playing");

					Drawable[] layers = new Drawable[2];
					layers[0] = activity.getResources().getDrawable(
							R.drawable.no_picture);
					layers[1] = activity.getResources().getDrawable(
							R.drawable.action_playback);
					LayerDrawable layerDrawable = new LayerDrawable(layers);
					holder.avatar.setImageDrawable(layerDrawable);
				} else {
					Log.i(TAG, "NO image and NO playing");
					holder.avatar.setImageDrawable(noImage);
				}
			}

		} catch (FileNotFoundException e) {

			if (posts.get(position).isPlaying()) {
				Drawable[] layers = new Drawable[2];
				layers[0] = activity.getResources().getDrawable(
						R.drawable.no_picture);
				layers[1] = activity.getResources().getDrawable(
						R.drawable.action_playback);
				LayerDrawable layerDrawable = new LayerDrawable(layers);
				holder.avatar.setImageDrawable(layerDrawable);
			} else {
				holder.avatar.setImageDrawable(noImage);
			}

			Log.e(TAG, "Image Exception");
		}

		Post post = posts.get(position);

		if (post != null) {

			holder.userNick.setTypeface(robotoRegular);
			holder.postContent.setTypeface(robotoLight);

			if (holder.userNick != null) {
				holder.userNick.setText(post.getUserNick());
			}
			if (holder.postDate != null) {
				holder.postDate.setText(generateDateHeader(position));
			}
			if (holder.postContent != null) {
				String content = post.getContent();
				content = content.replaceAll("\\s", " ").trim();
				holder.postContent.setText(content);
				if (posts.get(position).isExpanded()) {
					holder.postContent.setMaxLines(500);
				} else {
					holder.postContent.setMaxLines(5);
				}
			}

			if (post.isMarked()) {
				convertView.setBackgroundColor(0xFFF0E68C);
			} else {
				convertView.setBackgroundColor(Color.WHITE);
			}
		}
		return convertView;
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

	public void togglePostMarkedStatus(int postIndex) {
		posts.get(postIndex).setMarked(true);
		notifyDataSetChanged();

	}

	public void untogglePostMarkedStatus(int postIndex) {
		posts.get(postIndex).setMarked(false);
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

	public Bitmap getUserImage(int userId) throws FileNotFoundException {

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
			throw new FileNotFoundException();
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new FileNotFoundException();
		}
	}

	public void toggleExpandedPostMarkedStatus() {
		Post post = posts.get(activity.positionSelected);
		postDao.updatePost(post);
	}

}
