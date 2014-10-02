package br.virtual.solarmobilis.view;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.ufc.virtual.solarmobilis.R;
import br.ufc.virtual.solarmobilis.model.Group;

@EViewGroup(R.layout.item_list)
public class GroupItemView extends RelativeLayout {

	@ViewById(R.id.sub_item)
	TextView name;
	@ViewById(R.id.itens_layout)
	RelativeLayout layout;

	public GroupItemView(Context context) {
		super(context);

	}

	@AfterViews
	public void setLayout() {
		layout.setPadding(15, 0, 0, 0);
	}

	public void bind(Group group) {

		name.setText(group.getCode());

	}

}
