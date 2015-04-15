package com.github.compto_bouffe;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.compto_bouffe.api.LabelAPI;
import com.github.compto_bouffe.api.Product;

import java.util.ArrayList;

public class RecherchePlats extends Activity {

    private TextView queryText;
    private Button sendQueryBtn;
    private ArrayList<Product> products;
    private LabelAPI labelAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_plats);
        labelAPI = LabelAPI.getInstance();
        // get queryText
        queryText = (TextView)findViewById(R.id.query_text);
        sendQueryBtn = (Button)findViewById(R.id.send_request);

        sendQueryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = queryText.getText().toString();
                if(!text.equals("")) {

                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recherche_plats, menu);
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

    private class SearchProduct extends AsyncTask<String, Integer, Long>
    {
        @Override
        protected Long doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
        }
    }
}
