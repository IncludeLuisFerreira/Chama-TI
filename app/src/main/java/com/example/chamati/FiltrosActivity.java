package com.example.chamati;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chamati.DataBase.DataBaseHelper;
import com.example.chamati.Model.Chamado;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class FiltrosActivity extends AppCompatActivity {
    private RadioGroup radioStatus;
    private Button btnData, btnFiltrar, btnLimpar;
    private TextView txtDataSelecionada;
    private ListView listView;
    private DataBaseHelper db;
    private long dataSelecionada = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros);
        
        radioStatus = findViewById(R.id.radioStatus);
        btnData = findViewById(R.id.btnData);
        btnFiltrar = findViewById(R.id.btnFiltrar);
        btnLimpar = findViewById(R.id.btnLimpar);
        txtDataSelecionada = findViewById(R.id.txtDataSelecionada);
        listView = findViewById(R.id.listViewFiltros);
        
        db = new DataBaseHelper(this);
        
        btnData.setOnClickListener(v -> selecionarData());
        btnFiltrar.setOnClickListener(v -> aplicarFiltros());
        btnLimpar.setOnClickListener(v -> limparFiltros());
    }
    
    private void selecionarData() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, day, 0, 0, 0);
            dataSelecionada = selected.getTimeInMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            txtDataSelecionada.setText("Data: " + sdf.format(dataSelecionada));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }
    
    private void aplicarFiltros() {
        String status = null;
        int checkedId = radioStatus.getCheckedRadioButtonId();
        if (checkedId == R.id.radioAberto) status = "aberto";
        else if (checkedId == R.id.radioAndamento) status = "andamento";
        else if (checkedId == R.id.radioFechado) status = "fechado";
        
        ArrayList<Chamado> chamados = db.getChamadosFiltrados(status, dataSelecionada);
        exibirChamados(chamados);
    }
    
    private void limparFiltros() {
        radioStatus.clearCheck();
        dataSelecionada = -1;
        txtDataSelecionada.setText("");
        listView.setAdapter(null);
    }
    
    private void exibirChamados(ArrayList<Chamado> chamados) {
        ArrayList<HashMap<String, String>> lista = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        
        for (Chamado c : chamados) {
            HashMap<String, String> map = new HashMap<>();
            map.put("titulo", c.getTitulo());
            map.put("info", c.getTipoAsString() + " | " + c.getStatusFormatado() + " | " + sdf.format(c.getDataCadastro()));
            lista.add(map);
        }
        
        SimpleAdapter adapter = new SimpleAdapter(this, lista, android.R.layout.simple_list_item_2,
                new String[]{"titulo", "info"}, new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);
    }
}