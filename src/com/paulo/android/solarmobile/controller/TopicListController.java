package com.paulo.android.solarmobile.controller;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TopicListController extends ListActivity {
			
			public String[] topics = {"Assunto1","Assunto2","Assunto3","Assunto4","Assunto5","Assunto6"};
			TopicAdapter adapter;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.topico);
			adapter = new TopicAdapter(this,topics);
			setListAdapter(adapter);
	}
		
		
		@Override
		protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this,PostList.class);
		startActivity(intent);
		}
		
	public class TopicAdapter extends BaseAdapter {
		
		String[] names;
		Activity activity;
		LayoutInflater inflater = null;
		
		public TopicAdapter(Activity a,String[] s) {
				activity = a;
				names = s;
				inflater = LayoutInflater.from(activity);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return names.length;
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
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.topicoitem,null);
			}	
			TextView text = (TextView)convertView.findViewById(R.id.topicText);
			text.setText(names[position]);
			return convertView;
		}
			
	}	
}
