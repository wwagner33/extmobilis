package br.ufc.virtual.solarmobilis.util;

import android.app.Activity;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;

@EBean
public class Toaster {

	@RootContext
	Activity rootActivity;

	public void showToast(String string) {

		Toast.makeText(rootActivity, string, Toast.LENGTH_SHORT).show();

	}

}
