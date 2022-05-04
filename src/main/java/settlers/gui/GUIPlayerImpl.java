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
import java.util.HashMap;

public class GUIPlayerImpl implements GUIPlayer{

    //Board
    private Board board;

    //Final params
    private final int boardOffsetX = 468;
    private final int boardOffsetY = 40;
    private final int standardObjectSize = 128;

    //Maps
    private HashMap<JButton,Hex> hexButtonMap = new HashMap<>();
    private HashMap<JButton,Vertex> vertexButtonMap = new HashMap<>();
    private HashMap<JButton,Edge> edgeButtonMap = new HashMap<>();

    private HashMap<Vertex,JLabel> vertexLabelMap = new HashMap<>();
    private HashMap<Edge,JLabel> edgeLabelMap = new HashMap<>();

    //Frame and image
    private Frame frame;
    private final Toolkit toolkit = Toolkit.getDefaultToolkit();

    //Thief
    private JLabel thiefImage = new JLabel(new ImageIcon(getImage("src/main/java/settlers/gui/textures/hexes/Thief.png").getScaledInstance(standardObjectSize,standardObjectSize,0)));


    public GUIPlayerImpl(Board board){
        this.board = board;

        //Sets frame up
        frame = new JFrame("This is a totally legit Catan");
        frame.setBackground(Color.BLACK);
        frame.setSize(1536,768);
        frame.setLayout(null);
        frame.setVisible(true);

        //Adds the hexes
        putHexes();

        //Adds the vertices
        putVerticesAndRoads();

        //Adds the thief
        frame.add(thiefImage);
        frame.setComponentZOrder(thiefImage, 0);

        //Repaints the frame
        frame.repaint();

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

    /**
     * Gets the outline image in hexes corresponding to the number
     * @param number the number you want the outline image corresponding to
     * @return the outline image corresponding to the number
     */
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

            //Creates hex outline
            Image hexOutlineIcon = getImage("src/main/java/settlers/gui/textures/hexes/HexagonOutline.png").getScaledInstance(standardObjectSize,standardObjectSize,0);
            JLabel hexOutlineLabel = new JLabel(new ImageIcon(hexOutlineIcon));
            hexOutlineLabel.setBounds(xPos,yPos,standardObjectSize,standardObjectSize);

            //Creates hex interior
            JLabel hexInteriorLabel = new JLabel(new ImageIcon(getHexInteriorImageByResource(hex.getResource()).getScaledInstance(standardObjectSize,standardObjectSize,0)));
            hexInteriorLabel.setBounds(xPos,yPos,standardObjectSize,standardObjectSize);

            //Creates hex button and adds it to the hexButtonMap
            Image hexButtonIcon = getImage("src/main/java/settlers/gui/textures/misc/PointerHover.png").getScaledInstance(standardObjectSize,standardObjectSize,0);
            JButton hexButton = new JButton(new ImageIcon(hexButtonIcon));
            hexButton.setBorderPainted(false);
            hexButton.setContentAreaFilled(false);
            hexButton.setFocusPainted(false);
            hexButton.setVisible(false);
            hexButton.setEnabled(false);
            hexButton.setRolloverIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/misc/Pointer.png").getScaledInstance(standardObjectSize,standardObjectSize,0)));
            hexButton.setBounds(xPos + standardObjectSize / 4,yPos + standardObjectSize / 4,standardObjectSize/2,standardObjectSize/2);
            hexButtonMap.put(hexButton,hex);

            //Puts thief at current position, if position is desert
            if(hex.getResource() == Resource.MISC){
                thiefImage.setBounds(xPos,yPos,standardObjectSize,standardObjectSize);
            }

            //Creates number outline
            JLabel hexNumberOutlineLabel = new JLabel(new ImageIcon(getNumberOutlineImage(hex.getNumber()).getScaledInstance(standardObjectSize,standardObjectSize,0)));
            hexNumberOutlineLabel.setBounds(xPos,yPos,standardObjectSize,standardObjectSize);

            //Creates number interior
            JLabel hexNumberLabel = new JLabel(new ImageIcon(getNumberImage(hex.getNumber()).getScaledInstance(standardObjectSize,standardObjectSize,0)));
            hexNumberLabel.setBounds(xPos,yPos,standardObjectSize,standardObjectSize);

            frame.add(hexOutlineLabel);
            frame.add(hexInteriorLabel);
            frame.add(hexButton);
            frame.add(hexNumberLabel);
            frame.add(hexNumberOutlineLabel);

            frame.setComponentZOrder(hexOutlineLabel,0);
            frame.setComponentZOrder(hexInteriorLabel,0);
            frame.setComponentZOrder(hexNumberLabel,0);
            frame.setComponentZOrder(hexNumberOutlineLabel,0);
            frame.setComponentZOrder(hexButton,0);
        }
    }

    /**
     * Places all vertices
     */
    private void putVerticesAndRoads(){
        Vertex[] vertices = board.getVertices();
        int[] vertexColumnBeginningIndices = board.getVertexColumnBeginningIndices();

        int row = -1;
        int column = -1;
        int xOffset = -120;

        for(int i = 0; i < vertices.length; i++) {
            //Calculates the current row and column
            if (row < vertexColumnBeginningIndices.length - 1 && i == vertexColumnBeginningIndices[row + 1]) {
                if(row % 2 == 0){
                    xOffset += 36;
                }else{
                    xOffset += 56;
                }

                row++;
                column = 0;
            } else {
                column++;
            }

            Vertex vertex = vertices[i];

            int xPos = boardOffsetX + xOffset;
            int yPos = boardOffsetY + (column * 88 + (Math.abs(3 - (row + 1)/2)) * 44 - 44);

            //Create vertex label
            JLabel vertexLabel = new JLabel(new ImageIcon(getImage("src/main/java/settlers/gui/textures/construction/RoadGrayCenter.png").getScaledInstance(standardObjectSize, standardObjectSize, 0)));
            vertexLabel.setBounds(xPos, yPos, standardObjectSize, standardObjectSize);

            //Create vertex button
            Image vertexButtonIcon = getImage("src/main/java/settlers/gui/textures/misc/PointerHover.png").getScaledInstance(standardObjectSize,standardObjectSize,0);
            JButton vertexButton = new JButton(new ImageIcon(vertexButtonIcon));
            vertexButton.setBorderPainted(false);
            vertexButton.setContentAreaFilled(false);
            vertexButton.setFocusPainted(false);
            vertexButton.setVisible(false);
            vertexButton.setEnabled(false);
            vertexButton.setRolloverIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/misc/Pointer.png").getScaledInstance(standardObjectSize,standardObjectSize,0)));
            vertexButton.setBounds(xPos + standardObjectSize / 4,yPos + standardObjectSize / 4,standardObjectSize/2,standardObjectSize/2);

            //Create all adjacent right facing roads
            if(row % 2 == 0){
                Edge topEdge = vertex.getEdges()[2];
                Edge bottomEdge = vertex.getEdges()[1];

                if(topEdge != null) {
                    int edgeXPos = xPos + 20;
                    int edgeYPos = yPos - 24;

                    JLabel topEdgeLabel = new JLabel(new ImageIcon(getImage("src/main/java/settlers/gui/textures/construction/RoadGrayLeft.png").getScaledInstance(standardObjectSize, standardObjectSize, 0)));
                    topEdgeLabel.setBounds(edgeXPos, edgeYPos, standardObjectSize, standardObjectSize);

                    Image topEdgeButtonIcon = getImage("src/main/java/settlers/gui/textures/misc/PointerHover.png").getScaledInstance(standardObjectSize,standardObjectSize,0);
                    JButton topEdgeButton = new JButton(new ImageIcon(topEdgeButtonIcon));
                    topEdgeButton.setBorderPainted(false);
                    topEdgeButton.setContentAreaFilled(false);
                    topEdgeButton.setFocusPainted(false);
                    //topEdgeButton.setVisible(false);
                    //topEdgeButton.setEnabled(false);
                    topEdgeButton.setRolloverIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/misc/Pointer.png").getScaledInstance(standardObjectSize,standardObjectSize,0)));
                    topEdgeButton.setBounds(edgeXPos + standardObjectSize / 4,edgeYPos + standardObjectSize / 4,standardObjectSize/2,standardObjectSize/2);

                    frame.add(topEdgeLabel);
                    frame.add(topEdgeButton);

                    frame.setComponentZOrder(topEdgeLabel, 2);
                    frame.setComponentZOrder(topEdgeButton, 0);
                }

                if(bottomEdge != null) {
                    int edgeXPos = xPos + 16;
                    int edgeYPos = yPos + 20;

                    JLabel bottomEdgeLabel = new JLabel(new ImageIcon(getImage("src/main/java/settlers/gui/textures/construction/RoadGrayRight.png").getScaledInstance(standardObjectSize, standardObjectSize, 0)));
                    bottomEdgeLabel.setBounds(edgeXPos, edgeYPos, standardObjectSize, standardObjectSize);

                    Image bottomEdgeButtonIcon = getImage("src/main/java/settlers/gui/textures/misc/PointerHover.png").getScaledInstance(standardObjectSize,standardObjectSize,0);
                    JButton bottomEdgeButton = new JButton(new ImageIcon(bottomEdgeButtonIcon));
                    bottomEdgeButton.setBorderPainted(false);
                    bottomEdgeButton.setContentAreaFilled(false);
                    bottomEdgeButton.setFocusPainted(false);
                    //bottomEdgeButton.setVisible(false);
                    //bottomEdgeButton.setEnabled(false);
                    bottomEdgeButton.setRolloverIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/misc/Pointer.png").getScaledInstance(standardObjectSize,standardObjectSize,0)));
                    bottomEdgeButton.setBounds(edgeXPos + standardObjectSize / 4,edgeYPos + standardObjectSize / 4,standardObjectSize/2,standardObjectSize/2);

                    frame.add(bottomEdgeLabel);
                    frame.add(bottomEdgeButton);

                    frame.setComponentZOrder(bottomEdgeLabel,2);
                    frame.setComponentZOrder(bottomEdgeButton,0);
                }


            }else{
                Edge sideEdge = vertex.getEdges()[2];

                if(sideEdge != null){
                    int edgeXPos = xPos + 28;
                    int edgeYPos = yPos;

                    JLabel sideEdgeLabel = new JLabel(new ImageIcon(getImage("src/main/java/settlers/gui/textures/construction/RoadGrayStraight.png").getScaledInstance(standardObjectSize, standardObjectSize, 0)));
                    sideEdgeLabel.setBounds(edgeXPos, edgeYPos, standardObjectSize, standardObjectSize);

                    Image sideEdgeButtonIcon = getImage("src/main/java/settlers/gui/textures/misc/PointerHover.png").getScaledInstance(standardObjectSize,standardObjectSize,0);
                    JButton sideEdgeButton = new JButton(new ImageIcon(sideEdgeButtonIcon));
                    sideEdgeButton.setBorderPainted(false);
                    sideEdgeButton.setContentAreaFilled(false);
                    sideEdgeButton.setFocusPainted(false);
                    //sideEdgeButton.setVisible(false);
                    //sideEdgeButton.setEnabled(false);
                    sideEdgeButton.setRolloverIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/misc/Pointer.png").getScaledInstance(standardObjectSize,standardObjectSize,0)));
                    sideEdgeButton.setBounds(edgeXPos + standardObjectSize / 4,edgeYPos + standardObjectSize / 4,standardObjectSize/2,standardObjectSize/2);


                    frame.add(sideEdgeLabel);
                    frame.add(sideEdgeButton);

                    frame.setComponentZOrder(sideEdgeLabel,2);
                    frame.setComponentZOrder(sideEdgeButton,0);
                }
            }

            vertexButtonMap.put(vertexButton,vertex);
            vertexLabelMap.put(vertex,vertexLabel);

            frame.add(vertexLabel);
            frame.add(vertexButton);

            frame.setComponentZOrder(vertexLabel, 1);
            frame.setComponentZOrder(vertexButton,0);
        }
    }

    /**
     * Temp Method
     */
    private void sleep(){
        try {
            while (true) {
                Thread.sleep(1);
            }
        }catch (InterruptedException e){
            throw new IllegalStateException("Thread drank caffine");
        }
    }
}