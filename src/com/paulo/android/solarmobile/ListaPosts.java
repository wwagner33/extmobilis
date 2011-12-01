package com.paulo.android.solarmobile;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;


		/*Método que pegue o resultado do HttpGet e jogue-o no ArrayList de HashMap*/

public class ListaPosts extends ListActivity implements OnClickListener {
	
	private ArrayList<HashMap<String,?>> valores;
	String[] from = {"teste","teste2"};
	int[] to = {R.id.item_nome_pessoa,R.id.item_hora_envio};
	Button teste;
	ImageButton button;

	private static final int REQ_CODE_1 = 1;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);
		button = (ImageButton)findViewById(R.id.nova_mensagem);
		button.setOnClickListener(this);
		teste =(Button)findViewById(R.id.button_inicio);
		teste.setOnClickListener(this);
		valores = new ArrayList<HashMap<String,?>>();
		
		
		HashMap<String,String> valor1 = new HashMap<String,String>();
		valor1.put("teste", "ForumName1");
		valor1.put("teste2","ForumContent1");
		
		HashMap<String,String> valor2 = new HashMap<String,String>();
		valor2 .put("teste", "ForumName2");
		valor2.put("teste2","ForumContent2");
		
		valores.add(valor1);
		valores.add(valor2);
		
		ListAdapter adapter = new SimpleAdapter(this, valores, R.layout.postitem, from, to);
		setListAdapter(adapter);
		 

	}
	
	@Override
			public boolean onCreateOptionsMenu(Menu menu) {
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.lista_posts_menu, menu);	
				return true;
				
			}
		@Override
				public boolean onOptionsItemSelected(MenuItem item) {
					if (item.getItemId()==R.id.lista_posts_responder) {
						Intent intent = new Intent(ListaPosts.this,ResponderTopico.class);
						startActivityForResult(intent,REQ_CODE_1);
						return true;
						
					}
					return super.onOptionsItemSelected(item);
				}
	
		@Override
				protected void onActivityResult(int requestCode, int resultCode,
						Intent data) {
					if (requestCode == REQ_CODE_1) {
						if (resultCode == RESULT_OK) {
						
						}
						
					}
					super.onActivityResult(requestCode, resultCode, data);
				}

		@Override
		public void onClick(View v) {
		Toast.makeText(this, "teste", Toast.LENGTH_SHORT).show();
			
		}

}
