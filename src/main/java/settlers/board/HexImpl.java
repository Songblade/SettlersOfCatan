package settlers.board;

import settlers.card.Resource;

public class HexImpl implements Hex{
    private Resource resource;
    private int number = 7;


    public HexImpl(Resource resource){
        this.resource = resource;
    }

    /**
     *
     * @param number the number we wish to set this object's number to
     * Sets the hex's number to number. Should only be called once
     */
    public void setNumber(int number){
        this.number = number;
    }

    /**
     *
     * @return the Hex's die number, equals to 7 if this is the desert
     */
    public int getNumber(){
        return 0;
    }

    /**
     * @return the resource players in adjacent vertices get when rolling the Hex's die number
     * WOOD means forest, WHEAT means field, ORE means mountain, BRICK means quarry, SHEEP means pasture
     * MISC means desert, and means that no resource should be given to the player
     */
    public Resource getResource(){
        return Resource.BRICK;
    }

    /**
     *
     * @return a length 6 array containing the vertices
     * Vertex 0 is the upper left, increases clockwise
     */
    public Vertex[] getVertices(){
        return new Vertex[1];
    }

    /**
     *
     * @param vertex being set adjacent to the Hex
     * @param position from 0 to 6, where the vertex is set, where 0 is the upper left, increasing clockwise
     */
    public void setVertex(Vertex vertex, int position){
        return;
    }

    /**
     *
     * @return true if there is a robber on this Hex, false otherwise
     */
    public boolean hasThief(){
        return true;
    }

    /**
     *
     * @param thiefIsHere sets whether the thief is here to this parameter
     */
    public void setThief(boolean thiefIsHere){
        return;
    }

}