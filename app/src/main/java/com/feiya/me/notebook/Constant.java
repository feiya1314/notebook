package com.feiya.me.notebook;

public class Constant {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NoteWidget.db";
    public static final String TABLE_WIDGET_NAME = "tb_widget_note";
    public static final String TABLE_WIDGET_CHECKBOXLINE = "tb_widget_checkboxline";
    public static final String CREATE_NOTE_TABLE = "create table tb_widget_note ("
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

    public static final String CREATE_CHECKBOX_LINE_TABLE = "create table tb_widget_checkboxline ("
            + "widgetId integer,"
            + "pageId integer,"
            + "lineId integer,"
            + "checkBoxLine integer,"
            + "checkBoxSelected integer,"
            + "lineText text,"
            + "PRIMARY KEY(widgetId,pageId,lineId)" +
            ")";

    public static final String NEXT_ACTION = "com.feiya.me.notewidget.NEXT_ACTION";
    public static final String PREVIOUS_ACTION = "com.feiya.me.notewidget.PREVIOUS_ACTION";
    public static final String COLLECTION_VIEW_ACTION = "com.feiya.me.notewidget.COLLECTION_VIEW_ACTION";
    public static final String COLLECTION_VIEW_EXTRA = "com.feiya.me.notewidget.COLLECTION_VIEW_EXTRA";
    public static final String LINE_CHECK_BOX_ACTION = "com.feiya.me.notewidget.LINE_CHECK_BOX_ACTION";
    public static final String PAGE_ID = "com.feiya.me.notewidget.PAGE_ID";
    public static final String CHECK_BOX = "com.feiya.me.notewidget.CHECKBOX";
    public static final String DATA_CHANGED_ACTION = "com.feiya.me.notewidget.DATA_CHANGED_ACTION";
    public static final String INIT_NOTE_TITLE = "在此输入标题";
    public static final String INIT_NOTE_CONTENT = "在此输入记事. 小提示：可通过底部左右箭头翻页！";
    public static final String SPACE = " ";
    public static final String EQ = "=";
    public static final String GT = ">";
    public static final String LT = "<";
    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String FROM = "FROM";
    public static final String SPACE_LINE = "                                                              ";
    public static final String LAST_CHANGED_TIME = "\n\n\n\n上次修改时间 : ";
    public static final String NEXT_LINE = "\n";
    public static final int NUM_TRUE = 1;
    public static final int NUM_FALSE = 0;
}
