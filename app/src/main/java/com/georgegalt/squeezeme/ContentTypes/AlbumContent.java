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

package com.georgegalt.squeezeme.ContentTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ggalt on 11/29/15.
 */
public class AlbumContent {

    public static final List<AlbumItem> ITEMS = new ArrayList<AlbumItem>();

    public static final Map<String, AlbumItem> ITEM_MAP = new HashMap<String, AlbumItem>();

    public static void addItem(AlbumItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

//    private static AlbumItem createArtistItem(int position) {
//        return new AlbumItem(String.valueOf(position), "Item " + position, makeDetails(position));
//    }
//
//    private static String makeDetails(int position) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("Details about Item: ").append(position);
//        for (int i = 0; i < position; i++) {
//            builder.append("\nMore textKey information here.");
//        }
//        return builder.toString();
//    }

    public static class AlbumItem {
        public final String id;
        public final String artistName;
        public final String albumName;
        public final String artworkID;
        public final String textKey;

        public AlbumItem(String id, String artistName, String albumName, String artworkID, String textKey) {
            this.id = id;
            this.artistName = artistName;
            this.albumName = albumName;
            this.artworkID = artworkID;
            this.textKey = textKey;
        }

        @Override
        public String toString() {
            return albumName;
        }
    }
}
