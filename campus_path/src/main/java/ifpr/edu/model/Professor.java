package ifpr.edu.model;

import java.time.LocalDateTime;

public class Professor {

    private Integer idProfessor;
    private String nome;
    private String login;
    private String senhaHash;
    private String fotoPerfil;
    private Integer idPontoAtual;
    private String mensagem;
    private LocalDateTime mensagemEnviadaEm;
    private String nomeSala;

    public Professor() {
    }

    public Professor(String nome, String login, String senhaHash) {
        this.nome = nome;
        this.login = login;
        this.senhaHash = senhaHash;
    }

    public Integer getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(Integer idProfessor) {
        this.idProfessor = idProfessor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public Integer getIdPontoAtual() {
        return idPontoAtual;
    }

    public void setIdPontoAtual(Integer idPontoAtual) {
        this.idPontoAtual = idPontoAtual;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getMensagemEnviadaEm() {
        return mensagemEnviadaEm;
    }

    public void setMensagemEnviadaEm(LocalDateTime mensagemEnviadaEm) {
        this.mensagemEnviadaEm = mensagemEnviadaEm;
    }

    public String getNomeSala() {
        return nomeSala;
    }

    public void setNomeSala(String nomeSala) {
        this.nomeSala = nomeSala;
    }

    @Override
    public String toString() {
        String sala = nomeSala == null ? "sem localizacao" : nomeSala;
        String texto = mensagem == null ? "sem mensagem" : mensagem;
        return idProfessor + " - " + nome + " (login: " + login + ") | " + sala + " | " + texto;
    }
}
