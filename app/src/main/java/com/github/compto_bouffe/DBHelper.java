package com.github.compto_bouffe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by sabrinaouaret on 16/04/15.
 */
public class DBHelper extends SQLiteOpenHelper {

    static final String DB_NAME = "comptoBouffe.db";
    static final int DB_VERSION = 1;

    // Table des emissions
    static final String TABLE_PROFILS = "Profils";
    static final String TABLE_LISTEPLATS = "ListePlats";
    static final String TABLE_RESULTATS = "Resultats";
    static final String P_ID = "ID";
    static final String P_PRENOM ="Prenom";
    static final String P_OBJECTIF ="Objectif";
    static final String L_ID ="ID";
    static final String L_OBJECTIF ="Objectif";
    static final String L_QUANTITE ="Quantite";
    static final String L_UPC ="UPC";
    static final String L_NOM ="Nom";
    static final String L_DESC ="Desc";
    static final String L_DATEENTREE ="DateEntree";
    static final String L_CALORIES ="Calories";
    static final String L_TOTALFAT ="TotalFat";
    static final String L_SUGARS ="Sugars";
    static final String L_PROTEIN ="Protein";
    static final String L_SODIUM ="Sodium";
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
                +P_OBJECTIF+" INT NOT NULL,"
                +"PRIMARY KEY("+P_ID+"));";

        String creerTableListe = "CREATE TABLE IF NOT EXISTS "+TABLE_LISTEPLATS+" ("
                +L_ID+" INT, "
                +L_OBJECTIF+"INT NOT NULL,"
                +L_QUANTITE+" INT, "
                +L_UPC+" TEXT NOT NULL, "
                +L_NOM+" TEXT NOT NULL, "
                +L_DESC+" TEXT, "
                +L_DATEENTREE+" TEXT, "
                +L_CALORIES+" TEXT NOT NULL, "
                +L_TOTALFAT+" TEXT NOT NULL, "
                +L_SUGARS+" TEXT NOT NULL, "
                +L_PROTEIN+" TEXT NOT NULL, "
                +L_SODIUM+" TEXT NOT NULL, "
                +"FOREIGN KEY("+L_ID+") REFERENCES "+TABLE_PROFILS+"("+P_ID+"),"
                +"PRIMARY KEY("+L_ID+","+L_DATEENTREE+"));";

        String creerTableResultats = "CREATE TABLE IF NOT EXISTS "+TABLE_RESULTATS+" ("
                +R_ID+" INT, "
                +R_OBJECTIF_INIT+" TEXT NOT NULL,"
                +R_OBJECTIF_RES+" INT,"
                +R_DATE+"TEXT NOT NULL,"
                +"FOREIGN KEY("+R_ID+") REFERENCES "+TABLE_PROFILS+"("+P_ID+"),"
                +"FOREIGN KEY("+R_OBJECTIF_INIT+") REFERENCES "+TABLE_LISTEPLATS+"("+L_OBJECTIF+"),"
                +"PRIMARY KEY("+R_ID+", "+R_DATE+", "+R_OBJECTIF_INIT+"));";

