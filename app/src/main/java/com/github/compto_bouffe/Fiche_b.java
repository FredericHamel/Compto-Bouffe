package com.github.compto_bouffe;

/**
 * Created by Jessica_bonou on 15-04-06.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;



public class Fiche_b extends ActionBarActivity implements View.OnClickListener {

    Button bton1;          //bouton "Aujourd'hui"
    Button bton2;          //bouton "Récapitulatif"
    Button bton3;          //bouton "Modifier les infos"


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feuille_b);

        bton1 = (Button)findViewById(R.id.button);
        bton2 = (Button)findViewById(R.id.button2);
        bton3 = (Button)findViewById(R.id.button3);

        bton1.setOnClickListener(this);
        bton2.setOnClickListener(this);
        bton3.setOnClickListener(this);


    }

    /**public void buttonOnClick(View v){
     Button button = (Button) v;
     startActivity(new Intent(getApplicationContext(), fiche_c.class));
     }**/

    public void onClick(View v) {

        if(v.getId()==R.id.button){


            //code pour passer de la fiche b à la fiche c

            Button button = (Button) v;
            startActivity(new Intent(getApplicationContext(), Fiche_c.class));


        }


        //code pour retourner à la fiche a pour modifier les informations

        if(v.getId()==R.id.button3){

            Button button = (Button) v;
            startActivity(new Intent(getApplicationContext(), Fiche_a.class));

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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



