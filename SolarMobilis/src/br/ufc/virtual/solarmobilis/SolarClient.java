package br.ufc.virtual.solarmobilis;

import org.json.JSONObject;
import org.springframework.http.converter.json.GsonHttpMessageConverter;


import com.googlecode.androidannotations.annotations.rest.Accept;
import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Rest;
import com.googlecode.androidannotations.api.rest.MediaType;

@Rest(converters = { GsonHttpMessageConverter.class })
public interface SolarClient {

	@Get("http://apolo11teste.virtual.ufc.br/curriculum_units/list.json?auth_token=gtPAzJy2yzGouUBhypif")
	@Accept(MediaType.APPLICATION_JSON)
	Object getCurriculumAndList();
}
