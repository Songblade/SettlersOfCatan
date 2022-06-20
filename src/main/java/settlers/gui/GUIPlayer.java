package settlers.gui;

import settlers.Player;
import settlers.board.*;
import settlers.card.Resource;

import java.util.Map;
import java.util.Set;

public interface GUIPlayer {

    /**
     * Starts Player's turn
     */
    void startTurn(int roll);

    /**
     * Starts a settlement turn
     */
    void startSettlementTurn(Set<Vertex> availableSpots);

    /**
     * Gets the last spot the player placed a settlement
     * @return
     */
    Vertex getLastSettlementSpot();

    /**
     * informs the GUIPlayer that the main phase of the game has started. This is important so the player can cance
     */
    void startMainPhase();

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
     * Updates the GUIPlayer's development resource counters
     * @param plr the player whose development card count changed
     */
    void updateDevelopmentCounters(Player plr);

    /**
     * Updates the GUIPlayer's knight counters
     * @param plr the player who played the development card
     */
    void updateKnightCounters(Player plr);

    /**
     * Moves the thief image
     * @param hex the new location of the thief image
     */
    void moveThiefImage(Hex hex);

    /**
     * Forces player to discard half of his hand
     * @param target the amount the player must discard until
     */
    void discardUntil(int target);

    /**
     * Sends the player a trading request
     * @param sender the player who sent the trading request
     * @param resourcesExchanged the resources which will be exchanged if the request is accepted
     */
    void receiveTradeRequest(Player sender, Map<Resource,Integer> resourcesExchanged);

    /**
     * Triggered whenever all players decline a trade request sent py this GUIPlayer or a player accepts a
     * trade request sent by this GUIPlayer
     * @param responder the player who accepted the request. Null if no one accepted.
     */
    void tradeRequestResponseReceived(Player responder);
}