package common;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.concurrent.locks.ReentrantLock;

import app.heroeswear.com.heroesmakers.login.application.App;


public class AppSettingsProfile {

    private static final String TAG = "AppSettingsProfile";

    private static final String SETTINGS_FILE_NAME = "app_pref.dat";

    private static final String IMMUTABLE_SETTINGS_FILE_NAME = "immutable_app_pref.dat";

    private static final String PREF_TIME_ON_EXIT = "PREF_TIME_ON_EXIT";
    private static final int PUSH = 5;

    private static AppSettingsProfile _instance = new AppSettingsProfile();

    private static final ReentrantLock settingsLock = new ReentrantLock();



    public static AppSettingsProfile getInstance() {
        return _instance;
    }

    public SharedPreferences getPrefsInstance() {
        return App.getContext().getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
    }

    public SharedPreferences getImmutablePrefsInstance() {
        return App.getContext().getSharedPreferences(IMMUTABLE_SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
    }

    public void clearAll() {
        SharedPreferences prefs = getPrefsInstance();
        prefs.edit().clear().apply();
        App.getContext().deleteFile(SETTINGS_FILE_NAME);
        _instance = new AppSettingsProfile();
    }

    public boolean isSignedIn() {
        return getPrefsInstance().getBoolean("FIRST_LAUNCH", false);
    }

    public void setSignedIn(boolean value) {
        SharedPreferences.Editor editor = getPrefsInstance().edit();
        editor.putBoolean("FIRST_LAUNCH", value);
        editor.apply();
    }

    public String getUserID() {
        return getPrefsInstance().getString("USER_ID", "");
    }

    public void setUserID(String userId) {
        SharedPreferences.Editor editor = getPrefsInstance().edit();
        editor.putString("USER_ID", userId);
        editor.apply();
    }

    public String getPackageName() {
        return App.getContext().getPackageName();
    }


//    public void saveSettings() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                settingsLock.lock();
//                try {
//                    Log.d(TAG, "Save settings");
//                    SharedPreferences.Editor editor = getPrefsInstance().edit();
//                    String serializedSettings = objectToString(Settings.getInstance());
//                    editor.putString("SETTINGS_GSON", serializedSettings);
//                    editor.apply();
//                } finally {
//                    settingsLock.unlock();
//                }
//            }
//        }).start();
//    }

//    public static synchronized String objectToString(Object object) {
//
//        Gson gson = new GsonBuilder()
//                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss Z")
//                .registerTypeAdapter(Settings.class, new Settings()).serializeNulls()
//                .create();
//        try {
//            return gson.toJson(object);
//        }catch(Exception e){
//            Crashlytics.logException(e);
//            e.printStackTrace();
//        }
//        return null;
//    }
//    public void restoreSettings(Settings settingsRestorTo) {
//        String raw_settings = getPrefsInstance().getString("SETTINGS_GSON", "");
//        if (!TextUtils.isEmpty(raw_settings)) {
//            long start_time = new Date().getTime();
//            Settings deserializedSettings = (Settings) ObjectSerializer.stringToObject(raw_settings,
//                    Settings.class);
//            if (deserializedSettings != null) {
//                settingsRestorTo.merge(deserializedSettings);
//                settingsRestorTo.save();
//            }
//
//            Logger.d("SettingsRestore", "Restore settings taked " + (new Date().getTime() - start_time) / 1000 + " sec");
//        }


        /**
         * User notification preferences
         * OTHER = 0;
         * SMS = 1;
         * EMAIL = 2;
         * PUSH = 4;
         * @return
         */

    public void changeUserNotificationPreferencesToken(int np) {
        SharedPreferences.Editor editor = getPrefsInstance().edit();
        editor.putInt("NOTIFICATIONS_PREFERENCES", np);
        editor.apply();
    }

    public int getUserNotificationPreferences() {
        return getPrefsInstance().getInt("NOTIFICATIONS_PREFERENCES", PUSH);
    }

}
