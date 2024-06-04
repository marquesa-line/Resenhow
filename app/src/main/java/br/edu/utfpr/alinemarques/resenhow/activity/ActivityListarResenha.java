package br.edu.utfpr.alinemarques.resenhow.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.ActionMode;
import androidx.constraintlayout.widget.ConstraintLayout;

import br.edu.utfpr.alinemarques.resenhow.R;
import br.edu.utfpr.alinemarques.resenhow.adapter.ResenhaAdapter;
import br.edu.utfpr.alinemarques.resenhow.application.ApplicationConfig;
import br.edu.utfpr.alinemarques.resenhow.application.PersistenciaTipo;
import br.edu.utfpr.alinemarques.resenhow.modelo.Resenha;
import br.edu.utfpr.alinemarques.resenhow.persistencia.lite.ResenhasDatabaseLite;
import br.edu.utfpr.alinemarques.resenhow.persistencia.room.ResenhaDatabase;
import br.edu.utfpr.alinemarques.resenhow.utils.UtilsGUI;

public class ActivityListarResenha extends AppCompatActivity {

    private PersistenciaTipo tipoPersistencia;
    private ConstraintLayout layout;
    public static final String ARQUIVO = "br.edu.utfpr.alinemarques.resenhow.sharedpreferences.PREFERENCIAS";
    public static final String ORDENACAO_ASCENDENTE = "ORDENACAO_ASCENDENTE";
    private boolean ordenacaoAscendente = true;
    private static final String COR = "COR";
    private int opcao = Color.TRANSPARENT;
    private ListView listViewResenha; //Lista visual
    private ArrayList<Resenha> resenhaArray; //copia local das resenhas
    private ResenhaAdapter resenhaAdapter; //coverte itens da lista em uma visualizacao especifica pro ListView
    private ActionMode actionMode;
    private View viewSelecionada;
    private int posicaoSelecionada = -1;
    private static final String MODO_NOTURNO = "MODO_NOTURNO";
    private boolean modoNoturno = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == ActivityResenha.EDITAR || requestCode == ActivityResenha.NOVO) && resultCode == RESULT_OK) {
            popularResenha();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_resenha);

        setTitle(R.string.biblioteca_de_resenha);

        ApplicationConfig applicationConfig = (ApplicationConfig) getApplicationContext();
        tipoPersistencia = applicationConfig.getTipoPersistencia(); // LITE ou ROOM

        layout = findViewById(R.id.layoutPrincipal);

        configuralistViewResenha();
        popularResenha();
        lerModoNoturno();
        lerPreferenciaCor();
        lerPreferenciaOrdenacaoAscendente();

        registerForContextMenu(listViewResenha);
    }

    private void popularResenha() {

        if (tipoPersistencia == PersistenciaTipo.LITE) {

            ResenhasDatabaseLite database = ResenhasDatabaseLite.getInstance(this);
            database.resenhasDaoLite.carregarTudo();

            // Atualiza a lista de resenhas
            resenhaArray.clear();
            resenhaArray.addAll(database.resenhasDaoLite.lista);

        } else
            if (tipoPersistencia == PersistenciaTipo.ROOM) {

            // Lógica para ROOM
            ResenhaDatabase database = ResenhaDatabase.getDatabase(this);
            resenhaArray.clear();
            resenhaArray.addAll(database.resenhaDao().queryAll());
        }

        resenhaAdapter.notifyDataSetChanged();
    }

    private void editarResenha(){
        Resenha resenha = resenhaArray.get(posicaoSelecionada);
        ActivityResenha.alterarResenha(this, ActivityResenha.EDITAR, resenha);
    }

    private void excluirResenha(final Resenha resenha){

        String mensagem = getString(R.string.deseja_realmente_apagar) + "\n" + resenha.getTitulo();

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                if (tipoPersistencia == PersistenciaTipo.LITE) {

                                    ResenhasDatabaseLite database = ResenhasDatabaseLite.getInstance(ActivityListarResenha.this);
                                    database.resenhasDaoLite.apagar(resenha);

                                } else
                                if (tipoPersistencia == PersistenciaTipo.ROOM) {
                                    // Lógica para ROOM
                                    ResenhaDatabase database = ResenhaDatabase.getDatabase(ActivityListarResenha.this);
                                    database.resenhaDao().delete(resenha);
                                    resenhaArray.remove(resenha);

                                }
                                popularResenha();
                                resenhaAdapter.notifyDataSetChanged();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

        UtilsGUI.confirmaAcao(this, mensagem, listener);
    }

    private void lerPreferenciaCor() {
        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        //valor default caso seja a primeira vez que está abrindo
        opcao = shared.getInt(COR, opcao);
        mudaBackground();
    }

    private void salvarPreferenciaCor(int novoValor) {
        //salvando config do app - o arquivo só será apagado se o app for desinstalado

        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = shared.edit();
        edit.putInt(COR, novoValor);
        edit.apply();
        opcao = novoValor;
        mudaBackground();
    }

    private void lerModoNoturno() {
        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        //valor default caso seja a primeira vez que está abrindo
        modoNoturno = shared.getBoolean(MODO_NOTURNO, modoNoturno);
        mudarModoNoturno();
    }

    private void salvarModoNoturno(boolean novoValor) {
        //salvando config do app - o arquivo só será apagado se o app for desinstalado
        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = shared.edit();
        edit.putBoolean(MODO_NOTURNO, novoValor);
        edit.apply();
        modoNoturno = novoValor;
        mudarModoNoturno();
    }

    private void ordenarLista() {
        if (ordenacaoAscendente) {
            Collections.sort(resenhaArray, Resenha.ordenacaoCrescente);
        } else {
            Collections.sort(resenhaArray, Resenha.ordenacaoDecrescente);
        }
        resenhaAdapter.notifyDataSetChanged();
    }

    private void atualizarIconeOrdenacao(MenuItem menuItemOrdenacao) {
        if(ordenacaoAscendente) {
            menuItemOrdenacao.setIcon(R.drawable.ic_action_ordenacao_ascendente);
        } else {
            menuItemOrdenacao.setIcon(R.drawable.ic_action_ordenacao_descendente);
        }
    }

    private void lerPreferenciaOrdenacaoAscendente() {
        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        ordenacaoAscendente = shared.getBoolean(ORDENACAO_ASCENDENTE, ordenacaoAscendente);
    }

    private void salvarPreferenciaOrdenacaoAscendente(boolean novaOrdenacao) {
        //salvando config do app - o arquivo só será apagado se o app for desinstalado
        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = shared.edit();
        edit.putBoolean(ORDENACAO_ASCENDENTE, novaOrdenacao);
        edit.apply();
        ordenacaoAscendente = novaOrdenacao;
    }

    private void mudaBackground() {
        layout.setBackgroundColor(opcao);
    }

    private void mudarModoNoturno() {
        AppCompatDelegate.setDefaultNightMode(modoNoturno ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal_opcoes, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        //true ou false se o menu será exibido

        MenuItem item;
        MenuItem menuItemOrdenacao = menu.findItem(R.id.menuItemOrdenacao);
        atualizarIconeOrdenacao(menuItemOrdenacao);

        if (opcao == Color.BLUE){
            item = menu.findItem(R.id.menuItemAzul);
        }else
        if (opcao == Color.RED){
            item = menu.findItem(R.id.menuItemVermelho);
        }else
        if (opcao == Color.TRANSPARENT){
            item = menu.findItem(R.id.menuItemTransparente);
        }else {
            return false;
        }

        item.setChecked(true);

        // Atualiza o estado do item do menu de modo noturno
        MenuItem menuItemModoNoturno = menu.findItem(R.id.menuItemModoNoturno);
        menuItemModoNoturno.setChecked(modoNoturno);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int botao = item.getItemId();

        if (botao == R.id.menuItemAdicionar){
            ActivityResenha.novaResenha(this, ActivityResenha.NOVO);
            return true;
        }else
        if (botao == R.id.menuItemOrdenacao) {
            salvarPreferenciaOrdenacaoAscendente(!ordenacaoAscendente);
            atualizarIconeOrdenacao(item);
            ordenarLista();
            return true;
        }else
        if (botao == R.id.menuItemModoNoturno) {
            boolean modoNoturnoAtivo = !item.isChecked();
            item.setChecked(modoNoturnoAtivo);
            salvarModoNoturno(modoNoturnoAtivo);
            return true;
        }else
        if (botao == R.id.menuItemSobre){
            ActivitySobre.nova(this);
            return true;
        }else
        if (botao == R.id.menuItemAzul){
            item.setChecked(true);
            salvarPreferenciaCor(Color.BLUE);
            return true;
        }else
        if (botao == R.id.menuItemVermelho){
            item.setChecked(true);
            salvarPreferenciaCor(Color.RED);
            return true;
        }else
        if (botao == R.id.menuItemTransparente){
            item.setChecked(true);
            salvarPreferenciaCor(Color.TRANSPARENT);
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void configuralistViewResenha() {

        listViewResenha = findViewById(R.id.listViewBiblioteca);
        resenhaArray = new ArrayList<>();
        resenhaAdapter = new ResenhaAdapter(this, resenhaArray);
        listViewResenha.setAdapter(resenhaAdapter);

        listViewResenha.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listViewResenha.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                posicaoSelecionada = position;
                editarResenha();
            }
        });

        listViewResenha.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (actionMode != null) {
                    return false;
                }

                posicaoSelecionada = position;
                view.setBackgroundColor(Color.LTGRAY);
                viewSelecionada = view;
                listViewResenha.setEnabled(false);
                actionMode = startSupportActionMode(mActionModeCallback);
                return false;
            }
        });
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            //abre o menu
            MenuInflater inflate = mode.getMenuInflater();
            inflate.inflate(R.menu.principal_item_selecionado, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            //toda vez que o menu é reexibido
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            //toda vez que eu clico

            Resenha resenha = resenhaArray.get(posicaoSelecionada);

            int idMenuItem = item.getItemId();

            if (idMenuItem == R.id.menuItemAlterar) {
                editarResenha();
                mode.finish();
                return true;
            } else
            if (idMenuItem == R.id.menuItemExcluir) {
                excluirResenha(resenha);
                mode.finish();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            if (viewSelecionada != null) {
                viewSelecionada.setBackgroundColor(Color.TRANSPARENT);
            }

            actionMode = null;
            viewSelecionada = null;

            listViewResenha.setEnabled(true);
        }
    };
}