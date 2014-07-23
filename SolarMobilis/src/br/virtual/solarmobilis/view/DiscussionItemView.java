package br.virtual.solarmobilis.view;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.ufc.virtual.solarmobilis.R;
import br.ufc.virtual.solarmobilis.model.Discussion;

@EViewGroup(R.layout.discussion_list_item)
public class DiscussionItemView extends RelativeLayout {

	@ViewById(R.id.left_bar)
	LinearLayout leftBar;

	@ViewById(R.id.discussion_name)
	TextView name;

	public DiscussionItemView(Context context) {
		super(context);
	}

	public void bind(Discussion discussion) {
		if (!("1".equals(discussion.getStatus()))) {
			leftBar.setBackgroundColor(getResources().getColor(
					R.color.very_dark_gray));
			name.setTextColor(getResources().getColor(R.color.very_dark_gray));
		} else {
			leftBar.setBackgroundColor(getResources().getColor(
					R.color.yellow_hightlight));
		}
		name.setText(discussion.getName());
	}

}
