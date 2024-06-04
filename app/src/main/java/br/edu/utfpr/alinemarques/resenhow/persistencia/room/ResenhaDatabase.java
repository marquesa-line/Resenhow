package br.edu.utfpr.alinemarques.resenhow.persistencia.room;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import br.edu.utfpr.alinemarques.resenhow.modelo.Resenha;
import br.edu.utfpr.alinemarques.resenhow.modelo.TipoConverter;

@Database(entities = {Resenha.class}, version = 1, exportSchema = false)
@TypeConverters({TipoConverter.class})
public abstract class ResenhaDatabase extends RoomDatabase {

    public abstract ResenhaDao resenhaDao();

    private static ResenhaDatabase instance;

    //evita que tenha dois objetos com a mesma database em memória
    public static ResenhaDatabase getDatabase(final Context context) {

        if (instance == null) {

            synchronized (ResenhaDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context,
                            ResenhaDatabase.class,
                            "resenha.room.db").allowMainThreadQueries().build(); //o build abre ou cria o database
                }
            }
        }
        return instance;
    }
}

