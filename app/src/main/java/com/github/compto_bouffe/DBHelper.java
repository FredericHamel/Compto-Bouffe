package com.github.compto_bouffe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.github.compto_bouffe.api.Nutriment;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Sabrina Ouaret on 16/04/15.
 */
public class DBHelper extends SQLiteOpenHelper {

    static final String DB_NAME = "comptoBouffe.db";
    static final int DB_VERSION = 4;
    static final int USER_ID = 1;

    // Table des profils
    static final String TABLE_PROFILS = "Profils";
    static final String P_ID = "ID";
    static final String P_PRENOM ="Prenom";
    static final String P_OBJECTIF ="Objectif";

    // Table de listes des plats
    static final String TABLE_LISTEPLATS = "ListePlats";
    static final String L_USER_ID ="UserID";
    static final String L_ID ="_id";
    static final String L_OBJECTIF ="Objectif";
    static final String L_QUANTITE ="Quantite";
    static final String L_UPC ="UPC";
    static final String L_NOM ="Nom";
    static final String L_SIZE ="Size";
    static final String L_DATEENTREE ="DateEntree";
    static final String L_CALORIES ="Calories";
    static final String L_TOTALFAT ="Total Fat";
    static final String L_SUGARS ="Sugars";
    static final String L_PROTEIN ="Protein";

    // Table des resultats
    static final String TABLE_RESULTATS = "Resultats";
    static final String R_ID = "ID";
    static final String R_OBJECTIF_INIT = "Objectif_initial";
    static final String R_OBJECTIF_RES = "Objectif_resultant";
    static final String R_DATE = "DateR";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String creerTableProfils = "CREATE TABLE IF NOT EXISTS "+TABLE_PROFILS+" ("
                +P_ID+" INT, "
                +P_PRENOM+" TEXT NOT NULL,"
                +P_OBJECTIF+" TEXT NOT NULL,"
                +"PRIMARY KEY("+P_ID+"));";

        String creerTableListe = "CREATE TABLE IF NOT EXISTS "+TABLE_LISTEPLATS+" ("
                +L_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +L_USER_ID +" INTEGER, "
                +L_OBJECTIF+" TEXT NOT NULL, "
                +L_QUANTITE+" INTEGER, "
                +L_UPC+" TEXT NOT NULL, "
                +L_NOM+" TEXT NOT NULL, "
                +L_SIZE+" TEXT, "
                +L_DATEENTREE+" TEXT, "
                +L_CALORIES+" TEXT, "
                +L_SUGARS+" TEXT, `"
                +L_TOTALFAT+"` TEXT, "
                +L_PROTEIN+" TEXT, "
                +"FOREIGN KEY("+ L_USER_ID +") REFERENCES "+TABLE_PROFILS+"("+P_ID+"));";

        String creerTableResultats = "CREATE TABLE IF NOT EXISTS "+TABLE_RESULTATS+" ("
                +R_ID+" INT, "
                +R_OBJECTIF_INIT+" TEXT NOT NULL,"
                +R_OBJECTIF_RES+" TEXT,"
                +R_DATE+" TEXT NOT NULL,"
                +"FOREIGN KEY("+R_ID+") REFERENCES "+TABLE_PROFILS+"("+P_ID+"),"
                +"FOREIGN KEY("+R_OBJECTIF_INIT+") REFERENCES "+TABLE_LISTEPLATS+"("+L_OBJECTIF+"),"
                +"PRIMARY KEY("+R_ID+", "+R_DATE+"));";

