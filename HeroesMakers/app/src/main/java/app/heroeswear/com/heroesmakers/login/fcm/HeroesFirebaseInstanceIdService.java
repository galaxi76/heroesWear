package app.heroeswear.com.heroesmakers.login.fcm;

import com.google.firebase.iid.FirebaseInstanceIdService;

import app.heroeswear.com.heroesfb.FirebaseManager;

public class HeroesFirebaseInstanceIdService extends FirebaseInstanceIdService {

    public void onTokenRefresh() {
        FirebaseManager.Companion.newInstance().updatePushToken();
    }
}
