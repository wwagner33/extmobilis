package br.ufc.virtual.solarmobilis.webservice;

import br.ufc.virtual.solarmobilis.util.BingURLCreator;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class BingAudioDownloader extends AudioDownloader {

	public BingAudioDownloader() {

	}

	@Background
	public void saveAudio(String text, int i) {

		try {
			stringUrl = BingURLCreator.getURL(text);
			saveFile(stringUrl, i);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
