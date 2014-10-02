package br.virtual.solarmobilis.view;

import java.util.List;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import br.ufc.virtual.solarmobilis.model.DiscussionPost;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;

@EBean
public class PostAdapter extends BaseAdapter {

	List<DiscussionPost> posts;

	@Bean
	SolarManager solarManager;

	@RootContext
	Context context;

	public void setPosts(List<DiscussionPost> posts) {
		this.posts = posts;
	}

	@Override
	public int getCount() {
		return posts.size();
	}

	@Override
	public Object getItem(int position) {
		return posts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		PostItemView postItemView;
		if (convertView == null) {
			postItemView = PostItemView_.build(context);
		} else {
			postItemView = (PostItemView) convertView;
		}

		postItemView.bind(posts.get(position), context);

		return postItemView;
	}

}
