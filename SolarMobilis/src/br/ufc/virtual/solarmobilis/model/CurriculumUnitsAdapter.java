package br.ufc.virtual.solarmobilis.model;

import java.util.ArrayList;
import java.util.List;

import br.ufc.virtual.solarmobilis.R;
import br.ufc.virtual.solarmobilis.model.DiscussionAdapter.Item;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CurriculumUnitsAdapter extends ArrayAdapter<String> {

	Context context;
	int resource;
	int layoutResourceId;
	ArrayList<String> curriculumUnits;

	public CurriculumUnitsAdapter(Context context, int resource,
			int layoutResourceId, ArrayList<String> curriculumUnits) {
		super(context, resource, layoutResourceId, curriculumUnits);
		this.context = context;
		this.resource = resource;
		this.layoutResourceId = layoutResourceId;
		this.curriculumUnits = curriculumUnits;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;

		Item item = null;

		if (row == null) {

			LayoutInflater inflater = ((Activity) context).getLayoutInflater();

			row = inflater.inflate(resource, parent, false);

			item = new Item();

			item.textView = (TextView) row.findViewById(R.id.item);

			row.setTag(item);

		}

		else {

			item = (Item) row.getTag();

		}

		// item.textView.setText(discussions.get(position));

		item.textView.setText(curriculumUnits.get(position));
		return row;

	}

	class Item {

		TextView textView;

	}

}
