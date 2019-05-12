package com.feiya.me.notebook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.feiya.me.notebook.Constant;
import com.feiya.me.notebook.model.CheckBoxTextLine;
import com.feiya.me.notebook.model.NoteItem;
import com.feiya.me.notebook.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by feiya on 2016/9/27.
 */

public class DatabaseManager implements IDatabaseManager {
    private static final String TAG = DatabaseManager.class.getSimpleName();
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private static volatile IDatabaseManager databaseManager;

    private DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public static IDatabaseManager getInstance(Context context) {
        if (databaseManager == null) {
            synchronized (DatabaseManager.class) {
                if (databaseManager == null) {
                    databaseManager = new DatabaseManager(context);
                }
            }
        }
        return databaseManager;
    }

    private Cursor getCheckBoxLine(int widgetId, int pageId){
        String sql = new SqlStringBuilder().select().withColumns(null).table(Constant.TABLE_WIDGET_CHECKBOXLINE).where()
                .columnEqValue("widgetId",String.valueOf(widgetId))
                .and().columnEqValue("pageId",String.valueOf(pageId)).build();
        return db.rawQuery(sql,null);
    }
    private Cursor getItemCursorByWid(int widgetId) {
        String sql = new SqlStringBuilder().select().withColumns(null).table(Constant.TABLE_WIDGET_NAME).where().columnEqValue("widgetId", String.valueOf(widgetId)).build();
        //String sql = "SELECT * FROM " + Constant.TABLE_WIDGET_NAME + " WHERE widgetId=" + widgetId;
        return db.rawQuery(sql, null);
    }

