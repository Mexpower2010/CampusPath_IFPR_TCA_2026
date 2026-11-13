package ifpr.edu.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ifpr.edu.db.PersistenciaException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.Executors;

public final class ServidorHttp {

    private static final Map<String, String> TIPOS = Map.ofEntries(
        Map.entry("html", "text/html; charset=utf-8"),
        Map.entry("css", "text/css; charset=utf-8"),
        Map.entry("js", "text/javascript; charset=utf-8"),
        Map.entry("json", "application/json; charset=utf-8"),
        Map.entry("svg", "image/svg+xml"),
        Map.entry("png", "image/png"),
        Map.entry("jpg", "image/jpeg"),
        Map.entry("jpeg", "image/jpeg"),
        Map.entry("ico", "image/x-icon"),
        Map.entry("woff2", "font/woff2"),
        Map.entry("txt", "text/plain; charset=utf-8"));

    private final HttpServer servidor;
    private final Path raiz;
    private final Api api = new Api();

    public ServidorHttp(int porta, Path raizSite) throws IOException {
        this.raiz = raizSite.toAbsolutePath().normalize();
        this.servidor = HttpServer.create(new InetSocketAddress(porta), 0);
        this.servidor.createContext("/", this::tratar);
        this.servidor.setExecutor(Executors.newFixedThreadPool(8));
    }

    public void iniciar() {
        servidor.start();
    }

    public void parar() {
        servidor.stop(0);
    }

    public int getPorta() {
        return servidor.getAddress().getPort();
    }

    public Path getRaiz() {
        return raiz;
    }

    private void tratar(HttpExchange troca) throws IOException {
        try {
            String caminho = troca.getRequestURI().getPath();
            if (caminho.startsWith("/api")) {
                Requisicao requisicao = new Requisicao(
                    troca.getRequestMethod(),
                    caminho,
                    troca.getRequestURI().getRawQuery(),
                    new String(troca.getRequestBody().readAllBytes(), StandardCharsets.UTF_8),
                    troca.getRequestHeaders().getFirst("Authorization"));
                enviar(troca, api.tratar(requisicao));
            } else {
                estatico(troca, caminho);
            }
        } catch (ErroHttp e) {
            enviar(troca, Resposta.erro(e.getStatus(), e.getMessage()));
        } catch (PersistenciaException e) {
            enviar(troca, Resposta.erro(400, e.getMessage()));
        } catch (IllegalArgumentException e) {
            enviar(troca, Resposta.erro(400, e.getMessage()));
        } catch (RuntimeException e) {
            enviar(troca, Resposta.erro(500, "Erro interno: " + e.getMessage()));
        } finally {
            troca.close();
        }
    }

    private void estatico(HttpExchange troca, String caminho) throws IOException {
        if (!"GET".equalsIgnoreCase(troca.getRequestMethod())) {
            enviar(troca, Resposta.erro(405, "Metodo nao permitido para arquivo estatico."));
            return;
        }
        String relativo = caminho.equals("/") ? "index.html" : caminho.substring(1);
        Path arquivo = raiz.resolve(relativo).normalize();
        if (!arquivo.startsWith(raiz)) {
            enviar(troca, Resposta.erro(403, "Caminho nao permitido."));
            return;
        }
        if (Files.isDirectory(arquivo)) {
            arquivo = arquivo.resolve("index.html");
        }
        if (!Files.isRegularFile(arquivo)) {
            enviar(troca, Resposta.erro(404, "Arquivo nao encontrado: " + caminho));
            return;
        }
        byte[] dados = Files.readAllBytes(arquivo);
        troca.getResponseHeaders().set("Content-Type", tipo(arquivo));
        troca.getResponseHeaders().set("Cache-Control", "no-store");
        troca.sendResponseHeaders(200, dados.length);
        try (OutputStream saida = troca.getResponseBody()) {
            saida.write(dados);
        }
    }

    private static void enviar(HttpExchange troca, Resposta resposta) throws IOException {
        byte[] dados = resposta.getCorpo();
        troca.getResponseHeaders().set("Content-Type", resposta.getTipoConteudo());
        troca.getResponseHeaders().set("Cache-Control", "no-store");
        if (dados.length == 0) {
            troca.sendResponseHeaders(resposta.getStatus(), -1);
            return;
        }
        troca.sendResponseHeaders(resposta.getStatus(), dados.length);
        try (OutputStream saida = troca.getResponseBody()) {
            saida.write(dados);
        }
    }

    private static String tipo(Path arquivo) {
        String nome = arquivo.getFileName().toString();
        int ponto = nome.lastIndexOf('.');
        if (ponto < 0) {
            return "application/octet-stream";
        }
        return TIPOS.getOrDefault(nome.substring(ponto + 1).toLowerCase(), "application/octet-stream");
    }
}
