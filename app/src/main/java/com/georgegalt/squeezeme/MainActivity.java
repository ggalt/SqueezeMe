package com.georgegalt.squeezeme;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.georgegalt.squeezeme.ContentTypes.AlbumContent;
import com.georgegalt.squeezeme.ContentTypes.ArtistContent;

public class MainActivity extends AppCompatActivity implements
        ArtistListFragment.OnArtistListFragInteractionListener,
        AlbumListFragment.OnAlbumListFragInteractionListener
{
    private static final String TAG = "Main-Activity";
    private static final String SERVER_CMD = "Server_command";

    public static final int SETUP_REQUEST_CODE = 0;
    ServerInfo serverInfo;
    ArtistListFragment artistListFragment;
    AlbumListFragment albumListFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        serverInfo = new ServerInfo(this);

        artistListFragment = new ArtistListFragment();
        albumListFragment = new AlbumListFragment();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.MainFrame, artistListFragment, "ArtistListFragment");
        fragmentTransaction.addToBackStack("ArtistListFragment");
        fragmentTransaction.commit();
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

    public void OnArtistListInteraction(ArtistContent.ArtistItem item, boolean isLongClick){
        if(isLongClick){
            Toast.makeText(getApplicationContext(), item.artistName, Toast.LENGTH_SHORT).show();
            Log.d(TAG,"Click was long: "+item.artistName);
        } else {
            Log.d(TAG, "Click was short: " + item.artistName);
            Bundle bundle = new Bundle();
            bundle.putString(SERVER_CMD,"[\"\",[\"albums\",\"0\",\"\",\"tags:s\"]");
            albumListFragment.setArguments(bundle);

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.MainFrame,albumListFragment);
            fragmentTransaction.addToBackStack("AlbumListFragment");
            fragmentTransaction.commit();
        }
        return;
    }

    public void onAlbumListInteraction(AlbumContent.AlbumItem item, boolean isLongClick) {

    }
}


/*
curl -i -X POST -d "{\"id\":1,\"method\":\"slim.request\",\"params\":[\"\",[\"serverstatus\",\"0\",\"100\",\"\"]]}" http://192.168.1.50:9000/jsonrpc.js

{"params":["",["serverstatus","0","100",""]],"method":"slim.request","id":1,"result":{"lastscan":"1448780427","version":"7.9.0","info total albums":527,
"players_loop":[
{"power":0,"model":"slimp3","connected":1,"canpoweroff":1,"isplayer":1,"playerid":"00:04:20:04:0b:2c","isplaying":0,"displaytype":"noritake-katakana","uuid":null,"seq_no":0,"name":"Workshop","ip":"192.168.1.21:3483"},{"connected":1,"power":"1","model":"squeezelite","ip":"192.168.1.162:60041","name":"Kitchen","displaytype":"none","uuid":null,"seq_no":0,"isplaying":0,"isplayer":1,"canpoweroff":1,"playerid":"00:00:00:00:00:00"},
{"ip":"192.168.1.163:60693","name":"Master Bath","uuid":null,"displaytype":"none","seq_no":0,"isplaying":1,"isplayer":1,"playerid":"16:bf:5c:45:d3:4b","canpoweroff":1,"connected":1,"power":"1","model":"squeezelite"}],
"uuid":"e7e9c30c-647b-4f30-9d40-304ad3c29250","info total songs":8427,"player count":3,"sn player count":0,"info total artists":308,"other player count":0,"info total genres":62}}
 */