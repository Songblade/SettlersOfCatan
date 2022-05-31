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
     * Returns whether or not the player has enough resources to build the project
     * And also whether or not the player has reached the maximum number of that project
     * The maximum numbers are 15 roads, 5 settlements, and 4 cities
     * @param player  that wants to build
     * @param project that the player wants to build
     * @return true if the player has enough resources, false otherwise
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
     * Makes the player use a development card
     * @param player playing the card
     * @param development card being played
     * @throws IllegalArgumentException if the development card is of type VICTORY_POINT
     * @throws IllegalStateException if the player does not have that development card
     * I may decide to make the effects of the card decided by the enum directly
     */
    void playDevelopmentCard(Player player, DevelopmentCard development);

    /**
     * Checks if this trade with the bank would work
     * @param player considering the trade
     * @param resourceGiven resource type that would be given
     * @param resourceNumber number of resources that would be given
     * @return true if the player can make this trade, false if the player lacks the port or resources
     */
    boolean canTrade(Player player, Resource resourceGiven, int resourceNumber);

    /**
     * This simulates a trade with the bank, updating the Player appropriately
     * @param player doing the trade
     * @param resourceGiven resource type being given
     * @param resourceNumber number of that resource being given
     * @param resourceGotten resource type being received
     */
    void trade(Player player, Resource resourceGiven, int resourceNumber, Resource resourceGotten);

    List<Player> getPlayers();

    // If we decide to implement Player to Player trading,
    // I will add versions of canTrade() and trade() for that

}