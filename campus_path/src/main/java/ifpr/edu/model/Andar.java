package ifpr.edu.model;

public class Andar {

    private Integer idAndar;
    private String nome;
    private Integer nivel;
    private String imagemMapa;
    private Integer idBloco;
    private String nomeBloco;

    public Andar() {
    }

    public Andar(String nome, Integer nivel, String imagemMapa, Integer idBloco) {
        this.nome = nome;
        this.nivel = nivel;
        this.imagemMapa = imagemMapa;
        this.idBloco = idBloco;
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

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public String getImagemMapa() {
        return imagemMapa;
    }

    public void setImagemMapa(String imagemMapa) {
        this.imagemMapa = imagemMapa;
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
        String bloco = nomeBloco == null ? "bloco " + idBloco : nomeBloco;
        return idAndar + " - " + bloco + " / " + nome + " (nivel " + nivel + ")";
    }
}
