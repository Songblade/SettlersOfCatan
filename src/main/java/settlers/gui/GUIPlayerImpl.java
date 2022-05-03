package settlers.gui;

import settlers.Player;
import settlers.board.*;
import settlers.card.Resource;

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

    private final int standardObjectSize = 128;

    public GUIPlayerImpl(Board board){
        this.board = board;

        //Sets frame up
        frame = new JFrame("This is a totally legit Catan");
        frame.setBackground(Color.BLACK);
        frame.setSize(1536,768);
        frame.setLayout(null);
        frame.setVisible(true);

        putHexes();
        frame.repaint();

        for(int i = 0; i < 100; i++) {
            sleep();
        }
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
     * Gets the internal hex image corresponding to a resource
     * @param resource the resource you want the hex image for
     * @return the hex image for the resource
     */
    private Image getHexInteriorImageByResource(Resource resource){
        String prefix = "src/main/java/settlers/gui/textures/hexes/";

        switch (resource){
            case WOOD:
                return getImage(prefix + "TileForest.png");
            case BRICK:
                return getImage(prefix + "TileBrick.png");
            case SHEEP:
                return getImage(prefix + "TileGrasslands.png");
            case WHEAT:
                return getImage(prefix + "TileFarmlands.png");
            case ORE:
                return getImage(prefix + "TileMountians.png");
            default:
                return getImage(prefix + "TileDesert.png");
        }
    }

    /**
     * Gets the image in numbers corresponding to the number
     * @param number the number you want the image corresponding to
     * @return the image corresponding to the number
     */
    private Image getNumberImage(int number){
        return getImage("src/main/java/settlers/gui/textures/numbers/" + number + ".png");
    }

    private Image getNumberOutlineImage(int number){
        String prefix = "src/main/java/settlers/gui/textures/hexes/HexagonOutline";

        switch (number){
            case 2:
            case 12:
                return getImage(prefix + "Blue.png");
            case 3:
            case 11:
                return getImage(prefix + "Green.png");
            case 4:
            case 10:
                return getImage(prefix + "Yellow.png");
            case 5:
            case 9:
                return getImage(prefix + "Orange.png");
            case 6:
            case 8:
                return getImage(prefix + "Red.png");
            default:
                return getImage(prefix + "Purple.png");
        }
    }

    /**
     * Puts all hexes onto the frame
     */
    private void putHexes(){
        Hex[] hexes = board.getHexes();
        int[] hexColumnBeginningIndices = board.getHexColumnBeginningIndices();

        int row = -1;
        int column = -1;
        for(int i = 0; i < hexes.length; i++){
            //Calculates the current row and column
            if(row < hexColumnBeginningIndices.length - 1 && i == hexColumnBeginningIndices[row + 1]){
                row++;
                column = 0;
            }else{
                column++;
            }

            Hex hex = hexes[i];

            int xPos = boardOffsetX + (92 * row);
            int yPos = boardOffsetY + Math.abs((row - 2) * 44) + (88 * column);

            Image hexOutlineIcon = getImage("src/main/java/settlers/gui/textures/hexes/HexagonOutline.png").getScaledInstance(standardObjectSize,standardObjectSize,0);
            JLabel hexOutlineLabel = new JLabel(new ImageIcon(hexOutlineIcon));
            hexOutlineLabel.setBounds(xPos,yPos,standardObjectSize,standardObjectSize);

            JLabel hexInteriorLabel = new JLabel(new ImageIcon(getHexInteriorImageByResource(hex.getResource()).getScaledInstance(standardObjectSize,standardObjectSize,0)));
            hexInteriorLabel.setBounds(xPos,yPos,standardObjectSize,standardObjectSize);

            JLabel hexNumberOutlineLabel = new JLabel(new ImageIcon(getNumberOutlineImage(hex.getNumber()).getScaledInstance(standardObjectSize,standardObjectSize,0)));
            hexNumberOutlineLabel.setBounds(xPos,yPos,standardObjectSize,standardObjectSize);

            JLabel hexNumberLabel = new JLabel(new ImageIcon(getNumberImage(hex.getNumber()).getScaledInstance(standardObjectSize,standardObjectSize,0)));
            hexNumberLabel.setBounds(xPos,yPos,standardObjectSize,standardObjectSize);

            frame.add(hexOutlineLabel);
            frame.add(hexInteriorLabel);
            frame.add(hexNumberLabel);
            frame.add(hexNumberOutlineLabel);

            frame.setComponentZOrder(hexOutlineLabel,0);
            frame.setComponentZOrder(hexInteriorLabel,0);
            frame.setComponentZOrder(hexNumberLabel,0);
            frame.setComponentZOrder(hexNumberOutlineLabel,0);
        }
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