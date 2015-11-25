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
 * Helper class for providing sample artistName for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ArtistContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<ArtistItem> ITEMS = new ArrayList<ArtistItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, ArtistItem> ITEM_MAP = new HashMap<String, ArtistItem>();

//    private static final int COUNT = 25;
//
//    static {
//        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createArtistItem(i));
//        }
//    }

    private static void addItem(ArtistItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static ArtistItem createArtistItem(int position) {
        return new ArtistItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore textKey information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of artistName.
     */
    public static class ArtistItem {
        public final String id;
        public final String artistName;
        public final String textKey;

        public ArtistItem(String id, String content, String textKey) {
            this.id = id;
            this.artistName = content;
            this.textKey = textKey;
        }

        @Override
        public String toString() {
            return artistName;
        }
    }
}
