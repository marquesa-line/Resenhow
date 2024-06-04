package br.edu.utfpr.alinemarques.resenhow.persistencia.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.edu.utfpr.alinemarques.resenhow.modelo.Resenha;

@Dao
public interface ResenhaDao {
    @Insert
    long insert(Resenha resenha);

    @Delete
    void delete(Resenha resenha);

    @Update
    void update(Resenha resenha);

    @Query("SELECT * FROM resenha WHERE id = :id")
    Resenha queryForId(long id);

    @Query("SELECT * FROM resenha ORDER BY titulo ASC")
    List<Resenha> queryAll();
}
