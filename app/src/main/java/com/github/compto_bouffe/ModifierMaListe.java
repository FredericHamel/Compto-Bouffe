package com.github.compto_bouffe;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ModifierMaListe extends Activity {

    ListView listeView;
    Button valider;
    LinearLayout titreLayout;
    ArrayAdapter<Plats> adapter;
    ArrayList<Plats> plats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_ma_liste);

        //Titre Description, Quantité et A supprimer
        titreLayout = (LinearLayout) findViewById(R.id.linearLayoutHeader);

        //pour tester en attendant les données de l'API
        plats = new ArrayList<Plats>();

        listeView = (ListView) findViewById(R.id.listeRepas);
        adapter = new Fiche_e_myAdapter(this,getPlat());
        listeView.setAdapter(adapter);
        valider = (Button) findViewById(R.id.boutonValider);

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ModifierMaListe.this, "Simulation de modification de la liste", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public ArrayList<Plats> getPlat(){
        plats.add(new Plats("Soupe", "1"));
        plats.add(new Plats("Fruits", "1"));
        plats.add(new Plats("Croissants", "1"));
        plats.add(new Plats("Big Mac", "1"));
        plats.add(new Plats("Woopie Burger", "1"));
        plats.add(new Plats("Sushi", "1"));
        plats.add(new Plats("Salade", "1"));
        return plats;
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

    private class Fiche_e_myAdapter extends ArrayAdapter<Plats> {

        private final ArrayList<Plats> plats;
        private final Activity context;
        ViewHolder holder;

        public Fiche_e_myAdapter(Context context, ArrayList<Plats> plats) {
            super(context, R.layout.modifier_ma_liste_row, plats);
            this.context = (Activity) context;
            this.plats= plats;
        }

        class ViewHolder{
            protected TextView theTextView;
            protected CheckBox checkBoxView;
            protected Button boutonPlus;
            protected TextView textViewQte;
            protected Button boutonMoins;
        }

        public View getView(final int position, View convertView,ViewGroup parent) {

            View viewRow=convertView;

            if (viewRow == null) {
                LayoutInflater myInflater = LayoutInflater.from(getContext());
                viewRow = myInflater.inflate(R.layout.modifier_ma_liste_row, null);

                holder = new ViewHolder();
                holder.theTextView = (TextView)viewRow.findViewById(R.id.textView1);
                holder.checkBoxView = (CheckBox)viewRow.findViewById(R.id.checkBoxSupprimer);
                holder.boutonPlus = (Button)viewRow.findViewById(R.id.boutonPlus);
                holder.textViewQte = (TextView)viewRow.findViewById(R.id.textQte);
                holder.boutonMoins = (Button)viewRow.findViewById(R.id.boutonMoins);

                holder.checkBoxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //position de la checkbox
                        int getPosition = (Integer) buttonView.getTag();
                        plats.get(getPosition).setSelected(buttonView.isChecked());
                    }
                });

                viewRow.setTag(holder);
                viewRow.setTag(R.id.textView1, holder.theTextView);
                viewRow.setTag(R.id.checkBoxSupprimer, holder.checkBoxView);

                viewRow.setTag(R.id.boutonMoins, holder.boutonMoins);
                viewRow.setTag(R.id.boutonPlus, holder.boutonPlus);
                viewRow.setTag(R.id.textQte, holder.textViewQte);

            }else {
                holder=(ViewHolder) viewRow.getTag();
            }


            holder.boutonMoins.setTag(holder);
            holder.boutonMoins.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolder holder_b = (ViewHolder)v.getTag();
                    int qte = Integer.parseInt(holder_b.textViewQte.getText().toString());
                    if(qte<=1){
                        holder_b.textViewQte.setText("0");
                        plats.get(position).setQte("0");
                    }else{
                        String quantite = Integer.toString(qte-1);
                        holder_b.textViewQte.setText(quantite);
                        plats.get(position).setQte(quantite);
                    }
                }
            });

            holder.boutonPlus.setTag(holder);
            holder.boutonPlus.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    ViewHolder holder_b = (ViewHolder)v.getTag();
                    int qte = Integer.parseInt(holder_b.textViewQte.getText().toString());
                    String quantite = Integer.toString(qte+1);
                    holder_b.textViewQte.setText(quantite);
                    plats.get(position).setQte(quantite);
                }
            });

            holder.checkBoxView.setTag(position);
            holder.checkBoxView.setChecked(plats.get(position).isSelected());
            holder.theTextView.setText(plats.get(position).getNom());
            holder.textViewQte.setText(plats.get(position).getQte());

            //Couleur alternative des rangées
            if (position % 2 == 1) {
                viewRow.setBackgroundColor(context.getResources().getColor(R.color.grisColonne1));
            } else {
                viewRow.setBackgroundColor(context.getResources().getColor(R.color.grisColonne2));
            }
            return viewRow;
        }
    }
}