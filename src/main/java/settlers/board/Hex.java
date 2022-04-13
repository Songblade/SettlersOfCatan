package settlers.board;

import settlers.card.Resource;

public interface Hex {

    /**
     * Sets the hex's number to number
     * @param number we wish to set this object's number to
     * @throws IllegalStateException if the hex already has a number
     * @throws IllegalArgumentException if the number is out of bounds
     */
    void setNumber(int number);

    /**
     *
     * @return the Hex's die number, equals to 7 if this is the desert
     */
    int getNumber();

    /**
     * @return the resource players in adjacent vertices get when rolling the Hex's die number
     * WOOD means forest, WHEAT means field, ORE means mountain, BRICK means quarry, SHEEP means pasture
     * MISC means desert, and means that no resource should be given to the player
     */
    Resource getResource();

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