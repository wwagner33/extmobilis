package br.ufc.virtual.solarmobilis.model;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import br.ufc.virtual.solarmobilis.R;

public class CurriculumUnitsAdapter extends ArrayAdapter<CurriculumUnit> {

	Context context;
	int resource;
	int layoutResourceId;
	List<CurriculumUnit> curriculumUnits;

	public CurriculumUnitsAdapter(Context context, int resource,
			int layoutResourceId, List<CurriculumUnit> curriculumUnits) {
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

		item.textView.setText(curriculumUnits.get(position).getName());
		return row;

	}

	class Item {

		TextView textView;

	}

}
