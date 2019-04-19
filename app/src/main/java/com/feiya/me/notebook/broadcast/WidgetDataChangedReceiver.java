package com.feiya.me.notebook.broadcast;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.feiya.me.notebook.Constant;
import com.feiya.me.notebook.R;
import com.feiya.me.notebook.service.RemoteListViewService;
import com.feiya.me.notebook.widget.NoteWidgetProvider;

public class WidgetDataChangedReceiver extends BroadcastReceiver {
    private static final String TAG = WidgetDataChangedReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        String action = intent.getAction();
        int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        int pageId = intent.getIntExtra(Constant.PAGE_ID, 0);
        Log.i(TAG, "WidgetDataChangedReceiver action : " + action + " widgetId : " + widgetId + " pageId : " + pageId);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.list_view_adapter);

        Intent listViewServiceDataChangedIntent = new Intent(context, RemoteListViewService.class);
        listViewServiceDataChangedIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        listViewServiceDataChangedIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        remoteViews.setRemoteAdapter(R.id.page_list_view, listViewServiceDataChangedIntent);

        appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.page_list_view);
    }
}
