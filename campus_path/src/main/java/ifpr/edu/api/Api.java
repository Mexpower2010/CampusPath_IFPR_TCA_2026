package ifpr.edu.api;

public final class Api {

    private final Contexto contexto = new Contexto();
    private final Roteador roteador = new Roteador();

    public Api() {
        registrarRotas();
    }

    public Resposta tratar(Requisicao requisicao) {
        return roteador.despachar(requisicao);
    }

    private void registrarRotas() {
        roteador.registrar("GET", "/api/saude", (req, par) ->
            Resposta.ok(Json.mapa("status", "ok")));

        roteador.registrar("POST", "/api/auth/professor", (req, par) ->
            ApiPessoas.loginProfessor(contexto, req, par));
        roteador.registrar("POST", "/api/auth/admin", (req, par) ->
            ApiPessoas.loginAdmin(contexto, req, par));
        roteador.registrar("GET", "/api/auth/sessao", (req, par) ->
            ApiPessoas.sessao(contexto, req, par));
        roteador.registrar("POST", "/api/auth/sair", (req, par) ->
            ApiPessoas.sair(contexto, req, par));

        roteador.registrar("GET", "/api/mapa", (req, par) ->
            ApiRota.mapa(contexto, req, par));
        roteador.registrar("GET", "/api/rotas", (req, par) ->
            ApiRota.calcular(contexto, req, par));
        roteador.registrar("POST", "/api/snapshot", (req, par) ->
            ApiRota.exportarSnapshot(contexto, req, par));

        roteador.registrar("GET", "/api/blocos", (req, par) ->
            ApiMapa.listarBlocos(contexto, req, par));
        roteador.registrar("POST", "/api/blocos", (req, par) ->
            ApiMapa.criarBloco(contexto, req, par));
        roteador.registrar("PUT", "/api/blocos/{id}", (req, par) ->
            ApiMapa.alterarBloco(contexto, req, par));
        roteador.registrar("DELETE", "/api/blocos/{id}", (req, par) ->
            ApiMapa.excluirBloco(contexto, req, par));

        roteador.registrar("GET", "/api/andares", (req, par) ->
            ApiMapa.listarAndares(contexto, req, par));
        roteador.registrar("POST", "/api/andares", (req, par) ->
            ApiMapa.criarAndar(contexto, req, par));
        roteador.registrar("PUT", "/api/andares/{id}", (req, par) ->
            ApiMapa.alterarAndar(contexto, req, par));
        roteador.registrar("DELETE", "/api/andares/{id}", (req, par) ->
            ApiMapa.excluirAndar(contexto, req, par));

        roteador.registrar("GET", "/api/pontos", (req, par) ->
            ApiMapa.listarPontos(contexto, req, par));
        roteador.registrar("POST", "/api/pontos", (req, par) ->
            ApiMapa.criarPonto(contexto, req, par));
        roteador.registrar("PUT", "/api/pontos/{id}", (req, par) ->
            ApiMapa.alterarPonto(contexto, req, par));
        roteador.registrar("DELETE", "/api/pontos/{id}", (req, par) ->
            ApiMapa.excluirPonto(contexto, req, par));

        roteador.registrar("GET", "/api/trechos", (req, par) ->
            ApiMapa.listarTrechos(contexto, req, par));
        roteador.registrar("POST", "/api/trechos", (req, par) ->
            ApiMapa.criarTrecho(contexto, req, par));
        roteador.registrar("PUT", "/api/trechos/{id}", (req, par) ->
            ApiMapa.alterarTrecho(contexto, req, par));
        roteador.registrar("PATCH", "/api/trechos/{id}/condicoes", (req, par) ->
            ApiMapa.alterarCondicoesTrecho(contexto, req, par));
        roteador.registrar("DELETE", "/api/trechos/{id}", (req, par) ->
            ApiMapa.excluirTrecho(contexto, req, par));

        roteador.registrar("GET", "/api/professores", (req, par) ->
            ApiPessoas.listarProfessores(contexto, req, par));
        roteador.registrar("POST", "/api/professores", (req, par) ->
            ApiPessoas.criarProfessor(contexto, req, par));
        roteador.registrar("PUT", "/api/professores/{id}", (req, par) ->
            ApiPessoas.alterarProfessor(contexto, req, par));
        roteador.registrar("DELETE", "/api/professores/{id}", (req, par) ->
            ApiPessoas.excluirProfessor(contexto, req, par));
        roteador.registrar("PUT", "/api/professores/{id}/nome", (req, par) ->
            ApiPessoas.alterarNome(contexto, req, par));
        roteador.registrar("PUT", "/api/professores/{id}/foto", (req, par) ->
            ApiPessoas.alterarFoto(contexto, req, par));
        roteador.registrar("PUT", "/api/professores/{id}/localizacao", (req, par) ->
            ApiPessoas.alterarLocalizacao(contexto, req, par));
        roteador.registrar("PUT", "/api/professores/{id}/mensagem", (req, par) ->
            ApiPessoas.publicarMensagem(contexto, req, par));
        roteador.registrar("DELETE", "/api/professores/{id}/presenca", (req, par) ->
            ApiPessoas.removerPresenca(contexto, req, par));

        roteador.registrar("GET", "/api/administradores", (req, par) ->
            ApiPessoas.listarAdministradores(contexto, req, par));
        roteador.registrar("POST", "/api/administradores", (req, par) ->
            ApiPessoas.criarAdministrador(contexto, req, par));
        roteador.registrar("PUT", "/api/administradores/{id}", (req, par) ->
            ApiPessoas.alterarAdministrador(contexto, req, par));
        roteador.registrar("DELETE", "/api/administradores/{id}", (req, par) ->
            ApiPessoas.excluirAdministrador(contexto, req, par));
    }
}
