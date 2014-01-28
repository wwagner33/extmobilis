package br.ufc.virtual.solarmobilis.webservice;

public interface PostPlayerListener {
	public void onCompletion();

	public void onPostPlayException(Exception exception);
}
