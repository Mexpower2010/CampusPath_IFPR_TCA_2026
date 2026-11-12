package ifpr.edu.dao;

import ifpr.edu.db.Conexao;
import ifpr.edu.db.PersistenciaException;
import ifpr.edu.model.Trecho;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrechoDAO {

    private static final String SELECT = "select t.id_trecho, t.id_ponto_a, t.id_ponto_b, t.distancia_metros, "
                                       + "t.possui_escadas, t.usa_elevador, t.coberto, t.interditado, "
                                       + "pa.nome as nome_ponto_a, pb.nome as nome_ponto_b "
                                       + "from trecho t "
                                       + "inner join ponto pa on pa.id_ponto = t.id_ponto_a "
                                       + "inner join ponto pb on pb.id_ponto = t.id_ponto_b ";

    public void inserir(Trecho trecho) {
        String sql = "insert into trecho(id_ponto_a, id_ponto_b, distancia_metros, possui_escadas, "
                   + "usa_elevador, coberto, interditado) values (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, trecho.getIdPontoA());
            stmt.setInt(2, trecho.getIdPontoB());
            stmt.setDouble(3, trecho.getDistanciaMetros());
            stmt.setBoolean(4, trecho.isPossuiEscadas());
            stmt.setBoolean(5, trecho.isUsaElevador());
            stmt.setBoolean(6, trecho.isCoberto());
            stmt.setBoolean(7, trecho.isInterditado());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao cadastrar trecho");
        }
    }

    public List<Trecho> listar() {
        List<Trecho> lista = new ArrayList<>();
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(SELECT + "order by t.id_trecho");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao consultar trechos");
        }
        return lista;
    }

    public Trecho buscarPorId(int id) {
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(SELECT + "where t.id_trecho = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return ler(rs);
                }
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao consultar trecho");
        }
        return null;
    }

    public void atualizar(Trecho trecho) {
        String sql = "update trecho set id_ponto_a = ?, id_ponto_b = ?, distancia_metros = ?, "
                   + "possui_escadas = ?, usa_elevador = ?, coberto = ?, interditado = ? "
                   + "where id_trecho = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, trecho.getIdPontoA());
            stmt.setInt(2, trecho.getIdPontoB());
            stmt.setDouble(3, trecho.getDistanciaMetros());
            stmt.setBoolean(4, trecho.isPossuiEscadas());
            stmt.setBoolean(5, trecho.isUsaElevador());
            stmt.setBoolean(6, trecho.isCoberto());
            stmt.setBoolean(7, trecho.isInterditado());
            stmt.setInt(8, trecho.getIdTrecho());
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Trecho nao encontrado para alteracao.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao alterar trecho");
        }
    }

    public void atualizarCondicoes(int idTrecho, boolean coberto, boolean interditado) {
        String sql = "update trecho set coberto = ?, interditado = ? where id_trecho = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setBoolean(1, coberto);
            stmt.setBoolean(2, interditado);
            stmt.setInt(3, idTrecho);
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Trecho nao encontrado para alteracao.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao alterar condicoes do trecho");
        }
    }

    public void excluir(int id) {
        String sql = "delete from trecho where id_trecho = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Trecho nao encontrado para exclusao.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao excluir trecho");
        }
    }

    private static Trecho ler(ResultSet rs) throws SQLException {
        Trecho trecho = new Trecho();
        trecho.setIdTrecho(rs.getInt("id_trecho"));
        trecho.setIdPontoA(rs.getInt("id_ponto_a"));
        trecho.setIdPontoB(rs.getInt("id_ponto_b"));
        trecho.setDistanciaMetros(rs.getDouble("distancia_metros"));
        trecho.setPossuiEscadas(rs.getBoolean("possui_escadas"));
        trecho.setUsaElevador(rs.getBoolean("usa_elevador"));
        trecho.setCoberto(rs.getBoolean("coberto"));
        trecho.setInterditado(rs.getBoolean("interditado"));
        trecho.setNomePontoA(rs.getString("nome_ponto_a"));
        trecho.setNomePontoB(rs.getString("nome_ponto_b"));
        return trecho;
    }
}
