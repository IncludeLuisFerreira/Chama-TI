package com.example.chamati;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.example.chamati.Cloud.ChamadoCloudManager;
import com.example.chamati.DataBase.DataBaseHelper;
import com.example.chamati.Model.Chamado;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class MainActivity extends BaseDrawerActivity {

    private TextView tvCountAbertos, tvCountAndamento, tvCountConcluidos;
    private MaterialCardView btnNovoChamado, btnVerChamados, cardAbertos, cardAndamento, cardConcluidos;
    private DataBaseHelper dbHelper;
    private ChamadoCloudManager cloudManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActivityTitle("ChamaTI");
        setSelectedNavItem(R.id.nav_home);

        dbHelper = new DataBaseHelper(this);
        cloudManager = new ChamadoCloudManager();

        tvCountAbertos = findViewById(R.id.tvCountAbertos);
        tvCountAndamento = findViewById(R.id.tvCountAndamento);
        tvCountConcluidos = findViewById(R.id.tvCountConcluidos);
        btnNovoChamado = findViewById(R.id.btnNovoChamado);
        btnVerChamados = findViewById(R.id.btnVerChamados);
        cardAbertos = findViewById(R.id.cardAbertos);
        cardAndamento = findViewById(R.id.cardAndamento);
        cardConcluidos = findViewById(R.id.cardConcluidos);

        btnNovoChamado.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CadastroChamadoActivity.class);
            startActivity(intent);
        });

        btnVerChamados.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ListaChamadoActivity.class));
        });

        cardAbertos.setOnClickListener(v -> abrirListaFiltrada("aberto"));
        cardAndamento.setOnClickListener(v -> abrirListaFiltrada("andamento"));
        cardConcluidos.setOnClickListener(v -> abrirListaFiltrada("fechado"));
    }

    private void abrirListaFiltrada(String status) {
        Intent intent = new Intent(MainActivity.this, ListaChamadoActivity.class);
        intent.putExtra("FILTRO_STATUS", status);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
        syncFromCloudIfNeeded();
    }

    private void updateStats() {
        int abertos = dbHelper.getCountByStatus("aberto");
        int andamento = dbHelper.getCountByStatus("andamento");
        int concluidos = dbHelper.getCountByStatus("fechado");

        tvCountAbertos.setText(String.valueOf(abertos));
        tvCountAndamento.setText(String.valueOf(andamento));
        tvCountConcluidos.setText(String.valueOf(concluidos));
    }

    private void syncFromCloudIfNeeded() {
        List<Chamado> local = dbHelper.getAllChamados();
        if (!local.isEmpty()) return;

        cloudManager.getAllChamados(new ChamadoCloudManager.ChamadoListCallback() {
            @Override
            public void onSuccess(List<Chamado> chamados) {
                for (Chamado c : chamados) {
                    dbHelper.upsertChamado(c);
                }
                runOnUiThread(() -> updateStats());
            }

            @Override
            public void onError(String errorMessage) {
            }
        });
    }
}
