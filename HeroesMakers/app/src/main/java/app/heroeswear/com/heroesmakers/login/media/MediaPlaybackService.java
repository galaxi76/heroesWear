package app.heroeswear.com.heroesmakers.login.media;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.app.NotificationCompat.MediaStyle;
import android.support.v4.media.MediaBrowserCompat.MediaItem;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import app.heroeswear.com.heroesmakers.R;
import app.heroeswear.com.heroesmakers.login.application.App;

public class MediaPlaybackService extends MediaBrowserServiceCompat
{
	private static final String 					CHANNEL_ID 				= "media_playback_channel";
	private static final int 						NOTIFICATION_ID 		= 101;
	private static final int 						MILLISECONDS_TO_JUMP 	= 30000;

	private String 									audioTitle				= null;
	private String 									audioSubtitle			= "subtitle";
	private String 									audioUrl				= null;
	private Bitmap									albumArt				= null;

	private MediaSessionCompat 						mediaSession 			= null;
	private MediaMetadataCompat.Builder 			metadataBuilder			= null;
	private PlaybackStateCompat.Builder 			playbackStateBuilder	= null;
	private MediaPlayer 							player					= null;
	private AudioManager.OnAudioFocusChangeListener audioFocusListener 		= new AudioManager.OnAudioFocusChangeListener()
	{
		@Override
		public void onAudioFocusChange(int focusChange)
		{
			if (focusChange == AudioManager.AUDIOFOCUS_LOSS) //Permanent loss of audio focus
			{
				pause();

				// Wait 30 seconds before stopping
				Handler handler = new Handler();
				handler.postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						stop();
					}
				}, TimeUnit.SECONDS.toMillis(30));
			}
			else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)
			{
				pause();
			}
			else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK)
			{
				//Lost focus for a short time, but it's ok to keep playing at an attenuated level
				if (player.isPlaying())
					player.setVolume(0.1f, 0.1f);
			}
			else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) //regain audio focus
			{
				// Raise volume to normal, restart playback if necessary
				player.setVolume(1.0f, 1.0f);
			}
		}
	};
	private BroadcastReceiver 						becomingNoisyReceiver	= new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction()))
			{
				pause();
			}
		}
	};

	@Override
	public void onCreate()
	{
		super.onCreate();

		mediaSession = new MediaSessionCompat(this, "media session");
		mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
		mediaSession.setCallback(new MediaSessionCallback());
		setSessionToken(mediaSession.getSessionToken());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		KeyEvent keyEvent = MediaButtonReceiver.handleIntent(mediaSession, intent);

		if (keyEvent != null)
		{
			return super.onStartCommand(intent, flags, startId);
		}

		String packageName 	= App.getContext().getPackageName();
		audioTitle 			= intent.getStringExtra(packageName + ".AUDIO_TITLE");
		audioUrl 			= intent.getStringExtra(packageName + ".AUDIO_URL");
		albumArt 			= BitmapFactory.decodeResource(getResources(), R.drawable.pigeon_icon);

		player = new MediaPlayer();
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
		{
			@Override
			public void onCompletion(MediaPlayer mp)
			{}
		});
		player.setOnErrorListener(new MediaPlayer.OnErrorListener()
		{
			@Override
			public boolean onError(MediaPlayer mediaPlayer, int what, int extra)
			{
				switch (what)
				{
					case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
						Log.e("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
						break;
					case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
						Log.e("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
						break;
					case MediaPlayer.MEDIA_ERROR_UNKNOWN:
						Log.e("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
						break;
					default:
						Log.e("MediaPlayer Error", "MEDIA ERROR " + extra);
						break;
				}
				return false;
			}
		});
		player.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
		{
			@Override
			public void onPrepared(MediaPlayer mediaPlayer)
			{
				metadataBuilder.putLong("DURATION", player.getDuration());
				mediaSession.setMetadata(metadataBuilder.build());

				play();
			}
		});
		//player.setOnBufferingUpdateListener(this);
		//player.setOnSeekCompleteListener(this);
		//player.setOnInfoListener(this);
		player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
		player.reset(); //Reset so that the MediaPlayer is not pointing to another data source
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);

		try
		{
			//player.setDataSource(audioUrl);
			AssetFileDescriptor afd = getAssets().openFd("myFile.mp3"); //loadSelectedName(this));
			player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			stopSelf();

			return super.onStartCommand(intent, flags, startId);
		}

		metadataBuilder 		= new MediaMetadataCompat.Builder();
		playbackStateBuilder 	= new PlaybackStateCompat.Builder();

		metadataBuilder
				.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART	, albumArt)
				.putString(MediaMetadataCompat.METADATA_KEY_TITLE		, audioTitle)
				.putString(MediaMetadataCompat.METADATA_KEY_ARTIST		, audioSubtitle);

		playbackStateBuilder
				.setActions(PlaybackStateCompat.ACTION_REWIND | PlaybackStateCompat.ACTION_FAST_FORWARD | PlaybackStateCompat.ACTION_PLAY
					| PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_STOP)
				.setState(PlaybackStateCompat.STATE_PLAYING, 0, 1, SystemClock.elapsedRealtime());

		mediaSession.setMetadata(metadataBuilder.build());
		mediaSession.setPlaybackState(playbackStateBuilder.build());
		player.prepareAsync();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public BrowserRoot onGetRoot(String clientPackageName, int clientUid, Bundle rootHints)
	{
		return new BrowserRoot("", null);
	}

	@Override
	public void onLoadChildren(final String parentMediaId, final Result<List<MediaItem>> result)
	{
		result.sendResult(null);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		if (mediaSession != null)
			mediaSession.release();

		if (albumArt != null)
			albumArt.recycle();

		if (player != null)
			player.release();

		AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		audioManager.abandonAudioFocus(audioFocusListener);

		try
		{
			unregisterReceiver(becomingNoisyReceiver);
		}
		catch(Exception e)
		{}
	}

	private Notification buildNotification(int action, boolean showElapsedTime)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			createChannel();

		int 	actionIcon 		= android.R.drawable.ic_media_pause;
		String 	actionTitle 	= "pause";
		int 	keyEvent		= -1;

		if (action == 0)
		{
			actionIcon 		= android.R.drawable.ic_media_pause;
			actionTitle 	= "pause";
			keyEvent		= KeyEvent.KEYCODE_MEDIA_PAUSE;
		}
		else if (action == 1)
		{
			actionIcon 		= android.R.drawable.ic_media_play;
			actionTitle 	= "play";
			keyEvent		= KeyEvent.KEYCODE_MEDIA_PLAY;
		}

		MediaControllerCompat 	controller 		= mediaSession.getController();
		MediaMetadataCompat 	mediaMetadata 	= controller.getMetadata();
		MediaDescriptionCompat 	description 	= mediaMetadata.getDescription();

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
		builder.setWhen(System.currentTimeMillis() - player.getCurrentPosition())
				.setShowWhen(showElapsedTime)
				.setUsesChronometer(showElapsedTime)
				.setLargeIcon(description.getIconBitmap())
				.setContentTitle(description.getTitle())
				.setContentText(description.getSubtitle())
				//.setContentInfo	(audioTitle)
				//.setSubText(description.getDescription())
				.setContentIntent(controller.getSessionActivity()) //Enables launching player by clicking notification
				.setDeleteIntent(getActionIntent(this, KeyEvent.KEYCODE_MEDIA_STOP)) // Stop service when notification is swiped away
				.setVisibility(NotificationCompat.VISIBILITY_PUBLIC) //Make transport controls visible on lockscreen
				.setSmallIcon(android.R.drawable.stat_sys_headset) //R.drawable.pigeon)
				.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark)) //R.color.colorAccent
				.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_rew, "previous"
						, getActionIntent(this, KeyEvent.KEYCODE_MEDIA_REWIND)))
				.addAction(new NotificationCompat.Action(actionIcon, actionTitle
						, getActionIntent(this, keyEvent)))
				.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_ff, "next"
						, getActionIntent(this, KeyEvent.KEYCODE_MEDIA_FAST_FORWARD)))
				.setOnlyAlertOnce(true)
				.setStyle(new MediaStyle()
						.setMediaSession(mediaSession.getSessionToken())
						.setShowCancelButton(true)
						.setCancelButtonIntent(
								MediaButtonReceiver.buildMediaButtonPendingIntent(
										this, PlaybackStateCompat.ACTION_STOP)));

		return builder.build();
	}

	private PendingIntent getActionIntent(Context context, int mediaKeyEvent)
	{
		Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
		intent.setPackage(context.getPackageName());
		intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, mediaKeyEvent));

		return PendingIntent.getBroadcast(context, mediaKeyEvent, intent, 0);
	}

	private class MediaSessionCallback extends MediaSessionCompat.Callback
	{
		public void onPlay()
		{
			super.onPlay();
			play();
		}

		@Override
		public void onPause()
		{
			super.onPause();
			pause();
		}

		@Override
		public void onRewind()
		{
			super.onRewind();
			rewind();
		}

		@Override
		public void onFastForward()
		{
			super.onFastForward();
			fastForward();
		}

		@Override
		public void onStop()
		{
			super.onStop();
			stop();
		}

		@Override
		public void onSeekTo(long pos)
		{
			super.onSeekTo(pos);
			seekTo(pos);
		}
	}

	private void play()
	{
		AudioManager 	audioManager 	= (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		int 			result 			= audioManager.requestAudioFocus(audioFocusListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
		{
			playbackStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, player.getCurrentPosition(), 1, SystemClock.elapsedRealtime());
			mediaSession.setPlaybackState(playbackStateBuilder.build());
			metadataBuilder.putLong("ELAPSED", player.getCurrentPosition());
			mediaSession.setMetadata(metadataBuilder.build());
			mediaSession.setActive(true);
			registerReceiver(becomingNoisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
			player.start();
			startForeground(NOTIFICATION_ID, buildNotification(0, true));
		}
	}

	private void pause()
	{
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFICATION_ID, buildNotification(1, false));

		playbackStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, player.getCurrentPosition(), 0, SystemClock.elapsedRealtime());
		mediaSession.setPlaybackState(playbackStateBuilder.build());

		try
		{
			unregisterReceiver(becomingNoisyReceiver);
		}
		catch(Exception e)
		{}

		player.pause();
		stopForeground(false);
	}

	private void stop()
	{
		AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		audioManager.abandonAudioFocus(audioFocusListener);

		playbackStateBuilder.setState(PlaybackStateCompat.STATE_STOPPED, player.getCurrentPosition(), 0, SystemClock.elapsedRealtime());
		mediaSession.setPlaybackState(playbackStateBuilder.build());
		mediaSession.setActive(false);

		try
		{
			unregisterReceiver(becomingNoisyReceiver);
		}
		catch(Exception e)
		{}

		player.stop();
		stopForeground(true);
		stopSelf();
	}

	private void rewind()
	{
		int resumePosition = player.getCurrentPosition() - MILLISECONDS_TO_JUMP;

		if (resumePosition < 0)
			resumePosition = 0;

		player.seekTo(resumePosition);

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		if (player.isPlaying())
		{	notificationManager.notify(NOTIFICATION_ID, buildNotification(0, true));	}
		else
			notificationManager.notify(NOTIFICATION_ID, buildNotification(1, false));

		metadataBuilder.putLong("ELAPSED", resumePosition);
		mediaSession.setMetadata(metadataBuilder.build());
	}

	private void fastForward()
	{
		int resumePosition 	= player.getCurrentPosition();
		int duration 		= player.getDuration();

		if (duration != -1)
		{
			int difference = duration - resumePosition;

			if (difference > MILLISECONDS_TO_JUMP)
				resumePosition += MILLISECONDS_TO_JUMP;

			if (resumePosition > duration)
				resumePosition = duration - 1; //not sure whether max of duration is duration or (duration - 1)

			player.seekTo(resumePosition);

			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

			if (player.isPlaying())
			{	notificationManager.notify(NOTIFICATION_ID, buildNotification(0, true));	}
			else
				notificationManager.notify(NOTIFICATION_ID, buildNotification(1, false));

			metadataBuilder.putLong("ELAPSED", resumePosition);
			mediaSession.setMetadata(metadataBuilder.build());
		}
	}

	private void seekTo(long pos)
	{
		int duration = player.getDuration();

		if (duration != -1)
		{
			int onePercentOfDuration 	= duration / 100; // in millis
			int resumePosition 			= (int)(onePercentOfDuration * pos);

			player.seekTo(resumePosition);

			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

			if (player.isPlaying())
			{	notificationManager.notify(NOTIFICATION_ID, buildNotification(0, true));	}
			else
				notificationManager.notify(NOTIFICATION_ID, buildNotification(1, false));

			metadataBuilder.putLong("ELAPSED", resumePosition);
			mediaSession.setMetadata(metadataBuilder.build());
		}
	}

	/*private void removeNotification()
	{
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);
	}*/

	@RequiresApi(Build.VERSION_CODES.O)
	private void createChannel()
	{
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		CharSequence 		name 				= "Media playback";
		String              description 		= "Media playback controls";
		int                 importance  		= NotificationManager.IMPORTANCE_LOW;
		NotificationChannel channel    			= new NotificationChannel(CHANNEL_ID, name, importance);

		channel.setDescription(description);
		channel.setShowBadge(false);
		channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
		notificationManager.createNotificationChannel(channel);
	}

	public static synchronized String loadSelectedName(final Context ctx)
	{
		if (ctx == null)
			throw new RuntimeException("Context cannot be null");

		return loadStringFromSharedPreferences(ctx, "SELECTED_NAME");
	}

	private static String loadStringFromSharedPreferences(final Context ctx, final String name)
	{
		if (ctx == null)
			throw new RuntimeException("Context cannot be null");

		if (name == null || name.isEmpty())
			throw new RuntimeException("String cannot be null or empty");

		return getSharedPreferencesContent(ctx).getString(name, null);
	}

	private static SharedPreferences getSharedPreferencesContent(final Context ctx)
	{
		if (ctx == null)
			throw new RuntimeException("Context cannot be null");

		return ctx.getSharedPreferences("HEROES_PREF", Context.MODE_PRIVATE);
	}

}