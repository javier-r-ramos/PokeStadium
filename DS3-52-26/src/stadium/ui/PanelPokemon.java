package stadium.ui;

import stadium.api.PokeApiClient;
import stadium.modelo.Pokemon;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//Panel de un jugador: permite elegir el pokemon y muestra sus datos.
//El diseno esta en PanelPokemon.form y la ventana lo usa dos veces, una por cada lado.
public class PanelPokemon
{
    private static final int TAMANO_SPRITE = 160;

    private static final Color VERDE = new Color(60, 170, 80);
    private static final Color AMARILLO = new Color(240, 190, 40);
    private static final Color ROJO = new Color(220, 60, 50);

    //componentes del diseñador (PanelPokemon.form)
    private JPanel panelRaiz;
    private JLabel labelTitulo;
    private JTextField campoNombre;
    private JButton botonLoad;
    private JButton botonRandom;
    private JLabel labelSprite;
    private JLabel labelNombre;
    private JLabel labelTipos;
    private JLabel labelStats;
    private JLabel labelHp;
    private JProgressBar barraHp;
    private JLabel labelEstado;

    private PokeApiClient cliente;

    //lo que se ejecuta cuando se termina de cargar un pokemon (lo pone la ventana)
    private Runnable alCargar;

    //queda en null mientras no se haya cargado ningún pokemon
    private Pokemon pokemon;

    public PanelPokemon()
    {
        //se usa la barra basica para que se vea igual en Mac y en Windows
        barraHp.setUI(new BasicProgressBarUI());
        barraHp.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        botonLoad.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                cargarPokemon(campoNombre.getText());
            }
        });

        botonRandom.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                cargarPokemon(null);
            }
        });
    }

    //El diseñador llama este metodo para los componentes marcados con "Custom Create".
    //El panel principal no es un JPanel normal sino la tarjeta redonda.
    private void createUIComponents()
    {
        panelRaiz = new PanelTarjeta();
    }

    //la ventana le pasa a cada lado su titulo, el cliente de la API y a quien avisar
    public void configurar(String titulo, PokeApiClient cliente, Runnable alCargar)
    {
        labelTitulo.setText(titulo);
        this.cliente = cliente;
        this.alCargar = alCargar;
    }

    //Consulta la API en un hilo aparte para no congelar la ventana.
    //Si el nombre llega en null se busca un pokemon aleatorio.
    private void cargarPokemon(String nombre)
    {
        setBotonesActivos(false);
        labelEstado.setForeground(Color.DARK_GRAY);
        labelEstado.setText("Cargando...");

        Thread hilo = new Thread(() ->
        {
            try
            {
                Pokemon encontrado;
                if (nombre == null)
                {
                    encontrado = cliente.buscarPokemonAleatorio();
                }
                else
                {
                    encontrado = cliente.buscarPokemon(nombre);
                }

                Image sprite = cliente.descargarSprite(encontrado);

                //los componentes de Swing solo se pueden tocar desde el hilo de la interfaz
                SwingUtilities.invokeLater(() -> mostrarPokemon(encontrado, sprite));
            }
            catch (Exception e)
            {
                SwingUtilities.invokeLater(() -> mostrarError(e.getMessage()));
            }
        });
        hilo.start();
    }

    private void mostrarPokemon(Pokemon encontrado, Image sprite)
    {
        pokemon = encontrado;

        if (sprite == null)
        {
            labelSprite.setIcon(null);
            labelSprite.setText("?");
        }
        else
        {
            //el sprite original es muy pequeno, se agranda para que se vea bien
            Image grande = sprite.getScaledInstance(TAMANO_SPRITE, TAMANO_SPRITE, Image.SCALE_DEFAULT);
            labelSprite.setIcon(new ImageIcon(grande));
            labelSprite.setText("");
        }

        campoNombre.setText(pokemon.getNombre());
        labelNombre.setText(pokemon.getNombre());
        labelTipos.setText("Tipos: " + String.join(", ", pokemon.getTipos()));
        labelStats.setText("HP: " + pokemon.getHpMaximo()
                + "   Attack: " + pokemon.getAttack()
                + "   Defense: " + pokemon.getDefense()
                + "   Speed: " + pokemon.getSpeed());

        barraHp.setMaximum(pokemon.getHpMaximo());
        actualizarHp(pokemon.getHpMaximo());

        labelEstado.setText(" ");
        setBotonesActivos(true);

        //se le avisa a la ventana para que revise si ya puede activar "Fight!"
        alCargar.run();
    }

    //si falla la carga, se deja el pokemon que ya estaba y se muestra el error
    private void mostrarError(String mensaje)
    {
        labelEstado.setForeground(Color.RED);
        labelEstado.setText(mensaje);
        setBotonesActivos(true);
    }

    public void actualizarHp(int hpActual)
    {
        barraHp.setValue(hpActual);

        //la barra cambia de color como en los juegos: verde, amarillo y rojo
        double porcentaje = (double) hpActual / barraHp.getMaximum();
        if (porcentaje > 0.5)
        {
            barraHp.setForeground(VERDE);
        }
        else if (porcentaje > 0.2)
        {
            barraHp.setForeground(AMARILLO);
        }
        else
        {
            barraHp.setForeground(ROJO);
        }

        labelHp.setText("HP " + hpActual + " / " + barraHp.getMaximum());
    }

    //se usa para bloquear los botones mientras se carga o mientras hay combate
    public void setBotonesActivos(boolean activos)
    {
        botonLoad.setEnabled(activos);
        botonRandom.setEnabled(activos);
        campoNombre.setEnabled(activos);
    }

    public Pokemon getPokemon()
    {
        return pokemon;
    }
}
