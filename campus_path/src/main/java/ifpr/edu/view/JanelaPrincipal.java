package ifpr.edu.view;

import ifpr.edu.controller.AdminController;
import ifpr.edu.controller.MapaController;
import ifpr.edu.controller.ProfessorController;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;

public class JanelaPrincipal extends JFrame {

    private final CardLayout cartoes = new CardLayout();
    private final JPanel conteudo = new JPanel(cartoes);
    private final Map<String, JButton> navegacao = new LinkedHashMap<>();

    public JanelaPrincipal() {
        super("CampusPath - IFPR Cascavel");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 800));

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Estilo.FUNDO);
        raiz.add(montarCabecalho(), BorderLayout.NORTH);

        conteudo.setBackground(Estilo.FUNDO);
        conteudo.add(new PainelMapa(new MapaController()), "mapa");
        conteudo.add(new PainelProfessor(new ProfessorController()), "professor");
        conteudo.add(new PainelAdmin(new AdminController()), "admin");
        raiz.add(conteudo, BorderLayout.CENTER);

        setContentPane(raiz);
        pack();
        setLocationRelativeTo(null);
        navegar("mapa");
    }

    private JPanel montarCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(Estilo.PAINEL);
        cabecalho.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Estilo.BORDA), new EmptyBorder(16, 20, 16, 20)));

        JLabel titulo = new JLabel("CampusPath");
        titulo.setForeground(Estilo.TEXTO);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        cabecalho.add(titulo, BorderLayout.WEST);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botoes.setBackground(Estilo.PAINEL);
        botoes.add(botao("Mapa e rotas", "mapa"));
        botoes.add(botao("Professor", "professor"));
        botoes.add(botao("Administracao", "admin"));
        cabecalho.add(botoes, BorderLayout.EAST);
        return cabecalho;
    }

    private JButton botao(String texto, String chave) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        botao.setForeground(Estilo.TEXTO);
        botao.setBackground(Estilo.PAINEL_CLARO);
        botao.setOpaque(true);
        botao.setContentAreaFilled(true);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setBorder(new EmptyBorder(10, 18, 10, 18));
        botao.addActionListener(e -> navegar(chave));
        navegacao.put(chave, botao);
        return botao;
    }

    private void navegar(String chave) {
        cartoes.show(conteudo, chave);
        navegacao.forEach((nome, botao) ->
            botao.setBackground(nome.equals(chave) ? Estilo.DESTAQUE : Estilo.PAINEL_CLARO));
    }
}
