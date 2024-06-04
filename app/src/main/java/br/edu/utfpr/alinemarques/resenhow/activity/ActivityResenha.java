package br.edu.utfpr.alinemarques.resenhow.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import br.edu.utfpr.alinemarques.resenhow.R;
import br.edu.utfpr.alinemarques.resenhow.application.ApplicationConfig;
import br.edu.utfpr.alinemarques.resenhow.application.PersistenciaTipo;
import br.edu.utfpr.alinemarques.resenhow.modelo.Resenha;
import br.edu.utfpr.alinemarques.resenhow.modelo.Tipo;
import br.edu.utfpr.alinemarques.resenhow.persistencia.lite.ResenhasDatabaseLite;
import br.edu.utfpr.alinemarques.resenhow.persistencia.room.ResenhaDatabase;
import br.edu.utfpr.alinemarques.resenhow.utils.UtilsGUI;

public class ActivityResenha extends AppCompatActivity {

    public static final String MODO = "MODO";
    public static final int NOVO = 1;
    public static final int EDITAR = 2;
    public static final String SUGERIR_GENERO = "SUGERIR_GENERO";
    public static final String ULTIMO_GENERO = "ULTIMO_GENERO";
    private static final String ID = "ID";
    private PersistenciaTipo tipoPersistencia;
    private ConstraintLayout layout;
    private EditText editTexTitulo, editTextDiretorAutor, editTextResumo;
    private CheckBox checkBoxFilme, checkBoxSerie, checkBoxLivro;
    private Spinner spinnerGenero, spinnerRating;
    private RadioGroup radioGroupConsumido;
    private int modo;
    private boolean sugerirGenero = false;
    private int ultimoGeneroSalvo = 0;
    private Resenha resenha;

    public static void novaResenha(Activity activity, int requestCode) {

        Intent intent = new Intent(activity, ActivityResenha.class);
        intent.putExtra(MODO, NOVO);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void alterarResenha(Activity activity, int requestCode, Resenha resenha) {

        Intent intent = new Intent(activity, ActivityResenha.class);
        intent.putExtra(MODO, EDITAR);
        intent.putExtra(ID, resenha.getId());
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resenha);
        layout = findViewById(R.id.layoutPrincipal);
        ApplicationConfig applicationConfig = (ApplicationConfig) getApplicationContext();
        tipoPersistencia = applicationConfig.getTipoPersistencia(); // LITE ou ROOM

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        carregarFindViewById();
        popularSpinners();
        lerGeneroConfig();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {

            modo = bundle.getInt(MODO, NOVO);

            if (modo == NOVO) {

                novoRegistro();

            } else if (modo == EDITAR) {

                long id = bundle.getLong(ID);

                atualizarRegistro(id);
            }
        }
    }

