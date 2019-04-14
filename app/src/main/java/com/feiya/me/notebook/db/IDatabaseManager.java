package com.feiya.me.notebook.db;

import com.feiya.me.notebook.model.NoteItem;

import java.io.Closeable;
import java.util.List;

public interface IDatabaseManager extends  Closeable {

    long insertNoteItem(NoteItem noteItem);

    int updateNoteItem(NoteItem noteItem, int widgetId);

    void deleteNoteItemByWId(int widgetId);

    NoteItem queryNoteItem(int widgetId);

    List<NoteItem> queryNoteList(int widgetId);

    void changeFlag(int widgetId, boolean flag);

    NoteItem queryCurrentPage(int widgetId);

    void changeCurrentPage(int widget, int pageId);

    NoteItem queryChangedNoteItem(int widgetId);
}
