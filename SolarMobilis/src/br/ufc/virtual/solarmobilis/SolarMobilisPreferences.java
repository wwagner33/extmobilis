package br.ufc.virtual.solarmobilis;

import org.androidannotations.annotations.sharedpreferences.SharedPref;
import org.androidannotations.annotations.sharedpreferences.SharedPref.Scope;

@SharedPref(value = Scope.UNIQUE)
public interface SolarMobilisPreferences {

	String authToken();

	int curriculumUnitSelected();

	int groupSelected();
	
	boolean remainLoggedIn();
	
	int userId();
	
}
