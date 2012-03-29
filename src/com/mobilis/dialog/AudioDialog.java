package com.mobilis.dialog;

import java.io.IOException;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.mobilis.audio.AudioPlayer;
import com.mobilis.controller.Constants;
import com.mobilis.controller.R;

public class AudioDialog extends Dialog implements OnSeekBarChangeListener,
		android.view.View.OnClickListener {

	private Context context;
	private AudioPlayer player;
	private TextView audioDuration;
	private SeekBar playerBar;
	private LinearLayout playArea;
	private LinearLayout deleteArea;
	private RelativeLayout mediaBar;
	private Handler activityHandler;
	private SeekBarUpdater updater;

	// PlayAudio auioPlayer;

	public AudioDialog(Context context, AudioPlayer player,
			Handler activityHandler) {
		super(context);

		this.activityHandler = activityHandler;
		this.context = context;
		this.player = player;

		try {
			player.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int duration = player.getAudioDuration();
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_audio);

		audioDuration = (TextView) this.findViewById(R.id.recording_duration);

		playArea = (LinearLayout) this.findViewById(R.id.listen_area);
		playArea.setOnClickListener(this);

		mediaBar = (RelativeLayout) this.findViewById(R.id.playback_area);

		deleteArea = (LinearLayout) this.findViewById(R.id.delete_area);
		deleteArea.setOnClickListener(this);

		playerBar = (SeekBar) this.findViewById(R.id.player_bar);
		playerBar.setMax(duration * 1000);
		playerBar.setOnSeekBarChangeListener(this);

		if (duration == 0) {
			audioDuration.setText("00:00");
		} else {
			int minutes = duration / 60;
			int seconds = duration % 60;

			String secondsString, minutesString;

			if (minutes < 10)
				minutesString = "0" + String.valueOf(minutes);
			else
				minutesString = String.valueOf(minutes);

			if (seconds < 10)
				secondsString = "0" + String.valueOf(seconds);
			else
				secondsString = String.valueOf(seconds);

			String durationString = minutesString + ":" + secondsString;

			audioDuration.setText(durationString);
		}

	}

	@Override
	public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {

		// pl.seekTo(progress);

	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.delete_area) {

			activityHandler
					.sendEmptyMessage(Constants.DIALOG_DELETE_AREA_CLICKED);
			// letting the activity handle

		}

		if (v.getId() == R.id.listen_area) {

			synchronized (this) {

				activityHandler
						.sendEmptyMessage(Constants.DIALOG_LISTEN_AREA_CLICKED);
				playArea.setVisibility(View.GONE);
				mediaBar.setVisibility(View.VISIBLE);

				activityHandler
						.sendEmptyMessage(Constants.DIALOG_PLAYBACK_AREA_CLICKED);
				updater = new SeekBarUpdater();
				try {
					// player.prepare();
					player.playOwnAudio();
					updater.execute();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}
	}

	public class SeekBarUpdater extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			while (player.isPlaying()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				playerBar.setProgress(player.getCurrentPosition());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			playArea.setVisibility(View.VISIBLE);
			mediaBar.setVisibility(View.GONE);
			super.onPostExecute(result);
		}

	}
}
