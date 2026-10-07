package stadium.modelo;

import java.util.List;

//Modelo: guarda los datos de un pokemon y su HP actual durante el combate
public class Pokemon
{
    private final String nombre;
    private final List<String> tipos;
    private final String spriteUrl;
    private final int hpMaximo;
    private final int attack;
    private final int defense;
    private final int speed;

    //es el unico dato que cambia durante la pelea
    private int hpActual;

    public Pokemon(String nombre, List<String> tipos, String spriteUrl, int hp, int attack, int defense, int speed)
    {
        this.nombre = nombre;
        this.tipos = tipos;
        this.spriteUrl = spriteUrl;
        this.hpMaximo = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.hpActual = hp;
    }

    //le quita vida al pokemon, el HP nunca queda negativo
    public void recibirDanio(int danio)
    {
        hpActual = hpActual - danio;
        if (hpActual < 0)
        {
            hpActual = 0;
        }
    }

    //deja al pokemon con la vida llena, se usa al empezar cada combate
    public void restaurarHp()
    {
        hpActual = hpMaximo;
    }

    public boolean estaVivo()
    {
        return hpActual > 0;
    }

    //para la efectividad solo se usa el primer tipo
    public String getPrimerTipo()
    {
        return tipos.get(0);
    }

    public String getNombre()
    {
        return nombre;
    }

    public List<String> getTipos()
    {
        return tipos;
    }

    public String getSpriteUrl()
    {
        return spriteUrl;
    }

    public int getHpMaximo()
    {
        return hpMaximo;
    }

    public int getHpActual()
    {
        return hpActual;
    }

    public int getAttack()
    {
        return attack;
    }

    public int getDefense()
    {
        return defense;
    }

    public int getSpeed()
    {
        return speed;
    }
}
