package ifpr.edu.view;

import ifpr.edu.controller.ProfessorController;
import ifpr.edu.model.Ponto;
import ifpr.edu.model.Professor;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.format.DateTimeFormatter;

public class PainelProfessor extends JPanel {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ProfessorController controller;
    private final CardLayout cartoes = new CardLayout();
    private final JPanel conteudo = new JPanel(cartoes);

    private final JTextField campoLogin = new JTextField(18);
    private final JPasswordField campoSenha = new JPasswordField(18);
    private final JLabel mensagemLogin = new JLabel(" ");

    private final JLabel nomeSessao = new JLabel(" ");
    private final JLabel salaAtual = new JLabel(" ");
    private final JComboBox<Object> comboSalas = new JComboBox<>();
    private final JTextArea campoMensagem = new JTextArea(4, 30);
    private final JTextField campoNome = new JTextField(24);
    private final JTextField campoFoto = new JTextField(24);
    private final JLabel status = new JLabel(" ");

    private Professor professor;

    public PainelProfessor(ProfessorController controller) {
        super(new BorderLayout());
        this.controller = controller;
        setBackground(Estilo.FUNDO);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        conteudo.setBackground(Estilo.FUNDO);
        conteudo.add(montarLogin(), "login");
        conteudo.add(montarAcoes(), "acoes");
        add(conteudo, BorderLayout.CENTER);

        status.setForeground(Estilo.TEXTO_FRACO);
        status.setFont(Estilo.PEQUENA);
        status.setBorder(new EmptyBorder(6, 4, 0, 0));
        add(status, BorderLayout.SOUTH);

        cartoes.show(conteudo, "login");
    }

