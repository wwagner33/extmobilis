package com.mobilis.util;

import android.os.Environment;

public class Constants {

	public static final String URL_LOCAL = "http://10.0.2.2:3000/";
	public static final String URL_SERVER = "http://apolo11.virtual.ufc.br/solar/";
	public static final String URL_TOKEN = "sessions";
	public static final String URL_COURSES = "curriculum_units/list.json";
	public static final String URL_GROUPS_PREFIX = "groups/";
	public static final String URL_GROUPS_SUFFIX = "/groups.json";
	public static final String URL_DISCUSSION_SUFFIX = "/discussions.json";
	public static final String URL_DISCUSSION_PREFIX = "discussions/";
	public static final String URL_CURRICULUM_UNITS_PREFIX = "curriculum_units/";
	public static final String URL_POSTS_SUFFIX = "/posts.json";

	public static String generateHistoryPostTTSURL(int topicId, String date) {

		String url = "discussions/" + topicId + "/posts/history/" + date
				+ "/order/asc" + "/limit/" + TOTAL_POSTS_TO_LOAD + ".json";
		return url;
	}

	public static String generateNewPostsTTSURL(int topicId, String date) {
		String url = "discussions/" + topicId + "/posts/news/" + date
				+ "/order/asc" + "/limit/" + TOTAL_POSTS_TO_LOAD + ".json";
		return url;
	}

	public static String generateAudioResponseURL(int postId) {
		String url = "posts/" + postId + "/post_files";
		return url;
	}

	public static String getImageURL(int postId) {
		return "users/" + postId + "/photo";
	}

	public static final String TEXT_PROGRESS_DIALOG = "Carregando";
	public static final int ERROR_CONNECTION_FAILED = 101;
	public static final int ERROR_CONNECTION_TIMEOUT = 102;

	public static final String DATABASE_PATH = "/data/data/com.mobilis.controller/databases/";
	public static final String DATABASE_NAME = "MobilisDB.sqlite";

	public static final int PARSE_TOKEN_ID = 221;
	public static final int PARSE_COURSES_ID = 222;
	public static final int PARSE_CLASSES_ID = 223;
	public static final int PARSE_TOPICS_ID = 224;
	public static final int PARSE_POSTS_ID = 225;
	public static final int PARSE_TEXT_RESPONSE_ID = 226;
	public static final long noParentId = 0;
	public static final String noParentString = "";
	public static final int PARSE_NEW_POSTS_ID = 227;
	public static final int PARSE_HISTORY_POSTS_ID = 228;

	public static final String PATH_SD_CARD = Environment
			.getExternalStorageDirectory().getAbsolutePath();
	public static final String PATH_MAIN_FOLDER = PATH_SD_CARD + "/Mobilis/";
	public static final String PATH_RECORDINGS = PATH_MAIN_FOLDER
			+ "/Recordings/";
	public static final String PATH_IMAGES = PATH_MAIN_FOLDER + "/Images/";
	public static final String PATH_IMAGESZIP = PATH_MAIN_FOLDER + "images.zip";

	public static final String RECORDING_FILENAME = "recording";
	public static final String RECORDING_EXTENSION = ".3gp";
	public static final String RECORDING_FULLNAME = "recording.3gp";

	public static final int TYPE_CONNECTION_POST = 300;
	public static final int TYPE_CONNECTION_GET = 301;

	public static final String REQUEST_FINISH_ACTIVITY_ON_RETURN_TEXT = "FinishActivity";
	public static final String REQUEST_FINISH_ACTIVITY_ON_RETURN_VALUE = "YES";

	public static final String oldDateString = "2000101010241010";

	public static final int TEXT_MAX_CHARACTER_LENGHT = 400;
	public static final int DIALOG_PROGRESS_STANDART = 401;
	public static final int DIALOG_ALERT_DISCARD = 402;

	public static final int DIALOG_PLAYBACK_AREA_CLICKED = 500;
	public static final int DIALOG_LISTEN_AREA_CLICKED = 501;
	public static final int DIALOG_DELETE_AREA_CLICKED = 502;
	public static final int DIALOG_ALERT_POSITIVE_BUTTON_CLICKED = 503;
	public static final int DIALOG_ALERT_NEGATIVE_BUTTON_CLICKED = 504;

	public static final int CONNECTION_POST_TOKEN = 600;
	public static final int CONNECTION_GET_COURSES = 601;
	public static final int CONNECTION_GET_CLASSES = 602;
	public static final int CONNECTION_GET_TOPICS = 603;
	public static final int CONNECTION_GET_NEW_POSTS = 604;
	public static final int CONNECTION_GET_IMAGES = 605;
	public static final int CONNECTION_GET_HISTORY_POSTS = 606;
	public static final int CONNECTION_POST_TEXT_RESPONSE = 607;
	public static final int CONNECTION_POST_AUDIO = 608;

	public static final String DATABASE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String WSCALL_DATE_FORMAT = "yyyyMMddHHmmss";
	public static final String AUDIO_DEFAULT_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/Mobilis/TTS/audios/";
	public static final int PLAY_NEXT_POST = 0;
	public static final int STOP_AUDIO = 1;
	public static final int ERROR_PLAYING = -1;
	public static final int CONNECTION_ERROR = -2;
	public static final int MIN_BLOCK_LENGTH = 200;
	public static final int MAX_BLOCK_LENGTH = 400;
	public static final int TOTAL_POSTS_TO_LOAD = 20;

}
