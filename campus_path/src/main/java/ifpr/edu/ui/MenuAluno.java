package ifpr.edu.ui;

import ifpr.edu.dao.AndarDAO;
import ifpr.edu.dao.BlocoDAO;
import ifpr.edu.dao.PontoDAO;
import ifpr.edu.dao.ProfessorDAO;
import ifpr.edu.db.PersistenciaException;
import ifpr.edu.model.Andar;
import ifpr.edu.model.Bloco;
import ifpr.edu.model.Ponto;
import ifpr.edu.model.Professor;
import ifpr.edu.service.Rota;
import ifpr.edu.service.RotaService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MenuAluno {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final BlocoDAO blocos = new BlocoDAO();
    private final AndarDAO andares = new AndarDAO();
    private final PontoDAO pontos = new PontoDAO();
    private final ProfessorDAO professores = new ProfessorDAO();
    private final RotaService rotas = new RotaService();

    public void abrir() {
        int opcao;
        do {
            System.out.println();
            System.out.println("--- Area do aluno ---");
            System.out.println("1 - Ver mapa do campus");
            System.out.println("2 - Professores presentes no campus");
            System.out.println("3 - Calcular rota");
            System.out.println("0 - Voltar");
            opcao = Entrada.inteiro("Opcao: ");
            try {
                switch (opcao) {
                    case 1 -> verMapa();
                    case 2 -> listarPresentes();
                    case 3 -> calcularRota();
                    case 0 -> System.out.println("Voltando ao menu principal.");
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (PersistenciaException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void verMapa() {
        List<Bloco> listaBlocos = blocos.listar();
        if (listaBlocos.isEmpty()) {
            System.out.println("Nenhum bloco cadastrado.");
            return;
        }
        System.out.println("Blocos disponiveis:");
        listaBlocos.forEach(bloco -> System.out.println("  " + bloco));
        int idBloco = Entrada.inteiro("Id do bloco: ");

        List<Andar> listaAndares = andares.listarPorBloco(idBloco);
        if (listaAndares.isEmpty()) {
            System.out.println("Este bloco nao possui andares cadastrados.");
            return;
        }
        System.out.println("Andares disponiveis:");
        listaAndares.forEach(andar -> System.out.println("  " + andar));
        int idAndar = Entrada.inteiro("Id do andar: ");

        Andar andar = andares.buscarPorId(idAndar);
        if (andar == null || !andar.getIdBloco().equals(idBloco)) {
            System.out.println("Andar nao pertence ao bloco informado.");
            return;
        }
        exibirAndar(andar, Set.of());
        expandirSala(idAndar);
    }

    private void exibirAndar(Andar andar, Set<Integer> rota) {
        System.out.println();
        System.out.println("Mapa: " + andar.getNomeBloco() + " / " + andar.getNome()
            + " (nivel " + andar.getNivel() + ")");
        if (andar.getImagemMapa() != null) {
            System.out.println("Imagem de referencia: " + andar.getImagemMapa());
        }
        List<Ponto> lista = pontos.listarPorAndar(andar.getIdAndar());
        if (lista.isEmpty()) {
            System.out.println("Nenhum ponto cadastrado neste andar.");
            return;
        }
        for (Ponto ponto : lista) {
            List<Professor> presentes = ponto.isSala()
                ? professores.listarPorSala(ponto.getIdPonto())
                : List.of();
            String marcador = ponto.isSala() && !presentes.isEmpty() ? "[*]" : "[ ]";
            String percurso = rota.contains(ponto.getIdPonto()) ? " <== rota" : "";
            System.out.println("  " + marcador + " " + ponto.getIdPonto() + " - " + ponto.getNome()
                + " [" + ponto.getTipo() + "]"
                + coordenadas(ponto)
                + (ponto.isSala() && !presentes.isEmpty() ? " | " + presentes.size() + " professor(es)" : "")
                + percurso);
        }
        System.out.println("  [*] sala com professor presente   [ ] sem professor");
    }

    private void expandirSala(int idAndar) {
        if (!Entrada.sim("Deseja abrir uma sala")) {
            return;
        }
        int idPonto = Entrada.inteiro("Id da sala: ");
        Ponto ponto = pontos.buscarPorId(idPonto);
        if (ponto == null || !ponto.getIdAndar().equals(idAndar) || !ponto.isSala()) {
            System.out.println("Sala invalida para este andar.");
            return;
        }
        List<Professor> presentes = professores.listarPorSala(idPonto);
        if (presentes.isEmpty()) {
            System.out.println(ponto.getNome() + ": nenhum professor presente nesta sala no momento.");
            return;
        }
        System.out.println(ponto.getNome() + ":");
        for (Professor professor : presentes) {
            System.out.println("  " + professor.getNome());
            System.out.println("    Foto: " + (professor.getFotoPerfil() == null ? "nao informada" : professor.getFotoPerfil()));
            System.out.println("    Mensagem: " + (professor.getMensagem() == null ? "sem mensagem" : professor.getMensagem()));
            System.out.println("    Publicada em: " + (professor.getMensagemEnviadaEm() == null
                ? "nao informada" : professor.getMensagemEnviadaEm().format(HORA)));
        }
    }

    private void listarPresentes() {
        List<Professor> presentes = professores.listarPresentes();
        if (presentes.isEmpty()) {
            System.out.println("Nenhum professor com localizacao informada no momento.");
            return;
        }
        System.out.println("Professores presentes:");
        for (Professor professor : presentes) {
            System.out.println("  " + professor.getNome() + " | " + professor.getNomeSala()
                + " | " + (professor.getMensagem() == null ? "sem mensagem" : professor.getMensagem())
                + " | " + (professor.getMensagemEnviadaEm() == null
                    ? "sem horario" : professor.getMensagemEnviadaEm().format(HORA)));
        }
    }

    private void calcularRota() {
        List<Ponto> lista = pontos.listar();
        if (lista.size() < 2) {
            System.out.println("Sao necessarios pelo menos dois pontos cadastrados.");
            return;
        }
        System.out.println("Pontos disponiveis:");
        lista.forEach(ponto -> System.out.println("  " + ponto));
        int origem = Entrada.inteiro("Id do ponto de origem: ");
        int destino = Entrada.inteiro("Id da sala de destino: ");
        boolean evitarEscadas = Entrada.sim("Evitar escadas");
        boolean somenteCoberto = Entrada.sim("Somente coberto");

        Rota rota = rotas.calcular(origem, destino, evitarEscadas, somenteCoberto);
        System.out.println();
        System.out.println("Rota calculada:");
        rota.getPassos().forEach(System.out::println);
        System.out.println("Distancia total: " + String.format("%.1f", rota.getDistanciaTotal()).replace('.', ',') + " m");

        Set<Integer> noPercurso = new HashSet<>();
        rota.getCaminho().forEach(ponto -> noPercurso.add(ponto.getIdPonto()));
        List<Andar> andaresDoPercurso = new ArrayList<>();
        for (Ponto ponto : rota.getCaminho()) {
            Andar andar = andares.buscarPorId(ponto.getIdAndar());
            if (andar != null && andaresDoPercurso.stream()
                    .noneMatch(a -> a.getIdAndar().equals(andar.getIdAndar()))) {
                andaresDoPercurso.add(andar);
            }
        }
        for (Andar andar : andaresDoPercurso) {
            exibirAndar(andar, noPercurso);
        }
    }

    private static String coordenadas(Ponto ponto) {
        if (ponto.getCoordenadaX() == null || ponto.getCoordenadaY() == null) {
            return "";
        }
        return " (" + String.format("%.0f", ponto.getCoordenadaX()) + ","
            + String.format("%.0f", ponto.getCoordenadaY()) + ")";
    }
}
