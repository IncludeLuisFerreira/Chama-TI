package com.example.chamati;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chamati.DataBase.DataBaseHelper;
import com.example.chamati.Model.Chamado;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class ListaChamadoActivity extends BaseDrawerActivity implements FiltrosBottomSheet.OnFiltroListener {

    private RecyclerView rvChamados;
    private ChamadoAdapter adapter;
    private DataBaseHelper dbHelper;
    private TextView tvContador;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_chamado);
        setActivityTitle("Meus Chamados");
        setSelectedNavItem(R.id.nav_listagem);

        dbHelper = new DataBaseHelper(this);

        rvChamados = findViewById(R.id.rvChamados);
        tvContador = findViewById(R.id.tvContadorChamados);
        fabAdd = findViewById(R.id.fabAdd);

        rvChamados.setLayoutManager(new LinearLayoutManager(this));

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, CadastroChamadoActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_chamado, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            FiltrosBottomSheet bottomSheet = new FiltrosBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "filtros");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarChamados();
    }

    private void carregarChamados() {
        List<Chamado> lista = dbHelper.getAllChamados();
        tvContador.setText(lista.size() + " chamados");

        if (adapter == null) {
            adapter = new ChamadoAdapter(lista, this);
            rvChamados.setAdapter(adapter);
        } else {
            adapter.updateList(lista);
        }
    }

    @Override
    public void onFiltroAplicado(Integer tipo, String status, String dataIni, String dataFim) {
        List<Chamado> filtrados = dbHelper.getChamadosFiltrados(tipo, status, dataIni, dataFim);
        adapter.updateList(filtrados);
        tvContador.setText(filtrados.size() + " chamados");
    }

    @Override
    public void onFiltroLimpo() {
        carregarChamados();
    }
}
