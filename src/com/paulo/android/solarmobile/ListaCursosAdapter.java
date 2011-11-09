package com.paulo.android.solarmobile;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ListaCursosAdapter extends BaseAdapter {
		 Activity activity;	
	     String data[];
	     private static LayoutInflater inflater=null;
	     
	     public ListaCursosAdapter(Activity a,String[] d){
	    	 activity = a;
	    	 data = d;
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
		vi = inflater.inflate(R.layout.itemcurso, null);
		TextView tv = (TextView)vi.findViewById(R.id.item);
			tv.setText((String) data[position]);
		}
		
		return vi;
	}
	
}