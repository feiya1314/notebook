package com.feiya.me.notebook.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.feiya.me.notebook.Constant;
import com.feiya.me.notebook.R;
import com.feiya.me.notebook.activity.EditNoteActivity;
import com.feiya.me.notebook.db.DatabaseManager;
import com.feiya.me.notebook.model.NoteItem;
import com.feiya.me.notebook.service.RemoteListViewService;
import com.feiya.me.notebook.utils.Utils;

import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class NoteWidgetProvider extends AppWidgetProvider {

    private static final String TAG = NoteWidgetProvider.class.getSimpleName();
  /*  private int pagesCount = 10;
    private Map<Integer, Object> initWidgetMap = new HashMap<>();*/

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(TAG, "Widget Provider onUpdate");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            initWidgetDatabase(context, appWidgetManager, appWidgetId);
            /*if (initWidgetMap.get(appWidgetId) != null) {
                continue;
            }*/
            registerListener(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context,appWidgetManager,appWidgetIds);
    }

    private void registerListener(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.list_view_adapter);
        Intent listViewServiceIntent = new Intent(context, RemoteListViewService.class);
        listViewServiceIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        listViewServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        //listViewServiceIntent.setData(Uri.parse(listViewServiceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(R.id.page_list_view, listViewServiceIntent);

        Intent flipperIntent = new Intent();
        flipperIntent.setComponent(new ComponentName(context, NoteWidgetProvider.class));
        flipperIntent.setAction(Constant.COLLECTION_VIEW_ACTION);
        flipperIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        flipperIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent filpperPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, flipperIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setPendingIntentTemplate(R.id.page_list_view, filpperPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }


    /**
     * Implements { BroadcastReceiver#onReceive} to dispatch calls to the various
     * other methods on AppWidgetProvider.
     * 接收窗口小部件点击时发送的广播
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        int pageId;
        int widgetId;
        DatabaseManager databaseManager = DatabaseManager.getInstance(context);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        Log.d(TAG, "provider onReceive " + action);
        switch (action) {
            case Constant.COLLECTION_VIEW_ACTION: {
                //pageId = intent.getIntExtra(Constant.COLLECTION_VIEW_EXTRA, 0);
                Bundle bundle = intent.getExtras();
                pageId = (int)bundle.get(Constant.COLLECTION_VIEW_EXTRA);
                widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                Log.e(TAG, "receive collection action widgetId " + widgetId);

                Intent startActivity = new Intent(context, EditNoteActivity.class);
                startActivity.putExtra(Constant.PAGE_ID, pageId);
                startActivity.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                //由于不是在activity中启动另一个activity，而是由context启动，需要设置FLAG_ACTIVITY_NEW_TASK标志
                //表明启动一个activity
                startActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startActivity);
                break;
            }
            case Constant.DATA_CHANGED_ACTION: {
                Log.d(TAG, "Action _appwidget_update");
                pageId = intent.getIntExtra(Constant.PAGE_ID, 0);
                widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                //ComponentName componentName=new ComponentName(context,NoteWidgetProvider.class);

                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.list_view_adapter);

                Intent listViewServiceDataChangedIntent = new Intent(context, RemoteListViewService.class);
                listViewServiceDataChangedIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                listViewServiceDataChangedIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                remoteViews.setRemoteAdapter(R.id.page_list_view, listViewServiceDataChangedIntent);

                appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.page_list_view);

                appWidgetManager.updateAppWidget(widgetId, remoteViews);
                break;
            }
            case Intent.ACTION_SHUTDOWN: {
                Log.d(TAG, "shutdown");
                databaseManager.topPageInit();
                break;
            }
            default:
                break;
        }

        super.onReceive(context, intent);
    }

    private int getTopPageId(Context context, DatabaseManager databaseManager, int widgetId) {
       /* NoteItem noteItem;
        noteItem = databaseManager.getItem(databaseManager.queryTopPageItem(widgetId));
        Log.e(TAG, "topPageId " + noteItem.getPageId());
        databaseManager.topPageToFalse(widgetId);
        return noteItem.getPageId();*/
       return 0;
    }

    private void initWidgetDatabase(Context context, AppWidgetManager appWidgetManager, int widgetId) {
        DatabaseManager databaseManager = DatabaseManager.getInstance(context);
        if (databaseManager.queryItemByWidgetId(widgetId).getCount() != 0) {
            Log.i(TAG, "该widget已经生成过啦");
            int topPage = databaseManager.getTopPageId(widgetId);
            RemoteViews showTopPage = new RemoteViews(context.getPackageName(), R.layout.list_view_adapter);
            showTopPage.setDisplayedChild(R.id.page_list_view, topPage);
            return;
        }

        for (int i = 0; i < 10; i++) {
            NoteItem noteItem = new NoteItem(Constant.INIT_NOTE_TITLE);
            noteItem.setContent(Constant.INIT_NOTE_CONTENT);
            noteItem.setPageId(i);
            if (i == 0) {
                noteItem.setFavorite(1);
            }
            noteItem.setWidgetId(widgetId);
            noteItem.setWritingDate(Utils.dateToString(new Date(System.currentTimeMillis())));
            databaseManager.addItem(noteItem);
        }

        databaseManager.changedFlagToTrue(widgetId, 0);
        appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.page_list_view);

    }

    /**
     * 第一个 widget被添加时调用
     *
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.i(TAG, "note widget onEnabled");
    }

    /**
     * 最后一个 widget被删除时调用
     *
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.i(TAG, "note widget onDisabled");
    }

    /**
     * widget被删除时调用
     *
     * @param context
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.e(TAG, "ondeleted" + appWidgetIds.length);
        DatabaseManager databaseManager = DatabaseManager.getInstance(context);
        for (int appwidgetId : appWidgetIds) {
            Log.e(TAG, "delete widgetId " + appwidgetId);
            databaseManager.deleteItemsByWidgetId(appwidgetId);
        }
    }

}

