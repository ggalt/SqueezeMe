package com.georgegalt.squeezeme;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.georgegalt.squeezeme.ContentTypes.ArtistContent;

public class MainActivity extends AppCompatActivity implements ArtistListFragment.OnListFragmentInteractionListener {
    private static final String TAG = "Main-Activity";

    public static final int SETUP_REQUEST_CODE = 0;
    ServerInfo serverInfo;
    ArtistListFragment artistListFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        serverInfo = new ServerInfo(this);

//        artistListFragment = new ArtistListFragment();
//
//        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.MainFrame, artistListFragment, "ArtistListFragment");
//        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(this,SetupActivity.class);
            startActivityForResult(intent, SETUP_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == SETUP_REQUEST_CODE){
            Bundle extras = data.getExtras();
            if(extras.getBoolean("DataUpdated", false)) {
                Toast.makeText(getApplicationContext(), R.string.settings_updated_msg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),R.string.settings_cancel_msg,Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onListFragmentInteraction(ArtistContent.ArtistItem item, boolean isLongClick){
        return;
    }
}
