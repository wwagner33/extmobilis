package br.ufc.virtual.solarmobilis.webservice;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import br.ufc.virtual.solarmobilis.util.BingURLCreator;

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
