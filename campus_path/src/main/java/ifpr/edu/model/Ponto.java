package ifpr.edu.model;

public class Ponto {

    private Integer idPonto;
    private Integer idAndar;
    private String nome;
    private TipoPonto tipo;
    private Double coordenadaX;
    private Double coordenadaY;
    private String nomeAndar;
    private String nomeBloco;

    public Ponto() {
    }

    public Ponto(Integer idAndar, String nome, TipoPonto tipo, Double coordenadaX, Double coordenadaY) {
        this.idAndar = idAndar;
        this.nome = nome;
        this.tipo = tipo;
        this.coordenadaX = coordenadaX;
        this.coordenadaY = coordenadaY;
    }

    public boolean isSala() {
        return tipo == TipoPonto.SALA;
    }

    public Integer getIdPonto() {
        return idPonto;
    }

    public void setIdPonto(Integer idPonto) {
        this.idPonto = idPonto;
    }

    public Integer getIdAndar() {
        return idAndar;
    }

    public void setIdAndar(Integer idAndar) {
        this.idAndar = idAndar;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoPonto getTipo() {
        return tipo;
    }

    public void setTipo(TipoPonto tipo) {
        this.tipo = tipo;
    }

    public Double getCoordenadaX() {
        return coordenadaX;
    }

    public void setCoordenadaX(Double coordenadaX) {
        this.coordenadaX = coordenadaX;
    }

    public Double getCoordenadaY() {
        return coordenadaY;
    }

    public void setCoordenadaY(Double coordenadaY) {
        this.coordenadaY = coordenadaY;
    }

    public String getNomeAndar() {
        return nomeAndar;
    }

    public void setNomeAndar(String nomeAndar) {
        this.nomeAndar = nomeAndar;
    }

    public String getNomeBloco() {
        return nomeBloco;
    }

    public void setNomeBloco(String nomeBloco) {
        this.nomeBloco = nomeBloco;
    }

    @Override
    public String toString() {
        String local = nomeAndar == null ? "andar " + idAndar : nomeAndar;
        return idPonto + " - " + nome + " [" + tipo + "] (" + local + ")";
    }
}
