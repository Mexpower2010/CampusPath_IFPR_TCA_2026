package ifpr.edu.dao;

import ifpr.edu.db.Conexao;
import ifpr.edu.db.PersistenciaException;
import ifpr.edu.model.Andar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AndarDAO {

    private static final String SELECT = "select a.id_andar, a.nome, a.nivel, a.imagem_mapa, a.id_bloco, "
                                       + "b.nome_bloco from andar a inner join bloco b on b.id_bloco = a.id_bloco ";

    public void inserir(Andar andar) {
        String sql = "insert into andar(nome, nivel, imagem_mapa, id_bloco) values (?, ?, ?, ?)";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, andar.getNome());
            stmt.setInt(2, andar.getNivel());
            stmt.setString(3, andar.getImagemMapa());
            stmt.setInt(4, andar.getIdBloco());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao cadastrar andar");
        }
    }

    public List<Andar> listar() {
        List<Andar> lista = new ArrayList<>();
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(SELECT + "order by b.nome_bloco, a.nivel");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao consultar andares");
        }
        return lista;
    }

    public List<Andar> listarPorBloco(int idBloco) {
        List<Andar> lista = new ArrayList<>();
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(
                 SELECT + "where a.id_bloco = ? order by a.nivel")) {
            stmt.setInt(1, idBloco);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(ler(rs));
                }
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao consultar andares do bloco");
        }
        return lista;
    }

    public Andar buscarPorId(int id) {
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(SELECT + "where a.id_andar = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return ler(rs);
                }
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao consultar andar");
        }
        return null;
    }

    public void atualizar(Andar andar) {
        String sql = "update andar set nome = ?, nivel = ?, imagem_mapa = ?, id_bloco = ? where id_andar = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, andar.getNome());
            stmt.setInt(2, andar.getNivel());
            stmt.setString(3, andar.getImagemMapa());
            stmt.setInt(4, andar.getIdBloco());
            stmt.setInt(5, andar.getIdAndar());
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Andar nao encontrado para alteracao.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao alterar andar");
        }
    }

    public void excluir(int id) {
        String sql = "delete from andar where id_andar = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Andar nao encontrado para exclusao.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao excluir andar");
        }
    }

    private static Andar ler(ResultSet rs) throws SQLException {
        Andar andar = new Andar();
        andar.setIdAndar(rs.getInt("id_andar"));
        andar.setNome(rs.getString("nome"));
        andar.setNivel(rs.getInt("nivel"));
        andar.setImagemMapa(rs.getString("imagem_mapa"));
        andar.setIdBloco(rs.getInt("id_bloco"));
        andar.setNomeBloco(rs.getString("nome_bloco"));
        return andar;
    }
}
