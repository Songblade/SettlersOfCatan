package settlers.board;

import settlers.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import java.util.Objects;

public class EdgeImpl implements Edge{

    private List<Edge> adjEdges;
    private Player player;

    public EdgeImpl() {
        adjEdges = new ArrayList<>(3);
    }

    /**
     * @return a length 3 array containing the adjacent vertices, where 0 is up, increasing clockwise
     */
    @Override
    public List<Edge> getAdjacentEdges() {
        return Collections.unmodifiableList(adjEdges);
    }

    /**
     * @param edge     being set adjacent to this Edge
     * @param position from 0 to 3, where the edge is set, where 0 is the upper left, increasing clockwise
     * @throws IllegalStateException if this position's vertex is already set
     */
    @Override
    public void setEdge(Edge edge, int position) {
        if (position < 0 || position >= 3) {
            throw new IllegalArgumentException("position " + position + " is out of bounds");
        }
        if (adjEdges.get(position) != null) {
            throw new IllegalStateException("Position " + position + " already has a vertex");
        }
        adjEdges.set(position, edge);
    }

    /**
     * @return the player who owns the road, or null if there is no road
     */
    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * @param player that owns this Road
     * @throws IllegalArgumentException if a different player already has this road
     */
    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    // I am not overriding .equals, because I want to have multiple roads of the same player

    /*@Override
    public int hashCode() {
        return Objects.hash(player);
    }*/
}
