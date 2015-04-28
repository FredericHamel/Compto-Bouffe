package com.github.compto_bouffe;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by adkuser on 4/22/15.
 * Un classe pour gere les connection avec la db.
 */
public class DatabaseManager {
    // Unique instance de Database Manager.
    private static DatabaseManager instance;

    private int nbConnection;
    private static DBHelper dbH;
    private SQLiteDatabase db;

    /**
     * Initialise l'instance et le DBHelper.
     */
    public static void init(Context context)
    {
        Log.d(DatabaseManager.class.getSimpleName(), "Initialise");
        if(instance == null) {
            instance = new DatabaseManager();
            dbH = new DBHelper(context);
        }
    }

    /**
     * Retourne l'unique instance, doit etre appeler apres init.
     * @return l'instance de la classe.
     */
    public static DatabaseManager getInstance()
    {
        if (instance == null)
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                " is not initialized, call init(..) method first.");
        return instance;
    }

    /**
     * Constructeur du Singleton.
     */
    private DatabaseManager() {
        nbConnection = 0;
    }

    /**
     * Retourne le nombre de connection a la DB.
     * @return nbConnection
     */
    public synchronized int getNbConnection()
    {
        return nbConnection;
    }

    /**
     * Retourne la connection a la DB, doit etre suivis de close.
     * @return un objet SQLiteDatabase.
     */
    public synchronized SQLiteDatabase openConnection()
    {
        ++nbConnection;
        if(nbConnection == 1) {
            // Open Database connection
            Log.d(DatabaseManager.class.getSimpleName(), "Open connection to DB.");
            String pragma = "PRAGMA foreign_keys=1;";
            db = dbH.getWritableDatabase();
            // Enable foreign keys.
            db.execSQL(pragma);
        }
        return db;
    }

    /**
     * Ferme la connection a la database.
     * Doit etre appele autant de fois que openConnection()
     */
    public synchronized void close()
    {
        --nbConnection;
        if(nbConnection == 0)
        {
            // Close database connection
            Log.d(DatabaseManager.class.getSimpleName(), "Close connection to DB.");
            db.close();
            db = null;
        }
    }
}
