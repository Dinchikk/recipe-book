package com.example.dns.rasm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.support.v7.app.ActionBarActivity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    public final static int TASK1_CODE = 1;
    public final static int STATUS_NEW = 100;
    public final static int STATUS_START = 200;
    public final static int STATUS_COUNT = 300;
    public final static int STATUS_FINISH = 400;
    public final static String PARAM_TIME = "time";
    public final static String PARAM_PINTENT = "pendingIntent";
    public final static String PARAM_RESULT = "result";

    final int REQUEST_CODE_NAME = 11;
    final int REQUEST_CODE_PROD = 12;

    final String ATTRIBUTE_NAME_PROD = "product";
    final String ATTRIBUTE_NAME_KOL = "prod_kol";

    SaveDB sdb;
    ImageView ivImage;
    TextView tv_NameRec;
    TextView tv_Rec;
    TextView tv_TimePrep;
    ListView lvIngredients;

    private int timerTime = 0;

    boolean bound = false;
    ServiceConnection sConn;
    Intent intent;

    TimerView timerview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivImage = (ImageView)findViewById(R.id.iv_Rec);
        tv_NameRec = (TextView)findViewById(R.id.tv_NameRec);
        tv_Rec = (TextView)findViewById(R.id.tv_Rec);
        tv_TimePrep = (TextView)findViewById(R.id.tv_TimePrep);

        sdb = new SaveDB(this);
        show_recipe();

        ReceptDB rdb = new ReceptDB(this);


        intent = new Intent(this, MyService.class);
        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                bound = true;
            }
            public void onServiceDisconnected(ComponentName name) {
                bound = false;
            }
        };

        timerview = (TimerView)this.findViewById(R.id.TimerView);
        OnTouchListener otlView = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                PendingIntent pi = createPendingResult(TASK1_CODE, new Intent(), 0);
                intent.putExtra(PARAM_TIME, timerTime).putExtra(PARAM_PINTENT, pi);
                startService(intent);

                return false;
            }
        };
        timerview.setOnTouchListener(otlView);

    }


    private void show_recipe() {
        tv_Rec.setText(sdb.recept_text);
        tv_NameRec.setText(sdb.recept_name);
        tv_TimePrep.setText("Время приготовления " + sdb.time_prep + "мин");
        try {
            ivImage.setImageResource(R.raw.class.getField(sdb.image_res).getInt(getResources()));
        } catch (IllegalAccessException | IllegalArgumentException	| NoSuchFieldException e) {
            e.printStackTrace();
        }


        timerTime = sdb.timeforTimer * 60;
        updateTimerView(timerTime, false);

        String[] from = { ATTRIBUTE_NAME_PROD, ATTRIBUTE_NAME_KOL };
        int[] to = { R.id.tvProd, R.id.tvKolvo };
        SimpleAdapter sAdapter = new SimpleAdapter(this, sdb.products, R.layout.main_act_item,from, to);
        lvIngredients = (ListView) findViewById(R.id.lvMainAct);
        lvIngredients.setAdapter(sAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.setGroupVisible(R.id.groupFind, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.find_name) {
            Intent intent = new Intent(this, FindNameActivity.class);
            startActivityForResult(intent, REQUEST_CODE_NAME);
            return true;
        }
        if (id == R.id.find_prod) {
            Intent intent = new Intent(this, FindProductsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_PROD);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart(){
        super.onStart();

        bindService(intent, sConn, BIND_AUTO_CREATE);

        PendingIntent pi = createPendingResult(TASK1_CODE, new Intent(), 0);
        intent.putExtra(PARAM_TIME, 0).putExtra(PARAM_PINTENT, pi);
        startService(intent);

    }
    @Override
    public void onDestroy(){
        unbindService(sConn);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Ловим сообщения о старте задач
        if (resultCode == STATUS_NEW) {
            int result = data.getIntExtra(PARAM_RESULT, 0);
            updateTimerView(timerTime, false);
        }
        if (resultCode == STATUS_COUNT) {
            int result = data.getIntExtra(PARAM_RESULT, 100);
            updateTimerView(result, true);
        }
        // 	Ловим сообщения об окончании задач
        if (resultCode == STATUS_FINISH) {
            int result = data.getIntExtra(PARAM_RESULT, 0);
            updateTimerView(0, false);
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_NAME){
                sdb.save_recipe( data.getStringExtra("name_recipe"));
                show_recipe();
            }
            if (requestCode == REQUEST_CODE_PROD){
                sdb.save_recipe( data.getStringExtra("name_recipe"));
                show_recipe();
            }
        }

    }

    private void updateTimerView(int values, boolean state) {
        if (timerview!= null) {
            timerview.setMinSec((int)(values / 60),(int)(values % 60), state);
            timerview.invalidate();
        }
    }

} 