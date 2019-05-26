package com.feiya.me.notebook.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseIntArray;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.feiya.me.notebook.Constant;
import com.feiya.me.notebook.R;
import com.feiya.me.notebook.db.DatabaseManager;
import com.feiya.me.notebook.db.IDatabaseManager;
import com.feiya.me.notebook.model.NoteItem;
import com.feiya.me.notebook.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by feiya on 2019/3/15.
 */
public class RemoteListViewService extends RemoteViewsService {
    private static String contentEnd;
    private static SparseIntArray fillInSpaceLineNum = new SparseIntArray();
    private static final String TAG = RemoteListViewService.class.getSimpleName();
    private static final int pagesCount = 1;

    public RemoteListViewService() {
        Log.i(TAG, "RemoteListViewService was constructed");
    }

    public static void changeFillInSpaceLineNum(int widgetId, int lineNum, boolean delete){
        if(delete) {
            fillInSpaceLineNum.removeAt(widgetId);
            return;
        }
        fillInSpaceLineNum.put(widgetId, lineNum);
    }
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.i(TAG, "onGetViewFactory");
        return new ListViewFactory(this, intent);
    }

    private class ListViewFactory implements RemoteViewsFactory {
        private List<NoteItem> noteItems = new ArrayList<>();
        private IDatabaseManager databaseManager;
        private int mWidgetId;

        public ListViewFactory(Context context, Intent intent) {
            // mWidgetId = Integer.valueOf(intent.getData().getSchemeSpecificPart());
            String type = intent.getType();
            //mWidgetId = Utils.isStringEmpty(type) ? 0 : Integer.valueOf(type);
            mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
            Log.i(TAG, "service created widgetId : " + String.valueOf(mWidgetId));
            Log.i(TAG, "service created type : " + type);

            databaseManager = DatabaseManager.getInstance(context);
        }

        /**
         * Called when your factory is first constructed. The same factory may be shared across
         * multiple RemoteViewAdapters depending on the intent passed.
         * 如果耗时长的任务应该在onDataSetChanged或者getViewAt中处理
         */
        @Override
        public void onCreate() {
            Log.i(TAG, "RemoteListViewService onCreated");
            List<NoteItem> noteItemList = databaseManager.queryNoteList(mWidgetId);
            if (!Utils.isCollectionEmpty(noteItemList)) {
                Log.i(TAG, "RemoteListViewService onCreated  noteItemList : " + noteItemList);
                noteItems = noteItemList;
                return;
            }
            Log.i(TAG, "RemoteListViewService onCreated noteitem is empty");
            for (int i = 0; i < pagesCount; i++) {
                NoteItem noteItem = new NoteItem(Constant.INIT_NOTE_TITLE);
                noteItem.setContent(Constant.INIT_NOTE_CONTENT);
                noteItem.setPageId(i + 1);
                noteItem.setWidgetId(mWidgetId);
                String initDate = Utils.dateToString(new Date(System.currentTimeMillis()));
                noteItem.setWritingDate(initDate);
                noteItem.setModifyDate(initDate);
                databaseManager.insertNoteItem(noteItem);
                noteItems.add(noteItem);
            }
        }

        /**
         * appWidgetManager.notifyAppWidgetViewDataChanged 之后会调用此方法更新页面
         */
        @Override
        public void onDataSetChanged() {
            // get
            Log.i(TAG, "onDataSetChanged");
            //当某一个widget的某一页有改变时，该页的changedFlag状态会改变，通过查询该Flag就可找出要更新的widget
            //NoteItem noteItem = databaseManager.queryChangedNoteItem(mWidgetId);
            //NoteItem noteItem  = databaseManager.queryFirstNoteItem(mWidgetId);
            List<NoteItem> noteItemFromDB = databaseManager.queryNoteList(mWidgetId);
            if (!Utils.isCollectionEmpty(noteItemFromDB) && !noteItemFromDB.equals(noteItems)) {
                noteItems.clear();
                noteItems.addAll(noteItemFromDB);
                Log.i(TAG, "noteItems length ： " + noteItems.size());
                Log.d(TAG, "noteitem : " + noteItems.toArray());
                //databaseManager.changedFlagToFalse();
            }
        }

        /**
         * onDataSetChanged 调用之后也会调用这个，初始化时也会被调用
         *
         * @param position The position of the item within the Factory's data set of the item whose
         *                 view we want.
         * @return A RemoteViews object corresponding to the data at the specified position.RemoteViewsService向widgetprovider传数据
         */
        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notepage);

            Log.i(TAG, "getViewFromService mWidgetId: " + mWidgetId + " position : " + position);
            //remoteViews.setTextViewText(R.id.note_title, noteItems.get(position).getTitle());
            String changedTime = noteItems.get(position).getModifyDate();
            if (Utils.isStringEmpty(changedTime)){
                changedTime = noteItems.get(position).getWritingDate();
            }
            remoteViews.setTextViewText(R.id.note_content, noteItems.get(position).getContent() + getContentEnd(changedTime, mWidgetId));

            Intent collectionIntent = new Intent();
            collectionIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            collectionIntent.putExtra(Constant.PAGE_ID, position + 1);

            //remoteViews.setOnClickFillInIntent(R.id.note_title, collectionIntent);
            remoteViews.setOnClickFillInIntent(R.id.note_content, collectionIntent);

            return remoteViews;
        }

        private String getContentEnd(String lastChangedDate, int widgetId){
            if (Utils.isStringEmpty(contentEnd)){
                contentEnd = getFillInSpace(fillInSpaceLineNum.get(widgetId));
            }
            return Constant.LAST_CHANGED_TIME + lastChangedDate + contentEnd +".";
        }

        private String getFillInSpace(int lineNum){
            StringBuilder sb = new StringBuilder();
            for (int i=0;i<lineNum;i++){
                sb.append(Constant.NEXT_LINE);
            }
            return sb.toString();
        }
        /**
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return pagesCount;
        }

        /**
         * @param position The position of the item within the data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * This allows for the use of a custom loading view which appears between the time that
         * {@link #getViewAt(int)} is called and returns. If null is returned, a default loading
         * view will be used.
         *
         * @return The RemoteViews representing the desired loading view.
         */
        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        /**
         * @return The number of types of Views that will be returned by this factory.
         */
        @Override
        public int getViewTypeCount() {
            return 1;
        }

        /**
         * @return True if the same id always refers to the same object.
         */
        @Override
        public boolean hasStableIds() {
            return false;
        }

        /**
         * Called when the last RemoteViewsAdapter that is associated with this factory is
         * unbound.
         */
        @Override
        public void onDestroy() {
            Log.i(TAG, "onDestroy");
            try {
                databaseManager.close();
            } catch (IOException e) {
                Log.e(TAG, "error occurs when close database ", e);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_REDELIVER_INTENT;
    }
}
