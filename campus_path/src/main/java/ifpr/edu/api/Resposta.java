package ifpr.edu.api;

import java.nio.charset.StandardCharsets;

public final class Resposta {

    private final int status;
    private final String corpo;
    private final String tipoConteudo;

    private Resposta(int status, String corpo, String tipoConteudo) {
        this.status = status;
        this.corpo = corpo;
        this.tipoConteudo = tipoConteudo;
    }

    public static Resposta json(int status, Object valor) {
        return new Resposta(status, Json.escrever(valor), "application/json; charset=utf-8");
    }

    public static Resposta ok(Object valor) {
        return json(200, valor);
    }

    public static Resposta criado(Object valor) {
        return json(201, valor);
    }

    public static Resposta erro(int status, String mensagem) {
        return json(status, Json.mapa("erro", mensagem));
    }

    public static Resposta semConteudo() {
        return new Resposta(204, "", "text/plain; charset=utf-8");
    }

    public int getStatus() {
        return status;
    }

    public byte[] getCorpo() {
        return corpo.getBytes(StandardCharsets.UTF_8);
    }

    public String getTipoConteudo() {
        return tipoConteudo;
    }
}
