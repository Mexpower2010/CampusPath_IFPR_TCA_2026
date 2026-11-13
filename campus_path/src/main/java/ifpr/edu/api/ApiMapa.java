package ifpr.edu.api;

import ifpr.edu.model.Andar;
import ifpr.edu.model.Bloco;
import ifpr.edu.model.Ponto;
import ifpr.edu.model.TipoPonto;
import ifpr.edu.model.Trecho;

import java.util.Map;

public final class ApiMapa {

    private ApiMapa() {
    }

    public static Resposta listarBlocos(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        return Resposta.ok(JsonModelo.blocos(contexto.blocos.listar()));
    }

    public static Resposta criarBloco(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        Map<String, Object> corpo = requisicao.corpoJson();
        Bloco bloco = new Bloco(Json.textoObrigatorio(corpo, "nomeBloco"));
        contexto.blocos.inserir(bloco);
        return Resposta.criado(Json.mapa("mensagem", "Bloco cadastrado."));
    }

    public static Resposta alterarBloco(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        int id = inteiro(parametros, "id");
        Bloco bloco = contexto.blocos.buscarPorId(id);
        if (bloco == null) {
            throw new ErroHttp(404, "Bloco nao encontrado.");
        }
        Map<String, Object> corpo = requisicao.corpoJson();
        bloco.setNomeBloco(Json.textoObrigatorio(corpo, "nomeBloco"));
        contexto.blocos.atualizar(bloco);
        return Resposta.ok(Json.mapa("mensagem", "Bloco alterado."));
    }

    public static Resposta excluirBloco(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        contexto.blocos.excluir(inteiro(parametros, "id"));
        return Resposta.ok(Json.mapa("mensagem", "Bloco excluido."));
    }

    public static Resposta listarAndares(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Integer idBloco = requisicao.parametroInteiro("bloco");
        if (idBloco == null) {
            return Resposta.ok(JsonModelo.andares(contexto.andares.listar()));
        }
        return Resposta.ok(JsonModelo.andares(contexto.andares.listarPorBloco(idBloco)));
    }

    public static Resposta criarAndar(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        Map<String, Object> corpo = requisicao.corpoJson();
        Andar andar = new Andar(
            Json.textoObrigatorio(corpo, "nome"),
            Json.inteiroObrigatorio(corpo, "nivel"),
            Json.texto(corpo, "imagemMapa"),
            Json.inteiroObrigatorio(corpo, "idBloco"));
        contexto.andares.inserir(andar);
        return Resposta.criado(Json.mapa("mensagem", "Andar cadastrado."));
    }

    public static Resposta alterarAndar(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        int id = inteiro(parametros, "id");
        Andar andar = contexto.andares.buscarPorId(id);
        if (andar == null) {
            throw new ErroHttp(404, "Andar nao encontrado.");
        }
        Map<String, Object> corpo = requisicao.corpoJson();
        andar.setNome(Json.textoObrigatorio(corpo, "nome"));
        andar.setNivel(Json.inteiroObrigatorio(corpo, "nivel"));
        andar.setImagemMapa(Json.texto(corpo, "imagemMapa"));
        andar.setIdBloco(Json.inteiroObrigatorio(corpo, "idBloco"));
        contexto.andares.atualizar(andar);
        return Resposta.ok(Json.mapa("mensagem", "Andar alterado."));
    }

    public static Resposta excluirAndar(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        contexto.andares.excluir(inteiro(parametros, "id"));
        return Resposta.ok(Json.mapa("mensagem", "Andar excluido."));
    }

    public static Resposta listarPontos(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Integer idAndar = requisicao.parametroInteiro("andar");
        String tipo = requisicao.parametro("tipo");
        if (idAndar != null) {
            return Resposta.ok(JsonModelo.pontos(contexto.pontos.listarPorAndar(idAndar)));
        }
        if (tipo != null && tipo.equalsIgnoreCase("SALA")) {
            return Resposta.ok(JsonModelo.pontos(contexto.pontos.listarSalas()));
        }
        return Resposta.ok(JsonModelo.pontos(contexto.pontos.listar()));
    }

    public static Resposta criarPonto(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        Map<String, Object> corpo = requisicao.corpoJson();
        Ponto ponto = new Ponto(
            Json.inteiroObrigatorio(corpo, "idAndar"),
            Json.textoObrigatorio(corpo, "nome"),
            TipoPonto.de(Json.textoObrigatorio(corpo, "tipo")),
            Json.decimal(corpo, "coordenadaX"),
            Json.decimal(corpo, "coordenadaY"));
        contexto.pontos.inserir(ponto);
        return Resposta.criado(Json.mapa("mensagem", "Ponto cadastrado."));
    }

