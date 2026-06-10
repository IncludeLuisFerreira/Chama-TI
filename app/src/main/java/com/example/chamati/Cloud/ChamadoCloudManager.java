package com.example.chamati.Cloud;

import com.example.chamati.Model.Chamado;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class ChamadoCloudManager {

    public interface SyncCallback {
        void onSuccess(String parseObjectId);
        void onError(String errorMessage);
    }

    public void salvarChamadoCloud(Chamado chamado, SyncCallback callback) {
        ParseObject parseObject = new ParseObject("Chamado");
        parseObject.put("titulo", chamado.getTitulo());
        parseObject.put("descricao", chamado.getDescricao());
        parseObject.put("local", chamado.getLocal());
        parseObject.put("tipo", chamado.getTipo());
        parseObject.put("dataCadastro", chamado.getDataCadastro());
        parseObject.put("status", chamado.getStatus());
        parseObject.put("solucao", chamado.getSolucao());

        String imagemPath = chamado.getImagemPath();
        if (imagemPath != null && !imagemPath.isEmpty()) {
            parseObject.put("imagemPath", imagemPath);
        }

        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    callback.onSuccess(parseObject.getObjectId());
                } else {
                    callback.onError(e.getMessage());
                }
            }
        });
    }
}
