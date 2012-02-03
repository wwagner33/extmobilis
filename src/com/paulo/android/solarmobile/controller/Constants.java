package com.paulo.android.solarmobile.controller;

public class Constants {

	// URL
	public static final String URL_LOCAL = "http://10.0.2.2:3000/";
	public static final String URL_SERVER = "http://apolo11teste.virtual.ufc.br/ws_solar/";
	public static final String URL_TOKEN = "sessions";

	// Dialogs
	public static final String TEXT_PROGRESS_DIALOG = "Carregando, por favor aguarde";
	
	//Error ids
	public static final int ERROR_CONNECTION_FAILED = 101;
	public static final int ERROR_CONNECTION_TIMEOUT=102;
	public static final int ERROR_SERVER_DOWN=103;
	public static final int ERROR_TOKEN_EXPIRED=104;
	public static final int ERROR_UNKNOWN=105;
	public static final int ERROR_PAGE_NOT_FOUND = 106;
	//public static final int BELOW_CHARACTER_LIMIT=2;
	
	
	// Database
	public static final String DATABASE_PATH = "/data/data/com.paulo.android.solarmobile.controller/databases/";
	public static final String DATABASE_NAME = "MobilisDB.sqlite";
	
	//Parse IDs
	public static final int PARSE_TOKEN_ID = 221;
	public static final int PARSE_COURSES_ID = 222;
	public static final int PARSE_CLASSES = 223;

}
