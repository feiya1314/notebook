package com.feiya.me.notebook;

public interface Constant {

    int DATABASE_VERSION = 1;
    String DATABASE_NAME = "NoteWidget.db";
    String TABLE_WIDGET_NAME = "tb_widget_note";
    String TABLE_WIDGET_CHECKBOXLINE = "tb_widget_checkboxline";
    String CREATE_NOTE_TABLE = "create table tb_widget_note ("
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

    String CREATE_CHECKBOX_LINE_TABLE = "create table tb_widget_checkboxline ("
            + "widgetId integer,"
            + "pageId integer,"
            + "lineId integer,"
            + "checkBoxLine integer,"
            + "checkBoxSelected text,"
            + "lineText text,"
            + "PRIMARY KEY(widgetId,pageId,lineId)" +
            ")";

    String NEXT_ACTION = "com.feiya.me.notewidget.NEXT_ACTION";
    String PREVIOUS_ACTION = "com.feiya.me.notewidget.PREVIOUS_ACTION";
    String COLLECTION_VIEW_ACTION = "com.feiya.me.notewidget.COLLECTION_VIEW_ACTION";
    String COLLECTION_VIEW_EXTRA = "com.feiya.me.notewidget.COLLECTION_VIEW_EXTRA";
    String LINE_CHECK_BOX_ACTION = "com.feiya.me.notewidget.LINE_CHECK_BOX_ACTION";
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
    int NUM_TRUE = 1;
    int NUM_FALSE = 0;
}
