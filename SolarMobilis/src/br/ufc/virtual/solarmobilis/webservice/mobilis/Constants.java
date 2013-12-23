package br.ufc.virtual.solarmobilis.webservice.mobilis;


public class Constants {

	public static final String URL_SERVER = "http://apolo11teste.virtual.ufc.br/";
	public static final String URL_POSTS_SUFFIX = "/posts.json";

	public static String generateAudioResponseURL(int postId) {
		String url = "posts/" + postId + "/post_files";
		return url;
	}

	public static final String RECORDING_MIME_TYPE = "audio/aac";
	public static final int TYPE_CONNECTION_POST = 300;
	public static final int CONNECTION_POST_AUDIO = 608;

}
