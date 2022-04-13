package settlers.board;

import settlers.Player;
import settlers.card.Resource;

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
     * @throws IllegalStateException if this position's vertex is already set
     */
    void setVertex(Vertex vertex, int position);

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
     * @throws IllegalStateException if this position's edge is already set
     */
    void setEdge(Edge edge, int position);

    /**
     * @return the resource of the 2:1 port, MISC for a 3:1 port, or null if there is no port
     */
    Resource getPort();

    /**
     * @param resource of the 2:1 port being added, or MISC for a 3:1 port
     * @throws IllegalArgumentException if resource is null
     * @throws IllegalStateException if there is already a port
     */
    void setPort(Resource resource);

    /**
     * The player defaults as null until it is changed
     * @return player that owns this Settlement or City, or null if there is no Settlement or City
     */
    Player getPlayer();

    /**
     * @param player that owns this Settlement
     * @throws IllegalStateException if a different player already has this city
     */
    void setPlayer(Player player);

    /**
     *
     * @return true if this is a City, false otherwise
     */
    boolean isCity();

    /**
     * Turns this settlement into a city
     * @throws IllegalStateException if this is not a Settlement (either empty or city)
     */
    void makeCity();

}