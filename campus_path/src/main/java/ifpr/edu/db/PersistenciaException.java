package ifpr.edu.db;

public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensagem) {
        super(mensagem);
    }

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public static PersistenciaException de(java.sql.SQLException e, String contexto) {
        String detalhe;
        switch (e.getErrorCode()) {
            case 1062 -> detalhe = "ja existe um cadastro com esse login ou e-mail.";
            case 1048 -> detalhe = "um campo obrigatorio ficou vazio.";
            case 1451 -> detalhe = "o registro esta sendo usado por outro cadastro e nao pode ser excluido.";
            case 1452 -> detalhe = "o registro referenciado nao existe.";
            case 1644 -> detalhe = e.getMessage();
            default -> detalhe = e.getMessage();
        }
        return new PersistenciaException(contexto + ": " + detalhe, e);
    }
}
