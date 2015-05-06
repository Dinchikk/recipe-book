package com.example.dns.rasm;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class FindNameActivity extends Activity {
    EditText name;
    ListView lst_rez_name;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        name =  (EditText) findViewById(R.id.EV_input_name);
        lst_rez_name =  (ListView ) findViewById(R.id.LV_res);


    }

}
