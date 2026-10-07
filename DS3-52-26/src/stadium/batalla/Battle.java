package stadium.batalla;

import stadium.modelo.Pokemon;

import java.util.Random;

//Reglas del combate por turnos. No tiene nada de interfaz:
//todo lo que pasa se avisa por medio del BattleListener.
public class Battle
{
    private static final double PROBABILIDAD_CRITICO = 0.10;
    private static final double MULTIPLICADOR_CRITICO = 1.5;

    //tiempo de espera entre turnos para que el combate se alcance a ver
    private static final int PAUSA_ENTRE_TURNOS = 800;

    private final Pokemon pokemon1;
    private final Pokemon pokemon2;
    private final BattleListener listener;
    private final Random random = new Random();

    public Battle(Pokemon pokemon1, Pokemon pokemon2, BattleListener listener)
    {
        this.pokemon1 = pokemon1;
        this.pokemon2 = pokemon2;
        this.listener = listener;
    }

    //Ejecuta el combate completo. Como tiene pausas, se debe llamar
    //desde un hilo aparte y no desde el hilo de la interfaz.
    public void iniciar()
    {
        //los dos empiezan con la vida llena
        pokemon1.restaurarHp();
        pokemon2.restaurarHp();

        //empieza el de mayor speed, si empatan se decide al azar
        Pokemon atacante;
        Pokemon defensor;

        if (pokemon1.getSpeed() > pokemon2.getSpeed())
        {
            atacante = pokemon1;
            defensor = pokemon2;
        }
        else if (pokemon2.getSpeed() > pokemon1.getSpeed())
        {
            atacante = pokemon2;
            defensor = pokemon1;
        }
        else if (random.nextBoolean())
        {
            atacante = pokemon1;
            defensor = pokemon2;
        }
        else
        {
            atacante = pokemon2;
            defensor = pokemon1;
        }

        listener.onBattleStarted(atacante.getNombre());
        listener.onHpChanged(pokemon1.getNombre(), pokemon1.getHpActual());
        listener.onHpChanged(pokemon2.getNombre(), pokemon2.getHpActual());

        //se pelea hasta que uno de los dos se quede sin vida
        while (pokemon1.estaVivo() && pokemon2.estaVivo())
        {
            pausar();
            atacar(atacante, defensor);

            //se cambian los papeles para el siguiente turno
            Pokemon temporal = atacante;
            atacante = defensor;
            defensor = temporal;
        }

        if (pokemon1.estaVivo())
        {
            listener.onBattleEnded(pokemon1.getNombre());
        }
        else
        {
            listener.onBattleEnded(pokemon2.getNombre());
        }
    }

    //un turno: se calcula el danio, se aplica y se avisa al listener
    private void atacar(Pokemon atacante, Pokemon defensor)
    {
        boolean critico = random.nextDouble() < PROBABILIDAD_CRITICO;
        double efectividad = calcularEfectividad(atacante.getPrimerTipo(), defensor.getPrimerTipo());
        int danio = calcularDanio(atacante, defensor, critico, efectividad);

        defensor.recibirDanio(danio);

        listener.onTurn(atacante.getNombre(), defensor.getNombre(), danio, critico, efectividad);
        listener.onHpChanged(defensor.getNombre(), defensor.getHpActual());
    }

    //Formula del daño:
    //  fuerza  = ATK * random(0.5 a 1.0)   -> el ataque siempre pega al menos la mitad
    //  bloqueo = DEF * random(0.0 a 0.5)   -> la defensa frena como mucho la mitad
    //  danio   = (fuerza - bloqueo) / 2    -> se divide en 2 para que el combate dure varios turnos
    //Despues se multiplica por el critico y la efectividad, y como minimo se hace 1 de danio.
    private int calcularDanio(Pokemon atacante, Pokemon defensor, boolean critico, double efectividad)
    {
        double fuerza = atacante.getAttack() * (0.5 + random.nextDouble() * 0.5);
        double bloqueo = defensor.getDefense() * (random.nextDouble() * 0.5);
        double danio = (fuerza - bloqueo) / 2;

        if (critico)
        {
            danio = danio * MULTIPLICADOR_CRITICO;
        }

        danio = danio * efectividad;

        if (danio < 1)
        {
            danio = 1;
        }

        return (int) Math.round(danio);
    }

    //Efectividad simple: agua > fuego, fuego > planta, planta > agua
    private double calcularEfectividad(String tipoAtacante, String tipoDefensor)
    {
        if (leGana(tipoAtacante, tipoDefensor))
        {
            return 1.3;
        }

        if (leGana(tipoDefensor, tipoAtacante))
        {
            return 0.7;
        }

        return 1.0;
    }

    private boolean leGana(String tipo, String otroTipo)
    {
        return (tipo.equals("water") && otroTipo.equals("fire"))
                || (tipo.equals("fire") && otroTipo.equals("grass"))
                || (tipo.equals("grass") && otroTipo.equals("water"));
    }

    private void pausar()
    {
        try
        {
            Thread.sleep(PAUSA_ENTRE_TURNOS);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}
