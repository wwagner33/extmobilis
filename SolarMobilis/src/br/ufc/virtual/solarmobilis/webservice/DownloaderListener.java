package br.ufc.virtual.solarmobilis.webservice;

import java.io.IOException;

public interface DownloaderListener {

	public void onDownload(String name, int i) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException;
	public void onDownloadException(Exception exception, int fileIndex);
}
