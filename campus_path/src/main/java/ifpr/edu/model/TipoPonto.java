package ifpr.edu.model;

public enum TipoPonto {

    SALA,
    PASSAGEM,
    ACESSO;

    public static TipoPonto de(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Tipo de ponto obrigatorio.");
        }
        try {
            return TipoPonto.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Tipo de ponto invalido: " + valor + ". Use SALA, PASSAGEM ou ACESSO.");
        }
    }
}
