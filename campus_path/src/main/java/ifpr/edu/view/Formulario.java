package ifpr.edu.view;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedHashMap;
import java.util.Map;

public class Formulario extends JPanel {

    private final Map<String, JComponent> campos = new LinkedHashMap<>();
    private int linha;

    public Formulario() {
        setLayout(new GridBagLayout());
        setBackground(Estilo.PAINEL);
        setBorder(new EmptyBorder(12, 12, 12, 12));
    }

    private GridBagConstraints restricoes(int coluna, int largura) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = coluna;
        c.gridy = linha;
        c.gridwidth = largura;
        c.weightx = coluna == 1 ? 1 : 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        return c;
    }

    private void adicionar(String chave, String rotulo, JComponent componente) {
        JLabel label = new JLabel(rotulo);
        label.setForeground(Estilo.TEXTO_FRACO);
        label.setFont(Estilo.PEQUENA);
        add(label, restricoes(0, 1));
        componente.setFont(Estilo.CORPO);
        add(componente, restricoes(1, 1));
        campos.put(chave, componente);
        linha++;
    }

    public JTextField campoTexto(String chave, String rotulo) {
        JTextField campo = new JTextField();
        adicionar(chave, rotulo, campo);
        return campo;
    }

    public JPasswordField campoSenha(String chave, String rotulo) {
        JPasswordField campo = new JPasswordField();
        adicionar(chave, rotulo, campo);
        return campo;
    }

    public JComboBox<Object> campoCombo(String chave, String rotulo, Object[] opcoes) {
        JComboBox<Object> campo = new JComboBox<>(opcoes);
        adicionar(chave, rotulo, campo);
        return campo;
    }

    public JCheckBox campoCheck(String chave, String rotulo) {
        JCheckBox campo = new JCheckBox(rotulo);
        campo.setBackground(Estilo.PAINEL);
        campo.setForeground(Estilo.TEXTO);
        campo.setFont(Estilo.CORPO);
        add(campo, restricoes(0, 2));
        campos.put(chave, campo);
        linha++;
        return campo;
    }

    public JTextArea campoArea(String chave, String rotulo, int linhasTexto) {
        JTextArea area = new JTextArea(linhasTexto, 20);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(Estilo.CORPO);
        adicionar(chave, rotulo, new JScrollPane(area));
        campos.put(chave, area);
        return area;
    }

    public String texto(String chave) {
        JComponent componente = campos.get(chave);
        if (componente instanceof JPasswordField senha) {
            return new String(senha.getPassword());
        }
        if (componente instanceof JTextArea area) {
            return area.getText().trim();
        }
        if (componente instanceof JTextField campo) {
            return campo.getText().trim();
        }
        return "";
    }

    public Integer inteiro(String chave) {
        String valor = texto(chave);
        if (valor.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Double duplo(String chave) {
        String valor = texto(chave);
        if (valor.isEmpty()) {
            return null;
        }
        try {
            return Double.valueOf(valor.replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean marcado(String chave) {
        JComponent componente = campos.get(chave);
        return componente instanceof JCheckBox check && check.isSelected();
    }

    public Object objeto(String chave) {
        JComponent componente = campos.get(chave);
        return componente instanceof JComboBox<?> combo ? combo.getSelectedItem() : null;
    }

    public JComponent componente(String chave) {
        return campos.get(chave);
    }

    public void definir(String chave, String valor) {
        JComponent componente = campos.get(chave);
        String texto = valor == null ? "" : valor;
        if (componente instanceof JPasswordField senha) {
            senha.setText(texto);
        } else if (componente instanceof JTextArea area) {
            area.setText(texto);
        } else if (componente instanceof JTextField campo) {
            campo.setText(texto);
        }
    }

    public void marcar(String chave, boolean valor) {
        JComponent componente = campos.get(chave);
        if (componente instanceof JCheckBox check) {
            check.setSelected(valor);
        }
    }

    public void limpar() {
        for (JComponent componente : campos.values()) {
            if (componente instanceof JPasswordField senha) {
                senha.setText("");
            } else if (componente instanceof JTextArea area) {
                area.setText("");
            } else if (componente instanceof JTextField campo) {
                campo.setText("");
            } else if (componente instanceof JCheckBox check) {
                check.setSelected(false);
            } else if (componente instanceof JComboBox<?> combo && combo.getItemCount() > 0) {
                combo.setSelectedIndex(0);
            }
        }
    }
}
