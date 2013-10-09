package br.ufc.virtual.solarmobilis.model;

import java.util.ArrayList;
import java.util.List;

import br.ufc.virtual.solarmobilis.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PostAdapter extends ArrayAdapter<DiscussionPosts> {

	Context context;
	int layoutResource;
	ArrayList<DiscussionPosts> objects;

	public PostAdapter(Context context, int resource, int textViewResourceId,
			List<DiscussionPosts> objects) {
		super(context, resource, textViewResourceId, objects);

		this.context = context;
		this.layoutResource = resource;
		this.objects = (ArrayList<DiscussionPosts>) objects;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		PostItem postitem = null;

		if (row == null) {

			LayoutInflater inflater = ((Activity) context).getLayoutInflater();

			row = inflater.inflate(layoutResource, parent, false);

			postitem = new PostItem();

			postitem.name = (TextView)row.findViewById(R.id.user_nick);
			postitem.date = (TextView)row.findViewById(R.id.post_date);
			postitem.content = (TextView)row.findViewById(R.id.post_content);

			row.setTag(postitem);
			
		}
		 else
	        {
	            postitem = (PostItem)row.getTag();
	        }
		
		postitem.name.setText(objects.get(position).getUserNick());
		postitem.date.setText(objects.get(position).getDateToPost());
		postitem.content.setText(objects.get(position).getContent());

		return row;
	}

	class PostItem {

		TextView name;
		TextView date;
		TextView content; //mudar o construtor na discussion post activity

	}

}