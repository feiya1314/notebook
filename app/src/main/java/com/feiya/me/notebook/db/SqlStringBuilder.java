package com.feiya.me.notebook.db;

import android.util.Log;

import com.feiya.me.notebook.utils.Utils;

public class SqlStringBuilder {
    private static final String TAG = SqlStringBuilder.class.getSimpleName();
    private StringBuilder sb = new StringBuilder();
    private static String SPACE = " ";
    private static String EQ = "=";
    private static String GT = ">";
    private static String LT = "<";
    private static String AND = "AND";
    private static String OR = "OR";
    private static String FROM = "FROM";


    public SqlStringBuilder getBuilder(){
        return new SqlStringBuilder();
    }

    public SqlStringBuilder select()
    {
        sb.append("select ");
        return this;
    }

    public SqlStringBuilder withColumns(String[] columns){
        if (Utils.isArrayEmpty(columns)) {
            sb.append("* ");
            return this;
        }

        for (String column : columns){
            sb.append(column);
            sb.append(SPACE);
        }
        return this;
    }

    public SqlStringBuilder table(String table) {
        sb.append(FROM);
        sb.append(SPACE);
        sb.append(table);
        sb.append(SPACE);
        return this;
    }

    public SqlStringBuilder where(){
        sb.append("WHERE ");
        return this;
    }

    public SqlStringBuilder columnEqValue(String column, String value){
        sb.append(column);
        sb.append(EQ);
        sb.append(value);
        sb.append(SPACE);
        return this;
    }

    public SqlStringBuilder and(){
        sb.append(AND);
        sb.append(SPACE);
        return this;
    }

    public SqlStringBuilder or(){
        sb.append(OR);
        sb.append(SPACE);
        return this;
    }

    public String build(){
        String sql = sb.toString();
        Log.d(TAG, "sql string : " + sql);
        return sql;
    }
}
