package settlers.board;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import settlers.board.Board;
import settlers.board.BoardImpl;
import settlers.board.Hex;
import settlers.card.Resource;

import java.util.HashMap;

public class BoardTest{

    private String getAdjacentVerticesString(Vertex baseVertex, BoardImpl board){
        String output = "[";

        for(Vertex vertex : baseVertex.getAdjacentVertices()){
            if(vertex != null) {
                output += board.t(vertex) + ", ";
            }
        }

        output += "]";

        return output;
    }

    private String getAdjacentHexesString(Hex baseHex, BoardImpl board){
        String output = "[";

        for(Vertex vertex : baseHex.getVertices()){
            if(vertex != null) {
                output += board.t(vertex) + ", ";
            }
        }

        output += "]";

        return output;
    }

    private String getAdjacentEdgesString(Vertex vertex, BoardImpl board){
        String output = "[";

        for(Edge edge : vertex.getEdges()){
            if(edge != null) {
                output += edge.hashCode() + ", ";
            }
        }

        output += "]";

        return output;
    }

    private String getPortString(Vertex vertex){
        if(vertex.getPort() != null){
            return vertex.getPort().toString();
        }else{
            return "None";
        }
    }

    @Test
    public void createBoard(){
        try{
            BoardImpl board = new BoardImpl();
            for(Vertex vertex : board.getVertices()){
                System.out.println("Vertex: " + board.t(vertex) + " | Adjacent vertices: " + getAdjacentVerticesString(vertex, board) + " | Adjacent edges: " + getAdjacentEdgesString(vertex, board) + " | Port: " + getPortString(vertex));
            }
            System.out.println("-------------------------------------------------");
            for (Hex hex : board.getHexes()){
                System.out.println("Hex: " + board.t(hex) + " | Adjacent vertices: " + getAdjacentHexesString(hex, board));
            }
        }catch (Throwable e){
            e.printStackTrace();
        }
    }

    //Generates a lot of boards to test if there are any possible issues with board generation
    @Test
    public void createALotOfBoards(){
        for(int i = 0; i < 10000; i++){
            Board board = new BoardImpl();
        }
    }

    //Ensures that the right quantities of numbers and resources are dispersed among the hexes
    @Test
    public void ensureQuantitiesOfNumbersAndResourcesAreCorrect() {
        for (int m = 0; m < 1000; m++) {
            Board board = new BoardImpl();
            HashMap<Resource,Integer> resourceCounter = new HashMap<>();
            HashMap<Integer,Integer> numberCounter = new HashMap<>();

            resourceCounter.put(Resource.WOOD,0);
            resourceCounter.put(Resource.BRICK,0);
            resourceCounter.put(Resource.SHEEP,0);
            resourceCounter.put(Resource.WHEAT,0);
            resourceCounter.put(Resource.ORE,0);
            resourceCounter.put(Resource.MISC,0);

            for(int i = 1; i <= 12; i++){
                numberCounter.put(i,0);
            }

            for(Hex hex : board.getHexes()){
                resourceCounter.put(hex.getResource(),resourceCounter.get(hex.getResource()) + 1);
                numberCounter.put(hex.getNumber(),numberCounter.get(hex.getNumber()) + 1);
            }

            Assertions.assertEquals(4,resourceCounter.get(Resource.WOOD));
            Assertions.assertEquals(3,resourceCounter.get(Resource.BRICK));
            Assertions.assertEquals(4,resourceCounter.get(Resource.SHEEP));
            Assertions.assertEquals(4,resourceCounter.get(Resource.WHEAT));
            Assertions.assertEquals(3,resourceCounter.get(Resource.ORE));
            Assertions.assertEquals(1,resourceCounter.get(Resource.MISC));

            Assertions.assertEquals(1,numberCounter.get(1));
            Assertions.assertEquals(1,numberCounter.get(2));
            Assertions.assertEquals(2,numberCounter.get(3));
            Assertions.assertEquals(2,numberCounter.get(4));
            Assertions.assertEquals(2,numberCounter.get(5));
            Assertions.assertEquals(2,numberCounter.get(6));
            Assertions.assertEquals(0,numberCounter.get(7));
            Assertions.assertEquals(2,numberCounter.get(8));
            Assertions.assertEquals(2,numberCounter.get(9));
            Assertions.assertEquals(2,numberCounter.get(10));
            Assertions.assertEquals(2,numberCounter.get(11));
            Assertions.assertEquals(1,numberCounter.get(12));
        }
    }
}