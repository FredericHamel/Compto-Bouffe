package com.github.compto_bouffe;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



// La fiche A demande son nom et son objectif à l'utilisateur; les deux sont sauvegardés dans la
// base de deonnées.
public class FicheA extends Activity implements View.OnClickListener {

    private EditText ed_name;    // le nom du l'utilisateur
    private EditText ed_cal;     //le nombre de calories entrées

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feuille_a);

        Button btnValider = (Button) findViewById(R.id.btn_profil_valider);
        Button btnGouv = (Button) findViewById(R.id.btn_profil_gouv);
        ed_name = (EditText)findViewById(R.id.name);
        ed_cal = (EditText)findViewById(R.id.objectifs);

        updateData();

        btnValider.setOnClickListener(this);
        btnGouv.setOnClickListener(this);
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
        ed_cal.setText(DBHelper.getObjectif(db));
        dbM.close();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_profil_valider:
                //on crée un toast
                String letter_name = ed_name.getText().toString();
                String number_cal = ed_cal.getText().toString();
                AsyncTask<String, Void, Long> d = new AsyncTask<String, Void, Long>() {

                    @Override
                    protected Long doInBackground(String... strings) {
                        DatabaseManager dbM = DatabaseManager.getInstance();
                        SQLiteDatabase db = dbM.openConnection();
                        DBHelper.insererProfil(db, strings[0], strings[1]);
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
                if (number_cal.matches("[1-9]+[0-9]{2,4}"))
                    d.execute(letter_name, number_cal);
                else
                    Toast.makeText(this, "Veuillez entrer un objectif valide", Toast.LENGTH_SHORT).show();

                break;
            case R.id.btn_profil_gouv:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.hc-sc.gc.ca/fn-an/nutrition/reference/table/index-fra.php"));
                startActivity(browserIntent);
                break;
        }
    }
}
