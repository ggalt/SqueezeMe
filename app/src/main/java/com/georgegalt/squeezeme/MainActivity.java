package com.georgegalt.squeezeme;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.georgegalt.squeezeme.ContentTypes.AlbumContent;
import com.georgegalt.squeezeme.ContentTypes.ArtistContent;
import com.georgegalt.squeezeme.net.SlimProtocol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        ArtistListFragment.OnArtistListFragInteractionListener,
        AlbumListFragment.OnAlbumListFragInteractionListener
{
    private static final String TAG = "Main-Activity";
    private static final String SERVER_CMD = "Server_command";

    public static final int SETUP_REQUEST_CODE = 0;
    public static ServerInfo serverInfo;
    private ArtistListFragment artistListFragment;
    private AlbumListFragment albumListFragment;
    private String macAddress;

    private SlimProtocol slimProtocol;

    ExpandableListView expandableListView;
    HomePageExpandableListAdapter homePageExpandableListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        artistListFragment = new ArtistListFragment();
        albumListFragment = new AlbumListFragment();

        if(GetConnectedState() ) {
            Log.d(TAG, "Connected");
            GetServerBasics();
            slimProtocol = new SlimProtocol();
        }
    }

    /*
     * Code for setting up home screen and responding to home screen interactions.
     * Here we will also take a first pass at getting information from server so that
     * we can populate "favorites" fields and other server information
     */

    private void GetServerBasics() {
        Log.d(TAG,"GetServerBasics() started");
        serverInfo = new ServerInfo(this);
        new GetServerBasicsTask().execute();
//        LoadHomePage();
    }

    //
