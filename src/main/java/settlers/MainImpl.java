package settlers;

import settlers.board.*;
import settlers.card.DevelopmentCard;
import settlers.card.Resource;
import settlers.gui.GUIMain;
import settlers.gui.GUIMainImpl;

import java.util.*;

public class MainImpl implements Main {

    private Board board;
    private Player[] players;
    private GUIMain gui;
    private boolean isMainPhase; // starts automatically as false
    private Hex thiefIsHere; // so we don't have to look for it

    protected MainImpl(int numberOfPlayers) {
        players = new Player[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            players[i] = new PlayerImpl();
        }
        board = new BoardImpl();
        // now we find the thief, the only time we need to do it this way
        for (Hex hex : board.getHexes()) {
            if (hex.getNumber() == 1) {
                thiefIsHere = hex;
                break;
            }
        }
        // I will create a GUIMain once Aryeh tells me how to do it
        gui = new GUIMainImpl(this);
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
        // I check the game phase, then call the appropriate method
        if (!isMainPhase) {
            return board.getOpenVertices(); // since all vertices not occupied or next to one occupied are
                // fair game here
        }
        return getSettleSpotsGame(player);
    }

    /**
     * Gets the settle spots during the game
     * @param player building a settlement
     * @return the available settlement spots for that player
     */
    private Set<Vertex> getSettleSpotsGame(Player player) {
        // First, I get a list of all open settle spots
        // I then traverse each of them
        // If it has a road of this player, I add it to a second list
        // I return the second list
        Set<Vertex> openSpots = board.getOpenVertices();
        Set<Vertex> settleSpots = new HashSet<>();
        for (Vertex spot : openSpots) {
            for (Edge edge : spot.getEdges()) {
                if (edge != null && player.equals(edge.getPlayer())) {
                    settleSpots.add(spot);
                }
            }
        }
        return settleSpots;
    }

    /**
     * Gets the locations where this player can build a road
     *
     * @param player building the road
     * @return a Set of Edges where this player could build
     */
    @Override
    public Set<Edge> getAvailableRoadSpots(Player player) {
        // we will start with an empty set
        // then, we will go through all the vertices
        // if one belongs to this player, add the other two
        // Then at the end, I will take all the viable roads, and retainAll
        // This is frankly too inefficient, but I am not bothering to find something better
        Set<Edge> roadSpots = new HashSet<>();
        for (Vertex vertex : board.getVertices()) {
            for (Edge edge : vertex.getEdges()) {
                if (edge != null && player.equals(edge.getPlayer())) {
                    roadSpots.addAll(Arrays.asList(vertex.getEdges()));
                }
            }
        }
        roadSpots.retainAll(board.getEmptyEdges());
        roadSpots.remove(null);
        return roadSpots;
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
     * This and the following methods do not throw exceptions to aid with testing
     */
    @Override
    public void buildSettlement(Player player, Vertex location) {
        // I need to change the vertex's status
        location.setPlayer(player);
        // I need to add the vertex to the player's list
        player.addSettlement(location);
        // I need to remove the vertex from the board's open vertex list
        // I need to remove the adjacent vertices from the board's open vertex list
        board.removeSettlement(location);
        // I need to add the port to the player's list, if applicable
        if (location.getPort() != null) {
            player.addPort(location.getPort());
        }
        // I need to remove resources from the player, if this is the right phase
        if (isMainPhase) {
            player.removeResources(Building.SETTLEMENT.getResources());
        }
    }

    /**
     * Builds a road, and updates the Edge accordingly
     *
     * @param player   building the road
     * @param location where the road is being built
     */
    @Override
    public void buildRoad(Player player, Edge location) {

    }

    /**
     * Upgrades a settlement to a city, and updates the Vertex accordingly
     *
     * @param player     who is building the city
     * @param settlement that the player is upgrading
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

    /**
     * This is used only for testing purposes, so I can change the stage from MainTest
     * @param changingToMain whether or not we are changing to main
     */
    protected void setPhase(boolean changingToMain) {
        isMainPhase = changingToMain;
    }

    protected List<Player> getPlayers() {
        return Arrays.asList(players);
    }
}
