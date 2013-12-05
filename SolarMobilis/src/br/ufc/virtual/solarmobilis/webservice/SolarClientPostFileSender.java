package br.ufc.virtual.solarmobilis.webservice;

import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.googlecode.androidannotations.annotations.rest.Post;
import com.googlecode.androidannotations.annotations.rest.Rest;

@Rest(rootUrl = "http://apolo11teste.virtual.ufc.br/", converters = {
		FormHttpMessageConverter.class, ByteArrayHttpMessageConverter.class })
public interface SolarClientPostFileSender {

	@Post("posts/{postId}/post_files?auth_token={token}")
	Object sendPostaudioFile(MultiValueMap postAudioFile, Integer postId,
			String token);

	RestTemplate getRestTemplate();

}
