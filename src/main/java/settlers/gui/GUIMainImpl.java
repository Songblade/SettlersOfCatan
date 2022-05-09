package settlers.gui;

import settlers.Action;
import settlers.Main;
import settlers.MainImpl;
import settlers.Player;
import settlers.card.Resource;
import settlers.board.*;

import java.util.HashMap;
import java.util.Set;

public class GUIMainImpl implements GUIMain {

    private Main main;
    private GUIPlayer[] playerGUIs;

    public GUIMainImpl(Main main) {
        this.main = main;
        playerGUIs = new GUIPlayer[main.getPlayers().size()];
        for(int i = 0; i < main.getPlayers().size(); i++){
            playerGUIs[i] = new GUIPlayerImpl(this,main.getBoard(),main.getPlayers().get(i),main.getPlayers());
        }
    }


    /**
     *Asks Main if the player can preform the specified action. Returns true if yes, false if no
     * @param action
     * @return
     */
    @Override
    public boolean canPreformAction(Action action) {
        return true;
    }

    /**
     * Tells Main to preform the specified action for the player
     * @param action
     */
    @Override
    public void preformAction(Action action) {

    }

    /**
     * Calls respective method in Main
     * @param player building the settlement
     * @return a Set of Vertices where this player could build
     */
    @Override
    public Set<Vertex> getAvailableSettlementSpots(Player player){
        return main.getAvailableSettlementSpots(player);
    }

    /**
     * Calls respective method in Main
     * @param player building the road
     * @return a Set of Edges where this player could build
     */
    @Override
    public Set<Edge> getAvailableRoadSpots(Player player){
        return main.getAvailableRoadSpots(player);
    }

    /**
     * Calls respective method in Main
     * @param player building the city
     * @return a Set of Vertices where this player could build
     */
    @Override
    public Set<Vertex> getAvailableCitySpots(Player player){
        return main.getAvailableCitySpots(player);
    }

    /**
     * Starts a turn. Called by Main, updates resources and die number in GUIPlayers
     * @param player the player whose turn it is
     * @param dieRoll the sum of both die rolls
     */
    @Override
    public void startTurn(Player player, int dieRoll) {

    }

    /**
     * Called by Main. Has the player built a settlement and road during setup
     * @param player whose turn it is
     * @param validSpots where a settlement can be built during setup
     * @return the Vertex where the player built a Settlement
     */
    @Override
    public Vertex startSetupTurn(Player player, Set<Vertex> validSpots) {
        return null;
    }
}