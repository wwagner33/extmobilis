package br.virtual.solarmobilis.view;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.ufc.virtual.solarmobilis.R;
import br.ufc.virtual.solarmobilis.model.CurriculumUnit;

@EViewGroup(R.layout.item_list)
public class CurriculumUnitItemView extends RelativeLayout {

	@ViewById(R.id.item)
	TextView name;
	public CurriculumUnitItemView(Context context) {

		super(context);

	}

	public void bind(CurriculumUnit curriculumUnit) {

		name.setText(curriculumUnit.getName());

	}

}
