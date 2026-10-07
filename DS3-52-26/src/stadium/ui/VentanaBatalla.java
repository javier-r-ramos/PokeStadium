package stadium.ui;

import stadium.api.PokeApiClient;
import stadium.batalla.Battle;
import stadium.batalla.BattleListener;
import stadium.modelo.Pokemon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//Ventana principal. El diseno esta en VentanaBatalla.form.
//Implementa BattleListener: durante el combate solo cambia lo que se ve
//en pantalla cuando la clase Battle le avisa un evento.
public class VentanaBatalla implements BattleListener
{
    //componentes del disenador (VentanaBatalla.form)
    private JPanel mainPanel;
    private PanelPokemon panel1;
    private PanelPokemon panel2;
    private JButton botonFight;
    private JScrollPane scrollLog;
    private JTextArea areaLog;

    private final PokeApiClient cliente = new PokeApiClient();

    public VentanaBatalla()
    {
        panel1.configurar("Jugador 1", cliente, this::revisarBotonFight);
        panel2.configurar("Jugador 2", cliente, this::revisarBotonFight);

        //marco amarillo del log, como el cuadro de texto de los juegos
        scrollLog.setBorder(BorderFactory.createLineBorder(new Color(255, 203, 5), 3));

        botonFight.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                iniciarCombate();
            }
        });
    }

    //El diseñador llama este metodo para los componentes marcados con "Custom Create".
    //El panel principal no es un JPanel normal sino el fondo dibujado del estadio.
    private void createUIComponents()
    {
        mainPanel = new PanelFondo();
    }

    public JPanel getMainPanel()
    {
        return mainPanel;
    }

    //la llaman los paneles cada vez que terminan de cargar un pokemon
    private void revisarBotonFight()
    {
        botonFight.setEnabled(panel1.getPokemon() != null && panel2.getPokemon() != null);
    }

    private void iniciarCombate()
    {
        Pokemon pokemon1 = panel1.getPokemon();
        Pokemon pokemon2 = panel2.getPokemon();

        //los eventos identifican al pokemon por el nombre, por eso no pueden ser iguales
        if (pokemon1.getNombre().equals(pokemon2.getNombre()))
        {
            JOptionPane.showMessageDialog(mainPanel, "Elige dos pokemon diferentes para pelear");
            return;
        }

        setControlesActivos(false);
        areaLog.setText("");
        escribirLog("=== " + pokemon1.getNombre() + " VS " + pokemon2.getNombre() + " ===");

        //el combate tiene pausas entre turnos, por eso corre en otro hilo
        Battle battle = new Battle(pokemon1, pokemon2, this);
        Thread hilo = new Thread(() -> battle.iniciar());
        hilo.start();
    }

    private void setControlesActivos(boolean activos)
    {
        botonFight.setEnabled(activos);
        panel1.setBotonesActivos(activos);
        panel2.setBotonesActivos(activos);
    }

    private void escribirLog(String texto)
    {
        areaLog.append(texto + "\n");
        //baja el scroll para que siempre se vea la ultima linea
        areaLog.setCaretPosition(areaLog.getDocument().getLength());
    }

    //devuelve el panel del lado al que pertenece ese pokemon
    private PanelPokemon buscarPanel(String nombrePokemon)
    {
        if (panel1.getPokemon().getNombre().equals(nombrePokemon))
        {
            return panel1;
        }
        return panel2;
    }

    // ---------- Eventos de la batalla ----------
    //Battle los llama desde su propio hilo, por eso cada uno usa
    //invokeLater para hacer el cambio en el hilo de la interfaz.

    @Override
    public void onBattleStarted(String first)
    {
        SwingUtilities.invokeLater(() -> escribirLog("Empieza " + first + " por tener mas speed (o por sorteo si empatan)"));
    }

    @Override
    public void onTurn(String attacker, String defender, int damage, boolean critical, double modifier)
    {
        String texto = attacker + " ataca a " + defender + " y le hace " + damage + " de daño";

        if (critical)
        {
            texto = texto + " (CRITICO!)";
        }

        if (modifier > 1.0)
        {
            texto = texto + " (es muy efectivo x" + modifier + ")";
        }
        else if (modifier < 1.0)
        {
            texto = texto + " (es poco efectivo x" + modifier + ")";
        }

        String linea = texto;
        SwingUtilities.invokeLater(() -> escribirLog(linea));
    }

    @Override
    public void onHpChanged(String pokemon, int hpActual)
    {
        SwingUtilities.invokeLater(() ->
        {
            buscarPanel(pokemon).actualizarHp(hpActual);
            escribirLog("   " + pokemon + " tiene " + hpActual + " HP");
        });
    }

    @Override
    public void onBattleEnded(String winner)
    {
        SwingUtilities.invokeLater(() ->
        {
            escribirLog("=== GANADOR: " + winner + " ===");
            setControlesActivos(true);
            JOptionPane.showMessageDialog(mainPanel, "El ganador es " + winner + "!");
        });
    }
}
