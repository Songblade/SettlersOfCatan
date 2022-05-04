package settlers.gui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import settlers.board.Board;
import settlers.board.BoardImpl;

public class GUIMainTest {

    @Test
    public void createGUIMain(){
        try {
            Board board = new BoardImpl();
            GUIMain guiMain = new GUIMainImpl(board);
        }catch (Throwable e){
            e.printStackTrace();
        }
    }
}