package com.example.chamati;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chamati.DataBase.DataBaseHelper;
import com.example.chamati.Model.Chamado;
import com.example.chamati.R;

public class CadastroChamadoActivity extends AppCompatActivity {

    // Declaração dos componentes
    private EditText editTitulo;
    private EditText editDescricao;
    private EditText editLocal;
    private RadioGroup radioGroupTipo;
    private Button btnSalvar;
    private Button btnCancelar;

    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_chamado);

        // Inicializa o banco de dados
        dbHelper = new DataBaseHelper(this);

        // Inicializa os componentes
        initViews();

        // Configura os click listeners
        setupListeners();
    }

    private void initViews() {
        editTitulo = findViewById(R.id.editTitulo);
        editDescricao = findViewById(R.id.editDescricao);
        editLocal = findViewById(R.id.editLocal);
        radioGroupTipo = findViewById(R.id.radioGroupTipo);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnCancelar = findViewById(R.id.btnCancelar);
    }

    private void setupListeners() {
        btnSalvar.setOnClickListener(v -> salvarChamado());
        btnCancelar.setOnClickListener(v -> finish()); // Fecha a tela
    }

    private void salvarChamado() {
        // Pegar os valores dos campos
        String titulo = editTitulo.getText().toString().trim();
        String descricao = editDescricao.getText().toString().trim();
        String local = editLocal.getText().toString().trim();

        // Pegar o tipo selecionado
        int tipo = getTipoSelecionado();

        // Validar campos obrigatórios
        if (!validarCampos(titulo, descricao, local)) {
            return;
        }

        // Criar objeto chamado
        long dataCadastro = System.currentTimeMillis();
        String status = "aberto"; // Status inicial

        Chamado novoChamado = new Chamado(titulo, descricao, local, tipo, dataCadastro, status);

        // Salvar no banco de dados
        long id = dbHelper.insert(novoChamado);

        if (id != -1) {
            // Sucesso
            Toast.makeText(this, "Chamado criado com sucesso! ID: " + id, Toast.LENGTH_LONG).show();
            finish(); // Fecha a tela e volta
        } else {
            // Erro
            Toast.makeText(this, "Erro ao criar chamado. Tente novamente.", Toast.LENGTH_SHORT).show();
        }
    }

    private int getTipoSelecionado() {
        int selectedId = radioGroupTipo.getCheckedRadioButtonId();
        if (selectedId == -1) {
            return 0; // Padrão TI
        }
        RadioButton radioButton = findViewById(selectedId);
        if (radioButton == null) {
            return 0; // Padrão TI
        }
        String tipoSelecionado = radioButton.getText().toString();
        return tipoSelecionado.equals("TI") ? 0 : 1;
    }

    private boolean validarCampos(String titulo, String descricao, String local) {
        if (TextUtils.isEmpty(titulo)) {
            editTitulo.setError("Título é obrigatório");
            editTitulo.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(descricao)) {
            editDescricao.setError("Descrição é obrigatória");
            editDescricao.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(local)) {
            editLocal.setError("Local é obrigatório");
            editLocal.requestFocus();
            return false;
        }

        return true;
    }
}