    @Override
    public long insertNoteItem(NoteItem noteItem) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", noteItem.getTitle());
        contentValues.put("content", noteItem.getContent());
        contentValues.put("nodeType", noteItem.getNodeType());
        contentValues.put("widgetId", noteItem.getWidgetId());
        contentValues.put("changedFlag", noteItem.getChangedFlag());
        contentValues.put("pageId", noteItem.getPageId());
        contentValues.put("writingDate", noteItem.getWritingDate());
        contentValues.put("modifyDate", noteItem.getModifyDate());
        contentValues.put("favorite", noteItem.getFavorite());
        contentValues.put("deleted", noteItem.getDeleted());
        db.beginTransaction();
        db.insert(Constant.TABLE_WIDGET_NAME, null, contentValues);
        insertCheckBoxLineList(noteItem.getTextLines());
        db.endTransaction();
        //todo 优化返回值
        return 1L;
    }

    private void insertCheckBoxLineList(List<CheckBoxTextLine> lines) {
        for (CheckBoxTextLine line : lines) {
            insertCheckBoxLine(line);
        }
    }

    private long insertCheckBoxLine(CheckBoxTextLine checkBoxTextLine) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("widgetId", checkBoxTextLine.getWidgetId());
        contentValues.put("pageId", checkBoxTextLine.getPageId());
        contentValues.put("lineId", checkBoxTextLine.getLineId());
        contentValues.put("checkBoxLine", checkBoxTextLine.getCheckBoxLine());
        contentValues.put("checkBoxSelected", checkBoxTextLine.getCheckBoxSelected());
        contentValues.put("lineText", checkBoxTextLine.getLineText());

        return db.insert(Constant.TABLE_WIDGET_NAME, null, contentValues);
    }

    public List<CheckBoxTextLine> queryCheckBoxLine(int widgetId, int pageId){
        Cursor cursor = getCheckBoxLine(widgetId,pageId);
        List<CheckBoxTextLine> lines = new ArrayList<>();
        if (cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                lines.add(getCheckBoxLineFromCursor(cursor));
            }
        }
        cursor.close();
        return lines;
    }
    @Override
    public List<NoteItem> queryNoteList(int widgetId) {
        Cursor cursor = getItemCursorByWid(widgetId);
        List<NoteItem> noteItems = new ArrayList<>();
        if (cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                noteItems.add(getNoteItemFromCursor(cursor));
            }
        }
        cursor.close();
        return noteItems;
    }

    @Override
    public int updateNoteItem(NoteItem noteItem, int widgetId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", noteItem.getTitle());
        contentValues.put("content", noteItem.getContent());
        contentValues.put("modifyDate", noteItem.getModifyDate());
        //todo 完善此方法
        return db.update(Constant.TABLE_WIDGET_NAME, contentValues, "widgetId=? ", new String[]{String.valueOf(widgetId)});
    }


    @Override
    public int updateTitle(int widgetId, int pageId, String title) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("modifyDate", Utils.dateToString(new Date(System.currentTimeMillis())));
        String whereClause = "widgetId=? and pageId=?";
        return db.update(Constant.TABLE_WIDGET_NAME, contentValues, whereClause, new String[]{String.valueOf(widgetId), String.valueOf(pageId)});
    }

    @Override
    public int updateContent(int widgetId, int pageId, String content) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("content", content);
        contentValues.put("modifyDate", Utils.dateToString(new Date(System.currentTimeMillis())));
        String whereClause = "widgetId=? and pageId=?";
        return db.update(Constant.TABLE_WIDGET_NAME, contentValues, whereClause, new String[]{String.valueOf(widgetId), String.valueOf(pageId)});
    }

    @Override
    public void deleteNoteItemByWId(int widgetId) {
        String sql = "DELETE FROM " + Constant.TABLE_WIDGET_NAME + " WHERE widgetId=" + widgetId;
        db.execSQL(sql);
    }

    @Override
    public NoteItem queryFirstNoteItem(int widgetId) {
        Cursor cursor = getItemCursorByWid(widgetId);
        if (cursor.getCount() == 0) {
            return null;
        }
        if (cursor.getCount() > 1) {
            Log.w(TAG, "note item count is 0 or >1");
        }
        cursor.moveToFirst();
        NoteItem noteItem = getNoteItemFromCursor(cursor);
        cursor.close();

        return noteItem;
    }

    @Override
    public NoteItem queryNoteItem(int widgetId, int pageId) {
        String sql = "SELECT * FROM " + Constant.TABLE_WIDGET_NAME + " WHERE widgetId=" + widgetId + " and pageId=" + pageId;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            return null;
        }
        if (cursor.getCount() > 1) {
            Log.w(TAG, "note item count by widgetId and pageId is 0 or >1");
        }
        cursor.moveToFirst();
        NoteItem noteItem = getNoteItemFromCursor(cursor);
        cursor.close();
        return noteItem;
    }

    private CheckBoxTextLine getCheckBoxLineFromCursor(Cursor cursor){
        CheckBoxTextLine checkBoxTextLine = new CheckBoxTextLine();
        //checkBoxTextLine.setWidgetId(cursor.getInt(cursor.getColumnIndex("widgetId")));
        //checkBoxTextLine.setPageId(cursor.getInt(cursor.getColumnIndex("pageId")));
        checkBoxTextLine.setLineId(cursor.getInt(cursor.getColumnIndex("lineId")));
        checkBoxTextLine.setCheckBoxLine(cursor.getInt(cursor.getColumnIndex("checkBoxLine")));
        checkBoxTextLine.setCheckBoxLine(cursor.getInt(cursor.getColumnIndex("checkBoxSelected")));
        checkBoxTextLine.setLineText(cursor.getString(cursor.getColumnIndex("lineText")));
        return checkBoxTextLine;
    }
    private NoteItem getNoteItemFromCursor(Cursor cursor) {
        NoteItem noteItem = new NoteItem();
        noteItem.setPageId(cursor.getInt(cursor.getColumnIndex("pageId")));
        noteItem.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        noteItem.setWidgetId(cursor.getInt(cursor.getColumnIndex("widgetId")));
        noteItem.setContent(cursor.getString(cursor.getColumnIndex("content")));
        noteItem.setFavorite(cursor.getInt(cursor.getColumnIndex("favorite")));
        noteItem.setChangedFlag(cursor.getInt(cursor.getColumnIndex("changedFlag")));
        noteItem.setWritingDate(cursor.getString(cursor.getColumnIndex("writingDate")));
        noteItem.setModifyDate(cursor.getString(cursor.getColumnIndex("modifyDate")));
        noteItem.setCurrentPage(cursor.getInt(cursor.getColumnIndex("currentPage")));
        noteItem.setDeleted(cursor.getInt(cursor.getColumnIndex("deleted")));

        List<CheckBoxTextLine> lines = queryCheckBoxLine(noteItem.getWidgetId(),noteItem.getPageId());
        noteItem.setTextLines(lines);
        return noteItem;
    }

    @Override
    public void changeFlag(int widgetId, int pageId, boolean flag) {
        String sql = flag ? "UPDATE " + Constant.TABLE_WIDGET_NAME + " SET changedFlag=1 WHERE widgetId = " + widgetId + " and pageId=" + pageId :
                "UPDATE " + Constant.TABLE_WIDGET_NAME + " SET changedFlag=0 WHERE widgetId = " + widgetId + " and pageId=" + pageId;

        db.execSQL(sql);
    }

    @Override
    public NoteItem queryChangedNoteItem(int widgetId) {
        String sql = "SELECT * FROM " + Constant.TABLE_WIDGET_NAME + " WHERE widgetId= " + widgetId + " and changedFlag=1";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        return getNoteItemFromCursor(cursor);
    }

    @Override
    public NoteItem queryCurrentPage(int widgetId) {
        return null;
    }

    @Override
    public void changeCurrentPage(int widget, int pageId) {

    }

    @Override
    public synchronized void close() {
        Log.i(TAG, "closeDB");
        //没有必要关闭，内核会去处理
        //db.close();
    }
}
