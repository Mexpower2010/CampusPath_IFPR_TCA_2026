package ifpr.edu.api;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Requisicao {

    private final String metodo;
    private final String caminho;
    private final Map<String, String> parametros;
    private final String corpo;
    private final String token;

    public Requisicao(String metodo, String caminho, String consulta, String corpo, String autorizacao) {
        this.metodo = metodo == null ? "GET" : metodo.toUpperCase();
        this.caminho = caminho == null ? "/" : caminho;
        this.parametros = separar(consulta);
        this.corpo = corpo == null ? "" : corpo;
        this.token = extrairToken(autorizacao);
    }

    public String getMetodo() {
        return metodo;
    }

    public String getCaminho() {
        return caminho;
    }

    public String getCorpo() {
        return corpo;
    }

    public String getToken() {
        return token;
    }

    public String parametro(String nome) {
        return parametros.get(nome);
    }

    public Integer parametroInteiro(String nome) {
        String valor = parametros.get(nome);
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            throw new ErroHttp(400, "Parametro " + nome + " deve ser um numero inteiro.");
        }
    }

    public boolean parametroBooleano(String nome) {
        String valor = parametros.get(nome);
        if (valor == null || valor.isBlank()) {
            return false;
        }
        return valor.equalsIgnoreCase("true") || valor.equalsIgnoreCase("1") || valor.equalsIgnoreCase("s");
    }

    public Map<String, Object> corpoJson() {
        return Json.objeto(Json.ler(corpo));
    }

    private static Map<String, String> separar(String consulta) {
        Map<String, String> mapa = new LinkedHashMap<>();
        if (consulta == null || consulta.isBlank()) {
            return mapa;
        }
        for (String par : consulta.split("&")) {
            if (par.isBlank()) {
                continue;
            }
            int igual = par.indexOf('=');
            String chave = igual < 0 ? par : par.substring(0, igual);
            String valor = igual < 0 ? "" : par.substring(igual + 1);
            mapa.put(decodificar(chave), decodificar(valor));
        }
        return mapa;
    }

    private static String decodificar(String valor) {
        try {
            return URLDecoder.decode(valor, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new ErroHttp(400, "Parametro malformado na URL.");
        }
    }

    private static String extrairToken(String autorizacao) {
        if (autorizacao == null || autorizacao.isBlank()) {
            return null;
        }
        String valor = autorizacao.trim();
        if (valor.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return valor.substring(7).trim();
        }
        return valor;
    }
}
