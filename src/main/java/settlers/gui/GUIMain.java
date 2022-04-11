package settlers.gui;

import settlers.Player;
import settlers.board.Edge;
import settlers.board.Vertex;

public interface GUIMain {
    /**
     * Triggered by GUIPlayer whenever a player passes
     * @param player the player who passed
     */
    public void playerPassed(Player player);

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

    
}