package app.heroeswear.com.heroesmakers.login.enums;

import android.support.v4.app.NotificationManagerCompat;

public enum NotificationChannelType {

    HEART_RATE_ALARTS("heart_rate_notifications", "Heart Rate Notifications", NotificationManagerCompat.IMPORTANCE_HIGH);

    private String id;
    private String name;
    private int importance;

    NotificationChannelType(String id, String name, int importance) {
        this.id = id;
        this.name = name;
        this.importance = importance;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getImportance() {
        return importance;
    }
}
