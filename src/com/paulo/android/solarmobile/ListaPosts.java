package com.paulo.android.solarmobile;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

		/*Método que pegue o resultado do HttpGet e jogue-o no ArrayList de HashMap*/

		/*	Menu OLD
		 * 	
		 * 		@Override
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
	sssss
		 * 
		 * 
		 * */

public class ListaPosts extends ListActivity implements OnClickListener, OnChronometerTickListener {
	
	private ArrayList<HashMap<String,?>> valores;
	String[] from = {"teste","teste2"};
	int[] to = {R.id.item_nome_pessoa,R.id.item_hora_envio};
	Button teste,stop,start,exit,startRecording;
	TextView contador,status;
	Chronometer stopWatch;
	ImageButton button;
	long startTime;
	PostAdapter adapter;
	private static final int REQ_CODE_1 = 1;
	private static final int DIALOG_GRAVAR_ID = 1;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);
		button = (ImageButton)findViewById(R.id.nova_mensagem);
		button.setOnClickListener(this);
		int position = 12;
		button.setTag(position);
		teste =(Button)findViewById(R.id.button_inicio);
		teste.setOnClickListener(this);
		valores = new ArrayList<HashMap<String,?>>();
	
		/*
		HashMap<String,String> valor1 = new HashMap<String,String>();
		valor1.put("teste", "ForumName1");
		valor1.put("teste2","ForumContent1");
		
		HashMap<String,String> valor2 = new HashMap<String,String>();
		valor2 .put("teste", "ForumName2");
		valor2.put("teste2","ForumContent2");
		
		valores.add(valor1);
		valores.add(valor2);
		
		ListAdapter adapter = new SimpleAdapter(this, valores, R.layout.postitem, from, to);
		*/
		ContentValues[] teste1= new ContentValues[2];
		teste1[0] = new ContentValues();
		teste1[1] = new ContentValues();
		
		
		teste1[0].put("teste", "teste");
		teste1[1].put("teste", "teste");
		adapter = new PostAdapter(this,teste1);
		
		//Button teste =(Button)adapter.getItem(0);
		
		setListAdapter(adapter);
		
	
		
		
		
		 

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
			
		Toast.makeText(this, "NO WAY", Toast.LENGTH_SHORT).show();		
		}
		@Override
		protected Dialog onCreateDialog(int id) {
			AlertDialog alertDialog;
					
						AlertDialog.Builder builder;
						
						Context mContext = getApplicationContext();
						LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
					View layout = inflater.inflate(R.layout.gravar, (ViewGroup)findViewById(R.layout.post));
				
						stop = (Button)layout.findViewById(R.id.stop_recording);
						stop.setOnClickListener(this);
						start = (Button)layout.findViewById(R.id.start_recording);
						start.setOnClickListener(this);
						exit = (Button)layout.findViewById(R.id.sair_dialog);
						exit.setOnClickListener(this);
						contador = (TextView)layout.findViewById(R.id.contador);
						status = (TextView)findViewById(R.id.status);
						stopWatch = (Chronometer)layout.findViewById(R.id.chrono);
						stopWatch.setBase(startTime);
						stopWatch.setOnChronometerTickListener(this);
						
						builder = new AlertDialog.Builder(this);
						builder.setView(layout);
						alertDialog = builder.create();
						
						
						return alertDialog;
		}


		@Override
		public void onChronometerTick(Chronometer chronometer) {
		
			
		} 		
	}
	


