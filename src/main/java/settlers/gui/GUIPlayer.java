package settlers.gui;

import settlers.Player;
import settlers.board.*;

public interface GUIPlayer {

    /**
     * Starts Player's turn
     */
    void startTurn();

    /**
     * Starts a settlement turn
     */
    void startSettlementTurn();

    /**
     * Tells GUIPlayers to make a city
     * @param vertex location of the city
     * @param player controller of the city
     */
    void setCity(Player player, Vertex vertex);

    /**
     * Tells GUIPlayers to make a settlement
     * @param vertex location of the settlement
     * @param player controller of the settlement
     */
    void setSettlement(Player player, Vertex vertex);

    /**
     * Tells GUIPlayers to make a road
     * @param edge location of the road
     * @param player controller of the road
     */
    void setRoad(Player player,Edge edge);
}