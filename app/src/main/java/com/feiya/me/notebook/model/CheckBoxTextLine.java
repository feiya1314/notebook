package com.feiya.me.notebook.model;

import com.feiya.me.notebook.Constant;

public class CheckBoxTextLine {
    private int widgetId;
    private int pageId = 0;
    private int lineId;
    //是否是check box line
    private int checkBoxLine = Constant.NUM_FALSE;
    private int checkBoxSelected = Constant.NUM_FALSE;
    private String lineText;

    public int getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public int getCheckBoxLine() {
        return checkBoxLine;
    }

    public void setCheckBoxLine(int checkBoxLine) {
        this.checkBoxLine = checkBoxLine;
    }

    public int getCheckBoxSelected() {
        return checkBoxSelected;
    }

    public void setCheckBoxSelected(int checkBoxSelected) {
        this.checkBoxSelected = checkBoxSelected;
    }

    public String getLineText() {
        return lineText;
    }

    public void setLineText(String lineText) {
        this.lineText = lineText;
    }
}
