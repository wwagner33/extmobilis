package com.paulo.android.solarmobile.controller;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TopicListController extends ListActivity {

	public String[] topics = { "Assunto1", "Assunto2", "Assunto3", "Assunto4",
			"Assunto5", "Assunto6" };;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topico);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.topicoitem, topics);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this, PostList.class);
		startActivity(intent);
	}

}
