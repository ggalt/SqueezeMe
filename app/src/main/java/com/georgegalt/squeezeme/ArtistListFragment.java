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

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.georgegalt.squeezeme.ContentTypes.ArtistContent;
import com.georgegalt.squeezeme.ContentTypes.ArtistContent.ArtistItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ArtistListFragment extends Fragment {

    // TODO: Customize parameters
    private int mColumnCount = 1;
    ServerInfo serverInfo;

    private OnListFragmentInteractionListener mListener;

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
        serverInfo = new ServerInfo(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_artist_list, container, false);

//        // Set the adapter
//        if (view instanceof RecyclerView) {
//            Context context = view.getContext();
//            RecyclerView recyclerView = (RecyclerView) view;
//            if (mColumnCount <= 1) {
//                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            } else {
//                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
//            }
//            recyclerView.setAdapter(new ArtistRecyclerViewAdapter(ArtistContent.ITEMS, mListener));
//        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(ArtistItem item, boolean isLongClick);
    }

//    private class ArtistGetTask extends AsyncTask<Void, Void, List<String>> {
//
//        // Get your own user name at http://www.geonames.org/login
//        private static final String USER_NAME = "aporter";
//
//        private static final String URL = "http://api.geonames.org/earthquakesJSON?north=44.1&south=-9.9&east=-22.4&west=55.2&username="
//                + USER_NAME;
//
//        AndroidHttpClient mClient = AndroidHttpClient.newInstance("");
//
//        @Override
//        protected List<String> doInBackground(Void... params) {
//            HttpGet request = new HttpGet(URL);
//            JSONResponseHandler responseHandler = new JSONResponseHandler();
//            try {
//                return mClient.execute(request, responseHandler);
//            } catch (ClientProtocolException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(List<String> result) {
//            if (null != mClient)
//                mClient.close();
//            setListAdapter(new ArrayAdapter<String>(
//                    NetworkingAndroidHttpClientJSONActivity.this,
//                    R.layout.list_item, result));
//        }
//    }
//
//    private class JSONResponseHandler implements ResponseHandler<List<String>> {
//
//        private static final String LONGITUDE_TAG = "lng";
//        private static final String LATITUDE_TAG = "lat";
//        private static final String MAGNITUDE_TAG = "magnitude";
//        private static final String EARTHQUAKE_TAG = "earthquakes";
//
//        @Override
//        public List<String> handleResponse(HttpResponse response)
//                throws ClientProtocolException, IOException {
//            List<String> result = new ArrayList<String>();
//            String JSONResponse = new BasicResponseHandler()
//                    .handleResponse(response);
//            try {
//
//                // Get top-level JSON Object - a Map
//                JSONObject responseObject = (JSONObject) new JSONTokener(
//                        JSONResponse).nextValue();
//
//                // Extract value of "earthquakes" key -- a List
//                JSONArray earthquakes = responseObject
//                        .getJSONArray(EARTHQUAKE_TAG);
//
//                // Iterate over earthquakes list
//                for (int idx = 0; idx < earthquakes.length(); idx++) {
//
//                    // Get single earthquake data - a Map
//                    JSONObject earthquake = (JSONObject) earthquakes.get(idx);
//
//                    // Summarize earthquake data as a string and add it to
//                    // result
//                    result.add(MAGNITUDE_TAG + ":"
//                            + earthquake.get(MAGNITUDE_TAG) + ","
//                            + LATITUDE_TAG + ":"
//                            + earthquake.getString(LATITUDE_TAG) + ","
//                            + LONGITUDE_TAG + ":"
//                            + earthquake.get(LONGITUDE_TAG));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return result;
//        }
//    }

}
