package com.example.chamati;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chamati.DataBase.DataBaseHelper;
import com.example.chamati.Model.Chamado;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CadastroChamadoActivity extends AppCompatActivity {

    private EditText etTitulo, etLocal, etDescricao;
    private MaterialButtonToggleGroup toggleTipo;
    private TextView tvDataAtual;
    private MaterialButton btnRegistrar;
    private ImageView btnVoltar;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_chamado);

        dbHelper = new DataBaseHelper(this);

        etTitulo = findViewById(R.id.etTitulo);
        etLocal = findViewById(R.id.etLocal);
        etDescricao = findViewById(R.id.etDescricao);
        toggleTipo = findViewById(R.id.toggleTipo);
        tvDataAtual = findViewById(R.id.tvDataAtual);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnVoltar = findViewById(R.id.btnVoltar);

        String currentData = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tvDataAtual.setText(currentData);

        btnVoltar.setOnClickListener(v -> finish());

        btnRegistrar.setOnClickListener(v -> {
            String titulo = etTitulo.getText().toString().trim();
            String local = etLocal.getText().toString().trim();
            String descricao = etDescricao.getText().toString().trim();
            
            if (titulo.isEmpty() || local.isEmpty() || descricao.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (toggleTipo.getCheckedButtonId() == -1) {
                Toast.makeText(this, "Selecione o tipo do chamado", Toast.LENGTH_SHORT).show();
                return;
            }

            int tipo = (toggleTipo.getCheckedButtonId() == R.id.btnInfra) ? 1 : 0;

            Chamado novo = new Chamado(titulo, descricao, local, tipo, currentData, "aberto");
            novo.setSolucao("");
            long id = dbHelper.inserirChamado(novo);

            if (id != -1) {
                Toast.makeText(this, "Chamado registrado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Erro ao registrar chamado", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
