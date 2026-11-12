package ifpr.edu.model;

public class Administrador {

    private Integer idAdmin;
    private String nomeAdmin;
    private String emailAdmin;
    private String senhaAdmin;

    public Administrador() {
    }

    public Administrador(String nomeAdmin, String emailAdmin, String senhaAdmin) {
        this.nomeAdmin = nomeAdmin;
        this.emailAdmin = emailAdmin;
        this.senhaAdmin = senhaAdmin;
    }

    public Integer getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(Integer idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getNomeAdmin() {
        return nomeAdmin;
    }

    public void setNomeAdmin(String nomeAdmin) {
        this.nomeAdmin = nomeAdmin;
    }

    public String getEmailAdmin() {
        return emailAdmin;
    }

    public void setEmailAdmin(String emailAdmin) {
        this.emailAdmin = emailAdmin;
    }

    public String getSenhaAdmin() {
        return senhaAdmin;
    }

    public void setSenhaAdmin(String senhaAdmin) {
        this.senhaAdmin = senhaAdmin;
    }

    @Override
    public String toString() {
        return idAdmin + " - " + nomeAdmin + " (" + emailAdmin + ")";
    }
}
