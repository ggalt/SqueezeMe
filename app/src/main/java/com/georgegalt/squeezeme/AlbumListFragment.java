/*
 * Copyright George Galt 2015 All Rights Reserved
 * george@georgegalt.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package com.georgegalt.squeezeme;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.georgegalt.squeezeme.ContentTypes.AlbumContent;

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

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnAlbumListFragInteractionListener}
 * interface.
 */
public class AlbumListFragment extends Fragment {
    private static final String TAG = "AlbumList-Fragment";
    private static final String SERVER_CMD = "Server_command";


    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnAlbumListFragInteractionListener mListener;

    private String displayCmd = null;

    // need to keep View at the class level so we can use it
    // after we've inflated the view when we get the data from
    // SqueezeServer
    View view;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AlbumListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null) {
            if(bundle.containsKey(SERVER_CMD)) {
                displayCmd = bundle.getString(SERVER_CMD);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_albumlist_list, container, false);

        Log.d(TAG, "setting listener");
        Context context = view.getContext();
        if (context instanceof OnAlbumListFragInteractionListener) {
            mListener = (OnAlbumListFragInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAlbumListFragInteractionListener");
        }
        Log.d(TAG, "onCreateView");
        if(view instanceof RecyclerView) {
            Log.d(TAG,"it's a recyclerview!!");

        }
        new AlbumGetTask().execute();
        Log.d(TAG,"onCreateView after ArtisGetTask");

// move to AsyncTask postexecute()
//        // Set the adapter
//        if (view instanceof RecyclerView) {
//            Context context = view.getContext();
//            RecyclerView recyclerView = (RecyclerView) view;
//            if (mColumnCount <= 1) {
//                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            } else {
//                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
//            }
//            recyclerView.setAdapter(new AlbumListRecyclerViewAdapter(AlbumContent.DummyContent.ITEMS, mListener));
//        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
//        super.onAttach(context);
        if (context instanceof OnAlbumListFragInteractionListener) {
            mListener = (OnAlbumListFragInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAlbumListFragInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnAlbumListFragInteractionListener {
        // TODO: Update argument type and name
        void onAlbumListInteraction(AlbumContent.AlbumItem item, boolean isLongClick);
    }

    private class AlbumGetTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            try {
                GetAlbums();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//             fill adapter with http asynctask request
//             Set the adapter
            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;
                if (mColumnCount <= 1) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
//                    recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
                    recyclerView.addItemDecoration( new VerticalSpaceItemDecoration(Global.VERTICAL_ITEM_SPACE));
                    Log.d(TAG,"Setting Veritcal Spacing");
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                }
                if(mListener==null) {
                    Log.d(TAG,"listener is null");
                }
                recyclerView.setAdapter(new AlbumListRecyclerViewAdapter(AlbumContent.ITEMS, mListener));
            }
        }
    }

    private void GetAlbums() throws IOException {
        GetAlbums(displayCmd);
        Log.d(TAG, "Executing command: " + displayCmd);
//        if(displayCmd!= null) {
//
//        }
//        GetAlbums("[\"\",[\"albums\",\"0\",\""+ServerInfo.getAlbumCount()+"\",\"tags:l,j,S,s\"]]");
    }

    private void GetAlbums(String albumCmd ) throws IOException {
        // create javascript request for SqueezeServer
        // format is  {"id":1,"method":"slim.request","params":["<player>",["cmd","param0","param1"]]}
        // or "{\"id\":1,\"method\":\"slim.request\",\"params\":[\"\",[\"artists\",\"0\",\"1000\"]]}"


        // format a request to get artist id, artist name and artist "textkey" (which is
        // the letter by which the artist is alphabetized).
        // we may have started with a request for a specific artist, which will have come
        // through as "displayCmd", so format a command if it is available.
        String slimRequest = "{\"id\":1,\"method\":\"slim.request\",\"params\":"+albumCmd+"}";

        Log.d(TAG, "cmd issued to server: "+slimRequest);
        JSONObject jsonSlimRequest = null;

        String strResponse = null;

        DataOutputStream dataOutputStream;
        InputStream dataInputStream;
        try
        {
            jsonSlimRequest = new JSONObject(slimRequest);
            URL url = null;
            HttpURLConnection client = null;
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
                Log.d(TAG, "Post Connect: " + slimRequest);

                // Send the JSON object to the server
                dataOutputStream = new DataOutputStream(client.getOutputStream());
                dataOutputStream.writeBytes(slimRequest);
                dataOutputStream.flush();
                dataOutputStream.close();

                Log.d(TAG,"dataOutputStream closed: "+jsonSlimRequest.toString());

                Integer response = (Integer)client.getResponseCode();
                Log.d(TAG, "Response code is: " + response.toString());

                dataInputStream = client.getInputStream();
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(dataInputStream, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                Log.d(TAG,"response length is: "+responseStrBuilder.toString().length());
                Log.d(TAG,responseStrBuilder.toString());

                // get the response and process the 'result' object and 'albums_loop' array
                JSONObject jsonResponse = new JSONObject(responseStrBuilder.toString());
                JSONObject resultObj = jsonResponse.getJSONObject("result");
                JSONArray albumLoopObject = resultObj.getJSONArray("albums_loop");

                AlbumContent.ITEMS.clear();
                AlbumContent.ITEM_MAP.clear();

                for (int idx = 0; idx < albumLoopObject.length(); idx++) {
                    JSONObject album = (JSONObject) albumLoopObject.get(idx);
                    AlbumContent.addItem(new AlbumContent.AlbumItem(
                            GetString(album, "id"),
                            GetString(album, "artist_id"),
                            GetString(album, "album"),
                            GetString(album, "artwork_track_id"),
                            GetString(album, "textkey")));
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                client.disconnect();
            }
        }
        catch(JSONException e)
        {
            Log.e(TAG, "JSONObject creation error");
            e.printStackTrace();
        }
    }

    // convenience method to see if there is a value, and if not return an empty string
    private String GetString(JSONObject album, String key) {
        String retVal = "";
        if(album.has(key)){
            try {
                retVal = album.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return retVal;
    }
}
