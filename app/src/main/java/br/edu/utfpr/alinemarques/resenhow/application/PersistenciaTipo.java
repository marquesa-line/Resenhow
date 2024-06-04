package br.edu.utfpr.alinemarques.resenhow.application;

public enum PersistenciaTipo {
    LITE(1),
    ROOM(2);

    private final int valor;

    PersistenciaTipo(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public static PersistenciaTipo fromValor(int valor) {
        for (PersistenciaTipo tipo : values()) {
            if (tipo.getValor() == valor) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de persistência inválida: " + valor);
    }
}