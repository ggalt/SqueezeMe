package com.georgegalt.george.squeezeme;

import android.content.Intent;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainScreen extends AppCompatActivity {

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_activity);

        actionBar = getSupportActionBar();
        if( findViewById(R.id.MainFrame) != null ) {
            // if being restored, don't load
            if( savedInstanceState != null ) {
                return;
            }

            // create home page fragment and display
            HomeScreenFragment homeScreenFragment = new HomeScreenFragment();

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.MainFrame,homeScreenFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if( id == R.id.action_setup_screen ) {
            Intent showSetupActivityIntent = new Intent(MainScreen.this, Setup.class);
            startActivity(showSetupActivityIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}
