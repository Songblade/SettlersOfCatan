package settlers.gui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import settlers.Main;
import settlers.MainImpl;
import settlers.board.Board;
import settlers.board.BoardImpl;

public class GUIMainTest {

    @Test
    @Disabled
    public void createGUIMain(){
        /**try {
            Board board = new BoardImpl();
            GUIMain guiMain = new GUIMainImpl(new MainImpl(4));
        }catch (Throwable e){
            e.printStackTrace();
        }*/
    }

    @Test
    public void createMain(){
        try {
            Main main = new MainImpl(4);
            main.buildSettlement(main.getPlayers().get(0), main.getBoard().getVertices()[0]);
        }catch (Throwable e){
            e.printStackTrace();
        }
    }
}