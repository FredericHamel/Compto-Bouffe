package com.github.compto_bouffe;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
import com.github.compto_bouffe.api.ProductQty;

import java.util.ArrayList;
import java.util.Calendar;

public class RecherchePlats extends Activity {

    // Database
    private DatabaseManager dbM;
    private SQLiteDatabase db;

    // Composantes graphiques
    private Button searchBtn, confirm, recent, resultat;
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
        myChoice = new ArrayList<>();
        listProducts = new ArrayList<>();
        dbM = DatabaseManager.getInstance();
        db = dbM.openConnection();
        init();
        initAdapter();
        initConfirmButton();
        initTabsBtn();
        initOnItemClickListener();
        initSearchBtn();
        initClickListener();
        initAddSubBtn();

        resultList.setAdapter(searchProductAdapter);
        myList.setAdapter(myListAdapter);

        resultList.setOnItemClickListener(itemClickListener);
        myList.setOnItemClickListener(itemClickListener);
        initMyListContent();
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
        recent = (Button)findViewById(R.id.recent_btn);
        resultat = (Button)findViewById(R.id.result_btn);
        recent.setEnabled(false);
    }

    // Initiailise les Adapter
    private void initAdapter() {
        // le cursor doit etre initialise correctement, pas a null.
        Cursor c = DBHelper.listePlatsDateCourante(db);
        searchProductAdapter = new SearchProductAdapter(this, c);
        myListAdapter = new MyListAdapter();
    }

    private void initMyListContent() {
        AsyncTask<Cursor, Void, ArrayList<ProductQty>> task = new AsyncTask<Cursor, Void, ArrayList<ProductQty>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected ArrayList<ProductQty> doInBackground(Cursor... cs) {
                Cursor c = cs[0];
                Log.d(getClass().toString(),"Cursor size: " +  c.getCount());
                ArrayList<ProductQty> productQtyArrayList  = new ArrayList<>(c.getCount());
                if(c.getCount() > 0) {
                    ProductQty productQty;
                    c.moveToFirst();
                    do {
                        productQty = new ProductQty(
                            new Product(c.getString(c.getColumnIndex(DBHelper.L_NOM)), "",
                                c.getString(c.getColumnIndex(DBHelper.L_UPC)),
                                c.getString(c.getColumnIndex(DBHelper.L_SIZE)) ),
                            c.getInt(c.getColumnIndex(DBHelper.L_QUANTITE)));

                        Log.d("Product", productQty.getProduct().toString());
                        productQtyArrayList.add(productQty);
                    } while (c.moveToNext());
                }
                c.close();
                return productQtyArrayList;
            }

            @Override
            protected void onPostExecute(ArrayList<ProductQty> productQties) {
                super.onPostExecute(productQties);
                myChoice = productQties;
                myListAdapter.notifyDataSetChanged();
            }
        };

        Cursor c = DBHelper.listePlatsDateCourante(db);
        task.execute(c);
    }

    // Initialise la logic du bouton de recherche
    private void initSearchBtn() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = queryText.getText().toString();
                searchProductAdapter.setSelectedIndex(-1);
                if(!text.equals("")) {
                    Toast.makeText(getApplicationContext(), "Recherche en cours...", Toast.LENGTH_SHORT).show();
                    new SearchProduct().execute(text);
                }else
                    Toast.makeText(getApplicationContext(), "Vous devez specifier une recherche", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initConfirmButton()
    {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask<ArrayList<ProductQty>, Void, Long> task = new AsyncTask<ArrayList<ProductQty>, Void, Long>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        confirm.setEnabled(false);
                    }

                    // Insert dans la base de donnees les produits choisit par l'usager.
                    @SafeVarargs
                    @Override
                    protected final Long doInBackground(ArrayList<ProductQty>... p) {
                        LabelAPI labelAPI1 = LabelAPI.getInstance();
                        ArrayList<Nutriment> nutriments;
                        DBHelper dbH = new DBHelper(getApplicationContext());
                        SQLiteDatabase db = dbH.getWritableDatabase();

                        Calendar mcurrentDate=Calendar.getInstance();
                        int mYear = mcurrentDate.get(Calendar.YEAR);
                        int mMonth=mcurrentDate.get(Calendar.MONTH);
                        int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                        String dateCourante=Integer.toString(mYear)+"-"+Integer.toString(mMonth)+"-"+Integer.toString(mDay);
                        String base_query = "SELECT " + DBHelper.L_UPC + " FROM " + DBHelper.TABLE_LISTEPLATS + " WHERE "+DBHelper.L_DATEENTREE+"='"+dateCourante+"' AND "+DBHelper.L_UPC+ "=";
                        for(ProductQty productQty : p[0]) {
                            Product pf = productQty.getProduct();
                            Cursor c = db.rawQuery(base_query + "'"+ pf.getUpc()+"'" + ";", null);
                            Log.d("SQL", base_query+ "'"+ pf.getUpc()+"'" + ";");
                            Log.d("Cursor", Integer.toString(c.getCount()));
                            if(c.getCount() == 0) {
                                nutriments = labelAPI1.searchScore(productQty.getProduct());
                                DBHelper.insererListePlats(db, productQty.getQte(), pf.getUpc(), pf.getName(), pf.getSize(), nutriments);
                            }else {
                                ContentValues cv = new ContentValues();
                                cv.put(DBHelper.L_QUANTITE, productQty.getQte());
                                db.update(DBHelper.TABLE_LISTEPLATS, cv, DBHelper.L_UPC+"='"+pf.getUpc()+"'", null);
                            }
                            c.close();
                        }
                        return 0L;
                    }

                    @Override
                    protected void onPostExecute(Long aLong) {
                        super.onPostExecute(aLong);
                        confirm.setEnabled(true);
                        finish();
                    }
                };

                //noinspection unchecked
                task.execute(myChoice);
            }
        });
    }

    public void initTabsBtn()
    {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId())
                {
                    case R.id.recent_btn:
                        searchProductAdapter.setRecent(true);
                        break;
                    case R.id.result_btn:
                        searchProductAdapter.setRecent(false);
                        break;
                }
                searchProductAdapter.notifyDataSetChanged();
                recent.setEnabled(resultat.isEnabled());
                resultat.setEnabled(!recent.isEnabled());
            }
        };
        recent.setOnClickListener(listener);
        resultat.setOnClickListener(listener);
    }

    private void initClickListener() {
        btnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i;
                switch (view.getId()) {
                    case R.id.add_element_btn:
                        i = searchProductAdapter.getSelectedIndex();
                        myListAdapter.setSelectedIndex(-1);
                        Log.d("BTN_ADD", "Add item " + i + " a mes choix");
                        if (i > -1) {
                            Product p = (Product)searchProductAdapter.getItem(i);
                            for (ProductQty pty : myChoice) {
                                if (pty.getProduct().equals(p)) {
                                    pty.add();
                                    p = null;
                                    break;
                                }
                            }
                            if (p != null)
                                myChoice.add(new ProductQty(p, 1));
                        }

                        break;
                    case R.id.sub_element_btn:
                        i = myListAdapter.getSelectedIndex();
                        if (i > -1 && i < myChoice.size()) {
                            ProductQty pty = myChoice.get(i);
                            if (pty.getQte() > 0)
                                pty.sub();
                            else {
                                myListAdapter.setSelectedIndex(-1);
                            }
                        }
                }
                myListAdapter.notifyDataSetChanged();
            }
        };
    }

    private void initOnItemClickListener() {
        itemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Adapter adapter = adapterView.getAdapter();
                if (adapter == myListAdapter) {
                    myListAdapter.setSelectedIndex(i);
                    searchProductAdapter.setSelectedIndex(-1);
                } else {
                    searchProductAdapter.setSelectedIndex(i);
                    myListAdapter.setSelectedIndex(-1);
                }
                searchProductAdapter.notifyDataSetChanged();
                myListAdapter.notifyDataSetChanged();
                Log.d("ResultList", "Adapter " + adapter.getClass().toString() + ". Item " + i + " selected(" + view.isSelected() + ") or pressed(" + view.isPressed() + ")");
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
    protected void onDestroy() {
        super.onDestroy();
        searchProductAdapter.getCursor().close();
        dbM.close();
        Log.d("SQLite", "NbConnection to SQLDatabase="+dbM.getNbConnection());
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
            searchBtn.setEnabled(!searchBtn.isEnabled());
        }

        @Override
        protected void onPostExecute(ArrayList<Product> products) {
            super.onPostExecute(products);
            searchBtn.setEnabled(!searchBtn.isEnabled());
            if (products == null) {
                Toast.makeText(RecherchePlats.this, "Problème de connexion, réessayez plus tard.", Toast.LENGTH_LONG).show();
                products = new ArrayList<>();
            }else if(products.size() == 0) {
                Toast.makeText(RecherchePlats.this, getString(R.string.empty_answer), Toast.LENGTH_LONG).show();
                products = new ArrayList<>();
            }
            RecherchePlats.this.listProducts =  products;
            searchProductAdapter.setRecent(false);
            recent.setEnabled(true);
            resultat.setEnabled(false);
            searchProductAdapter.notifyDataSetChanged();
        }
    }

    private class SearchProductAdapter extends CursorAdapter {
        private LayoutInflater inflater;
        private int selectedIndex;
        private boolean isRecent;

        private SearchProductAdapter(Context context, Cursor c) {
            super(context, c, false);
            this.isRecent = true;
            this.inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.selectedIndex = -1;
        }

        public void setSelectedIndex(int i) {
            selectedIndex = i;
        }

        public int getSelectedIndex() {
            return selectedIndex;
        }

        public void setRecent(boolean value) {
            this.isRecent = value;
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return isRecent ? super.getCount() : listProducts.size();
        }

        @Override
        public Object getItem(int position) {
            Object obj = null;
            if(position > -1)
                if(isRecent) {
                    Cursor c = getCursor();
                    if(position < c.getCount()) {
                        c.moveToPosition(position);
                        obj = new Product(c.getString(c.getColumnIndex(DBHelper.L_NOM)), "",
                                c.getString(c.getColumnIndex(DBHelper.L_UPC)),
                                c.getString(c.getColumnIndex(DBHelper.L_SIZE)));
                    }
                } else {
                    if(position < listProducts.size())
                        obj = listProducts.get(position);
                }
            return obj;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            if (view == null) {
                view = inflater.inflate(R.layout.activity_recherche_resultat_row, parent, false);
            }
            Log.d("SearchProductAdapter", "getView(" + position + ")");
            TextView tv1 = (TextView) view.findViewById(R.id.text1);
            TextView tv2 = (TextView) view.findViewById(R.id.text2);
            if (isRecent) {
                Cursor c = getCursor();
                c.moveToPosition(position);
                tv1.setText(c.getString(c.getColumnIndex(DBHelper.L_NOM)));
                tv2.setText(c.getString(c.getColumnIndex(DBHelper.L_SIZE)));
            } else {
                Product p = listProducts.get(position);
                tv1.setText(p.getName());
                tv2.setText(p.getSize());
            }
            int color = selectedIndex == position ? R.color.light_blue:
                    position % 2 == 1 ? R.color.grisRangee1 : R.color.grisRangee2;
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

        public MyListAdapter() {
            this.inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.selectedIndex = -1;
        }

        public void setSelectedIndex(int i)
        {
            if(i < myChoice.size())
                selectedIndex = myChoice.size() - i - 1;
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
