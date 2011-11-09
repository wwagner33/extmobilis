package com.paulo.android.solarmobile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CriarTopico extends Activity implements OnClickListener {
			Button submit;
			EditText titulo,corpo;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.criar_topico);
			titulo = (EditText)findViewById(R.id.criar_topico_nome);
			corpo = (EditText)findViewById(R.id.criar_topico_conteudo);
			submit = (Button)findViewById(R.id.criar_topico_submit);
			submit.setOnClickListener(this);
			
			
		}

		@Override
		public void onClick(View v) {
			 if (titulo.getText().toString().matches("") || corpo.getText().toString().matches("")) {
				 Toast.makeText(this,"Os campos não podem ser vazios", Toast.LENGTH_SHORT).show();
			 }
			 else {
				 Toast.makeText(this,"De volta pra lista",Toast.LENGTH_SHORT).show();
			 }
			
		}
}
