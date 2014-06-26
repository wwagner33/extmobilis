package br.ufc.virtual.solarmobilis.webservice;

import java.util.List;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.ufc.virtual.solarmobilis.PostSender;
import br.ufc.virtual.solarmobilis.model.CurriculumUnit;
import br.ufc.virtual.solarmobilis.model.DiscussionPostList;
import br.ufc.virtual.solarmobilis.model.LoginResponseApi;
import br.ufc.virtual.solarmobilis.model.SendPostResponse;
import br.ufc.virtual.solarmobilis.model.User;

@Rest(converters = { GsonHttpMessageConverter.class })
public interface SolarApiClient {

	@Post("oauth/token")
	LoginResponseApi doLogin(User user);
	
	@Get("api/v1/curriculum_units?access_token={token}")
	List<CurriculumUnit> getCurriculumUnits(String token);

	@Get("api/v1/discussions/{id}/posts/new/?date={date}&access_token={token}&group_id={groupId}")
	DiscussionPostList getPosts(String token, int id, String date, int groupId);

	@Post("api/v1/discussions/{discussionId}/posts?access_token={token}&group_id={groupId}")
	SendPostResponse sendPost(PostSender postSender, Integer discussionId,
			String token, int groupId);

	RestTemplate getRestTemplate();

	void setRootUrl(String rootUrl);

}
