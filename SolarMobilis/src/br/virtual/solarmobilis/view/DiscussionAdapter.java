package br.virtual.solarmobilis.view;

import java.util.List;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import br.ufc.virtual.solarmobilis.model.Discussion;

@EBean
public class DiscussionAdapter extends BaseAdapter {

	@RootContext
	Context context;

	List<Discussion> discussions;

	public void setDiscussions(List<Discussion> discussions) {
		this.discussions = discussions;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DiscussionItemView discussionItemView;
		if (convertView == null) {
			discussionItemView = DiscussionItemView_.build(context);
		} else {
			discussionItemView = (DiscussionItemView) convertView;
		}

		discussionItemView.bind(getItem(position));

		return discussionItemView;
	}

	@Override
	public int getCount() {
		return discussions.size();
	}

	@Override
	public Discussion getItem(int position) {
		return discussions.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
