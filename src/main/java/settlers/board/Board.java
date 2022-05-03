package settlers.board;

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
     * @return a length-5 array containing all the hex column beginning indices
     */
    int[] getHexColumnBeginningIndices();

    /**
     * @return a length-5 array containing all the vertex column beginning indices
     */
    int[] getVertexColumnBeginningIndices();

    // I don't have any setters here, because they will be set by the constructor
}
