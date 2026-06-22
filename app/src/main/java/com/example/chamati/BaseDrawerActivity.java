package com.example.chamati;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.FrameLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public abstract class BaseDrawerActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;
    protected MaterialToolbar toolbar;
    protected NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_drawer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.nav_novo_chamado) {
                intent = new Intent(this, CadastroChamadoActivity.class);
            } else if (itemId == R.id.nav_listagem) {
                intent = new Intent(this, ListaChamadoActivity.class);
            } else if (itemId == R.id.nav_estatisticas) {
                intent = new Intent(this, EstatisticasActivity.class);
            } else if (itemId == R.id.nav_sobre) {
                intent = new Intent(this, SobreActivity.class);
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }

    @Override
    public void setContentView(int layoutResID) {
        FrameLayout container = findViewById(R.id.content_frame);
        if (container != null) {
            container.removeAllViews();
            LayoutInflater.from(this).inflate(layoutResID, container);
        }
    }

    protected void setActivityTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    protected void setSelectedNavItem(int itemId) {
        navigationView.setCheckedItem(itemId);
    }
}
