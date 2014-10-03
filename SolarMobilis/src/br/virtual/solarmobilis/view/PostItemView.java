package br.virtual.solarmobilis.view;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import android.content.Context;
import android.text.Html;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.ufc.virtual.solarmobilis.R;
import br.ufc.virtual.solarmobilis.model.DiscussionPost;

import com.squareup.picasso.Picasso;

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
		userNick.setText(post.getUserNick());
		postDate.setText(post.getDateToPost());
		postContent.setText(Html.fromHtml(post.getContent()));

		Picasso.with(context).load(post.getUserImageURL())
				.placeholder(R.drawable.no_picture)
				.error(R.drawable.no_picture).into(UserImage);

	}

}
