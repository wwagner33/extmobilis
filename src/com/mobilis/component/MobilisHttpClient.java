package com.mobilis.component;

import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.params.HttpConnectionParams;
import ch.boye.httpclientandroidlib.params.HttpParams;

public class MobilisHttpClient extends DefaultHttpClient {
	
	private static final int CONNECTION_TIMEOUT_MILLIS = 20000;
	
	public MobilisHttpClient() {
		setConnectionTimeout();
	}
	
	private void setConnectionTimeout() {
		HttpParams params = this.getParams(); 
		HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT_MILLIS);
		HttpConnectionParams.setSoTimeout(params, CONNECTION_TIMEOUT_MILLIS);
	}

}
