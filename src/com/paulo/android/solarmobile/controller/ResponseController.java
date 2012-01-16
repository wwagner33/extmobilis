package com.paulo.android.solarmobile.controller;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;

public class ResponseController extends Activity {

	File recordingsFolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.responder_topico);

	}
}
