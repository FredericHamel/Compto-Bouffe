package com.github.compto_bouffe;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.compto_bouffe.api.LabelAPI;
import com.github.compto_bouffe.api.Nutriment;
import com.github.compto_bouffe.api.Product;

import java.util.ArrayList;

public class RecherchePlats extends Activity {

    // Composantes graphiques
    private Button searchBtn, confirm;
    private ImageButton addBtn, subBtn;
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

    // Les listeners
    private AdapterView.OnItemClickListener itemClickListener;
    private View.OnClickListener btnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_plats);
        listProducts = new ArrayList<>();
        myChoice = new ArrayList<>();
        init();
        initAdapter();
        initOnItemClickListener();
        initSearchBtn();
        initClickListener();
        initAddSubBtn();

        resultList.setAdapter(searchProductAdapter);
        myList.setAdapter(myListAdapter);

        resultList.setOnItemClickListener(itemClickListener);
        myList.setOnItemClickListener(itemClickListener);
    }

    // Recupere les objets a partir des id et l'instance de LabelAPI.
    private void init() {
        labelAPI = LabelAPI.getInstance();
        searchBtn = (Button)findViewById(R.id.search_btn);
        queryText = (TextView)findViewById(R.id.query_text);
        resultList = (ListView)findViewById(R.id.result_list);
        myList = (ListView)findViewById(R.id.my_list);
        addBtn = (ImageButton)findViewById(R.id.add_element_btn);
        subBtn = (ImageButton)findViewById(R.id.sub_element_btn);
        confirm = (Button)findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask<ArrayList<ProductQty>, Void, Long> task = new AsyncTask<ArrayList<ProductQty>, Void, Long>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();

                    }

                    // Insert dans la base de donnees les produits choisit par l'usager.

                    @Override
                    protected Long doInBackground(ArrayList<ProductQty>... p) {
                        LabelAPI labelAPI1 = LabelAPI.getInstance();
                        ArrayList<Nutriment> nutriments;
                        DBHelper dbH = new DBHelper(getApplicationContext());
                        SQLiteDatabase db = dbH.getWritableDatabase();

                        for(ProductQty productQty : p[0]) {
                            nutriments = labelAPI1.searchScore(productQty.getProduct());
                            if (nutriments == null) {
                                Toast.makeText(getApplicationContext(), "Nutriment is null", Toast.LENGTH_LONG).show();
                                Log.d("Nutriment", "Null Nutriment");
                            } else {
                                Product pf = productQty.getProduct();
                                Log.d("Nutriment", "Null Nutriment");
                                DBHelper.insererListePlats(db, productQty.getQte(), pf.getUpc(), pf.getName(), pf.getDesc(), nutriments);
                            }
                        }

                        return 0L;
                    }

                    @Override
                    protected void onPostExecute(Long aLong) {
                        super.onPostExecute(aLong);
                        finish();
                    }
                };

                task.execute(myChoice);

            }
        });
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
                if(!text.equals("")) {
                    Toast.makeText(getApplicationContext(), "Recherche en cours...", Toast.LENGTH_SHORT).show();
                    new SearchProduct().execute(text);
                }else
                    Toast.makeText(getApplicationContext(), "Votre recherche est trop courte", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initClickListener()
    {
        btnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i;
                switch(view.getId())
                {
                    case R.id.add_element_btn:
                        i = searchProductAdapter.getSelectedIndex();
                        Log.d("BTN_ADD", "Add item " + i + " a mes choix");
                        if(i > -1) {
                            Product p = listProducts.get(i);
                            for(ProductQty pty : myChoice) {
                                if(pty.getProduct().equals(p)) {
                                    pty.add();
                                    p = null;
                                    break;
                                }
                            }
                            if(p != null)
                                myChoice.add(new ProductQty(p, 1));
                        }

                        break;
                    case R.id.sub_element_btn:
                        i = myListAdapter.getSelectedIndex();
                        if(i > -1) {
                            ProductQty pty = myChoice.get(i);
                            if (pty.getQte() == 1) {
                                myChoice.remove(i);
                                myListAdapter.setSelectedIndex(-1);
                            }
                            pty.sub();
                        }

                }
                myListAdapter.notifyDataSetChanged();
            }
        };
    }

    private void initOnItemClickListener()
    {
        itemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Adapter adapter = adapterView.getAdapter();
                if(adapter == myListAdapter) {
                    myListAdapter.setSelectedIndex(i);
                    searchProductAdapter.setSelectedIndex(-1);
                }else {
                    searchProductAdapter.setSelectedIndex(i);
                    myListAdapter.setSelectedIndex(-1);
                }
                searchProductAdapter.notifyDataSetChanged();
                myListAdapter.notifyDataSetChanged();
                Log.d("ResultList","Adapter " + adapter.getClass().toString() +  ". Item " + i + " selected(" + view.isSelected() + ") or pressed(" + view.isPressed() + ")");
            }
        };
    }
    private void initAddSubBtn() {
        addBtn.setOnClickListener(btnClickListener);
        subBtn.setOnClickListener(btnClickListener);
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
        private int selectedIndex;

        private SearchProductAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.selectedIndex = -1;
        }

        public void setSelectedIndex(int i) {
            selectedIndex = i;
        }

        public int getSelectedIndex()
        {
            return selectedIndex;
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
            Log.d("SearchProductAdapter", "getView(" + position + ")");
            TextView tv1 = (TextView)view.findViewById(R.id.text1);
            TextView tv2 = (TextView)view.findViewById(R.id.text2);
            Product p = listProducts.get(position);
            tv1.setText(p.getName());
            tv2.setText(p.getSize());
            int color = selectedIndex == position ? R.color.light_blue: R.color.white;
            tv1.setBackgroundResource(color);
            tv2.setBackgroundResource(color);
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
        private int selectedIndex;
        private AdapterView.OnItemClickListener listener;

        public MyListAdapter() {
            this.inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.selectedIndex = -1;

        }



        public void setSelectedIndex(int i)
        {
            if(i < myChoice.size())
                selectedIndex = myChoice.size()- i - 1;
        }

        public int getSelectedIndex()
        {
            return selectedIndex;
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
            int reverseOrder = myChoice.size() - i - 1;

            Log.d("MyListAdapter", "getView(" + i + ")");
            TextView tv1 = (TextView)view.findViewById(R.id.text1);
            TextView tv2 = (TextView)view.findViewById(R.id.text2);
            TextView tv3 = (TextView)view.findViewById(R.id.text3);
            ProductQty productQty = myChoice.get(reverseOrder);
            tv1.setText(String.valueOf(productQty.getQte()));
            tv2.setText(productQty.getProduct().getName());
            tv3.setText(productQty.getProduct().getSize());
            int color = reverseOrder == selectedIndex ? R.color.light_blue:
                    reverseOrder % 2 == 1 ? R.color.grisRangee1 : R.color.grisRangee2;

            tv1.setBackgroundResource(color);
            tv2.setBackgroundResource(color);
            tv3.setBackgroundResource(color);
            return view;
        }
    }
}
