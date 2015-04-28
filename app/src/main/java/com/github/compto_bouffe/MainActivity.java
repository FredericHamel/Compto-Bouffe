package com.github.compto_bouffe;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

// Ici, on crée la base de données.
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseManager.init(getApplicationContext());
        DatabaseManager dBManager = DatabaseManager.getInstance();

        SQLiteDatabase db = dBManager.openConnection();
        Cursor c = db.rawQuery("SELECT " + DBHelper.P_PRENOM + " FROM " + DBHelper.TABLE_PROFILS, null);
        int i = c.getCount();
        c.close();
        dBManager.close();
        Class<?> obj;
        if(i == 0)
            obj = FicheProfil.class; //FicheA.class;
        else
            obj = MenuPrincipal.class;

        Log.d(MainActivity.class.getName(), "Start " + obj.getName());
        startActivity(new Intent(this, obj));
        finish();
    }
}
