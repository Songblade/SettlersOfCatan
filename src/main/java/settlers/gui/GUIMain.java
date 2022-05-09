package settlers.gui;

import settlers.Player;
import settlers.Action;
import settlers.board.*;

import java.util.Set;

public interface GUIMain {

    /**
     *Asks Main if the player can preform the specified action. Returns true if yes, false if no
     * @param action
     * @return
     */
    public boolean canPreformAction(Action action);

    /**
     * Tells Main to preform the specified action for the player
     * @param action
     */
    public void preformAction(Action action);

    /**
     * Starts a turn. Called by Main, updates resources and die number in GUIPlayers
     * @param player the player whose turn it is
     * @param dieRoll the sum of both die rolls
     */
    public void startTurn(Player player, int dieRoll);

    /**
     * Called by Main. Has the player built a settlement and road during setup
     * @param player whose turn it is
     * @param validSpots where a settlement can be built during setup
     * @return the Vertex where the player built a Settlement
     */
    public Vertex startSetupTurn(Player player, Set<Vertex> validSpots);
}