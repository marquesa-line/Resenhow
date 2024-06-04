package br.edu.utfpr.alinemarques.resenhow.modelo;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import android.content.Context;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
public class Resenha implements Comparable<Resenha> {

    @PrimaryKey(autoGenerate = true)
    private long id;
    @NonNull
    private String titulo;
    private List<Tipo> tipos;
    private int genero;
    private String diretorAutor;
    private boolean assistidoLido;
    private int resenhaRating;
    private String resenhaResumo;
    public Resenha() {}

    @Ignore
    public Resenha(String titulo) {
        setTitulo(titulo);
    }

    public int getResenhaRating() {
        return resenhaRating;
    }

    public void setResenhaRating(int resenhaRating) {
        this.resenhaRating = resenhaRating;
    }

    public String getResenhaResumo() {
        return resenhaResumo;
    }

    public void setResenhaResumo(String resenhaResumo) {
        this.resenhaResumo = resenhaResumo;
    }

    @NonNull
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(@NonNull String titulo) {
        this.titulo = titulo;
    }

    public List<Tipo> getTipos() {
        return tipos;
    }

    public void setTipos(List<Tipo> tipos) {
        this.tipos = tipos;
    }

    public int getGenero() {
        return genero;
    }

    public void setGenero(int genero) {
        this.genero = genero;
    }


    public String getDiretorAutor() {
        return diretorAutor;
    }

    public void setDiretorAutor(String diretorAutor) {
        this.diretorAutor = diretorAutor;
    }

    public boolean isAssistidoLido() {
        return assistidoLido;
    }

    public void setAssistidoLido(boolean assistidoLido) {
        this.assistidoLido = assistidoLido;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTiposString() {
        if (tipos == null || tipos.isEmpty()) {
            return "";
        }
        StringBuilder tiposBuilder = new StringBuilder();
        for (Tipo tipo : tipos) {
            if (tiposBuilder.length() > 0) {
                tiposBuilder.append(",");
            }
            tiposBuilder.append(tipo.name());
        }
        return tiposBuilder.toString();
    }

    public void setTiposFromString(String tiposString) {
        if (tiposString == null || tiposString.isEmpty()) {
            this.tipos = new ArrayList<>();
        } else {
            String[] tiposArray = tiposString.split(",");
            List<Tipo> tiposList = new ArrayList<>();
            for (String tipoString : tiposArray) {
                tiposList.add(Tipo.valueOf(tipoString.trim()));
            }
            this.tipos = tiposList;
        }
    }

    @Override
    public int compareTo(Resenha outraResenha) {
        return this.titulo.compareTo(outraResenha.getTitulo());
    }

    public static Comparator<Resenha> ordenacaoCrescente = new Comparator<Resenha>() {
        @Override
        public int compare(Resenha resenha1, Resenha resenha2) {
            return resenha1.getTitulo().compareToIgnoreCase(resenha2.getTitulo());
        }
    };

    public static Comparator<Resenha> ordenacaoDecrescente = new Comparator<Resenha>() {
        @Override
        public int compare(Resenha resenha1, Resenha resenha2) {
            return -1 * resenha1.getTitulo().compareToIgnoreCase(resenha2.getTitulo());
        }
    };
}