package com.example.chamati;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chamati.Model.Chamado;
import java.util.List;

public class ChamadoAdapter extends RecyclerView.Adapter<ChamadoAdapter.ChamadoViewHolder> {

    private List<Chamado> chamados;
    private Context context;

    public ChamadoAdapter(List<Chamado> chamados, Context context) {
        this.chamados = chamados;
        this.context = context;
    }

    @NonNull
    @Override
    public ChamadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chamado, parent, false);
        return new ChamadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChamadoViewHolder holder, int position) {
        Chamado chamado = chamados.get(position);

        holder.tvTitulo.setText(chamado.getTitulo());
        holder.tvData.setText(chamado.getDataCadastro());

        // Tipo
        if (chamado.getTipo() == 0) {
            holder.tvTipo.setText("TI");
            holder.tvTipo.setBackgroundResource(R.drawable.badge_ti);
        } else {
            holder.tvTipo.setText("Infraestrutura");
            holder.tvTipo.setBackgroundResource(R.drawable.badge_infra);
        }

        // Status
        String statusText;
        int statusColor;
        switch (chamado.getStatus()) {
            case "andamento":
                statusText = "Em Atendimento";
                statusColor = ContextCompat.getColor(context, R.color.status_andamento);
                break;
            case "fechado":
                statusText = "Concluído";
                statusColor = ContextCompat.getColor(context, R.color.status_concluido);
                break;
            case "aberto":
            default:
                statusText = "Aberto";
                statusColor = ContextCompat.getColor(context, R.color.status_aberto);
                break;
        }

        holder.tvStatus.setText(statusText);
        holder.viewStatusDot.setBackgroundTintList(ColorStateList.valueOf(statusColor));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AtendimentoActivity.class);
            intent.putExtra("CHAMADO_ID", chamado.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chamados.size();
    }

    public void updateList(List<Chamado> newList) {
        this.chamados = newList;
        notifyDataSetChanged();
    }

    static class ChamadoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvTipo, tvData, tvStatus;
        View viewStatusDot;

        public ChamadoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvItemTitulo);
            tvTipo = itemView.findViewById(R.id.tvItemTipo);
            tvData = itemView.findViewById(R.id.tvItemData);
            tvStatus = itemView.findViewById(R.id.tvItemStatus);
            viewStatusDot = itemView.findViewById(R.id.viewStatusDot);
        }
    }
}
