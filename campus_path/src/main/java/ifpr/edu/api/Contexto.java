package ifpr.edu.api;

import ifpr.edu.dao.AdministradorDAO;
import ifpr.edu.dao.AndarDAO;
import ifpr.edu.dao.BlocoDAO;
import ifpr.edu.dao.PontoDAO;
import ifpr.edu.dao.ProfessorDAO;
import ifpr.edu.dao.SnapshotService;
import ifpr.edu.dao.TrechoDAO;
import ifpr.edu.service.AutenticacaoService;
import ifpr.edu.service.RotaService;

public final class Contexto {

    public final BlocoDAO blocos = new BlocoDAO();
    public final AndarDAO andares = new AndarDAO();
    public final PontoDAO pontos = new PontoDAO();
    public final TrechoDAO trechos = new TrechoDAO();
    public final ProfessorDAO professores = new ProfessorDAO();
    public final AdministradorDAO administradores = new AdministradorDAO();
    public final RotaService rotas = new RotaService();
    public final AutenticacaoService autenticacao = new AutenticacaoService();
    public final SnapshotService snapshot = new SnapshotService();
    public final SessaoService sessoes = new SessaoService();
}
