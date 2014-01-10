package br.ufc.virtual.solarmobilis.util;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import android.app.Activity;
import android.widget.Toast;

@EBean
public class Toaster {

	@RootContext
	Activity rootActivity;

	public void showToast(String string) {

		Toast.makeText(rootActivity, string, Toast.LENGTH_SHORT).show();

	}

}
