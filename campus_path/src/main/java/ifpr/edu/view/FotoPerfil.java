package ifpr.edu.view;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class FotoPerfil {

    private static final Map<String, BufferedImage> CACHE = new HashMap<>();

    private FotoPerfil() {
    }

    public static BufferedImage imagem(String caminho, String nome, int tamanho) {
        int lado = Math.max(8, tamanho);
        String chave = chave(caminho, lado);
        BufferedImage emCache = CACHE.get(chave);
        if (emCache != null) {
            return emCache;
        }
        BufferedImage imagem = carregar(caminho, lado);
        if (imagem == null) {
            imagem = inicial(nome, lado);
        }
        CACHE.put(chave, imagem);
        return imagem;
    }

    private static String chave(String caminho, int lado) {
        long modificacao = 0;
        if (caminho != null && !caminho.isBlank()) {
            File arquivo = new File(caminho);
            modificacao = arquivo.lastModified();
        }
        return caminho + "|" + lado + "|" + modificacao;
    }

    private static BufferedImage carregar(String caminho, int lado) {
        if (caminho == null || caminho.isBlank()) {
            return null;
        }
        File arquivo = new File(caminho);
        if (!arquivo.isFile()) {
            return null;
        }
        BufferedImage original;
        try {
            original = ImageIO.read(arquivo);
        } catch (Exception e) {
            return null;
        }
        if (original == null) {
            return null;
        }
        BufferedImage destino = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = destino.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setClip(new Ellipse2D.Double(0, 0, lado, lado));
        double escala = Math.max((double) lado / original.getWidth(), (double) lado / original.getHeight());
        int largura = (int) Math.round(original.getWidth() * escala);
        int altura = (int) Math.round(original.getHeight() * escala);
        g2.drawImage(original, (lado - largura) / 2, (lado - altura) / 2, largura, altura, null);
        g2.setClip(null);
        g2.setColor(new Color(0x2B3A4A));
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new Ellipse2D.Double(0.5, 0.5, lado - 1, lado - 1));
        g2.dispose();
        return destino;
    }

    private static BufferedImage inicial(String nome, int lado) {
        String texto = nome == null || nome.isBlank() ? "?" : nome.trim().substring(0, 1).toUpperCase();
        int semente = nome == null ? 0 : nome.hashCode();
        float matiz = (Math.abs(semente) % 360) / 360f;
        Color cor = Color.getHSBColor(matiz, 0.45f, 0.55f);
        BufferedImage destino = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = destino.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(cor);
        g2.fill(new Ellipse2D.Double(0, 0, lado, lado));
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(9, lado / 2)));
        FontMetrics metricas = g2.getFontMetrics();
        int largura = metricas.stringWidth(texto);
        int altura = metricas.getAscent();
        g2.drawString(texto, (lado - largura) / 2, (lado + altura) / 2 - 1);
        g2.dispose();
        return destino;
    }
}
