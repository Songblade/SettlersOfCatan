package settlers.gui;

import settlers.Player;
import settlers.board.*;

public interface GUIPlayer {

    /**
     * Triggered whenever the player wishes to pass
     */
    public void pass();

    /**
     * Requests a check to see if the player can place a road
     */
    public void requestRoadPlace();

    /**
     * Requests a check to see if the player can place a settlement
     */
    public void requestSettlementPlace();

    /**
     * Requests a check to see if the player can place a city
     */
    public void requestCityPlace();

    /**
     * Triggered whenever the player requests to place a road
     * @param edge the edge where the player wants to place a road
     */
    public void placeRoad(Edge edge);

    /**
     * Triggered whenever the player requests to place a settlement
     * @param vertex the vertex where the player wants to place a settlement
     */
    public void placeSettlement(Vertex vertex);

    /**
     * Triggered whenever the player requests to place a city
     * @param vertex the vertex where the player wants to place a city
     */
    public void placeCity(Vertex vertex);

    /**
     * Triggered whenever the player requests to move the thief
     * @param hex the hexagon which the player wants to move the thief to
     */
    public void placeThief(Hex hex);
}