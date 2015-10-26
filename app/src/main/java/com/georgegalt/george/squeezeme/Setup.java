package com.georgegalt.george.squeezeme;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class Setup extends AppCompatActivity {

    private static final String DEFAULT_SERVER_IP = "127.0.0.1";
    private static final int SERVER_IP_ID = 0;
    private static final String DEFAULT_PLAYER_PORT = "3448";
    private static final int PLAYER_PORT_ID = 1;
    private static final String DEFAULT_CLI_PORT = "9090";
    private static final int CLI_PORT_ID = 2;
    private static final String DEFAULT_WEB_PORT = "9000";
    private static final int WEB_PORT_ID = 3;
    private static final String DEFAULT_PLAYER_NAME = "SqueezeMe";
    private static final int PLAYER_NAME_ID = 4;
    private static final String DEFAULT_USERNAME = "";
    private static final int USERNAME_ID = 5;
    private static final String DEFAULT_PASSWORD = "";
    private static final int PASSWORD_ID = 6;

    private static String SERVER_IP = "127.0.0.1";
    private static String PLAYER_PORT = "3448";
    private static String CLI_PORT = "9090";
    private static String WEB_PORT = "9000";
    private static String PLAYER_NAME = "SqueezeMe";
    private static String USERNAME = "";
    private static String PASSWORD = "";

    private EditText serverIP;
    private EditText playerPort;
    private EditText cliPort;
    private EditText webPort;
    private EditText playerName;
    private EditText userName;
    private EditText passWord;


    final SharedPreferences prefs = getPreferences(MODE_PRIVATE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setup);

        // get initial values and set up interface
        GetValues();
        SetScreenValues();

        // Reset Button
        final Button resetButton = (Button) findViewById(R.id.btnReset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SERVER_IP = DEFAULT_SERVER_IP;
                PLAYER_PORT = DEFAULT_PLAYER_PORT;
                CLI_PORT = DEFAULT_CLI_PORT;
                WEB_PORT = DEFAULT_WEB_PORT;
                PLAYER_NAME = DEFAULT_PLAYER_NAME;
                USERNAME = DEFAULT_USERNAME;
                PASSWORD = DEFAULT_PASSWORD;
                SetPreferenceValues();
                SetScreenValues();
            }
        });
        
        final Button okButton = (Button) findViewById(R.id.btnOK);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetPreferenceValues();
                finish();
            }
        });

        final Button cancelButton = (Button) findViewById(R.id.btnCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setup, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean SetPreferenceValues(){
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("SERVER_IP_ID", SERVER_IP);
        editor.putString("PLAYER_PORT_ID", PLAYER_PORT);
        editor.putString("CLI_PORT_ID", CLI_PORT);
        editor.putString("WEB_PORT_ID", WEB_PORT);
        editor.putString("PLAYER_NAME_ID", PLAYER_NAME);
        editor.putString("USERNAME_ID", USERNAME);
        editor.putString("PASSWORD_ID", PASSWORD);
        editor.commit();
        return true;
    }
    
    private void GetValues() {
        SERVER_IP = prefs.getString("SERVER_IP_ID", DEFAULT_SERVER_IP);
        PLAYER_PORT = prefs.getString("PLAYER_PORT_ID", DEFAULT_PLAYER_PORT );
        CLI_PORT = prefs.getString("CLI_PORT_ID", DEFAULT_CLI_PORT);
        WEB_PORT = prefs.getString("WEB_PORT_ID", DEFAULT_WEB_PORT);
        PLAYER_NAME = prefs.getString("PLAYER_NAME_ID", DEFAULT_PLAYER_NAME);
        USERNAME = prefs.getString("USERNAME_ID", DEFAULT_USERNAME);
        PASSWORD = prefs.getString("PASSWORD_ID", DEFAULT_PASSWORD);
    }

    private void SetScreenValues(){
        serverIP = (EditText)findViewById(R.id.edtServerIP);
        playerPort = (EditText)findViewById(R.id.edtPort);
        cliPort = (EditText)findViewById(R.id.edtCliPort);
        webPort = (EditText)findViewById(R.id.edtWebPort);
        playerName = (EditText)findViewById(R.id.edtPlayer);
        userName = (EditText)findViewById(R.id.edtUser);
        passWord = (EditText)findViewById(R.id.edtPass);

        serverIP.setText(SERVER_IP);
        playerPort.setText(PLAYER_PORT);
        cliPort.setText(CLI_PORT);
        webPort.setText(WEB_PORT);
        playerName.setText(PLAYER_NAME);
        userName.setText(USERNAME);
        passWord.setText(PASSWORD);
    }
    
    private void GetScreenValues() {
        SERVER_IP = serverIP.getText().toString();
        PLAYER_PORT = playerPort.getText().toString();
        CLI_PORT = cliPort.getText().toString();
        WEB_PORT = webPort.getText().toString();
        PLAYER_NAME = playerName.getText().toString();
        USERNAME = userName.getText().toString();
        PASSWORD = passWord.getText().toString();

    }

}
