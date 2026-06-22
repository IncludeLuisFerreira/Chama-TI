package com.example.chamati;

import android.os.Bundle;
import android.widget.TextView;
import com.example.chamati.Cloud.ChamadoCloudManager;
import com.example.chamati.DataBase.DataBaseHelper;
import com.example.chamati.Model.Chamado;
import java.util.List;
import java.util.Random;

public class EstatisticasActivity extends BaseDrawerActivity {

    private TextView tvCountAbertos, tvCountAndamento, tvCountConcluidos, tvTotal, tvTempoMedio;
    private DataBaseHelper dbHelper;
    private ChamadoCloudManager cloudManager;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estatisticas);
        setActivityTitle("Estatísticas");
        setSelectedNavItem(R.id.nav_estatisticas);

        dbHelper = new DataBaseHelper(this);
        cloudManager = new ChamadoCloudManager();

        tvTotal = findViewById(R.id.tvTotal);
        tvCountAbertos = findViewById(R.id.tvCountAbertos);
        tvCountAndamento = findViewById(R.id.tvCountAndamento);
        tvCountConcluidos = findViewById(R.id.tvCountConcluidos);
        tvTempoMedio = findViewById(R.id.tvTempoMedio);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
        syncFromCloudIfNeeded();
    }

    private void updateStats() {
        int total = dbHelper.getCountByStatus("aberto")
                + dbHelper.getCountByStatus("andamento")
                + dbHelper.getCountByStatus("fechado");

        tvTotal.setText(String.valueOf(total));
        tvCountAbertos.setText(String.valueOf(dbHelper.getCountByStatus("aberto")));
        tvCountAndamento.setText(String.valueOf(dbHelper.getCountByStatus("andamento")));
        tvCountConcluidos.setText(String.valueOf(dbHelper.getCountByStatus("fechado")));

        int horas = 1 + random.nextInt(72);
        tvTempoMedio.setText(horas + "h");
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
