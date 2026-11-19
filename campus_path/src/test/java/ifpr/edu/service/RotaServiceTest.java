package ifpr.edu.service;

import ifpr.edu.db.PersistenciaException;
import ifpr.edu.model.Ponto;
import ifpr.edu.model.TipoPonto;
import ifpr.edu.model.Trecho;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RotaServiceTest {

    private static final int TERREO_X = 1;
    private static final int ANDAR1_X = 2;
    private static final int TERREO_Y = 3;

    private final List<Ponto> pontos = new ArrayList<>();
    private final List<Trecho> trechos = new ArrayList<>();

    private final Trecho escada;
    private final Trecho elevador;
    private final Trecho entradaXY;

    RotaServiceTest() {
        ponto(1, TERREO_X, "Terreo", "Bloco X", "salaX_terreo", TipoPonto.SALA);
        ponto(2, TERREO_X, "Terreo", "Bloco X", "corredorX", TipoPonto.PASSAGEM);
        ponto(3, TERREO_X, "Terreo", "Bloco X", "escadaX_terreo", TipoPonto.PASSAGEM);
        ponto(4, ANDAR1_X, "1o andar", "Bloco X", "escadaX_1o", TipoPonto.PASSAGEM);
        ponto(5, ANDAR1_X, "1o andar", "Bloco X", "salaX_1o", TipoPonto.SALA);
        ponto(6, TERREO_X, "Terreo", "Bloco X", "elevadorX_terreo", TipoPonto.PASSAGEM);
        ponto(7, ANDAR1_X, "1o andar", "Bloco X", "elevadorX_1o", TipoPonto.PASSAGEM);
        ponto(8, TERREO_X, "Terreo", "Bloco X", "entradaX", TipoPonto.ACESSO);
        ponto(9, TERREO_Y, "Terreo", "Bloco Y", "entradaY", TipoPonto.ACESSO);
        ponto(10, TERREO_Y, "Terreo", "Bloco Y", "salaY", TipoPonto.SALA);

        trecho(1, 2, 10.0, false, false, true, false);
        trecho(2, 3, 5.0, false, false, true, false);
        escada = trecho(3, 4, 6.0, true, false, true, false);
        trecho(4, 5, 4.0, false, false, true, false);
        trecho(2, 6, 8.0, false, false, true, false);
        elevador = trecho(6, 7, 3.0, false, true, true, false);
        trecho(7, 5, 7.0, false, false, true, false);
        trecho(2, 8, 12.0, false, false, true, false);
        entradaXY = trecho(8, 9, 20.0, false, false, true, false);
        trecho(9, 10, 9.0, false, false, true, false);
    }

    @Test
    void menorCaminhoUsaDistanciasConhecidas() {
        Rota rota = servico().calcular(1, 10, false, false);

        assertEquals(51.0, rota.getDistanciaTotal(), 0.001);
        assertEquals(List.of(1, 2, 8, 9, 10), ids(rota));
    }

    @Test
    void subidaDeAndarNoMesmoBlocoGeraTrocaDeAndar() {
        Rota rota = servico().calcular(1, 5, false, false);

        assertEquals(25.0, rota.getDistanciaTotal(), 0.001);
        assertEquals(List.of(1, 2, 3, 4, 5), ids(rota));
        assertTrue(contem(rota, "Troca de andar"));
        assertFalse(contem(rota, "Troca de bloco"));
    }

    @Test
    void ligacaoEntreBlocosGeraTrocaDeBlocoSemTrocaDeAndar() {
        Rota rota = servico().calcular(8, 9, false, false);

        assertTrue(contem(rota, "Troca de bloco"));
        assertFalse(contem(rota, "Troca de andar"));
    }

    @Test
    void modoPadraoNaoUsaElevador() {
        escada.setInterditado(true);

        PersistenciaException erro = assertThrows(PersistenciaException.class,
            () -> servico().calcular(1, 5, false, false));

        assertTrue(erro.getMessage().contains("Trechos com elevador excluidos: 1"));
        assertTrue(erro.getMessage().contains("evitar escadas"));
    }

    @Test
    void evitarEscadasUsaElevador() {
        escada.setInterditado(true);

        Rota rota = servico().calcular(1, 5, true, false);

        assertEquals(28.0, rota.getDistanciaTotal(), 0.001);
        assertEquals(List.of(1, 2, 6, 7, 5), ids(rota));
        assertTrue(contem(rota, "(elevador)"));
    }

    @Test
    void evitarEscadasSemElevadorNaoTemRota() {
        elevador.setInterditado(true);

        assertThrows(PersistenciaException.class, () -> servico().calcular(1, 5, true, false));
    }

    @Test
    void trechoInterditadoNuncaEntra() {
        trecho(1, 5, 2.0, false, false, true, true);

        Rota rota = servico().calcular(1, 5, false, false);

        assertEquals(25.0, rota.getDistanciaTotal(), 0.001);
        assertEquals(List.of(1, 2, 3, 4, 5), ids(rota));
    }

    @Test
    void somenteCobertoExcluiTrechoDescoberto() {
        entradaXY.setCoberto(false);

        Rota rota = servico().calcular(1, 10, false, false);
        assertEquals(51.0, rota.getDistanciaTotal(), 0.001);

        PersistenciaException erro = assertThrows(PersistenciaException.class,
            () -> servico().calcular(1, 10, false, true));
        assertTrue(erro.getMessage().contains("Nao existe rota"));
    }

    private RotaService servico() {
        return new RotaService(() -> pontos, () -> trechos);
    }

    private Ponto ponto(int id, int idAndar, String nomeAndar, String nomeBloco, String nome, TipoPonto tipo) {
        Ponto ponto = new Ponto(idAndar, nome, tipo, 0.0, 0.0);
        ponto.setIdPonto(id);
        ponto.setNomeAndar(nomeAndar);
        ponto.setNomeBloco(nomeBloco);
        pontos.add(ponto);
        return ponto;
    }

    private Trecho trecho(int idPontoA, int idPontoB, double metros, boolean possuiEscadas,
                          boolean usaElevador, boolean coberto, boolean interditado) {
        Trecho trecho = new Trecho(idPontoA, idPontoB, metros, possuiEscadas, usaElevador, coberto, interditado);
        trechos.add(trecho);
        return trecho;
    }

    private static List<Integer> ids(Rota rota) {
        return rota.getCaminho().stream().map(Ponto::getIdPonto).toList();
    }

    private static boolean contem(Rota rota, String texto) {
        return rota.getPassos().stream().anyMatch(passo -> passo.contains(texto));
    }
}
