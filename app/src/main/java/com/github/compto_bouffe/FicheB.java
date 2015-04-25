package com.github.compto_bouffe;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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

    private Button recapitulatif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche_b);
        Button today = (Button)findViewById(R.id.btn_today);
        recapitulatif = (Button)findViewById(R.id.btn_recapitulatif);
        Button modifyProfils = (Button)findViewById(R.id.btn_modify_profils);

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
        DatabaseManager dbM = DatabaseManager.getInstance();
        SQLiteDatabase db = dbM.openConnection();
        String nom = DBHelper.getPrenom(db);
        Cursor c = DBHelper.listeObjectifs(db);
        recapitulatif.setEnabled(c.getCount() > 0);
        c.close();
        dbM.close();

        TextView prenom = (TextView)findViewById(R.id.prenom);
        prenom.setText(nom);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
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
