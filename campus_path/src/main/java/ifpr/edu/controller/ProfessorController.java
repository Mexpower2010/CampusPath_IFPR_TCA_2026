package ifpr.edu.controller;

import ifpr.edu.dao.PontoDAO;
import ifpr.edu.dao.ProfessorDAO;
import ifpr.edu.model.Ponto;
import ifpr.edu.model.Professor;
import ifpr.edu.service.AutenticacaoService;

import java.util.List;

public class ProfessorController {

    private final ProfessorDAO professores = new ProfessorDAO();
    private final PontoDAO pontos = new PontoDAO();
    private final AutenticacaoService autenticacao = new AutenticacaoService();

    public Professor autenticar(String login, String senha) {
        return autenticacao.autenticarProfessor(login, senha);
    }

    public List<Ponto> listarSalas() {
        return pontos.listarSalas();
    }

    public Professor recarregar(int idProfessor) {
        return professores.buscarPorId(idProfessor);
    }

    public void atualizarLocalizacao(int idProfessor, Integer idPonto) {
        professores.atualizarLocalizacao(idProfessor, idPonto);
    }

    public void removerPresenca(int idProfessor) {
        professores.removerPresenca(idProfessor);
    }

    public void publicarMensagem(int idProfessor, String mensagem) {
        professores.publicarMensagem(idProfessor, mensagem);
    }

    public void atualizarNome(int idProfessor, String nome) {
        professores.atualizarNome(idProfessor, nome);
    }

    public void atualizarFoto(int idProfessor, String fotoPerfil) {
        professores.atualizarFoto(idProfessor, fotoPerfil);
    }
}
