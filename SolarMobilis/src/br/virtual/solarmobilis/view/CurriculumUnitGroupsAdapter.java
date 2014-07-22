package br.virtual.solarmobilis.view;

import java.util.List;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import br.ufc.virtual.solarmobilis.model.CurriculumUnit;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

@EBean
public class CurriculumUnitGroupsAdapter extends BaseExpandableListAdapter {

	List<CurriculumUnit> curriculumUnits;

	@Bean
	SolarManager solarManager;

	@RootContext
	Context context;

	public void setCurriculumUnits(List<CurriculumUnit> curriculumUnits) {

		this.curriculumUnits = curriculumUnits;

	}

	@Override
	public int getGroupCount() {
		return curriculumUnits.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return curriculumUnits.get(groupPosition).getGroups().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return curriculumUnits.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return curriculumUnits.get(groupPosition).getGroups()
				.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return curriculumUnits.get(groupPosition).getid();
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return curriculumUnits.get(groupPosition).getGroups()
				.get(childPosition).getId();
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		CurriculumUnitItemView curriculumUnitItemView;
		ExpandableListView expandableListView = (ExpandableListView) parent;

		if (convertView == null) {
			curriculumUnitItemView = CurriculumUnitItemView_.build(context);
			expandableListView.expandGroup(groupPosition);
		} else {
			curriculumUnitItemView = (CurriculumUnitItemView) convertView;
		}

		curriculumUnitItemView.bind(curriculumUnits.get(groupPosition));
		return curriculumUnitItemView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		GroupItemView groupItemView;

		if (convertView == null) {

			groupItemView = GroupItemView_.build(context);

		} else {

			groupItemView = (GroupItemView) convertView;

		}

		groupItemView.bind(curriculumUnits.get(groupPosition).getGroups()
				.get(childPosition));
		return groupItemView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
