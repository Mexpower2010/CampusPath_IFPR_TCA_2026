package ifpr.edu.api;

import ifpr.edu.model.Administrador;
import ifpr.edu.model.Andar;
import ifpr.edu.model.Bloco;
import ifpr.edu.model.Ponto;
import ifpr.edu.model.Professor;
import ifpr.edu.model.Trecho;
import ifpr.edu.service.Rota;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JsonModelo {

    private JsonModelo() {
    }

    public static Map<String, Object> bloco(Bloco bloco) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("idBloco", bloco.getIdBloco());
        mapa.put("nomeBloco", bloco.getNomeBloco());
        return mapa;
    }

    public static List<Object> blocos(List<Bloco> lista) {
        List<Object> saida = new ArrayList<>();
        for (Bloco bloco : lista) {
            saida.add(bloco(bloco));
        }
        return saida;
    }

    public static Map<String, Object> andar(Andar andar) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("idAndar", andar.getIdAndar());
        mapa.put("nome", andar.getNome());
        mapa.put("nivel", andar.getNivel());
        mapa.put("imagemMapa", andar.getImagemMapa());
        mapa.put("idBloco", andar.getIdBloco());
        mapa.put("nomeBloco", andar.getNomeBloco());
        return mapa;
    }

    public static List<Object> andares(List<Andar> lista) {
        List<Object> saida = new ArrayList<>();
        for (Andar andar : lista) {
            saida.add(andar(andar));
        }
        return saida;
    }

    public static Map<String, Object> ponto(Ponto ponto) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("idPonto", ponto.getIdPonto());
        mapa.put("idAndar", ponto.getIdAndar());
        mapa.put("nome", ponto.getNome());
        mapa.put("tipo", ponto.getTipo() == null ? null : ponto.getTipo().name());
        mapa.put("coordenadaX", ponto.getCoordenadaX());
        mapa.put("coordenadaY", ponto.getCoordenadaY());
        mapa.put("nomeAndar", ponto.getNomeAndar());
        mapa.put("nomeBloco", ponto.getNomeBloco());
        return mapa;
    }

    public static List<Object> pontos(List<Ponto> lista) {
        List<Object> saida = new ArrayList<>();
        for (Ponto ponto : lista) {
            saida.add(ponto(ponto));
        }
        return saida;
    }

    public static Map<String, Object> trecho(Trecho trecho) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("idTrecho", trecho.getIdTrecho());
        mapa.put("idPontoA", trecho.getIdPontoA());
        mapa.put("idPontoB", trecho.getIdPontoB());
        mapa.put("distanciaMetros", trecho.getDistanciaMetros());
        mapa.put("possuiEscadas", trecho.isPossuiEscadas());
        mapa.put("usaElevador", trecho.isUsaElevador());
        mapa.put("coberto", trecho.isCoberto());
        mapa.put("interditado", trecho.isInterditado());
        mapa.put("nomePontoA", trecho.getNomePontoA());
        mapa.put("nomePontoB", trecho.getNomePontoB());
        return mapa;
    }

    public static List<Object> trechos(List<Trecho> lista) {
        List<Object> saida = new ArrayList<>();
        for (Trecho trecho : lista) {
            saida.add(trecho(trecho));
        }
        return saida;
    }

    public static Map<String, Object> professor(Professor professor) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("idProfessor", professor.getIdProfessor());
        mapa.put("nome", professor.getNome());
        mapa.put("login", professor.getLogin());
        mapa.put("fotoPerfil", professor.getFotoPerfil());
        mapa.put("idPontoAtual", professor.getIdPontoAtual());
        mapa.put("nomeSala", professor.getNomeSala());
        mapa.put("mensagem", professor.getMensagem());
        mapa.put("mensagemEnviadaEm", professor.getMensagemEnviadaEm() == null
            ? null : professor.getMensagemEnviadaEm().toString());
        return mapa;
    }

    public static List<Object> professores(List<Professor> lista) {
        List<Object> saida = new ArrayList<>();
        for (Professor professor : lista) {
            saida.add(professor(professor));
        }
        return saida;
    }

    public static Map<String, Object> administrador(Administrador administrador) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("idAdmin", administrador.getIdAdmin());
        mapa.put("nomeAdmin", administrador.getNomeAdmin());
        mapa.put("emailAdmin", administrador.getEmailAdmin());
        return mapa;
    }

    public static List<Object> administradores(List<Administrador> lista) {
        List<Object> saida = new ArrayList<>();
        for (Administrador administrador : lista) {
            saida.add(administrador(administrador));
        }
        return saida;
    }

    public static Map<String, Object> rota(Rota rota, List<Integer> trechos) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("origem", ponto(rota.getOrigem()));
        mapa.put("destino", ponto(rota.getDestino()));
        mapa.put("distanciaTotal", rota.getDistanciaTotal());
        mapa.put("passos", rota.getPassos());
        List<Object> caminho = new ArrayList<>();
        rota.getCaminho().forEach(ponto -> caminho.add(ponto.getIdPonto()));
        mapa.put("caminho", caminho);
        mapa.put("trechos", trechos);
        return mapa;
    }
}
