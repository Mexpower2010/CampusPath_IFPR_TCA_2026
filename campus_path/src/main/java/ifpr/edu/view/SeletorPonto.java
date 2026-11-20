package ifpr.edu.view;

import ifpr.edu.model.Ponto;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SeletorPonto extends JPanel {

    private static final String VAZIO = "Escolher...";

    private final String rotulo;
    private final JButton botao = new JButton(VAZIO);

    private List<Ponto> pontos = new ArrayList<>();
    private Ponto selecionado;
    private Runnable aoMudar;

    private JTextField campoBusca;
    private DefaultListModel<Ponto> modeloFiltrado;
    private JList<Ponto> listaFiltrada;
    private JDialog dialogo;

    public SeletorPonto(String rotulo) {
        super(new BorderLayout(0, 4));
        this.rotulo = rotulo;
        setBackground(Estilo.PAINEL);

        JLabel titulo = new JLabel(rotulo);
        titulo.setForeground(Estilo.TEXTO_FRACO);
        titulo.setFont(Estilo.PEQUENA);
        add(titulo, BorderLayout.NORTH);

        botao.setFont(Estilo.CORPO);
        botao.setForeground(Estilo.TEXTO);
        botao.setBackground(Estilo.PAINEL_CLARO);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setHorizontalAlignment(JButton.LEFT);
        botao.setBorder(new EmptyBorder(6, 8, 6, 8));
        botao.addActionListener(e -> abrirDialogo());
        add(botao, BorderLayout.CENTER);
    }

    public void setPontos(List<Ponto> pontos) {
        this.pontos = pontos == null ? new ArrayList<>() : new ArrayList<>(pontos);
        if (selecionado != null && !contem(selecionado)) {
            selecionado = null;
            atualizarBotao();
        }
    }

    public Ponto getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Ponto ponto) {
        selecionado = ponto;
        atualizarBotao();
        disparar();
    }

    public void setAoMudar(Runnable aoMudar) {
        this.aoMudar = aoMudar;
    }

    static String descrever(Ponto ponto) {
        String bloco = ponto.getNomeBloco() == null ? "bloco " + ponto.getIdAndar() : ponto.getNomeBloco();
        String andar = ponto.getNomeAndar() == null ? "andar " + ponto.getIdAndar() : ponto.getNomeAndar();
        return ponto.getNome() + " (" + bloco + " / " + andar + ")";
    }

    static boolean combina(String filtro, Ponto ponto) {
        if (ponto == null) {
            return false;
        }
        String alvo = normalizar(juntar(ponto.getNome(), ponto.getNomeBloco(), ponto.getNomeAndar()));
        String limpo = normalizar(filtro).trim();
        if (limpo.isEmpty()) {
            return true;
        }
        for (String palavra : limpo.split("\\s+")) {
            if (!alvo.contains(palavra)) {
                return false;
            }
        }
        return true;
    }

    private static String juntar(String... partes) {
        StringBuilder texto = new StringBuilder();
        for (String parte : partes) {
            if (parte != null && !parte.isBlank()) {
                texto.append(parte).append(' ');
            }
        }
        return texto.toString();
    }

    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toLowerCase(Locale.ROOT);
    }

    private void atualizarBotao() {
        botao.setText(selecionado == null ? VAZIO : descrever(selecionado));
    }

    private void disparar() {
        if (aoMudar != null) {
            aoMudar.run();
        }
    }

    private boolean contem(Ponto ponto) {
        for (Ponto item : pontos) {
            if (mesmoPonto(item, ponto)) {
                return true;
            }
        }
        return false;
    }

    private static boolean mesmoPonto(Ponto a, Ponto b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.getIdPonto() != null && b.getIdPonto() != null) {
            return a.getIdPonto().equals(b.getIdPonto());
        }
        return a == b;
    }

    private void abrirDialogo() {
        Window dono = SwingUtilities.getWindowAncestor(this);
        dialogo = new JDialog(dono, rotulo, Dialog.ModalityType.APPLICATION_MODAL);
        dialogo.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogo.add(montarConteudo(), BorderLayout.CENTER);
        dialogo.add(montarBotoes(), BorderLayout.SOUTH);

        campoBusca.registerKeyboardAction(e -> mover(1),
            KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), JComponent.WHEN_FOCUSED);
        campoBusca.registerKeyboardAction(e -> mover(-1),
            KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), JComponent.WHEN_FOCUSED);
        campoBusca.registerKeyboardAction(e -> confirmar(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);
        dialogo.getRootPane().registerKeyboardAction(e -> dialogo.dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        preencherLista("");
        dialogo.setSize(460, 420);
        dialogo.setLocationRelativeTo(this);
        SwingUtilities.invokeLater(campoBusca::requestFocusInWindow);
        dialogo.setVisible(true);
    }

    JPanel montarConteudo() {
        JPanel conteudo = new JPanel(new BorderLayout(0, 8));
        conteudo.setBackground(Estilo.PAINEL);
        conteudo.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel rotuloBusca = new JLabel("Digite para filtrar: sala, bloco ou andar");
        rotuloBusca.setForeground(Estilo.TEXTO_FRACO);
        rotuloBusca.setFont(Estilo.PEQUENA);

        campoBusca = new JTextField();
        campoBusca.setBackground(Estilo.PAINEL_CLARO);
        campoBusca.setForeground(Estilo.TEXTO);
        campoBusca.setCaretColor(Estilo.TEXTO);
        campoBusca.setFont(Estilo.CORPO);
        campoBusca.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Estilo.BORDA), new EmptyBorder(6, 8, 6, 8)));
        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent evento) {
                preencherLista(campoBusca.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent evento) {
                preencherLista(campoBusca.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent evento) {
                preencherLista(campoBusca.getText());
            }
        });

        JPanel topo = new JPanel(new BorderLayout(0, 4));
        topo.setBackground(Estilo.PAINEL);
        topo.add(rotuloBusca, BorderLayout.NORTH);
        topo.add(campoBusca, BorderLayout.CENTER);
        conteudo.add(topo, BorderLayout.NORTH);

        modeloFiltrado = new DefaultListModel<>();
        listaFiltrada = new JList<>(modeloFiltrado);
        listaFiltrada.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaFiltrada.setBackground(Estilo.PAINEL_CLARO);
        listaFiltrada.setForeground(Estilo.TEXTO);
        listaFiltrada.setFont(Estilo.CORPO);
        listaFiltrada.setFixedCellHeight(28);
        listaFiltrada.setCellRenderer(new RendererLista());
        listaFiltrada.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                if (evento.getClickCount() == 2 && listaFiltrada.getSelectedValue() != null) {
                    confirmar();
                }
            }
        });

        JScrollPane rolagem = new JScrollPane(listaFiltrada);
        rolagem.setBorder(BorderFactory.createLineBorder(Estilo.BORDA));
        rolagem.getViewport().setBackground(Estilo.PAINEL_CLARO);
        rolagem.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        rolagem.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        conteudo.add(rolagem, BorderLayout.CENTER);
        return conteudo;
    }

    private JPanel montarBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        painel.setBackground(Estilo.PAINEL);

        JButton cancelar = new JButton("Cancelar");
        cancelar.addActionListener(e -> dialogo.dispose());
        JButton escolher = new JButton("Escolher");
        escolher.addActionListener(e -> confirmar());

        painel.add(cancelar);
        painel.add(escolher);
        return painel;
    }

    private void preencherLista(String filtro) {
        if (modeloFiltrado == null) {
            return;
        }
        Ponto anterior = listaFiltrada.getSelectedValue();
        modeloFiltrado.clear();
        for (Ponto ponto : pontos) {
            if (combina(filtro, ponto)) {
                modeloFiltrado.addElement(ponto);
            }
        }
        Ponto alvo = anterior != null ? anterior : selecionado;
        int indice = -1;
        for (int i = 0; i < modeloFiltrado.size(); i++) {
            if (mesmoPonto(modeloFiltrado.get(i), alvo)) {
                indice = i;
                break;
            }
        }
        if (indice < 0 && !modeloFiltrado.isEmpty()) {
            indice = 0;
        }
        listaFiltrada.setSelectedIndex(indice);
        if (indice >= 0) {
            listaFiltrada.ensureIndexIsVisible(indice);
        }
    }

    private void mover(int passo) {
        if (modeloFiltrado == null || modeloFiltrado.isEmpty()) {
            return;
        }
        int atual = listaFiltrada.getSelectedIndex();
        int novo = Math.max(0, Math.min(modeloFiltrado.size() - 1, atual + passo));
        listaFiltrada.setSelectedIndex(novo);
        listaFiltrada.ensureIndexIsVisible(novo);
    }

    private void confirmar() {
        if (listaFiltrada == null) {
            return;
        }
        Ponto escolhido = listaFiltrada.getSelectedValue();
        if (escolhido == null) {
            return;
        }
        selecionado = escolhido;
        atualizarBotao();
        if (dialogo != null) {
            dialogo.dispose();
        }
        disparar();
    }

    private static final class RendererLista extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                      boolean selecionado, boolean foco) {
            super.getListCellRendererComponent(lista, valor, indice, selecionado, foco);
            setBorder(new EmptyBorder(0, 8, 0, 8));
            setFont(Estilo.CORPO);
            if (valor instanceof Ponto ponto) {
                setText(descrever(ponto));
            }
            return this;
        }
    }
}
