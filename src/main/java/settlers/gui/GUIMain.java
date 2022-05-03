package settlers.gui;

import settlers.Player;
import settlers.Action;
import settlers.card.Resource;
import settlers.board.*;

import java.util.HashMap;

public interface GUIMain {

    /**
     *Asks Main if the player can preform the specified action. Returns true if yes, false if no
     * @param player
     * @param action
     * @return
     */
    public boolean canPreformAction(Player player, Action action);

    /**
     * Tells Main to preform the specified action for the player
     * @param player
     * @param action
     */
    public void preformAction(Player player, Action action);

    /**
     * Triggered by Main on a player's turn when it wants to know what action the player will take
     * GUIMain contacts GUIPlayer or whatever, and GUIPlayer finds what action the player wants
     * It will then call the appropriate method in Main to find out if the player can do that, if applicable
     * It will then call the method to get the places where the player can do it, if applicable
     * It will then execute the action on what the player chooses, whether this is building, playing development cards
     *    or trading
     * It will then return true
     * If the player decides instead to end his turn, the method returns false
     * @param player
     * @return
     */
    public boolean getAction(Player player);

    /**
     * Triggered by Main to inform the PlayerGUIs that an action was processed and to update accordingly
     * @param action
     */
    public void reportAction(Action action);
}