package ifpr.edu.api;

import java.util.LinkedHashMap;
import java.util.Map;

public final class Json {

    private Json() {
    }

    public static String escrever(Object valor) {
        StringBuilder sb = new StringBuilder();
        escrever(valor, sb);
        return sb.toString();
    }

    private static void escrever(Object valor, StringBuilder sb) {
        if (valor == null) {
            sb.append("null");
        } else if (valor instanceof String texto) {
            escreverTexto(texto, sb);
        } else if (valor instanceof Boolean || valor instanceof Number) {
            sb.append(valor);
        } else if (valor instanceof Map<?, ?> mapa) {
            sb.append('{');
            boolean primeiro = true;
            for (Map.Entry<?, ?> entrada : mapa.entrySet()) {
                if (!primeiro) {
                    sb.append(',');
                }
                primeiro = false;
                escreverTexto(String.valueOf(entrada.getKey()), sb);
                sb.append(':');
                escrever(entrada.getValue(), sb);
            }
            sb.append('}');
        } else if (valor instanceof Iterable<?> lista) {
            sb.append('[');
            boolean primeiro = true;
            for (Object item : lista) {
                if (!primeiro) {
                    sb.append(',');
                }
                primeiro = false;
                escrever(item, sb);
            }
            sb.append(']');
        } else {
            escreverTexto(String.valueOf(valor), sb);
        }
    }

    private static void escreverTexto(String texto, StringBuilder sb) {
        sb.append('"');
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
    }

