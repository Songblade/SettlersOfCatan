package settlers.board;

import settlers.card.Resource;

import java.util.*;

public class BoardImpl implements Board {

    private Hex[] hexes;
    private Vertex[] vertices;
    private Set<Vertex> openVertices;
    private Set<Edge> emptyEdges;

    private Hashtable<Resource,Integer> tileResourceQuantities;
    private ArrayList<Integer> priorityTileNumbers;
    private ArrayList<Integer> otherTileNumbers;
    private ArrayList<Resource> portResources;

    //Constants
    private final int[] columnLengths = {3,4,5,4,3};
    private final int[] hexColumnBeginningIndices = calculateNewRowPositions(columnLengths);
    private final int[] vertexColumnLengths = {3,4,4,5,5,6,6,5,5,4,4,3};
    private final int[] vertexColumnBeginningIndices = calculateNewRowPositions(vertexColumnLengths);
    private final int[][] portLocations = {{0,0,1},{1,1,2},{3,0,5},{6,1,2},{11,2,3},{12,0,5},{15,3,4},{16,4,5},{17,3,4}};

    /**
     * Constructor
     */
    public BoardImpl(){
        //Creates empty tables of hexes and vertices
        hexes = new Hex[19];
        vertices = new Vertex[54];

        //Sets up hexagon variables
        tileResourceQuantities = new Hashtable<>();
        priorityTileNumbers = new ArrayList<Integer>();
        otherTileNumbers = new ArrayList<Integer>();
        portResources = new ArrayList<Resource>();
        setupHexQuantities(tileResourceQuantities);
        setupPriorityNumbers(priorityTileNumbers);
        setupOtherNumbers(otherTileNumbers);
        setupPortResources(portResources);

        //Generates hexes
        generateHexes();

        //Generates vertices
        generateVertices();

        openVertices = new HashSet<>(Arrays.asList(vertices));
        emptyEdges = getAllEdges();
    }

    /**
     * Calculates the beginning positions of each row based on the length of each row
     * @param lengths an array of the lengths of each row
     * @return an array of the beginning positions of each row
     */
    private int[] calculateNewRowPositions(int[] lengths){
        int[] newRowPositions = new int[lengths.length];
        int counter = 0;

        for(int i = 0; i < lengths.length; i++){
            newRowPositions[i] = counter;
            counter += lengths[i];
        }

        return newRowPositions;
    }