//    private class GetServerBasicsTask extends AsyncTask<ServerInfo, void, ServerInfo> {
    private class GetServerBasicsTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void ... params) {
            // this will request the serverstatus from the Squeezebox Server.  If the
            // server call fails, we will return false.
            // call to server is:
            // "{\"id\":1,\"method\":\"slim.request\",\"params\":[\"\",[\"serverstatus\",\"0\",\"1000\",\"tags:g,a,l,t,e,y,d,c\"]]}"
            // we'll just assume no one has more than 1000 players attached!!
            String jsonRequest = "{\"id\":1,\"method\":\"slim.request\",\"params\":[\"\",[\"serverstatus\",\"0\",\"1000\",\"tags:g,a,l,t,e,y,d,c\"]]}";
            DataOutputStream dataOutputStream;
            InputStream dataInputStream;
            URL url = null;
            HttpURLConnection client = null;
//            ServerInfo serverInfo = new ServerInfo(params[0]);
            Integer response = 0;

            try {
                // Establish http connection
                url = new URL("http://"+ServerInfo.getServerIP()+":"
                        +ServerInfo.getWebPort()+"/jsonrpc.js" );
                client = (HttpURLConnection) url.openConnection();
                client.setDoOutput(true);
                client.setDoInput(true);
                client.setUseCaches(false);
                client.setRequestProperty("Content-Type", "application/x-www-form-url encoded");
                client.setRequestMethod("POST");
                client.connect();

                // Send the JSON object to the server
                dataOutputStream = new DataOutputStream(client.getOutputStream());
                dataOutputStream.writeBytes(jsonRequest);
                dataOutputStream.flush();
                dataOutputStream.close();

                response = (Integer)client.getResponseCode();
                Log.d(TAG, "Response code is: " + response.toString());

                // check to see if we got a good response
                if( response == 200 ) {
                    dataInputStream = client.getInputStream();
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(dataInputStream, "UTF-8"));
                    StringBuilder responseStrBuilder = new StringBuilder();

                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null)
                        responseStrBuilder.append(inputStr);

                    Log.d(TAG,"Server responded with: "+responseStrBuilder.toString());
                    // get the response and process the 'result' object and 'artists_loop' array
                    try {
                        JSONObject jsonResponse = new JSONObject(responseStrBuilder.toString());
                        JSONObject resultObj = jsonResponse.getJSONObject("result");
                        JSONArray playersLoopArray = resultObj.getJSONArray("players_loop");

                        for (int idx = 0; idx < playersLoopArray.length(); idx++) {
//                            serverInfo.getPlayers().add(new PlayerInfo(
//                                    ((JSONObject)playersLoopArray.get(idx)).getString("name"),
//                                    ((JSONObject)playersLoopArray.get(idx)).getString("uuid"),
//                                    ((JSONObject)playersLoopArray.get(idx)).getString("playerid"),
//                                    ((JSONObject)playersLoopArray.get(idx)).getString("ip")
//                            ));
                            Log.d(TAG, "Player Name: " + ((JSONObject) playersLoopArray.get(idx)).getString("name"));
                        }
                        int iSongCount = resultObj.getInt("info total songs");
                        int iAlbumCount = resultObj.getInt("info total albums");
                        int iArtistCount = resultObj.getInt("info total artists");
                        int iGenreCount = resultObj.getInt("info total genres");
                        ServerInfo.setArtistCount(String.valueOf(iArtistCount));
                        ServerInfo.setAlbumCount(String.valueOf(iAlbumCount));
                        ServerInfo.setSongCount(String.valueOf(iSongCount));
                        ServerInfo.setGenreCount(String.valueOf(iGenreCount));

                    } catch (JSONException e) {
                        Log.e(TAG,"JSONException: "+e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG,"Bad Server Response Code : "+response);
                    return response;
                }
            } catch (IOException e) {
                Log.e(TAG,"IOException: " + e.getLocalizedMessage());
                e.printStackTrace();
            } finally {
                client.disconnect();
            }
            return response;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if(result != 200) {
                Toast.makeText(getApplicationContext(),"Failed to communicate with the server.  Response code was: " + result,Toast.LENGTH_LONG).show();
            } else {
                LoadHomePage();
                Log.d(TAG, "Test to see if serverInfo is updated.  Artist count is: " + ServerInfo.getArtistCount());
            }
        }
    }

    private void LoadHomePage() {
        final List<String> listDataHeader;
        final HashMap<String, List<String>> listDataChild;

        Resources resources = getResources();
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // create headers
        listDataHeader.add(resources.getString(R.string.My_Music));
        listDataHeader.add(resources.getString(R.string.Radio));
        listDataHeader.add(resources.getString(R.string.My_Apps));
        listDataHeader.add(resources.getString(R.string.Favorites));
        listDataHeader.add(resources.getString(R.string.Extras));

        // add children to headers
        List<String> myMusic = new ArrayList<String>();
        myMusic.add(resources.getString(R.string.My_Music_Artists));
        myMusic.add(resources.getString(R.string.My_Music_Composers));
        myMusic.add(resources.getString(R.string.My_Music_Albums));
        myMusic.add(resources.getString(R.string.My_Music_Compilations));
        myMusic.add(resources.getString(R.string.My_Music_Genres));
        myMusic.add(resources.getString(R.string.My_Music_Years));
        myMusic.add(resources.getString(R.string.My_Music_New_Music));
        myMusic.add(resources.getString(R.string.My_Music_Random_Mix));
        myMusic.add(resources.getString(R.string.My_Music_Folder));
        myMusic.add(resources.getString(R.string.My_Music_Playlists));

        List<String> Radio = new ArrayList<String>();
        Radio.add(resources.getString(R.string.Radio_My_Presets));
        Radio.add(resources.getString(R.string.Radio_Staff_Picks));
        Radio.add(resources.getString(R.string.Radio_Local));
        Radio.add(resources.getString(R.string.Radio_Music));
        Radio.add(resources.getString(R.string.Radio_Talk));
        Radio.add(resources.getString(R.string.Radio_News));
        Radio.add(resources.getString(R.string.Radio_Sports));
        Radio.add(resources.getString(R.string.Radio_Language));
        Radio.add(resources.getString(R.string.Radio_World));
        Radio.add(resources.getString(R.string.Radio_Podcasts));

        List<String> myApps = new ArrayList<String>();

        List<String> Favorites = new ArrayList<String>();

        List<String> Extras = new ArrayList<String>();
        Extras.add(resources.getString(R.string.Extras_Alarm));
        Extras.add(resources.getString(R.string.Extras_Music_Source));
        Extras.add(resources.getString(R.string.Extras_Image_Browser));
        Extras.add(resources.getString(R.string.Extras_Info_Browser));

        // add children to headers
        listDataChild.put(listDataHeader.get(0),myMusic);
        listDataChild.put(listDataHeader.get(1), Radio);
        listDataChild.put(listDataHeader.get(2), myApps);
        listDataChild.put(listDataHeader.get(3),Favorites);
        listDataChild.put(listDataHeader.get(4),Extras);

        expandableListView = (ExpandableListView)findViewById(R.id.homeScreenList);
        expandableListView.setClickable(true);
        homePageExpandableListAdapter = new HomePageExpandableListAdapter(this,listDataHeader,listDataChild);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                Log.d(TAG,"onChildClick");
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                +" : "
                                +listDataChild.get(listDataHeader.get(groupPosition))
                                .get(childPosition),
                        Toast.LENGTH_SHORT)
                        .show();
                HomePageSelection(groupPosition,childPosition);
                return false;
            }
        });

        expandableListView.setAdapter(homePageExpandableListAdapter);
    }

    /*
     * Interaction with Options Menu
     */
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

    /*
     * Code for managing return from Setup Activity
     */
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


    /*
     * Listeners for Fragment interaction
     */
    public void OnArtistListInteraction(ArtistContent.ArtistItem item, boolean isLongClick){
        if(isLongClick){
            Toast.makeText(getApplicationContext(), item.artistName, Toast.LENGTH_SHORT).show();
            Log.d(TAG,"Click was long: "+item.artistName);
        } else {
            Log.d(TAG, "Click was short: " + item.artistName);
            Bundle bundle = new Bundle();
            bundle.putString(SERVER_CMD,"[\"\",[\"albums\",\"0\",\"1000\",\"artist_id:"+item.id+"\",\"tags:l,j,S,s\"]]");
            albumListFragment.setArguments(bundle);

            changeFragment(albumListFragment, true);
//            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//            fragmentTransaction.replace(R.id.MainFrame,albumListFragment);
//            fragmentTransaction.addToBackStack("AlbumListFragment");
//            fragmentTransaction.commit();
        }
        return;
    }

    public void onAlbumListInteraction(AlbumContent.AlbumItem item, boolean isLongClick) {

    }

    /*
     * Switch statement for home page option selection
     */

    private void HomePageSelection(int group, int child) {
        switch (group) {
            case 0:     // My Music
                switch (child) {
                    case 0:     // Artist
                        // NOTE: fall through since artist and composer seem to the be the same thing
                    case 1:     // composer
                        changeFragment(artistListFragment,true);
                        expandableListView.setVisibility(View.INVISIBLE);
                        break;
                    case 2:     // Albums
                        Bundle bundle = new Bundle();
                        bundle.putString(SERVER_CMD,"[\"\",[\"albums\",\"0\",\""+ServerInfo.getAlbumCount() + "\",\"tags:l,j,S,s\"]]");
                        albumListFragment.setArguments(bundle);
                        changeFragment(albumListFragment, true);
                        expandableListView.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                    case 9:
                        break;
                    case 10:
                        break;
                    default:
                        break;
                }
                break;
            case 1:     // Radio
                switch (child) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                    case 9:
                        break;
                    case 10:
                        break;
                    default:
                        break;
                }
                break;
            case 2:     // My Apps
                break;
            case 3:     // Favorites
                break;
            case 4:     // Extras
                switch (child) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
                        break;
                    case 9:
                        break;
                    case 10:
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;

        }
    }

    // find out if we are on WiFi and get MAC address
    private boolean GetConnectedState() {
        boolean isWiFi = false;
        try {
            ConnectivityManager cm =
                    (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

            WifiManager wifi = (WifiManager) this
                    .getSystemService(this.WIFI_SERVICE);
            wifi.setWifiEnabled(true);
            WifiInfo info = wifi.getConnectionInfo();
            macAddress = info.getMacAddress();

            if (macAddress == null) {
                Toast.makeText(this, "Null " + isConnected +":"+info.toString(), Toast.LENGTH_LONG).show();
                Log.d(TAG,info.toString());
            } else {
                Toast.makeText(this, macAddress, Toast.LENGTH_LONG).show();
                ServerInfo.setThisPlayerID(macAddress);
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        return isWiFi;
    }

    /**
     * Change the current displayed fragment managing fragment backstack
     *
     * @param frag the fragment to display
     * @param saveInBackStack true if you want the previous to be in backstack
     */
    private void changeFragment(Fragment frag, boolean saveInBackStack) {
        String backStateName = ((Object) frag).getClass().getName();

        try {
            FragmentManager manager = getFragmentManager();
            if(manager.getBackStackEntryCount()==0) {
                Log.d(TAG,"Hide ExpandableListView");
                expandableListView.setVisibility(View.INVISIBLE);
            }
            boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

            if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) { //fragment not in back stack, create it.
                FragmentTransaction transaction = manager.beginTransaction();
//                transaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out);
                //Custom Animation
                transaction.setCustomAnimations(R.animator.fragment_slide_left_enter,
                        R.animator.fragment_slide_left_exit,
                        R.animator.fragment_slide_right_enter,
                        R.animator.fragment_slide_right_exit);

                transaction.replace(R.id.MainFrame, frag, ((Object) frag).getClass().getName());

                if (saveInBackStack) {
                    Log.d(TAG, "Change Fragment : addToBackTack");
                    transaction.addToBackStack(backStateName);
                } else {
                    Log.d(TAG, "Change Fragment : NO addToBackTack");
                }

                transaction.commit();
            } else {
                Log.d(TAG, "Change Fragment : nothing to do");
                // custom effect if fragment is already instanciated
            }
        } catch (IllegalStateException exception) {
            Log.e(TAG, "Unable to commit fragment, could be activity as been killed in background. " + exception.toString());
        }
    }

    @Override
    public void onBackPressed() {
        int fragments = getFragmentManager().getBackStackEntryCount();
        Log.d(TAG, "Fragment Count is: " + fragments);
        switch (fragments) {
            case 0:
                super.onBackPressed();
                break;
            case 1:
                expandableListView.setVisibility(View.VISIBLE);
                /// NOTE: fall through
            default:
                getFragmentManager().popBackStack();
                break;
        }
    }

    public SlimProtocol getProtocol() {
        return slimProtocol;
    }

    public void setSlimProtocol(SlimProtocol slimProtocol) {
        this.slimProtocol = slimProtocol;
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