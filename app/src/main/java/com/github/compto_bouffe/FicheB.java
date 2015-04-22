package com.github.compto_bouffe;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// La fiche B permet à l'usager, par trois boutons:
// - d'accéder au menu du jour;
// - d'accéder au récapitulatif des derniers jours;
// - de modifier ses informations
public class FicheB extends Activity implements View.OnClickListener {

    DatabaseManager dbM;
    //Button today;
    //Button bton1;          //bouton "Aujourd'hui"
    Button bton2;          //bouton "Récapitulatif"
    Button bton3;          //bouton "Modifier les infos"


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche_b);
        dbM = DatabaseManager.getInstance();
        SQLiteDatabase db = dbM.openConnection();
        String nom = DBHelper.getPrenom(db);
        dbM.close();

        TextView prenom = (TextView)findViewById(R.id.textView2);
        prenom.setText(nom);

        //bton1 = (Button)findViewById(R.id.button);
        bton2 = (Button)findViewById(R.id.button2);
        bton3 = (Button)findViewById(R.id.button3);

        //bton1.setOnClickListener(this);
        bton2.setOnClickListener(this);
        bton3.setOnClickListener(this);
    }

    public void buttonTodayOnClick(View v){
        //Button versC = (Button) v;
        startActivity(new Intent(getApplicationContext(), FicheC.class));
    }

    public void onClick(View v) {

        if(v.getId()==R.id.button){


            //code pour passer de la fiche b à la fiche c

            //Button button = (Button) v;
            startActivity(new Intent(getApplicationContext(), FicheC.class));


        }
        
        if(v.getId()==R.id.button2){
            //Button button = (Button) v;
            startActivity(new Intent(getApplicationContext(), Recapitulatif.class));
        }

        //code pour retourner à la fiche a pour modifier les informations

        if(v.getId()==R.id.button3){

            //Button button = (Button) v;
            startActivity(new Intent(getApplicationContext(), FicheA.class));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fiche_b, menu);
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
}
