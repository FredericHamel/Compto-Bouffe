package com.github.compto_bouffe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 * La fiche C permet à l'usager de consulter et de modifier son menu du jour.
 */
public class FicheC extends Activity {

    private DatabaseManager dbM;
    private SQLiteDatabase db;

    private TextView caloriesIngerer;
    private TextView caloriesRestant;

    private ListView listePlats;
    private MyAdapter adapter;

    private LinearLayout btnContainer;
    private Button btnAddPlat;
    private Button btnModifyMenu;

    private AsyncTask<PageInfo, Void, PageInfo> updater;
    private PageInfo pageInfo;

    private MenuItem next, preview;

    private ImageView imageResultat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche_c);
        Bundle bundle = getIntent().getExtras();
        init();
        initListener();
        initAdapter(bundle);
        initUpdater();
    }

    // Initialise les reference sur les elements graphique et logique
    private void init()
    {
        pageInfo = new PageInfo();
        dbM = DatabaseManager.getInstance();
        db = dbM.openConnection();

        caloriesIngerer = (TextView)findViewById(R.id.textViewCalIng);
        caloriesRestant = (TextView)findViewById(R.id.textViewCalRes);

        listePlats = (ListView)findViewById(R.id.list_nutri);

        imageResultat = (ImageView)findViewById(R.id.imageR);

        btnContainer = (LinearLayout)findViewById(R.id.btn_menu);
        btnAddPlat = (Button)findViewById(R.id.add_plat);
        btnModifyMenu = (Button)findViewById(R.id.modify);
    }

    // Actualise l'image indiquant le status de l'objectif.
    private void updateImage(int calObjectif, int calIngeree ){
        int ecart =100* Math.abs(calObjectif-calIngeree);
        // 100*Math.abs(objInitial - resultat) <= objInitial*marge
        if(ecart <= calObjectif*DBHelper.getMarge(db)){
            imageResultat.setImageResource(R.drawable.vert);
        }else{
            imageResultat.setImageResource(R.drawable.rouge);
        }
    }


    // Initialise l'ecouteur d'evenements.
    private void initListener()
    {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.add_plat:
                        startActivity(new Intent(FicheC.this, RecherchePlats.class));
                        break;
                    case R.id.modify:
                        startActivity(new Intent(FicheC.this, ModifierMaListe.class));
                        break;
                }
            }
        };
        btnAddPlat.setOnClickListener(listener);
        btnModifyMenu.setOnClickListener(listener);
    }

    // Initialise l'adapteur de la ListView.
    private void initAdapter(Bundle bundle)
    {
        final int position = bundle == null ? 0: bundle.getInt("position");

        AsyncTask<Integer, Void, PageInfo> task = new AsyncTask<Integer, Void, PageInfo>() {
            @Override
            protected PageInfo doInBackground(Integer... integers) {
                PageInfo pageInfo = new PageInfo();
                pageInfo.position = integers[0];
                pageInfo.listePages = DBHelper.listeObjectifs(db);
                pageInfo.listePages.moveToPosition(pageInfo.position);
                String date = pageInfo.listePages.getString(pageInfo.listePages.getColumnIndex(DBHelper.R_DATE));
                pageInfo.page = DBHelper.listePlats(db, date);
                return pageInfo;
            }

            @Override
            protected void onPostExecute(PageInfo pageInfo) {
                super.onPostExecute(pageInfo);
                FicheC.this.pageInfo.page = pageInfo.page;
                FicheC.this.pageInfo.position = pageInfo.position;
                FicheC.this.pageInfo.listePages = pageInfo.listePages;
                updateTitle(pageInfo);

                int objectif = pageInfo.listePages.getInt(pageInfo.listePages.getColumnIndex(DBHelper.R_OBJECTIF_INIT));
                int ingerer = pageInfo.listePages.getInt(pageInfo.listePages.getColumnIndex(DBHelper.R_OBJECTIF_RES));
                caloriesRestant.setText(String.valueOf(objectif - ingerer));
                caloriesIngerer.setText(String.valueOf(ingerer));
                if(!pageInfo.listePages.isFirst())
                    btnContainer.setVisibility(View.GONE);
                if(adapter == null) {
                    adapter = new MyAdapter(getApplicationContext(), pageInfo.page);
                    listePlats.setAdapter(adapter);
                }
                btnModifyMenu.setEnabled(pageInfo.page.getCount() != 0);
                int image = 100*Math.abs(objectif - ingerer) <= objectif*pageInfo.listePages.getInt(pageInfo.listePages.getColumnIndex(DBHelper.R_MARGE)) ? R.drawable.vert: R.drawable.rouge;
                imageResultat.setImageResource(image);
            }
        };
        task.execute(position);
    }

    // Initialise le Asynctask utilise pour actualise les donnees
    //  liees a la base de donnees.
    // Doit etre appele avant updater.execute()
    private void initUpdater()
    {
        updater = new AsyncTask<PageInfo, Void, PageInfo>() {
            @Override
            protected PageInfo doInBackground(PageInfo... pageInfos) {
                PageInfo pageInfo = pageInfos[0];
                int position = pageInfo.listePages.getPosition();
                if(position == 0)
                {
                    Cursor listePlatsDateCourante = DBHelper.listePlatsDateCourante(db);
                    if(listePlatsDateCourante.getCount() > 0)
                    {
                        listePlatsDateCourante.moveToFirst();
                        double resultat = 0;
                        do {
                            int qte = listePlatsDateCourante.getInt(listePlatsDateCourante.getColumnIndex(DBHelper.L_QUANTITE));
                            resultat += qte*Double.parseDouble(listePlatsDateCourante.getString(listePlatsDateCourante.getColumnIndex(DBHelper.L_CALORIES)).split(" ")[0]);
                        } while (listePlatsDateCourante.moveToNext());
                        ContentValues cv = new ContentValues();
                        cv.put(DBHelper.R_OBJECTIF_RES, (int)resultat);
                        db.update(DBHelper.TABLE_RESULTATS, cv, DBHelper.R_DATE + "='" + DBHelper.getDateCourante() + "'", null);
                    }
                    listePlatsDateCourante.close();
                    pageInfo.listePages = DBHelper.listeObjectifs(db);
                }
                pageInfo.listePages.moveToPosition(pageInfo.position);
                pageInfo.page = DBHelper.listeObjectifs(db);
                String date = pageInfo.listePages.getString(pageInfo.listePages.getColumnIndex(DBHelper.R_DATE));
                pageInfo.page = DBHelper.listePlats(db, date);
                return pageInfo;
            }

            @Override
            protected void onPostExecute(PageInfo pageInfo) {
                super.onPostExecute(pageInfo);
                updateTitle(pageInfo);
                adapter.changeCursor(pageInfo.page);
                int objectif = pageInfo.listePages.getInt(pageInfo.listePages.getColumnIndex(DBHelper.R_OBJECTIF_INIT));
                int ingerer = pageInfo.listePages.getInt(pageInfo.listePages.getColumnIndex(DBHelper.R_OBJECTIF_RES));
                caloriesRestant.setText(String.valueOf(objectif-ingerer));
                caloriesIngerer.setText(String.valueOf(ingerer));
                if(!pageInfo.listePages.isFirst())
                    btnContainer.setVisibility(View.GONE);
                else
                    btnContainer.setVisibility(View.VISIBLE);
                btnModifyMenu.setEnabled(pageInfo.page.getCount() != 0);
                updateActionBarBtn(pageInfo);
                updateImage(objectif, ingerer);

            }
        };
    }

    // Actualise le titre de la page selon la date du menu.
    private void updateTitle(PageInfo pageInfo)
    {
        if(pageInfo.position == 0)
            setTitle(R.string.today);
        else {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
            String date = pageInfo.listePages.getString(pageInfo.listePages.getColumnIndex(DBHelper.R_DATE));
            try {
                String MONTH[] = getResources().getStringArray(R.array.mois);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(formater.parse(date));
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                int year = calendar.get(Calendar.YEAR);

                date = String.format("%s %s %d", dayOfMonth, MONTH[(month + 1)%12 ], year);
            }catch (ParseException e)
            {
                Log.d("Formater", "Invalid Date Format " + date);
            }
            setTitle(date);
        }
    }

    // Actualise les boutons de navigation inter-menu.
    private void updateActionBarBtn(PageInfo pageInfo)
    {
        next.setVisible(pageInfo.position != 0);
        preview.setVisible(!pageInfo.listePages.isLast());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        update();
    }

    // Update l'interface.
    private void update() {
        initUpdater();
        updater.execute(pageInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        next = menu.findItem(R.id.next);
        preview = menu.findItem(R.id.preview);
        updateActionBarBtn(pageInfo);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int i = item.getItemId();
        switch (i)
        {
            case R.id.next:
                if(pageInfo.position >= 1) {
                    pageInfo.position--;
                    update();
                }
                break;
            case R.id.preview:
                if(pageInfo.position < pageInfo.listePages.getCount()-1) {
                    pageInfo.position++;
                    update();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbM.close();
    }

    // Structure contenant les informations de la page courante.
    private class PageInfo {
        public int position;
        public Cursor listePages;
        public Cursor page;
    }

    // L'adapter utilise pour remplir la liste deroulante.
    private class MyAdapter extends CursorAdapter {
        LayoutInflater inflater;

        public MyAdapter(Context context, Cursor c) {
            super(context, c, false);
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if(v==null){
                v = inflater.inflate(R.layout.activity_fiche_c_row, parent, false);
            }

            Cursor c = getCursor();
            c.moveToPosition(position);
            Integer qtity = c.getInt(c.getColumnIndex(DBHelper.L_QUANTITE));
            String name = c.getString(c.getColumnIndex(DBHelper.L_NOM));
            //String size = c.getString(c.getColumnIndex(DBHelper.L_SIZE));
            String cal = c.getString(c.getColumnIndex(DBHelper.L_CALORIES));
            String sucre = c.getString(c.getColumnIndex(DBHelper.L_SUGARS));
            String gras = c.getString(c.getColumnIndex(DBHelper.L_TOTALFAT));
            String prot = c.getString(c.getColumnIndex(DBHelper.L_PROTEIN));


            TextView q = (TextView)v.findViewById(R.id.quantity);
            TextView n = (TextView)v.findViewById(R.id.food_name);
            TextView calo = (TextView)v.findViewById(R.id.calorie);
            TextView s = (TextView)v.findViewById(R.id.sucre);
            TextView g = (TextView)v.findViewById(R.id.gras);
            TextView p = (TextView)v.findViewById(R.id.proteines);

            q.setText(Integer.toString(qtity));
            n.setText(name);
            calo.setText(newQuantite(cal, qtity));
            s.setText(newQuantite(sucre, qtity));
            g.setText(newQuantite(gras, qtity));
            p.setText(newQuantite(prot, qtity));

            int color = position % 2 == 1 ? R.color.grisRangee1 : R.color.grisRangee2;
            q.setBackgroundResource(color);
            n.setBackgroundResource(color);
            calo.setBackgroundResource(color);
            s.setBackgroundResource(color);
            g.setBackgroundResource(color);
            p.setBackgroundResource(color);
            Log.d("adapterFicheC","position" + position );
            return v;
        }

        private String newQuantite(String n, int q)
        {
            String[] parts = n.split(" ");
            double d = Double.parseDouble(parts[0]) * q;
            return String.format("%.1f %s", d, parts.length == 1? "": parts[1]);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return null;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

        }
    }
}
