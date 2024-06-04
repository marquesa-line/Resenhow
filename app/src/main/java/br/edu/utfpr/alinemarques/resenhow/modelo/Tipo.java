package br.edu.utfpr.alinemarques.resenhow.modelo;

import android.os.Parcel;
import android.os.Parcelable;

import br.edu.utfpr.alinemarques.resenhow.R;

import android.os.Parcel;
import android.os.Parcelable;

public enum Tipo implements Parcelable {
    FILME(R.string.filme),
    SERIE(R.string.serie),
    LIVRO(R.string.livro);

    private final int stringResId;

    Tipo(int stringResId) {
        this.stringResId = stringResId;
    }

    public int getStringResId() {
        return stringResId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.stringResId);
        dest.writeString(this.name());
    }

    public static final Parcelable.Creator<Tipo> CREATOR = new Parcelable.Creator<Tipo>() {
        @Override
        public Tipo createFromParcel(Parcel source) {
            int resId = source.readInt();
            String name = source.readString();
            return Tipo.valueOf(name);
        }

        @Override
        public Tipo[] newArray(int size) {
            return new Tipo[size];
        }
    };
}