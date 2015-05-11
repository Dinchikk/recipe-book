package com.example.dns.rasm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FindProductsActivity extends Activity {
    private static final String DATABASE_NAME    = "Recepts.db";
    private static final String DATABASE_TABLE_R = "Recept";
    private static final String DATABASE_TABLE_I = "Ingredients";
    private static final String DATABASE_TABLE_P = "Products";
    private static final int DATABASE_VERSION = 1;

    ListView lst_rez_name;
    ListView lst_products;

    private DBHelper dbHelper;
    private static SQLiteDatabase myReceptDB;
    private ArrayList<Map<String, Object>> products;
    private static Context cont;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cont = this;
        setContentView(R.layout.poisk2);

        lst_products = (ListView) findViewById(R.id.LV_var);
        lst_rez_name = (ListView) findViewById(R.id.LV_res2);

        dbHelper = new DBHelper(this);
        myReceptDB = dbHelper.getWritableDatabase();

        Cursor c = myReceptDB.query(DATABASE_TABLE_P, null, null, null, null, null, null);
/*
		if (c.moveToFirst()) {
			products = new ArrayList<Map<String, Object>>(c.getCount());
			Map<String, Object> m;
			for(int i = 0; i < c.getCount(); i++){
				m = new HashMap<String, Object>();
				m.put("product_name", c.getString(c.getColumnIndex("product_name")));
				products.add(m);
				c.moveToNext();
			}
		}
		String[] from = { "product_name"};
		int[] to = { R.id.tvProd_poisk2 };
		final SimpleAdapter sAdapter = new SimpleAdapter(this, products, R.layout.poisk2_act_item,from, to);
*/
        String[] array_prod = null;
        final ArrayList<String> _products = new ArrayList<String>();
        if (c.moveToFirst()) {
            array_prod = new String[c.getCount()];
            for(int i = 0; i < c.getCount(); i++){
                array_prod[i] = c.getString(c.getColumnIndex("product_name"));
                _products.add(array_prod[i]);
                c.moveToNext();
            }
        }
        final String[] tmp_arr = array_prod;
        final ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, _products);

        // Привяжем массив через адаптер к ListView
        lst_products.setAdapter(adapter);
        lst_products.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,	int position, long id) {

                // Объект SparseBooleanArray содержит массив значений, к которым можно получить доступ
                // через valueAt(index) и keyAt(index)
                SparseBooleanArray chosen = ((ListView) parent).getCheckedItemPositions();
                int count_str=0;
                for (int i = 0; i < chosen.size(); i++) if (chosen.valueAt(i))count_str++;
                if (count_str > 0){
                    String[] str_arr = new String[count_str];
                    count_str = 0;
                    for (int i = 0; i < chosen.size(); i++) {

                        // если пользователь выбрал пункт списка,
                        if (chosen.valueAt(i)) {
                            str_arr[count_str++] = tmp_arr[chosen.keyAt(i)];
                        }
                    }
                    String table = "Recept as PC inner join Ingredients as IG on PC.id = IG.id_recept";
                    String[] columns = new String[] { "PC.recept_name"};
                    Cursor _c = myReceptDB.query(table, columns,"IG.product_name LIKE ?", str_arr, null, null, null);

                    final ArrayList<String> recepts = new ArrayList<String>();
                    if (_c.moveToFirst()) {
                        for(int i = 0; i < _c.getCount(); i++){
                            recepts.add(_c.getString(_c.getColumnIndex("recept_name")));
                            _c.moveToNext();
                        }
                    }
                    final ArrayAdapter<String> find;
                    find = new ArrayAdapter<String>(cont,android.R.layout.simple_list_item_1, recepts);
                    lst_rez_name.setAdapter(find);
                }else{
                    final ArrayAdapter<String> find;
                    find = new ArrayAdapter<String>(cont,android.R.layout.simple_list_item_1, new ArrayList<String>());
                    lst_rez_name.setAdapter(find);
                }
            }
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