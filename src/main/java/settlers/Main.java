package settlers;

import settlers.board.*;
import settlers.card.*;

import java.util.*;

public interface Main {

    /**
     * @return this game's board
     */
    Board getBoard();

    /**
     * Made so that GUIPlayer doesn't have to interact with Player directly
     * @param player a resource is removed from
     * @param resource being removed
     * @return true if the removal was successful, false otherwise
     */
    boolean removePlayerResource(Player player, Resource resource);

    /**
     * Returns whether or not the player has enough resources to build the project
     * And also whether or not the player has reached the maximum number of that project
     * The maximum numbers are 15 roads, 5 settlements, and 4 cities
     * @param player  that wants to build
     * @param project that the player wants to build
     * @return true if the player has enough resources and it is that player's turn, false otherwise
     */
    boolean playerCanBuild(Player player, Building project);

    /**
     * @return spots where the thief can be moved to
     */
    Set<Hex> getAvailableThiefSpots();

    /**
     * Gets the locations where this player can build a settlement
     * @param player building the settlement
     * @return a Set of Vertices where this player could build
     */
    Set<Vertex> getAvailableSettlementSpots(Player player);

    /**
     * Gets the locations where this player can build a road
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
     * Gets the settlements that this player could upgrade into cities
     * @param player building the city
     * @return a Set of Vertices where this player could build
     */
    Set<Vertex> getAvailableCitySpots(Player player);

    /**
     * Moves the thief and steals a resource
     *
     * @param stealer    player who is stealing
     * @param settlement that is being robbed, can be an empty vertex if no one is being stolen from
     * @param location   that is being robbed, resources can't be gotten there until the thief is moved
     */
    void moveThief(Player stealer, Vertex settlement, Hex location);

    /**
     * Builds a settlement, and updates the Player and Vertex accordingly
     * @param player who is building the settlement
     * @param location where the player builds the settlement
     * This and the following methods do not throw exceptions to aid with testing
     */
    void buildSettlement(Player player, Vertex location);

    /**
     * Builds a road, and updates the Edge accordingly
     * @param player building the road
     * @param location where the road is being built
     */
    void buildRoad(Player player, Edge location);

    /**
     * Upgrades a settlement to a city, and updates the Vertex accordingly
     * @param player who is building the city
     * @param settlement that the player is upgrading
     */
    void buildCity(Player player, Vertex settlement);

    /**
     * Gives the player a development card from the deck, and updates the Player and deck accordingly
     * @param player buying the development card
     */
    void buildDevelopmentCard(Player player);

    /**
     * Does this player have this development
     * @param player who wants to play the card
     * @param card the player wants to play
     * @return false if it is VICTORY_POINT or the player doesn't have it, true otherwise
     */
    boolean canPlay(Player player, DevelopmentCard card);

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
     *                       Can be null if the player only has space to build one road
     * @return true if the card was successfully played, false if the player didn't have the card
     */
    boolean playRoadBuilding(Player player, Edge firstLocation, Edge secondLocation);

    /**
     * Checks if this trade with the bank would work
     * @param player considering the trade
     * @param resourceGiven resource type that would be given
     * @return true if the player can make this trade, false if the player lacks the port or resources
     */
    boolean canTrade(Player player, Resource resourceGiven);

    /**
     * Checks if this player can trade these resources to another player
     * Does not check if it is this player's turn, because this is also used to check if this player
     * can be traded with.
     * @param player trading or being traded with
     * @param resourcesGiven that this player would have to give as part of the trade
     * @param isRequestingPlayer if this player is requesting the trade or accepting it
     *                           If the player is requesting the trade, resourcesGiven values should
     *                              be negative.
     *                           Otherwise, they should be positive.
     * @return true if the player can make this trade, false if the player lacks the resources
     * Also returns false if the resources are empty, because you cannot donate resources
     */
    boolean canTrade(Player player, Map<Resource, Integer> resourcesGiven, boolean isRequestingPlayer);

    /**
     * This simulates a trade with the bank, updating the Player appropriately
     * @param player doing the trade
     * @param resourceGiven resource type being given
     * @param resourceGotten resource type being received
     */
    void trade(Player player, Resource resourceGiven, Resource resourceGotten);

    /**
     * This simulates a trade between 2 players, updating each accordingly
     * @param player1 who is initiating the trade, whose turn it is
     * @param resourcesExchanged where negative values are given by player1 and received by player2
     *                           While positive values are given by player2 and received by player1
     * @param player2 who is on the other end of the trade
     */
    void trade(Player player1, Map<Resource, Integer> resourcesExchanged, Player player2);

    List<Player> getPlayers();

    /**
     * @return the player with the longest road, or null if no player has it
     */
    Player getLongestRoadPlayer();

    /**
     * @return the player with the largest army, or null if no player has it
     */
    Player getLongestArmyPlayer();

}