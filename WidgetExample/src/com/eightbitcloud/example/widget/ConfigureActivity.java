package com.eightbitcloud.example.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ConfigureActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure);
    }
    
    
    public void createClicked(View view) {
        String username = ((TextView)findViewById(R.id.configure_username)).getText().toString().trim();
        
        if (username == null || username.length() == 0) {
        	Toast.makeText(this,  "Username is required", Toast.LENGTH_SHORT);
        	return;
        }

        Intent intent = getIntent();
    	Bundle extras = intent.getExtras();
    	if (extras != null) {
    	    int mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    	    
    	    Log.d("ConfigureActivity", "App Widget ID is " + mAppWidgetId);
    	    
    	    // Save the username to application preferences
            Editor settings = getSharedPreferences(TwitterFetcherService.PREFS_FILE, MODE_PRIVATE).edit();
            settings.putString("twitterUser", username);
            settings.apply();

    	    // Kick off a refresh, as when you have a configure activity android does not do one automatically the first time.
    		startService(new Intent(this, TwitterFetcherService.class));

    	    
    	    // Tell android that we're done
    	    Intent resultValue = new Intent();
    	    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
    	    setResult(RESULT_OK, resultValue);
    	    finish();
    	}
    }

}