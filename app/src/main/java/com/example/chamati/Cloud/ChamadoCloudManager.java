package com.example.chamati.Cloud;

import com.example.chamati.Model.Chamado;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChamadoCloudManager {

    public interface SyncCallback {
        void onSuccess(String parseObjectId);
        void onError(String errorMessage);
    }

    public interface ChamadoListCallback {
        void onSuccess(List<Chamado> chamados);
        void onError(String errorMessage);
    }

    public void getAllChamados(ChamadoListCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Chamado");
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    List<Chamado> chamados = new ArrayList<>();
                    for (ParseObject po : results) {
                        Chamado c = new Chamado();
                        c.setParseObjectId(po.getObjectId());
                        c.setTitulo(po.getString("titulo"));
                        c.setDescricao(po.getString("descricao"));
                        c.setLocal(po.getString("local"));
                        c.setTipo(po.getInt("tipo"));
                        c.setDataCadastro(po.getString("dataCadastro"));
                        c.setStatus(po.getString("status"));
                        c.setSolucao(po.getString("solucao"));
                        String imagemUrl = po.getString("imagemPath");
                        c.setImagemPath(imagemUrl != null ? imagemUrl : "");
                        chamados.add(c);
                    }
                    callback.onSuccess(chamados);
                } else {
                    callback.onError(e.getMessage());
                }
            }
        });
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
            File imageFile = new File(imagemPath);
            if (imageFile.exists()) {
                ParseFile parseFile = new ParseFile(imageFile);
                parseFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            parseObject.put("imagem", parseFile);
                            parseObject.put("imagemPath", parseFile.getUrl());
                        }
                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException saveError) {
                                if (saveError == null) {
                                    callback.onSuccess(parseObject.getObjectId());
                                } else {
                                    callback.onError(saveError.getMessage());
                                }
                            }
                        });
                    }
                });
                return;
            }
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
