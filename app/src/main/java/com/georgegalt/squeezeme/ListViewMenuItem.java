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

import java.net.URL;

/**
 * Created by ggalt66 on 12/1/2015.
 */

/*
 * Basic class to hold a menu item for a list view
 */
public class ListViewMenuItem {
    private String Title;
    private String SubTitle;
    private URL imageURL;
    private String clickCmd;
    private String longClickCmd;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSubTitle() {
        return SubTitle;
    }

    public void setSubTitle(String subTitle) {
        SubTitle = subTitle;
    }

    public URL getImageURL() {
        return imageURL;
    }

    public void setImageURL(URL imageURL) {
        this.imageURL = imageURL;
    }

    public String getClickCmd() {
        return clickCmd;
    }

    public void setClickCmd(String clickCmd) {
        this.clickCmd = clickCmd;
    }

    public String getLongClickCmd() {
        return longClickCmd;
    }

    public void setLongClickCmd(String longClickCmd) {
        this.longClickCmd = longClickCmd;
    }

}
