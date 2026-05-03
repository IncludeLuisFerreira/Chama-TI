package com.example.chamati;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chamati.DataBase.DataBaseHelper;
import com.example.chamati.Model.Chamado;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AtendimentoActivity extends AppCompatActivity {
    private TextView txtTitulo, txtDescricao, txtLocal, txtTipo, txtData;
    private EditText editSolucao;
    private Spinner spinnerStatus;
    private Button btnSalvar;
    private DataBaseHelper db;
    private long chamadoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atendimento);
        
        txtTitulo = findViewById(R.id.txtTitulo);
        txtDescricao = findViewById(R.id.txtDescricao);
        txtLocal = findViewById(R.id.txtLocal);
        txtTipo = findViewById(R.id.txtTipo);
        txtData = findViewById(R.id.txtData);
        editSolucao = findViewById(R.id.editSolucao);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnSalvar = findViewById(R.id.btnSalvar);
        
        db = new DataBaseHelper(this);
        chamadoId = getIntent().getLongExtra("id", -1);
        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);
        
        carregarChamado();
        
        btnSalvar.setOnClickListener(v -> salvarAtendimento());
    }
    
    private void carregarChamado() {
        Chamado c = db.getChamadoById(chamadoId);
        if (c != null) {
            txtTitulo.setText(c.getTitulo());
            txtDescricao.setText(c.getDescricao());
            txtLocal.setText(c.getLocal());
            txtTipo.setText(c.getTipoAsString());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            txtData.setText(sdf.format(c.getDataCadastro()));
            
            if (c.getSolucao() != null) {
                editSolucao.setText(c.getSolucao());
            }
            
            int pos = 0;
            if (c.getStatus().equals("andamento")) pos = 1;
            else if (c.getStatus().equals("fechado")) pos = 2;
            spinnerStatus.setSelection(pos);
        }
    }
    
    private void salvarAtendimento() {
        String solucao = editSolucao.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString().toLowerCase();
        if (status.contains("andamento")) status = "andamento";
        else if (status.contains("fechado")) status = "fechado";
        else status = "aberto";
        
        if (db.updateChamado(chamadoId, status, solucao)) {
            Toast.makeText(this, "Atendimento salvo!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erro ao salvar", Toast.LENGTH_SHORT).show();
        }
    }
}