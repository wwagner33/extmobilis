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
import android.widget.LinearLayout;
import android.widget.TextView;
import br.ufc.virtual.solarmobilis.R;

public class DiscussionAdapter extends ArrayAdapter<Discussion> {

	Context context;
	int layoutResourceId;
	List<Discussion> discussions;

	public DiscussionAdapter(Context context, int resource,
			int textViewResourceId, List<Discussion> discussions) {
		super(context, resource, textViewResourceId, discussions);
		this.context = context;
		this.layoutResourceId = resource;
		this.discussions = discussions;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		Item item = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			item = new Item();
			item.textView = (TextView) row.findViewById(R.id.topic_name);
			item.leftBar = (LinearLayout) row.findViewById(R.id.left_bar);

			if (!("1".equals(discussions.get(position).getStatus()))) {
				item.leftBar.setBackgroundColor(context.getResources()
						.getColor(R.color.very_dark_gray));
				item.textView.setTextColor(context.getResources().getColor(
						R.color.very_dark_gray));
			} else {
				item.leftBar.setBackgroundColor(Color.YELLOW);
			}

			row.setTag(item);
		} else {
			item = (Item) row.getTag();
		}

		item.textView.setText(discussions.get(position).getName());

		return row;
	}

	class Item {
		TextView textView;
		LinearLayout leftBar;
	}
}
