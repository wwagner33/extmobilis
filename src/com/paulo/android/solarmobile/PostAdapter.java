package com.paulo.android.solarmobile;

import android.app.Activity;
import android.content.ContentValues;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

public class PostAdapter extends BaseAdapter implements OnClickListener {

	Activity activity;
	ContentValues[] data;
	LayoutInflater inflater=null;
	public PostAdapter(Activity a,ContentValues[] d) {
		activity = a;
		data= d;
		inflater =LayoutInflater.from(activity);
	}
	
	static class ViewHolder {
		Button button;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
				View v = convertView;
				if (v==null) {
					ViewHolder holder;
					
				v =	inflater.inflate(R.layout.postitem,parent, false);
				holder = new ViewHolder();
				
				Button vf = (Button)v.findViewById(R.id.VoiceForum);
			//	vf.setOnClickListener(1);
				
				}
				
		return v;
	}



	@Override
	public void onClick(View v) {
		//	Toast.makeText(activity.getBaseContext(),"TESTE" , Toast.LENGTH_SHORT).show();
		//ListaPosts.teste(activity.getApplicationContext());
			
	}
	

}
