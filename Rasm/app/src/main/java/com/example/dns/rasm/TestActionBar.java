package com.example.dns.rasm;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class TestActionBar extends Activity {

    ActionBar actionbar = getActionBar();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_action_bar);
        actionbar.show();



    }

}
