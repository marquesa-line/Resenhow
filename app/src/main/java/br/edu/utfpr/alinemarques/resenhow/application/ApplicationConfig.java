package br.edu.utfpr.alinemarques.resenhow.application;

import android.app.Application;

public class ApplicationConfig extends Application {
    private PersistenciaTipo tipoPersistencia;

    public PersistenciaTipo getTipoPersistencia() {
        return tipoPersistencia;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // INICIALIZANDO PERSISTENCIA GLOBAL
        tipoPersistencia = PersistenciaTipo.ROOM; // ou PersistenciaTipo.LITE
    }
}