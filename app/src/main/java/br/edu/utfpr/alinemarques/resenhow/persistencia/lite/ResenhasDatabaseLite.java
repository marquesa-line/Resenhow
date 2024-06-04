package br.edu.utfpr.alinemarques.resenhow.persistencia.lite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ResenhasDatabaseLite extends SQLiteOpenHelper {

    private static final String DB_NAME    = "resenhas.db";
    private static final int    DB_VERSION = 3;

    private static ResenhasDatabaseLite instance;

    private Context context;

    public ResenhasDaoLite resenhasDaoLite;

    public static ResenhasDatabaseLite getInstance(Context contexto){

        if (instance == null){
            instance = new ResenhasDatabaseLite(contexto);
        }
        return instance;
    }

    private ResenhasDatabaseLite(Context contexto){
        super(contexto, DB_NAME, null, DB_VERSION);

        context = contexto;

        resenhasDaoLite = new ResenhasDaoLite(this);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        resenhasDaoLite.criarTabela(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        resenhasDaoLite.apagarTabela(db);

        onCreate(db);
    }
}