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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.georgegalt.squeezeme.AlbumListFragment.OnAlbumListFragInteractionListener;
import com.georgegalt.squeezeme.ContentTypes.AlbumContent.AlbumItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AlbumItem} and makes a call to the
 * specified {@link OnAlbumListFragInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AlbumListRecyclerViewAdapter extends RecyclerView.Adapter<AlbumListRecyclerViewAdapter.ViewHolder> {

    private final List<AlbumItem> mValues;
    private final OnAlbumListFragInteractionListener mListener;

    public AlbumListRecyclerViewAdapter(List<AlbumItem> items, OnAlbumListFragInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_albumlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mAlbumTitle.setText(mValues.get(position).albumName);
        holder.mArtistName.setText(mValues.get(position).artistName);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isLongClick = false;
                if(mListener != null ){
                    mListener.onAlbumListInteraction( holder.mItem, isLongClick);
                }
            }
        });

//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(AlbumItem holder.mItem, boolean isLongClick);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mAlbumTitle;
        public final TextView mArtistName;
        public final ImageView mImage;
        public AlbumItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAlbumTitle = (TextView) view.findViewById(R.id.txtAlbumTitle);
            mArtistName = (TextView) view.findViewById(R.id.txtArtistName);
            mImage = (ImageView) view.findViewById(R.id.albumArtwork);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAlbumTitle.getText() + "'";
        }
    }
}
