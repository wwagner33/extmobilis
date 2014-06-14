package br.ufc.virtual.solarmobilis.webservice;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.ufc.virtual.solarmobilis.PostSender;
import br.ufc.virtual.solarmobilis.model.CurriculumUnitList;
import br.ufc.virtual.solarmobilis.model.DiscussionList;
import br.ufc.virtual.solarmobilis.model.DiscussionPostList;
import br.ufc.virtual.solarmobilis.model.GroupList;
import br.ufc.virtual.solarmobilis.model.LoginResponse;
import br.ufc.virtual.solarmobilis.model.SendPostResponse;
import br.ufc.virtual.solarmobilis.model.UserMessage;

@Rest(converters = { GsonHttpMessageConverter.class })
public interface SolarClient {

	@Post("sessions")
	LoginResponse doLogin(UserMessage user);

	@Get("curriculum_units/mobilis_list.json?auth_token={token}")
	CurriculumUnitList getCurriculumUnits(String token);

	@Get("curriculum_units/{id}/groups/mobilis_list.json?auth_token={token}")
	GroupList getGroups(String token, int id);

	@Get("groups/{id}/discussions/mobilis_list.json?auth_token={token}")
	DiscussionList getDiscussions(String token, int id);

	RestTemplate getRestTemplate();

	void setRootUrl(String rootUrl);

}
