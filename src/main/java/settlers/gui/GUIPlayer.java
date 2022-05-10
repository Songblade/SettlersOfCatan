package settlers.gui;

import settlers.Player;
import settlers.board.*;

import java.util.Set;

public interface GUIPlayer {

    /**
     * Starts Player's turn
     */
    void startTurn(int roll);

    /**
     * Starts a settlement turn
     */
    Vertex startSettlementTurn(Set<Vertex> availableSpots);

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

    /**
     * Updates the die counter. Disables the die counter outline
     * @param roll the number which die counter should display
     */
    void updateDieCounter(int roll);

    /**
     * Updates the GUIPlayer's resource counters
     */
    void updateResourceCounters();

    /**
     * Moves the thief image
     * @param hex the new location of the thief image
     */
    void moveThiefImage(Hex hex);

    /**
     * Forces player to discard half of his hand
     */
    void discardHalfOfHand();
}