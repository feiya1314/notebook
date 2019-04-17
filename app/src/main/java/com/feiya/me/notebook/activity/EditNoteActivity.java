package com.feiya.me.notebook.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.feiya.me.notebook.Constant;
import com.feiya.me.notebook.R;
import com.feiya.me.notebook.db.DatabaseManager;
import com.feiya.me.notebook.db.IDatabaseManager;
import com.feiya.me.notebook.model.NoteItem;


public class EditNoteActivity extends Activity {
    private final static String TAG = EditNoteActivity.class.getSimpleName();
    private EditText eTitle;
    private EditText eContent;
    private FloatingActionButton btn_save;
    private IDatabaseManager databaseManager;

    private Context mContext;
    private Intent intent;

    private String title;
    private String content;
    private NoteItem noteItem;
    private int pageId;
    private int widgetId;

    private Boolean IsTitleChanged;
    private Boolean IsContentChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.warp_edit_note);
        init();
    }

    private void init() {

        IsTitleChanged = false;
        IsContentChanged = false;

        mContext = this;
        intent = this.getIntent();
        pageId = intent.getIntExtra(Constant.PAGE_ID, 0);
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        Log.d(TAG, "pageId from intent " + pageId);
        Log.d(TAG, "widgetId from intent " + widgetId);

        eTitle = findViewById(R.id.edit_note_title);
        eContent = findViewById(R.id.edit_note_content);
        // linearLayout=(LinearLayout)findViewById(R.id.save_edit_content);
        btn_save = findViewById(R.id.save);

        databaseManager = DatabaseManager.getInstance(mContext);
        noteItem = databaseManager.queryNoteItem(widgetId, pageId);

        title = noteItem.getTitle();
        Log.d(TAG, "init title " + title);
        content = noteItem.getContent();

        eTitle.setText(title);
        eContent.setText(content);

        eTitle.setHint("输入标题");

        eTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                IsTitleChanged = true;
            }
        });
        /*
        eTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        */

        eContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                IsContentChanged = true;
            }
        });

        /*
        eContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
`       */
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IsTitleChanged || IsContentChanged) {
                    saveNote();
                }
                finish();
                //overridePendingTransition(R.anim.zoomin,R.anim.zoomout);
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (IsTitleChanged || IsContentChanged) {
            Dialog dialog = new AlertDialog.Builder(this).
                    setMessage("是否放弃更改？").
                    setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).
                    setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveNote();
                            finish();
                            //overridePendingTransition(R.anim.zoomin,R.anim.zoomout);

                        }
                    }).
                    setNegativeButton("放弃", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            //overridePendingTransition(R.anim.zoomin,R.anim.zoomout);
                        }
                    }).create();
            dialog.show();
        } else {
            super.onBackPressed();
            //overridePendingTransition(R.anim.zoomin,R.anim.zoomout);
        }
    }

    private void saveNote() {
        if (!IsContentChanged && !IsTitleChanged) {
            return;
        }
        if (IsContentChanged && IsTitleChanged) {
            title = eTitle.getText().toString();
            content = eContent.getText().toString();
            databaseManager.updateTitle(widgetId, pageId, title);
            databaseManager.updateContent(widgetId, pageId, content);
        } else if (IsContentChanged) {
            content = eContent.getText().toString();
            databaseManager.updateContent(widgetId, pageId, content);
        } else {
            title = eTitle.getText().toString();
            databaseManager.updateTitle(widgetId, pageId, title);
        }
        databaseManager.changeFlag(widgetId, pageId,true);
        updateWidget(mContext, pageId);
    }

    @Override
    protected void onDestroy() {
        //databaseManager.close();
        super.onDestroy();
    }

    private void updateWidget(Context context, int pageId) {
        //databaseManager.getItem(databaseManager.queryItem(pageId));
        //noteItem.setTitle(eTitle.getText().toString());
        //noteItem.setContent(eContent.getText().toString());
        Intent intent = new Intent(Constant.DATA_CHANGED_ACTION);
        intent.putExtra(Constant.PAGE_ID, pageId);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        Log.e(TAG, "activity send data changed action");
        context.sendBroadcast(intent);
    }
}
