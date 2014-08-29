package br.virtual.solarmobilis.view;

import java.util.List;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.ufc.virtual.solarmobilis.R;
import br.ufc.virtual.solarmobilis.model.CurriculumUnit;
import br.ufc.virtual.solarmobilis.model.DiscussionPost;
import br.ufc.virtual.solarmobilis.model.Post;
import br.ufc.virtual.solarmobilis.model.PostAdapter;

@EViewGroup(R.layout.discussion_post_list_item)
public class PostItemView extends RelativeLayout {

	public PostItemView(Context context) {
		super(context);

	}

	@ViewById(R.id.user_nick)
	TextView userNick;
	@ViewById(R.id.post_date)
	TextView postDate;
	@ViewById(R.id.post_content)
	TextView postContent;
	@ViewById(R.id.user_photo)
	ImageView UserImage;

	public void bind(DiscussionPost post, Context context) {
		//
		userNick.setText(post.getUserNick());
		postDate.setText(post.getDateToPost());
		postContent.setText(Html.fromHtml(post.getContent()));

		Picasso.with(context).load(post.getUserImageURL())
				.placeholder(R.drawable.no_picture)
				.error(R.drawable.no_picture).into(UserImage);

	}

}
