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
				+ "/photo?auth_token=" + preferences.token().get());

	}

	private void setTimeout() {
		setTimeout(10);
	}

	private void setTimeout(int seconds) {

		int mil = 1000;
		ClientHttpRequestFactory requestFactory = solarClient.getRestTemplate()
				.getRequestFactory();
		if (requestFactory instanceof SimpleClientHttpRequestFactory) {
			((SimpleClientHttpRequestFactory) requestFactory)
					.setConnectTimeout(seconds * mil);
			((SimpleClientHttpRequestFactory) requestFactory)
					.setReadTimeout(seconds * mil);
		} else if (requestFactory instanceof HttpComponentsClientHttpRequestFactory) {
			((HttpComponentsClientHttpRequestFactory) requestFactory)
					.setReadTimeout(seconds * mil);
			((HttpComponentsClientHttpRequestFactory) requestFactory)
					.setConnectTimeout(seconds * mil);
		}
	}

	@UiThread
	public void errorHandler(HttpStatus statuscode) {

		int code = Integer.parseInt(statuscode.toString());

		switch (code) {
		case 401:
			toast(R.string.ERROR_AUTHENTICATION);
			if (preferences.token().get().length() != 0) {
				logout();
			}
			break;
		case 0:
			toast(R.string.ERROR_CONECTION);
			break;
		case 400:
			toast(R.string.ERROR_UNKNOWN);
			break;
		case 500:
			toast(R.string.ERROR_SERVER);

			break;
		case 404:
			toast(R.string.ERROR_ADDRESS);
			break;
		case 408:
			toast(R.string.ERROR_TIMEOUT);
			break;
		default:
			toast(R.string.ERROR_UNKNOWN);
		}
	}

	public void logout() {
		preferences.token().put(null);
		Intent intent = new Intent(rootActivity, LoginActivity_.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		rootActivity.startActivity(intent);
		rootActivity.finish();
	}

	@UiThread
	public void alertNoConnection() {
		toast(R.string.ERROR_NO_CONECTION);
	}

	public SolarClient getsolarClient() {
		return solarClient;
	}

	public void toast(int resourceMessageID) {
		Toast.makeText(rootActivity, resourceMessageID, Toast.LENGTH_SHORT)
				.show();
	}

}
