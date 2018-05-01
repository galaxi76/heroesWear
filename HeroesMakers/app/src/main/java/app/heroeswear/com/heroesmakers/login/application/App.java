package app.heroeswear.com.heroesmakers.login.application;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import app.heroeswear.com.heroesmakers.login.enums.NotificationChannelType;
import app.heroeswear.com.heroesmakers.login.utils.NotificationFactory;

public class App extends Application {
    private static App _instance;

    public static App getInstance() {
        return _instance;
    }

    public static Context getContext() {
        return _instance;
    }

    public App() {
        _instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (NotificationChannelType channelType : NotificationChannelType.values()) {
                NotificationFactory.createChannel(this, channelType);
            }
        }
    }
}
