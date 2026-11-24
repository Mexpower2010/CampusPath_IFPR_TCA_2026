package ifpr.edu.dao;

import ifpr.edu.db.Conexao;
import ifpr.edu.db.PersistenciaException;
import ifpr.edu.model.Professor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProfessorDAO {

    private static final String SELECT = "select p.id_professor, p.nome, p.login, p.senha_hash, p.foto_perfil, "
                                       + "p.id_ponto_atual, p.mensagem, p.mensagem_enviada_em, "
                                       + "pt.nome as nome_sala "
                                       + "from professor p "
                                       + "left join ponto pt on pt.id_ponto = p.id_ponto_atual ";

    public void inserir(Professor professor) {
        String sql = "insert into professor(nome, login, senha_hash, foto_perfil, id_ponto_atual, "
                   + "mensagem, mensagem_enviada_em) values (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, professor.getNome());
            stmt.setString(2, professor.getLogin());
            stmt.setString(3, professor.getSenhaHash());
            stmt.setString(4, professor.getFotoPerfil());
            ponto(stmt, 5, professor.getIdPontoAtual());
            stmt.setString(6, professor.getMensagem());
            data(stmt, 7, professor.getMensagemEnviadaEm());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao cadastrar professor");
        }
    }

    public List<Professor> listar() {
        List<Professor> lista = new ArrayList<>();
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(SELECT + "order by p.nome");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao consultar professores");
        }
        return lista;
    }

    public List<Professor> listarPresentes() {
        List<Professor> lista = new ArrayList<>();
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(
                 SELECT + "where p.id_ponto_atual is not null order by pt.nome, p.nome");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(ler(rs));
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao consultar professores presentes");
        }
        return lista;
    }

    public List<Professor> listarPorSala(int idPonto) {
        List<Professor> lista = new ArrayList<>();
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(
                 SELECT + "where p.id_ponto_atual = ? order by p.nome")) {
            stmt.setInt(1, idPonto);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(ler(rs));
                }
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao consultar professores da sala");
        }
        return lista;
    }

    public Professor buscarPorLogin(String login) {
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(SELECT + "where p.login = ?")) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return ler(rs);
                }
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao consultar professor");
        }
        return null;
    }

    public Professor buscarPorId(int id) {
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(SELECT + "where p.id_professor = ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return ler(rs);
                }
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao consultar professor");
        }
        return null;
    }

    public void atualizar(Professor professor) {
        String sql = "update professor set nome = ?, login = ?, foto_perfil = ?, "
                   + "id_ponto_atual = ?, mensagem = ?, mensagem_enviada_em = ? where id_professor = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, professor.getNome());
            stmt.setString(2, professor.getLogin());
            stmt.setString(3, professor.getFotoPerfil());
            ponto(stmt, 4, professor.getIdPontoAtual());
            stmt.setString(5, professor.getMensagem());
            data(stmt, 6, professor.getMensagemEnviadaEm());
            stmt.setInt(7, professor.getIdProfessor());
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Professor nao encontrado para alteracao.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao alterar professor");
        }
    }

    public void atualizarSenha(int idProfessor, String senhaHash) {
        String sql = "update professor set senha_hash = ? where id_professor = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, senhaHash);
            stmt.setInt(2, idProfessor);
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Professor nao encontrado.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao alterar senha");
        }
    }

    public void atualizarNome(int idProfessor, String nome) {
        String sql = "update professor set nome = ? where id_professor = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setInt(2, idProfessor);
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Professor nao encontrado.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao alterar nome");
        }
    }

    public void atualizarFoto(int idProfessor, String fotoPerfil) {
        String sql = "update professor set foto_perfil = ? where id_professor = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, fotoPerfil);
            stmt.setInt(2, idProfessor);
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Professor nao encontrado.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao alterar foto de perfil");
        }
    }

    public void atualizarLocalizacao(int idProfessor, Integer idPonto) {
        String sql = "update professor set id_ponto_atual = ? where id_professor = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            ponto(stmt, 1, idPonto);
            stmt.setInt(2, idProfessor);
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Professor nao encontrado.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao alterar localizacao");
        }
    }

    public void publicarMensagem(int idProfessor, String mensagem) {
        String sql = "update professor set mensagem = ?, mensagem_enviada_em = ? where id_professor = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, mensagem);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, idProfessor);
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Professor nao encontrado.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao publicar mensagem");
        }
    }

    public void removerPresenca(int idProfessor) {
        String sql = "update professor set id_ponto_atual = null, mensagem = null, "
                   + "mensagem_enviada_em = null where id_professor = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idProfessor);
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Professor nao encontrado.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao remover presenca");
        }
    }

    public void excluir(int idProfessor) {
        String sql = "delete from professor where id_professor = ?";
        try (Connection conexao = Conexao.abrir();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idProfessor);
            if (stmt.executeUpdate() == 0) {
                throw new PersistenciaException("Professor nao encontrado para exclusao.");
            }
        } catch (SQLException e) {
            throw PersistenciaException.de(e, "Erro ao excluir professor");
        }
    }

    private static void ponto(PreparedStatement stmt, int indice, Integer valor) throws SQLException {
        if (valor == null) {
            stmt.setNull(indice, Types.INTEGER);
        } else {
            stmt.setInt(indice, valor);
        }
    }

    private static void data(PreparedStatement stmt, int indice, LocalDateTime valor) throws SQLException {
        if (valor == null) {
            stmt.setNull(indice, Types.TIMESTAMP);
        } else {
            stmt.setTimestamp(indice, Timestamp.valueOf(valor));
        }
    }

    private static Professor ler(ResultSet rs) throws SQLException {
        Professor professor = new Professor();
        professor.setIdProfessor(rs.getInt("id_professor"));
        professor.setNome(rs.getString("nome"));
        professor.setLogin(rs.getString("login"));
        professor.setSenhaHash(rs.getString("senha_hash"));
        professor.setFotoPerfil(rs.getString("foto_perfil"));
        int ponto = rs.getInt("id_ponto_atual");
        professor.setIdPontoAtual(rs.wasNull() ? null : ponto);
        professor.setMensagem(rs.getString("mensagem"));
        Timestamp enviadaEm = rs.getTimestamp("mensagem_enviada_em");
        professor.setMensagemEnviadaEm(enviadaEm == null ? null : enviadaEm.toLocalDateTime());
        professor.setNomeSala(rs.getString("nome_sala"));
        return professor;
    }
}
