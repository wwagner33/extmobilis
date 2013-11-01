package br.ufc.virtual.solarmobilis.webservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import br.ufc.virtual.solarmobilis.LoginActivity_;
import br.ufc.virtual.solarmobilis.PostSender;
import br.ufc.virtual.solarmobilis.R;
import br.ufc.virtual.solarmobilis.SolarMobilisPreferences_;
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
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@EBean
public class SolarManager {

	@RootContext
	Activity rootActivity;

	@RestService
	SolarClient solarClient;

	@Pref
	SolarMobilisPreferences_ preferences;

	@AfterInject
	public void config() {
		setTimeout();
	}

	public Object doLogin(User user) {
		UserMessage userMessage = new UserMessage();
		userMessage.setUser(user);
		return solarClient.doLogin(userMessage);
	}

	public CurriculumUnitList getCurriculumUnits() {
		return solarClient.getCurriculumUnits(preferences.token().get());
	}

	public GroupList getGroups(int id) {
		return solarClient.getGroups(preferences.token().get(), id);
	}

	public DiscussionList getDiscussions(int id) {
		return solarClient.getDiscussions(preferences.token().get(), id);
	}

	public DiscussionPostList getPosts(int id, String date) {
		return solarClient.getPosts(preferences.token().get(), id, date);
	}

	public void sendPost(PostSender postSender, Integer id) {
		solarClient.sendPost(postSender, id, preferences.token().get());
	}

	public String getUserImageUrl(int userId) {
		return ("http://apolo11teste.virtual.ufc.br/users/" + userId
				+ "/photo.json?auth_token=" + preferences.token().get());

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
			if (preferences.token().get().length() != 0) {
				logout();
			}

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

	public void logout() {
		preferences.token().put(null);
		Intent intent = new Intent(rootActivity, LoginActivity_.class);
		rootActivity.startActivity(intent);
		rootActivity.finish();
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
