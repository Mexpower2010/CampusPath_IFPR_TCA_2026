package ifpr.edu.view;

import ifpr.edu.model.Ponto;
import ifpr.edu.model.TipoPonto;
import ifpr.edu.model.Trecho;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class ComponenteMapa extends JPanel {

    private static final int MARGEM = 46;
    private static final int RAIO = 9;

    private List<Ponto> pontos = new ArrayList<>();
    private List<Trecho> trechos = new ArrayList<>();
    private List<Ponto> caminho = new ArrayList<>();
    private final Map<Integer, String> professores = new HashMap<>();
    private Consumer<Ponto> aoClicar;
    private Integer idSelecionado;
    private double minX = 0;
    private double maxX = 100;
    private double minY = 0;
    private double maxY = 100;

    public ComponenteMapa() {
        setBackground(Estilo.PAINEL);
        setPreferredSize(new Dimension(660, 440));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
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
    }

    public void setPontos(List<Ponto> pontos) {
        this.pontos = pontos == null ? new ArrayList<>() : pontos;
        recalcularLimites();
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

    public void setProfessores(Map<Integer, String> professores) {
        this.professores.clear();
        if (professores != null) {
            this.professores.putAll(professores);
        }
        repaint();
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
        double faixa = Math.max(1e-6, maxX - minX);
        return (int) Math.round(MARGEM + (valor - minX) / faixa * Math.max(1, getWidth() - 2 * MARGEM));
    }

    private int py(Double y) {
        double valor = y == null ? minY : y;
        double faixa = Math.max(1e-6, maxY - minY);
        return (int) Math.round(MARGEM + (valor - minY) / faixa * Math.max(1, getHeight() - 2 * MARGEM));
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

        Map<Integer, Ponto> porId = new HashMap<>();
        for (Ponto ponto : pontos) {
            porId.put(ponto.getIdPonto(), ponto);
        }
        Set<String> arestasCaminho = arestasDoCaminho();

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

        FontMetrics metricas = g2.getFontMetrics(Estilo.PEQUENA);
        g2.setFont(Estilo.PEQUENA);

        for (int indice = 0; indice < pontos.size(); indice++) {
            Ponto ponto = pontos.get(indice);
            int x = px(ponto.getCoordenadaX());
            int y = py(ponto.getCoordenadaY());
            boolean rotuloAcima = indice % 2 == 1;

            if (professores.containsKey(ponto.getIdPonto())) {
                g2.setColor(Estilo.OK);
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

            String rotulo = ponto.getNome();
            int largura = metricas.stringWidth(rotulo);
            g2.setColor(Estilo.TEXTO);
            g2.drawString(rotulo, x - largura / 2, rotuloAcima ? y - RAIO - 8 : y + RAIO + 14);

            String professor = professores.get(ponto.getIdPonto());
            if (professor != null) {
                int larguraProfessor = metricas.stringWidth(professor);
                g2.setColor(Estilo.OK);
                g2.drawString(professor, x - larguraProfessor / 2,
                    rotuloAcima ? y + RAIO + 14 : y - RAIO - 8);
            }
        }

        if (pontos.isEmpty()) {
            g2.setColor(Estilo.TEXTO_FRACO);
            g2.setFont(Estilo.CORPO);
            g2.drawString("Nenhum ponto cadastrado neste andar.", MARGEM, MARGEM);
        }

        g2.dispose();
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
