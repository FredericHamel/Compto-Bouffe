package com.github.compto_bouffe.api;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by adkuser on 3/12/15.
 */
public class LabelAPI {

    private static  LabelAPI instance;

    private static final String BASE_URL = "http://api.foodessentials.com/";
    private static final String CREATE_SESSION = "createsession";
    private static final String SEARCH_PRODUCT = "searchprods";
    private static final String PRODUCT_SCORE = "productscore";

    private LabelAPI()
    {

    }

    private HttpEntity getHttp(String url) throws IOException
    {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        HttpResponse res = client.execute(get);
        return res.getEntity();
    }

    /**
     * Initialise et retourne l'unique instance.
     * @return l'unique instance de LabelAPI.
     */
    public static LabelAPI getInstance() {
        if(instance == null)
            instance = new LabelAPI();
        return instance;
    }

    /**
     * Cherche les produits a partir des mots clefs.
     * @param query la recherche.
     * @return la liste des Product, null si il y a un erreur lie au serveur ou liste vide..
     */
    public ArrayList<Product> searchProduct(String query)
    {
        ArrayList<Product> products = null;
        query = query.replaceAll("( )+", "+");
        query = query.toLowerCase();
        try
        {
            String url = BASE_URL + SEARCH_PRODUCT + "?q=" + query + "&sid=" + Constantes.SESSION_KEY + "&s=0&n=300&f=json&v=2.00&api_key=" + Constantes.API_KEY;
            HttpEntity entity = getHttp(url);
            JSONObject obj = new JSONObject(EntityUtils.toString(entity,HTTP.UTF_8));
            int size = obj.getInt("resultSize");
            Log.d("JSON", "Result size: " + size);

            products = new ArrayList<>(size);
            JSONArray array = obj.getJSONArray("productsArray");
            for (int i = 0; i < array.length(); ++i) {
                obj = (JSONObject) array.get(i);
                products.add(new Product(obj.getString("product_name"), obj.getString("product_description"), obj.getString("upc"), obj.getString("product_size")));
            }
        }catch(JSONException e)
        {
            Log.d("JSON", e.getMessage());
        }catch(IOException e)
        {
            Log.d("WEB", e.getMessage());
        }
        return products;
    }

    /**
     * Retourne la liste des nutriments associer au produit.
     * @param p le produit.
     * @return La liste des nutriments ou null si la liste est vide.
     */
    public ArrayList<Nutriment> searchScore(Product p)
    {
        ArrayList<Nutriment> nutriments = null;
        try
        {
            String url = BASE_URL + PRODUCT_SCORE + "?u="+ p.getUpc() +"&sid=" + Constantes.SESSION_KEY + "&f=json&api_key=" + Constantes.API_KEY;
            HttpEntity entity = getHttp(url);
            JSONObject obj = new JSONObject(EntityUtils.toString(entity,HTTP.UTF_8));
            JSONArray array = obj.getJSONArray("nutrients");
            nutriments = new ArrayList<>(array.length());
            for (int i = 0; i < array.length(); ++i) {
                obj = (JSONObject) array.get(i);
                nutriments.add(new Nutriment(obj.getString("nutrient_name"), obj.getString("nutrient_value"), obj.getString("nutrient_uom")));
            }
        }catch(JSONException e)
        {
            Log.d("JSON", e.getMessage());
        }catch(IOException e)
        {
            Log.d("Web", e.getMessage());
        }
        return nutriments;
    }
}
