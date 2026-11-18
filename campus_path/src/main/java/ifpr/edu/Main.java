package ifpr.edu;

import ifpr.edu.dao.SnapshotService;
import ifpr.edu.view.JanelaPrincipal;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::abrir);
    }

    private static void abrir() {
        aplicarTema();
        try {
            new SnapshotService().sincronizar();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null,
                "Nao foi possivel preparar o banco de dados.\n\n" + e.getMessage()
                    + "\n\nConfira o arquivo db.properties em src/main/resources.",
                "CampusPath", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new JanelaPrincipal().setVisible(true);
    }

    private static void aplicarTema() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            return;
        }
        Font fonte = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("Label.font", fonte);
        UIManager.put("Button.font", fonte);
        UIManager.put("TextField.font", fonte);
        UIManager.put("PasswordField.font", fonte);
        UIManager.put("TextArea.font", fonte);
        UIManager.put("ComboBox.font", fonte);
        UIManager.put("CheckBox.font", fonte);
        UIManager.put("Table.font", fonte);
        UIManager.put("TableHeader.font", fonte);
        UIManager.put("TabbedPane.font", fonte);
        UIManager.put("TabbedPane.background", new Color(0x17212B));
        UIManager.put("TabbedPane.foreground", new Color(0xE6EDF3));
        UIManager.put("TabbedPane.selected", new Color(0x1E2B38));
        UIManager.put("TabbedPane.contentAreaColor", new Color(0x0F1720));
        UIManager.put("TabbedPane.focus", new Color(0x0F1720));
        UIManager.put("TabbedPane.highlight", new Color(0x2B3A4A));
        UIManager.put("TabbedPane.light", new Color(0x2B3A4A));
        UIManager.put("TabbedPane.shadow", new Color(0x2B3A4A));
        UIManager.put("TabbedPane.darkShadow", new Color(0x2B3A4A));
        UIManager.put("OptionPane.messageFont", fonte);
        UIManager.put("OptionPane.buttonFont", fonte);
        UIManager.put("Panel.background", new Color(0x0F1720));
        UIManager.put("OptionPane.background", new Color(0x17212B));
    }
}
