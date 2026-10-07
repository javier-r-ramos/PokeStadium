package stadium.batalla;

//Eventos que la batalla le avisa a quien la este escuchando (la ventana).
//Asi la clase Battle no conoce nada de Swing.
public interface BattleListener
{
    //se llama una sola vez, cuando ya se sabe quien pega primero
    void onBattleStarted(String first);

    //se llama cada vez que un pokemon ataca
    void onTurn(String attacker, String defender, int damage, boolean critical, double modifier);

    //se llama cada vez que cambia la vida de un pokemon
    void onHpChanged(String pokemon, int hpActual);

    //se llama cuando un pokemon llega a 0 de HP
    void onBattleEnded(String winner);
}
