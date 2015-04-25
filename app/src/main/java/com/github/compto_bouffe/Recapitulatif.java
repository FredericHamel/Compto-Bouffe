package com.github.compto_bouffe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Sabrina Ouaret on 16/04/15.
 */
// Le récapitulatif montre à l'usager, rangée par rangée, un triplet date, objectif, résultat,
// l'objectif et le résultat étant en calories
public class Recapitulatif extends Activity{

    private String[] DAY_OF_WEEK;
    private String[] MONTH;

    private TextView objectif;
    private ListView listeView;
    private ArrayList<DateInfos> dates;
    private EditText editDate1;
    private EditText editDate2;
    private CursorAdapter adapter;
    private ImageView imageRangee;
    private Button valider;


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
        DAY_OF_WEEK = getResources().getStringArray(R.array.jour_semaine);
        MONTH = getResources().getStringArray(R.array.mois);
        dbM = DatabaseManager.getInstance();
        db = dbM.openConnection();
        String prenom = DBHelper.getPrenom(db);
        String obj = DBHelper.getObjectif(db);
        objectif = (TextView)findViewById(R.id.phrases);
        objectif.setText(String.format("%s %s %s", prenom, getString(R.string.recapitulatif_objectif), obj));

        dates = new ArrayList<DateInfos>();
        editDate1 = (EditText)findViewById(R.id.editDate1);
        editDate2 = (EditText)findViewById(R.id.editDate2);
        editDate1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar mcurrentDate=Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth=mcurrentDate.get(Calendar.MONTH);
                int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

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

        editDate2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar mcurrentDate=Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth=mcurrentDate.get(Calendar.MONTH);
                int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                if(anneeDebut == 0 || moisDebut == 0 || jourDebut == 0){
                    Toast.makeText(getApplicationContext(), "Veuillez sélectionner une date de début de période.", Toast.LENGTH_SHORT).show();
                }else{

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

        listeView = (ListView)findViewById(R.id.listViewPeriode);

        Cursor c = DBHelper.listeObjectifs(db);
        adapter = new Fiche_f_myAdapter(this, c);

        listeView.setAdapter(adapter);
    }

    /*
        public ArrayList<DateInfos> getDatesInfos(){
        Cursor curseur = DBHelper.listeObjectifs(db);
        return dates;
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private class Fiche_f_myAdapter extends CursorAdapter {
        private ViewHolder holder;
        private LayoutInflater inflater;
        private DateFormat formater;

        @SuppressLint("SimpleDateFormat")
        public Fiche_f_myAdapter(Context context, Cursor c) {
            super(context, c, false);
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            formater = new SimpleDateFormat("yyyy-MM-dd");
        }

        class ViewHolder{
            protected ImageView imageResultat;
            protected TextView textDate;
            protected TextView textObjectif;
            protected TextView textResultat;
            protected ImageButton imageDetails;
        }

        public View getView(final int position, View view, ViewGroup parent) {


            if (view == null) {

                view = inflater.inflate(R.layout.recapitulatif_row, parent, false);
                holder = new ViewHolder();
                holder.textDate = (TextView)view.findViewById(R.id.textDate);
                holder.imageResultat = (ImageView)view.findViewById(R.id.imageResultat);
                holder.textObjectif = (TextView)view.findViewById(R.id.textObjectif);
                holder.textResultat = (TextView)view.findViewById(R.id.textResultat);
                holder.imageDetails = (ImageButton)view.findViewById(R.id.imageDetails);
                holder.imageDetails.setImageResource(R.drawable.arrow);
                view.setTag(holder);
            }else {
                holder=(ViewHolder) view.getTag();
            }

            Cursor c = getCursor();
            c.moveToPosition(position);
            String date = c.getString(c.getColumnIndex(DBHelper.R_DATE));
            int objInitial = Integer.parseInt(c.getString(c.getColumnIndex(DBHelper.R_OBJECTIF_INIT)));
            int resultat = Integer.parseInt(c.getString(c.getColumnIndex(DBHelper.R_OBJECTIF_RES)));
            int image = resultat < objInitial ? R.drawable.vert : R.drawable.rouge;
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(formater.parse(date));
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                int year = calendar.get(Calendar.YEAR);
                date = String.format("%s %s %s %d", DAY_OF_WEEK[dayOfWeek-1], dayOfMonth, MONTH[(month + 1)%12 ], year);
            }catch (ParseException e) {
                Log.d(getClass().getName() + ".DateParser", "Unable to parse " + date);
            }

            holder.textDate.setText(date);
            holder.textObjectif.setText(Integer.toString(objInitial));
            holder.textResultat.setText(Integer.toString(resultat));
            holder.imageResultat.setImageResource(image);

            int color = position % 2 == 1 ? R.color.grisRangee1 : R.color.grisRangee2;
            //Couleur alternative des rangées
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
