package com.github.compto_bouffe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

// La fiche C permet à l'usager de consulter et de modifier son menu du jour.
public class FicheC extends Activity {

    DatabaseManager dbM;
    SQLiteDatabase db;
    ListView listfood;
    MyAdapter adapter;

    private TextView textViewCalIng, textViewCalRes;

    private View.OnClickListener listener;
    private Button modifier, addPlat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche_c);
        initClickListener();
        textViewCalIng = (TextView)findViewById(R.id.textViewCalIng);
        textViewCalRes = (TextView)findViewById(R.id.textViewCalRes);

        modifier = (Button)findViewById(R.id.modify);
        addPlat = (Button)findViewById(R.id.add_plat);

        listfood = (ListView)findViewById(R.id.list_nutri);

        modifier.setOnClickListener(listener);
        addPlat.setOnClickListener(listener);

        dbM = DatabaseManager.getInstance();
        db = dbM.openConnection();
        Cursor c = DBHelper.listePlatsDateCourante(db);

        adapter = new MyAdapter(this, c);
        listfood.setAdapter(adapter);

        updateStatus(c);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.getCursor().close();
        Cursor c = DBHelper.listePlatsDateCourante(db);
        adapter.changeCursor(c);
        updateStatus(c);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.getCursor().close();
        dbM.close();
        Log.d("SQLite", "NbConnection to SQLDatabase="+dbM.getNbConnection());
    }

    private void updateStatus(Cursor c)
    {
        double calorie = 0;
        double objectif = Double.parseDouble(DBHelper.getObjectif(db));
        if(c.getCount() > 0)
        {
            c.moveToFirst();
            do {
                int qte = c.getInt(c.getColumnIndex(DBHelper.L_QUANTITE));
                calorie += qte*Double.parseDouble(c.getString(c.getColumnIndex(DBHelper.L_CALORIES)).split(" ")[0]);
            } while (c.moveToNext());
        }
        textViewCalIng.setText(String.valueOf(calorie));
        textViewCalRes.setText(String.valueOf(objectif - calorie));
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
            //String size = c.getString(c.getColumnIndex(DBHelper.L_SIZE));
            String cal = c.getString(c.getColumnIndex(DBHelper.L_CALORIES));
            String sucre = c.getString(c.getColumnIndex(DBHelper.L_SUGARS));
            String gras = c.getString(c.getColumnIndex(DBHelper.L_TOTALFAT));
            String prot = c.getString(c.getColumnIndex(DBHelper.L_PROTEIN));


            TextView q = (TextView)v.findViewById(R.id.quantity);
            TextView n = (TextView)v.findViewById(R.id.food_name);
            TextView calo = (TextView)v.findViewById(R.id.calorie);
            TextView s = (TextView)v.findViewById(R.id.sucre);
            TextView g = (TextView)v.findViewById(R.id.gras);
            TextView p = (TextView)v.findViewById(R.id.proteines);

            q.setText(Integer.toString(qtity));
            n.setText(name);
            calo.setText(newQuantite(cal, qtity));
            s.setText(newQuantite(sucre, qtity));
            g.setText(newQuantite(gras, qtity));
            p.setText(newQuantite(prot, qtity));

            int color = position % 2 == 1 ? R.color.grisRangee1 : R.color.grisRangee2;
            q.setBackgroundResource(color);
            n.setBackgroundResource(color);
            calo.setBackgroundResource(color);
            s.setBackgroundResource(color);
            g.setBackgroundResource(color);
            p.setBackgroundResource(color);
            Log.d("adapterFicheC","position" + position );


            return v;
        }

        private String newQuantite(String n, int q)
        {
            String[] parts = n.split(" ");
            double d = Double.parseDouble(parts[0]) * q;
            return String.format("%.1f %s", d, parts.length == 1? "": parts[1]);
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
