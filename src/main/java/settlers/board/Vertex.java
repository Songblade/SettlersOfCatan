package settlers.board;

import settlers.Player;

public interface Vertex {

    /**
     *
     * @return a length 3 array containing the adjacent vertices, where 0 is up, increasing clockwise
     */
    Vertex[] getAdjacentVertices();

    /**
     *
     * @param vertex being set adjacent to the Vertex
     * @param position from 0 to 3, where the vertex is set, where 0 is the upper left, increasing clockwise
     * @throws IllegalArgumentException if this position's vertex is already set
     */
    void setVertices(Vertex vertex, int position);

    /**
     *
     * @return a length 3 array containing the adjacent edges, where 0 is up, increasing clockwise
     * Each edge at a position connects to the vertex of the same number
     */
    Edge[] getEdges();

    /**
     *
     * @param edge the adjacent edge being added
     * @param position from 0 to 3, where the edge is set, where 0 is the upper left, increasing clockwise
     * @throws IllegalArgumentException if this position's edge is already set
     */
    void setEdge(Edge edge, int position);

    /**
     * The player defaults as null until it is changed
     * @return player that owns this Settlement or City, or null if there is no Settlement or City
     */
    Player getPlayer();

    /**
     * @param player that owns this Settlement or City
     * @throws IllegalArgumentException if a different player already has this city
     */
    void setPlayer(Player player);

    /**
     *
     * @return true if this is a City, false otherwise
     */
    boolean isCity();

    /**
     * Turns this settlement into a city
     * @throws IllegalArgumentException if this is already a City
     * @throws IllegalStateException if this is not a Settlement
     */
    void makeCity();

}