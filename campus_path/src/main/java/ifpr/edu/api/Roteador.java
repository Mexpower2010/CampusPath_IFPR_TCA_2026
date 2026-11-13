package ifpr.edu.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Roteador {

    public interface Tratador {
        Resposta tratar(Requisicao requisicao, Map<String, String> parametros);
    }

    private record Rota(String metodo, String[] partes, Tratador tratador) {
    }

    private final List<Rota> rotas = new ArrayList<>();

    public void registrar(String metodo, String padrao, Tratador tratador) {
        rotas.add(new Rota(metodo.toUpperCase(), dividir(padrao), tratador));
    }

    public Resposta despachar(Requisicao requisicao) {
        String[] partes = dividir(requisicao.getCaminho());
        boolean caminhoExiste = false;
        for (Rota rota : rotas) {
            Map<String, String> parametros = casar(rota.partes(), partes);
            if (parametros == null) {
                continue;
            }
            caminhoExiste = true;
            if (rota.metodo().equals(requisicao.getMetodo())) {
                return rota.tratador().tratar(requisicao, parametros);
            }
        }
        if (caminhoExiste) {
            throw new ErroHttp(405, "Metodo " + requisicao.getMetodo() + " nao permitido neste recurso.");
        }
        throw new ErroHttp(404, "Recurso nao encontrado: " + requisicao.getCaminho());
    }

    private static Map<String, String> casar(String[] padrao, String[] caminho) {
        if (padrao.length != caminho.length) {
            return null;
        }
        for (int i = 0; i < padrao.length; i++) {
            String trecho = padrao[i];
            boolean variavel = trecho.startsWith("{") && trecho.endsWith("}");
            if (!variavel && !trecho.equals(caminho[i])) {
                return null;
            }
        }
        Map<String, String> parametros = new LinkedHashMap<>();
        for (int i = 0; i < padrao.length; i++) {
            String trecho = padrao[i];
            if (trecho.startsWith("{") && trecho.endsWith("}")) {
                parametros.put(trecho.substring(1, trecho.length() - 1), caminho[i]);
            }
        }
        return parametros;
    }

    private static String[] dividir(String caminho) {
        String limpo = caminho == null ? "" : caminho.trim();
        while (limpo.startsWith("/")) {
            limpo = limpo.substring(1);
        }
        while (limpo.endsWith("/")) {
            limpo = limpo.substring(0, limpo.length() - 1);
        }
        return limpo.isEmpty() ? new String[0] : limpo.split("/");
    }
}
