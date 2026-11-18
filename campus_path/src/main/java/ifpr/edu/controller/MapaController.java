package ifpr.edu.controller;

import ifpr.edu.dao.AndarDAO;
import ifpr.edu.dao.BlocoDAO;
import ifpr.edu.dao.PontoDAO;
import ifpr.edu.dao.ProfessorDAO;
import ifpr.edu.dao.TrechoDAO;
import ifpr.edu.model.Andar;
import ifpr.edu.model.Bloco;
import ifpr.edu.model.Ponto;
import ifpr.edu.model.Professor;
import ifpr.edu.model.Trecho;
import ifpr.edu.service.Rota;
import ifpr.edu.service.RotaService;

import java.util.List;

public class MapaController {

    private final BlocoDAO blocos = new BlocoDAO();
    private final AndarDAO andares = new AndarDAO();
    private final PontoDAO pontos = new PontoDAO();
    private final TrechoDAO trechos = new TrechoDAO();
    private final ProfessorDAO professores = new ProfessorDAO();
    private final RotaService rotas = new RotaService();

    public List<Bloco> listarBlocos() {
        return blocos.listar();
    }

    public List<Andar> listarAndares(int idBloco) {
        return andares.listarPorBloco(idBloco);
    }

    public List<Ponto> listarPontos(int idAndar) {
        return pontos.listarPorAndar(idAndar);
    }

    public List<Trecho> listarTrechos() {
        return trechos.listar();
    }

    public List<Professor> listarProfessoresPresentes() {
        return professores.listarPresentes();
    }

    public Rota calcularRota(int idOrigem, int idDestino, boolean evitarEscadas, boolean somenteCoberto) {
        return rotas.calcular(idOrigem, idDestino, evitarEscadas, somenteCoberto);
    }
}