    private void salvarResenha() {

        //VALIDA TITULO QUE É OBRIGATÓRIO
        String tituloResenha = UtilsGUI.validaCampoTexto(this, editTexTitulo, R.string.titulo_vazio);

        if (tituloResenha == null) {
            return;
        }

        //VALIDA DIRETOR/AUTOR QUE É OBRIGATÓRIO
        String diretorAutorConteudo = UtilsGUI.validaCampoTexto(this, editTextDiretorAutor, R.string.diretor_autor_vazio);
        if (diretorAutorConteudo == null) {
            return;
        }

        //CHECK-BOX TIPO
        List<Tipo> tiposConteudo = new ArrayList<>();
        if (checkBoxFilme.isChecked()) {
            tiposConteudo.add(Tipo.FILME);
        }
        if (checkBoxSerie.isChecked()) {
            tiposConteudo.add(Tipo.SERIE);
        }
        if (checkBoxLivro.isChecked()) {
            tiposConteudo.add(Tipo.LIVRO);
        }

        //VALIDA TIPO DE CONTEÚDO QUE É OBRIGATÓRIO
        if (tiposConteudo.isEmpty()) {
            UtilsGUI.avisoErro(this, R.string.nenhum_tipo_selecionado);
            return;
        }

        // VALIDAÇÃO DO SPINNER GENERO QUE É OBRIGATÓRIO
        int generoResenha = spinnerGenero.getSelectedItemPosition();
        if (generoResenha == 0) {
            UtilsGUI.avisoErro(this, R.string.nenhum_genero_selecionado);
            return;
        }

        // VALIDAÇÃO DO SPINNER RATING QUE É OBRIGATÓRIO
        int ratingResenha = spinnerRating.getSelectedItemPosition();
        if (ratingResenha == 0) {
            UtilsGUI.avisoErro(this, R.string.nenhum_rating_selecionado);
            return;
        }

        // VALIDAÇÃO DO RESUMO QUE É OBRIGATÓRIO
        String resumoResenha = UtilsGUI.validaCampoTexto(this,
                editTextResumo,
                R.string.resumo_vazio);

        if (resumoResenha == null) {
            return;
        }

        // VALIDAÇÃO DO RADIO ASSISTIDO/LIDO QUE É OBRIGATÓRIO
        int consumidoConteudo = radioGroupConsumido.getCheckedRadioButtonId();
        boolean assistidoLidoConteudo;
        if (consumidoConteudo == R.id.radioButtonSim) {
            assistidoLidoConteudo = true;
        } else if (consumidoConteudo == R.id.radioButtonNao) {
            assistidoLidoConteudo = false;
        } else {
            UtilsGUI.avisoErro(this, R.string.assistido_nao_selecionado);
            return;
        }

        //SALVA O ÚLTIMO GENERO UTILIZADO
        salvarUltimoGenero(generoResenha);

        resenha.setTitulo(tituloResenha);
        resenha.setDiretorAutor(diretorAutorConteudo);
        resenha.setGenero(generoResenha);
        resenha.setAssistidoLido(assistidoLidoConteudo);
        resenha.setResenhaRating(ratingResenha);
        resenha.setResenhaResumo(resumoResenha);
        resenha.setTipos(tiposConteudo);

        if (tipoPersistencia == PersistenciaTipo.LITE) {

            ResenhasDatabaseLite database = ResenhasDatabaseLite.getInstance(this);

            if (modo == NOVO) {
                database.resenhasDaoLite.inserir(resenha);
            } else {
                database.resenhasDaoLite.alterar(resenha);
            }

        } else if (tipoPersistencia == PersistenciaTipo.ROOM) {

            ResenhaDatabase database = ResenhaDatabase.getDatabase(this);

            if (modo == NOVO) {
                database.resenhaDao().insert(resenha);
            } else {
                database.resenhaDao().update(resenha);
            }
        }

        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void limparCampos() {
        editTexTitulo.setText(null);
        editTextDiretorAutor.setText(null);
        editTextResumo.setText(null);

        checkBoxFilme.setChecked(false);
        checkBoxSerie.setChecked(false);
        checkBoxLivro.setChecked(false);

        spinnerGenero.setSelection(0);
        spinnerRating.setSelection(0);

        radioGroupConsumido.clearCheck();

        editTexTitulo.requestFocus();

        Toast.makeText(this, getString(R.string.campos_limpos), Toast.LENGTH_LONG).show();
    }

    private void cancelar() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private void carregarFindViewById() {
        editTexTitulo = findViewById(R.id.editTexTitulo);
        editTextDiretorAutor = findViewById(R.id.editTextDiretorAutor);
        editTextResumo = findViewById(R.id.editTextResumo);

        checkBoxFilme = findViewById(R.id.checkBoxFilme);
        checkBoxSerie = findViewById(R.id.checkBoxSerie);
        checkBoxLivro = findViewById(R.id.checkBoxLivro);

        spinnerGenero = findViewById(R.id.spinnerGenero);
        spinnerRating = findViewById(R.id.spinnerRating);
        radioGroupConsumido = findViewById(R.id.radioGroupConsumido);
    }

    private void novoRegistro() {

        setTitle(R.string.cadastro_da_resenha);
        resenha = new Resenha("");

        if (sugerirGenero) {
            spinnerGenero.setSelection(ultimoGeneroSalvo);
        }
    }

    private void atualizarRegistro(long id) {

        setTitle(R.string.edicao_da_resenha);

        if (tipoPersistencia == PersistenciaTipo.LITE) {

            ResenhasDatabaseLite database = ResenhasDatabaseLite.getInstance(this);
            resenha = database.resenhasDaoLite.resenhaPorId(id);

        } else if (tipoPersistencia == PersistenciaTipo.ROOM) {

            ResenhaDatabase database = ResenhaDatabase.getDatabase(this);
            resenha = database.resenhaDao().queryForId(id);
        }

        editTexTitulo.setText(resenha.getTitulo());
        editTexTitulo.setSelection(editTexTitulo.getText().length());
        editTextDiretorAutor.setText(resenha.getDiretorAutor());
        editTextResumo.setText(resenha.getResenhaResumo());

        if (resenha.isAssistidoLido()) {
            radioGroupConsumido.check(R.id.radioButtonSim);
        } else {
            radioGroupConsumido.check(R.id.radioButtonNao);
        }

        List<Tipo> tiposConteudo = resenha.getTipos();
        if (tiposConteudo != null) {
            for (Tipo tipo : tiposConteudo) {
                if (tipo == Tipo.FILME) {
                    checkBoxFilme.setChecked(true);
                } else if (tipo == Tipo.SERIE) {
                    checkBoxSerie.setChecked(true);
                } else if (tipo == Tipo.LIVRO) {
                    checkBoxLivro.setChecked(true);
                }
            }
        }

        int generoResenha = resenha.getGenero();
        spinnerGenero.setSelection(generoResenha);

        int ratingResenha = resenha.getResenhaRating();
        spinnerRating.setSelection(ratingResenha);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.resenha_opcoes, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.menuItemSugerirGenero);
        item.setChecked(sugerirGenero);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int botao = item.getItemId();

        if (botao == android.R.id.home) {

            finish();
            return true;

        } else if (botao == R.id.menuItemSalvar) {

            salvarResenha();
            return true;

        } else if (botao == R.id.menuItemLimpar) {

            limparCampos();
            return true;

        } else if (botao == R.id.menuItemCancelar) {

            cancelar();
            return true;

        } else if (botao == R.id.menuItemSugerirGenero) {

            boolean valor = !item.isChecked();
            salvarGeneroConfig(valor);
            item.setChecked(valor);

            if (sugerirGenero && modo == NOVO) {
                spinnerGenero.setSelection(ultimoGeneroSalvo);
            }

            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void popularSpinners() {

        //GENERO
        ArrayAdapter<CharSequence> adapterGenero = ArrayAdapter.createFromResource(this, R.array.generos_base, android.R.layout.simple_spinner_item);
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenero.setAdapter(adapterGenero);

        //RATING
        ArrayAdapter<CharSequence> adapterRating = ArrayAdapter.createFromResource(this, R.array.resenha_rating_array, android.R.layout.simple_spinner_item);
        adapterRating.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRating.setAdapter(adapterRating);
    }

    private void lerGeneroConfig() {
        //Recupera o último genero utilizado pelo usuário na criação de uma resenha
        SharedPreferences shared = getSharedPreferences(ActivityListarResenha.ARQUIVO, Context.MODE_PRIVATE);
        sugerirGenero = shared.getBoolean(SUGERIR_GENERO, sugerirGenero);
        ultimoGeneroSalvo = shared.getInt(ULTIMO_GENERO, ultimoGeneroSalvo);
    }

    private void salvarGeneroConfig(boolean novoValor) {
        //Salva sempre o último gênero salvo pra poder sugerir, caso o usuário deseje, tipo rediscagem
        SharedPreferences shared = getSharedPreferences(ActivityListarResenha.ARQUIVO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(SUGERIR_GENERO, novoValor);
        editor.apply();
        sugerirGenero = novoValor;
    }

    private void salvarUltimoGenero(int novoGenero) {
        //Salva o último genero e já atualiza o valor em memória caso o usuário deseje utilizar.
        ultimoGeneroSalvo = novoGenero;
        SharedPreferences shared = getSharedPreferences(ActivityListarResenha.ARQUIVO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt(ULTIMO_GENERO, novoGenero);
        editor.apply();
    }
}