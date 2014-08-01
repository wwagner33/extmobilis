package br.ufc.virtual.solarmobilis.webservice;

import org.androidannotations.annotations.rest.Post;
import org.androidannotations.annotations.rest.RequiresHeader;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RequiresHeader("Authorization")
@Rest(converters = { FormHttpMessageConverter.class,
		GsonHttpMessageConverter.class })
public interface SolarClientPostFileSender {

	@Post("api/v1/posts/{postId}/files")
	Object sendPostaudioFile(MultiValueMap postAudioFile, Integer postId);

	RestTemplate getRestTemplate();

	void setRootUrl(String rootUrl);

	void setHeader(String name, String value);

	String getHeader(String name);

}
