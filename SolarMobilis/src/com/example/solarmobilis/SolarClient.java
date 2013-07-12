package com.example.solarmobilis;

import org.json.JSONObject;
import org.springframework.http.converter.FormHttpMessageConverter;

import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Rest;

@Rest(rootUrl="http://apolo11teste.virtual.ufc.br", converters = { FormHttpMessageConverter.class })
public interface SolarClient {
	
	@Get("/curriculum_units/list.json?auth_token=NGHXh3Zharxkwri229SD")
	JSONObject getCurriculumAndList();
}
