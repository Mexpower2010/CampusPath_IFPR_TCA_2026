package ifpr.edu.dao;

import ifpr.edu.db.Conexao;
import ifpr.edu.db.PersistenciaException;
import ifpr.edu.model.Administrador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdministradorDAO {

    public void inserir(Administrador administrador) {
        String sql = "insert into administrador(nome_admin, email_admin, senha_admin) values (?, ?, ?)";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, administrador.getNomeAdmin());
            stmt.setString(2, administrador.getEmailAdmin());
            stmt.setString(3, administrador.getSenhaAdmin());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao cadastrar administrador");
        }
    }

    public List<Administrador> listar() {
        String sql = "select id_admin, nome_admin, email_admin, senha_admin from administrador order by nome_admin";
        List<Administrador> lista = new ArrayList<>();
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao consultar administradores");
        }
        return lista;
    }

    public Administrador buscarPorEmail(String email) {
        String sql = "select id_admin, nome_admin, email_admin, senha_admin from administrador where email_admin = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return ler(rs);
                }
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao consultar administrador");
        }
        return null;
    }

    public void atualizar(Administrador administrador) {
        String sql = "update administrador set nome_admin = ?, email_admin = ?, senha_admin = ? where id_admin = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, administrador.getNomeAdmin());
            stmt.setString(2, administrador.getEmailAdmin());
            stmt.setString(3, administrador.getSenhaAdmin());
            stmt.setInt(4, administrador.getIdAdmin());
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Administrador nao encontrado para alteracao.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao alterar administrador");
        }
    }

    public void excluir(int idAdmin) {
        String sql = "delete from administrador where id_admin = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idAdmin);
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Administrador nao encontrado para exclusao.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao excluir administrador");
        }
    }

    private static Administrador ler(ResultSet rs) throws SQLException {
        Administrador administrador = new Administrador();
        administrador.setIdAdmin(rs.getInt("id_admin"));
        administrador.setNomeAdmin(rs.getString("nome_admin"));
        administrador.setEmailAdmin(rs.getString("email_admin"));
        administrador.setSenhaAdmin(rs.getString("senha_admin"));
        return administrador;
    }
}
