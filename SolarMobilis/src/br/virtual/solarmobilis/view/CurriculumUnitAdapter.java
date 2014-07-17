package br.virtual.solarmobilis.view;

import java.util.List;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import br.ufc.virtual.solarmobilis.model.CurriculumUnit;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

@EBean
public class CurriculumUnitAdapter extends BaseAdapter {

	List<CurriculumUnit> curriculumUnits;

	@Bean
	SolarManager solarManager;

	@RootContext
	Context context;

	public void setCurriculumUnits(List<CurriculumUnit> curriculumUnits) {

		this.curriculumUnits = curriculumUnits;

	}

	@Override
	public int getCount() {

		return curriculumUnits.size();
	}

	@Override
	public Object getItem(int position) {

		return curriculumUnits.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		CurriculumUnitItemView curriculumUnitItemView;

		if (convertView == null) {

			curriculumUnitItemView = CurriculumUnitItemView_.build(context);

		} else {

			curriculumUnitItemView = (CurriculumUnitItemView) convertView;

		}

		curriculumUnitItemView.bind(curriculumUnits.get(position));
		return curriculumUnitItemView;

	}

}
