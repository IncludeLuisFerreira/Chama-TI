package com.example.chamati;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FiltrosActivity extends AppCompatActivity {

    private ChipGroup chipGroupArea, chipGroupStatus;
    private EditText etPeriodo;
    private MaterialButton btnAplicar, btnLimpar;
    private ImageView btnVoltar;
    private String dataInicial = "", dataFinal = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros);

        chipGroupArea = findViewById(R.id.chipGroupArea);
        chipGroupStatus = findViewById(R.id.chipGroupStatus);
        etPeriodo = findViewById(R.id.etPeriodo);
        btnAplicar = findViewById(R.id.btnAplicarFiltros);
        btnLimpar = findViewById(R.id.btnLimparFiltros);
        btnVoltar = findViewById(R.id.btnVoltarFiltros);

        etPeriodo.setOnClickListener(v -> abrirCalendario());
        btnVoltar.setOnClickListener(v -> finish());

        btnAplicar.setOnClickListener(v -> {
            Intent intent = new Intent();

            // Pega Área (TI = 0, Infra = 1)
            int areaId = chipGroupArea.getCheckedChipId();
            if (areaId != -1) {
                intent.putExtra("FILTRO_AREA", areaId == R.id.chipInfra ? 1 : 0);
            }

            // Pega Status
            int statusId = chipGroupStatus.getCheckedChipId();
            if (statusId != -1) {
                Chip chip = findViewById(statusId);
                String status = "aberto";
                if (chip.getText().toString().contains("Atendimento")) status = "andamento";
                else if (chip.getText().toString().contains("Concluído")) status = "fechado";
                intent.putExtra("FILTRO_STATUS", status);
            }

            intent.putExtra("DATA_INI", dataInicial);
            intent.putExtra("DATA_FIM", dataFinal);

            setResult(RESULT_OK, intent);
            finish();
        });

        btnLimpar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void abrirCalendario() {
        MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Selecione o período")
                .setTheme(com.google.android.material.R.style.ThemeOverlay_Material3_MaterialCalendar)
                .build();

        picker.show(getSupportFragmentManager(), "RANGE_PICKER");

        picker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            dataInicial = sdf.format(new Date(selection.first));
            dataFinal = sdf.format(new Date(selection.second));
            etPeriodo.setText(dataInicial + " - " + dataFinal);
        });
    }
}