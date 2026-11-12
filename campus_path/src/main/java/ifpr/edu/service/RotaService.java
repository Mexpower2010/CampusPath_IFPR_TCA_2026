package ifpr.edu.service;

import ifpr.edu.dao.PontoDAO;
import ifpr.edu.dao.TrechoDAO;
import ifpr.edu.db.PersistenciaException;
import ifpr.edu.model.Ponto;
import ifpr.edu.model.Trecho;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class RotaService {

    private final PontoDAO pontos = new PontoDAO();
    private final TrechoDAO trechos = new TrechoDAO();

    public Rota calcular(int idOrigem, int idDestino, boolean evitarEscadas, boolean somenteCoberto) {
        Map<Integer, Ponto> mapaPontos = new HashMap<>();
        for (Ponto ponto : pontos.listar()) {
            mapaPontos.put(ponto.getIdPonto(), ponto);
        }
        Ponto origem = mapaPontos.get(idOrigem);
        Ponto destino = mapaPontos.get(idDestino);
        if (origem == null) {
            throw new PersistenciaException("Ponto de origem nao encontrado.");
        }
        if (destino == null) {
            throw new PersistenciaException("Ponto de destino nao encontrado.");
        }

        List<Trecho> todos = trechos.listar();
        List<Trecho> liberados = filtrar(todos, evitarEscadas, somenteCoberto);

        Map<Integer, List<Trecho>> grafo = montarGrafo(liberados);
        Map<Integer, Double> distancias = new HashMap<>();
        Map<Integer, Integer> anteriores = new HashMap<>();
        Set<Integer> visitados = new HashSet<>();

        for (Integer id : mapaPontos.keySet()) {
            distancias.put(id, Double.MAX_VALUE);
        }
        distancias.put(idOrigem, 0.0);

        PriorityQueue<Integer> fila = new PriorityQueue<>(Comparator.comparingDouble(distancias::get));
        fila.add(idOrigem);

        while (!fila.isEmpty()) {
            Integer atual = fila.poll();
            if (!visitados.add(atual)) {
                continue;
            }
            if (atual == idDestino) {
                break;
            }
            for (Trecho trecho : grafo.getOrDefault(atual, List.of())) {
                int vizinho = trecho.getIdPontoA() == atual ? trecho.getIdPontoB() : trecho.getIdPontoA();
                if (visitados.contains(vizinho)) {
                    continue;
                }
                double nova = distancias.get(atual) + trecho.getDistanciaMetros();
                if (nova < distancias.get(vizinho)) {
                    distancias.put(vizinho, nova);
                    anteriores.put(vizinho, atual);
                    fila.add(vizinho);
                }
            }
        }

        if (distancias.get(idDestino) == Double.MAX_VALUE) {
            throw new PersistenciaException(motivoSemRota(origem, destino, todos, liberados,
                evitarEscadas, somenteCoberto));
        }

        LinkedList<Integer> ordem = new LinkedList<>();
        Integer passo = idDestino;
        while (passo != null) {
            ordem.addFirst(passo);
            passo = anteriores.get(passo);
        }

        List<Ponto> caminho = new ArrayList<>();
        for (Integer id : ordem) {
            caminho.add(mapaPontos.get(id));
        }

        Map<String, Trecho> usados = new HashMap<>();
        for (Trecho trecho : liberados) {
            usados.put(chave(trecho.getIdPontoA(), trecho.getIdPontoB()), trecho);
        }

        List<String> passos = new ArrayList<>();
        for (int i = 0; i < caminho.size(); i++) {
            Ponto ponto = caminho.get(i);
            if (i == 0) {
                passos.add("Partida: " + ponto.getNome() + " (" + local(ponto) + ")");
                continue;
            }
            Ponto anterior = caminho.get(i - 1);
            Trecho trecho = usados.get(chave(anterior.getIdPonto(), ponto.getIdPonto()));
            String distancia = trecho == null ? "" : " - " + formatar(trecho.getDistanciaMetros()) + " m";
            passos.add("  -> " + ponto.getNome() + " (" + local(ponto) + ")" + distancia);
            if (!anterior.getIdAndar().equals(ponto.getIdAndar())) {
                passos.add("     Troca de andar: " + anterior.getNomeAndar() + " para " + ponto.getNomeAndar()
                    + descreverAcesso(trecho));
            }
            if (!anterior.getNomeBloco().equals(ponto.getNomeBloco())) {
                passos.add("     Troca de bloco: " + anterior.getNomeBloco() + " para " + ponto.getNomeBloco());
            }
        }

        return new Rota(origem, destino, caminho, distancias.get(idDestino), passos);
    }

    private List<Trecho> filtrar(List<Trecho> todos, boolean evitarEscadas, boolean somenteCoberto) {
        List<Trecho> liberados = new ArrayList<>();
        for (Trecho trecho : todos) {
            if (trecho.isInterditado()) {
                continue;
            }
            if (evitarEscadas && trecho.isPossuiEscadas()) {
                continue;
            }
            if (somenteCoberto && !trecho.isCoberto()) {
                continue;
            }
            liberados.add(trecho);
        }
        return liberados;
    }

    private Map<Integer, List<Trecho>> montarGrafo(List<Trecho> lista) {
        Map<Integer, List<Trecho>> grafo = new HashMap<>();
        for (Trecho trecho : lista) {
            grafo.computeIfAbsent(trecho.getIdPontoA(), k -> new ArrayList<>()).add(trecho);
            grafo.computeIfAbsent(trecho.getIdPontoB(), k -> new ArrayList<>()).add(trecho);
        }
        return grafo;
    }

    private String motivoSemRota(Ponto origem, Ponto destino, List<Trecho> todos, List<Trecho> liberados,
                                 boolean evitarEscadas, boolean somenteCoberto) {
        StringBuilder motivo = new StringBuilder();
        motivo.append("Nao existe rota entre ").append(origem.getNome())
              .append(" e ").append(destino.getNome()).append(" com as opcoes escolhidas.");

        long interditados = todos.stream().filter(Trecho::isInterditado).count();
        long comEscada = todos.stream().filter(t -> !t.isInterditado() && t.isPossuiEscadas()).count();
        long descobertos = todos.stream().filter(t -> !t.isInterditado() && !t.isCoberto()).count();

        motivo.append(" Trechos interditados: ").append(interditados).append(".");
        if (evitarEscadas) {
            motivo.append(" Trechos com escadas excluidos: ").append(comEscada).append(".");
        }
        if (somenteCoberto) {
            motivo.append(" Trechos descobertos excluidos: ").append(descobertos).append(".");
        }
        motivo.append(" Trechos disponiveis no calculo: ").append(liberados.size()).append(".");

        if (liberados.isEmpty()) {
            motivo.append(" Nenhum trecho restou apos os filtros.");
        } else if (alcanca(origem.getIdPonto(), destino.getIdPonto(), todos, false, false)) {
            motivo.append(" Sem os filtros existe caminho; desative a opcao que bloqueia o percurso.");
        } else {
            motivo.append(" O mapa nao possui ligacao cadastrada entre esses pontos.");
        }
        return motivo.toString();
    }

    private boolean alcanca(int idOrigem, int idDestino, List<Trecho> todos,
                            boolean evitarEscadas, boolean somenteCoberto) {
        Map<Integer, List<Trecho>> grafo = montarGrafo(filtrar(todos, evitarEscadas, somenteCoberto));
        Set<Integer> visitados = new HashSet<>();
        LinkedList<Integer> fila = new LinkedList<>();
        fila.add(idOrigem);
        while (!fila.isEmpty()) {
            Integer atual = fila.poll();
            if (!visitados.add(atual)) {
                continue;
            }
            if (atual == idDestino) {
                return true;
            }
            for (Trecho trecho : grafo.getOrDefault(atual, List.of())) {
                int vizinho = trecho.getIdPontoA() == atual ? trecho.getIdPontoB() : trecho.getIdPontoA();
                if (!visitados.contains(vizinho)) {
                    fila.add(vizinho);
                }
            }
        }
        return false;
    }

    private static String descreverAcesso(Trecho trecho) {
        if (trecho == null) {
            return "";
        }
        if (trecho.isUsaElevador()) {
            return " (elevador)";
        }
        if (trecho.isPossuiEscadas()) {
            return " (escada)";
        }
        return "";
    }

    private static String local(Ponto ponto) {
        return ponto.getNomeBloco() + " / " + ponto.getNomeAndar();
    }

    private static String chave(int a, int b) {
        return Math.min(a, b) + "-" + Math.max(a, b);
    }

    private static String formatar(double valor) {
        return String.format("%.1f", valor).replace('.', ',');
    }
}
