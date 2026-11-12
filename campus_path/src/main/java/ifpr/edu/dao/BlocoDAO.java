package ifpr.edu.dao;

import ifpr.edu.db.Conexao;
import ifpr.edu.db.PersistenciaException;
import ifpr.edu.model.Bloco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BlocoDAO {

    public void inserir(Bloco bloco) {
        String sql = "insert into bloco(nome_bloco) values (?)";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, bloco.getNomeBloco());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao cadastrar bloco");
        }
    }

    public List<Bloco> listar() {
        String sql = "select id_bloco, nome_bloco from bloco order by nome_bloco";
        List<Bloco> lista = new ArrayList<>();
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao consultar blocos");
        }
        return lista;
    }

    public Bloco buscarPorId(int id) {
        String sql = "select id_bloco, nome_bloco from bloco where id_bloco = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return ler(rs);
                }
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao consultar bloco");
        }
        return null;
    }

    public void atualizar(Bloco bloco) {
        String sql = "update bloco set nome_bloco = ? where id_bloco = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, bloco.getNomeBloco());
            stmt.setInt(2, bloco.getIdBloco());
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Bloco nao encontrado para alteracao.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao alterar bloco");
        }
    }

    public void excluir(int id) {
        String sql = "delete from bloco where id_bloco = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Bloco nao encontrado para exclusao.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao excluir bloco");
        }
    }

    private static Bloco ler(ResultSet rs) throws SQLException {
        Bloco bloco = new Bloco();
        bloco.setIdBloco(rs.getInt("id_bloco"));
        bloco.setNomeBloco(rs.getString("nome_bloco"));
        return bloco;
    }
}
