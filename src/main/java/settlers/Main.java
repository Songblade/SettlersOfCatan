package settlers;

import settlers.board.*;
import settlers.card.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
     * @throws IllegalArgumentException if the player cannot build a settlement, or not here
     */
    void buildSettlement(Player player, Vertex location);

    /**
     * Builds a road, and updates the Edge accordingly
     * @param player building the road
     * @param location where the road is being built
     * @throws IllegalArgumentException if the player cannot build a road, or not here
     */
    void buildRoad(Player player, Edge location);

    /**
     * Upgrades a settlement to a city, and updates the Vertex accordingly
     * @param player who is building the city
     * @param settlement that the player is upgrading
     * @throws IllegalArgumentException if the player cannot build a city, or not here
     */
    void buildCity(Player player, Vertex settlement);

    /**
     * Gives the player a development card from the deck, and updates the Player and deck accordingly
     * @param player buying the development card
     */
    void buildDevelopmentCard(Player player);

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


}