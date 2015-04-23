package com.github.compto_bouffe;

import android.app.Activity;
import android.content.Intent;
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
            //Toast.makeText(this, "Bonjour " + letter_name + " votre objectif de ce jour est de " + number_cal + " calories" , Toast.LENGTH_LONG).show();

            //code pour passer de la fiche a à la fiche b

            //Button button = (Button) v;
            AsyncTask<String, Void, Long> d = new AsyncTask<String, Void, Long>(){

                @Override
                protected Long doInBackground(String... strings) {
                    DatabaseManager dbM = DatabaseManager.getInstance();
                    SQLiteDatabase db = dbM.openConnection();
                    DBHelper.changerInformations(db, strings[0], strings[1]);
                    dbM.close();
                    return null;
                }

                @Override
                protected void onPostExecute(Long aLong) {
                    startActivity(new Intent(getApplicationContext(), FicheB.class));
                    super.onPostExecute(aLong);
                }

            };
            if(number_cal.matches("[1-9]+[0-9]{2,4}"))
                d.execute(letter_name, number_cal);
            else
                Toast.makeText(this, "Veuillez entrer un objectif valide", Toast.LENGTH_SHORT).show();
        }
        //code pour accéder à la documentation du gouvernement
        if(v.getId()==R.id.button2){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.hc-sc.gc.ca/fn-an/nutrition/reference/table/index-fra.php"));
            startActivity(browserIntent);
        }
    }
}
