package com.github.compto_bouffe;

import android.app.Activity;
import android.content.Context;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class ModifierMaListe extends Activity {

    ListView listeView;
    Button valider;
    LinearLayout titreLayout;
    DBHelper dbh;
    SQLiteDatabase db;
    MyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_ma_liste);

        //Titre Description, Quantité et A supprimer
        titreLayout = (LinearLayout) findViewById(R.id.linearLayoutHeader);



        //Base de données + cursor contenant les plats de la liste d'aujourd'hui
        dbh = new DBHelper(this);
        db = dbh.getReadableDatabase();
        Cursor c = DBHelper.listePlatsDateCourante(db);

        //ListView contenant les plats + quantites
        listeView = (ListView) findViewById(R.id.listeRepas);
        adapter = new MyAdapter(this, c);
        listeView.setAdapter(adapter);

        //Bouton valider
        valider = (Button) findViewById(R.id.boutonValider);

        valider.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DBHelper dbH = new DBHelper(getApplicationContext());
                SQLiteDatabase db = dbH.getWritableDatabase();

                ArrayList<Boolean> checkedI = adapter.getItemChecked();
                for(int i=0; i< checkedI.size();i++) {
                    if (checkedI.get(i)) {
                        DBHelper.supprimerPlatListe(db, i);
                        Log.d("adapterModifierListe","Supprimer de la bdd:" + i);
                    }
                }

                ArrayList<Integer> changedI = adapter.getItemQtyChanged();
                for(int i=0; i< changedI.size();i++){
                    if(changedI.get(i)!=-1 && !checkedI.get(i)){

                    }
                }

                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modifier_ma_liste, menu);
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

    //Adapter de la listView
    public class MyAdapter extends CursorAdapter {

        private final Context context;
        LayoutInflater myInflater;
        ViewHolder holder;
        Cursor cursor;
        ArrayList<Boolean> itemChecked = new ArrayList<Boolean>();
        ArrayList<Integer> itemQtyChanged = new ArrayList<Integer>();

        /**
         * Constructeur de la classe MyAdapter
         * @param context le contexte
         * @param c le curseur
         */
        public MyAdapter(Context context, Cursor c) {
            super(context, c, false);
            myInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.context=context;
            this.cursor=c;

            c.moveToFirst();
            for (int i = 0; i < this.getCount(); i++) {
                itemChecked.add(i, false); // initializes all items value with false
                itemQtyChanged.add(i, -1);
                c.moveToNext();
            }
        }

        public ArrayList<Boolean> getItemChecked(){
            return itemChecked;
        }

        public ArrayList<Integer> getItemQtyChanged(){
            return itemQtyChanged;
        }

        //Classe interne ViewHolder
        class ViewHolder{
            protected TextView nom;
            protected CheckBox checkBoxView;
            protected Button boutonPlus;
            protected TextView textViewQte;
            protected Button boutonMoins;
        }


        public View getView(final int position, View convertView,ViewGroup parent) {

            View viewRow=convertView;

            cursor.moveToPosition(position);

            if (viewRow == null) {
                viewRow = myInflater.inflate(R.layout.modifier_ma_liste_row, null);
                holder = new ViewHolder();

                holder.nom = (TextView) viewRow.findViewById(R.id.textView1);
                holder.checkBoxView = (CheckBox) viewRow.findViewById(R.id.checkBoxSupprimer);
                holder.boutonPlus = (Button) viewRow.findViewById(R.id.boutonPlus);
                holder.textViewQte = (TextView) viewRow.findViewById(R.id.textQte);
                holder.boutonMoins = (Button) viewRow.findViewById(R.id.boutonMoins);

                viewRow.setTag(holder);
                viewRow.setTag(R.id.textView1, holder.nom);
                viewRow.setTag(R.id.checkBoxSupprimer, holder.checkBoxView);

                viewRow.setTag(R.id.boutonMoins, holder.boutonMoins);
                viewRow.setTag(R.id.boutonPlus, holder.boutonPlus);
                viewRow.setTag(R.id.textQte, holder.textViewQte);

            } else {
                holder = (ViewHolder) viewRow.getTag();
            }

            int quantite = cursor.getInt(cursor.getColumnIndex(DBHelper.L_QUANTITE));
            String nomProduit = cursor.getString(cursor.getColumnIndex(DBHelper.L_NOM));
            holder.nom.setText(nomProduit);
            holder.textViewQte.setText(Integer.toString(quantite));

            holder.checkBoxView.setTag(position);
            holder.checkBoxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //position de la checkbox
                    int getPosition = (Integer) buttonView.getTag();

                    if (isChecked) {
                        itemChecked.set(getPosition, true);
                    } else if (!isChecked) {
                        itemChecked.set(getPosition, false);
                    }
                    Log.d("adapterModifierListe","position ArrayList A Supprimer:" + position );
                }
            });

            //Modification de la quantite lors du click sur le bouton 'moins'
            holder.boutonMoins.setTag(holder);
            holder.boutonMoins.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolder holder_b = (ViewHolder) v.getTag();
                    int qte = Integer.parseInt(holder_b.textViewQte.getText().toString());

                    if (qte <= 1) {
                        holder_b.textViewQte.setText("0");
                    } else {
                        String quantite = Integer.toString(qte - 1);
                        holder_b.textViewQte.setText(quantite);
                    }

                    int newQte = Integer.parseInt(holder_b.textViewQte.getText().toString());
                    itemQtyChanged.set(position,newQte);

                    //Insertion de la position et nouvelle quantite dans le Map
                    Log.d("adapterModifierListe", "position Map position:" + position + ", quantite:" + newQte);

                }
            });

            //Modification de la quantite lors du click sur le bouton 'plus'
            holder.boutonPlus.setTag(holder);
            holder.boutonPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolder holder_b = (ViewHolder) v.getTag();
                    int qte = Integer.parseInt(holder_b.textViewQte.getText().toString());
                    String quantite = Integer.toString(qte + 1);
                    holder_b.textViewQte.setText(quantite);
                    itemQtyChanged.set(position,Integer.parseInt(quantite));

                    //Insertion de la position et nouvelle quantite dans le Map
                    //positionQte.put(getPosition, (String) holder_b.textViewQte.getText());
                    Log.d("adapterModifierListe","position Map position:" + position + ", quantite:"+Integer.parseInt(quantite));
                }
            });

            holder.checkBoxView.setTag(position);
            Log.d("adapterModifierListe","itemChecked:" + itemChecked.get(position));
            //holder.checkBoxView.setChecked(itemChecked.get(position));
            //holder.nom.setText(plats.get(position).getNom());
            holder.textViewQte.setText(Integer.toString(itemQtyChanged.get(position)));

            //Couleur alternative des rangées
            if (position % 2 == 1) {
                viewRow.setBackgroundColor(context.getResources().getColor(R.color.grisRangee1));
            } else {
                viewRow.setBackgroundColor(context.getResources().getColor(R.color.grisRangee2));
            }
            return viewRow;
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