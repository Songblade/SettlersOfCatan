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

    // I don't have any setters here, because they will be set by the constructor
}
