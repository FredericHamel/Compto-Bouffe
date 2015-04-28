package com.github.compto_bouffe;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// La fiche B permet à l'usager, par trois boutons:
// - d'accéder au menu du jour;
// - d'accéder au récapitulatif des derniers jours;
// - de modifier ses informations
public class FicheB extends Activity implements View.OnClickListener {

    private DatabaseManager dbM;
    private Button today, recapitulatif, modifyProfils;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche_b);
        today = (Button)findViewById(R.id.btn_today);
        recapitulatif = (Button)findViewById(R.id.btn_recapitulatif);
        modifyProfils = (Button)findViewById(R.id.btn_modify_profils);

        updateData();

        today.setOnClickListener(this);
        recapitulatif.setOnClickListener(this);
        modifyProfils.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onStart();
        updateData();
    }

    private void updateData()
    {
        dbM = DatabaseManager.getInstance();
        SQLiteDatabase db = dbM.openConnection();
        String prenom = DBHelper.getPrenom(db);
        Cursor c = DBHelper.listeObjectifs(db);
        recapitulatif.setEnabled(c.getCount() > 0);
        c.close();

        //Affichage texte prenom+objectif
        int obj = DBHelper.getObjectif(db);
        TextView objectif = (TextView) findViewById(R.id.prenomFicheB);
        objectif.setText(String.format("%s %s %d %s", prenom, getString(R.string.recapitulatif_objectif), obj, "calories."));

        dbM.close();
    }

    @Override
    public void onClick(View v) {
        final View view = v;
        AsyncTask<Void, Void, Void> task =new AsyncTask<Void, Void, Void>() {
            private boolean oldState;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                oldState = recapitulatif.isEnabled();
                today.setEnabled(false);
                recapitulatif.setEnabled(false);
                modifyProfils.setEnabled(false);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                SQLiteDatabase db = dbM.openConnection();
                String dateCourante = DBHelper.getDateCourante();
                ContentValues cv = new ContentValues();
                cv.put(DBHelper.R_OBJECTIF_INIT, DBHelper.getObjectif(db));
                cv.put(DBHelper.R_MARGE, DBHelper.getMarge(db));
                if(db.update(DBHelper.TABLE_RESULTATS, cv, DBHelper.R_DATE+"='"+dateCourante+"'", null) == 0) {
                    cv.put(DBHelper.L_USER_ID, DBHelper.USER_ID);
                    cv.put(DBHelper.R_DATE, dateCourante);
                    cv.put(DBHelper.R_OBJECTIF_RES, 0);
                    db.insert(DBHelper.TABLE_RESULTATS, null, cv);
                }
                dbM.close();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                today.setEnabled(true);
                recapitulatif.setEnabled(oldState);
                modifyProfils.setEnabled(true);
                switch (view.getId()) {
                    case R.id.btn_today:
                        startActivity(new Intent(getApplicationContext(), FicheC.class));
                        break;
                    case R.id.btn_recapitulatif:
                        startActivity(new Intent(getApplicationContext(), Recapitulatif.class));
                        break;
                    case R.id.btn_modify_profils:
                        Intent intent = new Intent(getApplicationContext(), FicheA.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                }
            }
        };

        task.execute();
    }
}
