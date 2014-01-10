package br.ufc.virtual.solarmobilis.webservice;

import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Rest(converters = { FormHttpMessageConverter.class,
		ByteArrayHttpMessageConverter.class })
public interface SolarClientPostFileSender {

	@Post("posts/{postId}/post_files?auth_token={token}")
	Object sendPostaudioFile(MultiValueMap postAudioFile, Integer postId,
			String token);

	RestTemplate getRestTemplate();
	void setRootUrl(String rootUrl);

}
