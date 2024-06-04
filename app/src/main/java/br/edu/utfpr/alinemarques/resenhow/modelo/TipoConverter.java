package br.edu.utfpr.alinemarques.resenhow.modelo;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;
public class TipoConverter {

    @TypeConverter
    public static List<Tipo> toTipoList(String data) {
        List<Tipo> tipos = new ArrayList<>();
        if (data != null && !data.isEmpty()) {
            String[] tokens = data.split(",");
            for (String token : tokens) {
                tipos.add(Tipo.valueOf(token));
            }
        }
        return tipos;
    }

    @TypeConverter
    public static String fromTipoList(List<Tipo> tipos) {
        List<String> tokens = new ArrayList<>();
        for (Tipo tipo : tipos) {
            tokens.add(tipo.name());
        }
        return String.join(",", tokens);
    }
}