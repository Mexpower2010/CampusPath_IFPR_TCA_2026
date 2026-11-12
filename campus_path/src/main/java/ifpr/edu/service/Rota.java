package ifpr.edu.service;

import ifpr.edu.model.Ponto;

import java.util.Collections;
import java.util.List;

public class Rota {

    private final Ponto origem;
    private final Ponto destino;
    private final List<Ponto> caminho;
    private final double distanciaTotal;
    private final List<String> passos;

    public Rota(Ponto origem, Ponto destino, List<Ponto> caminho, double distanciaTotal, List<String> passos) {
        this.origem = origem;
        this.destino = destino;
        this.caminho = Collections.unmodifiableList(caminho);
        this.distanciaTotal = distanciaTotal;
        this.passos = Collections.unmodifiableList(passos);
    }

    public Ponto getOrigem() {
        return origem;
    }

    public Ponto getDestino() {
        return destino;
    }

    public List<Ponto> getCaminho() {
        return caminho;
    }

    public double getDistanciaTotal() {
        return distanciaTotal;
    }

    public List<String> getPassos() {
        return passos;
    }
}
