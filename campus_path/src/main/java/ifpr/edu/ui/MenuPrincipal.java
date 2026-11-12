package ifpr.edu.ui;

import ifpr.edu.dao.SnapshotService;
import ifpr.edu.db.PersistenciaException;

public class MenuPrincipal {

    private final MenuAluno aluno = new MenuAluno();
    private final MenuProfessor professor = new MenuProfessor();
    private final MenuAdmin admin = new MenuAdmin();
    private final SnapshotService snapshot;

    public MenuPrincipal(SnapshotService snapshot) {
        this.snapshot = snapshot;
    }

    public void abrir() {
        int opcao;
        do {
            System.out.println();
            System.out.println("=== CampusPath - IFPR Cascavel ===");
            System.out.println("1 - Area do aluno (sem login)");
            System.out.println("2 - Acesso do professor");
            System.out.println("3 - Acesso do administrador");
            System.out.println("0 - Sair e salvar o estado no repositorio");
            opcao = Entrada.inteiro("Opcao: ");
            try {
                switch (opcao) {
                    case 1 -> aluno.abrir();
                    case 2 -> professor.abrir();
                    case 3 -> admin.abrir();
                    case 0 -> salvar();
                    default -> System.out.println("Opcao invalida.");
                }
            } catch (PersistenciaException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private void salvar() {
        snapshot.exportar();
        System.out.println("Estado salvo em " + snapshot.getArquivo().toAbsolutePath());
        System.out.println("Faca o commit desse arquivo para levar o estado a outro computador.");
    }
}
