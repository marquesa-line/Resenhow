package br.edu.utfpr.alinemarques.resenhow.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.edu.utfpr.alinemarques.resenhow.R;
import br.edu.utfpr.alinemarques.resenhow.modelo.Resenha;
import br.edu.utfpr.alinemarques.resenhow.modelo.Tipo;

public class ResenhaAdapter extends BaseAdapter {

    private Context context;
    private List<Resenha> resenha;

    private static class BibliotecaHolder {
        public TextView titleValueTextView;
        public TextView typeValueTextView;
        public TextView genreValueTextView;
        public TextView directorValueTextView;
        public TextView assistidoLidoValueTextView;
        public TextView ratingValueTextView;
        public TextView resumoValueTextView;
    }

    public ResenhaAdapter(Context context, List<Resenha> resenha) {
        this.context = context;
        this.resenha = resenha;
    }

    @Override
    public int getCount() {
        return resenha.size();
    }

    @Override
    public Object getItem(int position) {
        return resenha.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        BibliotecaHolder holder;

        if (view == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.linha_lista_resenha, viewGroup, false);

            holder = new BibliotecaHolder();
            holder.titleValueTextView = view.findViewById(R.id.titleValueTextView);
            holder.typeValueTextView = view.findViewById(R.id.typeValueTextView);
            holder.genreValueTextView = view.findViewById(R.id.genreValueTextView);
            holder.directorValueTextView = view.findViewById(R.id.directorValueTextView);
            holder.assistidoLidoValueTextView = view.findViewById(R.id.assistidoLidoValueTextView);
            holder.ratingValueTextView = view.findViewById(R.id.ratingValueTextView);
            holder.resumoValueTextView = view.findViewById(R.id.resumoValueTextView);

            view.setTag(holder);

        } else {
            holder = (BibliotecaHolder) view.getTag();
        }

        Resenha currentResenha = resenha.get(i);

        holder.titleValueTextView.setText(currentResenha.getTitulo());
        holder.directorValueTextView.setText(currentResenha.getDiretorAutor());

        List<Tipo> currentTipos = currentResenha.getTipos();
        StringBuilder tiposBuilder = new StringBuilder();
        for (Tipo tipo : currentTipos) {
            String tipoString = context.getResources().getString(tipo.getStringResId());
            tiposBuilder.append(tipoString).append(" ");
        }
        holder.typeValueTextView.setText(tiposBuilder.toString().trim());

        holder.assistidoLidoValueTextView.setText(currentResenha.isAssistidoLido() ?
                context.getResources().getString(R.string.sim) : context.getResources().getString(R.string.nao));

        String[] ratingArray = context.getResources().getStringArray(R.array.resenha_rating_array);
        holder.ratingValueTextView.setText(ratingArray[currentResenha.getResenhaRating()]);

        String[] generoArray = context.getResources().getStringArray(R.array.generos_base);
        holder.genreValueTextView.setText(generoArray[currentResenha.getGenero()]);

        holder.resumoValueTextView.setText(currentResenha.getResenhaResumo());

        return view;
    }
}