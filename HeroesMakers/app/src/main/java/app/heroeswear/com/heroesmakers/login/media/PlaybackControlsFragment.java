package app.heroeswear.com.heroesmakers.login.media;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import app.heroeswear.com.heroesmakers.R;

public class PlaybackControlsFragment extends Fragment
{
	private int 							interval 					= 1000;
	private Handler 						handler 					= new Handler();
	private boolean							isRunning					= false;

	//private TextView 						title						= null;
	//private TextView 						subTitle					= null;
	private TextView 						tvElapsedTime 				= null;
	private TextView 						tvEndTime 					= null;
	private SeekBar 						seekBar 					= null;
	private ImageView 						rewind 						= null;
	private ImageView						playPause 					= null;
	private ImageView 						fastForward 				= null;

	private long							duration					= -1;
	private long							elapsed						= -1;
	private int								portion						= -1; //in seconds

	private Runnable 						runnable 					= new Runnable()
	{
		@Override
		public void run()
		{
			try
			{
				if (elapsed <= duration)
					tvElapsedTime.setText(millisecondsToFormattedTime(elapsed));

				long 	seconds 			= TimeUnit.MILLISECONDS.toSeconds(elapsed);
				int 	elapsedPortions 	= (int) (seconds / portion);
				seekBar.setProgress(elapsedPortions);

				elapsed += interval;
			}
			finally
			{
				handler.postDelayed(runnable, interval);
			}
		}
	};

	private MediaControllerCompat.Callback 	controllerCallback = new MediaControllerCompat.Callback()
	{
		@Override
		public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state)
		{
			playbackStateChanged(state);
		}

		@Override
		public void onMetadataChanged(MediaMetadataCompat metadata)
		{
			metadataChanged(metadata);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView 	= inflater.inflate(R.layout.fragment_playback_controls_layout, container, false);

		tvElapsedTime 	= (TextView) rootView.findViewById(R.id.fragment_elapsed_time);
		tvEndTime 		= (TextView) rootView.findViewById(R.id.fragment_ending_time);
		seekBar 		= (SeekBar) rootView.findViewById(R.id.fragment_seek_bar);
		rewind 			= (ImageView) rootView.findViewById(R.id.fragment_controller_rewind);
		playPause 		= (ImageView) rootView.findViewById(R.id.fragment_controller_play_pause);
		fastForward 	= (ImageView) rootView.findViewById(R.id.fragment_controller_fast_forward);

		seekBar.setOnSeekBarChangeListener((SeekBar.OnSeekBarChangeListener) getActivity());
		rewind.setOnClickListener((View.OnClickListener) getActivity());
		playPause.setOnClickListener((View.OnClickListener) getActivity());
		fastForward.setOnClickListener((View.OnClickListener) getActivity());
		rootView.setOnClickListener((View.OnClickListener) getActivity());

		return rootView;
	}

	@Override
	public void onStart()
	{
		super.onStart();

		MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(getActivity());

		if (mediaController != null)
		{
			metadataChanged(mediaController.getMetadata());
			playbackStateChanged(mediaController.getPlaybackState());
			mediaController.registerCallback(controllerCallback);
		}
	}

	@Override
	public void onStop()
	{
		super.onStop();

		MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(getActivity());

		if (mediaController != null)
			mediaController.unregisterCallback(controllerCallback);
	}

	private void metadataChanged(MediaMetadataCompat metadata)
	{
		if (metadata == null)
			return;

		duration 	= metadata.getLong("DURATION");
		elapsed 	= metadata.getLong("ELAPSED");

		if (duration > 0)
		{
			tvEndTime.setText(millisecondsToFormattedTime(duration));
			portion = (int)((duration / 100) / 1000);
		}

		if (elapsed > 0)
			tvElapsedTime.setText(millisecondsToFormattedTime(elapsed));
	}

	private void playbackStateChanged(PlaybackStateCompat state)
	{
		if (state == null || getActivity() == null)
			return;

		switch (state.getState())
		{
			case PlaybackStateCompat.STATE_PAUSED:
			case PlaybackStateCompat.STATE_STOPPED:
				playPause.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_media_play));
				stopRepeatingTask();
				break;
			case PlaybackStateCompat.STATE_PLAYING:
				playPause.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_media_pause));

				if (isRunning == false)
					startRepeatingTask();
				break;
			case PlaybackStateCompat.STATE_ERROR:
				Log.e("media error", state.getErrorMessage().toString());
				stopRepeatingTask();
				return;
			default:
				break;
		}
	}

	private String millisecondsToFormattedTime(long millis)
	{
		long hours 		= TimeUnit.MILLISECONDS.toHours(millis);
		long minutes 	= TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1);
		long seconds 	= TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1);

		return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
	}

	private void startRepeatingTask()
	{
		isRunning = true;
		runnable.run();
	}

	private void stopRepeatingTask()
	{
		if (handler == null)
			return;

		isRunning = false;
		handler.removeCallbacks(runnable);
	}
}
