package br.ufc.virtual.solarmobilis.webservice.mobilis;

public interface ConnectionCallback {
	
	public void resultFromConnection(int connectionId, String result,
			int statusCode);
}
