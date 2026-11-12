package ifpr.edu.ui;

import ifpr.edu.dao.PontoDAO;
import ifpr.edu.dao.ProfessorDAO;
import ifpr.edu.db.PersistenciaException;
import ifpr.edu.model.Ponto;
import ifpr.edu.model.Professor;
import ifpr.edu.service.AutenticacaoService;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MenuProfessor {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final AutenticacaoService autenticacao = new AutenticacaoService();
    private final ProfessorDAO professores = new ProfessorDAO();
    private final PontoDAO pontos = new PontoDAO();

    public void abrir() {
        System.out.println();
        System.out.println("--- Acesso do professor ---");
        String login = Entrada.texto("Login: ");
        String senha = Entrada.texto("Senha: ");

        Professor professor;
        try {
            professor = autenticacao.autenticarProfessor(login, senha);
        } catch (PersistenciaException e) {
            System.out.println("Erro: " + e.getMessage());
            return;
        }
        System.out.println("Bem-vindo, " + professor.getNome() + ".");
        menu(professor);
    }

    private void menu(Professor professor) {
        int opcao;
        do {
            System.out.println();
            System.out.println("--- Painel do professor ---");
            System.out.println("1 - Ver meu perfil");
            System.out.println("2 - Atualizar foto de perfil");
            System.out.println("3 - Alterar nome exibido");
            System.out.println("4 - Informar ou alterar minha sala");
            System.out.println("5 - Publicar ou atualizar mensagem");
            System.out.println("6 - Remover minha presenca");
            System.out.println("0 - Sair");
            opcao = Entrada.inteiro("Opcao: ");
            try {
                switch (opcao) {
                    case 1 -> perfil(professor);
                    case 2 -> atualizarFoto(professor);
                    case 3 -> atualizarNome(professor);
                    case 4 -> atualizarSala(professor);
                    case 5 -> publicarMensagem(professor);
                    case 6 -> removerPresenca(professor);
                    case 0 -> System.out.println("Sessao encerrada.");
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (PersistenciaException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void perfil(Professor professor) {
        Professor atual = professores.buscarPorId(professor.getIdProfessor());
        System.out.println("Nome: " + atual.getNome());
        System.out.println("Login: " + atual.getLogin());
        System.out.println("Foto: " + (atual.getFotoPerfil() == null ? "nao informada" : atual.getFotoPerfil()));
        System.out.println("Sala atual: " + (atual.getNomeSala() == null ? "sem localizacao" : atual.getNomeSala()));
        System.out.println("Mensagem: " + (atual.getMensagem() == null ? "sem mensagem" : atual.getMensagem()));
        System.out.println("Publicada em: " + (atual.getMensagemEnviadaEm() == null
            ? "nao informada" : atual.getMensagemEnviadaEm().format(HORA)));
    }

    private void atualizarFoto(Professor professor) {
        String foto = Entrada.textoOpcional("Caminho da foto", professor.getFotoPerfil());
        professores.atualizarFoto(professor.getIdProfessor(), foto);
        professor.setFotoPerfil(foto);
        System.out.println("Foto de perfil atualizada.");
    }

    private void atualizarNome(Professor professor) {
        String nome = Entrada.textoOpcional("Nome exibido", professor.getNome());
        professores.atualizarNome(professor.getIdProfessor(), nome);
        professor.setNome(nome);
        System.out.println("Nome atualizado.");
    }

    private void atualizarSala(Professor professor) {
        List<Ponto> salas = pontos.listarSalas();
        if (salas.isEmpty()) {
            System.out.println("Nenhuma sala cadastrada.");
            return;
        }
        System.out.println("Salas disponiveis:");
        salas.forEach(sala -> System.out.println("  " + sala));
        int idSala = Entrada.inteiro("Id da sala: ");
        Ponto sala = pontos.buscarPorId(idSala);
        if (sala == null || !sala.isSala()) {
            System.out.println("O ponto informado nao e uma sala.");
            return;
        }
        professores.atualizarLocalizacao(professor.getIdProfessor(), idSala);
        professor.setIdPontoAtual(idSala);
        professor.setNomeSala(sala.getNome());
        System.out.println("Localizacao atualizada para " + sala.getNome() + ".");
    }

    private void publicarMensagem(Professor professor) {
        String mensagem = Entrada.texto("Mensagem: ");
        professores.publicarMensagem(professor.getIdProfessor(), mensagem);
        System.out.println("Mensagem publicada com horario registrado automaticamente.");
    }

    private void removerPresenca(Professor professor) {
        if (!Entrada.sim("Confirma remover sua presenca")) {
            System.out.println("Operacao cancelada.");
            return;
        }
        professores.removerPresenca(professor.getIdProfessor());
        professor.setIdPontoAtual(null);
        professor.setMensagem(null);
        professor.setMensagemEnviadaEm(null);
        System.out.println("Presenca removida. Seu perfil continua cadastrado.");
    }
}
