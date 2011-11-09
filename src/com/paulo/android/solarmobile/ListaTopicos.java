package com.paulo.android.solarmobile;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ListaTopicos extends Activity implements OnItemClickListener{
		
	
			/*	Valores para adicionar e o id das Vires correspondentes
			 * 
	 		* 	valores --> ListaTopicos (nomeTopico - R.id.nome_topico
	 		* 									,nomeAutor - R.id.posted_by
	 		* 											,diaCriacao - R.id.dia_postagem
	 		* 													,horaCriacao - R.id.hora_post)
	 		* 
	 		* 
	 		* 	TODO : Criar menu para adicionar nova Postagem
	 		* 
	 		* 
	 		* */	
	
	
			private ListView lv;
			private ListaTopicosAdapter adapter;
			private ContentValues[] valores;
			
		//	private List<? extends Map<String,String>> list;
			
			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				
				valores = new ContentValues[20];
					for  (int i=0;i<valores.length;i++) {
						valores[i] = new ContentValues();
						valores[i].put("nomeTopico", " Topico "+i);
						valores[i].put("nomeAutor", " fulano ");
						valores[i].put("dataCriacao", " 20/10 ");
						valores[i].put("hora", " 22:00 ");
						
					}
					
				setContentView(R.layout.topico);
				lv = (ListView) findViewById(R.id.lista_topico);
				lv.setOnItemClickListener(this);
				adapter = new ListaTopicosAdapter(this,valores);
				lv.setAdapter(adapter);
					
			}
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			//	Toast.makeText(ListaTopicos.this, "Funcionando", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(ListaTopicos.this,ListaPosts.class);
				startActivity(intent);
				
	}
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.lista_topicos_menu, menu);
				return true;
				
		//return super.onCreateOptionsMenu(menu);
		}	
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			if (item.getItemId()==R.id.menu_lista_topicos) {
				//Toast.makeText(this, "Menu Funcionando", Toast.LENGTH_SHORT).show(); // Ir para Criar um novo Tópico
				Intent intent = new Intent(ListaTopicos.this,CriarTopico.class);
				startActivity(intent);
			}
			return true;
		//return super.onOptionsItemSelected(item);
		}
			
}
