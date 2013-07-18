package br.ufc.virtual.solarmobilis;

import org.json.JSONObject;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.w3c.dom.UserDataHandler;

import com.google.gson.JsonObject;
import com.googlecode.androidannotations.annotations.rest.Accept;
import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Post;
import com.googlecode.androidannotations.annotations.rest.Rest;
import com.googlecode.androidannotations.api.rest.MediaType;

@Rest(converters = { GsonHttpMessageConverter.class })
public interface SolarClient {

	@Post("http://apolo11teste.virtual.ufc.br/sessions")
	Object postUser(UserMessenge user);
}
