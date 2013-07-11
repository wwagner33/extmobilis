package com.example.solarmobilis;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.googlecode.androidannotations.annotations.rest.Rest;

@Rest(rootUrl="http://apolo11teste.virtual.ufc.br", converters = { MappingJackson2HttpMessageConverter.class })
public interface SolarClient {

}
