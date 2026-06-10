package com.example.chamati;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.example.chamati.Cloud.ChamadoCloudManager;
import com.example.chamati.DataBase.DataBaseHelper;
import com.example.chamati.Model.Chamado;
import com.google.android.material.button.MaterialButton;
import java.io.File;

public class AtendimentoActivity extends AppCompatActivity {

    private TextView tvAtendTitulo, tvAtendTipo, tvAtendStatus, tvAtendData, tvAtendLocal, tvAtendDescricao;
    private AutoCompleteTextView spinnerStatus;
    private EditText etSolucao;
    private MaterialButton btnSalvar;
    private ImageView ivAtendimentoImagem;
    private DataBaseHelper dbHelper;
    private ChamadoCloudManager cloudManager;
    private Chamado chamado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atendimento);

        dbHelper = new DataBaseHelper(this);
        cloudManager = new ChamadoCloudManager();

        int id = getIntent().getIntExtra("CHAMADO_ID", -1);
        chamado = dbHelper.getChamadoById(id);

        if (chamado == null) {
            finish();
            return;
        }

        tvAtendTitulo = findViewById(R.id.tvAtendTitulo);
        tvAtendTipo = findViewById(R.id.tvAtendTipo);
        tvAtendStatus = findViewById(R.id.tvAtendStatus);
        tvAtendData = findViewById(R.id.tvAtendData);
        tvAtendLocal = findViewById(R.id.tvAtendLocal);
        tvAtendDescricao = findViewById(R.id.tvAtendDescricao);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        etSolucao = findViewById(R.id.etSolucao);
        btnSalvar = findViewById(R.id.btnSalvarAtendimento);
        ivAtendimentoImagem = findViewById(R.id.ivAtendimentoImagem);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        setupData();

        btnSalvar.setOnClickListener(v -> {
            String novoStatusStr = spinnerStatus.getText().toString();
            String statusDB = "aberto";
            if (novoStatusStr.equals("Em Atendimento")) statusDB = "andamento";
            else if (novoStatusStr.equals("Concluído")) statusDB = "fechado";

            String solucao = etSolucao.getText().toString().trim();
            
            if (statusDB.equals("fechado") && solucao.isEmpty()) {
                Toast.makeText(this, "Informe a solução antes de concluir o chamado", Toast.LENGTH_SHORT).show();
                return;
            }

            chamado.setStatus(statusDB);
            chamado.setSolucao(solucao);

            if (dbHelper.atualizarChamado(chamado) > 0) {
                Toast.makeText(this, "Atendimento salvo!", Toast.LENGTH_SHORT).show();

                cloudManager.atualizarChamadoCloud(chamado, new ChamadoCloudManager.SyncCallback() {
                    @Override
                    public void onSuccess(String parseObjectId) {
                        // Atualizado na nuvem com sucesso
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Falha silenciosa - dados ja salvos localmente
                    }
                });

                finish();
            } else {
                Toast.makeText(this, "Erro ao salvar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupData() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chamado #" + chamado.getId());
        }
        tvAtendTitulo.setText(chamado.getTitulo());

        String imagemPath = chamado.getImagemPath();
        if (imagemPath != null && !imagemPath.isEmpty() && new File(imagemPath).exists()) {
            ivAtendimentoImagem.setImageBitmap(BitmapFactory.decodeFile(imagemPath));
            ivAtendimentoImagem.setImageTintList(null);
        } else {
            ivAtendimentoImagem.setImageResource(android.R.drawable.ic_menu_gallery);
            ivAtendimentoImagem.setImageTintList(
                    android.content.res.ColorStateList.valueOf(
                            ContextCompat.getColor(this, R.color.text_secondary)));
        }
        tvAtendData.setText(chamado.getDataCadastro());
        tvAtendLocal.setText(chamado.getLocal());
        tvAtendDescricao.setText(chamado.getDescricao());
        
        String solucao = chamado.getSolucao();
        etSolucao.setText(solucao != null ? solucao : "");

        if (chamado.getTipo() == 0) {
            tvAtendTipo.setText("TI");
            tvAtendTipo.setBackgroundResource(R.drawable.badge_ti);
        } else {
            tvAtendTipo.setText("Infraestrutura");
            tvAtendTipo.setBackgroundResource(R.drawable.badge_infra);
        }

        updateStatusBadge(chamado.getStatus());

        String[] statuses = {"Aberto", "Em Atendimento", "Concluído"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
        spinnerStatus.setAdapter(adapter);
        
        String currentStatusStr = "Aberto";
        if (chamado.getStatus().equals("andamento")) currentStatusStr = "Em Atendimento";
        else if (chamado.getStatus().equals("fechado")) currentStatusStr = "Concluído";
        spinnerStatus.setText(currentStatusStr, false);
    }

    private void updateStatusBadge(String status) {
        String text = "ABERTO";
        int color = R.color.status_aberto;
        switch (status) {
            case "andamento":
                text = "EM ATENDIMENTO";
                color = R.color.status_andamento;
                break;
            case "fechado":
                text = "CONCLUÍDO";
                color = R.color.status_concluido;
                break;
        }
        tvAtendStatus.setText(text);
        tvAtendStatus.setBackgroundTintList(ContextCompat.getColorStateList(this, color));
    }
}
