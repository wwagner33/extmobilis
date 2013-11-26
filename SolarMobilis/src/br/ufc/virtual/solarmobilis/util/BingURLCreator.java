package br.ufc.virtual.solarmobilis.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class BingURLCreator {
	private static final String BING_BASE_URI = "http://api.microsofttranslator.com/v2/Http.svc/Speak?appId={appId}&text=\"{text}\"";
	private static final String BING_APP_ID = "APP_ID";
	private static final String LANGUAGE = "&language=pt-br";

	public static String getURL(final String text) {
		try {
			return BING_BASE_URI.replace("{appId}", BING_APP_ID).replace(
					"{text}", URLEncoder.encode(text, "UTF-8"))
					+ LANGUAGE;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
}