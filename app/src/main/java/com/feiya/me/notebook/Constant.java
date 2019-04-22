package com.feiya.me.notebook;

public interface Constant {

    int DATABASE_VERSION = 1;
    String DATABASE_NAME = "NoteWidget.db";
    String TABLE_NAME = "note";
    String CREATE_NOTE_TABLE = "create table note ("
            + "id integer primary key autoincrement,"
            + "widgetId integer,"
            + "pageId integer,"
            + "currentPage integer,"
            + "title text,"
            + "content text,"
            + "nodeType text,"
            + "favorite integer,"
            + "changedFlag integer,"
            + "deleted integer,"
            + "writingDate text,"
            + "modifyDate text)";

    String NEXT_ACTION = "com.feiya.me.notewidget.NEXT_ACTION";
    String PREVIOUS_ACTION = "com.feiya.me.notewidget.PREVIOUS_ACTION";
    String COLLECTION_VIEW_ACTION = "com.feiya.me.notewidget.COLLECTION_VIEW_ACTION";
    String COLLECTION_VIEW_EXTRA = "com.feiya.me.notewidget.COLLECTION_VIEW_EXTRA";
    String PAGE_ID = "com.feiya.me.notewidget.PAGE_ID";
    String DATA_CHANGED_ACTION = "com.feiya.me.notewidget.DATA_CHANGED_ACTION";
    String INIT_NOTE_TITLE = "在此输入标题";
    String INIT_NOTE_CONTENT = "在此输入记事. 小提示：可通过底部左右箭头翻页！";
    String SPACE = " ";
    String EQ = "=";
    String GT = ">";
    String LT = "<";
    String AND = "AND";
    String OR = "OR";
    String FROM = "FROM";
    String SPACE_LINE = "                                                              ";
    String LAST_CHANGED_TIME = "\n\n\n\n上次修改时间 : ";
    String NEXT_LINE = "\n";
}
