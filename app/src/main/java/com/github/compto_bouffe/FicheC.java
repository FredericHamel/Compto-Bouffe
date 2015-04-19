package com.github.compto_bouffe;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;


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
        setContentView(R.layout.activity_fiche_c);
        initClickListener();
        modifier = (Button)findViewById(R.id.modify);
        addPlat = (Button)findViewById(R.id.add_plat);

        listfood = (ListView)findViewById(R.id.list_nutri);

        modifier.setOnClickListener(listener);
        addPlat.setOnClickListener(listener);



        dbh = new DBHelper(this);
        db = dbh.getReadableDatabase();
        Cursor c = dbh.listePlatsDateCourante(db);

        adapter = new MyAdapter(this, c);
        listfood.setAdapter(adapter);


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
                Cursor c = dbh.listePlatsDateCourante(db);
                adapter.changeCursor(c);
                adapter.notifyDataSetChanged();
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


    public class MyAdapter extends CursorAdapter {
        LayoutInflater inflater;

        public MyAdapter(Context context, Cursor c) {
            super(context, c, false);
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if(v==null){
                v = inflater.inflate(R.layout.nutriment_row, parent, false);
            }

            Cursor c = getCursor();
            c.moveToPosition(position);
            Integer qtity = c.getInt(c.getColumnIndex(DBHelper.L_QUANTITE));
            String name = c.getString(c.getColumnIndex(DBHelper.L_NOM));
            String desc = c.getString(c.getColumnIndex(DBHelper.L_DESC));
            String cal = c.getString(c.getColumnIndex(DBHelper.L_CALORIES));
            String sucre = c.getString(c.getColumnIndex(DBHelper.L_SUGARS));
            String gras = c.getString(c.getColumnIndex(DBHelper.L_TOTALFAT));
            String prot = c.getString(c.getColumnIndex(DBHelper.L_PROTEIN));


            TextView q = (TextView)v.findViewById(R.id.quantity);
            TextView n = (TextView)v.findViewById(R.id.food_name);
            TextView des = (TextView)v.findViewById(R.id.food_desc);
            TextView calo = (TextView)v.findViewById(R.id.calorie);
            TextView s = (TextView)v.findViewById(R.id.sucre);
            TextView g = (TextView)v.findViewById(R.id.gras);
            TextView p = (TextView)v.findViewById(R.id.proteines);

            q.setText(qtity);
            n.setText(name);
            des.setText(desc);
            calo.setText(cal);
            s.setText(sucre);
            g.setText(gras);
            p.setText(prot);

            Log.d("adapterFicheC","position" + position );


            return v;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return null;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

        }


    }
}
