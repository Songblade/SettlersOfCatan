package settlers.board;

import settlers.card.Resource;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class BoardImpl implements Board {

    Hex[] hexes;
    Vertex[] vertices;
    Hashtable<Resource,Integer> tileResourceQuantities;
    ArrayList<Integer> priorityTileNumbers;
    ArrayList<Integer> otherTileNumbers;

    public BoardImpl(){
        //Creates empty tables of hexes and vertices
        hexes = new Hex[19];
        vertices = new Vertex[54];

        //Sets up hexagon varubles
        tileResourceQuantities = new Hashtable<>();
        priorityTileNumbers = new ArrayList<>();
        otherTileNumbers = new ArrayList<>();
        setupHexQuantities(tileResourceQuantities);
        setupPriorityNumbers(priorityTileNumbers);
        setupOtherNumbers(otherTileNumbers);
    }

    /**
     * Where the quantities of hexagon resources are set
     * @param table the table containing all resources
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
     * Where priority numbers (numbers which can't be mapped next to each other) are set
     * @param array the table containing all priority numbers
     */
    private void setupPriorityNumbers(ArrayList<Integer> array){
        array.add(6);
        array.add(6);
        array.add(8);
        array.add(8);
    }

    /**
     * Where other numbers are set
     * @param array the table containing all other numbers
     */
    private void setupOtherNumbers(ArrayList<Integer> array){
        array.add(2);
        array.add(3);
        array.add(3);
        array.add(4);
        array.add(4);
        array.add(5);
        array.add(5);
        array.add(9);
        array.add(9);
        array.add(10);
        array.add(10);
        array.add(11);
        array.add(11);
        array.add(12);
    }

    private int getQuantityOfRemailingTiles(){
        int quantityOfAvailableTiles = 0;
        for(Resource resource : tileResourceQuantities.keySet()){
            quantityOfAvailableTiles += tileResourceQuantities.get(resource);
        }

        return quantityOfAvailableTiles;
    }

    /**
     * Gets an available resource from resourceQuantities and makes it unavailable
     * @return a resource in resource quantities which is available to be placed
     */
    private Resource getAvailableResource(){

    }

    /**
     * generates hexes and puts them in "hexes"
     */
    private void generateHexes(){
        for(int i = 0; i < 19; i++){
            hexes[i] = new HexImpl(Resource.BRICK);
        }
    }

    /**
     * @return a length-19 array containing all the Hexes
     */
    public Hex[] getHexes(){
        return new Hex[1];
    }

    /**
     * @return a length-54 array containing all the Vertices
     */
    public Vertex[] getVertices(){
        return new Vertex[1];
    }

    // I don't have any setters here, because they will be set by the constructor
}
