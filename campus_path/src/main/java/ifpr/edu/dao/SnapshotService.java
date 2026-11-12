package ifpr.edu.dao;

import ifpr.edu.db.Conexao;
import ifpr.edu.db.PersistenciaException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SnapshotService {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path arquivo;

    public SnapshotService() {
        this(Paths.get("dados", "snapshot.sql"));
    }

    public SnapshotService(Path arquivo) {
        this.arquivo = arquivo;
    }

    public Path getArquivo() {
        return arquivo;
    }

    public void exportar() {
        String geradoEm = LocalDateTime.now().format(FORMATO);
        List<String> linhas = new ArrayList<>();
        linhas.add("-- CampusPath - estado atual do banco, gerado automaticamente pela aplicacao.");
        linhas.add("-- Versione este arquivo no Git para restaurar o estado em outro computador.");
        linhas.add("-- Gerado em " + geradoEm);
        linhas.add("");
        linhas.add("use campuspath;");
        linhas.add("");
        linhas.add("delete from professor;");
        linhas.add("delete from administrador;");
        linhas.add("delete from trecho;");
        linhas.add("delete from ponto;");
        linhas.add("delete from andar;");
        linhas.add("delete from bloco;");
        linhas.add("");

        try (Connection conexao = Conexao.abrir()) {
            linhas.addAll(verificado(conexao, "bloco", exportarBlocos(conexao)));
            linhas.addAll(verificado(conexao, "andar", exportarAndares(conexao)));
            linhas.addAll(verificado(conexao, "ponto", exportarPontos(conexao)));
            linhas.addAll(verificado(conexao, "trecho", exportarTrechos(conexao)));
            linhas.addAll(verificado(conexao, "administrador", exportarAdministradores(conexao)));
            linhas.addAll(verificado(conexao, "professor", exportarProfessores(conexao)));
            linhas.add("");
            linhas.add("update controle_sync set snapshot_aplicado_em = '" + geradoEm + "' where id_controle = 1;");
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao gerar o snapshot");
        }

        try {
            Path pasta = arquivo.toAbsolutePath().getParent();
            if (pasta != null) {
                Files.createDirectories(pasta);
            }
            Files.write(arquivo, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao gravar " + arquivo + ": " + e.getMessage(), e);
        }
    }

    public boolean sincronizar() {
        if (!Files.exists(arquivo)) {
            return false;
        }
        String geradoEm = lerGeradoEm();
        if (geradoEm == null) {
            return false;
        }
        try (Connection conexao = Conexao.abrir()) {
            if (geradoEm.equals(marcadorAplicado(conexao))) {
                return false;
            }
            conexao.setAutoCommit(false);
            try {
                for (String comando : lerComandos()) {
                    try (Statement stmt = conexao.createStatement()) {
                        stmt.execute(comando);
                    } catch (SQLException e) {
                        if (e.getErrorCode() == 1452) {
                            throw new PersistenciaException(
                                "O snapshot referencia registros que nao existem neste banco. "
                                + "Execute o schema.sql antes de abrir o sistema.", e);
                        }
                        throw e;
                    }
                }
                conexao.commit();
                return true;
            } catch (SQLException | PersistenciaException e) {
                conexao.rollback();
                throw e;
            } finally {
                conexao.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao sincronizar o snapshot");
        }
    }

    private String lerGeradoEm() {
        try {
            for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
                String limpa = linha.trim();
                if (limpa.startsWith("-- Gerado em ")) {
                    return limpa.substring("-- Gerado em ".length()).trim();
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao ler " + arquivo + ": " + e.getMessage(), e);
        }
        return null;
    }

    private static String marcadorAplicado(Connection conexao) throws SQLException {
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "select snapshot_aplicado_em from controle_sync where id_controle = 1")) {
            if (rs.next()) {
                return rs.getString(1);
            }
        }
        return null;
    }

    private static List<String> verificado(Connection conexao, String tabela, List<String> linhas)
            throws SQLException {
        int noBanco = contar(conexao, tabela);
        if (noBanco != linhas.size()) {
            throw new PersistenciaException("Falha ao exportar " + tabela + ": "
                + noBanco + " registros no banco e " + linhas.size() + " gravados no snapshot.");
        }
        return linhas;
    }

    private static int contar(Connection conexao, String tabela) throws SQLException {
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery("select count(*) from " + tabela)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private List<String> exportarBlocos(Connection conexao) throws SQLException {
        List<String> linhas = new ArrayList<>();
        try (PreparedStatement stmt = conexao.prepareStatement(
                 "select id_bloco, nome_bloco from bloco order by id_bloco");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                linhas.add("insert into bloco(id_bloco, nome_bloco) values ("
                    + rs.getInt("id_bloco") + ", " + texto(rs.getString("nome_bloco")) + ");");
            }
        }
        return linhas;
    }

    private List<String> exportarAndares(Connection conexao) throws SQLException {
        List<String> linhas = new ArrayList<>();
        try (PreparedStatement stmt = conexao.prepareStatement(
                 "select id_andar, nome, nivel, imagem_mapa, id_bloco from andar order by id_andar");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                linhas.add("insert into andar(id_andar, nome, nivel, imagem_mapa, id_bloco) values ("
                    + rs.getInt("id_andar") + ", " + texto(rs.getString("nome")) + ", "
                    + rs.getInt("nivel") + ", " + texto(rs.getString("imagem_mapa")) + ", "
                    + rs.getInt("id_bloco") + ");");
            }
        }
        return linhas;
    }

    private List<String> exportarPontos(Connection conexao) throws SQLException {
        List<String> linhas = new ArrayList<>();
        try (PreparedStatement stmt = conexao.prepareStatement(
                 "select id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y from ponto order by id_ponto");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                linhas.add("insert into ponto(id_ponto, id_andar, nome, tipo, coordenada_x, coordenada_y) values ("
                    + rs.getInt("id_ponto") + ", " + rs.getInt("id_andar") + ", "
                    + texto(rs.getString("nome")) + ", " + texto(rs.getString("tipo")) + ", "
                    + numero(rs, "coordenada_x") + ", " + numero(rs, "coordenada_y") + ");");
            }
        }
        return linhas;
    }

    private List<String> exportarTrechos(Connection conexao) throws SQLException {
        List<String> linhas = new ArrayList<>();
        try (PreparedStatement stmt = conexao.prepareStatement(
                 "select id_trecho, id_ponto_a, id_ponto_b, distancia_metros, possui_escadas, "
                 + "usa_elevador, coberto, interditado from trecho order by id_trecho");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                linhas.add("insert into trecho(id_trecho, id_ponto_a, id_ponto_b, distancia_metros, "
                    + "possui_escadas, usa_elevador, coberto, interditado) values ("
                    + rs.getInt("id_trecho") + ", " + rs.getInt("id_ponto_a") + ", "
                    + rs.getInt("id_ponto_b") + ", " + rs.getDouble("distancia_metros") + ", "
                    + rs.getBoolean("possui_escadas") + ", " + rs.getBoolean("usa_elevador") + ", "
                    + rs.getBoolean("coberto") + ", " + rs.getBoolean("interditado") + ");");
            }
        }
        return linhas;
    }

    private List<String> exportarAdministradores(Connection conexao) throws SQLException {
        List<String> linhas = new ArrayList<>();
        try (PreparedStatement stmt = conexao.prepareStatement(
                 "select id_admin, nome_admin, email_admin, senha_admin from administrador order by id_admin");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                linhas.add("insert into administrador(id_admin, nome_admin, email_admin, senha_admin) values ("
                    + rs.getInt("id_admin") + ", " + texto(rs.getString("nome_admin")) + ", "
                    + texto(rs.getString("email_admin")) + ", " + texto(rs.getString("senha_admin")) + ");");
            }
        }
        return linhas;
    }

    private List<String> exportarProfessores(Connection conexao) throws SQLException {
        List<String> linhas = new ArrayList<>();
        try (PreparedStatement stmt = conexao.prepareStatement(
                 "select id_professor, nome, login, senha_hash, foto_perfil, id_ponto_atual, "
                 + "mensagem, mensagem_enviada_em from professor order by id_professor");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                linhas.add("insert into professor(id_professor, nome, login, senha_hash, foto_perfil, "
                    + "id_ponto_atual, mensagem, mensagem_enviada_em) values ("
                    + rs.getInt("id_professor") + ", " + texto(rs.getString("nome")) + ", "
                    + texto(rs.getString("login")) + ", " + texto(rs.getString("senha_hash")) + ", "
                    + texto(rs.getString("foto_perfil")) + ", " + numero(rs, "id_ponto_atual") + ", "
                    + texto(rs.getString("mensagem")) + ", "
                    + data(rs.getTimestamp("mensagem_enviada_em")) + ");");
            }
        }
        return linhas;
    }

    private List<String> lerComandos() {
        List<String> comandos = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        try {
            for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
                String limpa = linha.trim();
                if (limpa.isEmpty() || limpa.startsWith("--") || limpa.toLowerCase().startsWith("use ")) {
                    continue;
                }
                atual.append(linha).append('\n');
                if (limpa.endsWith(";")) {
                    comandos.add(atual.toString());
                    atual.setLength(0);
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao ler " + arquivo + ": " + e.getMessage(), e);
        }
        return comandos;
    }

    private static String texto(String valor) {
        if (valor == null) {
            return "NULL";
        }
        return "'" + valor.replace("'", "''") + "'";
    }

    private static String numero(ResultSet rs, String coluna) throws SQLException {
        Object valor = rs.getObject(coluna);
        return valor == null ? "NULL" : valor.toString();
    }

    private static String data(Timestamp valor) {
        if (valor == null) {
            return "NULL";
        }
        return "'" + valor.toLocalDateTime().format(FORMATO) + "'";
    }
}
