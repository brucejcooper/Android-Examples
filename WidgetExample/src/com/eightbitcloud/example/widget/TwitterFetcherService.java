package com.eightbitcloud.example.widget;

import java.io.InterruptedIOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;


public class TwitterFetcherService extends Service {
    private static final String LOG_TAG = "TwitterFetcherService";
    public static final String PREFS_FILE = "TwitterFetchPrefsFile";

	private NotificationManager mNM;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.local_service_started;

	private static PendingIntent pendingIntent;
	
	Thread fetcherThread;

    

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG, "Received start id " + startId + ": " + intent);
        
        SharedPreferences settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        String username = settings.getString("twitterUser", null);
        
        if (fetcherThread != null && fetcherThread.isAlive()) {
        	Log.d(LOG_TAG, "Killing existing fetch thread, so we can start a new one");
        	fetcherThread.interrupt();
        }
        fetcherThread = new Fetcher(username);
        fetcherThread.start();
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
    	// WE have no reason to exist!
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);
        
        if (fetcherThread.isAlive()) {
        	fetcherThread.interrupt();
        }

        Log.i(LOG_TAG, "Stopped Service");
    }


    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.local_service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.twitter, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, WidgetExampleActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.local_service_label),
                       text, contentIntent);

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

    
    /**
     * We can't bind to this service, so we return null when asked to bind.
     */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void updateWidgets(String tweetTxt) {
		// Get the widget manager and ids for this widget provider, then call the shared
		// clock update method.
		ComponentName thisAppWidget = new ComponentName(getPackageName(), ExampleAppWidgetProvider.class.getName());
	    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
	    int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
	    for (int appWidgetID: ids) {

			if (pendingIntent == null) {
				// Create an Intent to launch ExampleActivity
				Intent intent = new Intent(this, WidgetExampleActivity.class);
				pendingIntent = PendingIntent.getActivity(this, 0,	intent, 0);
			}

			RemoteViews updateViews = new RemoteViews(getPackageName(),	R.layout.widget1);

			// Set the Button Action
			updateViews.setOnClickPendingIntent(R.id.button, pendingIntent);

			// Update the text.
			updateViews.setTextViewText(R.id.widget1label, tweetTxt);
			appWidgetManager.updateAppWidget(appWidgetID, updateViews);

	    	
	    }
	}

	
	
	public class Fetcher extends Thread {
		private String username;

		public Fetcher(String username) {
			this.username= username;
		}

		public void run() {
			// Set a timeout on connections
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 10 * 1000);
			HttpConnectionParams.setSoTimeout(httpParams, 10 * 1000);

			HttpClient client = new DefaultHttpClient(httpParams);
			
			try {
				if (username == null) {
		        	Log.d(LOG_TAG, "Stopping before I've started. How sad");

				} else {
					Log.d(LOG_TAG, "Fetching tweet");
					HttpGet fetch = new HttpGet("https://api.twitter.com/1/statuses/user_timeline.json?include_entities=false&include_rts=true&screen_name="+username+"&count=1");
					if (!isInterrupted()) {
						HttpResponse response = client.execute(fetch);
						if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
							JSONArray tweets = new JSONArray(EntityUtils.toString(response.getEntity()));
							
							String tweetTxt = Html.fromHtml(tweets.getJSONObject(0).getString("text")).toString();
							
							updateWidgets(tweetTxt);
						} else {
							showError(null);
							// An Error happened.  Deal with it somehow
						}
					}
				}
			} catch (InterruptedIOException e) {
				Log.d(LOG_TAG, "I was interrupted!");
			} catch (Exception e) {
				showError(e);
			} finally {
				Log.d(LOG_TAG, "Shutting myself down");
				// We're done.  Shut ourself down.
				stopSelf();
			}
		}

		private void showError(Exception ex) {
			Log.e(LOG_TAG, "Error fetching tweets", ex);
			updateWidgets( "Fetching Tweets failed");
		}
	}


	
}
