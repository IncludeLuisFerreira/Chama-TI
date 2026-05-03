package com.example.chamati;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chamati.DataBase.DataBaseHelper;
import com.example.chamati.Model.Chamado;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ListaChamadoActivity extends AppCompatActivity {
    private ListView listView;
    private Button btnFiltros;
    private DataBaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_chamado);
        
        listView = findViewById(R.id.listView);
        btnFiltros = findViewById(R.id.btnFiltros);
        db = new DataBaseHelper(this);
        
        btnFiltros.setOnClickListener(v -> startActivity(new Intent(this, FiltrosActivity.class)));
        
        carregarChamados();
        
        listView.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, String> item = (HashMap<String, String>) parent.getItemAtPosition(position);
            Intent intent = new Intent(this, AtendimentoActivity.class);
            intent.putExtra("id", Long.parseLong(item.get("id")));
            startActivity(intent);
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        carregarChamados();
    }
    
    private void carregarChamados() {
        ArrayList<Chamado> chamados = db.getList();
        ArrayList<HashMap<String, String>> lista = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        
        for (Chamado c : chamados) {
            HashMap<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(c.getId()));
            map.put("titulo", c.getTitulo());
            map.put("info", c.getTipoAsString() + " | " + c.getStatusFormatado() + " | " + sdf.format(c.getDataCadastro()));
            lista.add(map);
        }
        
        SimpleAdapter adapter = new SimpleAdapter(this, lista, android.R.layout.simple_list_item_2,
                new String[]{"titulo", "info"}, new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);
    }
}