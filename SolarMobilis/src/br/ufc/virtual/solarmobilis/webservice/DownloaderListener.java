package br.ufc.virtual.solarmobilis.webservice;

public interface DownloaderListener {

	public void onDownload(String name, int i);
	public void onDownloadException(Exception exception);
}