    /**
     * Where the quantities of hexagon resources are set
     * @param table the table containing all resources
     */
    private void setupHexQuantities(Hashtable<Resource,Integer> table){
        table.put(Resource.WOOD,4);
        table.put(Resource.BRICK,3);
        table.put(Resource.WHEAT,4);
        table.put(Resource.SHEEP,4);
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
     * Where port resources are set
     * @param array the table containing all port resources
     */
    private void setupPortResources(ArrayList<Resource> array){
        array.add(Resource.WOOD);
        array.add(Resource.BRICK);
        array.add(Resource.SHEEP);
        array.add(Resource.WHEAT);
        array.add(Resource.ORE);
        for(int i = 0; i < 4; i++)array.add(Resource.MISC);
    }

    /**
     * Gets the number of tiles which haven't been placed
     * @return the number of tiles which haven't been placed
     */
    private int getQuantityOfRemainingTiles(){
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
        int resourceIndicator = rng.nextInt(getQuantityOfRemainingTiles());

        int index = 0;
        for(Resource resource : tileResourceQuantities.keySet()){
            index += tileResourceQuantities.get(resource);
            if(index > resourceIndicator){
                tileResourceQuantities.put(resource,tileResourceQuantities.get(resource) - 1);
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
    private ArrayList<Hex> generateArrayListCopyOfHexes(HashSet<Hex> toExclude){
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
     * Returns the column of an item based on its index
     * @param index the index of the item
     * @return the column of the item
     */
    private int getColumn(int index, int[] list){
        for(int i = 1; i < list.length; i++){
            if(index < list[i])return i - 1;
        }
        return list.length - 1;
    }

    /**
     * Returns the row of an item based on its index
     * @param index the index of the item
     * @return the row of the item
     */
    private int getRow(int index, int[] list){
        return index - list[getColumn(index, list)];
    }

    /**
     * Returns the hex at position @row, @column. Protects against searching out of bounds
     * @param row
     * @param column
     * @return
     */
    private Hex getHexByRowAndColumn(int row, int column){
        if(column < 0 || column >= hexColumnBeginningIndices.length) return null;
        if(row < 0 || row >= columnLengths[column]) return null;
        return hexes[hexColumnBeginningIndices[column] + row];
    }

    /**
     * Adds @hex to @hexSet with null protection
     * @param hex
     * @param hexSet
     */
    private void addHexToSet(Hex hex, HashSet<Hex> hexSet){
        if(hex == null)return;
        hexSet.add(hex);
    }
    /**
     * Gets all hexes adjacent to hex
     * @param hex the hex which we want all hexes adjacent to
     * @return all hexes adjacent to hex
     */
    private HashSet<Hex> getAdjacentHexes(Hex hex){
        HashSet<Hex> adjacentHexes = new HashSet<>();
        int hexIndex = getHexIndex(hex);
        int row = getRow(hexIndex, hexColumnBeginningIndices);
        int column = getColumn(hexIndex, hexColumnBeginningIndices);

        //Adds the hexes above, below, and to the left and right of @hex to adjacentHexes
        addHexToSet(getHexByRowAndColumn(row, column + 1),adjacentHexes);
        addHexToSet(getHexByRowAndColumn(row, column - 1),adjacentHexes);
        addHexToSet(getHexByRowAndColumn(row - 1, column),adjacentHexes);
        addHexToSet(getHexByRowAndColumn(row + 1, column),adjacentHexes);

        //Adds the hexes diagonal to @hex
        if(column != 0 && columnLengths[column - 1] < columnLengths[column]){
            addHexToSet(getHexByRowAndColumn(row - 1, column - 1),adjacentHexes);
        }else{
            addHexToSet(getHexByRowAndColumn(row + 1, column - 1),adjacentHexes);
        }

        if(column != columnLengths.length - 1 && columnLengths[column + 1] < columnLengths[column]){
            addHexToSet(getHexByRowAndColumn(row - 1, column + 1),adjacentHexes);
        }else{
            addHexToSet(getHexByRowAndColumn(row + 1, column + 1),adjacentHexes);
        }

        return adjacentHexes;
    }

    /**
     * @return A set of all tiles with numbers placed on them
     */
    private HashSet<Hex> getTilesWithNumbers(){
        HashSet<Hex> tilesWithNumbers = new HashSet<>();

        for(Hex hex : hexes){
            if(hex.getNumber() != - 1){
                tilesWithNumbers.add(hex);
            }
        }

        return tilesWithNumbers;
    }

    /**
     * places the priority numbers
     * @return a set of indicies in hexes of tiles which numbers were placed on
     */
    private void placeNumbers(ArrayList<Integer> numbers, boolean removeAdjacentHexes){
        ArrayList<Hex> validHexes = generateArrayListCopyOfHexes(getTilesWithNumbers());
        Random rng = new Random();

        //For every tile number in numbers, set the number of a random valid tile to that number,
        //then remove the tile (and all adjacent tiles if removeAdjacentTiles is true) from the list of valid numbers.
        int startingSize = validHexes.size();
        for(Integer number : numbers){
                int validHexesNumberPlacementIndex = rng.nextInt(validHexes.size());
                Hex hex = validHexes.get(validHexesNumberPlacementIndex);
                hex.setNumber(number);
                validHexes.remove(hex);
                if(removeAdjacentHexes) validHexes.removeAll(getAdjacentHexes(hex));
                //Test Code
                HashSet<Integer> ah = new HashSet<>();
                for(Hex adjacentHex : getAdjacentHexes(hex)){ ah.add(getHexIndex(adjacentHex));}
        }
    }

    /**
     * generates hexes and puts them in "hexes"
     */
    private void generateHexes(){
        //Generates 19 hexagons with random resources
        for(int i = 0; i < 19; i++){
            hexes[i] = new HexImpl(getAvailableResource());
        }

        //Sets the desert to 1
        for(int i = 0; i < 19; i++){
            if(hexes[i].getResource() == Resource.MISC){
                hexes[i].setNumber(1);
                hexes[i].setThief(true);
            }
        }

        //Sets priority numbers
        placeNumbers(priorityTileNumbers,true);

        //Sets other numbers
        placeNumbers(otherTileNumbers,false);
    }

    /**
     * Gets the index of vertex in vertices
     * @param vertex the vertex we want the index of
     * @return the index of vertex in vertices
     */
    private int getVertexIndex(Vertex vertex){
        for(int i = 0; i < vertices.length; i++){
            if(vertex.equals(vertices[i])){
                return i;
            }
        }

        throw new IllegalArgumentException("Requested Vertex doesn't exist on board");
    }

    /**
     * Returns the hex at position @row, @column. Protects against searching out of bounds
     * @param row
     * @param column
     * @return
     */
    private Vertex getVertexByRowAndColumn(int row, int column){
        if(column < 0 || column >= vertexColumnBeginningIndices.length) return null;
        if(row < 0 || row >= vertexColumnLengths[column]) return null;
        return vertices[vertexColumnBeginningIndices[column] + row];
    }

    /**
     * Maps the vertex to adjacent vertices
     * @param vertex the vertex being mapped
     */
    private void mapVertexToAdjacentVertices(Vertex vertex){
        int index = getVertexIndex(vertex);
        int row = getRow(index, vertexColumnBeginningIndices);
        int column = getColumn(index, vertexColumnBeginningIndices);

        if(getColumn(index,vertexColumnBeginningIndices) % 2 == 0){
            vertex.setVertex(getVertexByRowAndColumn(row, column - 1), 0);

            if(vertexColumnLengths[column + 1] > vertexColumnLengths[column]){
                vertex.setVertex(getVertexByRowAndColumn(row + 1, column + 1), 1);
                vertex.setVertex(getVertexByRowAndColumn(row, column + 1), 2);
            }else{
                vertex.setVertex(getVertexByRowAndColumn(row, column + 1), 1);
                vertex.setVertex(getVertexByRowAndColumn(row - 1, column + 1), 2);
            }
        }else{
            vertex.setVertex(getVertexByRowAndColumn(row, column + 1), 2);

            if(vertexColumnLengths[column - 1] > vertexColumnLengths[column]){
                vertex.setVertex(getVertexByRowAndColumn(row, column - 1), 0);
                vertex.setVertex(getVertexByRowAndColumn(row + 1, column - 1), 1);
            }else{
                vertex.setVertex(getVertexByRowAndColumn(row - 1, column - 1), 0);
                vertex.setVertex(getVertexByRowAndColumn(row, column - 1), 1);
            }
        }
    }

    /**
     * Maps the hex to adjacent vertices
     * @param hex the hex being mapped
     */
    private void mapHexToAdjacentVertices(Hex hex){
        int index = getHexIndex(hex);
        int row = getRow(index, hexColumnBeginningIndices);
        int column = getColumn(index, hexColumnBeginningIndices);
        int vertexColumn = (column + 1) * 2;

        hex.setVertex(getVertexByRowAndColumn(row, vertexColumn - 1),0);
        hex.setVertex(getVertexByRowAndColumn(row + 1, vertexColumn - 1),2);
        hex.setVertex(getVertexByRowAndColumn(row + 1, vertexColumn),3);
        hex.setVertex(getVertexByRowAndColumn(row, vertexColumn),5);

        if(vertexColumnLengths[vertexColumn - 1] > vertexColumnLengths[vertexColumn - 2]){
            hex.setVertex(getVertexByRowAndColumn(row, vertexColumn - 2),1);
        }else{
            hex.setVertex(getVertexByRowAndColumn(row + 1, vertexColumn - 2),1);
        }

        if(vertexColumnLengths[vertexColumn] > vertexColumnLengths[vertexColumn + 1]){
            hex.setVertex(getVertexByRowAndColumn(row, vertexColumn + 1),4);
        }else{
            hex.setVertex(getVertexByRowAndColumn(row + 1, vertexColumn + 1),4);
        }
    }

    /**
     * Gets the index of a specified vertex in another vertex's connections
     * @param vertex the vertex you are searching in
     * @param searchFor the vertex you are searching for
     * @return the index of the vertex you are searching for in vertex.getAdjacentVerticies
     */
    private int getVertexRelationshipIndex(Vertex vertex, Vertex searchFor){
        for(int i = 0; i < 3; i++){
            if(vertex != null && vertex.getAdjacentVertices()[i] != null && vertex.getAdjacentVertices()[i].equals(searchFor)){
                return i;
            }
        }

        throw new IllegalStateException("Vertices " + vertex + " and " + searchFor + " are not next to each other.");
    }

    /**
     * Maps the edges of vertex
     * @param vertex the vertex whose edges we are mapping
     */
    private void mapEdges(Vertex vertex){
        for(int i = 0; i < 3; i++){
            Vertex otherVertex = vertex.getAdjacentVertices()[i];

            //If a Vertex is mapped at i, but not an Edge
            if(otherVertex != null && vertex.getEdges()[i] == null){
                int otherVertexIndex = getVertexRelationshipIndex(otherVertex,vertex);
                Edge edge = new EdgeImpl();
                vertex.setEdge(edge,i);
                otherVertex.setEdge(edge,otherVertexIndex);
            }
        }
    }

    private void placePort(int[] location){
        Hex hex = hexes[location[0]];
        Random rng = new Random();
        Resource resource = portResources.remove(rng.nextInt(portResources.size()));

        hex.getVertices()[location[1]].setPort(resource);
        hex.getVertices()[location[2]].setPort(resource);
    }

    /**
     * Generates vertices and places them in "vertices"
     */
    private void generateVertices(){
        //Generates the vertices
        for(int i = 0; i < vertices.length; i++){
            vertices[i] = new VertexImpl();
        }

        //Maps the vertices to other vertices
        for(int i = 0; i < vertices.length; i++){
            mapVertexToAdjacentVertices(vertices[i]);
        }

        //Maps the edges to vertices
        for(int i = 0; i < vertices.length; i++){
            mapEdges(vertices[i]);
        }

        //Maps all hexes to vertices
        for(int i = 0; i < hexes.length; i++){
            mapHexToAdjacentVertices(hexes[i]);
        }

        //Places all ports
        for(int[] location : portLocations){
            placePort(location);
        }
    }

    /**
     * @return a length-19 array containing all the Hexes
     */
    public Hex[] getHexes(){
        return Arrays.copyOf(hexes,19);
    }

    /**
     * @return a length-54 array containing all the Vertices
     */
    public Vertex[] getVertices(){
        return Arrays.copyOf(vertices,54);
    }

    private Set<Edge> getAllEdges() {
        Set<Edge> edges = new HashSet<>();
        for (Vertex vertex : vertices) {
            edges.addAll(Arrays.asList(vertex.getEdges()));
            // adds all not already there
        }
        return edges;
    }

    /**
     * @return all vertices that don't have a settlement or city and aren't next to one
     */
    @Override
    public Set<Vertex> getOpenVertices() {
        return Collections.unmodifiableSet(openVertices);
    }

    /**
     * @return all edges that don't have a road
     */
    @Override
    public Set<Edge> getEmptyEdges() {
        return Collections.unmodifiableSet(emptyEdges);
    }

    /**
     * @param vertex to be removed from the collection of open vertices
     */
    @Override
    public void removeSettlement(Vertex vertex) {
        openVertices.remove(vertex);
        openVertices.removeAll(Arrays.asList(vertex.getAdjacentVertices()));
    }

    /**
     * @param road to be removed from the list of empty edges
     */
    @Override
    public void removeRoad(Edge road) {
        emptyEdges.remove(road);
    }

    /**
     * @return a length-5 array containing all the hex column beginning indices
     */
    public int[] getHexColumnBeginningIndices(){return Arrays.copyOf(hexColumnBeginningIndices,hexColumnBeginningIndices.length);}

    /**
     * @return a length-5 array containing all the vertex column beginning indices
     */
    public int[] getVertexColumnBeginningIndices(){return Arrays.copyOf(vertexColumnBeginningIndices,vertexColumnBeginningIndices.length);}

    /**
     * Test methods
     */
    public int t(Vertex vertex){return getVertexIndex(vertex);};
    public int t(Hex hex){return getHexIndex(hex);};
}
