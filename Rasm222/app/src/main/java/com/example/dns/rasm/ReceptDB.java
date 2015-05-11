package com.example.dns.rasm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

public class ReceptDB {
    private static final String DATABASE_NAME    = "Recepts.db";
    private static final String DATABASE_TABLE_R = "Recept";
    private static final String DATABASE_TABLE_I = "Ingredients";
    private static final String DATABASE_TABLE_P = "Products";
    private static final int DATABASE_VERSION = 1;

    private DBRHelper dbHelper;
    private static SQLiteDatabase myReceptDB;
    private static Context mainContext;

    public ReceptDB(Context context){
        mainContext = context;

        dbHelper = new DBRHelper(mainContext);
        myReceptDB = dbHelper.getWritableDatabase();

        ParseTask myTask = new ParseTask();
        myTask.execute();

    }
    private static class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {

                URL url = new URL("https://raw.githubusercontent.com/Dinchikk/recipe-book/master/Rasm/gradlew");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

//				InputStream inputStream = mainContext.getResources().openRawResource(R.raw.recept);

                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                resultJson = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);

            ContentValues cv;
            long rowID;

            int tmpr = myReceptDB.delete(DATABASE_TABLE_R, null, null);
            int tmpi = myReceptDB.delete(DATABASE_TABLE_I, null, null);
            int tmpp = myReceptDB.delete(DATABASE_TABLE_P, null, null);

            JSONObject dataJsonObj = null;

            try {
                dataJsonObj = new JSONObject(strJson);

                JSONArray recepts_array = dataJsonObj.getJSONArray("recepts_array");
                for (int i = 0; i < recepts_array.length(); i++) {

                    JSONObject item = recepts_array.getJSONObject(i);

                    cv = new ContentValues();
                    cv.put("recept_name", item.getString("recept_name"));
                    cv.put("recept", item.getString("recept"));
                    cv.put("time_prep", item.getInt("time_prep"));
                    cv.put("time_timer", item.getInt("time_timer"));
                    cv.put("image_res", item.getString("image_res"));
                    // вставляем запись и получаем ее ID
                    rowID = myReceptDB.insert(DATABASE_TABLE_R, null, cv);

                    JSONArray ingredients_array = item.getJSONArray("ingredients");
                    for (int j = 0; j < ingredients_array.length(); j++) {
                        JSONObject item_1 = ingredients_array.getJSONObject(j);
                        cv = new ContentValues();
                        cv.put("id_recept", rowID);
                        cv.put("product_name", item_1.getString("product_name"));
                        cv.put("product_kol", item_1.getString("product_kol"));
                        myReceptDB.insert(DATABASE_TABLE_I, null, cv);
                    }
                }
                JSONArray products_array = dataJsonObj.getJSONArray("products_array");
                for (int i = 0; i < products_array.length(); i++) {
                    JSONObject item = products_array.getJSONObject(i);
                    cv = new ContentValues();
                    cv.put("product_id", item.getInt("product_id"));
                    cv.put("product_name", item.getString("product_name"));
                    myReceptDB.insert(DATABASE_TABLE_P, null, cv);
                }
                myReceptDB.close();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class DBRHelper extends SQLiteOpenHelper {
        public DBRHelper(Context context) {
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
