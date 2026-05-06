package com.example.chamati;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chamati.DataBase.DataBaseHelper;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    private TextView tvCountAbertos, tvCountAndamento, tvCountConcluidos;
    private MaterialCardView btnNovoChamado, btnVerChamados;
    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DataBaseHelper(this);

        tvCountAbertos = findViewById(R.id.tvCountAbertos);
        tvCountAndamento = findViewById(R.id.tvCountAndamento);
        tvCountConcluidos = findViewById(R.id.tvCountConcluidos);
        btnNovoChamado = findViewById(R.id.btnNovoChamado);
        btnVerChamados = findViewById(R.id.btnVerChamados);

        btnNovoChamado.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CadastroChamadoActivity.class);
            startActivity(intent);
        });

        btnVerChamados.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListaChamadoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
    }

    private void updateStats() {
        int abertos = dbHelper.getCountByStatus("aberto");
        int andamento = dbHelper.getCountByStatus("andamento");
        int concluidos = dbHelper.getCountByStatus("fechado");

        tvCountAbertos.setText(String.valueOf(abertos));
        tvCountAndamento.setText(String.valueOf(andamento));
        tvCountConcluidos.setText(String.valueOf(concluidos));
    }
}
