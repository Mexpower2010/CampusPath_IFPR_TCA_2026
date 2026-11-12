package ifpr.edu.ui;

import ifpr.edu.dao.AdministradorDAO;
import ifpr.edu.dao.AndarDAO;
import ifpr.edu.dao.BlocoDAO;
import ifpr.edu.dao.PontoDAO;
import ifpr.edu.dao.ProfessorDAO;
import ifpr.edu.dao.TrechoDAO;
import ifpr.edu.db.PersistenciaException;
import ifpr.edu.model.Administrador;
import ifpr.edu.model.Andar;
import ifpr.edu.model.Bloco;
import ifpr.edu.model.Ponto;
import ifpr.edu.model.Professor;
import ifpr.edu.model.TipoPonto;
import ifpr.edu.model.Trecho;
import ifpr.edu.service.AutenticacaoService;
import ifpr.edu.util.Senha;

import java.util.List;

public class MenuAdmin {

    private final AutenticacaoService autenticacao = new AutenticacaoService();
    private final BlocoDAO blocos = new BlocoDAO();
    private final AndarDAO andares = new AndarDAO();
    private final PontoDAO pontos = new PontoDAO();
    private final TrechoDAO trechos = new TrechoDAO();
    private final ProfessorDAO professores = new ProfessorDAO();
    private final AdministradorDAO administradores = new AdministradorDAO();

    public void abrir() {
        System.out.println();
        System.out.println("--- Acesso do administrador ---");
        String email = Entrada.texto("E-mail: ");
        String senha = Entrada.texto("Senha: ");

        Administrador administrador;
        try {
            administrador = autenticacao.autenticarAdministrador(email, senha);
        } catch (PersistenciaException e) {
            System.out.println("Erro: " + e.getMessage());
            return;
        }
        System.out.println("Bem-vindo, " + administrador.getNomeAdmin() + ".");
        menu(administrador);
    }

