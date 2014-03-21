package br.ufc.virtual.solarmobilis.model;

import com.google.gson.annotations.SerializedName;

public class Session {

	private String error;
    
	@SerializedName("auth_token")
	private String authToken;
	
    public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getAuth_token() {
		return authToken;
	}
	public void setAuth_token(String auth_token) {
		this.authToken = auth_token;
	}

	

}
