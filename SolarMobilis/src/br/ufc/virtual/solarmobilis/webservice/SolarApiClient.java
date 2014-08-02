package br.ufc.virtual.solarmobilis.webservice;

import java.util.List;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.RequiresHeader;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.ufc.virtual.solarmobilis.model.CurriculumUnit;
import br.ufc.virtual.solarmobilis.model.Discussion;
import br.ufc.virtual.solarmobilis.model.DiscussionPostList;
import br.ufc.virtual.solarmobilis.model.Group;
import br.ufc.virtual.solarmobilis.model.LoginResponse;
import br.ufc.virtual.solarmobilis.model.PostSender;
import br.ufc.virtual.solarmobilis.model.SendPostResponse;
import br.ufc.virtual.solarmobilis.model.User;

@RequiresHeader("Authorization")
@Rest(converters = { GsonHttpMessageConverter.class })
public interface SolarApiClient {

	@Post("oauth/token")
	LoginResponse doLogin(User user);

	@Get("api/v1/curriculum_units")
	List<CurriculumUnit> getCurriculumUnits();

	@Get("api/v1/curriculum_units/groups")
	List<CurriculumUnit> getCurriculumUnitsAndGroups();

	@Get("api/v1/curriculum_units/{curriculumUnitId}/groups")
	List<Group> getGroups(int curriculumUnitId);

	@Get("api/v1/groups/{groupId}/discussions")
	List<Discussion> getDiscussions(int groupId);

	@Get("api/v1/discussions/{id}/posts/new/?date={date}&group_id={groupId}")
	DiscussionPostList getPosts(int id, String date, int groupId);

	@Post("api/v1/discussions/{discussionId}/posts?group_id={groupId}")
	SendPostResponse sendPost(PostSender postSender, Integer discussionId,
			int groupId);

	RestTemplate getRestTemplate();

	void setRootUrl(String rootUrl);

	void setHeader(String name, String value);

	String getHeader(String name);

}