    public static Resposta alterarPonto(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        int id = inteiro(parametros, "id");
        Ponto ponto = contexto.pontos.buscarPorId(id);
        if (ponto == null) {
            throw new ErroHttp(404, "Ponto nao encontrado.");
        }
        Map<String, Object> corpo = requisicao.corpoJson();
        ponto.setIdAndar(Json.inteiroObrigatorio(corpo, "idAndar"));
        ponto.setNome(Json.textoObrigatorio(corpo, "nome"));
        ponto.setTipo(TipoPonto.de(Json.textoObrigatorio(corpo, "tipo")));
        ponto.setCoordenadaX(Json.decimal(corpo, "coordenadaX"));
        ponto.setCoordenadaY(Json.decimal(corpo, "coordenadaY"));
        contexto.pontos.atualizar(ponto);
        return Resposta.ok(Json.mapa("mensagem", "Ponto alterado."));
    }

    public static Resposta excluirPonto(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        contexto.pontos.excluir(inteiro(parametros, "id"));
        return Resposta.ok(Json.mapa("mensagem", "Ponto excluido."));
    }

    public static Resposta listarTrechos(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        return Resposta.ok(JsonModelo.trechos(contexto.trechos.listar()));
    }

    public static Resposta criarTrecho(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        Map<String, Object> corpo = requisicao.corpoJson();
        Trecho trecho = new Trecho(
            Json.inteiroObrigatorio(corpo, "idPontoA"),
            Json.inteiroObrigatorio(corpo, "idPontoB"),
            Json.decimalObrigatorio(corpo, "distanciaMetros"),
            Json.booleano(corpo, "possuiEscadas", false),
            Json.booleano(corpo, "usaElevador", false),
            Json.booleano(corpo, "coberto", false),
            Json.booleano(corpo, "interditado", false));
        contexto.trechos.inserir(trecho);
        return Resposta.criado(Json.mapa("mensagem", "Trecho cadastrado."));
    }

    public static Resposta alterarTrecho(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        int id = inteiro(parametros, "id");
        Trecho trecho = contexto.trechos.buscarPorId(id);
        if (trecho == null) {
            throw new ErroHttp(404, "Trecho nao encontrado.");
        }
        Map<String, Object> corpo = requisicao.corpoJson();
        trecho.setIdPontoA(Json.inteiroObrigatorio(corpo, "idPontoA"));
        trecho.setIdPontoB(Json.inteiroObrigatorio(corpo, "idPontoB"));
        trecho.setDistanciaMetros(Json.decimalObrigatorio(corpo, "distanciaMetros"));
        trecho.setPossuiEscadas(Json.booleano(corpo, "possuiEscadas", false));
        trecho.setUsaElevador(Json.booleano(corpo, "usaElevador", false));
        trecho.setCoberto(Json.booleano(corpo, "coberto", false));
        trecho.setInterditado(Json.booleano(corpo, "interditado", false));
        contexto.trechos.atualizar(trecho);
        return Resposta.ok(Json.mapa("mensagem", "Trecho alterado."));
    }

    public static Resposta alterarCondicoesTrecho(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        int id = inteiro(parametros, "id");
        Trecho trecho = contexto.trechos.buscarPorId(id);
        if (trecho == null) {
            throw new ErroHttp(404, "Trecho nao encontrado.");
        }
        Map<String, Object> corpo = requisicao.corpoJson();
        boolean coberto = Json.booleano(corpo, "coberto", trecho.isCoberto());
        boolean interditado = Json.booleano(corpo, "interditado", trecho.isInterditado());
        contexto.trechos.atualizarCondicoes(id, coberto, interditado);
        return Resposta.ok(Json.mapa("mensagem", "Condicoes do trecho atualizadas."));
    }

    public static Resposta excluirTrecho(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        contexto.trechos.excluir(inteiro(parametros, "id"));
        return Resposta.ok(Json.mapa("mensagem", "Trecho excluido."));
    }

    static int inteiro(Map<String, String> parametros, String nome) {
        String valor = parametros.get(nome);
        if (valor == null) {
            throw new ErroHttp(400, "Parametro ausente: " + nome + ".");
        }
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            throw new ErroHttp(400, "Parametro " + nome + " deve ser um numero inteiro.");
        }
    }
}
