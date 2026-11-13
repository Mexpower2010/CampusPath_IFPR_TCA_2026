package ifpr.edu.api;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SessaoService {

    public record Sessao(String tipo, int id, String nome, Instant expiraEm) {
    }

    private static final Duration VALIDADE = Duration.ofHours(2);

    private final Map<String, Sessao> sessoes = new ConcurrentHashMap<>();

    public String criar(String tipo, int id, String nome) {
        String token = UUID.randomUUID().toString().replace("-", "");
        sessoes.put(token, new Sessao(tipo, id, nome, Instant.now().plus(VALIDADE)));
        return token;
    }

    public Sessao obter(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        Sessao sessao = sessoes.get(token);
        if (sessao == null) {
            return null;
        }
        if (sessao.expiraEm().isBefore(Instant.now())) {
            sessoes.remove(token);
            return null;
        }
        return sessao;
    }

    public void remover(String token) {
        if (token != null) {
            sessoes.remove(token);
        }
    }
}
