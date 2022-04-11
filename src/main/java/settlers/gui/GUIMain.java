package settlers.gui;

import settlers.Player;
import settlers.Resource;
import settlers.board.*;

import java.util.HashMap;

public interface GUIMain {
    /**
     * Triggered by GUIPlayer whenever a player passes
     * @param player the player who passed
     */
    public void playerPassed(Player player);

    /**
     * Triggered by GUIPlayer whenever a player requests to place a road
     * @param player the player who wants to build a road
     */
    public void playerRequestedRoadBuild(Player player);

    /**
     * Triggered by GUIPlayer whenever a player requests to place a settlement
     * @param player the player who wants to build a settlement
     */
    public void playerRequestedSettlementBuild(Player player);

    /**
     * Triggered by GUIPlayer whenever a player requests to place a city
     * @param player the player who wants to build a city
     */
    public void playerRequestedCityBuild(Player player);

    /**
     * Triggered by GUIPlayer whenever a player builds a road
     * @param player the player who wants to build a road
     * @param edge the edge where the player wants the road built
     */
    public void playerBuildRoad(Player player, Edge edge);

    /**
     * Triggered by GUIPlayer whenever a player builds a settlement
     * @param player the player who wants to build a settlement
     * @param vertex the vertex where the player wants a settlement built
     */
    public void playerBuildSettlement(Player player, Vertex vertex);

    /**
     * Triggered by GUIPlayer whenever a player upgrades a settlement to a city
     * @param player the player who wants to upgrade a settlement to a city
     * @param vertex the vertex where the player wants a settlement upgraded to a city
     */
    public void playerBuildCity(Player player, Vertex vertex);

    /**
     * Triggered by GUIPlayer whenever a player requests to move the thief
     * @param hex the hexagon the player wants to move the thief to
     */
    public void placeThief(Hex hex);

    /**
     * Triggered by GUIPlayer whenever a player
     * @param to the player who is stealing
     * @param from the player who @to is stealing from
     */
    public void playerStoleFromPlayer(Player to, Player from);

    /**
     * Triggered by GUIPlayer whenever he is forced to discard resources due to the thief
     * @param player the player who is discarding resources
     * @param resources the resources which the player wishes to discard
     */
    public void playerDiscardedResources(Player player, HashMap<Resource,Integer> resources);
}