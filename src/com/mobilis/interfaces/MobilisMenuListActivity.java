package com.mobilis.interfaces;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mobilis.controller.Config;
import com.mobilis.controller.Login;
import com.mobilis.controller.R;
import com.mobilis.util.MobilisPreferences;

public abstract class MobilisMenuListActivity extends FragmentActivity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {
			menuRefreshItemSelected();
		}
		if (item.getItemId() == R.id.menu_logout) {

			MobilisPreferences preferences = MobilisPreferences
					.getInstance(this);
			preferences.setToken(null);
			Intent intent = new Intent(this, Login.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

		}
		if (item.getItemId() == R.id.menu_config) {
			Intent intent = new Intent(this, Config.class);
			startActivity(intent);
		}
		return true;
	}

	public abstract void menuRefreshItemSelected();

}
