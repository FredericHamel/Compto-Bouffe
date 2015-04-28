package com.github.compto_bouffe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

// Le récapitulatif montre à l'usager, rangée par rangée, un triplet date, objectif, résultat,
// l'objectif et le résultat étant en calories
public class Recapitulatif extends Activity{

    private String[] MONTH;
    private EditText editDate1;
    private EditText editDate2;
    private CursorAdapter adapter;
    private Cursor curseurValider;


    private DatabaseManager dbM;
    private SQLiteDatabase db;

    int anneeDebut=0, moisDebut=0, jourDebut=0;//, anneeFin, moisFin, jourFin;

    public Recapitulatif() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recapitulatif);

        MONTH = getResources().getStringArray(R.array.mois);
        dbM = DatabaseManager.getInstance();
        db = dbM.openConnection();

        String prenom = DBHelper.getPrenom(db);
        int obj = DBHelper.getObjectif(db);
        Button valider = (Button) findViewById(R.id.boutonPeriode);

        editDate1 = (EditText)findViewById(R.id.editDate1);
        editDate2 = (EditText)findViewById(R.id.editDate2);

        /*EditText contenant la date de debut de periode
        Lors du click, un calendrier s'affiche.*/
        editDate1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Date du jour
                Calendar mcurrentDate=Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth=mcurrentDate.get(Calendar.MONTH);
                int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                //Recupere la date de debut de periode selectionnee par l'usager
                DatePickerDialog mDatePicker= new DatePickerDialog(Recapitulatif.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        editDate1.setText(String.format("%04d-%02d-%02d", selectedyear, selectedmonth+1, selectedday));
                        anneeDebut=selectedyear;
                        moisDebut=selectedmonth;
                        jourDebut=selectedday;
                    }
                },mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();
            }
        });

        /*EditText contenant la date de fin de periode
        Lors du click, un calendrier s'affiche.*/
        editDate2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Date du jour
                Calendar mcurrentDate=Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth=mcurrentDate.get(Calendar.MONTH);
                int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                //S'il n'y a pas de date de debut de periode, on affiche un message d'erreur
                if(anneeDebut == 0 || moisDebut == 0 || jourDebut == 0){
                    Toast.makeText(getApplicationContext(), "Veuillez sélectionner une date de début de période.", Toast.LENGTH_SHORT).show();
                }else{

                    //Recupere la date de debut de periode selectionnee par l'usager
                    DatePickerDialog mDatePicker = new DatePickerDialog(Recapitulatif.this, new DatePickerDialog.OnDateSetListener() {

                        public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                            if (selectedyear == anneeDebut && selectedmonth < moisDebut) {
                                Toast.makeText(getApplicationContext(), "Veuillez sélectionner une date postérieure à la date comparée.", Toast.LENGTH_SHORT).show();
                            } else if (selectedyear == anneeDebut && selectedmonth == moisDebut && selectedday < jourDebut) {
                                Toast.makeText(getApplicationContext(), "Veuillez sélectionner une date postérieure à la date comparée.", Toast.LENGTH_SHORT).show();
                            } else if (selectedyear < anneeDebut) {
                                Toast.makeText(getApplicationContext(), "Veuillez sélectionner une date postérieure à la date comparée.", Toast.LENGTH_SHORT).show();
                            } else {
                                editDate2.setText(String.format("%04d-%02d-%02d", selectedyear, selectedmonth+1, selectedday));
                            }
                        }
                    }, mYear, mMonth, mDay);
                    mDatePicker.setTitle("Select date");
                    mDatePicker.show();
                }
            }
        });

        ListView listeView = (ListView) findViewById(R.id.listViewPeriode);

        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("EditTextContent", editDate1.getText().toString());
                String a = editDate1.getText().toString();
                String b = editDate2.getText().toString();

                //Comparaison des chaines et affichage de messages d'erreur au besoin
                if (a.equals("") || b.equals("") || a.compareTo(b) > 0)
                    Toast.makeText(getApplicationContext(), "Entrez un intervalle de temps valide.", Toast.LENGTH_LONG).show();
                else {//Sinon appel de la fonction qui renvoit le curseur avec les informations de cette periode
                    curseurValider = DBHelper.listeObjectifsPeriode(db, a, b);
                    adapter.changeCursor(curseurValider);
                }
            }
        });
        Log.d("Recapitulatif", "CurseurValider est null?: " + (curseurValider == null));
        curseurValider = DBHelper.listeObjectifs(db);
        adapter = new ModifierListeAdaptor(this, curseurValider);

        listeView.setAdapter(adapter);
    }

    //Adapter de la fiche ModifierMaListe
    private class ModifierListeAdaptor extends CursorAdapter {
        private ViewHolder holder;
        private LayoutInflater inflater;
        private DateFormat formater;

        @SuppressLint("SimpleDateFormat")
        public ModifierListeAdaptor(Context context, Cursor c) {
            super(context, c, false);
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            formater = new SimpleDateFormat("yyyy-MM-dd");
        }

        class ViewHolder{
            protected ImageView imageResultat;
            protected TextView textDate, textObjectif, textResultat;
            protected ImageButton imageDetails;
        }

        //Methode qui renvoit la View selon une certaine position
        public View getView(final int position, View view, ViewGroup parent) {

            //Si la view est null, on recupere les elements et les ajoutons au holderView
            if (view == null) {

                view = inflater.inflate(R.layout.activity_recapitulatif_row, parent, false);
                holder = new ViewHolder();
                holder.textDate = (TextView)view.findViewById(R.id.textDate);
                holder.imageResultat = (ImageView)view.findViewById(R.id.imageResultat);
                holder.textObjectif = (TextView)view.findViewById(R.id.textObjectif);
                holder.textResultat = (TextView)view.findViewById(R.id.textResultat);
                holder.imageDetails = (ImageButton)view.findViewById(R.id.imageDetails);
                holder.imageDetails.setImageResource(R.drawable.arrow);
                holder.imageDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), FicheC.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", (Integer)view.getTag());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                view.setTag(holder);
            }else {
                holder=(ViewHolder) view.getTag();
            }

            Cursor c = getCursor();
            c.moveToPosition(position);

            //On recupere date, objectif initial, resultat objectif, marge de la base de donnees selon la position du curseur
            String date = c.getString(c.getColumnIndex(DBHelper.R_DATE));
            int objInitial = c.getInt(c.getColumnIndex(DBHelper.R_OBJECTIF_INIT));
            int resultat =  c.getInt(c.getColumnIndex(DBHelper.R_OBJECTIF_RES));
            int marge = c.getInt(c.getColumnIndex(DBHelper.R_MARGE));

            //On affiche l'image en consequence: rouge si resultat non atteint, vert sinon.
            int image = 100*Math.abs(objInitial - resultat) <= objInitial*marge ? R.drawable.vert : R.drawable.rouge;


            Log.d("ResultatSQL", ""+resultat);
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(formater.parse(date));
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                int year = calendar.get(Calendar.YEAR);
                if(date.equals(DBHelper.getDateCourante()))
                    date = getString(R.string.today);
                else
                    date = String.format("%s %s %d", dayOfMonth, MONTH[month], year);
            }catch (ParseException e) {
                Log.d(getClass().getName() + ".DateParser", "Unable to parse " + date);
            }

            holder.textDate.setText(date);
            holder.textObjectif.setText(String.format("%s %d ±%d%%", "OBJECTIF:", objInitial, marge));
            holder.textResultat.setText(String.format("%s %s", "RESULTAT:", String.valueOf(resultat)));
            holder.imageResultat.setImageResource(image);
            holder.imageDetails.setTag(position);

            //Couleur alternative des rangées
            int color = position % 2 == 1 ? R.color.grisRangee1 : R.color.grisRangee2;
            view.setBackgroundResource(color);
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
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbM.close();
    }
}
