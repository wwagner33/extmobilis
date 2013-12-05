package br.ufc.virtual.solarmobilis.dialog;

import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
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
import br.ufc.virtual.solarmobilis.R;
import br.ufc.virtual.solarmobilis.audio.AudioPlayer;
/*import com.mobilis.controller.R;*/


public class AudioDialog extends Dialog implements OnSeekBarChangeListener,
		android.view.View.OnClickListener {

	private AudioPlayer player;
	private TextView audioDuration, audioProgress, date;
	private SeekBar playerBar;
	private LinearLayout playArea;
	private LinearLayout deleteArea;
	private RelativeLayout mediaBar;
	private ImageView pause, stop;
	private Thread seekbarThread;
	private static final int updateLayout = 5;
	private AudioHandler audioHandler;
	private Message message;
	private Bundle bundle;
	private onDeleteListener deleteListener;

	
	public AudioDialog(Activity activity, AudioPlayer player) {
		super(activity);

		deleteListener = (onDeleteListener) activity;
		this.player = player;
		audioHandler = new AudioHandler();
		message = Message.obtain();
		bundle = new Bundle();

		try {
			player.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int duration = player.getAudioDuration();
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_audio);

		date = (TextView) this.findViewById(R.id.recording_date);
		
  		Calendar calendar = Calendar.getInstance();; //temporario
		/*date.setText(DateUtils.getCurrentDate().toString()); temporario*/
		date.setText(String.valueOf(calendar.get(Calendar.YEAR))+"/"+String.valueOf(calendar.get(Calendar.MONTH))+"/"+String.valueOf(calendar.get(Calendar.MONTH)) );
		

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

	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {

	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.delete_area) {

			deleteListener.onRecordingDeleted();

			this.dismiss();
		}

		if (v.getId() == R.id.listen_area) {

			synchronized (this) {

				audioDuration.setVisibility(View.GONE);
				audioProgress.setText("00:00/"
						+ audioDuration.getText().toString());

				audioProgress.setVisibility(View.VISIBLE);

				playArea.setVisibility(View.GONE);
				mediaBar.setVisibility(View.VISIBLE);

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
						audioHandler.sendEmptyMessage(updateLayout);

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

			if (msg.what == updateLayout) {

				audioProgress.setVisibility(View.GONE);
				audioDuration.setVisibility(View.VISIBLE);
				playArea.setVisibility(View.VISIBLE);
				mediaBar.setVisibility(View.GONE);

			}
		}
	}

	public interface onDeleteListener {

		public void onRecordingDeleted();
	}
}

