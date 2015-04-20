package com.github.compto_bouffe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

// Ici, on crée la base de données.
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBHelper dbHelper =new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT " + DBHelper.P_PRENOM + " FROM " + DBHelper.TABLE_PROFILS, null);
        int i = c.getCount();
        c.close();

        Class<?> obj;
        if(i == 0)
            obj = FicheA.class; //FicheA.class;
        else
            obj = FicheB.class;
        db.close();
        Log.d(MainActivity.class.getName(), "Start " + obj.getName());
        startActivity(new Intent(this, obj));
        finish();
    }
}
