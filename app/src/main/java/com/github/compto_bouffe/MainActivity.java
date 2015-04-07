package com.github.compto_bouffe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharePrefs = getSharedPreferences("profile", Context.MODE_PRIVATE);
        String name = sharePrefs.getString("name", "");
        Class<?> obj;
        if(name.equals(""))
            obj = FicheA.class; //FicheA.class;
        else
            obj = FicheB.class;
        Log.d(MainActivity.class.getName(), "Start " + obj.getName());
        startActivity(new Intent(this, obj));
        finish();
    }
}
