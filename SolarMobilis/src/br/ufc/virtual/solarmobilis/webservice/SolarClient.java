package br.ufc.virtual.solarmobilis.webservice;

import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.ufc.virtual.solarmobilis.model.LoginResponse;
import br.ufc.virtual.solarmobilis.model.UserMessage;

@Rest(converters = { GsonHttpMessageConverter.class })
public interface SolarClient {

	@Post("sessions")
	LoginResponse doLogin(UserMessage user);

	RestTemplate getRestTemplate();

	void setRootUrl(String rootUrl);

}
