package com.github.compto_bouffe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * La classe permet à l'usager de modifier un menu élément par éléments.
 */
public class ModifierMaListe extends Activity {

    ListView listeView;
    Button valider;
    LinearLayout titreLayout;
    DatabaseManager dbM;
    SQLiteDatabase db;
    MyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_ma_liste);

        //Titre Description, Quantité et A supprimer
        titreLayout = (LinearLayout) findViewById(R.id.linearLayoutHeader);

        //Base de données + cursor contenant les plats de la liste d'aujourd'hui
        dbM = DatabaseManager.getInstance();
        db = dbM.openConnection();
        Cursor c = DBHelper.listePlatsDateCourante(db);

        //ListView contenant les plats + quantites
        listeView = (ListView) findViewById(R.id.listeRepas);
        adapter = new MyAdapter(this, c);
        listeView.setAdapter(adapter);

        //Bouton valider
        valider = (Button) findViewById(R.id.boutonValider);

        @SuppressLint("ShowToast")
        final Toast toast = Toast.makeText(getApplicationContext(), "Veuillez patienter", Toast.LENGTH_LONG);

        valider.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AsyncTask<Integer, Void, Long> task = new AsyncTask<Integer, Void, Long>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        listeView.setEnabled(false);
                        valider.setEnabled(false);
                        toast.show();
                    }

                    @Override
                    protected Long doInBackground(Integer... integers) {
                        adapter.updateDB();
                        return null;
                    }


                    @Override
                    protected void onPostExecute(Long l) {
                        super.onPostExecute(l);
                        listeView.setEnabled(true);
                        valider.setEnabled(true);
                        toast.cancel();
                        finish();
                    }
                };
                task.execute();
            }
        });
    }

    @Override
    protected void onDestroy()    {
        super.onDestroy();
        adapter.getCursor().close();
        dbM.close();
    }

    //Adapter de la listView
    public class MyAdapter extends CursorAdapter {
        private View.OnClickListener listener;
        private CompoundButton.OnCheckedChangeListener changeListener;
        private LayoutInflater myInflater;
        private ViewHolder holder;
        private ArrayList<Plats> plats;

        /**
         * Constructeur de la classe MyAdapter
         * @param context le contexte
         * @param c le curseur
         */
        public MyAdapter(Context context, Cursor c) {
            super(context, c, false);

            this.myInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.plats = new ArrayList<>();
            initListener();

            //Parcours du curseur s'il n'est pas vide
            if(c != null){
                c.moveToFirst();
                Log.d("adapterModifierListe","Count dans cursor:" + c.getCount());

                /*On parcourt le curseur et on récupère nom, upc, qte de la base de donnees
                pour creer un nouveau plat dans l'arrayList<Plat>*/
                do {
                    String nom=c.getString(c.getColumnIndex(DBHelper.L_NOM));
                    int qte = c.getInt(c.getColumnIndex(DBHelper.L_QUANTITE));
                    String upc = c.getString(c.getColumnIndex(DBHelper.L_UPC));
                    plats.add(new Plats(nom, upc, qte));
                } while(c.moveToNext());
            }
        }

        /**
         * Initialise le listener des boutons add et sub.
         * Initialise le checkListener pour les checkbox.
         */
        private void initListener()
        {
            this.listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewHolder holder_b = (ViewHolder) view.getTag(R.layout.activity_modifier_ma_liste_row);

                    //Position de la view
                    int i = (int)view.getTag();
                    //Quantite a cette position
                    int qte = Integer.parseInt(holder_b.textViewQte.getText().toString());
                    //Plat a cette position
                    Plats p = plats.get(i);

                    switch (view.getId())
                    {
                        //Insertion de la position et nouvelle quantite dans le Map
                        case R.id.boutonPlus:
                            p.setQte(qte + 1);
                            holder_b.textViewQte.setText(String.valueOf(p.getQte()));
                            break;
                        //Insertion de la position et nouvelle quantite dans le Map
                        case R.id.boutonMoins:
                            p.setQte(qte - 1);

                            holder_b.textViewQte.setText(String.valueOf(p.getQte()));
                            if(p.getQte() == 0) {
                                p.setSelected(true);
                                holder_b.checkBoxView.setChecked(p.isSelected());
                            }
                            break;
                    }

                    Log.d("adapterModifierListe", "position Map position:" + i + ", quantite:" + plats.get(i).getQte());
                }
            };

            this.changeListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //position de la checkbox
                    int position = (int) buttonView.getTag();

                    //le plat est dans un etat selectionne
                    plats.get(position).setSelected(isChecked);
                    Log.d("adapterModifierListe","position ArrayList A Supprimer:" + position );
                }
            };
        }


        //Classe interne ViewHolder
        private class ViewHolder{
            protected TextView nom;
            protected CheckBox checkBoxView;
            protected Button boutonPlus, boutonMoins;
            protected TextView textViewQte;
        }

        //Methode d'update de la base de donnees
        public void updateDB()
        {
            DatabaseManager dbM = DatabaseManager.getInstance();
            SQLiteDatabase db = dbM.openConnection();

            for(Plats plat : plats)
            {
                if(plat.isSelected()) {
                    Log.d("Modify", "Modify " + plat.getNom() + " 0");
                    DBHelper.changerQuantite(db, plat.getUpc(), 0); //Modification de la quantite a 0 dans la bdd selon l'upc
                }else if(plat.isModified()) {
                    Log.d("Modify", "Modify " + plat.getNom() + " " + plat.getQte());
                    DBHelper.changerQuantite(db, plat.getUpc(), plat.getQte());//Modification de la quantite dans la bdd selon upc
                }
            }
            dbM.close();
        }

        //Methode qui renvoit la View selon une certaine position
        public View getView(final int position, View convertView,ViewGroup parent) {

            Log.d(MyAdapter.class.getSimpleName(), "Position: " + position);
            View viewRow=convertView;
            Cursor cursor = getCursor();
            cursor.moveToPosition(position);

            //Si la vue s'affiche pour la première fois
            if (viewRow == null) {
                //On inflate la fiche XML correspondante
                viewRow = myInflater.inflate(R.layout.activity_modifier_ma_liste_row, null);
                //On cree notre ViewHolder
                holder = new ViewHolder();

                //On recupere les elements nom, checkBox, boutons et textView de chaque rangee de notre listeView
                holder.nom = (TextView) viewRow.findViewById(R.id.textView1);
                holder.checkBoxView = (CheckBox) viewRow.findViewById(R.id.checkBoxSupprimer);
                holder.boutonPlus = (Button) viewRow.findViewById(R.id.boutonPlus);
                holder.textViewQte = (TextView) viewRow.findViewById(R.id.textQte);
                holder.boutonMoins = (Button) viewRow.findViewById(R.id.boutonMoins);

                viewRow.setTag(holder);

                holder.boutonPlus.setOnClickListener(listener);
                holder.boutonMoins.setOnClickListener(listener);
                holder.checkBoxView.setOnCheckedChangeListener(changeListener);

            } else {//Sinon le holder recupere la vue existante
                holder = (ViewHolder) viewRow.getTag();
            }

            String nomProduit = cursor.getString(cursor.getColumnIndex(DBHelper.L_NOM));

            holder.nom.setText(nomProduit);
            holder.textViewQte.setText(Integer.toString(plats.get(position).getQte()));
            holder.checkBoxView.setTag(position);

            //Modification de la quantite lors du click sur le bouton 'moins'
            holder.boutonMoins.setTag(position);
            holder.boutonMoins.setTag(R.layout.activity_modifier_ma_liste_row, holder);


            //Modification de la quantite lors du click sur le bouton 'plus'
            holder.boutonPlus.setTag(position);
            holder.boutonPlus.setTag(R.layout.activity_modifier_ma_liste_row, holder);

            Plats plat = plats.get(position);
            holder.checkBoxView.setTag(position);
            holder.checkBoxView.setChecked(plat.isSelected());

            Log.d("adapterModifierListe","itemChecked:" + plat.isSelected());

            //Couleur alternative des rangées
            if (position % 2 == 1) {
                viewRow.setBackgroundResource(R.color.grisRangee1);
            } else {
                viewRow.setBackgroundResource(R.color.grisRangee2);
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