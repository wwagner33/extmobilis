//package com.mobilis.ws;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.impl.client.DefaultHttpClient;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.os.AsyncTask;
//import android.os.Handler;
//
//public class ImageLoader {
//
//	private ImageConnection imageConnection;
//	private static final int BLOCK_SIZE = 5;
//	private Handler handler;
//	private Context context;
//	private int blocks = 0;
//
//	private DefaultHttpClient client;
//	private HttpResponse response;
//
//	private public ImageLoader(Context context, Handler handler) {
//		this.context = context;
//		this.handler = handler;
//	}
//
//	public void loadImages(String images[]) {
//		blocks = images.length / BLOCK_SIZE;
//
//	}
//
//	public Bitmap getImage(String userId) {
//		return null;
//	}
//
//	public class ImageConnection extends AsyncTask<Void, Void, Integer> {
//
//		@Override
//		protected Integer doInBackground(Void... arg0) {
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Integer result) {
//			super.onPostExecute(result);
//		}
//	}
// }
