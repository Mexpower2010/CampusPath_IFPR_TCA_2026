package ifpr.edu.view;

import ifpr.edu.controller.AdminController;
import ifpr.edu.model.Administrador;
import ifpr.edu.model.Andar;
import ifpr.edu.model.Bloco;
import ifpr.edu.model.Ponto;
import ifpr.edu.model.Professor;
import ifpr.edu.model.TipoPonto;
import ifpr.edu.model.Trecho;
import ifpr.edu.util.Senha;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class PainelAdmin extends JPanel {

    private final AdminController controller;
    private final CardLayout cartoes = new CardLayout();
    private final JPanel conteudo = new JPanel(cartoes);
    private final JComboBox<String> seletor = new JComboBox<>(new String[] {
        "Blocos", "Andares", "Pontos", "Trechos", "Professores", "Administradores"});
    private final CardLayout cartoesEntidades = new CardLayout();
    private final JPanel painelEntidades = new JPanel(cartoesEntidades);

    private final JTextField campoEmail = new JTextField(20);
    private final JPasswordField campoSenha = new JPasswordField(20);
    private final JLabel mensagemLogin = new JLabel(" ");
    private final JLabel status = new JLabel(" ");

    private AbaBlocos abaBlocos;
    private AbaAndares abaAndares;
    private AbaPontos abaPontos;
    private AbaTrechos abaTrechos;
    private AbaProfessores abaProfessores;
    private AbaAdministradores abaAdministradores;

    public PainelAdmin(AdminController controller) {
        super(new BorderLayout());
        this.controller = controller;
        setBackground(Estilo.FUNDO);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        conteudo.setBackground(Estilo.FUNDO);
        conteudo.add(montarLogin(), "login");
        conteudo.add(montarAbas(), "abas");
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

        JLabel titulo = new JLabel("Entrar como administrador");
        titulo.setForeground(Estilo.TEXTO);
        titulo.setFont(Estilo.TITULO);
        cartao.add(titulo, c);

        c.gridy++;
        c.gridwidth = 1;
        c.insets = new Insets(4, 0, 4, 8);
        cartao.add(rotulo("E-mail"), c);
        c.gridx = 1;
        cartao.add(campoEmail, c);

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
        JLabel dica = new JLabel("Conta de teste: admin@campuspath.ifpr.edu.br / admin123");
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

    private JPanel montarAbas() {
        JPanel painel = new JPanel(new BorderLayout(0, 8));
        painel.setBackground(Estilo.FUNDO);

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        barra.setBackground(Estilo.FUNDO);
        JLabel rotuloEntidade = new JLabel("Entidade");
        rotuloEntidade.setForeground(Estilo.TEXTO_FRACO);
        rotuloEntidade.setFont(Estilo.PEQUENA);
        seletor.setPreferredSize(new Dimension(180, 26));
        seletor.addActionListener(e ->
            cartoesEntidades.show(painelEntidades, String.valueOf(seletor.getSelectedItem())));
        barra.add(rotuloEntidade);
        barra.add(seletor);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        direita.setBackground(Estilo.FUNDO);
        JButton exportar = new JButton("Exportar snapshot");
        exportar.addActionListener(e -> exportar());
        JButton sair = new JButton("Sair");
        sair.addActionListener(e -> sair());
        direita.add(exportar);
        direita.add(sair);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Estilo.FUNDO);
        topo.add(barra, BorderLayout.WEST);
        topo.add(direita, BorderLayout.EAST);
        painel.add(topo, BorderLayout.NORTH);

        abaBlocos = new AbaBlocos();
        abaAndares = new AbaAndares();
        abaPontos = new AbaPontos();
        abaTrechos = new AbaTrechos();
        abaProfessores = new AbaProfessores();
        abaAdministradores = new AbaAdministradores();

        painelEntidades.setBackground(Estilo.FUNDO);
        painelEntidades.add(abaBlocos, "Blocos");
        painelEntidades.add(abaAndares, "Andares");
        painelEntidades.add(abaPontos, "Pontos");
        painelEntidades.add(abaTrechos, "Trechos");
        painelEntidades.add(abaProfessores, "Professores");
        painelEntidades.add(abaAdministradores, "Administradores");
        painel.add(painelEntidades, BorderLayout.CENTER);
        return painel;
    }

    private void entrar() {
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword());
        if (email.isEmpty() || senha.isEmpty()) {
            mensagemLogin.setText("Informe e-mail e senha.");
            return;
        }
        try {
            controller.autenticar(email, senha);
            mensagemLogin.setText(" ");
            campoSenha.setText("");
            recarregarTudo();
            cartoes.show(conteudo, "abas");
            status.setText("Sessao de administrador iniciada.");
        } catch (RuntimeException e) {
            mensagemLogin.setText(e.getMessage());
        }
    }

    private void sair() {
        campoEmail.setText("");
        campoSenha.setText("");
        cartoes.show(conteudo, "login");
        status.setText("Sessao encerrada.");
    }

    private void recarregarTudo() {
        abaBlocos.carregar();
        abaAndares.carregar();
        abaPontos.carregar();
        abaTrechos.carregar();
        abaProfessores.carregar();
        abaAdministradores.carregar();
    }

    private void exportar() {
        try {
            String arquivo = controller.exportarSnapshot();
            status.setText("Estado exportado para " + arquivo);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "CampusPath", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static Integer idDe(Object item) {
        if (item instanceof Bloco bloco) {
            return bloco.getIdBloco();
        }
        if (item instanceof Andar andar) {
            return andar.getIdAndar();
        }
        if (item instanceof Ponto ponto) {
            return ponto.getIdPonto();
        }
        if (item instanceof Professor professor) {
            return professor.getIdProfessor();
        }
        return null;
    }

    private static void selecionarPorId(JComboBox<Object> combo, Integer id) {
        if (id == null) {
            if (combo.getItemCount() > 0) {
                combo.setSelectedIndex(0);
            }
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            Integer valor = idDe(combo.getItemAt(i));
            if (id.equals(valor)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private abstract class AbaCrud extends JPanel {

        protected final DefaultTableModel modelo;
        protected final JTable tabela;
        protected final Formulario form = new Formulario();
        protected final JButton botaoSalvar = new JButton("Salvar");
        protected final JButton botaoExcluir = new JButton("Excluir");
        protected final JButton botaoNovo = new JButton("Novo");
        protected Integer idSelecionado;

        AbaCrud(String[] colunas) {
            super(new BorderLayout(10, 0));
            setBackground(Estilo.FUNDO);
            setBorder(new EmptyBorder(10, 10, 10, 10));

            modelo = new DefaultTableModel(colunas, 0) {
                @Override
                public boolean isCellEditable(int linha, int coluna) {
                    return false;
                }
            };
            tabela = new JTable(modelo);
            tabela.setBackground(Estilo.PAINEL);
            tabela.setForeground(Estilo.TEXTO);
            tabela.setGridColor(Estilo.BORDA);
            tabela.setFont(Estilo.CORPO);
            tabela.setRowHeight(24);
            tabela.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
            tabela.getTableHeader().setBackground(Estilo.PAINEL_CLARO);
            tabela.getTableHeader().setForeground(Estilo.TEXTO);
            tabela.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable origem, Object valor,
                        boolean selecionado, boolean focado, int linha, int coluna) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(origem, valor,
                        selecionado, focado, linha, coluna);
                    label.setOpaque(true);
                    label.setBackground(Estilo.PAINEL_CLARO);
                    label.setForeground(Estilo.TEXTO);
                    label.setBorder(new EmptyBorder(4, 6, 4, 6));
                    return label;
                }
            });
            tabela.getSelectionModel().addListSelectionListener(evento -> {
                if (!evento.getValueIsAdjusting() && tabela.getSelectedRow() >= 0) {
                    idSelecionado = idDaLinha(tabela.getSelectedRow());
                    preencher(tabela.getSelectedRow());
                }
            });

            JScrollPane rolagem = new JScrollPane(tabela);
            rolagem.getViewport().setBackground(Estilo.PAINEL);
            add(rolagem, BorderLayout.CENTER);

            JPanel direita = new JPanel(new BorderLayout(0, 8));
            direita.setBackground(Estilo.FUNDO);
            direita.setPreferredSize(new Dimension(330, 0));
            JPanel topoFormulario = new JPanel(new BorderLayout());
            topoFormulario.setBackground(Estilo.FUNDO);
            topoFormulario.add(form, BorderLayout.NORTH);
            direita.add(topoFormulario, BorderLayout.CENTER);

            JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            botoes.setBackground(Estilo.FUNDO);
            botoes.add(botaoSalvar);
            botoes.add(botaoExcluir);
            botoes.add(botaoNovo);
            direita.add(botoes, BorderLayout.SOUTH);
            add(direita, BorderLayout.EAST);

            botaoSalvar.addActionListener(e -> executar(this::gravar));
            botaoExcluir.addActionListener(e -> executar(this::removerSelecionado));
            botaoNovo.addActionListener(e -> {
                idSelecionado = null;
                tabela.clearSelection();
                form.limpar();
                recarregarCombos();
            });
        }

        protected abstract int idDaLinha(int linha);

        protected abstract void preencher(int linha);

        protected abstract void gravar();

        protected abstract void remover(int id);

        protected abstract void carregar();

        protected void recarregarCombos() {
        }

        protected void removerSelecionado() {
            if (idSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Selecione um registro na tabela.",
                    "CampusPath", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int resposta = JOptionPane.showConfirmDialog(this, "Excluir o registro selecionado?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
            if (resposta != JOptionPane.YES_OPTION) {
                return;
            }
            remover(idSelecionado);
            idSelecionado = null;
            form.limpar();
        }

        protected void executar(Runnable acao) {
            try {
                acao.run();
                carregar();
                status.setText("Operacao concluida.");
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "CampusPath", JOptionPane.ERROR_MESSAGE);
            }
        }

        protected int inteiroDaLinha(int linha, int coluna) {
            Object valor = modelo.getValueAt(linha, coluna);
            return valor instanceof Number numero ? numero.intValue() : 0;
        }
    }

    private class AbaBlocos extends AbaCrud {

        AbaBlocos() {
            super(new String[] {"ID", "Nome"});
            form.campoTexto("nome", "Nome do bloco");
        }

        @Override
        protected int idDaLinha(int linha) {
            return inteiroDaLinha(linha, 0);
        }

        @Override
        protected void preencher(int linha) {
            form.definir("nome", String.valueOf(modelo.getValueAt(linha, 1)));
        }

        @Override
        protected void gravar() {
            Bloco bloco = new Bloco();
            bloco.setIdBloco(idSelecionado);
            bloco.setNomeBloco(form.texto("nome"));
            controller.salvarBloco(bloco);
            idSelecionado = null;
            form.limpar();
        }

        @Override
        protected void remover(int id) {
            controller.excluirBloco(id);
        }

        @Override
        protected void carregar() {
            modelo.setRowCount(0);
            for (Bloco bloco : controller.listarBlocos()) {
                modelo.addRow(new Object[] {bloco.getIdBloco(), bloco.getNomeBloco()});
            }
        }
    }

    private class AbaAndares extends AbaCrud {

        AbaAndares() {
            super(new String[] {"ID", "Nome", "Nivel", "Bloco"});
            form.campoTexto("nome", "Nome do andar");
            form.campoTexto("nivel", "Nivel");
            form.campoCombo("bloco", "Bloco", new Object[0]);
            form.campoTexto("imagem", "Imagem do mapa");
        }

        @Override
        protected int idDaLinha(int linha) {
            return inteiroDaLinha(linha, 0);
        }

        @Override
        protected void preencher(int linha) {
            form.definir("nome", String.valueOf(modelo.getValueAt(linha, 1)));
            form.definir("nivel", String.valueOf(modelo.getValueAt(linha, 2)));
            form.definir("imagem", "");
            for (Andar andar : controller.listarAndares()) {
                if (andar.getIdAndar() == idDaLinha(linha)) {
                    form.definir("imagem", andar.getImagemMapa() == null ? "" : andar.getImagemMapa());
                    selecionarPorId(comboBloco(), andar.getIdBloco());
                    break;
                }
            }
        }

        @Override
        protected void gravar() {
            Andar andar = new Andar();
            andar.setIdAndar(idSelecionado);
            andar.setNome(form.texto("nome"));
            andar.setNivel(form.inteiro("nivel"));
            andar.setImagemMapa(form.texto("imagem"));
            Object bloco = form.objeto("bloco");
            andar.setIdBloco(bloco instanceof Bloco b ? b.getIdBloco() : null);
            controller.salvarAndar(andar);
            idSelecionado = null;
            form.limpar();
        }

        @Override
        protected void remover(int id) {
            controller.excluirAndar(id);
        }

        @Override
        protected void carregar() {
            modelo.setRowCount(0);
            for (Andar andar : controller.listarAndares()) {
                modelo.addRow(new Object[] {andar.getIdAndar(), andar.getNome(), andar.getNivel(),
                    andar.getNomeBloco()});
            }
        }

        @Override
        protected void recarregarCombos() {
            JComboBox<Object> combo = comboBloco();
            combo.removeAllItems();
            for (Bloco bloco : controller.listarBlocos()) {
                combo.addItem(bloco);
            }
        }

        @SuppressWarnings("unchecked")
        private JComboBox<Object> comboBloco() {
            return (JComboBox<Object>) form.componente("bloco");
        }
    }

    private class AbaPontos extends AbaCrud {

        AbaPontos() {
            super(new String[] {"ID", "Nome", "Tipo", "Andar", "X", "Y"});
            form.campoCombo("andar", "Andar", new Object[0]);
            form.campoTexto("nome", "Nome do ponto");
            form.campoCombo("tipo", "Tipo", new Object[] {"SALA", "PASSAGEM", "ACESSO"});
            form.campoTexto("x", "Coordenada X");
            form.campoTexto("y", "Coordenada Y");
        }

        @Override
        protected int idDaLinha(int linha) {
            return inteiroDaLinha(linha, 0);
        }

        @Override
        protected void preencher(int linha) {
            int id = idDaLinha(linha);
            form.definir("nome", String.valueOf(modelo.getValueAt(linha, 1)));
            form.definir("tipo", String.valueOf(modelo.getValueAt(linha, 2)));
            form.definir("x", String.valueOf(modelo.getValueAt(linha, 4)));
            form.definir("y", String.valueOf(modelo.getValueAt(linha, 5)));
            for (Ponto ponto : controller.listarPontos()) {
                if (ponto.getIdPonto() == id) {
                    selecionarPorId(comboAndar(), ponto.getIdAndar());
                    break;
                }
            }
        }

        @SuppressWarnings("unchecked")
        private JComboBox<Object> comboAndar() {
            return (JComboBox<Object>) form.componente("andar");
        }

        @Override
        protected void gravar() {
            Ponto ponto = new Ponto();
            ponto.setIdPonto(idSelecionado);
            ponto.setNome(form.texto("nome"));
            ponto.setTipo(TipoPonto.de(form.texto("tipo")));
            ponto.setCoordenadaX(form.duplo("x"));
            ponto.setCoordenadaY(form.duplo("y"));
            Object andar = form.objeto("andar");
            ponto.setIdAndar(andar instanceof Andar a ? a.getIdAndar() : null);
            controller.salvarPonto(ponto);
            idSelecionado = null;
            form.limpar();
        }

        @Override
        protected void remover(int id) {
            controller.excluirPonto(id);
        }

        @Override
        protected void carregar() {
            modelo.setRowCount(0);
            for (Ponto ponto : controller.listarPontos()) {
                modelo.addRow(new Object[] {ponto.getIdPonto(), ponto.getNome(),
                    ponto.getTipo() == null ? "" : ponto.getTipo().name(),
                    ponto.getNomeAndar(), ponto.getCoordenadaX(), ponto.getCoordenadaY()});
            }
        }

        @Override
        protected void recarregarCombos() {
            JComboBox<Object> combo = comboAndar();
            combo.removeAllItems();
            for (Andar andar : controller.listarAndares()) {
                combo.addItem(andar);
            }
        }
    }

    private class AbaTrechos extends AbaCrud {

        AbaTrechos() {
            super(new String[] {"ID", "Ponto A", "Ponto B", "Distancia", "Escadas", "Elevador",
                "Coberto", "Interditado"});
            form.campoCombo("a", "Ponto A", new Object[0]);
            form.campoCombo("b", "Ponto B", new Object[0]);
            form.campoTexto("distancia", "Distancia (m)");
            form.campoCheck("escadas", "Possui escadas");
            form.campoCheck("elevador", "Usa elevador");
            form.campoCheck("coberto", "Coberto");
            form.campoCheck("interditado", "Interditado");
        }

        @Override
        protected int idDaLinha(int linha) {
            return inteiroDaLinha(linha, 0);
        }

        @Override
        protected void preencher(int linha) {
            int id = idDaLinha(linha);
            form.definir("distancia", String.valueOf(modelo.getValueAt(linha, 3)));
            form.marcar("escadas", "Sim".equals(modelo.getValueAt(linha, 4)));
            form.marcar("elevador", "Sim".equals(modelo.getValueAt(linha, 5)));
            form.marcar("coberto", "Sim".equals(modelo.getValueAt(linha, 6)));
            form.marcar("interditado", "Sim".equals(modelo.getValueAt(linha, 7)));
            for (Trecho trecho : controller.listarTrechos()) {
                if (trecho.getIdTrecho() == id) {
                    selecionarPorId(combo("a"), trecho.getIdPontoA());
                    selecionarPorId(combo("b"), trecho.getIdPontoB());
                    break;
                }
            }
        }

        @SuppressWarnings("unchecked")
        private JComboBox<Object> combo(String chave) {
            return (JComboBox<Object>) form.componente(chave);
        }

        @Override
        protected void gravar() {
            Trecho trecho = new Trecho();
            trecho.setIdTrecho(idSelecionado);
            Object a = form.objeto("a");
            Object b = form.objeto("b");
            trecho.setIdPontoA(a instanceof Ponto pa ? pa.getIdPonto() : null);
            trecho.setIdPontoB(b instanceof Ponto pb ? pb.getIdPonto() : null);
            Double distancia = form.duplo("distancia");
            trecho.setDistanciaMetros(distancia == null ? 0 : distancia);
            trecho.setPossuiEscadas(form.marcado("escadas"));
            trecho.setUsaElevador(form.marcado("elevador"));
            trecho.setCoberto(form.marcado("coberto"));
            trecho.setInterditado(form.marcado("interditado"));
            controller.salvarTrecho(trecho);
            idSelecionado = null;
            form.limpar();
        }

        @Override
        protected void remover(int id) {
            controller.excluirTrecho(id);
        }

        @Override
        protected void carregar() {
            modelo.setRowCount(0);
            for (Trecho trecho : controller.listarTrechos()) {
                modelo.addRow(new Object[] {trecho.getIdTrecho(), trecho.getNomePontoA(),
                    trecho.getNomePontoB(), trecho.getDistanciaMetros(),
                    trecho.isPossuiEscadas() ? "Sim" : "Nao",
                    trecho.isUsaElevador() ? "Sim" : "Nao",
                    trecho.isCoberto() ? "Sim" : "Nao",
                    trecho.isInterditado() ? "Sim" : "Nao"});
            }
        }

        @Override
        protected void recarregarCombos() {
            List<Ponto> pontos = controller.listarPontos();
            for (String chave : new String[] {"a", "b"}) {
                JComboBox<Object> combo = combo(chave);
                combo.removeAllItems();
                for (Ponto ponto : pontos) {
                    combo.addItem(ponto);
                }
            }
        }
    }

    private class AbaProfessores extends AbaCrud {

        AbaProfessores() {
            super(new String[] {"ID", "Nome", "Login", "Sala"});
            form.campoTexto("nome", "Nome");
            form.campoTexto("login", "Login");
            form.campoSenha("senha", "Senha (so no cadastro)");
            form.campoTexto("foto", "Foto de perfil");
        }

        @Override
        protected int idDaLinha(int linha) {
            return inteiroDaLinha(linha, 0);
        }

        @Override
        protected void preencher(int linha) {
            form.definir("nome", String.valueOf(modelo.getValueAt(linha, 1)));
            form.definir("login", String.valueOf(modelo.getValueAt(linha, 2)));
            form.definir("senha", "");
            for (Professor professor : controller.listarProfessores()) {
                if (professor.getIdProfessor() == idDaLinha(linha)) {
                    form.definir("foto", professor.getFotoPerfil() == null ? "" : professor.getFotoPerfil());
                    break;
                }
            }
        }

        @Override
        protected void gravar() {
            String nome = form.texto("nome");
            String login = form.texto("login");
            String senha = form.texto("senha");
            if (idSelecionado == null) {
                if (senha.isEmpty()) {
                    throw new IllegalArgumentException("Informe a senha do novo professor.");
                }
                Professor professor = new Professor(nome, login, Senha.hash(senha));
                professor.setFotoPerfil(form.texto("foto"));
                controller.salvarProfessor(professor, senha);
            } else {
                Professor professor = null;
                for (Professor item : controller.listarProfessores()) {
                    if (item.getIdProfessor().equals(idSelecionado)) {
                        professor = item;
                        break;
                    }
                }
                if (professor == null) {
                    throw new IllegalArgumentException("Professor nao encontrado.");
                }
                professor.setNome(nome);
                professor.setLogin(login);
                professor.setFotoPerfil(form.texto("foto"));
                controller.salvarProfessor(professor, senha);
            }
            idSelecionado = null;
            form.limpar();
        }

        @Override
        protected void remover(int id) {
            controller.excluirProfessor(id);
        }

        @Override
        protected void carregar() {
            modelo.setRowCount(0);
            for (Professor professor : controller.listarProfessores()) {
                modelo.addRow(new Object[] {professor.getIdProfessor(), professor.getNome(),
                    professor.getLogin(), professor.getNomeSala()});
            }
        }
    }

    private class AbaAdministradores extends AbaCrud {

        AbaAdministradores() {
            super(new String[] {"ID", "Nome", "E-mail"});
            form.campoTexto("nome", "Nome");
            form.campoTexto("email", "E-mail");
            form.campoSenha("senha", "Senha");
        }

        @Override
        protected int idDaLinha(int linha) {
            return inteiroDaLinha(linha, 0);
        }

        @Override
        protected void preencher(int linha) {
            form.definir("nome", String.valueOf(modelo.getValueAt(linha, 1)));
            form.definir("email", String.valueOf(modelo.getValueAt(linha, 2)));
            form.definir("senha", "");
        }

        @Override
        protected void gravar() {
            String senha = form.texto("senha");
            Administrador administrador = new Administrador();
            administrador.setIdAdmin(idSelecionado);
            administrador.setNomeAdmin(form.texto("nome"));
            administrador.setEmailAdmin(form.texto("email"));
            if (!senha.isEmpty()) {
                administrador.setSenhaAdmin(Senha.hash(senha));
            } else if (idSelecionado != null) {
                for (Administrador item : controller.listarAdministradores()) {
                    if (item.getIdAdmin().equals(idSelecionado)) {
                        administrador.setSenhaAdmin(item.getSenhaAdmin());
                        break;
                    }
                }
            } else {
                throw new IllegalArgumentException("Informe a senha do novo administrador.");
            }
            controller.salvarAdministrador(administrador);
            idSelecionado = null;
            form.limpar();
        }

        @Override
        protected void remover(int id) {
            controller.excluirAdministrador(id);
        }

        @Override
        protected void carregar() {
            modelo.setRowCount(0);
            for (Administrador administrador : controller.listarAdministradores()) {
                modelo.addRow(new Object[] {administrador.getIdAdmin(), administrador.getNomeAdmin(),
                    administrador.getEmailAdmin()});
            }
        }
    }
}
