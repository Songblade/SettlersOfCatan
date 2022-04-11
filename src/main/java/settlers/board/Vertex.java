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
     */
    void setEdge(Edge edge, int position);

    /**
     * @param player that owns this vertex
     *
     */
    void setPlayer(Player player);

}