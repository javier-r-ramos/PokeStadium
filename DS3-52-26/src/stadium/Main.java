package stadium;

import stadium.ui.VentanaBatalla;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main
{
    public static void main(String[] args)
    {
        //la ventana se crea en el hilo de la interfaz de Swing
        SwingUtilities.invokeLater(() ->
        {
            JFrame frame = new JFrame("Pokemon Stadium");
            frame.setContentPane(new VentanaBatalla().getMainPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            //en Mac a veces la ventana queda escondida detras de IntelliJ,
            //con esto se obliga a que salga al frente
            frame.setAlwaysOnTop(true);
            frame.toFront();
            frame.setAlwaysOnTop(false);
        });
    }
}
