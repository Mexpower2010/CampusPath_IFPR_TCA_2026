package ifpr.edu.view;

import ifpr.edu.controller.MapaController;
import ifpr.edu.model.Andar;
import ifpr.edu.model.Ponto;
import ifpr.edu.model.Professor;
import ifpr.edu.model.Trecho;
import ifpr.edu.service.Rota;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PainelMapa extends JPanel {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final MapaController controller;

    private final JComboBox<Object> comboOrigem = new JComboBox<>();
    private final JComboBox<Object> comboDestino = new JComboBox<>();
    private final JCheckBox evitarEscadas = new JCheckBox("Evitar escadas");
    private final JCheckBox somenteCoberto = new JCheckBox("Somente coberto");
    private final ComponenteMapa mapa = new ComponenteMapa();
    private final JTextArea resultado = new JTextArea();
    private final DefaultListModel<Professor> modeloProfessores = new DefaultListModel<>();
    private final JLabel status = new JLabel(" ");
    private final JLabel vistaAtual = new JLabel("Campus - Terreo");
    private final JLabel salaNome = new JLabel("Nenhum ponto selecionado.");
    private final JPanel salaConteudo = new JPanel();

    private final Map<Integer, Ponto> pontosPorId = new HashMap<>();
    private final Set<Integer> pontosVerticais = new HashSet<>();
    private List<Andar> andares = new ArrayList<>();
    private List<Ponto> todosPontos = new ArrayList<>();
    private List<Trecho> todosTrechos = new ArrayList<>();
    private Ponto salaSelecionada;

    public PainelMapa(MapaController controller) {
        super(new BorderLayout(10, 10));
        this.controller = controller;
        setBackground(Estilo.FUNDO);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(montarBarraSuperior(), BorderLayout.NORTH);
        add(montarMapa(), BorderLayout.CENTER);
        add(montarLateral(), BorderLayout.EAST);

        mapa.setAoClicar(this::selecionarPonto);
        carregarTudo();
    }

    private JPanel montarBarraSuperior() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        barra.setBackground(Estilo.FUNDO);

        vistaAtual.setForeground(Estilo.TEXTO);
        vistaAtual.setFont(Estilo.SUBTITULO);

        evitarEscadas.setBackground(Estilo.FUNDO);
        evitarEscadas.setForeground(Estilo.TEXTO);
        evitarEscadas.setFont(Estilo.CORPO);
        somenteCoberto.setBackground(Estilo.FUNDO);
        somenteCoberto.setForeground(Estilo.TEXTO);
        somenteCoberto.setFont(Estilo.CORPO);

        JButton atualizar = new JButton("Atualizar");
        atualizar.addActionListener(e -> carregarTudo());

        barra.add(vistaAtual);
        barra.add(evitarEscadas);
        barra.add(somenteCoberto);
        barra.add(atualizar);
        return barra;
    }

    private JPanel montarMapa() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Estilo.PAINEL);
        painel.setBorder(BorderFactory.createLineBorder(Estilo.BORDA));
        painel.add(mapa, BorderLayout.CENTER);

        status.setForeground(Estilo.TEXTO_FRACO);
        status.setFont(Estilo.PEQUENA);
        status.setBorder(new EmptyBorder(4, 8, 4, 8));
        painel.add(status, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel montarLateral() {
        JPanel lateral = new JPanel(new BorderLayout(0, 10));
        lateral.setBackground(Estilo.FUNDO);
        lateral.setPreferredSize(new Dimension(330, 0));

        lateral.add(montarRota(), BorderLayout.NORTH);
        lateral.add(montarCentro(), BorderLayout.CENTER);
        lateral.add(montarProfessores(), BorderLayout.SOUTH);
        return lateral;
    }

    private JPanel montarCentro() {
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setBackground(Estilo.FUNDO);
        centro.add(montarSala(), BorderLayout.NORTH);
        centro.add(montarResultado(), BorderLayout.CENTER);
        return centro;
    }

    private JPanel montarRota() {
        JPanel painel = new JPanel(new BorderLayout(0, 6));
        painel.setBackground(Estilo.PAINEL);
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Estilo.BORDA), new EmptyBorder(10, 10, 10, 10)));

        JLabel titulo = new JLabel("Rota");
        titulo.setForeground(Estilo.TEXTO);
        titulo.setFont(Estilo.SUBTITULO);
        painel.add(titulo, BorderLayout.NORTH);

        comboOrigem.setRenderer(new RendererPonto());
        comboDestino.setRenderer(new RendererPonto());

        JPanel campos = new JPanel(new GridLayout(2, 2, 6, 6));
        campos.setBackground(Estilo.PAINEL);
        JLabel rotuloOrigem = new JLabel("Origem");
        rotuloOrigem.setForeground(Estilo.TEXTO_FRACO);
        rotuloOrigem.setFont(Estilo.PEQUENA);
        JLabel rotuloDestino = new JLabel("Destino");
        rotuloDestino.setForeground(Estilo.TEXTO_FRACO);
        rotuloDestino.setFont(Estilo.PEQUENA);
        campos.add(rotuloOrigem);
        campos.add(comboOrigem);
        campos.add(rotuloDestino);
        campos.add(comboDestino);
        painel.add(campos, BorderLayout.CENTER);

        JButton calcular = new JButton("Calcular rota");
        calcular.addActionListener(e -> calcularRota());
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(Estilo.PAINEL);
        rodape.add(calcular, BorderLayout.CENTER);
        painel.add(rodape, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel montarSala() {
        JPanel painel = new JPanel(new BorderLayout(0, 4));
        painel.setBackground(Estilo.PAINEL);
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Estilo.BORDA), new EmptyBorder(8, 8, 8, 8)));

        JLabel titulo = new JLabel("Sala selecionada");
        titulo.setForeground(Estilo.TEXTO);
        titulo.setFont(Estilo.SUBTITULO);
        painel.add(titulo, BorderLayout.NORTH);

        salaNome.setForeground(Estilo.TEXTO_FRACO);
        salaNome.setFont(Estilo.PEQUENA);

        salaConteudo.setLayout(new BoxLayout(salaConteudo, BoxLayout.Y_AXIS));
        salaConteudo.setBackground(Estilo.PAINEL);

        JScrollPane rolagem = new JScrollPane(salaConteudo);
        rolagem.setPreferredSize(new Dimension(0, 118));
        rolagem.setBorder(null);

        JPanel corpo = new JPanel(new BorderLayout(0, 4));
        corpo.setBackground(Estilo.PAINEL);
        corpo.add(salaNome, BorderLayout.NORTH);
        corpo.add(rolagem, BorderLayout.CENTER);
        painel.add(corpo, BorderLayout.CENTER);
        return painel;
    }

    private JPanel montarResultado() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Estilo.PAINEL);
        painel.setBorder(BorderFactory.createLineBorder(Estilo.BORDA));

        resultado.setEditable(false);
        resultado.setLineWrap(true);
        resultado.setWrapStyleWord(true);
        resultado.setBackground(Estilo.PAINEL);
        resultado.setForeground(Estilo.TEXTO);
        resultado.setFont(Estilo.CORPO);
        resultado.setBorder(new EmptyBorder(8, 8, 8, 8));
        painel.add(new JScrollPane(resultado), BorderLayout.CENTER);
        return painel;
    }

    private JPanel montarProfessores() {
        JPanel painel = new JPanel(new BorderLayout(0, 4));
        painel.setBackground(Estilo.PAINEL);
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Estilo.BORDA), new EmptyBorder(8, 8, 8, 8)));

        JLabel titulo = new JLabel("Professores presentes");
        titulo.setForeground(Estilo.TEXTO);
        titulo.setFont(Estilo.SUBTITULO);
        painel.add(titulo, BorderLayout.NORTH);

        JList<Professor> lista = new JList<>(modeloProfessores);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setBackground(Estilo.PAINEL);
        lista.setForeground(Estilo.TEXTO);
        lista.setFont(Estilo.CORPO);
        lista.setCellRenderer(new RendererProfessor());
        JScrollPane rolagem = new JScrollPane(lista);
        rolagem.setPreferredSize(new Dimension(0, 150));
        rolagem.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        painel.add(rolagem, BorderLayout.CENTER);
        return painel;
    }

    private void carregarTudo() {
        try {
            andares = controller.listarAndares();
            todosPontos = controller.listarPontos();
            todosTrechos = controller.listarTrechos();
            pontosPorId.clear();
            for (Ponto ponto : todosPontos) {
                pontosPorId.put(ponto.getIdPonto(), ponto);
            }
            pontosVerticais.clear();
            for (Trecho trecho : todosTrechos) {
                Ponto a = pontosPorId.get(trecho.getIdPontoA());
                Ponto b = pontosPorId.get(trecho.getIdPontoB());
                if (a == null || b == null || a.getIdAndar() == null || b.getIdAndar() == null) {
                    continue;
                }
                if (a.getIdAndar().equals(b.getIdAndar())) {
                    continue;
                }
                if (a.getNomeBloco() == null || !a.getNomeBloco().equals(b.getNomeBloco())) {
                    continue;
                }
                pontosVerticais.add(a.getIdPonto());
                pontosVerticais.add(b.getIdPonto());
            }
            mapa.setTrechos(todosTrechos);
            mapa.setPontosVerticais(pontosVerticais);
            preencherCombos();
            carregarProfessores();
            mostrarTerreo();
        } catch (RuntimeException e) {
            aviso("Falha ao carregar o mapa: " + e.getMessage());
        }
    }

    private void preencherCombos() {
        comboOrigem.removeAllItems();
        comboDestino.removeAllItems();
        for (Ponto ponto : todosPontos) {
            comboOrigem.addItem(ponto);
            comboDestino.addItem(ponto);
        }
        if (comboDestino.getItemCount() > 1) {
            comboDestino.setSelectedIndex(1);
        }
    }

    private void carregarProfessores() {
        modeloProfessores.clear();
        Map<Integer, List<Professor>> porPonto = new HashMap<>();
        try {
            for (Professor professor : controller.listarProfessoresPresentes()) {
                modeloProfessores.addElement(professor);
                if (professor.getIdPontoAtual() != null) {
                    porPonto.computeIfAbsent(professor.getIdPontoAtual(), chave -> new ArrayList<>())
                        .add(professor);
                }
            }
        } catch (RuntimeException e) {
            aviso("Falha ao carregar os professores: " + e.getMessage());
        }
        mapa.setProfessores(porPonto);
        if (salaSelecionada != null) {
            atualizarSala(salaSelecionada);
        }
    }

    private void mostrarTerreo() {
        vistaAtual.setText("Campus - Terreo");
        List<Ponto> visiveis = new ArrayList<>();
        for (Ponto ponto : todosPontos) {
            Andar andar = andarDoPonto(ponto);
            if (andar != null && andar.getNivel() != null && andar.getNivel() == 0) {
                visiveis.add(ponto);
            }
        }
        mapa.setPanHabilitado(true);
        mapa.setPontos(visiveis);
        mapa.limparSelecao();
        status.setText(visiveis.size()
            + " ponto(s) no terreo. Clique em escada ou elevador para trocar de andar.");
    }

    private void mostrarAndar(Andar andar) {
        vistaAtual.setText(andar.getNomeBloco() + " - " + andar.getNome());
        List<Ponto> visiveis = new ArrayList<>();
        for (Ponto ponto : todosPontos) {
            if (andar.getIdAndar().equals(ponto.getIdAndar())) {
                visiveis.add(ponto);
            }
        }
        mapa.setPanHabilitado(false);
        mapa.setPontos(visiveis);
        mapa.limparSelecao();
        status.setText(visiveis.size()
            + " ponto(s) neste andar. Clique em escada ou elevador para trocar de andar.");
    }

    private Andar andarDoPonto(Ponto ponto) {
        for (Andar andar : andares) {
            if (andar.getIdAndar().equals(ponto.getIdAndar())) {
                return andar;
            }
        }
        return null;
    }

    private void selecionarPonto(Ponto ponto) {
        if (pontosVerticais.contains(ponto.getIdPonto())) {
            trocarAndar(ponto);
            return;
        }
        selecionarDestino(ponto);
        atualizarSala(ponto);
    }

    private void trocarAndar(Ponto ponto) {
        Ponto outro = outroExtremoVertical(ponto);
        if (outro == null) {
            return;
        }
        Andar andar = andarDoPonto(outro);
        if (andar == null) {
            return;
        }
        if (andar.getNivel() != null && andar.getNivel() == 0) {
            mostrarTerreo();
        } else {
            mostrarAndar(andar);
        }
    }

    private Ponto outroExtremoVertical(Ponto ponto) {
        for (Trecho trecho : todosTrechos) {
            Integer outroId = null;
            if (ponto.getIdPonto().equals(trecho.getIdPontoA())) {
                outroId = trecho.getIdPontoB();
            } else if (ponto.getIdPonto().equals(trecho.getIdPontoB())) {
                outroId = trecho.getIdPontoA();
            }
            if (outroId == null) {
                continue;
            }
            Ponto outro = pontosPorId.get(outroId);
            if (outro != null && outro.getIdAndar() != null
                && !outro.getIdAndar().equals(ponto.getIdAndar())) {
                return outro;
            }
        }
        return null;
    }

    private void atualizarSala(Ponto ponto) {
        salaSelecionada = ponto;
        salaConteudo.removeAll();
        if (ponto == null) {
            salaNome.setText("Nenhum ponto selecionado.");
        } else if (!ponto.isSala()) {
            salaNome.setText(ponto.getNome() + " - este ponto nao e uma sala.");
        } else {
            salaNome.setText(ponto.getNome());
            try {
                List<Professor> presentes = controller.listarProfessoresPorSala(ponto.getIdPonto());
                if (presentes.isEmpty()) {
                    salaConteudo.add(linha("Nenhum professor presente nesta sala no momento."));
                } else {
                    for (Professor professor : presentes) {
                        salaConteudo.add(montarProfessorSala(professor));
                    }
                }
            } catch (RuntimeException e) {
                salaConteudo.add(linha("Falha ao carregar os professores: " + e.getMessage()));
            }
        }
        salaConteudo.revalidate();
        salaConteudo.repaint();
    }

    private JLabel linha(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(Estilo.TEXTO_FRACO);
        label.setFont(Estilo.PEQUENA);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel montarProfessorSala(Professor professor) {
        JPanel painel = new JPanel(new BorderLayout(8, 0));
        painel.setBackground(Estilo.PAINEL);
        painel.setBorder(new EmptyBorder(4, 0, 4, 0));
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel foto = new JLabel(new ImageIcon(
            FotoPerfil.imagem(professor.getFotoPerfil(), professor.getNome(), 40)));
        painel.add(foto, BorderLayout.WEST);

        JPanel textos = new JPanel(new GridLayout(3, 1, 0, 2));
        textos.setBackground(Estilo.PAINEL);
        JLabel nome = new JLabel(professor.getNome());
        nome.setForeground(Estilo.TEXTO);
        nome.setFont(Estilo.CORPO);
        JLabel mensagem = new JLabel(professor.getMensagem() == null
            ? "sem mensagem" : professor.getMensagem());
        mensagem.setForeground(Estilo.TEXTO_FRACO);
        mensagem.setFont(Estilo.PEQUENA);
        JLabel horario = new JLabel(professor.getMensagemEnviadaEm() == null
            ? "sem horario" : professor.getMensagemEnviadaEm().format(FORMATO));
        horario.setForeground(Estilo.TEXTO_FRACO);
        horario.setFont(Estilo.PEQUENA);
        textos.add(nome);
        textos.add(mensagem);
        textos.add(horario);
        painel.add(textos, BorderLayout.CENTER);
        return painel;
    }

    private void selecionarDestino(Ponto ponto) {
        for (int i = 0; i < comboDestino.getItemCount(); i++) {
            Ponto item = (Ponto) comboDestino.getItemAt(i);
            if (item.getIdPonto().equals(ponto.getIdPonto())) {
                comboDestino.setSelectedIndex(i);
                status.setText("Destino: " + ponto.getNome());
                return;
            }
        }
    }

    private void calcularRota() {
        Ponto origem = (Ponto) comboOrigem.getSelectedItem();
        Ponto destino = (Ponto) comboDestino.getSelectedItem();
        if (origem == null || destino == null) {
            aviso("Selecione a origem e o destino.");
            return;
        }
        try {
            Rota rota = controller.calcularRota(origem.getIdPonto(), destino.getIdPonto(),
                evitarEscadas.isSelected(), somenteCoberto.isSelected());
            Andar andarOrigem = andarDoPonto(rota.getOrigem());
            if (andarOrigem != null && andarOrigem.getNivel() != null && andarOrigem.getNivel() > 0) {
                mostrarAndar(andarOrigem);
            } else {
                mostrarTerreo();
            }
            mapa.setCaminho(rota.getCaminho());

            StringBuilder texto = new StringBuilder();
            texto.append("Origem: ").append(rota.getOrigem().getNome()).append('\n');
            texto.append("Destino: ").append(rota.getDestino().getNome()).append('\n');
            texto.append(String.format("Distancia total: %.1f m%n", rota.getDistanciaTotal()));
            texto.append('\n').append("Passos:").append('\n');
            for (String passo : rota.getPassos()) {
                texto.append("  - ").append(passo).append('\n');
            }
            resultado.setText(texto.toString());
            resultado.setCaretPosition(0);
            status.setText(String.format("Rota calculada: %.1f m. O percurso pode continuar em outro andar:"
                + " clique na escada ou elevador para ver.", rota.getDistanciaTotal()));
        } catch (RuntimeException e) {
            mapa.setCaminho(new ArrayList<>());
            resultado.setText(e.getMessage());
            status.setText("Nenhuma rota encontrada com os filtros atuais.");
        }
    }

    private void aviso(String mensagem) {
        status.setText(mensagem);
    }

    private static String descreverPonto(Ponto ponto) {
        String bloco = ponto.getNomeBloco() == null ? "bloco " + ponto.getIdAndar() : ponto.getNomeBloco();
        String andar = ponto.getNomeAndar() == null ? "andar " + ponto.getIdAndar() : ponto.getNomeAndar();
        return ponto.getNome() + " (" + bloco + " / " + andar + ")";
    }

    private static final class RendererPonto extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                      boolean selecionado, boolean foco) {
            super.getListCellRendererComponent(lista, valor, indice, selecionado, foco);
            if (valor instanceof Ponto ponto) {
                setText(descreverPonto(ponto));
            }
            return this;
        }
    }

    private static final class RendererProfessor extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                      boolean selecionado, boolean foco) {
            super.getListCellRendererComponent(lista, valor, indice, selecionado, foco);
            setBorder(new EmptyBorder(4, 4, 4, 4));
            setFont(Estilo.CORPO);
            if (!(valor instanceof Professor professor)) {
                setIcon(null);
                setText(" ");
                return this;
            }
            String sala = professor.getNomeSala() == null ? "sem sala" : professor.getNomeSala();
            String mensagem = professor.getMensagem() == null ? "sem mensagem" : professor.getMensagem();
            setIcon(new ImageIcon(
                FotoPerfil.imagem(professor.getFotoPerfil(), professor.getNome(), 24)));
            setIconTextGap(6);
            setText("<html><body>" + escapar(professor.getNome() + " - " + sala)
                + "<br><span style='color:#8FA3B8'>" + escapar(mensagem) + "</span></body></html>");
            return this;
        }

        private static String escapar(String texto) {
            return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
    }
}
