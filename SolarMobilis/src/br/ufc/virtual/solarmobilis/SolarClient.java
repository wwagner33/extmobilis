package br.ufc.virtual.solarmobilis;

import java.util.prefs.Preferences;

import org.apache.http.TokenIterator;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Post;
import com.googlecode.androidannotations.annotations.rest.Rest;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@Rest(rootUrl = "http://apolo11teste.virtual.ufc.br/", converters = { GsonHttpMessageConverter.class })
public interface SolarClient {

	
	

	
	
	
	
	@Post("sessions")
	Object doLogin(UserMessage user);
	
	@Get("curriculum_units/list.json?auth_token={token}")
	Object getDisciplinas(String token);
	
    @Get("curriculum_units/1/groups.json?auth_token={token}")
    Object getTurmas(String token);
    
    @Get("groups//discussions.json?auth_token={token}")
	Object getDiscussions(String token);
	
	
	
}
