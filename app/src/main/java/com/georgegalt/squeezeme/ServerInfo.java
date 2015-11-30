package com.georgegalt.squeezeme;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

// DEFAULT_SERVER_IP
// DEFAULT_PLAYER_PORT
// DEFAULT_CLI_PORT
// DEFAULT_WEB_PORT
// DEFAULT_PLAYER_NAME
// DEFAULT_USERNAME
// DEFAULT_PASSWORD
//
// SERVER_IP
// PLAYER_PORT
// CLI_PORT
// WEB_PORT
// PLAYER_NAME
// USERNAME
// PASSWORD


/**
 * Created by ggalt66 on 11/18/2015.
 */
public class ServerInfo {
    private static final String TAG = "ServerInfo-Class";

    private static final String DEFAULT_SERVER_IP = "127.0.0.1";
    private static final String DEFAULT_PLAYER_PORT = "3448";
    private static final String DEFAULT_CLI_PORT = "9090";
    private static final String DEFAULT_WEB_PORT = "9000";
    private static final String DEFAULT_PLAYER_NAME = "SqueezeMe";
    private static final String DEFAULT_USERNAME = "";
    private static final String DEFAULT_PASSWORD = "";

    private static String SERVER_IP;
    private static String PLAYER_PORT;
    private static String CLI_PORT;
    private static String WEB_PORT;
    private static String PLAYER_NAME;
    private static String USERNAME;
    private static String PASSWORD;

    private SharedPreferences prefs;
    // if false, we can't write to preference file because we don't have a context
    private boolean bCanUpdate = false;

    public ServerInfo(Activity activity) {
        prefs = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
        bCanUpdate = true;      // we have a context so allow updating
        readValues();
    }

    // this constructor is used when we want to use a copy of the information in the class
    // where a context may not be available (or where we don't need to read and write)
    public ServerInfo(ServerInfo s) {
        SERVER_IP = s.getServerIP();
        PLAYER_PORT = s.getPlayerPort();
        CLI_PORT = s.getCliPort();
        WEB_PORT = s.getWebPort();
        PLAYER_NAME = s.getPlayerName();
        USERNAME = s.getUSERNAME();
        PASSWORD = s.getPASSWORD();
    }

    public ServerInfo() {
        SERVER_IP = null;
        PLAYER_PORT = null;
        CLI_PORT = null;
        WEB_PORT = null;
        PLAYER_NAME = null;
        USERNAME = null;
        PASSWORD = null;
    }

    public String getServerIP() { return SERVER_IP;}
    public String getPlayerPort() { return PLAYER_PORT; }
    public String getCliPort() { return CLI_PORT; }
    public String getWebPort() { return WEB_PORT; }
    public String getPlayerName() { return PLAYER_NAME; }
    public String getUSERNAME() { return USERNAME; }
    public String getPASSWORD() { return PASSWORD; }

    public boolean isbCanUpdate() {return bCanUpdate;}
    
    public void setServerIP(String s) {SERVER_IP=s;}
    public void setPlayerPort(String s) {PLAYER_PORT=s;}
    public void setCliPort(String s) {CLI_PORT=s;}
    public void setWebPort(String s) {WEB_PORT=s;}
    public void setPlayerName(String s) {PLAYER_NAME=s;}
    public void setUSERNAME(String s) {USERNAME=s;}
    public void setPASSWORD(String s) {PASSWORD=s;}

    public void resetValues(){
        SERVER_IP = DEFAULT_SERVER_IP;
        PLAYER_PORT = DEFAULT_PLAYER_PORT;
        CLI_PORT = DEFAULT_CLI_PORT;
        WEB_PORT = DEFAULT_WEB_PORT;
        PLAYER_NAME = DEFAULT_PLAYER_NAME;
        USERNAME = DEFAULT_USERNAME;
        PASSWORD = DEFAULT_PASSWORD;
    }

    public void readValues(){
        if(bCanUpdate){
            SERVER_IP = prefs.getString("SERVER_IP_ID", DEFAULT_SERVER_IP);
            PLAYER_PORT = prefs.getString("PLAYER_PORT_ID", DEFAULT_PLAYER_PORT );
            CLI_PORT = prefs.getString("CLI_PORT_ID", DEFAULT_CLI_PORT);
            WEB_PORT = prefs.getString("WEB_PORT_ID", DEFAULT_WEB_PORT);
            PLAYER_NAME = prefs.getString("PLAYER_NAME_ID", DEFAULT_PLAYER_NAME);
            USERNAME = prefs.getString("USERNAME_ID", DEFAULT_USERNAME);
            PASSWORD = prefs.getString("PASSWORD_ID", DEFAULT_PASSWORD);
        }
    }

    public void writeValues(){
        if(bCanUpdate){
            SharedPreferences.Editor editor = prefs.edit();

            editor.putString("SERVER_IP_ID", SERVER_IP);
            editor.putString("PLAYER_PORT_ID", PLAYER_PORT);
            editor.putString("CLI_PORT_ID", CLI_PORT);
            editor.putString("WEB_PORT_ID", WEB_PORT);
            editor.putString("PLAYER_NAME_ID", PLAYER_NAME);
            editor.putString("USERNAME_ID", USERNAME);
            editor.putString("PASSWORD_ID", PASSWORD);
            editor.commit();
        }
    }
}
