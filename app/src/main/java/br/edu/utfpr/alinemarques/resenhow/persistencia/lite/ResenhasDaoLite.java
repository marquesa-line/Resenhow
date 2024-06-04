package br.edu.utfpr.alinemarques.resenhow.persistencia.lite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.edu.utfpr.alinemarques.resenhow.modelo.Resenha;

public class ResenhasDaoLite {

    private ResenhasDatabaseLite dbHelper;
    public static final String TABELA = "RESENHA";
    public static final String ID = "ID";
    public static final String TITULO = "TITULO";
    public static final String TIPOS = "TIPOS";
    public static final String GENERO = "GENERO";
    public static final String DIRETOR_AUTOR = "DIRETOR_AUTOR";
    public static final String ASSISTIDO_LIDO = "ASSISTIDO_LIDO";
    public static final String RATING = "RATING";
    public static final String RESUMO = "RESUMO";
    private ResenhasDatabaseLite conexao;

    public List<Resenha> lista = new ArrayList<>();

    public ResenhasDaoLite(ResenhasDatabaseLite dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void criarTabela(SQLiteDatabase database){

        String sql = "CREATE TABLE " + TABELA + " (" +
                ResenhasDaoLite.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ResenhasDaoLite.TITULO + " TEXT NOT NULL, " +
                ResenhasDaoLite.TIPOS + " TEXT, " +
                ResenhasDaoLite.GENERO + " INTEGER, " +
                ResenhasDaoLite.DIRETOR_AUTOR + " TEXT, " +
                ResenhasDaoLite.ASSISTIDO_LIDO + " BOOLEAN NOT NULL CHECK (" + ResenhasDaoLite.ASSISTIDO_LIDO + " IN (0, 1)), " +
                ResenhasDaoLite.RATING + " INTEGER, " +
                ResenhasDaoLite.RESUMO + " TEXT)";
        database.execSQL(sql);
    }

    public void apagarTabela(SQLiteDatabase database){

        String sql = "DROP TABLE IF EXISTS " + TABELA;
        database.execSQL(sql);
    }

    public boolean inserir(Resenha resenha) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITULO, resenha.getTitulo());
        values.put(GENERO, resenha.getGenero());
        values.put(DIRETOR_AUTOR, resenha.getDiretorAutor());
        values.put(ASSISTIDO_LIDO, resenha.isAssistidoLido() ? 1 : 0);
        values.put(RATING, resenha.getResenhaRating());
        values.put(RESUMO, resenha.getResenhaResumo());
        values.put(TIPOS, resenha.getTiposString());

        long id = db.insert(TABELA, null, values);
        if (id != -1) {
            resenha.setId(id);
            lista.add(resenha);
            ordenarLista();
            Log.d("ResenhaDAO", "Resenha inserida com sucesso: " + resenha.getTitulo());
            return true;
        } else {
            Log.e("ResenhaDAO", "Erro ao inserir resenha");
            return false;
        }
    }

    public boolean alterar(Resenha resenha) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITULO, resenha.getTitulo());
        values.put(GENERO, resenha.getGenero());
        values.put(DIRETOR_AUTOR, resenha.getDiretorAutor());
        values.put(ASSISTIDO_LIDO, resenha.isAssistidoLido() ? 1 : 0);
        values.put(RATING, resenha.getResenhaRating());
        values.put(RESUMO, resenha.getResenhaResumo());
        values.put(TIPOS, resenha.getTiposString());

        String[] args = {String.valueOf(resenha.getId())};
        int rowsUpdated = db.update(TABELA, values, ID + " = ?", args);

        if (rowsUpdated > 0) {
            Log.d("ResenhaDAO", "Resenha atualizada com sucesso: " + resenha.getTitulo());
            return true;
        } else {
            Log.e("ResenhaDAO", "Erro ao atualizar resenha");
            return false;
        }
    }

    public boolean apagar(Resenha resenha) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = {String.valueOf(resenha.getId())};
        int deleted = db.delete(TABELA, ID + " = ?", args);
        if (deleted > 0) {
            lista.remove(resenha);
            Log.d("ResenhaDAO", "Resenha apagada com sucesso: " + resenha.getTitulo());
            return true;
        } else {
            Log.e("ResenhaDAO", "Erro ao apagar resenha");
            return false;
        }
    }
    public void carregarTudo() {

        lista.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase(); // Obtendo a conexão de leitura

        String sql = "SELECT * FROM " + TABELA + " ORDER BY " + TITULO;

        Cursor cursor = null;

        try {
            cursor = db.rawQuery(sql, null);

            if (cursor.getCount() == 0) {
                Log.d("TABELA-VAZIA", "Nenhuma resenha encontrada.");
                return;
            }

            int colunaId = cursor.getColumnIndex(ID);
            int colunaTitulo = cursor.getColumnIndex(TITULO);
            int colunaTipos = cursor.getColumnIndex(TIPOS);
            int colunaGenero = cursor.getColumnIndex(GENERO);
            int colunaDiretorAutor = cursor.getColumnIndex(DIRETOR_AUTOR);
            int colunaAssistidoLido = cursor.getColumnIndex(ASSISTIDO_LIDO);
            int colunaRating = cursor.getColumnIndex(RATING);
            int colunaResumo = cursor.getColumnIndex(RESUMO);

            while (cursor.moveToNext()) {
                Resenha resenha = new Resenha(cursor.getString(colunaTitulo));
                resenha.setId(cursor.getLong(colunaId));
                resenha.setTiposFromString(cursor.getString(colunaTipos));
                resenha.setGenero(cursor.getInt(colunaGenero));
                resenha.setDiretorAutor(cursor.getString(colunaDiretorAutor));
                resenha.setAssistidoLido(cursor.getInt(colunaAssistidoLido) == 1);
                resenha.setResenhaRating(cursor.getInt(colunaRating));
                resenha.setResenhaResumo(cursor.getString(colunaResumo));

                lista.add(resenha);
            }

            Log.d("RESENHA-QTDE-REG", "Número total de resenhas carregadas: " + lista.size());

        } catch (Exception e) {
            Log.e("CARREGAR-TUDO-ERRO", "Erro ao carregar resenhas", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        ordenarLista();
    }

    public Resenha resenhaPorId(long id) {
        for (Resenha r : lista) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }

    public void ordenarLista() {
        Collections.sort(lista, Resenha.ordenacaoCrescente);
    }
}
