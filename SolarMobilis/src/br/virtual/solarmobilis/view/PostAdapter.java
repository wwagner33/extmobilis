package br.virtual.solarmobilis.view;

import java.util.List;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import br.ufc.virtual.solarmobilis.R;
import br.ufc.virtual.solarmobilis.model.CurriculumUnit;
import br.ufc.virtual.solarmobilis.model.DiscussionPost;
import br.ufc.virtual.solarmobilis.model.Post;
import br.ufc.virtual.solarmobilis.webservice.SolarManager;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

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
		// TODO Auto-generated method stub
		return posts.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return posts.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
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
