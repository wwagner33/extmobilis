package br.ufc.virtual.solarmobilis;

import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.ufc.virtual.model.CurriculumUnitList;

import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Post;
import com.googlecode.androidannotations.annotations.rest.Rest;

@Rest(rootUrl = "http://apolo11teste.virtual.ufc.br/", converters = { GsonHttpMessageConverter.class })
public interface SolarClient {

	@Post("sessions")
	Object doLogin(UserMessage user);

	@Get("curriculum_units/mobilis_list.json?auth_token={token}")
	CurriculumUnitList getCurriculumUnits(String token);

	@Get("curriculum_units/1/groups.json?auth_token={token}")
	Object getGroups(String token);

	@Get("groups//discussions.json?auth_token={token}")
	Object getDiscussions(String token);

	RestTemplate getRestTemplate();

}
