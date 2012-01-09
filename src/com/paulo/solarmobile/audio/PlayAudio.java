package com.paulo.solarmobile.audio;

import java.io.File;

import android.media.MediaPlayer;
import android.os.Environment;

public abstract class PlayAudio {
		MediaPlayer player;
		File directoryPath;
		
		public PlayAudio(String fileName) {
			File path = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/Mobilis/Recordings/");
			//	player.setDataSource(path)
			
		}
		
		public void play() {};
		
		public void stop() {}
		
		public void pause() {}
		
	public abstract void updatePlayingDialog();
}
