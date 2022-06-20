package settlers.gui;

import settlers.Player;
import settlers.board.*;
import settlers.card.DevelopmentCard;
import settlers.card.Resource;

import java.util.Map;
import java.util.Set;

public interface GUIMain {

    /**
     * Informs GUIMain that the current turn was passed, and it can return control of what happens next to Main
     */
    public void pass();

    /**
     * Asks main if player can build a road
     * @param player
     * @return
     */
    public boolean canBuildRoad(Player player);

    /**
     * Asks main if player can build a settlement
     * @param player
     * @return
     */
    public boolean canBuildSettlement(Player player);

    /**
     * Asks main if player can build a city
     * @param player
     * @return
     */
    public boolean canBuildCity(Player player);

    /**
     * Asks main if player can buy a development card
     * @param player
     * @return
     */
    public boolean canBuyDevelopmentCard(Player player);
    /**
     * Asks main if player can play the development card card
     * @param player
     * @param card
     * @return
     */
    public boolean canPlayDevelopmentCard(Player player, DevelopmentCard card);

    /**
     * Tells main to build a road at edge for player
     * @param player
     * @param edge
     * @return
     */
    public void buildRoad(Player player, Edge edge);

    /**
     * Tells main to build a settlement at vertex for player
     * @param player
     * @param vertex
     * @return
     */
    public void buildSettlement(Player player, Vertex vertex);

    /**
     * Tells main to build a city at vertex for player
     * @param player
     * @param vertex
     * @return
     */
    public void buildCity(Player player, Vertex vertex);

    /**
     * Tells main to purchase a development card for the player
     * @param player
     */
    public void buildDevelopmentCard(Player player);

    /**
     * Plays the player's Knight development card, lets them move the robber and steals a resource
     * @param stealer playing the knight card
     * @param settlement being stolen from
     * @param location hexagon being blocked, adjacent to the settlement
     * @return true if the card was successfully played, false if the player didn't have the card
     */
    boolean playKnight(Player stealer, Vertex settlement, Hex location);

    /**
     * Plays the player's Year of Plenty development card, gives them 2 resources of their choice
     * @param player playing the card
     * @param firstResource the player receives
     * @param secondResource the player receives
     * @return true if the card was successfully played, false if the player didn't have the card
     */
    boolean playYearOfPlenty(Player player, Resource firstResource, Resource secondResource);

    /**
     * Plays the player's Monopoly development card, stealing every copy of that resource from all other players
     * @param player playing the card
     * @param resource the player steals from all other players
     * @return true if the card was successfully played, false if the player didn't have the card
     */
    boolean playMonopoly(Player player, Resource resource);

    /**
     * Plays the player's Road Building development card, letting them place 2 roads
     * @param player playing the card
     * @param firstLocation an empty edge where this player can build
     * @param secondLocation an empty edge where this player can build after building firstLocation
     * @return true if the card was successfully played, false if the player didn't have the card
     */
    boolean playRoadBuilding(Player player, Edge firstLocation, Edge secondLocation);

    /**
     * Calls respective method in Main
     * @param player building the settlement
     * @return a Set of Vertices where this player could build
     */
    Set<Vertex> getAvailableSettlementSpots(Player player);

    /**
     * Calls respective method in Main
     * @param player building the road
     * @return a Set of Edges where this player could build
     */
    Set<Edge> getAvailableRoadSpots(Player player);

    /**
     * Gets the locations where this player can build a road, given that the player is going to build
     * on this edge first. Used to find the second edge for playRoadBuilding
     * @param player building the road
     * @param roadToBuild where the player will build a road, but hasn't yet
     * @return a Set of Edges where this player could build once they build roadToBuild
     */
    Set<Edge> getAvailableRoadSpotsGivenEdge(Player player, Edge roadToBuild);

    /**
     * Calls respective method in Main
     * @param player building the city
     * @return a Set of Vertices where this player could build
     */
    Set<Vertex> getAvailableCitySpots(Player player);

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

    /**
     * Moves the thief
     * @param player the player who moved the thief
     * @param location the location you are stealing from
     * @param position where the thief is being moved to
     */
    public void moveThief(Player player, Vertex location, Hex position);

    /**
     *Calls main.getAvailableThiefSpots
     */
    public Set<Hex> getAvailableThiefSpots();

    /**
     * Signals that the player had discarded resources down to the target amount
     * @param player the player
     */
    public void playerHasTargetResources(Player player);

    /**
     * Checks if this trade with the bank would work
     * @param player considering the trade
     * @param resourceGiven resource type that would be given
     * @return true if the player can make this trade, false if the player lacks the port or resources
     */
    boolean canTrade(Player player, Resource resourceGiven);


    /**
     * This simulates a trade with the bank, updating the Player appropriately
     * @param player doing the trade
     * @param resourceGiven resource type being given
     * @param resourceGotten resource type being received
     */
    void trade(Player player, Resource resourceGiven, Resource resourceGotten);


    /**
     * Asks main if this trade is valid, sends trading requests if it is, then waits for one of them to be accepted.
     * Once a trading request is accepted, the trade will be made between the player who initiated the trade and the player
     * who accepted the request.
     * If all requests were declined, this method stops waiting for a request to be accepted and continues the player's turn
     * @param player the player who sent the request
     * @param resourcesExchanged a map of resources that would be exchanged during this trade
     */
    void trade(Player player, Map<Resource, Integer> resourcesExchanged, Set<Player> sendTo);

    void playerDeclinedTrade(Player player);

    void playerAcceptedTrade(Player player);
}