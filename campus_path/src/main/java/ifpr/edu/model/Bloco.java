package ifpr.edu.model;

public class Bloco {

    private Integer idBloco;
    private String nomeBloco;

    public Bloco() {
    }

    public Bloco(String nomeBloco) {
        this.nomeBloco = nomeBloco;
    }

    public Integer getIdBloco() {
        return idBloco;
    }

    public void setIdBloco(Integer idBloco) {
        this.idBloco = idBloco;
    }

    public String getNomeBloco() {
        return nomeBloco;
    }

    public void setNomeBloco(String nomeBloco) {
        this.nomeBloco = nomeBloco;
    }

    @Override
    public String toString() {
        return idBloco + " - " + nomeBloco;
    }
}
