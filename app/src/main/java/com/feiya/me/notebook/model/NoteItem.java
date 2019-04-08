package com.feiya.me.notebook.model;

/**
 * Created by feiya on 2016/9/17.
 */
public class NoteItem {

    private String content;
    private String writingDate;
    private String title;
    private int pageId;
    private int favorite = 0;
    private int widgetId;
    private int changedFlag = 0;
    private int deleted = 0;


    public NoteItem() {

    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public NoteItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWritingDate() {
        return writingDate;
    }

    public void setWritingDate(String writingDate) {
        this.writingDate = writingDate;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int id) {
        this.pageId = id;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
    }

    public int getWidgetId() {
        return this.widgetId;
    }

    public int getChangedFlag() {
        return changedFlag;
    }

    public void setChangedFlag(int changedFlag) {
        this.changedFlag = changedFlag;
    }
}
