package ifpr.edu;

import ifpr.edu.dao.SnapshotService;
import ifpr.edu.db.PersistenciaException;
import ifpr.edu.ui.MenuPrincipal;

public class Main {

    public static void main(String[] args) {
        SnapshotService snapshot = new SnapshotService();
        try {
            if (snapshot.sincronizar()) {
                System.out.println("Estado sincronizado a partir de " + snapshot.getArquivo());
            }
            new MenuPrincipal(snapshot).abrir();
        } catch (PersistenciaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