        db.execSQL(creerTableProfils);
        db.execSQL(creerTableListe);
        db.execSQL(creerTableResultats);
        Log.d("DB", "DB created");
    }

    // SELECT Prenom FROM Profils;
    public static String getPrenom(SQLiteDatabase db){
        String requete = "SELECT "+P_PRENOM+" FROM "+TABLE_PROFILS+";";
        Cursor c = db.rawQuery(requete, null);
        String prenom = "";
        if(c != null) {
            c.moveToFirst();
            prenom=c.getString(c.getColumnIndex(P_PRENOM));
            c.close();
        }
        return prenom;
    }



    /**
     * Methode qui permet d'inserer pour la premiere fois ou d'updater les informations du profil
     * @param db la base de donnees
     * @param prenom le nouveau prenom
     * @param objectif le nouvel objectif
     */
    public static void changerInformations(SQLiteDatabase db, String prenom, String objectif){
        String requete = "INSERT OR REPLACE INTO "+TABLE_PROFILS+"("+P_PRENOM+", "+P_OBJECTIF
                +") VALUES ("+prenom+", "+objectif+");";
        db.execSQL(requete);
    }


    /**
     * Methode qui renvoie la liste de plats de l'utilisateur a la date courante
     * @param db la base de donnees
     * @return c le curseur;
     */
    public static Cursor listePlatsDateCourante (SQLiteDatabase db){
        Calendar mcurrentDate=Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth=mcurrentDate.get(Calendar.MONTH);
        int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

        String dateCourante=Integer.toString(mYear)+"-"+Integer.toString(mMonth)+"-"+Integer.toString(mDay);

        String requete = "SELECT "+L_ID + ", " +L_QUANTITE+", "+L_NOM+", "+L_SIZE+", "+L_CALORIES+", "+L_SUGARS+", `"
                        +L_TOTALFAT+"`, "+L_PROTEIN+" FROM "+TABLE_LISTEPLATS
                        +" WHERE "+L_DATEENTREE+"='"+dateCourante+"';";
        Log.d("Query", requete);
        Cursor c = db.rawQuery(requete, null);
        return c;
    }

    /**
     * Methode qui permet d'afficher la liste des element deja mange
     * @param db la base de donnees
     * @return c le cursor
     */
    public static Cursor afficherHistorique (SQLiteDatabase db){
        String requete = "SELECT DISTINCT "+L_NOM+", "+L_SIZE+" FROM "+TABLE_LISTEPLATS
                          +" ORDER BY "+L_DATEENTREE
                          +" LIMIT 15;";
        Log.d("Query", requete);
        Cursor c=db.rawQuery(requete, null);
        return c;
    }


    /**
     * Methode qui retourne la quantite d'un plat suivant l'upc du plat
     * @param db la base de donnees
     * @param upc le code du plat
     * @return c le curseur
     */
    public static Cursor getQuantite(SQLiteDatabase db, String upc){
        Calendar mcurrentDate=Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth=mcurrentDate.get(Calendar.MONTH);
        int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

        String dateCourante='"'+Integer.toString(mYear)+"-"+Integer.toString(mMonth)+"-"+Integer.toString(mDay)+'"';

        String requete = "SELECT "+L_QUANTITE+" FROM "+TABLE_LISTEPLATS
                        +" WHERE "+L_UPC+"="+upc
                        +" AND "+L_DATEENTREE+"='"+dateCourante+"';";
        Log.d("Query", requete);
        Cursor c= db.rawQuery(requete, null);
        return c;
    }


    /**
     * Methode qui update la quantite de la rangee concernee dans la base de donnee
     * @param db la base de donnees
     * @param upc le code du plat
     * @param qte la nouvelle quantite
     */
    public static void changerQuantite(SQLiteDatabase db, String upc, int qte) {
        ContentValues v = new ContentValues();
        v.put(L_QUANTITE, qte);
        db.update(TABLE_LISTEPLATS, v, " WHERE "+L_UPC+"="+upc, null);
    }

     /*   // On va chercher la quantite initiale pour pouvoir la comparer avec la nouvelle
        Cursor qteC = getQuantite(db, upc);
        String quantiteAvantChgt = qteC.getString(qteC.getColumnIndex(L_QUANTITE));
        int ancienneQte = Integer.parseInt(quantiteAvantChgt);

        if(ancienneQte!=qte) {
            Cursor c;

            Calendar mcurrentDate = Calendar.getInstance();
            int mYear = mcurrentDate.get(Calendar.YEAR);
            int mMonth = mcurrentDate.get(Calendar.MONTH);
            int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
            String dateCourante = Integer.toString(mYear) + "-" + Integer.toString(mMonth) + "-" + Integer.toString(mDay);

            ContentValues values = new ContentValues();


            String nutriments = "SELECT * FROM " + TABLE_LISTEPLATS
                    + " WHERE " + L_DATEENTREE + "=" + dateCourante
                    + " AND " + L_UPC + "=" + upc + ";";
            c = db.rawQuery(nutriments, null);

            //String recevant les donnees des requetes suivants les colonnes de la base de donnees
            String calories = c.getString(c.getColumnIndex(L_CALORIES));
            String protein = c.getString(c.getColumnIndex(L_PROTEIN));
            String sugars = c.getString(c.getColumnIndex(L_SUGARS));
            String totalFat = c.getString(c.getColumnIndex(L_TOTALFAT));


            ArrayList<String> n = new ArrayList<String>();
            n.add(calories);
            n.add(protein);
            n.add(sugars);
            n.add(totalFat);

            for (String nutr : n) {

                String[] info = nutr.split(" ");
                double info1 = Double.parseDouble(info[0]); // valeur
                String info2 = info[1]; // unite

                if (ancienneQte < qte) {
                    info1 = (qte*info1)/ancienneQte;

                } else if (ancienneQte > qte) {

                }else{ //qte==0

                }
            }

            //Split de chacune des Strings pour distinguer les valeurs des unites
            String[] cal = calories.split(" ");
            String cal1 = cal[0]; // 1500
            String cal2 = cal[1]; // kcal

            String[] prot = protein.split(" ");
            String prot1 = prot[0]; // 12
            String prot2 = prot[1]; // unites

            String[] sug = sugars.split(" ");
            String sug1 = sug[0]; // 150
            String sug2 = sug[1]; // unites

            String[] fat = totalFat.split(" ");
            String fat1 = fat[0]; // 3.2
            String fat2 = fat[1]; // unite


            //Déclarations des nouvelles values des nutriments
            String newCalorie;
            String newProtein;
            String newSugars;
            String newTotalFat;

            Cursor cObj = getObjectif(db);
            String obj = cObj.getString(cObj.getColumnIndex(P_OBJECTIF));


            //Insert dans le contentValue des nouvelles valeurs
            values.put(L_QUANTITE, qte);
            values.put(L_CALORIES, newCalorie);
            values.put(L_PROTEIN, newProtein);
            values.put(L_SUGARS, newSugars);
            values.put(L_TOTALFAT, newTotalFat);

            //Mise a jour de la base de donnees
            db.update(TABLE_LISTEPLATS, values, " WHERE " + L_NOM + "=" + nom, null);
        }
    }*/


    /**
     * Methode qui renvoie la liste des plats d'une journee donnee d'un utilisateur
     * @param db la base de donnees
     * @param date la date de type YYYY-MM-DD dont on veut connaitre la liste des plats
     * @return c le curseur
     */
    public static Cursor listePeriode(SQLiteDatabase db, String date){
        String requete = "SELECT "+L_ID+", "+L_QUANTITE+", "+L_NOM+", "+L_SIZE+", "+L_CALORIES+", "+L_SUGARS+", `"
                        +L_TOTALFAT+"`, "+L_PROTEIN+" FROM "+TABLE_LISTEPLATS
                        +" WHERE "+L_DATEENTREE+"='"+date+"';";
        Cursor c = db.rawQuery(requete, null);
        return c;
    }

    /**
     * Methode qui renvoit une liste  de date et l'objectif du jour et l'objectif resultat associes
     * pour une periode de temps donnee
     * @param db la base de donnees
     * @param dateDebut la date au format YYYY-MM-DD du debut de periode
     * @param dateFin la date au format YYYY-MM-DD de fin de periode
     * @return c le curseur
     */
    public static Cursor listeObjectifs(SQLiteDatabase db, String dateDebut, String dateFin){
        String requete = "SELECT "+R_DATE+", "+R_OBJECTIF_INIT+", "+R_OBJECTIF_RES
                +" FROM "+TABLE_RESULTATS
                +" WHERE "+R_DATE+">="+dateDebut
                +" AND "+R_DATE+"<="+dateFin+";";

        Cursor c = db.rawQuery(requete, null);
        return c;
    }

    /**
     * Methode qui permet de supprimer un plat de la liste de plat de la journee courante
     * @param db la base de donnees
     * @param upc le code du plat
     */
    public static void supprimerPlatListe(SQLiteDatabase db, String upc){
        Calendar mcurrentDate=Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth=mcurrentDate.get(Calendar.MONTH);
        int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

        String dateCourante=Integer.toString(mYear)+"-"+Integer.toString(mMonth)+"-"+Integer.toString(mDay);

        db.delete(TABLE_LISTEPLATS, L_UPC+"="+upc+" AND "+L_DATEENTREE+"='"+dateCourante+"'", null);
    }

    /**
     * Methode qui insert dans la base de donnees les informations concernant l'utilisateur
     * @param db la base de donnees
     * @param prenom le prenom de l'utilisateur
     * @param objectif l'objectif quotidien
     */
    public static void insererProfil(SQLiteDatabase db, String prenom, String objectif){
        ContentValues values = new ContentValues();
        values.put(P_ID, USER_ID);
        values.put(P_PRENOM, prenom);
        values.put(P_OBJECTIF, objectif);

        db.insert(TABLE_PROFILS, null, values);
    }

    public static String getObjectif(SQLiteDatabase db){
        String requete = "SELECT "+P_OBJECTIF+" FROM "+TABLE_PROFILS+";";
        Cursor c = db.rawQuery(requete, null);
        String obj = "";
        if(c != null) {
            c.moveToFirst();
            obj = c.getString(c.getColumnIndex(P_OBJECTIF));
            c.close();
        }
        return obj;
    }

    /**
     * Methode qui insert les plats selectionnes dans la base de donnees
     * @param db la base de donnees
     * @param qte la quantite du produit
     * @param upc le code du produit
     * @param nom le nom du produit
     * @param size le format du produit
     * @param nutriments le arraylist de type nutriment correspondant au produit
     */
    public static void insererListePlats(SQLiteDatabase db, int qte, String upc, String nom,
                                    String size, ArrayList<Nutriment> nutriments){
        Calendar mcurrentDate=Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth=mcurrentDate.get(Calendar.MONTH);
        int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

        String dateCourante=Integer.toString(mYear)+"-"+Integer.toString(mMonth)+"-"+Integer.toString(mDay);

        ContentValues values = new ContentValues();
        values.put(L_USER_ID, USER_ID);
        values.put(L_NOM, nom);
        values.put(L_QUANTITE, qte);
        values.put(L_SIZE, size);
        values.put(L_UPC, upc);
        values.put(L_DATEENTREE, dateCourante);

        String obj = getObjectif(db);
        values.put(L_OBJECTIF, obj);

        //Si la quantite initiale est à 1, on recupere les valeurs initiales des nutriments
        for (Nutriment n : nutriments) {
            double valeur;
            String nouvelleValeur = " ";
            valeur = Double.parseDouble(n.getValue());
            nouvelleValeur = Double.toString(valeur * qte);

            switch (n.getName()) {
                case L_CALORIES:
                    Log.d("DB", "Ajouter " + n.toString());
                    values.put(L_CALORIES, n.getValue() + " " + n.getUOM());
                    break;
                case L_PROTEIN:
                    Log.d("DB", "Ajouter " + n.toString());
                    values.put(L_PROTEIN, n.getValue() + " " + n.getUOM());
                    break;
                case L_SUGARS:
                    Log.d("DB", "Ajouter " + n.toString());
                    values.put(L_SUGARS, n.getValue() + " " + n.getUOM());
                    break;
                case L_TOTALFAT:
                    Log.d("DB", "Ajouter " + n.toString());
                    values.put("`" + L_TOTALFAT + "`", n.getValue() + " " + n.getUOM());
                    break;
            }
        }
        db.insert(TABLE_LISTEPLATS, null, values);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}