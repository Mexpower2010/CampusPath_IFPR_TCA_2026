package ifpr.edu.view;

import ifpr.edu.controller.MapaController;
import ifpr.edu.model.Andar;
import ifpr.edu.model.Bloco;
import ifpr.edu.model.Ponto;
import ifpr.edu.model.Professor;
import ifpr.edu.model.Trecho;
import ifpr.edu.service.Rota;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PainelMapa extends JPanel {

    private final MapaController controller;

    private final JComboBox<Object> comboBloco = new JComboBox<>();
    private final JComboBox<Object> comboAndar = new JComboBox<>();
    private final JComboBox<Object> comboOrigem = new JComboBox<>();
    private final JComboBox<Object> comboDestino = new JComboBox<>();
    private final JCheckBox evitarEscadas = new JCheckBox("Evitar escadas");
    private final JCheckBox somenteCoberto = new JCheckBox("Somente coberto");
    private final ComponenteMapa mapa = new ComponenteMapa();
    private final JTextArea resultado = new JTextArea();
    private final DefaultListModel<String> modeloProfessores = new DefaultListModel<>();
    private final JLabel status = new JLabel(" ");

    private boolean atualizando;

    public PainelMapa(MapaController controller) {
        super(new BorderLayout(10, 10));
        this.controller = controller;
        setBackground(Estilo.FUNDO);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(montarBarraSuperior(), BorderLayout.NORTH);
        add(montarMapa(), BorderLayout.CENTER);
        add(montarLateral(), BorderLayout.EAST);

        mapa.setAoClicar(this::selecionarDestino);
        carregarBlocos();
    }

    private JPanel montarBarraSuperior() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        barra.setBackground(Estilo.FUNDO);

        JLabel rotuloBloco = new JLabel("Bloco");
        rotuloBloco.setForeground(Estilo.TEXTO_FRACO);
        rotuloBloco.setFont(Estilo.PEQUENA);
        JLabel rotuloAndar = new JLabel("Andar");
        rotuloAndar.setForeground(Estilo.TEXTO_FRACO);
        rotuloAndar.setFont(Estilo.PEQUENA);

        comboBloco.setPreferredSize(new Dimension(180, 26));
        comboAndar.setPreferredSize(new Dimension(200, 26));
        comboBloco.addActionListener(e -> {
            if (!atualizando) {
                carregarAndares();
            }
        });
        comboAndar.addActionListener(e -> {
            if (!atualizando) {
                carregarPontos();
            }
        });

        evitarEscadas.setBackground(Estilo.FUNDO);
        evitarEscadas.setForeground(Estilo.TEXTO);
        evitarEscadas.setFont(Estilo.CORPO);
        somenteCoberto.setBackground(Estilo.FUNDO);
        somenteCoberto.setForeground(Estilo.TEXTO);
        somenteCoberto.setFont(Estilo.CORPO);

        JButton atualizar = new JButton("Atualizar");
        atualizar.addActionListener(e -> carregarTudo());

        barra.add(rotuloBloco);
        barra.add(comboBloco);
        barra.add(rotuloAndar);
        barra.add(comboAndar);
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
        lateral.add(montarResultado(), BorderLayout.CENTER);
        lateral.add(montarProfessores(), BorderLayout.SOUTH);
        return lateral;
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

        JList<String> lista = new JList<>(modeloProfessores);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setBackground(Estilo.PAINEL);
        lista.setForeground(Estilo.TEXTO);
        lista.setFont(Estilo.CORPO);
        JScrollPane rolagem = new JScrollPane(lista);
        rolagem.setPreferredSize(new Dimension(0, 130));
        painel.add(rolagem, BorderLayout.CENTER);
        return painel;
    }

    private void carregarBlocos() {
        try {
            atualizando = true;
            comboBloco.removeAllItems();
            for (Bloco bloco : controller.listarBlocos()) {
                comboBloco.addItem(bloco);
            }
        } catch (RuntimeException e) {
            aviso("Falha ao carregar os blocos: " + e.getMessage());
        } finally {
            atualizando = false;
        }
        carregarAndares();
    }

    private void carregarAndares() {
        try {
            atualizando = true;
            comboAndar.removeAllItems();
            Bloco bloco = (Bloco) comboBloco.getSelectedItem();
            if (bloco != null) {
                for (Andar andar : controller.listarAndares(bloco.getIdBloco())) {
                    comboAndar.addItem(andar);
                }
            }
        } catch (RuntimeException e) {
            aviso("Falha ao carregar os andares: " + e.getMessage());
        } finally {
            atualizando = false;
        }
        carregarPontos();
    }

    private void carregarPontos() {
        try {
            atualizando = true;
            Andar andar = (Andar) comboAndar.getSelectedItem();
            List<Ponto> pontos = andar == null ? new ArrayList<>() : controller.listarPontos(andar.getIdAndar());
            mapa.setPontos(pontos);
            mapa.setTrechos(controller.listarTrechos());
            mapa.setCaminho(new ArrayList<>());

            comboOrigem.removeAllItems();
            comboDestino.removeAllItems();
            for (Ponto ponto : pontos) {
                comboOrigem.addItem(ponto);
                comboDestino.addItem(ponto);
            }
            if (comboDestino.getItemCount() > 1) {
                comboDestino.setSelectedIndex(1);
            }
            carregarProfessores();
            status.setText(pontos.size() + " ponto(s) neste andar. Clique em um ponto para definir o destino.");
        } catch (RuntimeException e) {
            aviso("Falha ao carregar os pontos: " + e.getMessage());
        } finally {
            atualizando = false;
        }
    }

    private void carregarProfessores() {
        modeloProfessores.clear();
        Map<Integer, String> marcadores = new HashMap<>();
        try {
            for (Professor professor : controller.listarProfessoresPresentes()) {
                String sala = professor.getNomeSala() == null ? "sem sala" : professor.getNomeSala();
                modeloProfessores.addElement(professor.getNome() + " - " + sala);
                if (professor.getIdPontoAtual() != null) {
                    marcadores.put(professor.getIdPontoAtual(), professor.getNome());
                }
            }
        } catch (RuntimeException e) {
            aviso("Falha ao carregar os professores: " + e.getMessage());
        }
        mapa.setProfessores(marcadores);
    }

    private void carregarTudo() {
        carregarBlocos();
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
            status.setText(String.format("Rota calculada: %.1f m", rota.getDistanciaTotal()));
        } catch (RuntimeException e) {
            mapa.setCaminho(new ArrayList<>());
            resultado.setText(e.getMessage());
            status.setText("Nenhuma rota encontrada com os filtros atuais.");
        }
    }

    private void aviso(String mensagem) {
        status.setText(mensagem);
    }
}
