package stadium.ui;

import javax.swing.*;
import java.awt.*;

//Tarjeta blanca medio transparente con esquinas redondas.
//Es el fondo del panel de cada jugador.
public class PanelTarjeta extends JPanel
{
    public PanelTarjeta()
    {
        //el panel es transparente para que se vea el fondo del estadio
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        g2.setColor(new Color(42, 77, 160));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 30, 30);
    }
}
