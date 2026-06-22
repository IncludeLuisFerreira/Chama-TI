package com.example.chamati;

import android.os.Bundle;
import android.widget.TextView;
import com.example.chamati.DataBase.DataBaseHelper;

public class EstatisticasActivity extends BaseDrawerActivity {

    private TextView tvCountAbertos, tvCountAndamento, tvCountConcluidos, tvTotal;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estatisticas);
        setActivityTitle("Estatísticas");
        setSelectedNavItem(R.id.nav_estatisticas);

        dbHelper = new DataBaseHelper(this);

        tvTotal = findViewById(R.id.tvTotal);
        tvCountAbertos = findViewById(R.id.tvCountAbertos);
        tvCountAndamento = findViewById(R.id.tvCountAndamento);
        tvCountConcluidos = findViewById(R.id.tvCountConcluidos);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
    }

    private void updateStats() {
        int total = dbHelper.getCountByStatus("aberto")
                + dbHelper.getCountByStatus("andamento")
                + dbHelper.getCountByStatus("fechado");

        tvTotal.setText(String.valueOf(total));
        tvCountAbertos.setText(String.valueOf(dbHelper.getCountByStatus("aberto")));
        tvCountAndamento.setText(String.valueOf(dbHelper.getCountByStatus("andamento")));
        tvCountConcluidos.setText(String.valueOf(dbHelper.getCountByStatus("fechado")));
    }
}
