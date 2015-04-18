package com.github.compto_bouffe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
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
        super.onCreate(savedInstanceState);
        dbh = new DBHelper(this);
        db = dbh.getReadableDatabase();
        Cursor c = DBHelper.listePlatsDateCourante(db);

        //adapter = new MyAdapter(this, c);
        //listfood.setAdapter(adapter);

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

    private class MyAdapter extends CursorAdapter
    {
        private LayoutInflater inflater;
        private MyAdapter(Context context, Cursor c) {
            super(context, c, false);
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            if(view == null) {
                view = inflater.inflate(R.layout.nutriment_row, parent, false);
            }
            //Cursor c = getCursor();
            //c.moveToFirst();

            //int qte = c.getInt(c.getColumnIndex(DBHelper.L_QUANTITE));
            /*String nom = c.getString(c.getColumnIndex(DBHelper.L_NOM));
            String desc = c.getString(c.getColumnIndex(DBHelper.L_NOM));
            String cal = c.getString(c.getColumnIndex(DBHelper.L_CALORIES));
            String sucre = c.getString(c.getColumnIndex(DBHelper.L_SUGARS));
            String fat  = c.getString(c.getColumnIndex("'" +DBHelper.L_TOTALFAT + "'"));
            String proteine = c.getString(c.getColumnIndex(DBHelper.L_PROTEIN));*/
            //TextView tv1 = (TextView)view.findViewById(R.id.quantity);
            //tv1.setText(Integer.parseInt(qte));
            return view;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return null;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

        }
    }
}
