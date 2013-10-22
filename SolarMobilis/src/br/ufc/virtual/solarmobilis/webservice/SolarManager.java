package br.ufc.virtual.solarmobilis.webservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import android.app.Activity;
import android.widget.Toast;
import br.ufc.virtual.solarmobilis.PostSender;
import br.ufc.virtual.solarmobilis.R;
import br.ufc.virtual.solarmobilis.model.CurriculumUnitList;
import br.ufc.virtual.solarmobilis.model.DiscussionList;
import br.ufc.virtual.solarmobilis.model.DiscussionPostList;
import br.ufc.virtual.solarmobilis.model.GroupList;
import br.ufc.virtual.solarmobilis.model.User;
import br.ufc.virtual.solarmobilis.model.UserMessage;

import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;
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

	public Object doLogin(User user) {
		UserMessage userMessage = new UserMessage();
		userMessage.setUser(user);
		return solarClient.doLogin(userMessage);
	}

	public CurriculumUnitList getCurriculumUnits(String token) {
		return solarClient.getCurriculumUnits(token);
	}

	public GroupList getGroups(String token, int id) {
		return solarClient.getGroups(token, id);
	}

	public DiscussionList getDiscussions(String token, int id) {
		return solarClient.getDiscussions(token, id);
	}

	public DiscussionPostList getPosts(String token, int id, String date) {
		return solarClient.getPosts(token, id, date);
	}

	public void sendPost(PostSender postSender, Integer id, String token) {
		solarClient.sendPost(postSender, id, token);
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

	@UiThread
	public void errorHandler(HttpStatus statuscode) {

		int code = Integer.parseInt(statuscode.toString());

		switch (code) {
		case 401:
			Toast.makeText(rootActivity, R.string.ERROR_AUTHENTICATION,
					Toast.LENGTH_SHORT).show();
			break;
		case 0:
			Toast.makeText(rootActivity, R.string.ERROR_CONECTION,
					Toast.LENGTH_SHORT).show();
			break;
		case 400:
			Toast.makeText(rootActivity, R.string.ERROR_UNKNOWN,
					Toast.LENGTH_SHORT).show();
			break;
		case 500:
			Toast.makeText(rootActivity, R.string.ERROR_SERVER,
					Toast.LENGTH_SHORT).show();
			break;
		case 404:
			Toast.makeText(rootActivity, R.string.ERROR_ADDRESS,
					Toast.LENGTH_SHORT).show();
			break;
		default:
			Toast.makeText(rootActivity, R.string.ERROR_UNKNOWN,
					Toast.LENGTH_SHORT).show();
		}
	}

	@UiThread
	public void alertTimeout() {
		Toast.makeText(rootActivity, R.string.ERROR_TIMEOUT, Toast.LENGTH_SHORT)
				.show();
	}

	public SolarClient getsolarClient() {
		return solarClient;
	}


}