    private JPanel montarLogin() {
        JPanel externo = new JPanel(new GridBagLayout());
        externo.setBackground(Estilo.FUNDO);

        JPanel cartao = new JPanel(new GridBagLayout());
        cartao.setBackground(Estilo.PAINEL);
        cartao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Estilo.BORDA), new EmptyBorder(20, 24, 20, 24)));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 0, 12, 0);

        JLabel titulo = new JLabel("Entrar como professor");
        titulo.setForeground(Estilo.TEXTO);
        titulo.setFont(Estilo.TITULO);
        cartao.add(titulo, c);

        c.gridy++;
        c.insets = new Insets(4, 0, 4, 8);
        c.gridwidth = 1;
        cartao.add(rotulo("Login"), c);
        c.gridx = 1;
        cartao.add(campoLogin, c);

        c.gridy++;
        c.gridx = 0;
        cartao.add(rotulo("Senha"), c);
        c.gridx = 1;
        cartao.add(campoSenha, c);

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 2;
        c.insets = new Insets(14, 0, 0, 0);
        JButton entrar = new JButton("Entrar");
        entrar.addActionListener(e -> entrar());
        cartao.add(entrar, c);

        c.gridy++;
        c.insets = new Insets(10, 0, 0, 0);
        mensagemLogin.setForeground(Estilo.ERRO);
        mensagemLogin.setFont(Estilo.PEQUENA);
        cartao.add(mensagemLogin, c);

        c.gridy++;
        c.insets = new Insets(6, 0, 0, 0);
        JLabel dica = new JLabel("Conta de teste: roberta.parcianello / 123456");
        dica.setForeground(Estilo.TEXTO_FRACO);
        dica.setFont(Estilo.PEQUENA);
        cartao.add(dica, c);

        externo.add(cartao);
        return externo;
    }

    private JLabel rotulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(Estilo.TEXTO_FRACO);
        label.setFont(Estilo.PEQUENA);
        return label;
    }

    private JPanel montarAcoes() {
        JPanel externo = new JPanel(new BorderLayout(0, 10));
        externo.setBackground(Estilo.FUNDO);

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(Estilo.PAINEL);
        cabecalho.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Estilo.BORDA), new EmptyBorder(12, 14, 12, 14)));

        nomeSessao.setForeground(Estilo.TEXTO);
        nomeSessao.setFont(Estilo.TITULO);
        cabecalho.add(nomeSessao, BorderLayout.WEST);

        JButton sair = new JButton("Sair");
        sair.addActionListener(e -> sair());
        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        direita.setBackground(Estilo.PAINEL);
        direita.add(sair);
        cabecalho.add(direita, BorderLayout.EAST);
        externo.add(cabecalho, BorderLayout.NORTH);

        JPanel corpo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        corpo.setBackground(Estilo.FUNDO);
        corpo.add(montarPresenca());
        corpo.add(montarMensagem());
        corpo.add(montarPerfil());
        externo.add(corpo, BorderLayout.CENTER);
        return externo;
    }

    private JPanel montarPresenca() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(Estilo.PAINEL);
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Estilo.BORDA), new EmptyBorder(12, 14, 12, 14)));
        painel.setPreferredSize(new Dimension(330, 260));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JLabel titulo = new JLabel("Minha presenca");
        titulo.setForeground(Estilo.TEXTO);
        titulo.setFont(Estilo.SUBTITULO);
        painel.add(titulo, c);

        c.gridy++;
        c.insets = new Insets(8, 0, 0, 0);
        salaAtual.setForeground(Estilo.TEXTO_FRACO);
        salaAtual.setFont(Estilo.PEQUENA);
        painel.add(salaAtual, c);

        c.gridy++;
        c.gridwidth = 1;
        c.insets = new Insets(10, 0, 4, 8);
        painel.add(rotulo("Sala"), c);
        c.gridx = 1;
        c.weightx = 1;
        comboSalas.setPreferredSize(new Dimension(180, 26));
        painel.add(comboSalas, c);

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 2;
        c.insets = new Insets(10, 0, 0, 0);
        JButton atualizar = new JButton("Atualizar localizacao");
        atualizar.addActionListener(e -> atualizarLocalizacao());
        painel.add(atualizar, c);

        c.gridy++;
        c.insets = new Insets(6, 0, 0, 0);
        JButton remover = new JButton("Remover presenca");
        remover.addActionListener(e -> removerPresenca());
        painel.add(remover, c);

        c.gridy++;
        c.weighty = 1;
        c.insets = new Insets(6, 0, 0, 0);
        painel.add(new JLabel(" "), c);
        return painel;
    }

    private JPanel montarMensagem() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(Estilo.PAINEL);
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Estilo.BORDA), new EmptyBorder(12, 14, 12, 14)));
        painel.setPreferredSize(new Dimension(360, 260));

        JLabel titulo = new JLabel("Mensagem para os alunos");
        titulo.setForeground(Estilo.TEXTO);
        titulo.setFont(Estilo.SUBTITULO);
        painel.add(titulo, BorderLayout.NORTH);

        campoMensagem.setLineWrap(true);
        campoMensagem.setWrapStyleWord(true);
        campoMensagem.setFont(Estilo.CORPO);
        painel.add(new JScrollPane(campoMensagem), BorderLayout.CENTER);

        JButton publicar = new JButton("Publicar mensagem");
        publicar.addActionListener(e -> publicarMensagem());
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rodape.setBackground(Estilo.PAINEL);
        rodape.add(publicar);
        painel.add(rodape, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel montarPerfil() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(Estilo.PAINEL);
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Estilo.BORDA), new EmptyBorder(12, 14, 12, 14)));
        painel.setPreferredSize(new Dimension(330, 260));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JLabel titulo = new JLabel("Perfil");
        titulo.setForeground(Estilo.TEXTO);
        titulo.setFont(Estilo.SUBTITULO);
        painel.add(titulo, c);

        c.gridy++;
        c.gridwidth = 1;
        c.insets = new Insets(10, 0, 4, 8);
        painel.add(rotulo("Nome"), c);
        c.gridx = 1;
        painel.add(campoNome, c);

        c.gridy++;
        c.gridx = 0;
        painel.add(rotulo("Foto"), c);
        c.gridx = 1;
        painel.add(campoFoto, c);

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 2;
        c.insets = new Insets(12, 0, 0, 0);
        JButton salvar = new JButton("Salvar perfil");
        salvar.addActionListener(e -> salvarPerfil());
        painel.add(salvar, c);

        c.gridy++;
        c.weighty = 1;
        c.insets = new Insets(6, 0, 0, 0);
        painel.add(new JLabel(" "), c);
        return painel;
    }

    private void entrar() {
        String login = campoLogin.getText().trim();
        String senha = new String(campoSenha.getPassword());
        if (login.isEmpty() || senha.isEmpty()) {
            mensagemLogin.setText("Informe login e senha.");
            return;
        }
        try {
            professor = controller.autenticar(login, senha);
            mensagemLogin.setText(" ");
            campoSenha.setText("");
            carregarSalas();
            atualizarTela();
            cartoes.show(conteudo, "acoes");
            status.setText("Sessao iniciada.");
        } catch (RuntimeException e) {
            mensagemLogin.setText(e.getMessage());
        }
    }

    private void sair() {
        professor = null;
        campoLogin.setText("");
        campoSenha.setText("");
        campoMensagem.setText("");
        cartoes.show(conteudo, "login");
        status.setText("Sessao encerrada.");
    }

    private void carregarSalas() {
        comboSalas.removeAllItems();
        comboSalas.addItem("sem sala definida");
        for (Ponto sala : controller.listarSalas()) {
            comboSalas.addItem(sala);
        }
    }

    private void atualizarTela() {
        if (professor == null) {
            return;
        }
        nomeSessao.setText(professor.getNome());
        String sala = professor.getNomeSala() == null ? "nao definida" : professor.getNomeSala();
        String horario = professor.getMensagemEnviadaEm() == null
            ? "sem registro"
            : professor.getMensagemEnviadaEm().format(FORMATO);
        salaAtual.setText("Sala atual: " + sala + " | Mensagem em " + horario);
        campoNome.setText(professor.getNome());
        campoFoto.setText(professor.getFotoPerfil() == null ? "" : professor.getFotoPerfil());
        campoMensagem.setText(professor.getMensagem() == null ? "" : professor.getMensagem());

        comboSalas.setSelectedIndex(0);
        if (professor.getIdPontoAtual() != null) {
            for (int i = 1; i < comboSalas.getItemCount(); i++) {
                Ponto salaItem = (Ponto) comboSalas.getItemAt(i);
                if (salaItem.getIdPonto().equals(professor.getIdPontoAtual())) {
                    comboSalas.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void recarregar() {
        try {
            professor = controller.recarregar(professor.getIdProfessor());
            atualizarTela();
        } catch (RuntimeException e) {
            status.setText("Falha ao recarregar: " + e.getMessage());
        }
    }

    private void atualizarLocalizacao() {
        if (professor == null) {
            return;
        }
        Object selecionado = comboSalas.getSelectedItem();
        Integer idPonto = selecionado instanceof Ponto ponto ? ponto.getIdPonto() : null;
        try {
            controller.atualizarLocalizacao(professor.getIdProfessor(), idPonto);
            recarregar();
            status.setText("Localizacao atualizada.");
        } catch (RuntimeException e) {
            status.setText(e.getMessage());
        }
    }

    private void removerPresenca() {
        if (professor == null) {
            return;
        }
        try {
            controller.removerPresenca(professor.getIdProfessor());
            recarregar();
            status.setText("Presenca removida.");
        } catch (RuntimeException e) {
            status.setText(e.getMessage());
        }
    }

    private void publicarMensagem() {
        if (professor == null) {
            return;
        }
        String mensagem = campoMensagem.getText().trim();
        if (mensagem.isEmpty()) {
            status.setText("Digite a mensagem antes de publicar.");
            return;
        }
        try {
            controller.publicarMensagem(professor.getIdProfessor(), mensagem);
            recarregar();
            status.setText("Mensagem publicada com horario registrado.");
        } catch (RuntimeException e) {
            status.setText(e.getMessage());
        }
    }

    private void salvarPerfil() {
        if (professor == null) {
            return;
        }
        String nome = campoNome.getText().trim();
        if (nome.isEmpty()) {
            status.setText("O nome nao pode ficar vazio.");
            return;
        }
        try {
            controller.atualizarNome(professor.getIdProfessor(), nome);
            controller.atualizarFoto(professor.getIdProfessor(), campoFoto.getText().trim());
            recarregar();
            status.setText("Perfil atualizado.");
        } catch (RuntimeException e) {
            status.setText(e.getMessage());
        }
    }
}
