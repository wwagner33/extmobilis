package br.ufc.virtual.solarmobilis;

import android.content.SharedPreferences;

import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref.Scope;

@SharedPref(value = Scope.UNIQUE)
public interface SolarMobilisPreferences {

	
	
	String token();

}
