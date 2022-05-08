package settlers.gui;

import settlers.Player;
import settlers.PlayerImpl;
import settlers.board.*;
import settlers.card.Resource;

import javax.swing.*;
import javax.swing.plaf.basic.BasicListUI;
import javax.swing.plaf.basic.BasicMenuUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GUIPlayerImpl implements GUIPlayer{

    //Board and Player
    private Board board;
    private int playerid;
    private List<Player> players;

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

    //Render Ordering Sets
    private HashMap<Component,Integer> paintLayerMap = new HashMap<>();

    //Frame and image
    private JFrame frame;
    private final Toolkit toolkit = Toolkit.getDefaultToolkit();

    //Thief
    private JLabel thiefImage = new JLabel(new ImageIcon(getImage("src/main/java/settlers/gui/textures/hexes/Thief.png").getScaledInstance(standardObjectSize,standardObjectSize,0)));

    //Functional
    private boolean thisPlayerHasTurn = false;

    //Events
    private ActionListener events;

    //Actions
    private Action passTurn = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            thisPlayerHasTurn = false;
        }
    };


    public GUIPlayerImpl(Board board, Player player, List<Player> players){
        this.board = board;
        this.players = players;
        this.playerid = player.getID();

        //Sets frame up
        frame = new JFrame("Catan: Player " + (player.getID() + 1));
        frame.setBackground(Color.BLACK);
        frame.setSize(1536,768);
        frame.setLayout(null);
        frame.setVisible(true);

        //Maps the frame's actions
        mapActions();

        //Adds the hexes
        putHexes();

        //Adds the vertices
        putVerticesAndRoads();

        //Adds other elements
        putOtherElements();

        //Adds the thief
        frame.add(thiefImage);
        paintLayerMap.put(thiefImage,1);

        //Ensures everything paints at the right Z layer
        orderPainting();

        //Repaints the frame
        frame.repaint();

        thisPlayerHasTurn = true;
        startTurn();
    }

    /**
     * Places GUI elements in the right Z order
     */
    private void orderPainting(){
        for(int i = 0; i < 4; i++) {
            for (Component component : paintLayerMap.keySet()) {
                if(paintLayerMap.containsKey(component) && i == paintLayerMap.get(component)){
                    frame.add(component);
                }
            }
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
     * Creates a label with specified image, and places it at xPos, yPos.
     * @param icon
     * @param xPos
     * @param yPos
     * @return
     */
    private JLabel createLabel(Image icon, int xPos, int yPos, int zOrder){
        //Resizes icon
        icon = icon.getScaledInstance(standardObjectSize,standardObjectSize,0);

        //Creates label
        JLabel label = new JLabel(new ImageIcon(icon));
        label.setBounds(xPos,yPos,standardObjectSize,standardObjectSize);

        //Sets the frame's Z order
        paintLayerMap.put(label,zOrder);

        return label;
    }

    private JLabel createLabel(String location, int xPos, int yPos, int zOrder){
        Image icon = getImage(location);
        return createLabel(icon,xPos,yPos,zOrder);
    }

    /**
     * Creates a button at xPos, yPos.
     * @param xPos
     * @param yPos
     * @return
     */
    private JButton createButton(int xPos, int yPos){
        Image icon = getImage("src/main/java/settlers/gui/textures/misc/Pointer.png").getScaledInstance(standardObjectSize,standardObjectSize,0);
        JButton button = new JButton(new ImageIcon(icon));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setVisible(false);
        button.setEnabled(false);
        button.setRolloverIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/misc/PointerHover.png").getScaledInstance(standardObjectSize,standardObjectSize,0)));
        button.setBounds(xPos + standardObjectSize / 4,yPos + standardObjectSize / 4,standardObjectSize/2,standardObjectSize/2);

        paintLayerMap.put(button,0);

        return button;
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
            JLabel hexOutlineLabel = createLabel("src/main/java/settlers/gui/textures/hexes/HexagonOutline.png",xPos,yPos,3);

            //Creates hex interior
            JLabel hexInteriorLabel = createLabel(getHexInteriorImageByResource(hex.getResource()),xPos,yPos,3);

            //Creates hex button and adds it to the hexButtonMap
            JButton hexButton = createButton(xPos,yPos);
            hexButtonMap.put(hexButton,hex);

            //Puts thief at current position, if position is desert
            if(hex.getResource() == Resource.MISC){
                thiefImage.setBounds(xPos,yPos,standardObjectSize,standardObjectSize);
            }

            //Creates number outline
            JLabel hexNumberOutlineLabel = createLabel(getNumberOutlineImage(hex.getNumber()),xPos,yPos,2);

            //Creates number interior
            JLabel hexNumberLabel = createLabel(getNumberImage(hex.getNumber()),xPos,yPos,2);
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
            JLabel vertexLabel = createLabel("src/main/java/settlers/gui/textures/construction/RoadGrayCenter.png",xPos,yPos,2);

            //Create vertex button
            JButton vertexButton = createButton(xPos,yPos);

            //Create all adjacent right facing roads
            if(row % 2 == 0){
                Edge topEdge = vertex.getEdges()[2];
                Edge bottomEdge = vertex.getEdges()[1];

                if(topEdge != null) {
                    int edgeXPos = xPos + 20;
                    int edgeYPos = yPos - 24;

                    JLabel topEdgeLabel = createLabel("src/main/java/settlers/gui/textures/construction/RoadGrayLeft.png",edgeXPos,edgeYPos,3);
                    JButton topEdgeButton = createButton(edgeXPos,edgeYPos);

                    edgeButtonMap.put(topEdgeButton,topEdge);
                }

                if(bottomEdge != null) {
                    int edgeXPos = xPos + 16;
                    int edgeYPos = yPos + 20;

                    JLabel bottomEdgeLabel = createLabel("src/main/java/settlers/gui/textures/construction/RoadGrayRight.png",edgeXPos,edgeYPos,3);
                    JButton bottomEdgeButton = createButton(edgeXPos,edgeYPos);

                    edgeButtonMap.put(bottomEdgeButton,bottomEdge);
                }


            }else{
                Edge sideEdge = vertex.getEdges()[2];

                if(sideEdge != null){
                    int edgeXPos = xPos + 28;
                    int edgeYPos = yPos;

                    JLabel sideEdgeLabel = createLabel("src/main/java/settlers/gui/textures/construction/RoadGrayStraight.png",edgeXPos,edgeYPos,3);
                    JButton sideEdgeButton = createButton(edgeXPos,edgeYPos);

                    edgeButtonMap.put(sideEdgeButton,sideEdge);
                }
            }

            vertexButtonMap.put(vertexButton,vertex);
            vertexLabelMap.put(vertex,vertexLabel);
        }
    }

    /**
     * Puts all non-board related elements onto the GUI
     */
    private void putOtherElements(){
        //Places the label for this player
        JLabel thisPlayerLabel = createLabel("",50,40,1);
        thisPlayerLabel.setIcon(new ImageIcon(getCityByColor(playerid).getScaledInstance(256,256,0)));

        //Places the labels for other players
        int currentYOffset = 120;
        int yOffsetIncrement = 60;
        for(Player plr : players){
            if(plr.getID() != playerid) {
                JLabel playerLabel = createLabel("", 35, currentYOffset, 1);
                playerLabel.setIcon(new ImageIcon(getCityByColor(plr.getID()).getScaledInstance(128, 128, 0)));
                currentYOffset += yOffsetIncrement;
            }
        }
    }

    private Image getCityByColor(int id){
        switch (id){
            case 0:
                return getImage("src/main/java/settlers/gui/textures/construction/CityRed.png");
            case 1:
                return getImage("src/main/java/settlers/gui/textures/construction/CityBlue.png");
            case 2:
                return getImage("src/main/java/settlers/gui/textures/construction/CityWhite.png");
            default:
                return getImage("src/main/java/settlers/gui/textures/construction/CityOrange.png");
        }
    }

    /**
     * Starts Player's turn
     */
    public void startTurn(){
        while(thisPlayerHasTurn){
            try {
                Thread.sleep(1);
            }catch (InterruptedException e){
                throw new IllegalStateException("InterruptedException was thrown. Exception: " + e);
            }
        }
    }

    /**
     * Maps all of the frame's actions
     */
    private void mapActions(){
        frame.getRootPane().getInputMap().put(KeyStroke.getKeyStroke((char) 32),"passTurn");
        frame.getRootPane().getActionMap().put("passTurn",passTurn);
    }
}