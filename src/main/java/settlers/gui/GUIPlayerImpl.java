package settlers.gui;

import settlers.Player;
import settlers.board.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicListUI;
import javax.swing.plaf.basic.BasicMenuUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

public class GUIPlayerImpl implements GUIPlayer{

    private Board board;
    private int boardOffsetX = 468;
    private int boardOffsetY = 40;

    private Frame frame;

    private final Toolkit toolkit = Toolkit.getDefaultToolkit();

    public GUIPlayerImpl(Board board){
        this.board = board;

        //Sets frame up
        frame = new JFrame("This is a totally legit Catan");
        frame.setSize(1536,768);
        frame.setLayout(null);
        frame.setVisible(true);

        sleep();
    }

    /**
     * Gets an image based on its path
     * @param path
     * @return
     */
    private Image getImage(String path){
        return toolkit.getImage(path);
    }

    /**
     * Scales an image
     * @param image the image to be scaled
     * @param width width of the scaled image
     * @param height height of the scaled image
     * @return the scaled image
     */
    private Image scaleImage(Image image, int width, int height){
        return image.getScaledInstance(width,height,0);
    }

    /**
     * Temp Method
     */
    private void sleep(){
        try {
            Thread.sleep(10000);
        }catch (InterruptedException e){
            throw new IllegalStateException("Thread drank caffine");
        }
    }
}