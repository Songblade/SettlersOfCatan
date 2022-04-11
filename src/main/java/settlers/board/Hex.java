package settlers.board;

public interface Hex {

    /**
     *
     * @return a length 6 array containing the vertices
     * Vertex 0 is the upper left, increases clockwise
     */
    Vertex[] getVertices();

    /**
     *
     * @param vertex being set adjacent to the Hex
     * @param position from 0 to 6, where the vertex is set, where 0 is the upper left, increasing clockwise
     */
    void setVertex(Vertex vertex, int position);

    /**
     *
     * @return true if there is a robber on this Hex, false otherwise
     */
    boolean hasThief();

    /**
     *
     * @param thiefIsHere sets whether the thief is here to this parameter
     */
    void setThief(boolean thiefIsHere);

}