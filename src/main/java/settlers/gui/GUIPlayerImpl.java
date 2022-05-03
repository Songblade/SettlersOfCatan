package settlers.gui;

import settlers.Player;
import settlers.board.*;

import javax.swing.JFrame;

public class GUIPlayerImpl implements GUIPlayer{

    private Board board;

    public GUIPlayerImpl(Board board){
        this.board = board;
    }
}