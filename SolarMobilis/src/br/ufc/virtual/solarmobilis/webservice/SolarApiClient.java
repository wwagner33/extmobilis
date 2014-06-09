package br.ufc.virtual.solarmobilis.webservice;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.ufc.virtual.solarmobilis.model.DiscussionPostList;
import br.ufc.virtual.solarmobilis.model.LoginResponseApi;
import br.ufc.virtual.solarmobilis.model.User;

@Rest(converters = { GsonHttpMessageConverter.class })
public interface SolarApiClient {

	@Post("oauth/token")
	LoginResponseApi doLogin(User user);

	@Get("api/v1/discussions/{id}/posts/new/?date={date}&access_token={token}")
	DiscussionPostList getPosts(String token, int id, String date);

	RestTemplate getRestTemplate();

	void setRootUrl(String rootUrl);

}
