package com.github.compto_bouffe;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.compto_bouffe.api.LabelAPI;
import com.github.compto_bouffe.api.Product;

import java.util.ArrayList;

public class RecherchePlats extends Activity {

    // Composantes graphiques
    private Button searchBtn;//, add, sub, confirm;
    private TextView queryText;
    private ListView resultList, myList;

    // Composantes Logiques
    private LabelAPI labelAPI;
    private ArrayList<Product> listProducts;
    // Produit avec sa quantite.
    private ArrayList<ProductQty> myChoice;

    // Les Adapters
    private SearchProductAdapter searchProductAdapter;
    private MyListAdapter myListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_plats);
        listProducts = new ArrayList<>();
        myChoice = new ArrayList<>();
        myChoice.add(new ProductQty(new Product("Red Apple", "Fruit", "", "1 lb"), 50));
        myChoice.add(new ProductQty(new Product("Green Apple", "Fruit", "", "1 lb"), 50));
        init();
        initAdapter();
        initSearchBtn();


        resultList.setAdapter(searchProductAdapter);
        myList.setAdapter(myListAdapter);
    }

    // Recupere les objets a partir des id et l'instance de LabelAPI.
    private void init() {
        labelAPI = LabelAPI.getInstance();
        searchBtn = (Button)findViewById(R.id.search_btn);
        queryText = (TextView)findViewById(R.id.query_text);
        resultList = (ListView)findViewById(R.id.result_list);
        myList = (ListView)findViewById(R.id.my_list);
    }

    // Initiailise les Adapter
    private void initAdapter()
    {
        // le cursor doit etre initialise correctement, pas a null.
        searchProductAdapter = new SearchProductAdapter(this, null, false);
        myListAdapter = new MyListAdapter();
    }

    // Initialise la logic du bouton de recherche
    private void initSearchBtn() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = queryText.getText().toString();
                if(!text.equals(""))
                    new SearchProduct().execute(text);
            }
        });
    }

    private void initSearchResultListView() {

    }

    private void initAddBtn() {
    }

    private void initSubBtn() {

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

    private class ProductQty {
        private Product product;
        private int qte;

        public ProductQty(Product p, int q) {
            this.product = p;
            this.qte = q;
        }

        public void add() {
            ++qte;
        }

        public void sub() {
            if(qte > 0)
                --qte;
        }

        public Product getProduct() {
            return product;
        }

        public int getQte() {
            return qte;
        }
    }

    private class SearchProduct extends AsyncTask<String, Void, ArrayList<Product>> {
        @Override
        protected ArrayList<Product> doInBackground(String... strings) {
            Log.d("WEB", "Search for " + strings[0]);
            return labelAPI.searchProduct(strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Place indicateur execution
        }

        @Override
        protected void onPostExecute(ArrayList<Product> products) {
            super.onPostExecute(products);
            if (products == null) {
                Toast.makeText(RecherchePlats.this, "Probleme de connection, reessayer plus tard.", Toast.LENGTH_LONG).show();
                products = new ArrayList<>();
            }else if(products.size() == 0) {
                Toast.makeText(RecherchePlats.this, getString(R.string.empty_answer), Toast.LENGTH_LONG).show();
                products = new ArrayList<>();
            }
            RecherchePlats.this.listProducts =  products;
            searchProductAdapter.notifyDataSetChanged();
        }
    }

    private class SearchProductAdapter extends CursorAdapter {
        private LayoutInflater inflater;

        private SearchProductAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return listProducts.size();
        }

        @Override
        public Object getItem(int i) {
            return listProducts.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            if(view == null)
            {
                view = inflater.inflate(R.layout.activity_recherche_resultat_row, parent, false);
            }

            TextView tv1 = (TextView)view.findViewById(R.id.text1);
            TextView tv2 = (TextView)view.findViewById(R.id.text2);
            Product p = listProducts.get(position);
            tv1.setText(p.getName());
            tv2.setText(p.getSize());
            return view;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return null;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

        }
    }

    private class MyListAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyListAdapter() {
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return myChoice.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            if(view == null)
            {
                view = inflater.inflate(R.layout.activity_recherche_my_list_row, parent, false);
            }
            Log.d("MyListAdapter", "getView(" + i + ")");
            TextView tv1 = (TextView)view.findViewById(R.id.text1);
            TextView tv2 = (TextView)view.findViewById(R.id.text2);
            TextView tv3 = (TextView)view.findViewById(R.id.text3);
            ProductQty productQty = myChoice.get(i);
            tv1.setText(String.valueOf(productQty.getQte()));
            tv2.setText(productQty.getProduct().getName());
            tv3.setText(productQty.getProduct().getSize());

            return view;
        }
    }
}
