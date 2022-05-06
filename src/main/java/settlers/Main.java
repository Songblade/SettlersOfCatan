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
     * @param player that wants to build
     * @param project that the player wants to build
     * @return true if the player has enough resources, false otherwise
     */
    boolean playerElementsFor(Player player, Building project);

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

enum Building {
    ROAD(1, 1, 0, 0, 0),
    SETTLEMENT(1, 1, 1, 0, 1),
    CITY(0, 0, 2, 3, 0),
    DEVELOPMENT_CARD(0, 0, 1, 1, 1);

    Building(int brickNumber, int woodNumber, int wheatNumber, int oreNumber, int sheepNumber) {
        resources = new HashMap<>();
        resources.put(Resource.BRICK, brickNumber);
        resources.put(Resource.WOOD, woodNumber);
        resources.put(Resource.WHEAT, wheatNumber);
        resources.put(Resource.ORE, oreNumber);
        resources.put(Resource.SHEEP, sheepNumber);
    }

    private final Map<Resource, Integer> resources;

    public Map<Resource, Integer> getResources() {
        return Collections.unmodifiableMap(resources);
    }


}