package br.ufc.virtual.solarmobilis.webservice;

import java.io.File;
import java.util.List;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.rest.RestService;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import br.ufc.virtual.solarmobilis.LoginActivity_;
import br.ufc.virtual.solarmobilis.PostSender;
import br.ufc.virtual.solarmobilis.R;
import br.ufc.virtual.solarmobilis.SolarMobilisPreferences_;
import br.ufc.virtual.solarmobilis.model.CurriculumUnit;
import br.ufc.virtual.solarmobilis.model.Discussion;
import br.ufc.virtual.solarmobilis.model.DiscussionPostList;
import br.ufc.virtual.solarmobilis.model.Group;
import br.ufc.virtual.solarmobilis.model.LoginResponse;
import br.ufc.virtual.solarmobilis.model.SendPostResponse;
import br.ufc.virtual.solarmobilis.model.User;
import br.ufc.virtual.solarmobilis.webservice.mobilis.Connection;
import br.ufc.virtual.solarmobilis.webservice.mobilis.ConnectionCallback;
import br.ufc.virtual.solarmobilis.webservice.mobilis.Constants;

@EBean
public class SolarManager implements ConnectionCallback {
	// public static final String SERVER_ROOT_URL =
	// "http://solar2.virtual.ufc.br/";
	public static final String SERVER_ROOT_URL = "http://200.129.43.170/";

	@RootContext
	Activity rootActivity;

	@RestService
	SolarApiClient solarApiClient;

	@RestService
	SolarClientPostFileSender solarClientPostFileSender;

	@Pref
	SolarMobilisPreferences_ preferences;

	private Connection connection;

	@AfterInject
	public void config() {
		setTimeout();
		solarApiClient.setRootUrl(SERVER_ROOT_URL);
		solarClientPostFileSender.setRootUrl(SERVER_ROOT_URL);
	}

	public LoginResponse doLogin(User user) {
		return solarApiClient.doLogin(user);
	}

	public List<CurriculumUnit> getCurriculumUnits() {
		return solarApiClient.getCurriculumUnits(preferences.authToken().get());
	}

	public List<Group> getGroups(int id) {
		return solarApiClient.getGroups(preferences.authToken().get(), id);
	}

	public List<Discussion> getDiscussions(int id) {
		return solarApiClient.getDiscussions(preferences.authToken().get(), id);
	}

	public DiscussionPostList getPosts(int id, String date, int groupId) {
		return solarApiClient.getPosts(preferences.authToken().get(), id, date,
				groupId);
	}

	public SendPostResponse sendPost(PostSender postSender, Integer id,
			int groupId) {
		return solarApiClient.sendPost(postSender, id, preferences.authToken()
				.get(), groupId);
	}

	// send audio post n�o utilizado
	public Object sendPostAudio(File postAudioFile, Integer postId) {
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", new FileSystemResource(postAudioFile));
		return solarClientPostFileSender.sendPostaudioFile(parts, postId,
				preferences.authToken().get());
	}

	// send audio post atual
	public void sendAudioPost(File postAudioFile, Integer postId) {
		connection = new Connection(this);

		String url = SERVER_ROOT_URL + "api/v1/posts/" + postId
				+ "/files?access_token=" + preferences.authToken().get();

		connection.postToServer(Constants.CONNECTION_POST_AUDIO, url,
				postAudioFile, preferences.authToken().get());
	}

	// send audio post atual
	@Override
	public void resultFromConnection(int connectionId, String result,
			int statusCode) {
	}

	public String getUserImageUrl(int userId) {
		return (SERVER_ROOT_URL + "api/v1/users/" + userId
				+ "/photo?style=small&access_token=" + preferences.authToken()
				.get());
	}

	public String getFileUrl(String link) {
		return (link + "?access_token=" + preferences.authToken().get());
	}

	private void setTimeout() {
		setTimeout(35);
	}

	private void setTimeout(int seconds) {

		int mil = 1000;
		ClientHttpRequestFactory requestFactory = solarApiClient
				.getRestTemplate().getRequestFactory();
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
			if (preferences.authToken().get().length() != 0) {
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
		preferences.authToken().put(null);
		Intent intent = new Intent(rootActivity, LoginActivity_.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		rootActivity.startActivity(intent);
		rootActivity.finish();
	}

	@UiThread
	public void alertNoConnection() {
		toast(R.string.ERROR_NO_CONECTION);
	}

	public void toast(int resourceMessageID) {
		Toast.makeText(rootActivity, resourceMessageID, Toast.LENGTH_SHORT)
				.show();
	}

}
