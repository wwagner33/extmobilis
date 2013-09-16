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

public class DiscussionAdapter extends ArrayAdapter<String> {

	Context context;
	int layoutResourceId;
	ArrayList<String> objects;
	DiscussionList response;

	public DiscussionAdapter(Context context, int resource,
			int textViewResourceId, List<String> objects,
			DiscussionList response) {
		super(context, resource, textViewResourceId, objects);

		this.context = context;
		this.layoutResourceId = resource;
		this.objects = (ArrayList<String>) objects;
		this.response = response;
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
			item.leftbar = (LinearLayout) row.findViewById(R.id.left_bar);

			// area de tratamento
			if (!(response.getDiscussions().get(position).getStatus()
					.equals("1"))) {

				item.leftbar.setBackgroundColor(context.getResources()
						.getColor(R.color.very_dark_gray));

				item.textView.setTextColor(context.getResources().getColor(
						R.color.very_dark_gray));

			} else {

				item.leftbar.setBackgroundColor(Color.YELLOW);

			}

			// area de tratamento
			row.setTag(item);

		} else {

			item = (Item) row.getTag();

		}

		item.textView.setText(objects.get(position));

		return row;/* super.getView(position, convertView, parent); */
	}

	class Item {

		TextView textView;
		LinearLayout leftbar;
	}

}
