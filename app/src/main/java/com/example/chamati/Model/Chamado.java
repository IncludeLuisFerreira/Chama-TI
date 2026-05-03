package com.example.chamati.Model;

public class Chamado {
    private long id;
    private String titulo;
    private long dataCadastro;
    private String descricao;
    private String local;
    private int tipo;
    private String status;  // Mudado de Status para String
    private String solucao;

    // Construtor padrão (vazio)
    public Chamado() {
    }

    // Construtor para criar novo chamado (sem id)
    public Chamado(String titulo, String descricao, String local,
                   int tipo, long dataCadastro, String status) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.local = local;
        this.tipo = tipo;
        this.dataCadastro = dataCadastro;
        this.status = status;
    }

    // Construtor completo (com id)
    public Chamado(long id, String titulo, String descricao, String local,
                   int tipo, long dataCadastro, String status) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.local = local;
        this.tipo = tipo;
        this.dataCadastro = dataCadastro;
        this.status = status;
    }

    // Construtor completo com solução (para chamados finalizados)
    public Chamado(long id, String titulo, String descricao, String local,
                   int tipo, long dataCadastro, String status, String solucao) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.local = local;
        this.tipo = tipo;
        this.dataCadastro = dataCadastro;
        this.status = status;
        this.solucao = solucao;
    }

    // Getters e Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public long getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(long dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSolucao() {
        return solucao;
    }

    public void setSolucao(String solucao) {
        this.solucao = solucao;
    }

    // Método auxiliar para retornar o tipo como texto
    public String getTipoAsString() {
        return tipo == 0 ? "TI" : "INFRA";
    }

    // Método auxiliar para retornar o status formatado
    public String getStatusFormatado() {
        switch (status) {
            case "aberto":
                return "Aberto";
            case "andamento":
                return "Em Andamento";
            case "fechado":
                return "Fechado";
            default:
                return status;
        }
    }

    @Override
    public String toString() {
        return "Chamado{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", dataCadastro=" + dataCadastro +
                ", descricao='" + descricao + '\'' +
                ", local='" + local + '\'' +
                ", tipo=" + getTipoAsString() +
                ", status='" + status + '\'' +
                ", solucao='" + solucao + '\'' +
                '}';
    }
}