package br.ufc.virtual.solarmobilis;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import android.app.Activity;

import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.rest.RestService;

@EBean
public class SolarManager {
	
	@RootContext
	Activity rootActivity;

	@RestService
	SolarClient solarClient;

	@AfterInject
	public void config() {
		setTimeout();
	}

	private void setTimeout() {
		ClientHttpRequestFactory requestFactory = solarClient.getRestTemplate()
				.getRequestFactory();
		if (requestFactory instanceof SimpleClientHttpRequestFactory) {
			((SimpleClientHttpRequestFactory) requestFactory)
					.setConnectTimeout(10 * 1000);
			((SimpleClientHttpRequestFactory) requestFactory)
					.setReadTimeout(10 * 1000);
		} else if (requestFactory instanceof HttpComponentsClientHttpRequestFactory) {
			((HttpComponentsClientHttpRequestFactory) requestFactory)
					.setReadTimeout(10 * 1000);
			((HttpComponentsClientHttpRequestFactory) requestFactory)
					.setConnectTimeout(10 * 1000);

		}
	}

}
