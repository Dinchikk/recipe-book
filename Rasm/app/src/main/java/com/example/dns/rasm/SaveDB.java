package com.example.dns.rasm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SaveDB {

    private static final String DATABASE_NAME    = "lastRecept.db";
    private static final String DATABASE_TABLE_R = "Recept";
    private static final String DATABASE_TABLE_P = "Products";
    private static final String DATABASE_TABLE_I = "Ingredients";
    private static final int DATABASE_VERSION = 1;

    private static final String RECEPT_NAME = "Каша манная";
    private static final String RECEPT_OPIS = "Вскипятить молоко. Добавить сахар. Помешивая всыпать крупу";
    private static final int RECEPT_TIME = 10;
    private static final int TIME_FOR_TIMER = 2;
    private static final String PRODUCT_NAME_1 = "Крупа манная";
    private static final String PRODUCT_KOL_1 = "100 гр";
    private static final String PRODUCT_NAME_2 = "Молоко";
    private static final String PRODUCT_KOL_2 = "500 мл";

    final String ATTRIBUTE_NAME_PROD = "product";
    final String ATTRIBUTE_NAME_KOL = "prod_kol";

    private DBHelper dbHelper;
    private SQLiteDatabase myDB;
    private Context mainContext;
    private int imageRes = 0;

    public String recept_name = "";
    public String recept_text = "";
    public int time_prep = 0;
    public int timeforTimer = 0;
    public int image_res = 0;
    public String id_recipe = "";
    public ArrayList<Map<String, Object>> products;

    //  Конструкторы
    public SaveDB(Context context){
        mainContext = context;

        dbHelper = new DBHelper(mainContext);
        myDB = dbHelper.getWritableDatabase();

        Cursor c = myDB.query(DATABASE_TABLE_R, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            recept_text = c.getString(c.getColumnIndex("recept"));
            recept_name = c.getString(c.getColumnIndex("recept_name"));
            time_prep = c.getInt(c.getColumnIndex("time_prep"));
            timeforTimer = c.getInt(c.getColumnIndex("time_timer"));
            image_res = c.getInt(c.getColumnIndex("image_res"));
        }

        c = myDB.query(DATABASE_TABLE_P, null, null, null, null, null, null);
        products = new ArrayList<Map<String, Object>>(c.getCount());

        if (c.moveToFirst()) {
            Map<String, Object> m;
            for(int i = 0; i < c.getCount(); i++){
                m = new HashMap<String, Object>();
                m.put(ATTRIBUTE_NAME_PROD, c.getString(c.getColumnIndex("product")));
                m.put(ATTRIBUTE_NAME_KOL, c.getString(c.getColumnIndex("prod_kol")));
                products.add(m);
                c.moveToNext();
            }
        }


    }

    public void save_recept(String _recept_name, String _recept_text, int _time_prep, int _timeforTimer,
                            int _image_res, ContentValues[] _products){
        recept_name = _recept_name;
        recept_text = _recept_text;
        time_prep = _time_prep;
        timeforTimer = _timeforTimer;
        image_res = _image_res;

        int rew_r = myDB.delete(DATABASE_TABLE_R, null, null);
        int rew_p = myDB.delete(DATABASE_TABLE_P, null, null);

        ContentValues cv = new ContentValues();

        cv.put("recept_name", recept_name);
        cv.put("recept", recept_text);
        cv.put("time_prep", time_prep);
        cv.put("time_timer", timeforTimer);
        cv.put("image_res", image_res);

        myDB.insert(DATABASE_TABLE_R, null, cv);

        for(int i = 0; i < _products.length; i++){
            myDB.insert(DATABASE_TABLE_P, null, _products[i]);
        }
    }
    public void save_recipe(String _name_recipe) {

        int rew_r = myDB.delete(DATABASE_TABLE_R, null, null);
        int rew_p = myDB.delete(DATABASE_TABLE_P, null, null);

        DBRHelper dbrHelper = new DBRHelper(mainContext);
        SQLiteDatabase RecipeDB = dbrHelper.getWritableDatabase();

        String[] str_arr = new String[1];
        str_arr[0] = _name_recipe;

        Cursor c = RecipeDB.query(DATABASE_TABLE_R, null, "recept_name LIKE ?", str_arr, null, null, null);

        if (c.moveToFirst()) {
            recept_text = c.getString(c.getColumnIndex("recept"));
            recept_name = c.getString(c.getColumnIndex("recept_name"));
            time_prep = c.getInt(c.getColumnIndex("time_prep"));
            timeforTimer = c.getInt(c.getColumnIndex("time_timer"));
            image_res = c.getInt(c.getColumnIndex("image_res"));
            id_recipe = c.getString(c.getColumnIndex("id"));
        }

        ContentValues cv = new ContentValues();

        cv.put("recept_name", recept_name);
        cv.put("recept", recept_text);
        cv.put("time_prep", time_prep);
        cv.put("time_timer", timeforTimer);
        cv.put("image_res", image_res);

        myDB.insert(DATABASE_TABLE_R, null, cv);

        str_arr[0] = id_recipe;
        c = RecipeDB.query(DATABASE_TABLE_I, null, "id_recept = ?", str_arr, null, null, null);

        if (c.moveToFirst()) {
            products = new ArrayList<Map<String, Object>>(c.getCount());
            Map<String, Object> m;
            for(int i = 0; i < c.getCount(); i++){
                m = new HashMap<String, Object>();
                m.put(ATTRIBUTE_NAME_PROD, c.getString(c.getColumnIndex("product_name")));
                m.put(ATTRIBUTE_NAME_KOL, c.getString(c.getColumnIndex("product_kol")));
                products.add(m);

                cv = new ContentValues();
                cv.put("product", c.getString(c.getColumnIndex("product_name")));
                cv.put("prod_kol", c.getString(c.getColumnIndex("product_kol")));

                myDB.insert(DATABASE_TABLE_P, null, cv);

                c.moveToNext();
            }
        }
    }

    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public DBHelper(Context context, int _imageRes) {
            // конструктор суперкласса
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            imageRes = _imageRes;
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
                    + "image_res integer" + ");");

            ContentValues cv = new ContentValues();

            cv.put("recept_name", RECEPT_NAME);
            cv.put("recept", RECEPT_OPIS);
            cv.put("time_prep", RECEPT_TIME);
            cv.put("time_timer", TIME_FOR_TIMER);
            cv.put("image_res", imageRes);
            // вставляем запись и получаем ее ID
            db.insert(DATABASE_TABLE_R, null, cv);

            db.execSQL("create table " + DATABASE_TABLE_P + "("
                    + "id integer primary key autoincrement,"
                    + "product text not null,"
                    + "prod_kol text not null" + ");");

            cv = new ContentValues();
            cv.put("product", PRODUCT_NAME_1);
            cv.put("prod_kol", PRODUCT_KOL_1);
            db.insert(DATABASE_TABLE_P, null, cv);

            cv = new ContentValues();
            cv.put("product", PRODUCT_NAME_2);
            cv.put("prod_kol", PRODUCT_KOL_2);
            db.insert(DATABASE_TABLE_P, null, cv);

        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE_R);
            db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE_P);
            onCreate(db);
        }
    }
    private class DBRHelper extends SQLiteOpenHelper {
        public DBRHelper(Context context) {
            // конструктор суперкласса
            super(context, "Recepts.db", null, 1);
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
                    + "image_res integer" + ");");

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