    public static Object ler(String json) {
        if (json == null || json.isBlank()) {
            throw new ErroHttp(400, "Corpo da requisicao vazio.");
        }
        Leitor leitor = new Leitor(json);
        Object valor = leitor.lerValor();
        leitor.pularEspacos();
        if (!leitor.fim()) {
            throw new ErroHttp(400, "JSON invalido: conteudo extra apos o valor.");
        }
        return valor;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> objeto(Object valor) {
        if (valor instanceof Map<?, ?> mapa) {
            return (Map<String, Object>) mapa;
        }
        throw new ErroHttp(400, "Esperado um objeto JSON.");
    }

    public static String texto(Map<String, Object> corpo, String chave) {
        Object valor = corpo.get(chave);
        return valor == null ? null : String.valueOf(valor);
    }

    public static String textoObrigatorio(Map<String, Object> corpo, String chave) {
        String valor = texto(corpo, chave);
        if (valor == null || valor.isBlank()) {
            throw new ErroHttp(400, "Campo obrigatorio ausente: " + chave + ".");
        }
        return valor.trim();
    }

    public static Integer inteiro(Map<String, Object> corpo, String chave) {
        Object valor = corpo.get(chave);
        if (valor == null) {
            return null;
        }
        if (valor instanceof Number numero) {
            return numero.intValue();
        }
        try {
            return Integer.valueOf(String.valueOf(valor).trim());
        } catch (NumberFormatException e) {
            throw new ErroHttp(400, "Campo " + chave + " deve ser um numero inteiro.");
        }
    }

    public static int inteiroObrigatorio(Map<String, Object> corpo, String chave) {
        Integer valor = inteiro(corpo, chave);
        if (valor == null) {
            throw new ErroHttp(400, "Campo obrigatorio ausente: " + chave + ".");
        }
        return valor;
    }

    public static Double decimal(Map<String, Object> corpo, String chave) {
        Object valor = corpo.get(chave);
        if (valor == null) {
            return null;
        }
        if (valor instanceof Number numero) {
            return numero.doubleValue();
        }
        try {
            return Double.valueOf(String.valueOf(valor).trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new ErroHttp(400, "Campo " + chave + " deve ser um numero.");
        }
    }

    public static double decimalObrigatorio(Map<String, Object> corpo, String chave) {
        Double valor = decimal(corpo, chave);
        if (valor == null) {
            throw new ErroHttp(400, "Campo obrigatorio ausente: " + chave + ".");
        }
        return valor;
    }

    public static boolean booleano(Map<String, Object> corpo, String chave, boolean padrao) {
        Object valor = corpo.get(chave);
        if (valor == null) {
            return padrao;
        }
        if (valor instanceof Boolean b) {
            return b;
        }
        return Boolean.parseBoolean(String.valueOf(valor).trim());
    }

    public static Map<String, Object> mapa(Object... chavesEValores) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        for (int i = 0; i + 1 < chavesEValores.length; i += 2) {
            mapa.put(String.valueOf(chavesEValores[i]), chavesEValores[i + 1]);
        }
        return mapa;
    }

    private static final class Leitor {

        private final String texto;
        private int posicao;

        Leitor(String texto) {
            this.texto = texto;
        }

        boolean fim() {
            return posicao >= texto.length();
        }

        void pularEspacos() {
            while (!fim() && Character.isWhitespace(texto.charAt(posicao))) {
                posicao++;
            }
        }

        Object lerValor() {
            pularEspacos();
            if (fim()) {
                throw new ErroHttp(400, "JSON invalido: fim inesperado.");
            }
            char c = texto.charAt(posicao);
            return switch (c) {
                case '{' -> lerObjeto();
                case '[' -> lerLista();
                case '"' -> lerTexto();
                case 't' -> lerLiteral("true", Boolean.TRUE);
                case 'f' -> lerLiteral("false", Boolean.FALSE);
                case 'n' -> lerLiteral("null", null);
                default -> lerNumero();
            };
        }

        private Object lerLiteral(String esperado, Object valor) {
            if (!texto.startsWith(esperado, posicao)) {
                throw new ErroHttp(400, "JSON invalido na posicao " + posicao + ".");
            }
            posicao += esperado.length();
            return valor;
        }

        private Map<String, Object> lerObjeto() {
            Map<String, Object> mapa = new LinkedHashMap<>();
            posicao++;
            pularEspacos();
            if (!fim() && texto.charAt(posicao) == '}') {
                posicao++;
                return mapa;
            }
            while (true) {
                pularEspacos();
                String chave = lerTexto();
                pularEspacos();
                if (fim() || texto.charAt(posicao) != ':') {
                    throw new ErroHttp(400, "JSON invalido: esperado ':' na posicao " + posicao + ".");
                }
                posicao++;
                mapa.put(chave, lerValor());
                pularEspacos();
                if (fim()) {
                    throw new ErroHttp(400, "JSON invalido: objeto nao terminado.");
                }
                char c = texto.charAt(posicao++);
                if (c == '}') {
                    return mapa;
                }
                if (c != ',') {
                    throw new ErroHttp(400, "JSON invalido: esperado ',' ou '}' na posicao " + (posicao - 1) + ".");
                }
            }
        }

        private java.util.List<Object> lerLista() {
            java.util.List<Object> lista = new java.util.ArrayList<>();
            posicao++;
            pularEspacos();
            if (!fim() && texto.charAt(posicao) == ']') {
                posicao++;
                return lista;
            }
            while (true) {
                lista.add(lerValor());
                pularEspacos();
                if (fim()) {
                    throw new ErroHttp(400, "JSON invalido: lista nao terminada.");
                }
                char c = texto.charAt(posicao++);
                if (c == ']') {
                    return lista;
                }
                if (c != ',') {
                    throw new ErroHttp(400, "JSON invalido: esperado ',' ou ']' na posicao " + (posicao - 1) + ".");
                }
            }
        }

        private String lerTexto() {
            if (fim() || texto.charAt(posicao) != '"') {
                throw new ErroHttp(400, "JSON invalido: esperado texto na posicao " + posicao + ".");
            }
            posicao++;
            StringBuilder sb = new StringBuilder();
            while (true) {
                if (fim()) {
                    throw new ErroHttp(400, "JSON invalido: texto nao terminado.");
                }
                char c = texto.charAt(posicao++);
                if (c == '"') {
                    return sb.toString();
                }
                if (c != '\\') {
                    sb.append(c);
                    continue;
                }
                if (fim()) {
                    throw new ErroHttp(400, "JSON invalido: escape incompleto.");
                }
                char escape = texto.charAt(posicao++);
                switch (escape) {
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case 'u' -> {
                        if (posicao + 4 > texto.length()) {
                            throw new ErroHttp(400, "JSON invalido: escape unicode incompleto.");
                        }
                        String hex = texto.substring(posicao, posicao + 4);
                        posicao += 4;
                        try {
                            sb.append((char) Integer.parseInt(hex, 16));
                        } catch (NumberFormatException e) {
                            throw new ErroHttp(400, "JSON invalido: escape unicode invalido.");
                        }
                    }
                    default -> throw new ErroHttp(400, "JSON invalido: escape desconhecido.");
                }
            }
        }

        private Object lerNumero() {
            int inicio = posicao;
            if (!fim() && (texto.charAt(posicao) == '-' || texto.charAt(posicao) == '+')) {
                posicao++;
            }
            boolean decimal = false;
            while (!fim()) {
                char c = texto.charAt(posicao);
                if (Character.isDigit(c)) {
                    posicao++;
                } else if (c == '.' || c == 'e' || c == 'E' || c == '-' || c == '+') {
                    decimal = decimal || c == '.' || c == 'e' || c == 'E';
                    posicao++;
                } else {
                    break;
                }
            }
            String numero = texto.substring(inicio, posicao);
            if (numero.isEmpty()) {
                throw new ErroHttp(400, "JSON invalido: numero esperado na posicao " + inicio + ".");
            }
            try {
                return decimal ? (Object) Double.valueOf(numero) : (Object) Long.valueOf(numero);
            } catch (NumberFormatException e) {
                throw new ErroHttp(400, "JSON invalido: numero malformado.");
            }
        }
    }
}
