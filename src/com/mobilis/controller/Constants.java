package com.mobilis.controller;

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

	// Nova chamada

	// Dialogs
	public static final String TEXT_PROGRESS_DIALOG = "Carregando, por favor aguarde";

	// Error ids
	public static final int ERROR_CONNECTION_FAILED = 101;
	public static final int ERROR_CONNECTION_TIMEOUT = 102;
	public static final int ERROR_SERVER_DOWN = 103;
	public static final int ERROR_TOKEN_EXPIRED = 104;
	public static final int ERROR_UNKNOWN = 105;
	public static final int ERROR_PAGE_NOT_FOUND = 106;
	public static final int ERROR_CONNECTION_REFUSED = 107;
	public static final int ERROR_AUDIO_RECORDING_FAILED = 108;
	// public static final int BELOW_CHARACTER_LIMIT=2;

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

	// Folder Paths
	public static final String PATH_SD_CARD = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static final String PATH_MAIN_FOLDER = PATH_SD_CARD + "/Mobilis/";
	public static final String PATH_RECORDINGS = PATH_MAIN_FOLDER + "/Recordings/";
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
	// YYYYMMDDHH24MMSS

	public static final String oldDateString = "2000101010241010";

	public static final int DIALOG_PROGRESS = 400;
	public static final int DIALOG_ALERT = 401;

}
