package com.example.chamati;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.chamati.DataBase.DataBaseHelper;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {

    private TextView tvCountAbertos, tvCountAndamento, tvCountConcluidos;
    private MaterialCardView btnNovoChamado, btnVerChamados;
    private DataBaseHelper dbHelper;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DataBaseHelper(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_novo_chamado) {
                startActivity(new Intent(MainActivity.this, CadastroChamadoActivity.class));
            } else if (itemId == R.id.nav_listagem) {
                startActivity(new Intent(MainActivity.this, ListaChamadoActivity.class));
            } else if (itemId == R.id.nav_estatisticas) {
                startActivity(new Intent(MainActivity.this, EstatisticasActivity.class));
            } else if (itemId == R.id.nav_sobre) {
                startActivity(new Intent(MainActivity.this, SobreActivity.class));
            }

            drawerLayout.closeDrawers();
            return true;
        });

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
