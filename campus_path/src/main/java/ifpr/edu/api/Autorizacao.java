package ifpr.edu.api;

public final class Autorizacao {

    private Autorizacao() {
    }

    public static SessaoService.Sessao exigirSessao(Contexto contexto, Requisicao requisicao) {
        SessaoService.Sessao sessao = contexto.sessoes.obter(requisicao.getToken());
        if (sessao == null) {
            throw new ErroHttp(401, "Sessao expirada ou inexistente. Faca login novamente.");
        }
        return sessao;
    }

    public static SessaoService.Sessao exigirAdmin(Contexto contexto, Requisicao requisicao) {
        SessaoService.Sessao sessao = exigirSessao(contexto, requisicao);
        if (!"ADMIN".equals(sessao.tipo())) {
            throw new ErroHttp(403, "Acesso restrito ao administrador.");
        }
        return sessao;
    }

    public static SessaoService.Sessao exigirProfessor(Contexto contexto, Requisicao requisicao, int idProfessor) {
        SessaoService.Sessao sessao = exigirSessao(contexto, requisicao);
        if (!"PROFESSOR".equals(sessao.tipo())) {
            throw new ErroHttp(403, "Acesso restrito ao professor.");
        }
        if (sessao.id() != idProfessor) {
            throw new ErroHttp(403, "Um professor so pode alterar o proprio perfil.");
        }
        return sessao;
    }
}
