package stadium.ui;

import javax.swing.*;
import java.awt.*;

//Fondo de la ventana: se dibuja a mano un estadio con cielo, pasto,
//una pokebola gigante y el titulo con los colores del logo de Pokemon.
//Lo que va encima (paneles, boton, log) se acomoda en VentanaBatalla.form
public class PanelFondo extends JPanel
{
    private static final Color AMARILLO = new Color(255, 203, 5);
    private static final Color AZUL = new Color(42, 77, 160);

    //Swing llama este metodo cada vez que necesita pintar el panel
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        //suaviza los bordes de las figuras y las letras
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();

        //cielo: un degradado de azul oscuro arriba a azul claro abajo
        g2.setPaint(new GradientPaint(0, 0, new Color(40, 90, 180), 0, alto, new Color(150, 210, 255)));
        g2.fillRect(0, 0, ancho, alto);

        //pasto: un ovalo verde muy ancho que solo se asoma por abajo
        g2.setColor(new Color(90, 180, 90));
        g2.fillOval(-ancho / 2, alto / 2, ancho * 2, alto);

        dibujarPokebola(g2, ancho / 2, alto / 2 - 40, 300);
        dibujarTitulo(g2, "POKEMON STADIUM LITE", ancho);
    }

    //pokebola transparente en el centro del estadio
    private void dibujarPokebola(Graphics2D g2, int centroX, int centroY, int tamano)
    {
        int x = centroX - tamano / 2;
        int y = centroY - tamano / 2;

        //mitad de arriba roja y mitad de abajo blanca (el 70 es la transparencia)
        g2.setColor(new Color(230, 50, 50, 70));
        g2.fillArc(x, y, tamano, tamano, 0, 180);
        g2.setColor(new Color(255, 255, 255, 70));
        g2.fillArc(x, y, tamano, tamano, 180, 180);

        //linea del medio y boton del centro
        g2.setColor(new Color(30, 30, 30, 70));
        g2.setStroke(new BasicStroke(8));
        g2.drawOval(x, y, tamano, tamano);
        g2.drawLine(x, centroY, x + tamano, centroY);
        g2.setColor(new Color(255, 255, 255, 120));
        g2.fillOval(centroX - 30, centroY - 30, 60, 60);
        g2.setColor(new Color(30, 30, 30, 70));
        g2.drawOval(centroX - 30, centroY - 30, 60, 60);
    }

    //titulo amarillo con borde azul, centrado arriba
    private void dibujarTitulo(Graphics2D g2, String texto, int ancho)
    {
        g2.setFont(new Font("SansSerif", Font.BOLD, 40));
        int x = (ancho - g2.getFontMetrics().stringWidth(texto)) / 2;
        int y = 55;

        //el borde se logra pintando el texto azul corrido hacia los 8 lados
        g2.setColor(AZUL);
        for (int dx = -3; dx <= 3; dx += 3)
        {
            for (int dy = -3; dy <= 3; dy += 3)
            {
                g2.drawString(texto, x + dx, y + dy);
            }
        }

        g2.setColor(AMARILLO);
        g2.drawString(texto, x, y);
    }
}
