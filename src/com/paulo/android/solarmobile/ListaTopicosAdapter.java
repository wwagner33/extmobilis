package com.paulo.android.solarmobile;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListaTopicosAdapter extends BaseAdapter {
	public Activity activity;
	public ContentValues[] data;
	public LayoutInflater inflater=null;
	
	public ListaTopicosAdapter(Activity a,ContentValues[] d) {
		activity = a;
		data=d;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
			View vi = convertView;
			if (convertView==null) {
				vi = inflater.inflate(R.layout.topicoitem, null);
				
				TextView nomeTopico = (TextView)vi.findViewById(R.id.nome_topico);
				nomeTopico.setText(data[position].getAsString("nomeTopico"));
							
				TextView horaPostagem = (TextView)vi.findViewById(R.id.hora_post);
				horaPostagem.setText(data[position].getAsString("hora"));
				
				TextView nomeAutor = (TextView)vi.findViewById(R.id.posted_by);
				nomeAutor.setText(data[position].getAsString("nomeAutor"));
				
				TextView diaPostagem = (TextView)vi.findViewById(R.id.dia_postagem);
				diaPostagem.setText(data[position].getAsString("dataCriacao"));	
				
					
					
					
				
			
				
			 

			}
		return vi;
	}
		
	

}
