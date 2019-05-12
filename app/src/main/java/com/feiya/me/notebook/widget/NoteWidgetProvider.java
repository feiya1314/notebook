package com.feiya.me.notebook.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.widget.RemoteViews;

import com.feiya.me.notebook.Constant;
import com.feiya.me.notebook.R;
import com.feiya.me.notebook.activity.EditNoteActivity;
import com.feiya.me.notebook.db.DatabaseManager;
import com.feiya.me.notebook.db.IDatabaseManager;
import com.feiya.me.notebook.service.RemoteListViewService;
import com.feiya.me.notebook.utils.Utils;

/**
 * Implementation of App Widget functionality.
 */
public class NoteWidgetProvider extends AppWidgetProvider {
    private static SparseIntArray fillInSpaceLineInitNum = new SparseIntArray();
    private static final String TAG = NoteWidgetProvider.class.getSimpleName();
    private IDatabaseManager databaseManager;
    private int pagesCount = 10;
    private int contentTextSize = 13;
    private boolean enabled = true;

    /*private Map<Integer, Object> initWidgetMap = new HashMap<>();*/

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(TAG, "Widget Provider onUpdate");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            //initWidgetDatabase(context, appWidgetManager, appWidgetId);
            /*if (initWidgetMap.get(appWidgetId) != null) {
                continue;
            }*/
            Log.i(TAG, "Widget :" + appWidgetId + " onUpdate enable : " + enabled);
            registerListener(context, appWidgetManager, appWidgetId);
            if (enabled) {
                int num = calculateLineNum(context,appWidgetManager, appWidgetId);
                Log.d(TAG, "calculateLineNum appWidgetId : " + appWidgetId + " num : " + num);
                RemoteListViewService.changeFillInSpaceLineNum(appWidgetId, num, false);
                enabled = false;
            }
        }
    }

    private void registerListener(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.list_view_adapter);
        Intent listViewServiceIntent = new Intent(context, RemoteListViewService.class);
        listViewServiceIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        //listViewServiceIntent.setData(Uri.fromParts("content",String.valueOf(appWidgetId),null));
        //RemoteViewsService 调用onGetViewFactory的时候，如果intent 的 action, data, type, class, and categories
        //与之前的相同，则不会重新调用,所以使用上面的setData 与setType效果一样
        listViewServiceIntent.setType(String.valueOf(appWidgetId));
        listViewServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
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
        int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        int pageId = intent.getIntExtra(Constant.PAGE_ID, 0);
        String cone = intent.getStringExtra("CONTENT");
        String cone3 = intent.getStringExtra("BOXCHECK");
        Log.i(TAG, "provider onReceive action : " + action + " widgetId : " + widgetId + " pageId : " + pageId + "   cone："+cone +"   BOXCHECK："+cone3);

        switch (action) {
            case Constant.COLLECTION_VIEW_ACTION: {
                Intent startActivity = new Intent(context, EditNoteActivity.class);
                startActivity.putExtra(Constant.PAGE_ID, pageId);
                startActivity.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                //由于不是在activity中启动另一个activity，而是由context启动，需要设置FLAG_ACTIVITY_NEW_TASK标志
                //表明启动一个activity
                startActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startActivity);
                break;
            }
            //case
            case Intent.ACTION_SHUTDOWN: {
                Log.d(TAG, "shutdown");
                //databaseManager.topPageInit();
                break;
            }
            default:
                break;
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        //appWidgetManager.getAppWidgetOptions(appWidgetId).
        //appWidgetManager.getAppWidgetInfo(appWidgetId).
        //todo  update changeFillInSpaceLineNum
        //RemoteListViewService.changeFillInSpaceLineNum(15);
        int lineNum = calculateLineNum(context, appWidgetManager, appWidgetId);
        Log.d(TAG, "onAppWidgetOptionsChanged : lineNum : " + lineNum);
        RemoteListViewService.changeFillInSpaceLineNum(appWidgetId, lineNum, false);
        //Bundle bundle = appWidgetManager.getAppWidgetOptions(appWidgetId);
        /*int oldminW = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int oldmaxW = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int oldminH = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int oldmaxH = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);*/

        //Log.d(TAG, "oldmin wid:" + oldminW + "oldmax wid:" + oldmaxW + "oldmin h:" + oldminH + "oldmax h:" + oldmaxH);

        /*int minW = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int maxW = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int minH = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxH = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);*/

        //Log.d(TAG, "min wid:" + minW + "max wid:" + maxW + "min h:" + minH + "max h:" + maxH);
    }

    private int getTopPageId(Context context, DatabaseManager databaseManager, int widgetId) {
       /* NoteItem noteItem;
        noteItem = databaseManager.getItem(databaseManager.queryTopPageItem(widgetId));
        Log.e(TAG, "topPageId " + noteItem.getPageId());
        databaseManager.topPageToFalse(widgetId);
        return noteItem.getPageId();*/
        return 0;
    }

    private int calculateLineNum(Context context,AppWidgetManager appWidgetManager, int appWidgetId) {

        Bundle bundle = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int maxH = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        int minW = bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

        //int dpSize = Utils.sp2dp(context,contentTextSize);
        Log.d(TAG, "min wid: " + minW + " max h: " + maxH);
        //int lineTextNum = minW/contentTextSize;

        return maxH / contentTextSize -5;
    }

    /**
     * 第一个 widget被添加时调用
     *
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        databaseManager = DatabaseManager.getInstance(context);
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
        IDatabaseManager databaseManager = DatabaseManager.getInstance(context);
        for (int appwidgetId : appWidgetIds) {
            Log.e(TAG, "delete widgetId " + appwidgetId);
            databaseManager.deleteNoteItemByWId(appwidgetId);
            RemoteListViewService.changeFillInSpaceLineNum(appwidgetId, 0, true);
        }
    }

}

