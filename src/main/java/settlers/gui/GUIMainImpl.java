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
        for(GUIPlayer gui : playerGUIs){
            gui.startSettlementTurn();
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
     * Tells main and all GUIPlayers that a @player built a road at @edge
     * @param player the builder of the road
     * @param edge the location of the road
     */
    private void playerBuiltRoad(Player player, Edge edge){
        main.buildRoad(player,edge);
        for(GUIPlayer gui : playerGUIs){
            gui.setRoad(player,edge);
        }
    }

    /**
     * Tells main and all GUIPlayers that a @player built a settlement at @vertex
     * @param player the builder of the settlement
     * @param vertex the location of the settlement
     */
    private void playerBuiltSettlement(Player player, Vertex vertex){
        main.buildSettlement(player,vertex);
        for(GUIPlayer gui : playerGUIs){
            gui.setSettlement(player,vertex);
        }
    }

    /**
     * Tells main and all GUIPlayers that a @player built a city at @vertex
     * @param player the builder of the city
     * @param vertex the location of the city
     */
    private void playerBuiltCity(Player player, Vertex vertex){
        main.buildCity(player,vertex);
        for(GUIPlayer gui : playerGUIs){
            gui.setCity(player,vertex);
        }
    }

    /**
     * Tells Main to preform the specified action for the player
     * @param action
     */
    @Override
    public void preformAction(Action action) {
        switch (action.type){
            case ROAD:
                playerBuiltRoad(action.player, action.road);
                break;
            case SETTLEMENT:
                playerBuiltSettlement(action.player,action.vertex);
                break;
            case CITY:
                playerBuiltCity(action.player,action.vertex);
                break;
            case THIEF:
                main.moveThief(action.player,action.vertex,action.hex);
                break;
        }
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