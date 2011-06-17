package com.eightbitcloud.example.widget;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class ExampleAppWidgetProvider extends AppWidgetProvider {
	DateFormat df = new SimpleDateFormat("hh:mm:ss");

	public static String CLOCK_WIDGET_UPDATE = "CLOCK_WIDGET_UPDATE";

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		if (CLOCK_WIDGET_UPDATE.equals(intent.getAction())) {
			Toast.makeText(context, "onReceiver()", Toast.LENGTH_LONG).show();
		}
	}

	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final int N = appWidgetIds.length;

		Log.i("ExampleWidget",	"Updating widgets " + Arrays.asList(appWidgetIds));

		// Perform this loop procedure for each App Widget that belongs to this
		// provider
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];

			// Create an Intent to launch ExampleActivity
			Intent intent = new Intent(context, WidgetExampleActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,	intent, 0);

			// Get the layout for the App Widget and attach an on-click listener
			// to the button
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.widget1);
			views.setOnClickPendingIntent(R.id.button, pendingIntent);

			// To update a label
			views.setTextViewText(R.id.widget1label, df.format(new Date()));

			// Tell the AppWidgetManager to perform an update on the current app
			// widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

	public static void updateAppWidget(Context context,	AppWidgetManager appWidgetManager, int appWidgetId) {
		String currentTime = formatter.format(new Date());
		strWidgetText = currentTime;

		RemoteViews updateViews = new RemoteViews(context.getPackageName(),	R.layout.hellowidget_layout);
		updateViews.setTextViewText(R.id.widgettext, "[" + String.valueOf(appWidgetId) + "]" + strWidgetText);
		appWidgetManager.updateAppWidget(appWidgetId, updateViews);

		Toast.makeText(context,	"updateAppWidget(): " + String.valueOf(appWidgetId) + "\n"+ strWidgetText, Toast.LENGTH_LONG).show();

	}

}
