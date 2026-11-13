package ifpr.edu;

import ifpr.edu.api.ServidorHttp;
import ifpr.edu.dao.SnapshotService;
import ifpr.edu.db.PersistenciaException;
import ifpr.edu.ui.MenuPrincipal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static final int PORTA_PADRAO = 8080;

    public static void main(String[] args) {
        String modo = args.length > 0 ? args[0] : "--web";
        try {
            if ("--console".equals(modo)) {
                console();
            } else if ("--web".equals(modo)) {
                web(args.length > 1 ? Integer.parseInt(args[1]) : PORTA_PADRAO);
            } else {
                System.out.println("Uso: java ifpr.edu.Main [--web [porta] | --console]");
            }
        } catch (PersistenciaException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Erro: porta invalida.");
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }

    private static void console() {
        SnapshotService snapshot = new SnapshotService();
        if (snapshot.sincronizar()) {
            System.out.println("Estado sincronizado a partir de " + snapshot.getArquivo());
        }
        new MenuPrincipal(snapshot).abrir();
    }

    private static void web(int porta) throws IOException {
        Path raiz = localizarSite();
        ServidorHttp servidor = new ServidorHttp(porta, raiz);
        servidor.iniciar();
        System.out.println("CampusPath no ar em http://localhost:" + servidor.getPorta());
        System.out.println("Arquivos estaticos servidos de " + servidor.getRaiz());
        System.out.println("API em http://localhost:" + servidor.getPorta() + "/api/saude");
        System.out.println("Pressione Ctrl+C para encerrar.");
    }

    private static Path localizarSite() {
        Path[] candidatos = {
            Paths.get("web"),
            Paths.get("..", "web")
        };
        for (Path candidato : candidatos) {
            if (Files.isDirectory(candidato)) {
                return candidato;
            }
        }
        throw new PersistenciaException(
            "Pasta do site nao encontrada. Execute a partir da raiz do repositorio ou de campus_path.");
    }
}