        db.execSQL(creerTableProfils);
        db.execSQL(creerTableListe);
        db.execSQL(creerTableResultats);
        Log.d("DB", "DB created");
    }


    /**
     * Methode qui renvoie la liste de plats d'un utilisateur a la date courante
     * @param db la base de donnees
     * @param id l'id unique de l'utilisateur
     * @return c le curseur;
     */
    public static Cursor listePlatsDateCourante (SQLiteDatabase db, int id){
        Calendar mcurrentDate=Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth=mcurrentDate.get(Calendar.MONTH);
        int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

        String dateCourante=Integer.toString(mYear)+"-"+Integer.toString(mMonth)+"-"+Integer.toString(mDay);

        String requete = "SELECT "+L_NOM+", "+L_CALORIES+", "+L_PROTEIN+", "+L_SODIUM+", "
                +L_SUGARS+", "+L_TOTALFAT+" FROM "+TABLE_LISTEPLATS
                +" WHERE "+L_ID+"="+id
                +" AND "+L_DATEENTREE+"="+dateCourante+";";
        Cursor c = db.rawQuery(requete, null);
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

        String dateCourante=Integer.toString(mYear)+"-"+Integer.toString(mMonth)+"-"+Integer.toString(mDay);

        String requete = "SELECT "+L_QUANTITE+" FROM "+TABLE_LISTEPLATS
                +" WHERE "+L_UPC+"="+upc
                +" AND "+L_DATEENTREE+"="+dateCourante+";";
        Cursor c= db.rawQuery(requete, null);
        return c;
    }


    /**
     * Methode qui fait la mise a jour des objectifs quotidien d'un utilisateur
     * @param db la base de donnees
     * @param id l'id unique de l'utilisateur
     * @param obj le nouvel objectif de l'utilisateur
     */
    public static void changerObjectif(SQLiteDatabase db, int id, int obj){
        ContentValues values = new ContentValues();
        values.put(P_OBJECTIF, obj);
        db.update(TABLE_PROFILS, values,P_ID+" = "+id,null);
    }


    /**
     * Methode qui fait la mise a jour du prenom de l'utilisateur
     * @param db
     * @param id
     * @param prenom
     */
    public static void changerPrenom(SQLiteDatabase db, int id, int prenom){
        ContentValues values = new ContentValues();
        values.put(P_PRENOM, prenom);
        db.update(TABLE_PROFILS, values, P_ID+" = "+id, null);
    }

    /*public static void changerQuantite(SQLiteDatabase db, int id, String nom, int qte){
        ContentValues values = new ContentValues();
        values.put(L_QUANTITE, qte);
        db.update(TABLE_PROFILS, values, P_ID+" = "+id, null);
    }*/

    /**
     * Methode qui renvoie la liste des plats d'une journee donnee d'un utilisateur
     * @param db la base de donnees
     * @param date la date de type YYYY-MM-DD dont on veut connaitre la liste des plats
     * @return c le curseur
     */
    public static Cursor listePeriode(SQLiteDatabase db, int id, String date){
        String requete = "SELECT "+L_NOM+", "+L_CALORIES+", "+L_PROTEIN+", "+L_SODIUM+", "
                +L_SUGARS+", "+L_TOTALFAT+" FROM "+TABLE_LISTEPLATS
                +" WHERE "+L_ID+"="+id
                +" AND "+L_DATEENTREE+"="+date+";";
        Cursor c = db.rawQuery(requete, null);
        return c;
    }

    /**
     * Methode qui renvoit une liste  de date et l'objectif du jour et l'objectif resultat associes
     * pour une periode de temps donnee
     * @param db la base de donnees
     * @param id l'id unique de l'utilisateur
     * @param dateDebut la date au format YYYY-MM-DD du debut de periode
     * @param dateFin la date au format YYYY-MM-DD de fin de periode
     * @return c le curseur
     */
    public static Cursor listeObjectifs(SQLiteDatabase db, int id, String dateDebut, String dateFin){
        String requete = "SELECT "+R_DATE+", "+R_OBJECTIF_INIT+", "+R_OBJECTIF_RES
                +" FROM "+TABLE_RESULTATS
                +" WHERE "+R_ID+"="+id
                +" AND "+R_DATE+">="+dateDebut
                +" AND "+R_DATE+"<="+dateFin+";";

        Cursor c = db.rawQuery(requete, null);
        return c;
    }

    /**
     * Methode qui permet de supprimer un plat de la liste de plat de la journee courante
     * @param db la base de donnees
     * @param upc le code du plat
     */
    public static void supprimerPlatListe(SQLiteDatabase db, int id, String upc){
        Calendar mcurrentDate=Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth=mcurrentDate.get(Calendar.MONTH);
        int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

        String dateCourante=Integer.toString(mYear)+"-"+Integer.toString(mMonth)+"-"+Integer.toString(mDay);

        db.delete(TABLE_LISTEPLATS, L_UPC+"="+upc+" AND "+L_DATEENTREE+"="+dateCourante
                +" AND "+L_ID+"="+id, null);
    }

    /**
     *Insertions
     */


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}