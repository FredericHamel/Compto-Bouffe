package com.github.compto_bouffe;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



// La fiche A demande son nom et son objectif à l'utilisateur; les deux sont sauvegardés dans la
// base de deonnées.
public class FicheA extends Activity implements View.OnClickListener {

    private EditText ed_name;    // le nom du l'utilisateur
    private EditText ed_cal;     //le nombre de calories entrées
    private TextView ed_marge;

    private Profil profil;

    private class Profil {
        public String nom;
        public int objectif;
        public int marge;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        profil = new Profil();

        Button btnValider = (Button) findViewById(R.id.btn_profil_valider);
        Button btnGouv = (Button) findViewById(R.id.btn_profil_gouv);
        Button addPercent = (Button) findViewById(R.id.add_percent);
        Button subPercent = (Button) findViewById(R.id.sub_percent);
        ed_name = (EditText)findViewById(R.id.name);
        ed_cal = (EditText)findViewById(R.id.objectifs);
        ed_marge = (TextView)findViewById(R.id.marge);

        updateData();

        btnValider.setOnClickListener(this);
        btnGouv.setOnClickListener(this);
        addPercent.setOnClickListener(this);
        subPercent.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateData();
    }

    private void updateData() {
        DatabaseManager dbM = DatabaseManager.getInstance();
        SQLiteDatabase db = dbM.openConnection();
        ed_name.setText(DBHelper.getPrenom(db));
        ed_cal.setText((String.valueOf(DBHelper.getObjectif(db))));
        ed_marge.setText(String.format("%d %%", DBHelper.getMarge(db)));
        dbM.close();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.add_percent:
                String margeText = ed_marge.getText().toString();
                int marge = Integer.parseInt(margeText.split(" ")[0]);
                if(marge <= 95) {
                    marge += 5;
                    ed_marge.setText(String.format("%d %%", marge));
                }
                break;
            case R.id.sub_percent:
                margeText = ed_marge.getText().toString();
                marge = Integer.parseInt(margeText.split(" ")[0]);
                if(marge >= 5) {
                    marge -= 5;
                    ed_marge.setText(String.format("%d %%", marge));
                }
                break;
            case R.id.btn_profil_valider:
                //on crée un toast
                String letter_name = ed_name.getText().toString();
                String number_cal = ed_cal.getText().toString();
                margeText = ed_marge.getText().toString();
                AsyncTask<Profil, Void, Long> d = new AsyncTask<Profil, Void, Long>() {

                    @Override
                    protected Long doInBackground(Profil... profils) {
                        Profil profil = profils[0];
                        DatabaseManager dbM = DatabaseManager.getInstance();
                        SQLiteDatabase db = dbM.openConnection();
                        DBHelper.updateProfil(db, profil.nom, profil.objectif, profil.marge);
                        dbM.close();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Long aLong) {
                        super.onPostExecute(aLong);
                        Intent intent = new Intent(getApplicationContext(), FicheB.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                };
                if (number_cal.matches("[1-9]+[0-9]{2,4}")) {
                    profil.nom = letter_name;
                    profil.objectif = Integer.parseInt(number_cal);
                    profil.marge = Integer.parseInt(margeText.split(" ")[0]);
                    d.execute(profil);
                }else
                    Toast.makeText(this, "Veuillez entrer un objectif valide", Toast.LENGTH_SHORT).show();

                break;
            case R.id.btn_profil_gouv:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.hc-sc.gc.ca/fn-an/nutrition/reference/table/index-fra.php"));
                startActivity(browserIntent);
                break;
        }
    }
}
