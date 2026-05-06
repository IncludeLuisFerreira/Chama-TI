package com.example.chamati.Model;

import java.io.Serializable;

public class Chamado implements Serializable {
    private int id;
    private String titulo;
    private String descricao;
    private String local;
    private int tipo; // 0=TI, 1=Infraestrutura
    private String dataCadastro;
    private String status; // "aberto", "andamento", "fechado"
    private String solucao;

    public Chamado() {}

    public Chamado(String titulo, String descricao, String local, int tipo, String dataCadastro, String status) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.local = local;
        this.tipo = tipo;
        this.dataCadastro = dataCadastro;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public int getTipo() { return tipo; }
    public void setTipo(int tipo) { this.tipo = tipo; }

    public String getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(String dataCadastro) { this.dataCadastro = dataCadastro; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSolucao() { return solucao; }
    public void setSolucao(String solucao) { this.solucao = solucao; }
}
