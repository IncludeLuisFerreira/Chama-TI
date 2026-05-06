package com.example.chamati;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FiltrosBottomSheet extends BottomSheetDialogFragment {

    private ChipGroup chipGroupArea, chipGroupStatus;
    private EditText etPeriodo;
    private MaterialButton btnAplicar, btnLimpar;
    private String dataInicial = "", dataFinal = "";

    public interface OnFiltroListener {
        void onFiltroAplicado(Integer tipo, String status, String dataIni, String dataFim);
        void onFiltroLimpo();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_filtros, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chipGroupArea = view.findViewById(R.id.chipGroupArea);
        chipGroupStatus = view.findViewById(R.id.chipGroupStatus);
        etPeriodo = view.findViewById(R.id.etPeriodo);
        btnAplicar = view.findViewById(R.id.btnAplicarFiltros);
        btnLimpar = view.findViewById(R.id.btnLimparFiltros);

        etPeriodo.setOnClickListener(v -> abrirCalendario());

        btnAplicar.setOnClickListener(v -> {
            Integer tipo = null;
            int areaId = chipGroupArea.getCheckedChipId();
            if (areaId != -1) {
                Chip chip = view.findViewById(areaId);
                tipo = chip.getText().toString().contains("Infraestrutura") ? 1 : 0;
            }

            String status = null;
            int statusId = chipGroupStatus.getCheckedChipId();
            if (statusId != -1) {
                Chip chip = view.findViewById(statusId);
                if (chip.getText().toString().contains("Aberto")) status = "aberto";
                else if (chip.getText().toString().contains("Atendimento")) status = "andamento";
                else if (chip.getText().toString().contains("Concluído")) status = "fechado";
            }

            try {
                OnFiltroListener listener = (OnFiltroListener) getActivity();
                if (listener != null) {
                    listener.onFiltroAplicado(tipo, status, dataInicial, dataFinal);
                }
            } catch (ClassCastException e) {
                Toast.makeText(getContext(), "Erro ao aplicar filtros", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        });

        btnLimpar.setOnClickListener(v -> {
            try {
                OnFiltroListener listener = (OnFiltroListener) getActivity();
                if (listener != null) {
                    listener.onFiltroLimpo();
                }
            } catch (ClassCastException e) {
                Toast.makeText(getContext(), "Erro ao limpar filtros", Toast.LENGTH_SHORT).show();
            }
            dismiss();
        });
    }

    private void abrirCalendario() {
        MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Selecione o período")
                .build();

        picker.show(getParentFragmentManager(), "RANGE_PICKER");

        picker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            dataInicial = sdf.format(new Date(selection.first));
            dataFinal = sdf.format(new Date(selection.second));
            etPeriodo.setText(dataInicial + " - " + dataFinal);
        });
    }
}
