package com.feiya.me.notebook.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.util.SparseIntArray;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.feiya.me.notebook.Constant;
import com.feiya.me.notebook.R;
import com.feiya.me.notebook.db.DatabaseManager;
import com.feiya.me.notebook.db.IDatabaseManager;
import com.feiya.me.notebook.model.CheckBoxTextLine;
import com.feiya.me.notebook.model.NoteItem;
import com.feiya.me.notebook.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by feiya on 2019/3/15.
 */
public class RemoteListViewService extends RemoteViewsService {
    private static SparseIntArray fillInSpaceLineNum = new SparseIntArray();
    private static final String TAG = RemoteListViewService.class.getSimpleName();
    private static final int pagesCount = 1;

    public RemoteListViewService() {
        Log.d(TAG, "RemoteListViewService was constructed");
    }

    public static void changeFillInSpaceLineNum(int widgetId, int lineNum, boolean delete) {
        Log.d(TAG, "changeFillInSpaceLineNum was called");
        if (delete) {
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
                noteItem.setTextLines(initItem(mWidgetId, noteItem.getPageId()));
                databaseManager.insertNoteItem(noteItem);
                noteItems.add(noteItem);
            }
        }

        private List<CheckBoxTextLine> initItem(int mWidgetId, int pageId) {
            List<CheckBoxTextLine> lines = new ArrayList<>(3);
            CheckBoxTextLine line1 = new CheckBoxTextLine();
            line1.setCheckBoxLine(Constant.NUM_FALSE);
            line1.setCheckBoxSelected(Constant.NUM_FALSE);
            line1.setLineId(1);
            line1.setWidgetId(mWidgetId);
            line1.setPageId(pageId);
            line1.setLineText(Constant.INIT_NOTE_CONTENT);
            lines.add(line1);

            CheckBoxTextLine line2 = new CheckBoxTextLine();
            line2.setCheckBoxLine(Constant.NUM_TRUE);
            line2.setCheckBoxSelected(Constant.NUM_FALSE);
            line2.setLineId(1);
            line2.setWidgetId(mWidgetId);
            line2.setPageId(pageId);
            line2.setLineText(Constant.INIT_NOTE_CONTENT);
            lines.add(line2);

            CheckBoxTextLine line3 = new CheckBoxTextLine();
            line3.setCheckBoxLine(Constant.NUM_TRUE);
            line3.setCheckBoxSelected(Constant.NUM_TRUE);
            line3.setLineId(1);
            line3.setWidgetId(mWidgetId);
            line3.setPageId(pageId);
            line3.setLineText(Constant.INIT_NOTE_CONTENT);
            lines.add(line3);
            return lines;
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

            NoteItem noteItem = noteItems.get(position);
            int itemPos = position +1;
            Log.i(TAG, "getViewFromService mWidgetId: " + mWidgetId + " position : " + position);
            remoteViews.setTextViewText(R.id.note_title, noteItem.getTitle());
            String changedTime = noteItem.getModifyDate();
            if (Utils.isStringEmpty(changedTime)) {
                changedTime = noteItem.getWritingDate();
            }
            //remoteViews.setTextViewText(R.id.note_content, noteItems.get(position).getContent() + getContentEnd(changedTime, mWidgetId));

            /*RemoteViews singleLineView = new RemoteViews(getPackageName(), R.layout.single_note_line);
            singleLineView.setInt(R.id.singleLineText, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG);
            singleLineView.setCharSequence(R.id.singleLineText, "setText", "ttttttttttttttttttttttttttttttttttttttttttttttttttttttttt");
            //为imageview设置drable
            singleLineView.setImageViewResource(R.id.singleLineCheckBox, R.drawable.ic_circlip);

            RemoteViews singleLineView2 = new RemoteViews(getPackageName(), R.layout.single_note_line);
            singleLineView2.setInt(R.id.singleLineText, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG);
            singleLineView2.setCharSequence(R.id.singleLineText, "setText", "ttttttttttttttttt爱说大话哈哈说的话哈和很大声的啊tttttttttttttttttttttt");
            //为imageview设置drable
            singleLineView2.setImageViewResource(R.id.singleLineCheckBox, R.drawable.ic_circlip);*/

            for (CheckBoxTextLine checkBoxTextLine : noteItem.getTextLines()) {
                RemoteViews singleLineView = new RemoteViews(getPackageName(), R.layout.single_note_line);
                singleLineView.setCharSequence(R.id.singleLineText, "setText", checkBoxTextLine.getLineText());
                if (checkBoxTextLine.getCheckBoxLine() == Constant.NUM_TRUE) {
                    //为checkbox设置是否选中 和 下划线
                    if (checkBoxTextLine.getCheckBoxSelected() == Constant.NUM_TRUE) {
                        singleLineView.setInt(R.id.singleLineText, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG);
                        singleLineView.setImageViewResource(R.id.singleLineCheckBox, R.drawable.ic_circle);
                    }else {
                        singleLineView.setImageViewResource(R.id.singleLineCheckBox, R.drawable.ic_circlip);
                    }
                }

                Intent checkBoxIntent = new Intent();
                checkBoxIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                checkBoxIntent.putExtra(Constant.PAGE_ID, itemPos);
                checkBoxIntent.putExtra("CHECK_BOX", "checkbox");

                remoteViews.addView(R.id.page1, singleLineView);
                remoteViews.setOnClickFillInIntent(R.id.singleLineCheckBox, checkBoxIntent);
            }

            RemoteViews endLineView = new RemoteViews(getPackageName(), R.layout.single_note_line);
            //endLineView.setInt(R.id.singleLineText, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG);
            endLineView.setCharSequence(R.id.singleLineText, "setText", getContentEnd(changedTime, mWidgetId));

            /*Intent collectionIntent = new Intent();
            collectionIntent.setAction("CONTENT");
            collectionIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            collectionIntent.putExtra(Constant.PAGE_ID, position + 1);
            collectionIntent.putExtra("CONTENT", "CONTENT");*/




            //singleLineView.setOnClickFillInIntent(R.id.singleLineCheckBox,collectionIntent2);
           /* remoteViews.addView(R.id.page1, singleLineView);
            remoteViews.addView(R.id.page1, singleLineView2);*/

            Intent collectionEndLineIntent = new Intent();

            collectionEndLineIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            collectionEndLineIntent.putExtra(Constant.PAGE_ID, itemPos);
            collectionEndLineIntent.putExtra("ENDLINE", "ENDLINE");

            remoteViews.addView(R.id.page1, endLineView);
            remoteViews.setOnClickFillInIntent(R.id.singleLineText, collectionEndLineIntent);

            Intent collectionIntent3 = new Intent();
            collectionIntent3.setAction("TITLE");
            collectionIntent3.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            collectionIntent3.putExtra(Constant.PAGE_ID, itemPos);

            Intent collectionIntent2 = new Intent();
            collectionIntent2.setAction(Constant.LINE_CHECK_BOX_ACTION);
            collectionIntent2.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            collectionIntent2.putExtra(Constant.PAGE_ID, itemPos);
            collectionIntent2.putExtra("BOXCHECK", "checkbox");

            //remoteViews.setOnClickFillInIntent(R.id.singleLineCheckBox, collectionIntent2);
            remoteViews.setOnClickFillInIntent(R.id.note_title, collectionIntent3);
            //remoteViews.setOnClickFillInIntent(R.id.note_content, collectionIntent);

            return remoteViews;
        }

        private String getContentEnd(String lastChangedDate, int widgetId) {
            int lineNum = fillInSpaceLineNum.get(widgetId);
            Log.d(TAG, "lineNum : " + lineNum);
            return Constant.LAST_CHANGED_TIME + lastChangedDate + getFillInSpace(lineNum) + ".";
        }

        private String getFillInSpace(int lineNum) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < lineNum; i++) {
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
