package br.ufc.virtual.solarmobilis.webservice;

import java.io.File;

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
import android.util.Log;
import android.widget.Toast;
import br.ufc.virtual.solarmobilis.LoginActivity_;
import br.ufc.virtual.solarmobilis.PostSender;
import br.ufc.virtual.solarmobilis.R;
import br.ufc.virtual.solarmobilis.SolarMobilisPreferences_;
import br.ufc.virtual.solarmobilis.model.CurriculumUnitList;
import br.ufc.virtual.solarmobilis.model.DiscussionList;
import br.ufc.virtual.solarmobilis.model.DiscussionPostList;
import br.ufc.virtual.solarmobilis.model.GroupList;
import br.ufc.virtual.solarmobilis.model.LoginResponse;
import br.ufc.virtual.solarmobilis.model.LoginResponseApi;
import br.ufc.virtual.solarmobilis.model.SendPostResponse;
import br.ufc.virtual.solarmobilis.model.User;
import br.ufc.virtual.solarmobilis.model.UserMessage;
import br.ufc.virtual.solarmobilis.webservice.mobilis.Connection;
import br.ufc.virtual.solarmobilis.webservice.mobilis.ConnectionCallback;
import br.ufc.virtual.solarmobilis.webservice.mobilis.Constants;

@EBean
public class SolarManager implements ConnectionCallback {
	public static final String SERVER_ROOT_URL = "http://apolo11teste.virtual.ufc.br/";

	@RootContext
	Activity rootActivity;

	@RestService
	SolarClient solarClient;

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
		solarClient.setRootUrl(SERVER_ROOT_URL);
		solarApiClient.setRootUrl(SERVER_ROOT_URL);
		solarClientPostFileSender.setRootUrl(SERVER_ROOT_URL);
	}

	public LoginResponse doLogin(User user) {
		UserMessage userMessage = new UserMessage();
		userMessage.setUser(user);

		return solarClient.doLogin(userMessage);

	}

	// ----------------------
	public LoginResponseApi doLogin2(User user) {

		return solarApiClient.doLogin(user);

	}

	// ----------------
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
		return solarApiClient.getPosts(preferences.authToken().get(), id, date);
	}

	public SendPostResponse sendPost(PostSender postSender, Integer id) {
		return solarClient.sendPost(postSender, id, preferences.token().get());
	}

	public Object sendPostAudio(File postAudioFile, Integer postId) {
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("post_file", new FileSystemResource(postAudioFile));
		return solarClientPostFileSender.sendPostaudioFile(parts, postId,
				preferences.token().get());
	}

	public void sendAudioPost(File postAudioFile, Integer postId) {
		connection = new Connection(this);

		String url = SERVER_ROOT_URL + "posts/" + postId
				+ "/post_files?auth_token=" + preferences.token().get();
		connection.postToServer(Constants.CONNECTION_POST_AUDIO, url,
				postAudioFile, preferences.token().get());
	}

	@Override
	public void resultFromConnection(int connectionId, String result,
			int statusCode) {
	}

	public String getUserImageUrl(int userId) {
		return (SERVER_ROOT_URL + "users/" + userId + "/photo?auth_token=" + preferences
				.token().get());
	}

	public String getAttachmentUrl(String link) {
		String root = (String) SERVER_ROOT_URL.subSequence(0,
				SERVER_ROOT_URL.length() - 1);
		return (root + link + "?auth_token=" + preferences.token().get());
	}

	public String getFileUrl(String link) {
		return (link + "?auth_token=" + preferences.token().get());
	}

	private void setTimeout() {
		setTimeout(20);
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
			if (preferences.token().get().length() != 0
					|| preferences.authToken().get().length() != 0) {
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

	public SolarClient getsolarClient() {
		return solarClient;
	}

	public void toast(int resourceMessageID) {
		Toast.makeText(rootActivity, resourceMessageID, Toast.LENGTH_SHORT)
				.show();
	}

}
