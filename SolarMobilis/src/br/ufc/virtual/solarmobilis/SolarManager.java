package br.ufc.virtual.solarmobilis;

import java.net.SocketTimeoutException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import android.app.Activity;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.rest.Post;
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

	
	
	public Object  doLogin(User user)/*throws SocketTimeoutException*/{
		
		UserMessage userMessage = new UserMessage();
		
		userMessage.setUser(user);
		
		return solarClient.doLogin(userMessage);
		
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
	
	
	public void errorHandler(HttpStatus statuscode){
		
		
		int code = Integer.parseInt(statuscode.toString());
		
		switch (code) {
		case 401:
			Toast.makeText(rootActivity, "Usuário ou senha inválido",
					Toast.LENGTH_SHORT).show();

			
			
			break;
		case 0:
			Toast.makeText(rootActivity, "Erro de Conexão", Toast.LENGTH_SHORT).show();
			break;
		case 699:
			Toast.makeText(rootActivity, "Tempo limite de conexão atingido",
					Toast.LENGTH_SHORT).show();
			break;
		case 400:
			Toast.makeText(rootActivity, "Erro desconhecido", Toast.LENGTH_SHORT)
					.show();
			break;
		case 500:
			Toast.makeText(rootActivity, "Servidor indisponível", Toast.LENGTH_SHORT)
					.show();
			break;
		case 404:
			Toast.makeText(rootActivity, "Endereço não encontrado", Toast.LENGTH_SHORT)
					.show();
			break;
		default:
			Toast.makeText(rootActivity, "Erro desconhecido", Toast.LENGTH_SHORT)
					.show();
		
		}
		
		
		
		
	}



	public SolarClient getsolarClient() {
		// TODO Auto-generated method stub
		return solarClient;
	}
	
}
	


