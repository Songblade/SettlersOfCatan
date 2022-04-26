package settlers.board;

import settlers.card.Resource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;

public class BoardImpl implements Board {

    private Hex[] hexes;
    private Vertex[] vertices;
    private Hashtable<Resource,Integer> tileResourceQuantities;
    private ArrayList<Integer> priorityTileNumbers;
    private ArrayList<Integer> otherTileNumbers;

    //Constants
    private final int[] hexColumnBeginningIndices = {0,3,7,12,16};

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

    /**
     * Gets the number of tiles which haven't been placed
     * @return the number of tiles which haven't been placed
     */
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
        Random rng = new Random();
        int resourceIndicator = rng.nextInt(getQuantityOfRemailingTiles());

        int index = 0;
        for(Resource resource : tileResourceQuantities.keySet()){
            index += tileResourceQuantities.get(resource);
            if(index > resourceIndicator){
                return resource;
            }
        }

        throw new IllegalStateException("Could not get resource with given RNG bounds");
    }

    /**
     * Makes a copy of hexes as an ArrayList
     * @param toExclude a set of items to exclude from the copy
     * @return a copy of hexes as an ArrayList
     */
    private ArrayList<Hex> makeArrayListCopyOfHexes(HashSet<Hex> toExclude){
        ArrayList<Hex> hexList = new ArrayList<>();

        for(Hex hex : hexes){
            if(!toExclude.contains(hex)) {
                hexList.add(hex);
            }
        }

        return hexList;
    }

    /**
     * Gets the index of hex in hexes
     * @param hex the hex we want the index of
     * @return the index of hex in hexes
     */
    private int getHexIndex(Hex hex){
        for(int i = 0; i < hexes.length; i++){
            if(hex.equals(hexes[i])){
                return i;
            }
        }

        throw new IllegalArgumentException("Requested Hex doesn't exist on board");
    }

    /**
     * Tests to see where @index's position in its column is
     * @param index
     * @return 1 if index is on top of a column, 0 if it is in the middle, and -1 if it is at the bottom
     */
    private int getPlacementOnColumn(int index){
        for(int number: hexColumnBeginningIndices){
            if(index == number)return 1;
            if(index + 1 == number || index >= hexes.length)return -1;
        }
        return 0;
    }

    /**
     * Gets all hexes adjacent to hex
     * @param hex the hex which we want all hexes adjacent to
     * @return all hexes adjacent to hex
     */
    private HashSet<Hex> getAdjacentHexes(Hex hex){
        HashSet<Hex> adjacentHexes = new HashSet<>();
        int hexIndex = getHexIndex(hex);

        //Adds the hex above hex if this isn't a top hex, and the hex below hex if this isn't a bottom hex
        int hexColumnPlacement = getPlacementOnColumn(hexIndex);

        if(hexColumnPlacement != 1){
            adjacentHexes.add(hexes[hexIndex - 1]);
        }

        if(hexColumnPlacement != -1) {
            adjacentHexes.add(hexes[hexIndex + 1]);
        }

        return adjacentHexes;
    }

    /**
     * places the priority numbers
     * @return a set of indicies in hexes of tiles which numbers were placed on
     */
    private HashSet<Hex> placePriorityNumbers(){
        HashSet<Hex> placedHexes = new HashSet<>();
        ArrayList<Hex> validHexes = makeArrayListCopyOfHexes(new HashSet<Hex>());
        Random rng = new Random();

        for(Integer number : priorityTileNumbers){
            int validHexesNumberPlacementIndex = rng.nextInt(validHexes.size());
            Hex hex = validHexes.get(validHexesNumberPlacementIndex);
            placedHexes.add(hex);
            validHexes.remove(hex);
            validHexes.removeAll(getAdjacentHexes(hex));
        }

        return placedHexes;
    }

    /**
     * generates hexes and puts them in "hexes"
     */
    private void generateHexes(){
        //Generates 19 hexagons with random resources
        for(int i = 0; i < 19; i++){
            hexes[i] = new HexImpl(getAvailableResource());
        }

        //Sets priority numbers

        //Sets other numbers
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
