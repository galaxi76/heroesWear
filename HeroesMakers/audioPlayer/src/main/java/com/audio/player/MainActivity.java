package com.audio.player;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

import com.audio.player.classes.Constants;
import com.audio.player.media.MediaPlaybackService;
import com.audio.player.media.PlaybackControlsFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener
{
	private CardView                 cardView         = null;
	private PlaybackControlsFragment controlsFragment = null;

	/* Media */
	private MediaBrowserCompat 						mediaBrowser 		= null;
	private MediaBrowserCompat.ConnectionCallback 	connectionCallback	= new MediaBrowserCompat.ConnectionCallback()
	{
		@Override
		public void onConnected()
		{
			MediaSessionCompat.Token token           = mediaBrowser.getSessionToken();
			MediaControllerCompat    mediaController = null;

			try
			{
				mediaController = new MediaControllerCompat(MainActivity.this, token);
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
				return;
			}

			MediaControllerCompat.setMediaController(MainActivity.this, mediaController);

			// Display the initial state
			//MediaMetadataCompat	metadata 	= mediaController.getMetadata();
			//PlaybackStateCompat 	state 		= mediaController.getPlaybackState();

			mediaController.registerCallback(controllerCallback);
		}

		@Override
		public void onConnectionSuspended()
		{
			// The Service has crashed. Disable transport controls until it automatically reconnects
		}

		@Override
		public void onConnectionFailed()
		{
			// The Service has refused our connection
		}
	};
	private MediaControllerCompat.Callback        controllerCallback             = new MediaControllerCompat.Callback()
	{
		@Override
		public void onMetadataChanged(MediaMetadataCompat metadata)
		{}

		@Override
		public void onPlaybackStateChanged(PlaybackStateCompat state)
		{}
	};
	private BroadcastReceiver                     notificationSwipedAwayReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction()))
			{
				KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

				if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_STOP)
				{
					hideFragment();

					try
					{
						unregisterReceiver(this);
					}
					catch(Exception e)
					{}
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		cardView			= (CardView) findViewById(R.id.controls_container);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(this);

		mediaBrowser = new MediaBrowserCompat(this, new ComponentName(this, MediaPlaybackService.class), connectionCallback, null); //optional Bundle
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		mediaBrowser.connect();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop()
	{
		super.onStop();

		if (MediaControllerCompat.getMediaController(MainActivity.this) != null)
			MediaControllerCompat.getMediaController(MainActivity.this).unregisterCallback(controllerCallback);

		mediaBrowser.disconnect();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		try
		{
			unregisterReceiver(notificationSwipedAwayReceiver);
		}
		catch(Exception e)
		{}
	}

	/*public void playButtonClicked(Entry entry)
	{
		if (entry.enclosure != null && entry.enclosure.isEmpty() == false)
		{
			showFragment();

			registerReceiver(notificationSwipedAwayReceiver, new IntentFilter(Intent.ACTION_MEDIA_BUTTON));

			Intent intent 		= new Intent(this, MediaPlaybackService.class);
			String packageName 	= MyApplication.getContext().getPackageName();
			intent.putExtra(packageName + ".AUDIO_TITLE", entry.title);
			intent.putExtra(packageName + ".AUDIO_URL", entry.enclosure);
			startService(intent);
		}
	}*/

	public void playButtonClicked()
	{
		showFragment();

		registerReceiver(notificationSwipedAwayReceiver, new IntentFilter(Intent.ACTION_MEDIA_BUTTON));

		Intent intent      = new Intent(this, MediaPlaybackService.class);
		String packageName = Constants.PACKAGE_NAME;
		intent.putExtra(packageName + ".AUDIO_TITLE", "Audio title");
		intent.putExtra(packageName + ".AUDIO_URL", "Audio url");
		startService(intent);
	}

	private void showFragment()
	{
		cardView.setVisibility(View.VISIBLE);

		controlsFragment = new PlaybackControlsFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.fragment_playback_controls, controlsFragment, "fragment_playback_controls");
		transaction.commitAllowingStateLoss();
	}

	private void hideFragment()
	{
		cardView.setVisibility(View.GONE);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.remove(controlsFragment);
		transaction.commitAllowingStateLoss();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar)
	{}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar)
	{
		MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(this);

		if (mediaController == null)
			return;

		mediaController.getTransportControls().seekTo(seekBar.getProgress());
	}

	@Override
	public void onClick(View v)
	{
		MediaControllerCompat mediaController = null;

		if (v.getId() == R.id.fab)
		{
			playButtonClicked();
		}
		else if (v.getId() == R.id.fragment_controller_rewind)
		{
			mediaController = MediaControllerCompat.getMediaController(this);

			if (mediaController == null)
				return;

			mediaController.getTransportControls().rewind();
		}
		else if (v.getId() == R.id.fragment_controller_play_pause)
		{
			mediaController = MediaControllerCompat.getMediaController(this);

			if (mediaController == null)
				return;

			PlaybackStateCompat 	playbackState 	= mediaController.getPlaybackState();
			int 					currentState	= PlaybackStateCompat.STATE_NONE;

			if (playbackState != null)
				currentState = playbackState.getState();

			if (currentState == PlaybackStateCompat.STATE_PLAYING || currentState == PlaybackStateCompat.STATE_BUFFERING || currentState == PlaybackStateCompat.STATE_CONNECTING)
			{	mediaController.getTransportControls().pause();	}
			else //if (currentState == PlaybackStateCompat.STATE_PAUSED || currentState == PlaybackStateCompat.STATE_STOPPED || currentState == PlaybackStateCompat.STATE_NONE)
				mediaController.getTransportControls().play();
		}
		else if (v.getId() == R.id.fragment_controller_fast_forward)
		{
			mediaController = MediaControllerCompat.getMediaController(this);

			if (mediaController == null)
				return;

			mediaController.getTransportControls().fastForward();
		}
	}
}
