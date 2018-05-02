package app.heroeswear.com.heroesmakers.login.Activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import app.heroeswear.com.heroesmakers.R;
import app.heroeswear.com.heroesmakers.login.application.App;
import app.heroeswear.com.heroesmakers.login.media.MediaPlaybackService;
import app.heroeswear.com.heroesmakers.login.media.PlaybackControlsFragment;

public class AudioPlayerActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener
{
	private String 						selectedFile		= null;
	private TextView					startPlayingButton 	= null;
	private Spinner						recordingsSpinner 	= null;
	private CardView                 	cardView        	= null;
	private PlaybackControlsFragment 	controlsFragment 	= null;

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
				mediaController = new MediaControllerCompat(AudioPlayerActivity.this, token);
			}
			catch (RemoteException e)
			{
				e.printStackTrace();
				return;
			}

			MediaControllerCompat.setMediaController(AudioPlayerActivity.this, mediaController);

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
		setContentView(R.layout.activity_audio_player);

		startPlayingButton = (TextView) findViewById(R.id.player_start_btn);
		recordingsSpinner = (Spinner) findViewById(R.id.recordings_spinner);
		cardView		= (CardView) findViewById(R.id.controls_container);
		mediaBrowser	= new MediaBrowserCompat(this, new ComponentName(this, MediaPlaybackService.class), connectionCallback, null); //optional Bundle

		startPlayingButton.setOnClickListener(this);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		mediaBrowser.connect();

		//getFiles();
	}

	@Override
	protected void onStop()
	{
		super.onStop();

		if (MediaControllerCompat.getMediaController(AudioPlayerActivity.this) != null)
			MediaControllerCompat.getMediaController(AudioPlayerActivity.this).unregisterCallback(controllerCallback);

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
			String packageName 	= App.getContext().getPackageName();
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
		String packageName 	= App.getContext().getPackageName();
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

		if (v.getId() == R.id.player_start_btn)
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

	private void getFiles()
	{
		String 			path      	= Environment.getExternalStorageDirectory().toString() + "/Recordings";
		File  	 		directory 	= new File(path);
		File[] 			files     	= directory.listFiles();
		List<String> 	recordings 	= new Vector<>();

		for (int i = 0; i < files.length; i++)
			recordings.add(files[i].getName());

		recordings.add(" ");

		inflateRecordingsSpinner(recordings);
	}

	private void inflateRecordingsSpinner(List<String> recordings)
	{
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner_item, recordings);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		recordingsSpinner.setAdapter(adapter);
		recordingsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				selectedFile = (String) recordingsSpinner.getSelectedItem();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{}
		});

		//recordingsSpinner.setSelection(adapter.getPosition());
		recordingsSpinner.clearFocus();
	}

	public static synchronized boolean storeelectedName(final Context ctx, final String name)
	{
		if (ctx == null)
			throw new RuntimeException("Context cannot be null");

		return storeStringToSharedPreferences(ctx, "SELECTED_NAME", name);
	}

	private static boolean storeStringToSharedPreferences(final Context ctx, final String name, final String value)
	{
		if (ctx == null)
			throw new RuntimeException("Context cannot be null");

		if (name == null || name.isEmpty() || value == null || value.isEmpty())
			throw new RuntimeException("String cannot be null or empty");

		SharedPreferences.Editor editor = getSharedPreferencesEditor(ctx);

		editor.putString(name, value);

		return editor.commit();
	}

	private static SharedPreferences.Editor getSharedPreferencesEditor(final Context ctx)
	{
		if (ctx == null)
			throw new RuntimeException("Context cannot be null");

		SharedPreferences content = getSharedPreferencesContent(ctx);

		return content.edit();
	}

	private static SharedPreferences getSharedPreferencesContent(final Context ctx)
	{
		if (ctx == null)
			throw new RuntimeException("Context cannot be null");

		return ctx.getSharedPreferences("HEROES_PREF", Context.MODE_PRIVATE);
	}



}
