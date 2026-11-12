package ifpr.edu.ui;

import java.util.Scanner;

public final class Entrada {

    private static final Scanner SCANNER = new Scanner(System.in);

    private Entrada() {
    }

    public static String texto(String rotulo) {
        System.out.print(rotulo);
        return SCANNER.nextLine().trim();
    }

    public static String textoOpcional(String rotulo, String atual) {
        System.out.print(rotulo + (atual == null ? "" : " [" + atual + "]") + ": ");
        String valor = SCANNER.nextLine().trim();
        return valor.isEmpty() ? atual : valor;
    }

    public static int inteiro(String rotulo) {
        while (true) {
            String valor = texto(rotulo);
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido, digite um numero inteiro.");
            }
        }
    }

    public static Integer inteiroOpcional(String rotulo) {
        String valor = texto(rotulo);
        if (valor.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(valor);
        } catch (NumberFormatException e) {
            System.out.println("Valor invalido, continuando sem informacao.");
            return null;
        }
    }

    public static double decimal(String rotulo) {
        while (true) {
            String valor = texto(rotulo).replace(',', '.');
            try {
                return Double.parseDouble(valor);
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido, digite um numero.");
            }
        }
    }

    public static boolean sim(String rotulo) {
        String valor = texto(rotulo + " (s/n): ");
        return valor.equalsIgnoreCase("s") || valor.equalsIgnoreCase("sim");
    }
}
