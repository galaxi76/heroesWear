package app.heroeswear.com.heroesmakers.login.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import app.heroeswear.com.heroesfb.Logger;
import app.heroeswear.com.heroesmakers.R;

public class AreYouOkActivity extends AppCompatActivity {

    private Button btn_notOk;
    private Button btn_Ok;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_are_you_ok);

        btn_notOk = (Button) findViewById(R.id.btn_not_ok);
        btn_notOk.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                launchActivity();
            }
        });

        btn_Ok = (Button) findViewById(R.id.btn_not_ok);
        btn_Ok.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                moveTaskToBack(true);
            }
        });

        //setShowWhenLocked(true);
        //setTurnScreenOn(true);
        playNotificationSound();
    }
    private void launchActivity(){
        Intent intent = new Intent(this,SolutionListActivity.class);
        startActivity(intent);
    }
    @Override
    public void onAttachedToWindow() {
        //this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        super.onAttachedToWindow();
    }

    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getPackageName() + "/raw/push_notification_sound");
            Ringtone r = RingtoneManager.getRingtone(this, alarmSound);
            r.play();
        } catch (Exception e) {
            Logger.Companion.e(e.getMessage());
        }
    }
}
