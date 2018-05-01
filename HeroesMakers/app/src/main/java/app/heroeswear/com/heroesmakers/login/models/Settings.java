package app.heroeswear.com.heroesmakers.login.models;

/**
 * Created by livnatavikasis on 30/04/2018.
 */

public class Settings {
    private static Settings _instance = new Settings();

    public static Settings getInstance() {
        return _instance;
    }

    public Settings() {
        _instance = this;
    }



    private User mUser;

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }
}
