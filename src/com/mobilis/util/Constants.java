package com.mobilis.util;

import android.os.Environment;

public class Constants {

	// URL
	public static final String URL_LOCAL = "http://10.0.2.2:3000/";
	public static final String URL_SERVER = "http://apolo11teste.virtual.ufc.br/ws_solar/";
	public static final String URL_TOKEN = "sessions";
	public static final String URL_COURSES = "curriculum_units.json"; // suffix
	public static final String URL_GROUPS_PREFIX = "groups/";
	public static final String URL_GROUPS_SUFFIX = "/groups.json";
	public static final String URL_DISCUSSION_SUFFIX = "/discussions.json";
	public static final String URL_DISCUSSION_PREFIX = "discussions/";
	public static final String URL_CURRICULUM_UNITS_PREFIX = "curriculum_units/";
	public static final String URL_POSTS_SUFFIX = "/posts.json";

	// Dialogs
	public static final String TEXT_PROGRESS_DIALOG = "Carregando";

	// Error ids
	public static final int ERROR_CONNECTION_FAILED = 101;
	public static final int ERROR_CONNECTION_TIMEOUT = 102;

	// Database
	public static final String DATABASE_PATH = "/data/data/com.mobilis.controller/databases/";
	public static final String DATABASE_NAME = "MobilisDB.sqlite";

	// Parse IDs
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

	public static final String RECORDING_FILENAME = "recording"; // PREFIX
	public static final String RECORDING_EXTENSION = ".3gp";
	public static final String RECORDING_FULLNAME = "recording.3gp";

	// Connection Types
	public static final int TYPE_CONNECTION_POST = 300;
	public static final int TYPE_CONNECTION_GET = 301;

	// Requests
	public static final String REQUEST_FINISH_ACTIVITY_ON_RETURN_TEXT = "FinishActivity";
	public static final String REQUEST_FINISH_ACTIVITY_ON_RETURN_VALUE = "YES";

	public static final String oldDateString = "2000101010241010";

	// Texto
	public static final int TEXT_MAX_CHARACTER_LENGHT = 400;
	public static final int DIALOG_PROGRESS_STANDART = 401;
	public static final int DIALOG_ALERT_DISCARD = 402;

	// Dialog messages
	public static final int DIALOG_PLAYBACK_AREA_CLICKED = 500;
	public static final int DIALOG_LISTEN_AREA_CLICKED = 501;
	public static final int DIALOG_DELETE_AREA_CLICKED = 502;
	public static final int DIALOG_ALERT_POSITIVE_BUTTON_CLICKED = 503;
	public static final int DIALOG_ALERT_NEGATIVE_BUTTON_CLICKED = 504;

	// Id das conexões
	public static final int CONNECTION_POST_TOKEN = 600;
	public static final int CONNECTION_GET_COURSES = 601;
	public static final int CONNECTION_GET_CLASSES = 602;
	public static final int CONNECTION_GET_TOPICS = 603;
	public static final int CONNECTION_GET_NEW_POSTS = 604;
	public static final int CONNECTION_GET_IMAGES = 605;
	public static final int CONNECTION_GET_HISTORY_POSTS = 606;
	public static final int CONNECTION_POST_TEXT_RESPONSE = 607;
	public static final int CONNECTION_POST_AUDIO = 608;

	// Mesagens de Conexão bem sucedida
	public static final int MESSAGE_CONNECTION_FAILED = 700;
	public static final int MESSAGE_TOKEN_CONNECTION_OK = 701;
	public static final int MESSAGE_COURSE_CONNECTION_OK = 702;
	public static final int MESSAGE_CLASS_CONNECTION_OK = 703;
	public static final int MESSAGE_TOPIC_CONNECTION_OK = 704;
	public static final int MESSAGE_NEW_POST_CONNECTION_OK = 705;
	public static final int MESSAGE_IMAGE_CONNECTION_OK = 706;
	public static final int MESSAGE_HISTORY_POST_CONNECTION_OK = 707;
	public static final int MESSAGE_AUDIO_POST_OK = 708;
	public static final int MESSAGE_TEXT_RESPONSE_OK = 709;
	public static final int MESSAGE_IMAGE_CONNECION_FAILED = 710;

}
