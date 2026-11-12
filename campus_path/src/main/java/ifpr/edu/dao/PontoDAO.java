package ifpr.edu.dao;

import ifpr.edu.db.Conexao;
import ifpr.edu.db.PersistenciaException;
import ifpr.edu.model.Ponto;
import ifpr.edu.model.TipoPonto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PontoDAO {

    private static final String SELECT = "select p.id_ponto, p.id_andar, p.nome, p.tipo, p.coordenada_x, "
                                       + "p.coordenada_y, a.nome as nome_andar, b.nome_bloco "
                                       + "from ponto p "
                                       + "inner join andar a on a.id_andar = p.id_andar "
                                       + "inner join bloco b on b.id_bloco = a.id_bloco ";

    public void inserir(Ponto ponto) {
        String sql = "insert into ponto(id_andar, nome, tipo, coordenada_x, coordenada_y) values (?, ?, ?, ?, ?)";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, ponto.getIdAndar());
            stmt.setString(2, ponto.getNome());
            stmt.setString(3, ponto.getTipo().name());
            coordenada(stmt, 4, ponto.getCoordenadaX());
            coordenada(stmt, 5, ponto.getCoordenadaY());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao cadastrar ponto");
        }
    }

    public List<Ponto> listar() {
        List<Ponto> lista = new ArrayList<>();
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(
                 SELECT + "order by b.nome_bloco, a.nivel, p.nome");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao consultar pontos");
        }
        return lista;
    }

    public List<Ponto> listarPorAndar(int idAndar) {
        List<Ponto> lista = new ArrayList<>();
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(
                 SELECT + "where p.id_andar = ? order by p.tipo, p.nome")) {
            stmt.setInt(1, idAndar);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(ler(rs));
                }
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao consultar pontos do andar");
        }
        return lista;
    }

    public List<Ponto> listarSalas() {
        List<Ponto> lista = new ArrayList<>();
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(
                 SELECT + "where p.tipo = 'SALA' order by b.nome_bloco, a.nivel, p.nome");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao consultar salas");
        }
        return lista;
    }

    public Ponto buscarPorId(int id) {
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(SELECT + "where p.id_ponto = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return ler(rs);
                }
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao consultar ponto");
        }
        return null;
    }

    public void atualizar(Ponto ponto) {
        String sql = "update ponto set id_andar = ?, nome = ?, tipo = ?, coordenada_x = ?, "
                   + "coordenada_y = ? where id_ponto = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, ponto.getIdAndar());
            stmt.setString(2, ponto.getNome());
            stmt.setString(3, ponto.getTipo().name());
            coordenada(stmt, 4, ponto.getCoordenadaX());
            coordenada(stmt, 5, ponto.getCoordenadaY());
            stmt.setInt(6, ponto.getIdPonto());
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Ponto nao encontrado para alteracao.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao alterar ponto");
        }
    }

    public void excluir(int id) {
        String sql = "delete from ponto where id_ponto = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Ponto nao encontrado para exclusao.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao excluir ponto");
        }
    }

    private static void coordenada(PreparedStatement stmt, int indice, Double valor) throws SQLException {
        if (valor == null) {
            stmt.setNull(indice, Types.DECIMAL);
        } else {
            stmt.setDouble(indice, valor);
        }
    }

    private static Ponto ler(ResultSet rs) throws SQLException {
        Ponto ponto = new Ponto();
        ponto.setIdPonto(rs.getInt("id_ponto"));
        ponto.setIdAndar(rs.getInt("id_andar"));
        ponto.setNome(rs.getString("nome"));
        ponto.setTipo(TipoPonto.de(rs.getString("tipo")));
        double x = rs.getDouble("coordenada_x");
        ponto.setCoordenadaX(rs.wasNull() ? null : x);
        double y = rs.getDouble("coordenada_y");
        ponto.setCoordenadaY(rs.wasNull() ? null : y);
        ponto.setNomeAndar(rs.getString("nome_andar"));
        ponto.setNomeBloco(rs.getString("nome_bloco"));
        return ponto;
    }
}
