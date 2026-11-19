package ifpr.edu.view;

import ifpr.edu.model.Ponto;
import ifpr.edu.model.Professor;
import ifpr.edu.model.TipoPonto;
import ifpr.edu.model.Trecho;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class ComponenteMapa extends JPanel {

    private static final int MARGEM = 40;
    private static final int RAIO = 8;
    private static final int FOLGA_BLOCO = 14;
    private static final int FOTO = 26;
    private static final int LIMITE_FOTOS = 3;
    private static final int ARRASTE_MINIMO = 4;

    private List<Ponto> pontos = new ArrayList<>();
    private List<Trecho> trechos = new ArrayList<>();
    private List<Ponto> caminho = new ArrayList<>();
    private final Map<Integer, List<Professor>> professores = new HashMap<>();
    private final Set<Integer> pontosVerticais = new HashSet<>();
    private Consumer<Ponto> aoClicar;
    private Integer idSelecionado;
    private boolean panHabilitado;
    private double minX = 0;
    private double maxX = 100;
    private double minY = 0;
    private double maxY = 100;
    private double escala = 1;
    private double baseX = MARGEM;
    private double baseY = MARGEM;
    private double panX;
    private double panY;
    private int pressaoX;
    private int pressaoY;
    private double panInicialX;
    private double panInicialY;
    private boolean arrastando;

    public ComponenteMapa() {
        setBackground(Estilo.PAINEL);
        setPreferredSize(new Dimension(660, 440));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evento) {
                pressaoX = evento.getX();
                pressaoY = evento.getY();
                panInicialX = panX;
                panInicialY = panY;
                arrastando = false;
            }

            @Override
            public void mouseReleased(MouseEvent evento) {
                setCursor(Cursor.getDefaultCursor());
                if (arrastando) {
                    arrastando = false;
                    return;
                }
                Ponto ponto = pontoEm(evento.getX(), evento.getY());
                if (ponto != null) {
                    idSelecionado = ponto.getIdPonto();
                    repaint();
                    if (aoClicar != null) {
                        aoClicar.accept(ponto);
                    }
                }
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent evento) {
                int dx = evento.getX() - pressaoX;
                int dy = evento.getY() - pressaoY;
                if (!arrastando && Math.hypot(dx, dy) > ARRASTE_MINIMO) {
                    arrastando = true;
                    if (panHabilitado) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }
                }
                if (arrastando && panHabilitado) {
                    panX = panInicialX + dx;
                    panY = panInicialY + dy;
                    repaint();
                }
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evento) {
                ajustarVista();
                repaint();
            }
        });
    }

    public void setPontos(List<Ponto> pontos) {
        this.pontos = pontos == null ? new ArrayList<>() : pontos;
        ajustarVista();
        repaint();
    }

    public void setTrechos(List<Trecho> trechos) {
        this.trechos = trechos == null ? new ArrayList<>() : trechos;
        repaint();
    }

    public void setCaminho(List<Ponto> caminho) {
        this.caminho = caminho == null ? new ArrayList<>() : caminho;
        repaint();
    }

    public void setProfessores(Map<Integer, List<Professor>> professores) {
        this.professores.clear();
        if (professores != null) {
            this.professores.putAll(professores);
        }
        repaint();
    }

    public void setPontosVerticais(Set<Integer> pontosVerticais) {
        this.pontosVerticais.clear();
        if (pontosVerticais != null) {
            this.pontosVerticais.addAll(pontosVerticais);
        }
        repaint();
    }

    public void setPanHabilitado(boolean panHabilitado) {
        this.panHabilitado = panHabilitado;
    }

    public void setAoClicar(Consumer<Ponto> aoClicar) {
        this.aoClicar = aoClicar;
    }

    public void limparSelecao() {
        idSelecionado = null;
        repaint();
    }

    private int px(Double x) {
        double valor = x == null ? minX : x;
        return (int) Math.round(baseX + (valor - minX) * escala + panX);
    }

    private int py(Double y) {
        double valor = y == null ? minY : y;
        return (int) Math.round(baseY + (valor - minY) * escala + panY);
    }

    private void ajustarVista() {
        recalcularLimites();
        panX = 0;
        panY = 0;
        int largura = getWidth();
        int altura = getHeight();
        if (largura <= 0 || altura <= 0) {
            escala = 1;
            baseX = MARGEM;
            baseY = MARGEM;
            return;
        }
        double faixaX = Math.max(1e-6, maxX - minX);
        double faixaY = Math.max(1e-6, maxY - minY);
        double disponivelX = Math.max(1, largura - 2 * MARGEM);
        double disponivelY = Math.max(1, altura - 2 * MARGEM);
        escala = Math.min(disponivelX / faixaX, disponivelY / faixaY);
        baseX = MARGEM + (disponivelX - faixaX * escala) / 2.0;
        baseY = MARGEM + (disponivelY - faixaY * escala) / 2.0;
    }

    private void recalcularLimites() {
        if (pontos.isEmpty()) {
            minX = 0;
            maxX = 100;
            minY = 0;
            maxY = 100;
            return;
        }
        double menorX = Double.MAX_VALUE;
        double maiorX = -Double.MAX_VALUE;
        double menorY = Double.MAX_VALUE;
        double maiorY = -Double.MAX_VALUE;
        for (Ponto ponto : pontos) {
            double x = ponto.getCoordenadaX() == null ? 0 : ponto.getCoordenadaX();
            double y = ponto.getCoordenadaY() == null ? 0 : ponto.getCoordenadaY();
            menorX = Math.min(menorX, x);
            maiorX = Math.max(maiorX, x);
            menorY = Math.min(menorY, y);
            maiorY = Math.max(maiorY, y);
        }
        double folgaX = Math.max(12, (maiorX - menorX) * 0.18);
        double folgaY = Math.max(12, (maiorY - menorY) * 0.18);
        minX = menorX - folgaX;
        maxX = maiorX + folgaX;
        minY = menorY - folgaY;
        maxY = maiorY + folgaY;
    }

    private Ponto pontoEm(int x, int y) {
        for (Ponto ponto : pontos) {
            int dx = px(ponto.getCoordenadaX()) - x;
            int dy = py(ponto.getCoordenadaY()) - y;
            if (dx * dx + dy * dy <= (RAIO + 5) * (RAIO + 5)) {
                return ponto;
            }
        }
        return null;
    }

    private static String chaveAresta(int a, int b) {
        return Math.min(a, b) + "-" + Math.max(a, b);
    }

    private Set<String> arestasDoCaminho() {
        Set<String> arestas = new HashSet<>();
        for (int i = 0; i + 1 < caminho.size(); i++) {
            arestas.add(chaveAresta(caminho.get(i).getIdPonto(), caminho.get(i + 1).getIdPonto()));
        }
        return arestas;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Map<Integer, Ponto> porId = new HashMap<>();
        for (Ponto ponto : pontos) {
            porId.put(ponto.getIdPonto(), ponto);
        }
        Set<String> arestasCaminho = arestasDoCaminho();
        List<Rectangle> ocupados = new ArrayList<>();
        List<String> nomesBlocos = new ArrayList<>();
        List<int[]> posicoesBlocos = new ArrayList<>();

        desenharBlocos(g2, ocupados, nomesBlocos, posicoesBlocos);

        for (Trecho trecho : trechos) {
            Ponto a = porId.get(trecho.getIdPontoA());
            Ponto b = porId.get(trecho.getIdPontoB());
            if (a == null || b == null) {
                continue;
            }
            boolean noCaminho = arestasCaminho.contains(chaveAresta(a.getIdPonto(), b.getIdPonto()));
            if (noCaminho) {
                g2.setColor(Estilo.DESTAQUE);
                g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            } else if (trecho.isInterditado()) {
                g2.setColor(Estilo.ERRO);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    10f, new float[] {6f, 6f}, 0f));
            } else if (trecho.isCoberto()) {
                g2.setColor(Estilo.OK);
                g2.setStroke(new BasicStroke(2f));
            } else {
                g2.setColor(Estilo.BORDA);
                g2.setStroke(new BasicStroke(2f));
            }
            g2.drawLine(px(a.getCoordenadaX()), py(a.getCoordenadaY()),
                px(b.getCoordenadaX()), py(b.getCoordenadaY()));
        }

        for (Ponto ponto : pontos) {
            int x = px(ponto.getCoordenadaX());
            int y = py(ponto.getCoordenadaY());

            if (pontosVerticais.contains(ponto.getIdPonto())) {
                g2.setColor(Estilo.AVISO);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(x - RAIO - 5, y - RAIO - 5, 2 * (RAIO + 5), 2 * (RAIO + 5));
            }
            if (idSelecionado != null && idSelecionado.equals(ponto.getIdPonto())) {
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(x - RAIO - 3, y - RAIO - 3, 2 * (RAIO + 3), 2 * (RAIO + 3));
            }

            g2.setColor(corDoPonto(ponto.getTipo()));
            g2.fillOval(x - RAIO, y - RAIO, 2 * RAIO, 2 * RAIO);
            ocupados.add(new Rectangle(x - RAIO - 1, y - RAIO - 1, 2 * RAIO + 2, 2 * RAIO + 2));

            Rectangle areaFotos = areaFotos(ponto, x, y);
            if (areaFotos != null) {
                ocupados.add(areaFotos);
            }
        }

        g2.setFont(Estilo.PEQUENA);
        FontMetrics metricas = g2.getFontMetrics(Estilo.PEQUENA);
        g2.setColor(Estilo.TEXTO_FRACO);
        for (int i = 0; i < nomesBlocos.size(); i++) {
            g2.drawString(nomesBlocos.get(i), posicoesBlocos.get(i)[0], posicoesBlocos.get(i)[1]);
        }
        for (int indice = 0; indice < pontos.size(); indice++) {
            Ponto ponto = pontos.get(indice);
            desenharRotulo(g2, metricas, ponto.getNome(), px(ponto.getCoordenadaX()),
                py(ponto.getCoordenadaY()), indice % 2 == 1, ocupados);
        }

        for (Ponto ponto : pontos) {
            desenharFotos(g2, ponto, px(ponto.getCoordenadaX()), py(ponto.getCoordenadaY()));
        }

        if (pontos.isEmpty()) {
            g2.setColor(Estilo.TEXTO_FRACO);
            g2.setFont(Estilo.CORPO);
            g2.drawString("Nenhum ponto cadastrado nesta vista.", MARGEM, MARGEM);
        }

        g2.dispose();
    }

    private void desenharBlocos(Graphics2D g2, List<Rectangle> ocupados, List<String> nomes,
                                List<int[]> posicoes) {
        Map<String, List<Ponto>> porBloco = new LinkedHashMap<>();
        for (Ponto ponto : pontos) {
            String bloco = ponto.getNomeBloco() == null ? "Bloco" : ponto.getNomeBloco();
            porBloco.computeIfAbsent(bloco, chave -> new ArrayList<>()).add(ponto);
        }
        g2.setFont(Estilo.PEQUENA);
        FontMetrics metricas = g2.getFontMetrics(Estilo.PEQUENA);
        for (Map.Entry<String, List<Ponto>> entrada : porBloco.entrySet()) {
            double menorX = Double.MAX_VALUE;
            double maiorX = -Double.MAX_VALUE;
            double menorY = Double.MAX_VALUE;
            double maiorY = -Double.MAX_VALUE;
            for (Ponto ponto : entrada.getValue()) {
                double x = ponto.getCoordenadaX() == null ? 0 : ponto.getCoordenadaX();
                double y = ponto.getCoordenadaY() == null ? 0 : ponto.getCoordenadaY();
                menorX = Math.min(menorX, x);
                maiorX = Math.max(maiorX, x);
                menorY = Math.min(menorY, y);
                maiorY = Math.max(maiorY, y);
            }
            int x1 = px(menorX - FOLGA_BLOCO);
            int y1 = py(menorY - FOLGA_BLOCO);
            int x2 = px(maiorX + FOLGA_BLOCO);
            int y2 = py(maiorY + FOLGA_BLOCO);
            int largura = Math.max(1, x2 - x1);
            int altura = Math.max(1, y2 - y1);
            g2.setColor(Estilo.PAINEL_CLARO);
            g2.fill(new RoundRectangle2D.Double(x1, y1, largura, altura, 18, 18));
            g2.setColor(Estilo.BORDA);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Double(x1, y1, largura, altura, 18, 18));
            nomes.add(entrada.getKey());
            posicoes.add(new int[] {x1 + 6, y1 + 13});
            ocupados.add(new Rectangle(x1 + 4, y1 + 1, metricas.stringWidth(entrada.getKey()) + 4,
                metricas.getAscent() + metricas.getDescent() + 2));
        }
    }

    private void desenharRotulo(Graphics2D g2, FontMetrics metricas, String rotulo, int x, int y,
                                boolean acimaPrimeiro, List<Rectangle> ocupados) {
        int largura = metricas.stringWidth(rotulo);
        int altura = metricas.getAscent() + metricas.getDescent();
        int acima = y - RAIO - 6 - metricas.getDescent();
        int abaixo = y + RAIO + 6 + metricas.getAscent();
        int acima2 = acima - altura - 2;
        int abaixo2 = abaixo + altura + 2;
        int lateral = y + metricas.getAscent() / 2 - 2;
        int[][] posicoes = acimaPrimeiro
            ? new int[][] {{x - largura / 2, acima}, {x - largura / 2, acima2},
                           {x - largura / 2, abaixo}, {x - largura / 2, abaixo2},
                           {x + RAIO + 4, lateral}, {x - RAIO - 4 - largura, lateral}}
            : new int[][] {{x - largura / 2, abaixo}, {x - largura / 2, abaixo2},
                           {x - largura / 2, acima}, {x - largura / 2, acima2},
                           {x + RAIO + 4, lateral}, {x - RAIO - 4 - largura, lateral}};
        for (int[] posicao : posicoes) {
            Rectangle area = new Rectangle(posicao[0] - 1, posicao[1] - metricas.getAscent(),
                largura + 2, altura + 2);
            if (colide(area, ocupados)) {
                continue;
            }
            g2.setColor(Estilo.TEXTO);
            g2.drawString(rotulo, posicao[0], posicao[1]);
            ocupados.add(area);
            return;
        }
    }

    private static boolean colide(Rectangle area, List<Rectangle> ocupados) {
        for (Rectangle outro : ocupados) {
            if (outro.intersects(area)) {
                return true;
            }
        }
        return false;
    }

    private Rectangle areaFotos(Ponto ponto, int x, int y) {
        List<Professor> presentes = professores.get(ponto.getIdPonto());
        if (presentes == null || presentes.isEmpty()) {
            return null;
        }
        int desenhadas = Math.min(presentes.size(), LIMITE_FOTOS);
        int largura = desenhadas * (FOTO + 2);
        if (presentes.size() > LIMITE_FOTOS) {
            largura += 16;
        }
        return new Rectangle(x + RAIO + 4, y - FOTO / 2, largura, FOTO);
    }

    private void desenharFotos(Graphics2D g2, Ponto ponto, int x, int y) {
        List<Professor> presentes = professores.get(ponto.getIdPonto());
        if (presentes == null || presentes.isEmpty()) {
            return;
        }
        int inicioX = x + RAIO + 4;
        int inicioY = y - FOTO / 2;
        int desenhadas = Math.min(presentes.size(), LIMITE_FOTOS);
        for (int i = 0; i < desenhadas; i++) {
            Professor professor = presentes.get(i);
            BufferedImage foto = FotoPerfil.imagem(professor.getFotoPerfil(), professor.getNome(), FOTO);
            g2.drawImage(foto, inicioX + i * (FOTO + 2), inicioY, null);
        }
        if (presentes.size() > LIMITE_FOTOS) {
            g2.setFont(Estilo.PEQUENA);
            g2.setColor(Estilo.TEXTO_FRACO);
            g2.drawString("+" + (presentes.size() - LIMITE_FOTOS),
                inicioX + desenhadas * (FOTO + 2), y + 4);
        }
    }

    private Color corDoPonto(TipoPonto tipo) {
        if (tipo == null) {
            return Estilo.TEXTO_FRACO;
        }
        return switch (tipo) {
            case SALA -> Estilo.DESTAQUE;
            case ACESSO -> Estilo.AVISO;
            case PASSAGEM -> Estilo.TEXTO_FRACO;
        };
    }
}
