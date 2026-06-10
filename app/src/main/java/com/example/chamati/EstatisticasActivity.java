package com.example.chamati;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.chamati.DataBase.DataBaseHelper;

public class EstatisticasActivity extends AppCompatActivity {

    private TextView tvCountAbertos, tvCountAndamento, tvCountConcluidos, tvTotal;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estatisticas);

        dbHelper = new DataBaseHelper(this);

        tvTotal = findViewById(R.id.tvTotal);
        tvCountAbertos = findViewById(R.id.tvCountAbertos);
        tvCountAndamento = findViewById(R.id.tvCountAndamento);
        tvCountConcluidos = findViewById(R.id.tvCountConcluidos);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
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
