package com.example.chamati.Cloud;

import com.example.chamati.Model.Chamado;
import com.parse.GetCallback;
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

    public void atualizarChamadoCloud(Chamado chamado, SyncCallback callback) {
        String parseObjectId = chamado.getParseObjectId();
        if (parseObjectId == null || parseObjectId.isEmpty()) {
            if (callback != null) {
                callback.onError("Chamado não sincronizado com a nuvem");
            }
            return;
        }

        ParseObject.createWithoutData("Chamado", parseObjectId).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null && parseObject != null) {
                    parseObject.put("status", chamado.getStatus());
                    parseObject.put("solucao", chamado.getSolucao());

                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException saveError) {
                            if (saveError == null) {
                                if (callback != null) {
                                    callback.onSuccess(parseObject.getObjectId());
                                }
                            } else {
                                if (callback != null) {
                                    callback.onError(saveError.getMessage());
                                }
                            }
                        }
                    });
                } else {
                    if (callback != null) {
                        callback.onError(e != null ? e.getMessage() : "Erro ao buscar objeto na nuvem");
                    }
                }
            }
        });
    }
}
