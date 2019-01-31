package com.example.test.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.test.R;
import com.example.test.profile.ProfileActivity;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayoutId;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationId;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle("Home Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        makeObj();

        navigationId.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayoutId, R.string.nav_open,R.string.nav_close);

        drawerLayoutId.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void makeObj() {

        //for navigation drawer
        drawerLayoutId = (DrawerLayout)findViewById(R.id.drawerLayoutId);
        navigationId = (NavigationView) findViewById(R.id.navigationId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.home_menu:
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                break;

            case R.id.profile_menu:
                finish();
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        progressBar.setVisibility(View.GONE);
        super.onBackPressed();
    }
}































