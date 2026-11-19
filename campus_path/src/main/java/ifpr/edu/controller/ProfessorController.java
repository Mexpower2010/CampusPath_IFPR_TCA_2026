package ifpr.edu.controller;

import ifpr.edu.dao.PontoDAO;
import ifpr.edu.dao.ProfessorDAO;
import ifpr.edu.db.PersistenciaException;
import ifpr.edu.model.Ponto;
import ifpr.edu.model.Professor;
import ifpr.edu.service.AutenticacaoService;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ProfessorController {

    private static final long TAMANHO_MAXIMO_FOTO = 5L * 1024 * 1024;

    private final ProfessorDAO professores = new ProfessorDAO();
    private final PontoDAO pontos = new PontoDAO();
    private final AutenticacaoService autenticacao = new AutenticacaoService();

    public Professor autenticar(String login, String senha) {
        return autenticacao.autenticarProfessor(login, senha);
    }

    public List<Ponto> listarSalas() {
        return pontos.listarSalas();
    }

    public Professor recarregar(int idProfessor) {
        return professores.buscarPorId(idProfessor);
    }

    public void atualizarLocalizacao(int idProfessor, Integer idPonto) {
        professores.atualizarLocalizacao(idProfessor, idPonto);
    }

    public void removerPresenca(int idProfessor) {
        professores.removerPresenca(idProfessor);
    }

    public void publicarMensagem(int idProfessor, String mensagem) {
        professores.publicarMensagem(idProfessor, mensagem);
    }

    public void atualizarNome(int idProfessor, String nome) {
        professores.atualizarNome(idProfessor, nome);
    }

    public void atualizarFoto(int idProfessor, String fotoPerfil) {
        professores.atualizarFoto(idProfessor, fotoPerfil);
    }

    public void enviarFoto(int idProfessor, Path origem) {
        if (origem == null || !Files.isRegularFile(origem)) {
            throw new IllegalArgumentException("Escolha um arquivo de imagem valido.");
        }
        if (!origem.getFileName().toString().toLowerCase().endsWith(".png")) {
            throw new IllegalArgumentException("Escolha um arquivo com extensao .png.");
        }
        try {
            if (Files.size(origem) > TAMANHO_MAXIMO_FOTO) {
                throw new IllegalArgumentException("A imagem deve ter no maximo 5 MB.");
            }
            if (ImageIO.read(origem.toFile()) == null) {
                throw new IllegalArgumentException("Nao foi possivel ler a imagem escolhida.");
            }
            Path destino = Paths.get("fotos", "prof_" + idProfessor + ".png");
            Path pasta = destino.getParent();
            if (pasta != null) {
                Files.createDirectories(pasta);
            }
            Files.copy(origem, destino, StandardCopyOption.REPLACE_EXISTING);
            professores.atualizarFoto(idProfessor, destino.toString().replace('\\', '/'));
        } catch (IOException e) {
            throw new PersistenciaException("Falha ao copiar a foto: " + e.getMessage(), e);
        }
    }
}
