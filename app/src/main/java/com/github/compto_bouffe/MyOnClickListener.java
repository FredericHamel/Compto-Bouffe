package com.github.compto_bouffe;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

/**
 * Created by sr on 2015-03-26.
 */

public class MyOnClickListener extends ActionBarActivity implements View.OnClickListener{
    public void onClick(View v){
        if(v.getId() == R.id.versC){
            startActivity(new Intent(getApplicationContext(), FicheC.class));
        }
    }
}