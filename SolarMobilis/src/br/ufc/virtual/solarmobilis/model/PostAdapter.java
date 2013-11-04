package br.ufc.virtual.solarmobilis.model;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.ufc.virtual.solarmobilis.R;

import com.squareup.picasso.Picasso;

public class PostAdapter extends ArrayAdapter<DiscussionPost> {

	Context context;
	int layoutResource;
	ArrayList<DiscussionPost> posts;

	public PostAdapter(Context context, int resource, int textViewResourceId,
			List<DiscussionPost> objects) {
		super(context, resource, textViewResourceId, objects);

		this.context = context;
		this.layoutResource = resource;
		this.posts = (ArrayList<DiscussionPost>) objects;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		PostItem postItem = null;

		if (row == null) {

			LayoutInflater inflater = ((Activity) context).getLayoutInflater();

			row = inflater.inflate(layoutResource, parent, false);

			postItem = new PostItem();

			postItem.name = (TextView) row.findViewById(R.id.user_nick);
			postItem.date = (TextView) row.findViewById(R.id.post_date);
			postItem.content = (TextView) row.findViewById(R.id.post_content);
			postItem.image = (ImageView) row.findViewById(R.id.user_photo);

			row.setTag(postItem);

		} else {
			postItem = (PostItem) row.getTag();
		}

		postItem.name.setText(posts.get(position).getUserNick());
		postItem.date.setText(posts.get(position).getDateToPost());
		postItem.content.setText(posts.get(position).getContent());

		Picasso.with(context).load(posts.get(position).getUserImageURL())
				.placeholder(R.drawable.no_picture)
				.error(R.drawable.no_picture).into(postItem.image);

		if (posts.get(position).isMarked) {
			row.setBackgroundColor(0xFFF0E68C);
		} else {

			row.setBackgroundColor(Color.WHITE);
		}

		return row;
	}

	class PostItem {

		TextView name;
		TextView date;
		TextView content;
		ImageView image;

	}

}
