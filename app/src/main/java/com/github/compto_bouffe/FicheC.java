package com.github.compto_bouffe;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class FicheC extends Activity {


    ListView listfood;

    DBHelper dbh;
    SQLiteDatabase db;
    MyAdapter adapter;




    private View.OnClickListener listener;
    private Button modifier, addPlat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        dbh = new DBHelper(this);
        db = dbh.getReadableDatabase();
        Cursor c = dbh.listeE(db);

        adapter = new MyAdapter(this, c);
        listfood.setAdapter(adapter);

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
