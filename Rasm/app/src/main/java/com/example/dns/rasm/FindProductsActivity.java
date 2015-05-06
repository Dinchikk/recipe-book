package com.example.dns.rasm;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class FindProductsActivity extends Activity {
    ListView lst_rez_name;
    ListView lst_products;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.poisk2 );

        lst_products = (ListView) findViewById(R.id.LV_var);
        lst_rez_name =  (ListView ) findViewById(R.id.LV_res2);

    }

}