package ifpr.edu.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Conexao {

    private static final String ARQUIVO = "db.properties";
    private static Properties configuracao;

    private Conexao() {
    }

    public static Connection abrir() {
        Properties config = configuracao();
        String url = valor(config, "db.url", "CAMPUSPATH_DB_URL");
        String usuario = valor(config, "db.user", "CAMPUSPATH_DB_USER");
        String senha = valor(config, "db.password", "CAMPUSPATH_DB_PASSWORD");

        if (url == null || url.isBlank()) {
            throw new PersistenciaException(
                "Configuracao do banco ausente: copie db.properties.example para db.properties e preencha os dados.");
        }

        try {
            return DriverManager.getConnection(url, usuario, senha);
        } catch (SQLException e) {
            throw new PersistenciaException(
                "Nao foi possivel conectar ao banco de dados: " + e.getMessage(), e);
        }
    }

    private static synchronized Properties configuracao() {
        if (configuracao == null) {
            configuracao = carregar();
        }
        return configuracao;
    }

    private static Properties carregar() {
        Properties config = new Properties();
        try (InputStream entrada = Conexao.class.getClassLoader().getResourceAsStream(ARQUIVO)) {
            if (entrada != null) {
                config.load(entrada);
            }
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao ler " + ARQUIVO + ": " + e.getMessage(), e);
        }
        return config;
    }

    private static String valor(Properties config, String chave, String variavel) {
        String ambiente = System.getenv(variavel);
        if (ambiente != null && !ambiente.isBlank()) {
            return ambiente;
        }
        return config.getProperty(chave);
    }
}
