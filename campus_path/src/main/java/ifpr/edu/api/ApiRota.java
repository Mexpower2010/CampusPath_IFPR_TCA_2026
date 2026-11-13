package ifpr.edu.api;

import ifpr.edu.model.Ponto;
import ifpr.edu.model.Trecho;
import ifpr.edu.service.Rota;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ApiRota {

    private ApiRota() {
    }

    public static Resposta mapa(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Integer idBloco = requisicao.parametroInteiro("bloco");
        List<Object> andares = idBloco == null
            ? JsonModelo.andares(contexto.andares.listar())
            : JsonModelo.andares(contexto.andares.listarPorBloco(idBloco));

        List<Object> pontos = new ArrayList<>();
        Set<Integer> andaresDoBloco = idBloco == null ? null : andaresDoBloco(contexto, idBloco);
        for (Object item : JsonModelo.pontos(contexto.pontos.listar())) {
            @SuppressWarnings("unchecked")
            Map<String, Object> ponto = (Map<String, Object>) item;
            if (andaresDoBloco == null || andaresDoBloco.contains(ponto.get("idAndar"))) {
                pontos.add(ponto);
            }
        }

        return Resposta.ok(Json.mapa(
            "blocos", JsonModelo.blocos(contexto.blocos.listar()),
            "andares", andares,
            "pontos", pontos,
            "trechos", JsonModelo.trechos(contexto.trechos.listar()),
            "professores", JsonModelo.professores(contexto.professores.listarPresentes())));
    }

    public static Resposta calcular(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Integer origem = requisicao.parametroInteiro("origem");
        Integer destino = requisicao.parametroInteiro("destino");
        if (origem == null || destino == null) {
            throw new ErroHttp(400, "Informe os parametros origem e destino.");
        }
        boolean evitarEscadas = requisicao.parametroBooleano("evitarEscadas");
        boolean somenteCoberto = requisicao.parametroBooleano("somenteCoberto");

        Rota rota = contexto.rotas.calcular(origem, destino, evitarEscadas, somenteCoberto);
        return Resposta.ok(JsonModelo.rota(rota, trechosDoCaminho(contexto, rota)));
    }

    public static Resposta exportarSnapshot(Contexto contexto, Requisicao requisicao, Map<String, String> parametros) {
        Autorizacao.exigirAdmin(contexto, requisicao);
        contexto.snapshot.exportar();
        return Resposta.ok(Json.mapa(
            "mensagem", "Estado exportado.",
            "arquivo", contexto.snapshot.getArquivo().toAbsolutePath().toString()));
    }

    private static Set<Integer> andaresDoBloco(Contexto contexto, int idBloco) {
        Set<Integer> ids = new HashSet<>();
        for (var andar : contexto.andares.listarPorBloco(idBloco)) {
            ids.add(andar.getIdAndar());
        }
        return ids;
    }

    private static List<Integer> trechosDoCaminho(Contexto contexto, Rota rota) {
        Map<String, Integer> porPar = new HashMap<>();
        for (Trecho trecho : contexto.trechos.listar()) {
            porPar.put(chave(trecho.getIdPontoA(), trecho.getIdPontoB()), trecho.getIdTrecho());
        }
        List<Integer> ids = new ArrayList<>();
        List<Ponto> caminho = rota.getCaminho();
        for (int i = 1; i < caminho.size(); i++) {
            Integer id = porPar.get(chave(caminho.get(i - 1).getIdPonto(), caminho.get(i).getIdPonto()));
            if (id != null) {
                ids.add(id);
            }
        }
        return ids;
    }

    private static String chave(int a, int b) {
        return Math.min(a, b) + "-" + Math.max(a, b);
    }
}
