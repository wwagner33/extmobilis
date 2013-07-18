package br.ufc.virtual.solarmobilis;

import org.springframework.http.converter.json.GsonHttpMessageConverter;

import com.googlecode.androidannotations.annotations.rest.Post;
import com.googlecode.androidannotations.annotations.rest.Rest;

@Rest(rootUrl = "http://apolo11teste.virtual.ufc.br/", converters = { GsonHttpMessageConverter.class })
public interface SolarClient {

	@Post("sessions")
	Object doLogin(UserMessage user);
}
