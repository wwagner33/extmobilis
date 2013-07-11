package com.example.solarmobilis;

import android.app.Activity;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_login)
public class LoginActivity extends Activity {

	@ViewById(R.id.editText1)
	EditText usuario;
	@ViewById(R.id.editText2)
	EditText senha;

	@Click(R.id.button1)
	void submit(){
		if (!(usuario.getText().toString().trim().length() == 0 || senha.getText().toString().trim().length() == 0)){
			Toast.makeText(LoginActivity.this, "bem vindo: "+ usuario.getText().toString() +" senha: " + senha.getText().toString(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
