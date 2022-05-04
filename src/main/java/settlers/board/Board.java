package settlers.board;

import java.util.Set;

public interface Board {

    /**
     * @return a length-19 array containing all the Hexes
     */
    Hex[] getHexes();

    /**
     * @return a length-54 array containing all the Vertices
     */
    Vertex[] getVertices();

    /**
     * @return all vertices that don't have a settlement or city and aren't next to one
     */
    Set<Vertex> getOpenVertices();

    /**
     * @return all edges that don't have a road
     */
    Set<Edge> getEmptyEdges();

    /**
     * @param vertex to be removed from the collection of open vertices
     * Also removes all vertices adjacent to it
     */
    void removeSettlement(Vertex vertex);

    /**
     * @param road to be removed from the list of empty edges
     */
    void removeRoad(Edge road);

    /**
     * @return a length-5 array containing all the hex column beginning indices
     */
    int[] getHexColumnBeginningIndices();

    /**
     * @return a length-5 array containing all the vertex column beginning indices
     */
    int[] getVertexColumnBeginningIndices();

    // I don't have any setters here, because they will be set by the constructor
}
