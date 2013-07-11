package com.example.solarmobilis;

import com.googlecode.androidannotations.annotations.EActivity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;



@EActivity(R.layout.activity_login)
public class LoginActivity extends Activity {


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
