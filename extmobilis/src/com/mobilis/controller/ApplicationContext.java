package com.mobilis.controller;

import com.crittercism.app.Crittercism;

import android.app.Application;

public class ApplicationContext extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		Crittercism.init(getApplicationContext(), "517dca7b558d6a0aa3000002");
	}

}
