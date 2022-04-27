package settlers.board;

import org.junit.jupiter.api.Test;

import settlers.board.Board;
import settlers.board.BoardImpl;
import settlers.board.Hex;

public class BoardTest{

    @Test
    public void createBoard(){
        Board board;
        try {
            board = new BoardImpl();
        }catch (Throwable e){
            e.printStackTrace();
        }

        board = new BoardImpl();

        //Prints board hexes
        int i = 0;
        for(Hex hex : board.getHexes()){
            //System.out.println("Hexes: " + board.getHexes()[i]);
            i++;
            System.out.println("Hex: " + hex + " | Resource Type: " + hex.getResource() + " | Number: " + hex.getNumber());
        }
    }
}