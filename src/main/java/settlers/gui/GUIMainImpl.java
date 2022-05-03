package settlers.gui;

import settlers.Action;
import settlers.Main;
import settlers.Player;
import settlers.card.Resource;
import settlers.board.*;

import java.util.HashMap;

public class GUIMainImpl implements GUIMain {

    private Main main;
    private GUIPlayer[] playerGUIs;

    public GUIMainImpl(Main main) {
        this.main = main;
        playerGUIs = new GUIPlayer[4];
    }

    /** Temp Constructor */
    public GUIMainImpl(Board board){
        this.main = null;
        playerGUIs = new GUIPlayer[1];
        playerGUIs[0] = new GUIPlayerImpl(board);
    }

    /**
     *Asks Main if the player can preform the specified action. Returns true if yes, false if no
     * @param player
     * @param action
     * @return
     */
    @Override
    public boolean canPreformAction(Player player, Action action) {
        return false;
    }

    /**
     * Tells Main to preform the specified action for the player
     * @param player
     * @param action
     */
    @Override
    public void preformAction(Player player, Action action) {

    }

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
    @Override
    public boolean getAction(Player player) {
        return false;
    }

    /**
     * Triggered by Main to inform the PlayerGUIs that an action was processed and to update accordingly
     * @param action
     */
    @Override
    public void reportAction(Action action){

    }
}