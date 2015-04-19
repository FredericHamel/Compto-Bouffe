package com.github.compto_bouffe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class FicheC extends Activity {

    private class MyAdapter extends CursorAdapter{
        private MyAdapter(Context context, Cursor c) {
            super(context, c, false);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return null;
        }
    }

    DBHelper dbh;
    SQLiteDatabase db;
    MyAdapter adapter;
    ListView listFood;



    private View.OnClickListener listener;
    private Button modifier, addPlat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        dbh = new DBHelper(this);
        db = dbh.getReadableDatabase();
        Cursor c = DBHelper.listePlatsDateCourante(db);

        adapter = new MyAdapter(this, c);
        listFood.setAdapter(adapter);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche_c);
        initClickListener();
        modifier = (Button)findViewById(R.id.modify);
        addPlat = (Button)findViewById(R.id.add_plat);

        modifier.setOnClickListener(listener);
        addPlat.setOnClickListener(listener);
    }

    private void initClickListener()
    {
        listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(view.getId())
                {
                    case R.id.modify:
                        startActivity(new Intent(FicheC.this, ModifierMaListe.class));
                        break;
                    case R.id.add_plat:
                        startActivity(new Intent(FicheC.this, RecherchePlats.class));
                        break;

                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fiche_c, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
