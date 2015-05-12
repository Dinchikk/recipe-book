package com.example.dns.rasm;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FindNameActivity extends Activity {
    private static final String DATABASE_NAME    = "Recepts.db";
    private static final String DATABASE_TABLE_R = "Recept";
    private static final String DATABASE_TABLE_I = "Ingredients";
    private static final String DATABASE_TABLE_P = "Products";
    private static final int DATABASE_VERSION = 1;

    EditText name;
    ListView lst_rez_name;

    private DBHelper dbHelper;
    private static SQLiteDatabase myReceptDB;
    private ArrayList<Map<String, Object>> products;
    private static Context cont;

    String[] columns =  { "recept_name"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poisk1);
        cont = this;

        name =  (EditText) findViewById(R.id.EV_input_name);
        lst_rez_name =  (ListView ) findViewById(R.id.LV_res);

        dbHelper = new DBHelper(this);
        myReceptDB = dbHelper.getWritableDatabase();

        String tmp = name.getText().toString();
        show_find_recipes(tmp);


        name.addTextChangedListener(new TextWatcher(){

            @Override
            public void afterTextChanged(Editable arg0) {
                show_find_recipes(arg0.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {}
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {}
        });

        lst_rez_name.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v,	int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("name_recipe", ((TextView)v).getText());
                setResult(RESULT_OK, intent);
                finish();
            }

        });

    }
    private void show_find_recipes(String find_string) {
        Cursor c;
        if (find_string.length() == 0){
            c = myReceptDB.query(DATABASE_TABLE_R, columns, null, null, null, null, null);
        }else{
            String[] str_arr = new String[1];
            str_arr[0] = "%" + find_string + "%";
            c = myReceptDB.query(DATABASE_TABLE_R, columns, "recept_name LIKE ?", str_arr, null, null, null);
        }

        final ArrayList<String> recepts = new ArrayList<String>();
        if (c.moveToFirst()) {
            for(int i = 0; i < c.getCount(); i++){
                recepts.add(c.getString(c.getColumnIndex("recept_name")));
                c.moveToNext();
            }
        }
        final ArrayAdapter<String> find;
        find = new ArrayAdapter<String>(cont,android.R.layout.simple_list_item_1, recepts);
        lst_rez_name.setAdapter(find);

    }
    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // создаем таблицу с полями
            db.execSQL("create table " + DATABASE_TABLE_R + "("
                    + "id integer primary key autoincrement,"
                    + "recept_name text not null,"
                    + "recept text not null,"
                    + "time_prep integer,"
                    + "time_timer integer,"
                    + "image_res text not null" + ");");

            db.execSQL("create table " + DATABASE_TABLE_I + "("
                    + "id integer primary key autoincrement,"
                    + "id_recept long,"
                    + "product_name text not null,"
                    + "product_kol text not null" + ");");

            db.execSQL("create table " + DATABASE_TABLE_P + "("
                    + "id integer primary key autoincrement,"
                    + "product_id integer,"
                    + "product_name text not null" + ");");

        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE_R);
            db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE_P);
            onCreate(db);
        }
    }
} 