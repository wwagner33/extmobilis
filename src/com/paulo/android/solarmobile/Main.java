package com.paulo.android.solarmobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Main extends Activity implements OnClickListener {
    	public EditText campo1,campo2;
    	public Button submit;
		public Intent intent;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login); 
        SharedPreferences prefs = this.getPreferences(MODE_PRIVATE);
        	campo1 = (EditText) findViewById(R.id.campo1);
        	campo2 = (EditText) findViewById(R.id.campo2);
        	submit = (Button) findViewById(R.id.submit);
        	submit.setOnClickListener(this);
        	
        	
        	
        	
        
        
    }


	@Override
	public void onClick(View v) {
				if (v.equals(submit)) {
					/*Aqui deve haver a validação, comparando os valores dos campos com a base de dados
					 Por enqunato o botão simplesmente passa para a tele d cursos
					 
					 os Campos evem ser armazenados para se a validação for bem sucedida
					 esta seja automática*/
				intent = new Intent(Main.this,ListaCursos.class);
				startActivity(intent);
					
				}
		
	}
}