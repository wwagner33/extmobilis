package br.ufc.virtual.solarmobilis.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponseApi {

	@SerializedName("access_token")
	private String accessToken;
	
	@SerializedName("token_type")
	private String tokenType;
	
	@SerializedName("expires_in")
	private int expiresIn;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public int getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}
	
	 /*"access_token": "4919ad1f1bdf1aa6a89664139e05ad3c56f8b5c1abbf42fe088aa5e53b1aef71",
	    "token_type": "bearer",
	    "expires_in": 315576000*/

}
