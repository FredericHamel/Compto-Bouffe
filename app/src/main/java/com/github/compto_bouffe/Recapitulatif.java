package com.github.compto_bouffe;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Sabrina Ouaret on 16/04/15.
 */
// Le récapitulatif montre à l'usager, rangée par rangée, un triplet date, objectif, résultat,
// l'objectif et le résultat étant en calories
public class Recapitulatif extends Activity{

    ListView listeView;
    ArrayList<DateInfos> dates;
    EditText editDate1;
    EditText editDate2;
    ImageView imageRangee;
    ArrayAdapter<DateInfos> adapter;
    Button valider;

    int anneeDebut=0, moisDebut=0, jourDebut=0;//, anneeFin, moisFin, jourFin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recapitulatif);

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
                        editDate1.setText("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                        anneeDebut= selectedyear;
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
                                editDate2.setText("" + selectedday + "/" + selectedmonth + "/" + selectedyear);
                            }
                        }
                    }, mYear, mMonth, mDay);
                    mDatePicker.setTitle("Select date");
                    mDatePicker.show();
                }

            }
        });

        listeView = (ListView)findViewById(R.id.listViewPeriode);
        adapter = new Fiche_f_myAdapter(this, getDatesInfos());

        listeView.setAdapter(adapter);
    }

    public ArrayList<DateInfos> getDatesInfos(){
        dates.add(new DateInfos("21 décembre", "1500", "1800"));
        dates.add(new DateInfos("22 décembre", "1500", "1500"));
        dates.add(new DateInfos("23 décembre", "1500", "2100"));
        dates.add(new DateInfos("24 décembre", "1500", "1500"));
        dates.add(new DateInfos("25 décembre", "1500", "1500"));
        return dates;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private class Fiche_f_myAdapter extends ArrayAdapter<DateInfos> {

        private final ArrayList<DateInfos> dateInfos;
        private final Activity context;
        ViewHolder holder;

        public Fiche_f_myAdapter(Context context, ArrayList<DateInfos> dateInfos) {
            super(context, R.layout.recapitulatif_row, dateInfos);
            this.context = (Activity) context;
            this.dateInfos= dateInfos;
        }

        class ViewHolder{
            protected TextView textView1;
            protected TextView textView2;
            protected TextView textView3;
            protected ImageView imageView1;
            protected ImageButton imageView2;
        }

        public View getView(final int position, View convertView,ViewGroup parent) {

            View viewRow=convertView;

            if (viewRow == null) {
                LayoutInflater myInflater = LayoutInflater.from(getContext());
                viewRow = myInflater.inflate(R.layout.recapitulatif_row, null);
                holder = new ViewHolder();
                holder.textView1 = (TextView)viewRow.findViewById(R.id.textDate);
                holder.imageView1 = (ImageView)viewRow.findViewById(R.id.imageResultat);
                holder.textView2 = (TextView)viewRow.findViewById(R.id.textObjectif);
                holder.textView3 = (TextView)viewRow.findViewById(R.id.textResultat);
                holder.imageView2 = (ImageButton)viewRow.findViewById(R.id.imageDetails);
                holder.imageView2.setImageResource(R.drawable.vert);

                /*holder.imageView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //int getPosition = (Integer) buttonView.getTag();
                        //plats.get(getPosition).setSelected(buttonView.isChecked());
                    }
                });*/


                viewRow.setTag(holder);
                viewRow.setTag(R.id.textDate, holder.textView1);
                viewRow.setTag(R.id.imageResultat, holder.imageView1);
                viewRow.setTag(R.id.textObjectif, holder.textView2);
                viewRow.setTag(R.id.textResultat, holder.textView3);
                viewRow.setTag(R.id.imageDetails, holder.imageView2);

            }else {
                holder=(ViewHolder) viewRow.getTag();
            }

            //holder.checkBoxView.setTag(position);
            //holder.textViewQte.setText(plats.get(position).getNom());

            holder.textView1.setText(dateInfos.get(position).getDate());
            holder.textView2.setText(dateInfos.get(position).getInfo1());
            holder.textView3.setText(dateInfos.get(position).getInfo2());
            holder.imageView1.setImageResource(dateInfos.get(position).getImage());
            holder.imageView2.setImageResource(R.drawable.arrow);

            //Couleur alternative des rangées
            if (position % 2 == 1) {
                viewRow.setBackgroundColor(context.getResources().getColor(R.color.grisRangee1));
            } else {
                viewRow.setBackgroundColor(context.getResources().getColor(R.color.grisRangee2));
            }
            return viewRow;
        }
    }
}
