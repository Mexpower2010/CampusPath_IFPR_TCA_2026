package ifpr.edu.controller;

import ifpr.edu.dao.AdministradorDAO;
import ifpr.edu.dao.AndarDAO;
import ifpr.edu.dao.BlocoDAO;
import ifpr.edu.dao.PontoDAO;
import ifpr.edu.dao.ProfessorDAO;
import ifpr.edu.dao.SnapshotService;
import ifpr.edu.dao.TrechoDAO;
import ifpr.edu.model.Administrador;
import ifpr.edu.model.Andar;
import ifpr.edu.model.Bloco;
import ifpr.edu.model.Ponto;
import ifpr.edu.model.Professor;
import ifpr.edu.model.Trecho;
import ifpr.edu.service.AutenticacaoService;

import java.util.List;

public class AdminController {

    private final BlocoDAO blocos = new BlocoDAO();
    private final AndarDAO andares = new AndarDAO();
    private final PontoDAO pontos = new PontoDAO();
    private final TrechoDAO trechos = new TrechoDAO();
    private final ProfessorDAO professores = new ProfessorDAO();
    private final AdministradorDAO administradores = new AdministradorDAO();
    private final AutenticacaoService autenticacao = new AutenticacaoService();
    private final SnapshotService snapshot = new SnapshotService();

    public Administrador autenticar(String email, String senha) {
        return autenticacao.autenticarAdministrador(email, senha);
    }

    public List<Bloco> listarBlocos() {
        return blocos.listar();
    }

    public void salvarBloco(Bloco bloco) {
        if (bloco.getIdBloco() == null) {
            blocos.inserir(bloco);
        } else {
            blocos.atualizar(bloco);
        }
    }

    public void excluirBloco(int id) {
        blocos.excluir(id);
    }

    public List<Andar> listarAndares() {
        return andares.listar();
    }

    public void salvarAndar(Andar andar) {
        if (andar.getIdAndar() == null) {
            andares.inserir(andar);
        } else {
            andares.atualizar(andar);
        }
    }

    public void excluirAndar(int id) {
        andares.excluir(id);
    }

    public List<Ponto> listarPontos() {
        return pontos.listar();
    }

    public void salvarPonto(Ponto ponto) {
        if (ponto.getIdPonto() == null) {
            pontos.inserir(ponto);
        } else {
            pontos.atualizar(ponto);
        }
    }

    public void excluirPonto(int id) {
        pontos.excluir(id);
    }

    public List<Trecho> listarTrechos() {
        return trechos.listar();
    }

    public void salvarTrecho(Trecho trecho) {
        if (trecho.getIdTrecho() == null) {
            trechos.inserir(trecho);
        } else {
            trechos.atualizar(trecho);
        }
    }

    public void excluirTrecho(int id) {
        trechos.excluir(id);
    }

    public List<Professor> listarProfessores() {
        return professores.listar();
    }

    public void salvarProfessor(Professor professor, String senha) {
        if (professor.getIdProfessor() == null) {
            professores.inserir(professor);
        } else {
            professores.atualizar(professor);
        }
    }

    public void excluirProfessor(int id) {
        professores.excluir(id);
    }

    public List<Administrador> listarAdministradores() {
        return administradores.listar();
    }

    public void salvarAdministrador(Administrador administrador) {
        if (administrador.getIdAdmin() == null) {
            administradores.inserir(administrador);
        } else {
            administradores.atualizar(administrador);
        }
    }

    public void excluirAdministrador(int id) {
        administradores.excluir(id);
    }

    public String exportarSnapshot() {
        snapshot.exportar();
        return snapshot.getArquivo().toString();
    }
}
