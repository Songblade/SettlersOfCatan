package settlers;

import settlers.board.Board;
import settlers.board.BoardImpl;
import settlers.board.Edge;
import settlers.board.Vertex;
import settlers.card.DevelopmentCard;
import settlers.card.Resource;
import settlers.gui.GUIMain;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainImpl implements Main {

    private Board board;
    private Player[] players;
    private GUIMain gui;

    protected MainImpl(int numberOfPlayers) {
        players = new Player[numberOfPlayers];
        for (Player player : players) {
            player = new PlayerImpl();
        }
        board = new BoardImpl();
        // I will create a GUIMain once Aryeh tells me how to do it
    }

    /**
     * @return this game's board
     */
    @Override
    public Board getBoard() {
        return board; // already unmodifiable, yay
    }

    /**
     * Returns whether or not the player has enough resources to build the project
     *
     * @param player  that wants to build
     * @param project that the player wants to build
     * @return true if the player has enough resources, false otherwise
     */
    @Override
    public boolean playerElementsFor(Player player, Building project) {
        Map<Resource, Integer> requirements = project.getResources();
        for (Resource resource : requirements.keySet()) {
            if (player.getResources().getOrDefault(resource, 0) < requirements.get(resource)) {
                // if the player doesn't have enough of that resource
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the locations where this player can build a settlement
     *
     * @param player building the settlement
     * @return a Set of Vertices where this player could build
     */
    @Override
    public Set<Vertex> getAvailableSettlementSpots(Player player) {
        return null;
    }

    /**
     * Gets the locations where this player can build a road
     *
     * @param player building the road
     * @return a Set of Edges where this player could build
     */
    @Override
    public Set<Edge> getAvailableRoadSpots(Player player) {
        return null;
    }

    /**
     * Gets the settlements that this player could upgrade into cities
     *
     * @param player building the city
     * @return a Set of Vertices where this player could build
     */
    @Override
    public Set<Vertex> getAvailableCitySpots(Player player) {
        return null;
    }

    /**
     * Builds a settlement, and updates the Player and Vertex accordingly
     *
     * @param player   who is building the settlement
     * @param location where the player builds the settlement
     * @throws IllegalArgumentException if the player cannot build a settlement, or not here
     */
    @Override
    public void buildSettlement(Player player, Vertex location) {

    }

    /**
     * Builds a road, and updates the Edge accordingly
     *
     * @param player   building the road
     * @param location where the road is being built
     * @throws IllegalArgumentException if the player cannot build a road, or not here
     */
    @Override
    public void buildRoad(Player player, Edge location) {

    }

    /**
     * Upgrades a settlement to a city, and updates the Vertex accordingly
     *
     * @param player     who is building the city
     * @param settlement that the player is upgrading
     * @throws IllegalArgumentException if the player cannot build a city, or not here
     */
    @Override
    public void buildCity(Player player, Vertex settlement) {

    }

    /**
     * Gives the player a development card from the deck, and updates the Player and deck accordingly
     *
     * @param player buying the development card
     */
    @Override
    public void buildDevelopmentCard(Player player) {

    }

    /**
     * Makes the player use a development card
     *
     * @param player      playing the card
     * @param development card being played
     * @throws IllegalArgumentException if the development card is of type VICTORY_POINT
     * @throws IllegalStateException    if the player does not have that development card
     *                                  I may decide to make the effects of the card decided by the enum directly
     */
    @Override
    public void playDevelopmentCard(Player player, DevelopmentCard development) {

    }

    /**
     * Checks if this trade with the bank would work
     *
     * @param player         considering the trade
     * @param resourceGiven  resource type that would be given
     * @param resourceNumber number of resources that would be given
     * @return true if the player can make this trade, false if the player lacks the port or resources
     */
    @Override
    public boolean canTrade(Player player, Resource resourceGiven, int resourceNumber) {
        return false;
    }

    /**
     * This simulates a trade with the bank, updating the Player appropriately
     *
     * @param player         doing the trade
     * @param resourceGiven  resource type being given
     * @param resourceNumber number of that resource being given
     * @param resourceGotten resource type being received
     */
    @Override
    public void trade(Player player, Resource resourceGiven, int resourceNumber, Resource resourceGotten) {

    }
}
