package settlers.board;

import settlers.card.Resource;

import java.util.Arrays;

public class HexImpl implements Hex{
    private final Resource resource;
    private int number;
    private Vertex[] vertices;
    private boolean hasThief; // default starts false


    public HexImpl(Resource resource){
        if (resource == null) {
            throw new IllegalArgumentException("Constructor is null");
        }
        this.resource = resource;
        this.number = -1; // the default value, before it is set
        this.vertices = new Vertex[6];
    }

    /**
     * Sets the hex's number to number
     * @param number we wish to set this object's number to
     * @throws IllegalStateException if the hex already has a number
     * @throws IllegalArgumentException if the number is out of bounds
     */
    public void setNumber(int number){
        if (this.number != -1) {
            throw new IllegalStateException("hex's number is already set to " + this.number);
        }
        if (number < 1 || number > 12 || number == 7) {
            throw new IllegalArgumentException(number + " is out of bounds, must be between 1 and 12 excluding 7");
        }
        if (resource == Resource.MISC && number != 1) {
            throw new IllegalStateException("desert must be set to 1");
        }
        this.number = number;
    }

    /**
     * @return the Hex's die number, equals to 1 if this is the desert
     * @throws IllegalStateException if number hasn't been set yet
     */
    public int getNumber(){
        if (number == -1) {
            throw new IllegalStateException("number has not been set yet");
        }
        return number;
    }

    /**
     * @return the resource players in adjacent vertices get when rolling the Hex's die number
     * WOOD means forest, WHEAT means field, ORE means mountain, BRICK means quarry, SHEEP means pasture
     * MISC means desert, and means that no resource should be given to the player
     */
    public Resource getResource(){
        return this.resource;
    }

    /**
     *
     * @return a length 6 array containing the vertices, new copy
     * Vertex 0 is the upper left, increases clockwise
     */
    public Vertex[] getVertices(){
        return Arrays.copyOf(vertices, vertices.length);
    }

    /**
     *
     * @param vertex being set adjacent to the Hex
     * @param position from 0 to 6, where the vertex is set, where 0 is the upper left, increasing clockwise
     * @throws IllegalArgumentException if position > 5 or < 0
     * @throws IllegalStateException if position already has a vertex
     */
    public void setVertex(Vertex vertex, int position){
        if (position > 5 || position < 0) {
            throw new IllegalArgumentException("position " + position + " is out of bounds");
        }
        if (vertices[position] != null) {
            throw new IllegalStateException("position " + position + " already has a vertex");
        }
        vertices[position] = vertex;
    }

    /**
     *
     * @return true if there is a robber on this Hex, false otherwise
     */
    public boolean hasThief(){
        return hasThief;
    }

    /**
     *
     * @param thiefIsHere sets whether the thief is here to this parameter
     */
    public void setThief(boolean thiefIsHere){
        hasThief = thiefIsHere;
    }

}