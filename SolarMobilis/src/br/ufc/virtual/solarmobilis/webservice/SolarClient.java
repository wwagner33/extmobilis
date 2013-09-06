package br.ufc.virtual.solarmobilis.webservice;

import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.ufc.virtual.solarmobilis.model.CurriculumUnitList;
import br.ufc.virtual.solarmobilis.model.DiscussionList;
import br.ufc.virtual.solarmobilis.model.GroupList;
import br.ufc.virtual.solarmobilis.model.UserMessage;

import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Post;
import com.googlecode.androidannotations.annotations.rest.Rest;

@Rest(rootUrl = "http://apolo11teste.virtual.ufc.br/", converters = { GsonHttpMessageConverter.class })
public interface SolarClient {

	@Post("sessions")
	Object doLogin(UserMessage user);

	@Get("curriculum_units/mobilis_list.json?auth_token={token}")
	CurriculumUnitList getCurriculumUnits(String token);

	@Get("curriculum_units/{id}/groups/mobilis_list.json?auth_token={token}")
	GroupList getGroups(String token, int id);

	@Get("groups/{id}/discussions/mobilis_list.json?auth_token={token}")
	DiscussionList getDiscussions(String token, int id);

	RestTemplate getRestTemplate();

}
