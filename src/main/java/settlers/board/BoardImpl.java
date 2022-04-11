package settlers.board;

import settlers.card.Resource;

import java.util.ArrayList;
import java.util.Hashtable;

public class BoardImpl implements Board {

    Hex[] hexes;
    Vertex[] vertices;
    Hashtable<Resource,Integer> tileQuantities;

    public BoardImpl(){
        hexes = new Hex[19];
        vertices = new Vertex[54];

        tileQuantities = new Hashtable<>();
    }

    /**
     * Where the quantities of hexagon resources are set
     * @param table
     */
    private void setupHexQuantities(Hashtable<Resource,Integer> table){
        table.put(Resource.WOOD,3);
        table.put(Resource.BRICK,3);
        table.put(Resource.WHEAT,3);
        table.put(Resource.SHEEP,3);
        table.put(Resource.ORE,3);
        table.put(Resource.MISC,1);
    }

    /**
     * generates hexes and puts them in "hexes"
     */
    private void generateHexes(){
        for(int i = 0; i < 19; i++){
            hexes[i] = new HexImpl();
        }
    }

    /**
     * @return a length-19 array containing all the Hexes
     */
    public Hex[] getHexes();

    /**
     * @return a length-54 array containing all the Vertices
     */
    public Vertex[] getVertices();

    // I don't have any setters here, because they will be set by the constructor
}
