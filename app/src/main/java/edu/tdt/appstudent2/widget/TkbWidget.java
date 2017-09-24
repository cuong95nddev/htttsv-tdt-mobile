package edu.tdt.appstudent2.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import java.util.Calendar;

import edu.tdt.appstudent2.R;
import edu.tdt.appstudent2.actitities.tkb.TkbActivity;
import edu.tdt.appstudent2.utils.Util;

/**
 * Implementation of App Widget functionality.
 */
public class TkbWidget extends AppWidgetProvider {
    private Calendar calendarToDay;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        calendarToDay = Calendar.getInstance();

        for (int i = 0; i < N; ++i) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.tkb_widget);

            remoteViews.setTextViewText(R.id.tvDaySelected, Util.getThuCalendar(calendarToDay));
            remoteViews.setTextViewText(R.id.tvDateSelected, Util.showCalendar(calendarToDay));


            Intent svcIntent = new Intent(context, WidgetService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
            remoteViews.setRemoteAdapter(appWidgetIds[i], R.id.listViewWidget, svcIntent);

            Intent tkbIntent = new Intent(context, TkbActivity.class);
            PendingIntent tkbPendingIntent = PendingIntent.getActivity(context, 0, tkbIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.btnOpenCalendar, tkbPendingIntent);

            Intent intent = new Intent(context, TkbWidget.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.btnReload, pendingIntent);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.listViewWidget);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}

