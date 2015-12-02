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

import com.georgegalt.squeezeme.ContentTypes.ArtistContent;
import com.georgegalt.squeezeme.ContentTypes.ArtistContent.ArtistItem;

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
 * <p>
 * Activities containing this fragment MUST implement the {@link OnArtistListFragInteractionListener}
 * interface.
 */
public class ArtistListFragment extends Fragment {
    private static final String TAG = "ArtistList-Fragment";
    private static final String SERVER_CMD = "Server_command";

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private String displayCmd = null;

    private OnArtistListFragInteractionListener mListener;

    // need to keep View at the class level so we can use it
    // after we've inflated the view when we get the data from
    // SqueezeServer
    View view;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArtistListFragment() {

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
        view = inflater.inflate(R.layout.fragment_artist_list, container, false);

        Log.d(TAG,"setting listener");
        Context context = view.getContext();
        if (context instanceof OnArtistListFragInteractionListener) {
            mListener = (OnArtistListFragInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnArtistListFragInteractionListener");
        }
        Log.d(TAG,"onCreateView");
        new ArtistGetTask().execute();
        Log.d(TAG,"onCreateView after ArtisGetTask");
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG,"***** onAttach ******");

        // This appears to never be called . . .  thanks Obama!
        if (context instanceof OnArtistListFragInteractionListener) {
            mListener = (OnArtistListFragInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnArtistListFragInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnArtistListFragInteractionListener {
        // TODO: Update argument type and name
        void OnArtistListInteraction(ArtistItem item, boolean isLongClick);
    }


    private class ArtistGetTask extends AsyncTask<Void, Void, ArtistContent> {

        @Override
        protected ArtistContent doInBackground(Void... params) {
            try {
                return GetArtists();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArtistContent artistContent) {
            // fill adapter with http asynctask request
            // Set the adapter
            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;
                if (mColumnCount <= 1) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                }
                if(mListener==null) {
                    Log.d(TAG,"listener is null");
                }
                recyclerView.setAdapter(new ArtistRecyclerViewAdapter(ArtistContent.ITEMS, mListener));
            }
        }
    }


    private ArtistContent GetArtists() throws IOException {
        ArtistContent artistContent;
        artistContent = new ArtistContent();
        // create javascript request for SqueezeServer
        // format is  {"id":1,"method":"slim.request","params":["<player>",["cmd","param0","param1"]]}
        // or "{\"id\":1,\"method\":\"slim.request\",\"params\":[\"\",[\"artists\",\"0\",\"1000\"]]}"


        // format a request to get artist id, artist name and artist "textkey" (which is
        // the letter by which the artist is alphabetized).
        // we may have started with a request for a specific artist, which will have come
        // through as "displayCmd", so format a command if it is available.
        String slimRequest;
        if(displayCmd!=null){
            slimRequest = "{\"id\":1,\"method\":\"slim.request\",\"params\":"+displayCmd+"}";
        } else {
            slimRequest = "{\"id\":1,\"method\":\"slim.request\",\"params\":[\"\",[\"artists\",\"0\",\"\",\"tags:s\"]]}";
        }

        Log.d(TAG, "Artist cmd issued to server: "+slimRequest);
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
                url = new URL("http://"+MainActivity.serverInfo.getServerIP()+":"
                        +MainActivity.serverInfo.getWebPort()+"/jsonrpc.js" );
                Log.d(TAG,"Full URL request is: "+url.toString());
                client = (HttpURLConnection) url.openConnection();
                client.setDoOutput(true);
                client.setDoInput(true);
                client.setUseCaches(false);
                client.setRequestProperty("Content-Type", "application/x-www-form-url encoded");
                client.setRequestMethod("POST");
                client.connect();
                Log.d(TAG, slimRequest.toString());

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

                Log.d(TAG,responseStrBuilder.toString());

                // get the response and process the 'result' object and 'artists_loop' array
                JSONObject jsonResponse = new JSONObject(responseStrBuilder.toString());
                JSONObject resultObj = jsonResponse.getJSONObject("result");
                JSONArray artistLoopObject = resultObj.getJSONArray("artists_loop");

                for (int idx = 0; idx < artistLoopObject.length(); idx++) {
                    JSONObject artist = (JSONObject) artistLoopObject.get(idx);
                    artistContent.addItem(new ArtistItem(artist.getString("id"), artist.getString("artist"), artist.getString("textkey")));
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

        return artistContent;
    }

}
