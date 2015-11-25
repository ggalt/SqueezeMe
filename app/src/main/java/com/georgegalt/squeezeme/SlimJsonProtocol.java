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

/**
 * Created by ggalt66 on 11/24/2015.
 */
public class SlimJsonProtocol {
    private static final String TAG = "SlimJsonProtocol-class";

    private ServerInfo serverInfo;
    private String jSonRequestAddress;

    SlimJsonProtocol(ServerInfo s) {
        serverInfo = new ServerInfo(s);
        jSonRequestAddress = serverInfo.getServerIP()+":"+serverInfo.getWebPort()+"/jsonrpc.js";
    }

    public void GetArtistList(){

    }
}
