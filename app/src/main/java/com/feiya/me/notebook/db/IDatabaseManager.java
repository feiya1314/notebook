package com.feiya.me.notebook.db;

import com.feiya.me.notebook.model.NoteItem;

import java.io.Closeable;
import java.util.List;

public interface IDatabaseManager extends  Closeable {

    long insertNoteItem(NoteItem noteItem);

    int updateNoteItem(NoteItem noteItem, int widgetId);

    int updateTitle(int widgetId, int pageId, String title);

    int updateContent(int widgetId, int pageId, String content);

    void deleteNoteItemByWId(int widgetId);

    NoteItem queryFirstNoteItem(int widgetId);

    NoteItem queryNoteItem(int widgetId, int pageId);

    List<NoteItem> queryNoteList(int widgetId);

    void changeFlag(int widgetId, int pageId, boolean flag);

    NoteItem queryCurrentPage(int widgetId);

    void changeCurrentPage(int widget, int pageId);

    NoteItem queryChangedNoteItem(int widgetId);
}