    private void menu(Administrador administrador) {
        int opcao;
        do {
            System.out.println();
            System.out.println("--- Painel administrativo ---");
            System.out.println("1 - Blocos");
            System.out.println("2 - Andares");
            System.out.println("3 - Pontos do mapa");
            System.out.println("4 - Trechos");
            System.out.println("5 - Professores");
            System.out.println("6 - Administradores");
            System.out.println("0 - Sair");
            opcao = Entrada.inteiro("Opcao: ");
            try {
                switch (opcao) {
                    case 1 -> menuBlocos();
                    case 2 -> menuAndares();
                    case 3 -> menuPontos();
                    case 4 -> menuTrechos();
                    case 5 -> menuProfessores();
                    case 6 -> menuAdministradores(administrador);
                    case 0 -> System.out.println("Sessao encerrada.");
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (PersistenciaException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void menuBlocos() {
        int opcao;
        do {
            System.out.println();
            System.out.println("--- Blocos ---");
            System.out.println("1 - Listar  2 - Cadastrar  3 - Alterar  4 - Excluir  0 - Voltar");
            opcao = Entrada.inteiro("Opcao: ");
            try {
                switch (opcao) {
                    case 1 -> blocos.listar().forEach(System.out::println);
                    case 2 -> {
                        Bloco bloco = new Bloco(Entrada.texto("Nome do bloco: "));
                        blocos.inserir(bloco);
                        System.out.println("Bloco cadastrado.");
                    }
                    case 3 -> {
                        int id = Entrada.inteiro("Id do bloco: ");
                        Bloco bloco = blocos.buscarPorId(id);
                        if (bloco == null) {
                            System.out.println("Bloco nao encontrado.");
                            break;
                        }
                        bloco.setNomeBloco(Entrada.textoOpcional("Nome do bloco", bloco.getNomeBloco()));
                        blocos.atualizar(bloco);
                        System.out.println("Bloco alterado.");
                    }
                    case 4 -> {
                        int id = Entrada.inteiro("Id do bloco: ");
                        blocos.excluir(id);
                        System.out.println("Bloco excluido.");
                    }
                    case 0 -> System.out.println("Voltando.");
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (PersistenciaException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void menuAndares() {
        int opcao;
        do {
            System.out.println();
            System.out.println("--- Andares ---");
            System.out.println("1 - Listar  2 - Cadastrar  3 - Alterar  4 - Excluir  0 - Voltar");
            opcao = Entrada.inteiro("Opcao: ");
            try {
                switch (opcao) {
                    case 1 -> andares.listar().forEach(System.out::println);
                    case 2 -> {
                        blocos.listar().forEach(System.out::println);
                        Andar andar = new Andar(
                            Entrada.texto("Nome do andar: "),
                            Entrada.inteiro("Nivel: "),
                            Entrada.texto("Imagem do mapa (Enter para nenhuma): "),
                            Entrada.inteiro("Id do bloco: "));
                        andares.inserir(andar);
                        System.out.println("Andar cadastrado.");
                    }
                    case 3 -> {
                        int id = Entrada.inteiro("Id do andar: ");
                        Andar andar = andares.buscarPorId(id);
                        if (andar == null) {
                            System.out.println("Andar nao encontrado.");
                            break;
                        }
                        andar.setNome(Entrada.textoOpcional("Nome", andar.getNome()));
                        andar.setNivel(Entrada.inteiro("Nivel [" + andar.getNivel() + "]: "));
                        andar.setImagemMapa(Entrada.textoOpcional("Imagem do mapa", andar.getImagemMapa()));
                        andar.setIdBloco(Entrada.inteiro("Id do bloco [" + andar.getIdBloco() + "]: "));
                        andares.atualizar(andar);
                        System.out.println("Andar alterado.");
                    }
                    case 4 -> {
                        int id = Entrada.inteiro("Id do andar: ");
                        andares.excluir(id);
                        System.out.println("Andar excluido.");
                    }
                    case 0 -> System.out.println("Voltando.");
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (PersistenciaException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void menuPontos() {
        int opcao;
        do {
            System.out.println();
            System.out.println("--- Pontos do mapa ---");
            System.out.println("1 - Listar  2 - Cadastrar  3 - Alterar  4 - Excluir  0 - Voltar");
            opcao = Entrada.inteiro("Opcao: ");
            try {
                switch (opcao) {
                    case 1 -> pontos.listar().forEach(System.out::println);
                    case 2 -> {
                        andares.listar().forEach(System.out::println);
                        Ponto ponto = new Ponto(
                            Entrada.inteiro("Id do andar: "),
                            Entrada.texto("Nome do ponto: "),
                            TipoPonto.de(Entrada.texto("Tipo (SALA/PASSAGEM/ACESSO): ")),
                            Entrada.decimal("Coordenada X: "),
                            Entrada.decimal("Coordenada Y: "));
                        pontos.inserir(ponto);
                        System.out.println("Ponto cadastrado.");
                    }
                    case 3 -> {
                        int id = Entrada.inteiro("Id do ponto: ");
                        Ponto ponto = pontos.buscarPorId(id);
                        if (ponto == null) {
                            System.out.println("Ponto nao encontrado.");
                            break;
                        }
                        ponto.setNome(Entrada.textoOpcional("Nome", ponto.getNome()));
                        ponto.setTipo(TipoPonto.de(Entrada.textoOpcional("Tipo", ponto.getTipo().name())));
                        ponto.setIdAndar(Entrada.inteiro("Id do andar [" + ponto.getIdAndar() + "]: "));
                        ponto.setCoordenadaX(Entrada.decimal("Coordenada X: "));
                        ponto.setCoordenadaY(Entrada.decimal("Coordenada Y: "));
                        pontos.atualizar(ponto);
                        System.out.println("Ponto alterado.");
                    }
                    case 4 -> {
                        int id = Entrada.inteiro("Id do ponto: ");
                        pontos.excluir(id);
                        System.out.println("Ponto excluido.");
                    }
                    case 0 -> System.out.println("Voltando.");
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (PersistenciaException e) {
                System.out.println("Erro: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void menuTrechos() {
        int opcao;
        do {
            System.out.println();
            System.out.println("--- Trechos ---");
            System.out.println("1 - Listar  2 - Cadastrar  3 - Alterar  4 - Alterar cobertura/interdicao  5 - Excluir  0 - Voltar");
            opcao = Entrada.inteiro("Opcao: ");
            try {
                switch (opcao) {
                    case 1 -> trechos.listar().forEach(System.out::println);
                    case 2 -> {
                        pontos.listar().forEach(System.out::println);
                        Trecho trecho = new Trecho(
                            Entrada.inteiro("Id do ponto A: "),
                            Entrada.inteiro("Id do ponto B: "),
                            Entrada.decimal("Distancia em metros: "),
                            Entrada.sim("Possui escadas"),
                            Entrada.sim("Usa elevador"),
                            Entrada.sim("Coberto"),
                            Entrada.sim("Interditado"));
                        trechos.inserir(trecho);
                        System.out.println("Trecho cadastrado.");
                    }
                    case 3 -> {
                        int id = Entrada.inteiro("Id do trecho: ");
                        Trecho trecho = trechos.buscarPorId(id);
                        if (trecho == null) {
                            System.out.println("Trecho nao encontrado.");
                            break;
                        }
                        trecho.setIdPontoA(Entrada.inteiro("Id do ponto A [" + trecho.getIdPontoA() + "]: "));
                        trecho.setIdPontoB(Entrada.inteiro("Id do ponto B [" + trecho.getIdPontoB() + "]: "));
                        trecho.setDistanciaMetros(Entrada.decimal("Distancia em metros: "));
                        trecho.setPossuiEscadas(Entrada.sim("Possui escadas"));
                        trecho.setUsaElevador(Entrada.sim("Usa elevador"));
                        trecho.setCoberto(Entrada.sim("Coberto"));
                        trecho.setInterditado(Entrada.sim("Interditado"));
                        trechos.atualizar(trecho);
                        System.out.println("Trecho alterado.");
                    }
                    case 4 -> {
                        int id = Entrada.inteiro("Id do trecho: ");
                        Trecho trecho = trechos.buscarPorId(id);
                        if (trecho == null) {
                            System.out.println("Trecho nao encontrado.");
                            break;
                        }
                        System.out.println("Situacao atual: " + trecho);
                        boolean coberto = Entrada.sim("Coberto");
                        boolean interditado = Entrada.sim("Interditado");
                        trechos.atualizarCondicoes(id, coberto, interditado);
                        System.out.println("Condicoes do trecho atualizadas.");
                    }
                    case 5 -> {
                        int id = Entrada.inteiro("Id do trecho: ");
                        trechos.excluir(id);
                        System.out.println("Trecho excluido.");
                    }
                    case 0 -> System.out.println("Voltando.");
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (PersistenciaException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void menuProfessores() {
        int opcao;
        do {
            System.out.println();
            System.out.println("--- Professores ---");
            System.out.println("1 - Listar  2 - Cadastrar  3 - Alterar  4 - Excluir  0 - Voltar");
            opcao = Entrada.inteiro("Opcao: ");
            try {
                switch (opcao) {
                    case 1 -> professores.listar().forEach(System.out::println);
                    case 2 -> {
                        Professor professor = new Professor(
                            Entrada.texto("Nome: "),
                            Entrada.texto("Login: "),
                            Senha.hash(Entrada.texto("Senha: ")));
                        professor.setFotoPerfil(Entrada.texto("Foto de perfil (Enter para nenhuma): "));
                        professores.inserir(professor);
                        System.out.println("Professor cadastrado.");
                    }
                    case 3 -> {
                        int id = Entrada.inteiro("Id do professor: ");
                        Professor professor = professores.buscarPorId(id);
                        if (professor == null) {
                            System.out.println("Professor nao encontrado.");
                            break;
                        }
                        professor.setNome(Entrada.textoOpcional("Nome", professor.getNome()));
                        professor.setLogin(Entrada.textoOpcional("Login", professor.getLogin()));
                        professor.setFotoPerfil(Entrada.textoOpcional("Foto", professor.getFotoPerfil()));
                        professores.atualizar(professor);
                        System.out.println("Professor alterado.");
                    }
                    case 4 -> {
                        int id = Entrada.inteiro("Id do professor: ");
                        professores.excluir(id);
                        System.out.println("Professor excluido. Salas e trechos foram mantidos.");
                    }
                    case 0 -> System.out.println("Voltando.");
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (PersistenciaException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void menuAdministradores(Administrador administrador) {
        int opcao;
        do {
            System.out.println();
            System.out.println("--- Administradores ---");
            System.out.println("1 - Listar  2 - Cadastrar  3 - Alterar  4 - Excluir  0 - Voltar");
            opcao = Entrada.inteiro("Opcao: ");
            try {
                switch (opcao) {
                    case 1 -> administradores.listar().forEach(System.out::println);
                    case 2 -> {
                        Administrador novo = new Administrador(
                            Entrada.texto("Nome: "),
                            Entrada.texto("E-mail: "),
                            Senha.hash(Entrada.texto("Senha: ")));
                        administradores.inserir(novo);
                        System.out.println("Administrador cadastrado.");
                    }
                    case 3 -> {
                        int id = Entrada.inteiro("Id do administrador: ");
                        List<Administrador> lista = administradores.listar();
                        Administrador alvo = lista.stream()
                            .filter(a -> a.getIdAdmin() == id).findFirst().orElse(null);
                        if (alvo == null) {
                            System.out.println("Administrador nao encontrado.");
                            break;
                        }
                        alvo.setNomeAdmin(Entrada.textoOpcional("Nome", alvo.getNomeAdmin()));
                        alvo.setEmailAdmin(Entrada.textoOpcional("E-mail", alvo.getEmailAdmin()));
                        String senha = Entrada.texto("Nova senha (Enter para manter): ");
                        if (!senha.isEmpty()) {
                            alvo.setSenhaAdmin(Senha.hash(senha));
                        }
                        administradores.atualizar(alvo);
                        System.out.println("Administrador alterado.");
                    }
                    case 4 -> {
                        int id = Entrada.inteiro("Id do administrador: ");
                        if (id == administrador.getIdAdmin()) {
                            System.out.println("Nao e permitido excluir o administrador em uso.");
                            break;
                        }
                        administradores.excluir(id);
                        System.out.println("Administrador excluido.");
                    }
                    case 0 -> System.out.println("Voltando.");
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (PersistenciaException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }
}
