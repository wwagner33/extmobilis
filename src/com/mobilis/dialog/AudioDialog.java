package com.mobilis.dialog;

import java.io.IOException;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.mobilis.audio.AudioPlayer;
import com.mobilis.controller.Constants;
import com.mobilis.controller.R;
import com.mobilis.util.DateUtils;

public class AudioDialog extends Dialog implements OnSeekBarChangeListener,
		android.view.View.OnClickListener {

	private AudioPlayer player;
	private TextView audioDuration, audioProgress, date;
	private SeekBar playerBar;
	private LinearLayout playArea;
	private LinearLayout deleteArea;
	private RelativeLayout mediaBar;
	private Handler activityHandler;
	private ImageView pause, stop;
	private Thread seekbarThread;
	private static final int teste = 5;
	private AudioHandler audioHandler;
	private Message message;
	private Bundle bundle;

	public AudioDialog(Context context, AudioPlayer player,
			Handler activityHandler) {
		super(context);

		this.activityHandler = activityHandler;
		this.player = player;
		audioHandler = new AudioHandler();
		message = Message.obtain();
		bundle = new Bundle();

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

		date = (TextView) this.findViewById(R.id.recording_date);
		date.setText(DateUtils.getCurrentDate().toString());

		audioDuration = (TextView) this.findViewById(R.id.recording_duration);

		audioProgress = (TextView) this
				.findViewById(R.id.recording_progress_teste);

		playArea = (LinearLayout) this.findViewById(R.id.listen_area);
		playArea.setOnClickListener(this);

		mediaBar = (RelativeLayout) this.findViewById(R.id.playback_area);

		deleteArea = (LinearLayout) this.findViewById(R.id.delete_area);
		deleteArea.setOnClickListener(this);

		pause = (ImageView) this.findViewById(R.id.button_pause_blue);
		pause.setOnClickListener(this);

		stop = (ImageView) this.findViewById(R.id.stop_button);
		stop.setOnClickListener(this);

		playerBar = (SeekBar) this.findViewById(R.id.player_bar);
		playerBar.setMax(duration * 1000);
		playerBar.setOnSeekBarChangeListener(this);

		if (duration == 0) {
			audioDuration.setText("00:00");
		} else {

			audioDuration.setText(formatProgress(duration));
		}
	}

	public String formatProgress(int duration) {

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

		return durationString;

	}

	public void teste(String teste) {
		audioProgress.setText("teste");
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {

		if (fromUser)
			player.getPlayerInstance().seekTo(progress);

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
			this.dismiss();

		}

		if (v.getId() == R.id.listen_area) {

			synchronized (this) {

				audioDuration.setVisibility(View.GONE);
				audioProgress.setText("00:00/"
						+ audioDuration.getText().toString());

				audioProgress.setVisibility(View.VISIBLE);

				activityHandler
						.sendEmptyMessage(Constants.DIALOG_LISTEN_AREA_CLICKED);
				playArea.setVisibility(View.GONE);
				mediaBar.setVisibility(View.VISIBLE);

				activityHandler
						.sendEmptyMessage(Constants.DIALOG_PLAYBACK_AREA_CLICKED);
				// updater = new SeekBarUpdater();
				seekbarThread = new Thread(new Runnable() {

					@Override
					public void run() {
						while (player.isPlaying() || player.isPaused()) {

							try {
								Thread.sleep(1000);
								bundle = new Bundle();
								message = new Message();
								bundle.putInt("Update", player.getProgress());
								message.setData(bundle);
								audioHandler.sendMessage(message);

							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							if (!player.isPaused() || !player.isPlaying()) {
								playerBar.setProgress(player
										.getCurrentPosition());
							}

						}
						audioHandler.sendEmptyMessage(teste);

					}
				});

				try {

					player.playOwnAudio();

					seekbarThread.start();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		if (v.getId() == R.id.button_pause_blue) {
			player.pause();
		}

		if (v.getId() == R.id.stop_button) {
			player.getPlayerInstance().seekTo(0);
			player.stop();
			playerBar.setProgress(0);
		}

	}

	public class AudioHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.getData() != null) {
				Log.w("DURATION", audioDuration.getText().toString());
				audioProgress.setText(formatProgress(msg.getData().getInt(
						"Update"))
						+ "/" + audioDuration.getText());
			}

			if (msg.what == teste) {

				audioProgress.setVisibility(View.GONE);
				audioDuration.setVisibility(View.VISIBLE);
				playArea.setVisibility(View.VISIBLE);
				mediaBar.setVisibility(View.GONE);

			}
		}
	}
}
