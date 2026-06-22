package com.example.chamati;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chamati.Cloud.ChamadoCloudManager;
import com.example.chamati.DataBase.DataBaseHelper;
import com.example.chamati.Model.Chamado;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class ListaChamadoActivity extends BaseDrawerActivity implements FiltrosBottomSheet.OnFiltroListener {

    private RecyclerView rvChamados;
    private ChamadoAdapter adapter;
    private DataBaseHelper dbHelper;
    private ChamadoCloudManager cloudManager;
    private TextView tvContador;
    private FloatingActionButton fabAdd;

    private String filtroStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_chamado);
        setActivityTitle("Meus Chamados");
        setSelectedNavItem(R.id.nav_listagem);

        dbHelper = new DataBaseHelper(this);
        cloudManager = new ChamadoCloudManager();

        rvChamados = findViewById(R.id.rvChamados);
        tvContador = findViewById(R.id.tvContadorChamados);
        fabAdd = findViewById(R.id.fabAdd);

        rvChamados.setLayoutManager(new LinearLayoutManager(this));

        if (getIntent() != null) {
            filtroStatus = getIntent().getStringExtra("FILTRO_STATUS");
            if (filtroStatus != null) {
                String titulo = "Chamados";
                switch (filtroStatus) {
                    case "aberto": titulo = "Chamados Abertos"; break;
                    case "andamento": titulo = "Chamados em Andamento"; break;
                    case "fechado": titulo = "Chamados Concluídos"; break;
                }
                setActivityTitle(titulo);
            }
        }

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
        if (filtroStatus != null) {
            List<Chamado> filtrados = dbHelper.getChamadosFiltrados(null, filtroStatus, null, null);
            atualizarLista(filtrados);
            return;
        }

        List<Chamado> local = dbHelper.getAllChamados();

        if (!local.isEmpty()) {
            atualizarLista(local);
            sincronizarDaNuvem();
            return;
        }

        tvContador.setText("Sincronizando da nuvem...");
        cloudManager.getAllChamados(new ChamadoCloudManager.ChamadoListCallback() {
            @Override
            public void onSuccess(List<Chamado> chamados) {
                for (Chamado c : chamados) {
                    dbHelper.upsertChamado(c);
                }
                runOnUiThread(() -> {
                    List<Chamado> atualizados = dbHelper.getAllChamados();
                    atualizarLista(atualizados);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    tvContador.setText("0 chamados");
                    Toast.makeText(ListaChamadoActivity.this,
                            "Erro ao sincronizar da nuvem: " + errorMessage,
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void sincronizarDaNuvem() {
        cloudManager.getAllChamados(new ChamadoCloudManager.ChamadoListCallback() {
            @Override
            public void onSuccess(List<Chamado> chamados) {
                boolean atualizou = false;
                for (Chamado c : chamados) {
                    Chamado existente = dbHelper.getChamadoByParseObjectId(c.getParseObjectId());
                    if (existente == null) {
                        dbHelper.upsertChamado(c);
                        atualizou = true;
                    }
                }
                if (atualizou) {
                    runOnUiThread(() -> {
                        List<Chamado> atualizados = dbHelper.getAllChamados();
                        atualizarLista(atualizados);
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
            }
        });
    }

    private void atualizarLista(List<Chamado> lista) {
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
        filtroStatus = null;
        List<Chamado> filtrados = dbHelper.getChamadosFiltrados(tipo, status, dataIni, dataFim);
        adapter.updateList(filtrados);
        tvContador.setText(filtrados.size() + " chamados");
    }

    @Override
    public void onFiltroLimpo() {
        filtroStatus = null;
        carregarChamados();
    }
}
