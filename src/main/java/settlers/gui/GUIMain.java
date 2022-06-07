package settlers.gui;

import settlers.Player;
import settlers.board.*;
import settlers.card.DevelopmentCard;
import settlers.card.Resource;

import java.util.Set;

public interface GUIMain {

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
}