package com.mobilis.interfaces;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mobilis.controller.Config;
import com.mobilis.controller.Login;
import com.mobilis.controller.R;

public abstract class MobilisListActivity extends ListActivity {

	private SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = PreferenceManager.getDefaultSharedPreferences(this);
	}

	public SharedPreferences getPreferences() {
		return settings;
	}

	public void commit(SharedPreferences.Editor editor) {
		if (android.os.Build.VERSION.SDK_INT <= 8)
			editor.commit();
		else
			editor.commit();
	}

	public void closeDialog(Dialog dialog) {

		if (dialog != null) {
			if (dialog.isShowing())
				dialog.dismiss();
		}
	}

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

			SharedPreferences.Editor editor = settings.edit();
			editor.putString("token", null);
			commit(editor);
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
