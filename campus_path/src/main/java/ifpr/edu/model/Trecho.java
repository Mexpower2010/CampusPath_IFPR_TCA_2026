package ifpr.edu.model;

public class Trecho {

    private Integer idTrecho;
    private Integer idPontoA;
    private Integer idPontoB;
    private double distanciaMetros;
    private boolean possuiEscadas;
    private boolean usaElevador;
    private boolean coberto;
    private boolean interditado;
    private String nomePontoA;
    private String nomePontoB;

    public Trecho() {
    }

    public Trecho(Integer idPontoA, Integer idPontoB, double distanciaMetros,
                  boolean possuiEscadas, boolean usaElevador, boolean coberto, boolean interditado) {
        this.idPontoA = idPontoA;
        this.idPontoB = idPontoB;
        this.distanciaMetros = distanciaMetros;
        this.possuiEscadas = possuiEscadas;
        this.usaElevador = usaElevador;
        this.coberto = coberto;
        this.interditado = interditado;
    }

    public Integer getIdTrecho() {
        return idTrecho;
    }

    public void setIdTrecho(Integer idTrecho) {
        this.idTrecho = idTrecho;
    }

    public Integer getIdPontoA() {
        return idPontoA;
    }

    public void setIdPontoA(Integer idPontoA) {
        this.idPontoA = idPontoA;
    }

    public Integer getIdPontoB() {
        return idPontoB;
    }

    public void setIdPontoB(Integer idPontoB) {
        this.idPontoB = idPontoB;
    }

    public double getDistanciaMetros() {
        return distanciaMetros;
    }

    public void setDistanciaMetros(double distanciaMetros) {
        this.distanciaMetros = distanciaMetros;
    }

    public boolean isPossuiEscadas() {
        return possuiEscadas;
    }

    public void setPossuiEscadas(boolean possuiEscadas) {
        this.possuiEscadas = possuiEscadas;
    }

    public boolean isUsaElevador() {
        return usaElevador;
    }

    public void setUsaElevador(boolean usaElevador) {
        this.usaElevador = usaElevador;
    }

    public boolean isCoberto() {
        return coberto;
    }

    public void setCoberto(boolean coberto) {
        this.coberto = coberto;
    }

    public boolean isInterditado() {
        return interditado;
    }

    public void setInterditado(boolean interditado) {
        this.interditado = interditado;
    }

    public String getNomePontoA() {
        return nomePontoA;
    }

    public void setNomePontoA(String nomePontoA) {
        this.nomePontoA = nomePontoA;
    }

    public String getNomePontoB() {
        return nomePontoB;
    }

    public void setNomePontoB(String nomePontoB) {
        this.nomePontoB = nomePontoB;
    }

    @Override
    public String toString() {
        String a = nomePontoA == null ? "ponto " + idPontoA : nomePontoA;
        String b = nomePontoB == null ? "ponto " + idPontoB : nomePontoB;
        return idTrecho + " - " + a + " <-> " + b + " | " + distanciaMetros + " m"
            + (possuiEscadas ? " | escadas" : "")
            + (usaElevador ? " | elevador" : "")
            + (coberto ? " | coberto" : " | descoberto")
            + (interditado ? " | INTERDITADO" : "");
    }
}
