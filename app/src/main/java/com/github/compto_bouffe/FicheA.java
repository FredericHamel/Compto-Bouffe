package com.github.compto_bouffe;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;




public class FicheA extends Activity implements View.OnClickListener {

    EditText ed_name;    // le nom du l'utilisateur
    EditText ed_cal;     //le nombre de calories entrées
    Button btn1;          //bouton valider
    Button btn2;          //bouton pour accéder à la documentation


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.feuille_a);

        btn1 = (Button)findViewById(R.id.button);
        btn2 = (Button)findViewById(R.id.button2);
        ed_name = (EditText)findViewById(R.id.name);
        ed_cal = (EditText)findViewById(R.id.objectifs);

        //String letter_name = ed_name.getText().toString();
        //String number_cal = ed_cal.getText().toString();

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        //adapter = new myAdapter();



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



    //@Override
    public void onClick(View v) {

        if(v.getId()==R.id.button){

            //on crée un toast
            String letter_name = ed_name.getText().toString();
            String number_cal = ed_cal.getText().toString();

            int obCal = Integer.parseInt(number_cal);
            //Toast.makeText(this, "Bonjour " + letter_name + " votre objectif de ce jour est de " + number_cal + " calories" , Toast.LENGTH_LONG).show();

            //code pour passer de la fiche a à la fiche b si l,utilisateur rente des objectifs compris entre 1200 et 6000 calories


            if(obCal >= 1200 && obCal <= 6000 ){

                Button button = (Button) v;
                startActivity(new Intent(getApplicationContext(), FicheB.class));

            }
            else{
                Toast.makeText(this, "Bonjour " + letter_name + " veuillez entrer un objectif entre 1200 et 6000 calories "  , Toast.LENGTH_LONG).show();


            }

            //Toast.makeText(this, "Bonjour " + letter_name + " votre objectif de ce jour est de " + number_cal + " calories" , Toast.LENGTH_LONG).show();

            //code pour passer de la fiche a à la fiche b

            //Button button = (Button) v;
            //startActivity(new Intent(getApplicationContext(), FicheB.class));


        }


        //code pour accéder à la documentation du gouvernement

        if(v.getId()==R.id.button2){

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.hc-sc.gc.ca/fn-an/nutrition/reference/table/index-fra.php"));
            startActivity(browserIntent);

        }


    }


}
