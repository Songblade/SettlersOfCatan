package settlers.board;

import settlers.Player;

import java.util.List;

public interface Edge {

    /**
     *
     * @return a length 3 array containing the adjacent vertices, where 0 is up, increasing clockwise
     */
    List<Edge> getAdjacentEdges();

    /**
     *
     * @param edge being set adjacent to this Edge
     * @param position from 0 to 3, where the edge is set, where 0 is the upper left, increasing clockwise
     * @throws IllegalStateException if this position's vertex is already set
     */
    void setEdge(Edge edge, int position);

    /**
     *
     * @return the player who owns the road, or null if there is no road
     */
    Player getPlayer();

    /**
     * @param player that owns this Road
     * @throws IllegalArgumentException if a different player already has this road
     */
    void setPlayer(Player player);

}