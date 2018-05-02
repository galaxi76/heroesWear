package app.heroeswear.com.heroesmakers.login.fcm;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import app.heroeswear.com.heroesfb.Logger;
import app.heroeswear.com.heroesmakers.login.Activities.AreYouOkActivity;
import app.heroeswear.com.heroesmakers.login.enums.NotificationChannelType;
import app.heroeswear.com.heroesmakers.login.utils.NotificationFactory;

public class HeroesFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Logger.Companion.d("Remote Message " +remoteMessage.getData().toString());
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
        Logger.Companion.d();

        NotificationFactory.build(this, "Hi", "Are you ok?", NotificationChannelType.HEART_RATE_ALARTS);
        Intent activityIntent = new Intent(this, AreYouOkActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(activityIntent);
    }
}
