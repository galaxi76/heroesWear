package app.heroeswear.com.heroesmakers.login.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import app.heroeswear.com.heroesmakers.R;
import common.BaseActivity;

/**
 * Created by livnatavikasis on 01/05/2018.
 */

public class HomePageActivity extends BaseActivity  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        initMenues();

    }




}
