package settlers.gui;

import settlers.Player;
import settlers.Action;
import settlers.card.Resource;
import settlers.board.*;

import java.util.HashMap;
import java.util.List;

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
     *
     * @param player
     * @param validSpots
     */
    public void startSetupTurn(Player player, List<Vertex> validSpots);
}