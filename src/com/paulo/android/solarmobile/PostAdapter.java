package com.paulo.android.solarmobile;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PostAdapter extends BaseAdapter {
		private Activity activity;
		private ContentValues[] data;
		private static LayoutInflater inflater = null;
	
		
	public PostAdapter (Activity a,ContentValues[] d) {
			activity = a;
			data =d;
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
				vi = inflater.inflate(R.layout.postitem, null);
				TextView ForumName = (TextView)vi.findViewById(R.id.ForumName);
					ForumName.setText(data[position].getAsString("ForumName"));
				TextView ForumContent = (TextView)vi.findViewById(R.id.ForumText);
					ForumContent.setText(data[position].getAsString("ForumContent"));
				
				
				
			}
		return vi;
	}

}
