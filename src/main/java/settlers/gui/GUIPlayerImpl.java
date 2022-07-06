package settlers.gui;

import settlers.Player;
import settlers.PlayerImpl;
import settlers.board.*;
import settlers.card.DevelopmentCard;
import settlers.card.Resource;

import javax.swing.*;
import javax.swing.plaf.basic.BasicListUI;
import javax.swing.plaf.basic.BasicMenuUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;

public class GUIPlayerImpl implements GUIPlayer{

    //Board and Player
    private final Board board;
    private final Player player;
    private final List<Player> players;
    private final GUIMain main;

    //Final params
    private final int boardOffsetX = 468;
    private final int boardOffsetY = 40;
    private final int standardObjectSize = 128;
    private final int dieCounterSize = 256;

    //Button maps
    private HashMap<JButton,Hex> hexButtonMap = new HashMap<>();
    private HashMap<JButton,Vertex> vertexButtonMap = new HashMap<>();
    private HashMap<JButton,Edge> edgeButtonMap = new HashMap<>();
    private HashMap<JButton,Resource> resourceButtonMap = new HashMap<>();
    private HashMap<JButton,Player> playerButtonMap = new HashMap<>();

    //Inv Button maps
    private HashMap<Resource,JButton> invResourceButtonMap = new HashMap<>();

    //Label maps
    private HashMap<Vertex,JLabel> vertexLabelMap = new HashMap<>();
    private HashMap<Edge,JLabel> edgeLabelMap = new HashMap<>();
    private HashMap<Player,JLabel> playerSelectionLabelMap = new HashMap<>();
    private HashMap<Resource,JTextField> resourceTextMap = new HashMap<>();
    private HashMap<Resource,JTextField> tradeTextMap = new HashMap<>();
    private HashMap<DevelopmentCard,JTextField> developmentTextMap = new HashMap<>();
    private HashMap<Player,HashMap<String,JTextField>> playerTextMap = new HashMap<>();
    private HashMap<Move,JTextField> moveTextMap = new HashMap<>();

    //Other maps
    private HashMap<Edge, String> edgeDirectionMap = new HashMap<>();

    //Render Ordering Maps
    private HashMap<Component,Integer> paintLayerMap = new HashMap<>();

    //Possible Moves Map
    private HashMap<Move,Boolean> possibleMoves = new HashMap();
    private HashMap<Move,MoveDescription> moveDescriptionMap = new HashMap<>();

    //Request tables
    private Resource[] yearOfPlentyRequest = new Resource[2];
    private Edge[] roadBuildingRequest = new Edge[2];
    private Resource[] bankTradeRequest = new Resource[2];
    private HashMap<Resource, Integer> playerTradeRequest = new HashMap<>();
    private HashSet<Player> playerTradeRequestReceivers = new HashSet<>();

    //Frame and image
    private JFrame frame;
    private final Toolkit toolkit = Toolkit.getDefaultToolkit();

    //Fields for bottom cards
    JTextField playedKnightsCountLabel;
    JLabel longestRoadLabel;
    JLabel largestArmyLabel;

    //Thief and die
    private JLabel thiefImage;
    private JLabel dieCounter;
    private JLabel dieCounterOutline;

    //Player to player trade related variables
    private JButton tradeDirectionButton;
    private boolean tradeDirectionGiving = false;

    //Functional
    private boolean thisPlayerHasTurn = false;
    private boolean mainPhase = false;
    private boolean stealPreformed = false;
    private int targetResourceAmount = 0;
    private Vertex lastSettlementSpot = null;
    private Edge lastRoadSpot = null;
    private Hex thiefRequestSpot = null;
    private GUIState currentState = GUIState.NONE;

    //Fonts
    Font defaultFont;
    Font defaultFontRed;
    Font defaultFontBlue;
    Font defaultFontGreen;


    public GUIPlayerImpl(GUIMain main,Board board, Player player, List<Player> players){
        this.main = main;
        this.board = board;
        this.players = players;
        this.player = player;

        //Sets frame up
        frame = new JFrame("Catan: Player " + (player.getID() + 1));
        frame.setBackground(Color.BLACK);
        frame.setSize(1536,768);
        frame.setLayout(null);
        frame.setVisible(true);

        //Maps the frame's actions
        mapActions();

        //Sets up fonts
        setupFonts();

        //Adds the thief
        thiefImage = createLabel("src/main/java/settlers/gui/textures/hexes/Thief.png",0,0,1);

        //Adds the hexes
        putHexes();

        //Adds the vertices
        putVerticesAndRoads();

        //Adds the ports
        putPorts();

        //Adds other elements
        putOtherElements();

        //Disables other GUI elements which should start disabled
        disablePlayerTradingGUIElements();

        //Ensures everything paints at the right Z layer
        orderPainting();

        //Repaints the frame
        frame.repaint();
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

    private Map<TextAttribute,Object> createDefaultTextAttributes(){
        Map<TextAttribute,Object> defaultFontAttributes = new HashMap<>();
        defaultFontAttributes.put(TextAttribute.FAMILY,Font.DIALOG);
        defaultFontAttributes.put(TextAttribute.WEIGHT,TextAttribute.WEIGHT_BOLD);
        defaultFontAttributes.put(TextAttribute.SIZE,24);
        return defaultFontAttributes;
    }

    /**
     * Sets up the fonts
     */
    private void setupFonts(){
        Font baseFont = new Font(Font.DIALOG,Font.BOLD,24);

        Map<TextAttribute,Object> defaultFontAttributes = createDefaultTextAttributes();
        defaultFont = new Font(defaultFontAttributes);

        Map<TextAttribute,Object> defaultFontAttributesRed = createDefaultTextAttributes();
        defaultFontAttributesRed.put(TextAttribute.FOREGROUND,Color.RED);
        defaultFontRed = new Font(defaultFontAttributesRed);
        
        Map<TextAttribute,Object> defaultFontAttributesBlue = createDefaultTextAttributes();
        defaultFontAttributesBlue.put(TextAttribute.FOREGROUND,Color.BLUE);
        defaultFontBlue = new Font(defaultFontAttributesBlue);

        Map<TextAttribute,Object> defaultFontAttributesGreen = createDefaultTextAttributes();
        defaultFontAttributesGreen.put(TextAttribute.FOREGROUND,Color.GREEN);
        defaultFontGreen = new Font(defaultFontAttributesGreen);
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

    private JTextField createText(String text, int xPos, int yPos, int zOrder){
        JTextField field = new JTextField(text);
        field.setBounds(xPos,yPos,standardObjectSize,standardObjectSize);
        field.setEditable(false);
        field.setFocusable(false);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder());
        field.setFont(defaultFont);

        //Sets the frame's Z order
        paintLayerMap.put(field,zOrder);

        return field;
    }


    private JTextField createMoveText(Move move){
        JTextField field = createText("", 1152, 0, 2);
        field.setHorizontalAlignment(JTextField.LEFT);
        field.setSize(384,32);
        field.setVisible(false);
        field.setFont(new Font(Font.DIALOG,Font.BOLD,20));

        moveTextMap.put(move,field);
        return field;
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
        disableButton(button);
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
            hexButton.addActionListener(hexButtonClickedAction(hex));

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
     * Creates an edge
     */
    private void createEdge(Edge edge, String direction, int offsetX, int offsetY){
        JLabel edgeLabel = createLabel("src/main/java/settlers/gui/textures/construction/RoadGray" + direction + ".png",offsetX,offsetY,3);
        JButton edgeButton = createButton(offsetX,offsetY);

        edgeButton.addActionListener(edgeButtonClickedAction(edge));

        edgeButtonMap.put(edgeButton,edge);
        edgeLabelMap.put(edge,edgeLabel);
        edgeDirectionMap.put(edge,direction);
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
            JLabel vertexLabel = createLabel("src/main/java/settlers/gui/textures/construction/RoadGrayCenter.png", xPos, yPos, 1);

            //Create vertex button
            JButton vertexButton = createButton(xPos,yPos);
            vertexButton.addActionListener(vertexButtonClickedAction(vertex));

            //Create all adjacent right facing roads
            if(row % 2 == 0){
                Edge topEdge = vertex.getEdges()[2];
                Edge bottomEdge = vertex.getEdges()[1];

                if(topEdge != null) {
                    createEdge(topEdge,"Left",xPos + 20, yPos - 24);
                }

                if(bottomEdge != null) {
                    createEdge(bottomEdge,"Right",xPos + 16, yPos + 20);
                }
            }else{
                Edge sideEdge = vertex.getEdges()[2];

                if(sideEdge != null){
                    createEdge(sideEdge,"Straight",xPos + 28, yPos);
                }
            }

            vertexButtonMap.put(vertexButton,vertex);
            vertexLabelMap.put(vertex,vertexLabel);
        }
    }

    private Image getPortDirectionImage(int direction){
        String basePath = "src/main/java/settlers/gui/textures/hexes/";

        switch (direction){
            case 0:
                return getImage(basePath + "PortUpLeft.png");
            case 1:
                return getImage(basePath + "PortDownLeft.png");
            case 2:
                return getImage(basePath + "PortDownCenter.png");
            case 3:
                return getImage(basePath + "PortDownRight.png");
            case 4:
                return getImage(basePath + "PortUpRight.png");
            default:
                return getImage(basePath + "PortUpCenter.png");

        }
    }

    /**
     * Puts ports onto the board
     */
    private void putPorts(){
        String basePath = "src/main/java/settlers/gui/textures/hexes/";

        int[][] portImageLocations = {{0,-92,44,3},{1,-92,220,4},{10,0,352,4},{11,92,-44,2},{26,184,440,5},{33,276,-44,2},{42,368,352,0},{47,460,44,1},{49,460,220,0}};

        for(int i = 0; i < portImageLocations.length; i++) {
            int offsetX = boardOffsetX + portImageLocations[i][1];
            int offsetY =  boardOffsetY + portImageLocations[i][2];

            JLabel portDirection = createLabel(getPortDirectionImage(portImageLocations[i][3]),offsetX,offsetY,2);

            JLabel portImage = createLabel("", offsetX, offsetY, 1);
            portImage.setIcon(new ImageIcon(getResourceImage(board.getVertices()[portImageLocations[i][0]].getPort()).getScaledInstance(64,64,0)));
        }
    }

    private void putPlayerLabel(){
        //Places the label for this player
        JLabel thisPlayerLabel = createLabel("",50,550,1);
        thisPlayerLabel.setIcon(new ImageIcon(getConstructionImage(player.getID(),2).getScaledInstance(256,256,0)));
    }

    private void putTradeDirectionButton(){
        tradeDirectionButton = createButton(50,480);
        tradeDirectionButton.setIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/misc/Give.png").getScaledInstance(standardObjectSize/2,standardObjectSize/2,0)));
        tradeDirectionButton.setRolloverEnabled(false);
        tradeDirectionButton.addActionListener(tradeDirectionButtonClickedAction());

        enableButton(tradeDirectionButton);
    }

    private void putPlayerResourceLabels(){
        //Places the resources to the side of the player label
        int currentXOffset = 140;
        int xOffsetIncrement = 80;
        int yOffset = 550;
        for(Resource resource : Resource.values()){
            if(resource != Resource.MISC){
                JLabel resourceLabel = createLabel("",currentXOffset,yOffset,2);
                resourceLabel.setIcon(new ImageIcon(getResourceImage(resource).getScaledInstance(64,64,0)));

                JLabel resourceBackgroundLabel = createLabel("",currentXOffset,yOffset,3);
                resourceBackgroundLabel.setIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/resources/BGResource.png").getScaledInstance(64,64,0)));

                //Puts the resource count label for the resource
                JTextField resourceCountLabel = createText("0",currentXOffset + xOffsetIncrement - 24,yOffset + 50,1);
                resourceTextMap.put(resource,resourceCountLabel);

                //Puts the resource trade label for the resource
                JTextField resourceTradeLabel = createText("0",currentXOffset + xOffsetIncrement - 24,yOffset - 50,1);
                tradeTextMap.put(resource,resourceTradeLabel);

                //Creates a button for the resource
                JButton resourceButton = createButton(currentXOffset,yOffset);
                resourceButton.addActionListener(resourceButtonClickedAction(resource));
                resourceButtonMap.put(resourceButton,resource);
                invResourceButtonMap.put(resource,resourceButton);

                currentXOffset += xOffsetIncrement;
            }
        }
    }

    private void putPlayerDevelopmentLabels(){
        int currentXOffset = 840;
        int xOffsetIncrement = 80;
        int yOffset = 550;
        for(DevelopmentCard development : DevelopmentCard.values()){
            JLabel developmentLabel = createLabel("",currentXOffset,yOffset,2);
            developmentLabel.setIcon(new ImageIcon(getDevelopmentCardImage(development).getScaledInstance(64,64,0)));

            JLabel developmentBackgroundLabel = createLabel("",currentXOffset,yOffset,3);
            developmentBackgroundLabel.setIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/resources/BGDevelopment.png").getScaledInstance(64,64,0)));

            JTextField developmentCountLabel = createText("0",currentXOffset + xOffsetIncrement - 24,yOffset + 50,1);
            developmentTextMap.put(development,developmentCountLabel);

            currentXOffset += xOffsetIncrement;
        }
    }

    private void putPlayedKnightLabel(){
        int xOffset = 650;
        int yOffset = 575;

        JLabel knightLabel = createLabel("",xOffset,yOffset,2);
        knightLabel.setIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/resources/PlayedKnight.png").getScaledInstance(64,64,0)));

        JLabel knightBackgroundLabel = createLabel("",xOffset,yOffset,3);
        knightBackgroundLabel.setIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/resources/BGDevelopment.png").getScaledInstance(64,64,0)));

        playedKnightsCountLabel = createText("0",xOffset + 56, yOffset + 50, 1);
    }

    private void putAchievementLabels(){
        int xOffset = 570;
        int yOffset = 575;
        int xDifference = 160;

        JLabel longestRoadBackgroundLabel = createLabel("",xOffset,yOffset,2);
        longestRoadBackgroundLabel.setIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/resources/BGAchievement.png").getScaledInstance(64,64,0)));

        JLabel largestArmyBackgroundLabel = createLabel("",xOffset + xDifference,yOffset,2);
        largestArmyBackgroundLabel.setIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/resources/BGAchievement.png").getScaledInstance(64,64,0)));

        longestRoadLabel = createLabel("",xOffset,yOffset,1);
        largestArmyLabel = createLabel("",xOffset + xDifference,yOffset,1);
    }

    private void putOtherPlayerLabels(){
        //Places the labels for other players
        int currentYOffset = 240;
        int currentXOffset = 90;
        int yOffsetIncrement = 60;
        int xOffsetIncrement = 90;
        int textXOffsetIncrement = 100;
        for(Player plr : players){
            if(plr.getID() != player.getID()) {
                HashMap<String,JTextField> playerTextBoxes = new HashMap<>();

                //Places the player labels for other players
                JLabel playerLabel = createLabel("", 35, currentYOffset, 1);
                playerLabel.setIcon(new ImageIcon(getConstructionImage(plr.getID(),2).getScaledInstance(128, 128, 0)));

                //Places the player selection label for other players
                JLabel playerSelectionLabel = createLabel("", 35, currentYOffset, 1);
                playerSelectionLabel.setIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/misc/PlayerSelect.png").getScaledInstance(128, 128, 0)));
                playerSelectionLabel.setVisible(false);
                playerSelectionLabelMap.put(plr,playerSelectionLabel);

                //Places the resource labels for other players
                JLabel playerResourceLabel = createLabel("",currentXOffset,currentYOffset,1);
                playerResourceLabel.setIcon(new ImageIcon(getResourceImage(Resource.MISC).getScaledInstance(56,56,0)));

                JLabel playerResourceBackgroundLabel = createLabel("",currentXOffset,currentYOffset,3);
                playerResourceBackgroundLabel.setIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/resources/BGResource.png").getScaledInstance(56,56,0)));

                JTextField playerResourceText = createText("0",currentXOffset + textXOffsetIncrement,currentYOffset,1);
                playerTextBoxes.put("Resource",playerResourceText);

                //Places the development card labels for other players
                JLabel playerDevelopmentLabel = createLabel("",currentXOffset + xOffsetIncrement,currentYOffset,1);
                playerDevelopmentLabel.setIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/resources/DMisc.png").getScaledInstance(56,56,0)));

                JLabel playerDevelopmentBackgroundLabel = createLabel("",currentXOffset + xOffsetIncrement,currentYOffset,3);
                playerDevelopmentBackgroundLabel.setIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/resources/BGDevelopment.png").getScaledInstance(56,56,0)));

                JTextField playerDevelopmentText = createText("0",currentXOffset + xOffsetIncrement + textXOffsetIncrement,currentYOffset,1);
                playerTextBoxes.put("Development",playerDevelopmentText);

                //Places the played knight labels for other players
                JLabel playerKnightLabel = createLabel("",currentXOffset + (2 * xOffsetIncrement),currentYOffset,1);
                playerKnightLabel.setIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/resources/PlayedKnight.png").getScaledInstance(56,56,0)));

                JLabel playerKnightBackgroundLabel = createLabel("",currentXOffset + (2 * xOffsetIncrement),currentYOffset,3);
                playerKnightBackgroundLabel.setIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/resources/BGDevelopment.png").getScaledInstance(56,56,0)));

                JTextField playerKnightText = createText("0",currentXOffset + (2 * xOffsetIncrement) + textXOffsetIncrement,currentYOffset,1);
                playerTextBoxes.put("Knight",playerKnightText);

                //Places a button for the player
                JButton playerButton = createButton(35,currentYOffset);
                playerButton.addActionListener(playerButtonClickedAction(plr));
                playerButtonMap.put(playerButton,plr);

                //Stores the textboxes for future use
                playerTextMap.put(plr,playerTextBoxes);
                currentYOffset += yOffsetIncrement;
            }
        }
    }

    private void putDieCounterAssets(){
        //Creates die counter
        dieCounter = createLabel("",64,0,1);
        dieCounter.setSize(dieCounterSize,dieCounterSize);

        //Creates die counter outline
        dieCounterOutline = createLabel("",64,0,2);
        dieCounterOutline.setSize(dieCounterSize,dieCounterSize);
    }

    /**
     * Puts all non-board related elements onto the GUI
     */
    private void putOtherElements(){
         putPlayerLabel();
         putTradeDirectionButton();
         putPlayerResourceLabels();
         putPlayerDevelopmentLabels();
         putPlayedKnightLabel();
         putAchievementLabels();
         putOtherPlayerLabels();
         putDieCounterAssets();
    }

    /**
     * @param resource
     * @return an image of @resource
     */
    private Image getResourceImage(Resource resource){
        String basePath = "src/main/java/settlers/gui/textures/resources/";

        switch (resource){
            case WOOD:
                return getImage(basePath + "Wood.Png");
            case BRICK:
                return getImage(basePath + "Brick.Png");
            case SHEEP:
                return getImage(basePath + "Sheep.Png");
            case WHEAT:
                return getImage(basePath + "Wheat.Png");
            case ORE:
                return getImage(basePath + "Ore.Png");
            default:
                return getImage(basePath + "Misc.Png");
        }
    }

    private Image getDevelopmentCardImage(DevelopmentCard developmentCard){
        String basePath = "src/main/java/settlers/gui/textures/resources/";

        switch (developmentCard){
            case KNIGHT:
                return getImage(basePath + "Knight.png");
            case YEAR_OF_PLENTY:
                return getImage(basePath + "YearOfPlenty.png");
            case ROAD_BUILDING:
                return getImage(basePath + "RoadBuilding.png");
            case MONOPOLY:
                return getImage(basePath + "Monopoly.png");
            case VICTORY_POINT:
                return getImage(basePath + "VictoryPoint.png");
            default:
                return getImage(basePath + "DMisc.png");
        }
    }


    /**
     * @param id player's id
     * @return the player's color
     */
    private String getPlayerColor(int id){
        switch (id){
            case 0:
                return "Red";
            case 1:
                return "Blue";
            case 2:
                return "White";
            default:
                return "Orange";
        }
    }

    /**
     * @param id construction type's id
     * @return the construction type in string form
     */
    private String getConstructionType(int id){
        switch (id){
            case 0:
                return "Road";
            case 1:
                return "Settlement";
            default:
                return "City";
        }
    }

    /**
     * @param plrId player's id
     * @param constructionId construction's id
     * @return the specified player's construction of specified type
     */
    private Image getConstructionImage(int plrId, int constructionId, String roadDirection){
        String basePath = "src/main/java/settlers/gui/textures/construction/";
        String suffix = ".png";

        if(constructionId == 0){
            return getImage(basePath + "Road" + getPlayerColor(plrId) + roadDirection + suffix);
        }else{
            return getImage(basePath + getConstructionType(constructionId) + getPlayerColor(plrId) + suffix);
        }
    }

    private Image getLongestRoadImage(int plrId){
        String basePath = "src/main/java/settlers/gui/textures/resources/";
        String suffix = ".png";

        return getImage(basePath + "LongestRoad" + getPlayerColor(plrId) + suffix);
    }

    private Image getLargestArmyImage(int plrId){
        String basePath = "src/main/java/settlers/gui/textures/resources/";
        String suffix = ".png";

        return getImage(basePath + "LargestArmy" + getPlayerColor(plrId) + suffix);
    }

    private Image getConstructionImage(int plrId, int constructionId){
        return getConstructionImage(plrId,constructionId,null);
    }

    /**
     * Updates the die counter. Disables the die counter outline
     * @param roll the number which die counter should display
     */
    @Override
    public void updateDieCounter(int roll){
        dieCounter.setIcon(new ImageIcon(getNumberImage(roll).getScaledInstance(dieCounterSize,dieCounterSize,0)));
        dieCounterOutline.setIcon(new ImageIcon(getImage("")));
    }

    /**
     * Updates resource counters of all players
     */
    @Override
    public void updateResourceCounters(){
        //Sets this player's resource count
        for(Resource resource : resourceTextMap.keySet()){
            resourceTextMap.get(resource).setText("" + player.getResources().get(resource));
        }

        for(Player plr : players){
            if(plr != player){
                playerTextMap.get(plr).get("Resource").setText("" + plr.getCardNumber());
            }
        }
    }

    /**
     * Updates plr's development card counters
     */
    @Override
    public void updateDevelopmentCounters(Player plr){
        if(plr.equals(player)){
            for(DevelopmentCard development : DevelopmentCard.values()){
                int quantityOfDevelopmentCard = player.getDevelopmentCards().get(development) == null ? 0 : player.getDevelopmentCards().get(development);
                developmentTextMap.get(development).setText("" + quantityOfDevelopmentCard);
            }
        }else{
            playerTextMap.get(plr).get("Development").setText("" + plr.getDevelopmentCardCount());
        }
    }

    /**
     * Updates plr's played knights counter
     */
    @Override
    public void updateKnightCounters(Player plr){
        if(plr.equals(player)){
            playedKnightsCountLabel.setText("" + plr.getKnightNumber());
        }else{
            playerTextMap.get(plr).get("Knight").setText("" + plr.getKnightNumber());
        }
    }

    @Override
    public void updateLongestRoad(Player plr){
        longestRoadLabel.setIcon(new ImageIcon(getLongestRoadImage(plr.getID()).getScaledInstance(standardObjectSize/2,standardObjectSize/2,0)));
    }

    @Override
    public void updateLargestArmy(Player plr){
        largestArmyLabel.setIcon(new ImageIcon(getLargestArmyImage(plr.getID()).getScaledInstance(standardObjectSize/2,standardObjectSize/2,0)));
    }

    /**
     * Enables the die counter outline
     * @param roll the number whose color the die counter outline should display
     */
    private void enableDieCounterOutline(int roll){
        dieCounterOutline.setIcon(new ImageIcon(getNumberOutlineImage(roll).getScaledInstance(dieCounterSize,dieCounterSize,0)));
    }

    public void startMainPhase(){
        mainPhase = true;
    }

    /**
     * Starts Player's turn
     */
    @Override
    public void startTurn(int roll){
        enableDieCounterOutline(roll);
        focusFrame();

        if(roll == 7){
            startTurnOn7();
        }else {
            startTurnMainPhase();
        }
    }

    /**
     * Used to initiate a turn when the dice rolled 7
     */
    private void startTurnOn7(){
        thisPlayerHasTurn = true;
        stealPreformed = false;

        currentState = GUIState.THIEF;
        startThiefMove();
    }

    /**
     * Used to initiate a turn when the dice didn't roll 7 or after the thief was moved on 7
     */
    private void startTurnMainPhase(){
        thisPlayerHasTurn = true;
        reloadPossibleMovesGUI();
    }

    /**
     * Starts a turn in the settlement phase
     * @param availableSpots all available spots
     * @return the spot where the player placed his settlement
     */
    public void startSettlementTurn(Set<Vertex> availableSpots){
        lastSettlementSpot = null;
        lastRoadSpot = null;
        thisPlayerHasTurn = true;

        //Requests to place a settlement in settlement phase
        requestSettlementPlacementSP(availableSpots);
    }

    @Override
    public Vertex getLastSettlementSpot(){
        return lastSettlementSpot;
    }

    /**
     * Forces player to discard  until @target
     * @param target the amount the player must discard until
     */
    public void discardUntil(int target){
        focusFrame();
        targetResourceAmount = target;
        currentState = GUIState.DISCARD;

        //Enables specific resource buttons
        for(Resource resource : Resource.values()){
            if(resource != Resource.MISC && player.getResources().get(resource) > 0){
                enableButton(invResourceButtonMap.get(resource));
            }
        }
    }

    /**
     * Maps an action to a key
     * @param keyId the ASCII identifier of the key
     * @param action the action to be performed whenever the key is pressed
     */
    private void mapAction(Move move, int keyId, Action action, MoveDescription description){
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke((char) keyId),description);
        frame.getRootPane().getActionMap().put(description,action);
        createMoveText(move);
        moveDescriptionMap.put(move,description);
    }

    /**
     * Maps all of the frame's actions
     */
    private void mapActions(){
        Map<GUIState,String> cancelDescriptionMap = new HashMap<>();
        cancelDescriptionMap.put(GUIState.PLAYER_TRADE_REQUEST,"Decline Trade Request");

        Map<GUIState,String> confirmDescriptionMap = new HashMap<>();
        confirmDescriptionMap.put(GUIState.PLAYER_TRADE_REQUEST,"Accept Trade Request");
        confirmDescriptionMap.put(GUIState.PLAYER_TRADE,"Confirm Trade");

        mapAction(Move.CANCEL,8,cancelMove(),new MoveDescription("Backspace","Cancel Move",cancelDescriptionMap));
        mapAction(Move.PASS,32,passTurn(),new MoveDescription("Space","Pass the turn",null));
        mapAction(Move.CONFIRM,10,confirmAction(),new MoveDescription("Enter","Confirm Action",confirmDescriptionMap));
        mapAction(Move.ROAD,49,requestRoadPlacement(),new MoveDescription("1","Build a road",null));
        mapAction(Move.SETTLEMENT,50,requestSettlementPlacement(),new MoveDescription("2","Build a settlement",null));
        mapAction(Move.CITY,51,requestCityPlacement(),new MoveDescription("3","Build a city",null));
        mapAction(Move.DEVELOPMENT_CARD,52,buildDevelopmentCard(),new MoveDescription("4","Buy a development card",null));
        mapAction(Move.KNIGHT,113,requestKnight(),new MoveDescription("Q","Play knight",null));
        mapAction(Move.YEAR_OF_PLENTY,119,requestYearOfPlenty(),new MoveDescription("W","Play year of plenty",null));
        mapAction(Move.ROAD_BUILDING,101,requestRoadBuilding(),new MoveDescription("E","Play road building",null));
        mapAction(Move.MONOPOLY,114,requestMonopoly(),new MoveDescription("R","Play monopoly",null));
        mapAction(Move.TRADE_BANK,97,requestBankTrade(),new MoveDescription("A","Trade with the bank",null));
        mapAction(Move.TRADE_PLAYER,115,requestPlayerTrade(),new MoveDescription("S","Trade with players",null));
    }

    /**
     * @param toCheck checks this list for @move
     * @param move the move being checked for
     * @return true if toCheck is null or if it contains @move, false if neither condition is met
     */
    private boolean checkMoveListForMove(Set<Move> toCheck, Move move){
        if(toCheck == null || toCheck.contains(move))return true;
        return false;
    }

    /**
     * Gets all possible moves assuming they are on toCheck list. A null toCheck list would count as a complete toCheck list
     * @param toCheck
     * @return all possible moves which are in the toCheck list
     */
    private Set<Move> getPossibleMoves(Set<Move> toCheck){
        Set<Move> moves = new HashSet<>();

            //Asks if the player can pass
            if (checkMoveListForMove(toCheck, Move.PASS) && mainPhase && thisPlayerHasTurn && currentState.isCancelable()) {
                moves.add(Move.PASS);
            }

            //Asks if the player can cancel a move
            if (checkMoveListForMove(toCheck, Move.CANCEL) && mainPhase && currentState.isCancelable() && currentState != GUIState.NONE) {
                moves.add(Move.CANCEL);
            }

            //Asks if the player can confirm a move
            if(checkMoveListForMove(toCheck, Move.CONFIRM) && (currentState == GUIState.PLAYER_TRADE || currentState == GUIState.PLAYER_TRADE_REQUEST)){
                moves.add(Move.CONFIRM);
            }

            //Asks if the player can build a road
            if (checkMoveListForMove(toCheck, Move.ROAD) && main.canBuildRoad(player) && canPerformActions()) {
                moves.add(Move.ROAD);
            }

            //Asks if the player can build a settlement
            if (checkMoveListForMove(toCheck, Move.SETTLEMENT) && main.canBuildSettlement(player) && canPerformActions()) {
                moves.add(Move.SETTLEMENT);
            }

            //Asks if the player can build a city
            if (checkMoveListForMove(toCheck, Move.CITY) && main.canBuildCity(player) && canPerformActions()) {
                moves.add(Move.CITY);
            }

            //Asks if the player can build a development card
            if (checkMoveListForMove(toCheck, Move.DEVELOPMENT_CARD) && main.canBuyDevelopmentCard(player) && canPerformActions()) {
                moves.add(Move.DEVELOPMENT_CARD);
            }

            //Asks if the player can play knight
            if (checkMoveListForMove(toCheck, Move.KNIGHT) && main.canPlayDevelopmentCard(player, DevelopmentCard.KNIGHT) && canPerformActions()) {
                moves.add(Move.KNIGHT);
            }

            //Asks if the player can play year of plenty
            if (checkMoveListForMove(toCheck, Move.YEAR_OF_PLENTY) && main.canPlayDevelopmentCard(player, DevelopmentCard.YEAR_OF_PLENTY) && canPerformActions()) {
                moves.add(Move.YEAR_OF_PLENTY);
            }

            //Asks if the player can play road building
            if (checkMoveListForMove(toCheck, Move.ROAD_BUILDING) && main.canPlayDevelopmentCard(player, DevelopmentCard.ROAD_BUILDING) && canPerformActions()) {
                moves.add(Move.ROAD_BUILDING);
            }

            //Asks if the player can play monopoly
            if (checkMoveListForMove(toCheck, Move.MONOPOLY) && main.canPlayDevelopmentCard(player, DevelopmentCard.MONOPOLY) && canPerformActions()) {
                moves.add(Move.MONOPOLY);
            }

            if(checkMoveListForMove(toCheck, Move.TRADE_BANK) && getPossibleTradingResources().size() != 0 && canPerformActions()){
                moves.add(Move.TRADE_BANK);
            }

            if(checkMoveListForMove(toCheck, Move.TRADE_PLAYER) && player.getCardNumber() > 0 && canPerformActions()){
                moves.add(Move.TRADE_PLAYER);
            }

        return moves;
    }


    /**
     * Tells GUIPlayers to make a city
     * @param vertex location of the city
     * @param player controller of the city
     */
    public void setCity(Player player, Vertex vertex){
        JLabel label = vertexLabelMap.get(vertex);
        label.setIcon(new ImageIcon(getConstructionImage(player.getID(),2).getScaledInstance(standardObjectSize,standardObjectSize,0)));
    }

    /**
     * Tells GUIPlayers to make a settlement
     * @param vertex location of the settlement
     * @param player controller of the settlement
     */
    public void setSettlement(Player player ,Vertex vertex){
        JLabel label = vertexLabelMap.get(vertex);
        label.setIcon(new ImageIcon(getConstructionImage(player.getID(),1).getScaledInstance(standardObjectSize,standardObjectSize,0)));
    }

    /**
     * Tells GUIPlayers to make a road
     * @param edge location of the road
     * @param player controller of the road
     */
    public void setRoad(Player player, Edge edge){
        JLabel label = edgeLabelMap.get(edge);
        label.setIcon(new ImageIcon(getConstructionImage(player.getID(),0,edgeDirectionMap.get(edge)).getScaledInstance(standardObjectSize,standardObjectSize,0)));
    }

    /**
     * Moves the thief image
     * @param hex the new location of the thief image
     */
    public void moveThiefImage(Hex hex){
        for(JButton button : hexButtonMap.keySet()){
            if(hexButtonMap.get(button).equals(hex)){
                thiefImage.setBounds(button.getX() - standardObjectSize / 4,button.getY() - standardObjectSize / 4,standardObjectSize,standardObjectSize);
            }
        }
    }

    /**
     * When main requests GUIPlayer to place a settlement, show all the buttons which would allow him to do so
     */
    private void requestSettlementPlacementSP(Set<Vertex> availableSpots){
        enableSpecifiedButtons(vertexButtonMap.keySet(),vertexButtonMap,availableSpots);
        currentState = GUIState.SETTLEMENT;
    }

    private void requestRoadPlacementSP(){
        for(Edge edge : lastSettlementSpot.getEdges()){
            for(JButton button : edgeButtonMap.keySet()){
                if(edgeButtonMap.get(button).equals(edge)){
                    enableButton(button);
                }
            }
        }
        currentState = GUIState.ROAD;
    }

    private HashSet<Vertex> getOccupiedAdjacentVertices(Hex hex, boolean selfSearch){
        HashSet<Vertex> result = new HashSet<>();

        for(Vertex vertex : hex.getVertices()){
            if(vertex.getPlayer() != null && (vertex.getPlayer() != player || selfSearch)){
                result.add(vertex);
            }
        }

        return result;
    }

    /**
     * Sets weather when we click a button in a player to player trade we are giving or taking a resource
     * @param giving are we giving a resource?
     */
    private void setTradeDirection(boolean giving){
        if(giving){
            tradeDirectionButton.setIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/misc/Give.png").getScaledInstance(standardObjectSize/2,standardObjectSize/2,0)));
            tradeDirectionGiving = true;
        }else{
            tradeDirectionButton.setIcon(new ImageIcon(getImage("src/main/java/settlers/gui/textures/misc/Take.png").getScaledInstance(standardObjectSize/2,standardObjectSize/2,0)));
            tradeDirectionGiving = false;
        }
    }

    private void enableButton(JButton button){
        button.setEnabled(true);
        button.setVisible(true);
    }

    private void disableButton(JButton button){
        button.setEnabled(false);
        button.setVisible(false);
    }

    /**
     * Dnables all buttons within @buttons
     * @param buttons the set of buttons
     */
    private void enableButtons(Set<JButton> buttons){
        for(JButton button : buttons){
            enableButton(button);
        }
    }

    /**
     * Enables some buttons from buttons
     * @param buttons the set of buttons to enable buttons from
     * @param mappedButtons a map where all buttons on buttons are stored
     * @param toEnable a set of items which the buttons must be mapped to in mapped buttons
     *                 in order to be enabled
     */
    private void enableSpecifiedButtons(Set<JButton> buttons,HashMap mappedButtons, Set toEnable){
        for(JButton button : buttons){
            if(toEnable.contains(mappedButtons.get(button))){
                enableButton(button);
            }
        }
    }

    /**
     * Disables all buttons within @buttons
     * @param buttons the set of buttons
     */
    private void disableButtons(Set<JButton> buttons){
        for(JButton button : buttons){
            disableButton(button);
        }
    }

    private void disableAllButtons(){
        disableButtons(hexButtonMap.keySet());
        disableButtons(vertexButtonMap.keySet());
        disableButtons(edgeButtonMap.keySet());
        disableButtons(resourceButtonMap.keySet());
        disableButtons(playerButtonMap.keySet());
    }

    private void focusFrame(){
        frame.requestFocusInWindow();
    }

    private void finishThiefMove(Vertex location){
        if(currentState == GUIState.KNIGHT) {
            main.playKnight(player, location, thiefRequestSpot);
        }else if(currentState == GUIState.THIEF){
            main.moveThief(player, location, thiefRequestSpot);
            startTurnMainPhase();
        }
        stealPreformed = true;
        currentState = GUIState.NONE;
    }

    private void startThiefMove(){
        Set<Hex> availableThiefSpots = main.getAvailableThiefSpots();
        enableSpecifiedButtons(hexButtonMap.keySet(),hexButtonMap,availableThiefSpots);
    }

    /**
     * Gets all the resources the player can use to trade
     * @return a list of all the resources the player can use to trade
     */
    private Set<Resource> getPossibleTradingResources(){
        Set<Resource> possibleTradingResources = new HashSet<>();

        for(Resource resource : Resource.values()){
            if(main.canTrade(player,resource))possibleTradingResources.add(resource);
        }

        return possibleTradingResources;
    }

    /**
     * Updates all possible moves
     * @param toCheck the moves you should update. If null, will check all moves for updates.
     */
    private void updatePossibleMoves(Set<Move> toCheck){
        Set currentlyPossibleMoves = getPossibleMoves(toCheck);

        Move[] movesToCheck = toCheck == null ? Move.values() : (Move[]) toCheck.toArray();

        for(Move move : movesToCheck){
            if(currentlyPossibleMoves.contains(move)){
                possibleMoves.put(move,true);
            }else{
                possibleMoves.put(move,false);
            }
        }
    }

    private void reloadPossibleMovesGUI(Set<Move> movesWhichMayHaveChanged){
        updatePossibleMoves(movesWhichMayHaveChanged);

        int currentXOffset = 1040;
        int currentYOffset = 0;
        int yIncrement = 32;

        for(Move move : Move.values()){
            if(possibleMoves.containsKey(move) && possibleMoves.get(move)){
                JTextField field = moveTextMap.get(move);
                field.setText(moveDescriptionMap.get(move).key + " - " + moveDescriptionMap.get(move).getDescription(currentState));
                field.setVisible(true);
                field.setBounds(currentXOffset,currentYOffset,384,128);
                currentYOffset += yIncrement;
            }else{
                JTextField field = moveTextMap.get(move);
                field.setVisible(false);
            }
        }
    }

    /**
     * Enables all trading GUI elements
     * @param initiatingTrade is a trade being initiated?
     */
    private void enablePlayerTradingGUIElements(boolean initiatingTrade){
        if(initiatingTrade){
            playerTradeRequest = new HashMap<>();
            playerTradeRequestReceivers = new HashSet<>();

            for(Resource resource : Resource.values()) {
                if (resource != Resource.MISC) {
                    playerTradeRequest.put(resource, 0);
                }
            }

            enableButton(tradeDirectionButton);

            for(Player plr : players){
                if(plr != player){
                    playerTradeRequestReceivers.add(plr);
                    playerSelectionLabelMap.get(plr).setVisible(true);
                }
            }

            enableButtons(playerButtonMap.keySet());
        }


        for(Resource resource : Resource.values()){
            if(resource != Resource.MISC) {
                JTextField resourceTradeTextField = tradeTextMap.get(resource);

                resourceTradeTextField.setText("0");
                resourceTradeTextField.setVisible(true);
                resourceTradeTextField.setFont(defaultFontBlue);
            }
        }
    }

    /**
     * Disables all trading GUI elements
     */
    private void disablePlayerTradingGUIElements(){
        for(Resource resource : Resource.values()){
            if(resource != Resource.MISC) {
                tradeTextMap.get(resource).setVisible(false);
            }
        }

        for(Player plr : playerSelectionLabelMap.keySet()){
            playerSelectionLabelMap.get(plr).setVisible(false);
        }

        disableButton(tradeDirectionButton);
    }

    public void receiveTradeRequest(Player sender, Map<Resource,Integer> resourcesExchanged){
        enablePlayerTradingGUIElements(false);
        currentState = GUIState.PLAYER_TRADE_REQUEST;
        reloadPossibleMovesGUI();

        for(Resource resource : Resource.values()) {
            if(resource != Resource.MISC) {
                JTextField resourceText = tradeTextMap.get(resource);

                int resourceQuantity = resourcesExchanged.get(resource);
                resourceText.setText("" + Math.abs(resourceQuantity));

                if(resourceQuantity < 0){
                    resourceText.setFont(defaultFontGreen);
                }else if(resourceQuantity > 0){
                    resourceText.setFont(defaultFontRed);
                }else{
                    resourceText.setFont(defaultFontBlue);
                }
            }
        }
    }

    public void tradeRequestResponseReceived(Player responder){
        currentState = GUIState.NONE;
        reloadPossibleMovesGUI();
        disablePlayerTradingGUIElements();
    }

    private boolean canPerformActions(){return mainPhase && thisPlayerHasTurn && currentState == GUIState.NONE;}

    private void reloadPossibleMovesGUI(){
        reloadPossibleMovesGUI(null);
    }

    //Actions by keybinds
    /**
     * Passes the turn
     * @return
     */
    private Action passTurn(){
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(mainPhase && thisPlayerHasTurn &&currentState.isCancelable()){
                    disableAllButtons();
                    currentState = GUIState.NONE;
                    thisPlayerHasTurn = false;
                    reloadPossibleMovesGUI();
                    main.pass();
                }
            }
        };
    }

    /**
     * Cancels your move
     * @return
     */
    private Action cancelMove(){
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //Some GUIStates have extra actions which must be taken upon canceling moves
                if(currentState == GUIState.PLAYER_TRADE){
                    disablePlayerTradingGUIElements();
                }else if(currentState == GUIState.PLAYER_TRADE_REQUEST){
                    disablePlayerTradingGUIElements();
                    main.playerDeclinedTrade(player);
                }

                //Cancels the move
                if(mainPhase && currentState.isCancelable()){
                    disableAllButtons();
                    currentState = GUIState.NONE;
                    reloadPossibleMovesGUI();
                }
            }
        };
    }

    /**
     * Confirms an action
     * @return
     */
    private Action confirmAction(){
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    if(currentState == GUIState.PLAYER_TRADE){
                        disableAllButtons();
                        disablePlayerTradingGUIElements();

                        currentState = GUIState.PLAYER_TRADE_PENDING;
                        reloadPossibleMovesGUI();

                        main.trade(player,playerTradeRequest,playerTradeRequestReceivers);
                    }else if(currentState == GUIState.PLAYER_TRADE_REQUEST){
                        disablePlayerTradingGUIElements();
                        main.playerAcceptedTrade(player);
                        currentState = GUIState.NONE;
                        reloadPossibleMovesGUI();
                    }
                }
        };
    }

    /**
     * Proscesses settlement building requests
     * @return
     */
    private Action requestCityPlacement() {
        return new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("City placement requested by: " + player.getID() + ", GUIState is: " + currentState + ", turn: " + thisPlayerHasTurn + ", main phase: " + mainPhase + ", can build: " + main.canBuildCity(player));
                //Checks if player can preform action
                if(canPerformActions() && main.canBuildCity(player)){
                    currentState = GUIState.CITY;
                    Set<Vertex> availableSpots = main.getAvailableCitySpots(player);
                    enableSpecifiedButtons(vertexButtonMap.keySet(),vertexButtonMap,availableSpots);

                    //Reloads the possible moves
                    reloadPossibleMovesGUI();
                }
            }
        };
    }

    /**
     * Proscesses settlement building requests
     * @return
     */
    private Action requestSettlementPlacement() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Settlement placement requested by: " + player.getID() + ", GUIState is: " + currentState + ", turn: " + thisPlayerHasTurn + ", main phase: " + mainPhase + ", can build: " + main.canBuildSettlement(player));
                //Checks if player can preform action
                if(canPerformActions() && main.canBuildSettlement(player)){
                    currentState = GUIState.SETTLEMENT;
                    Set<Vertex> availableSpots = main.getAvailableSettlementSpots(player);
                    enableSpecifiedButtons(vertexButtonMap.keySet(),vertexButtonMap,availableSpots);

                    //Reloads the possible moves
                    reloadPossibleMovesGUI();
                }
            }
        };
    }

    /**
     * Proscesses road building requests
     * @return
     */
    private Action requestRoadPlacement() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Road placement requested by: " + player.getID() + ", GUIState is: " + currentState + ", turn: " + thisPlayerHasTurn + ", main phase: " + mainPhase + ", can build: " + main.canBuildRoad(player));
                //Checks if player can preform action
                if(canPerformActions() && main.canBuildRoad(player)){
                    currentState = GUIState.ROAD;
                    Set<Edge> availableSpots = main.getAvailableRoadSpots(player);
                    enableSpecifiedButtons(edgeButtonMap.keySet(),edgeButtonMap,availableSpots);

                    //Reloads the possible moves
                    reloadPossibleMovesGUI();
                }
            }
        };
    }

    private Action buildDevelopmentCard() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(canPerformActions() && main.canBuyDevelopmentCard(player)){
                    main.buildDevelopmentCard(player);

                    //Reloads the possible moves
                    reloadPossibleMovesGUI();
                }
            }
        };
    }

    private Action requestKnight() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(canPerformActions() && main.canPlayDevelopmentCard(player,DevelopmentCard.KNIGHT)){
                    currentState = GUIState.KNIGHT;
                    startThiefMove();

                    //Reloads the possible moves
                    reloadPossibleMovesGUI();
                }
            }
        };
    }

    private Action requestYearOfPlenty() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(canPerformActions() && main.canPlayDevelopmentCard(player,DevelopmentCard.YEAR_OF_PLENTY)){
                    currentState = GUIState.YEAR_OF_PLENTY_FIRST;
                    enableButtons(resourceButtonMap.keySet());

                    //Reloads the possible moves
                    reloadPossibleMovesGUI();
                }
            }
        };
    }

    private Action requestRoadBuilding() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(canPerformActions() && main.canPlayDevelopmentCard(player,DevelopmentCard.ROAD_BUILDING)){
                    currentState = GUIState.ROAD_BUILDING_FIRST;
                    Set<Edge> availableSpots = main.getAvailableRoadSpots(player);
                    enableSpecifiedButtons(edgeButtonMap.keySet(),edgeButtonMap,availableSpots);

                    //Reloads the possible moves
                    reloadPossibleMovesGUI();
                }
            }
        };
    }

    private Action requestMonopoly() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(canPerformActions() && main.canPlayDevelopmentCard(player,DevelopmentCard.MONOPOLY)){
                    currentState = GUIState.MONOPOLY;
                    enableButtons(resourceButtonMap.keySet());

                    //Reloads the possible moves
                    reloadPossibleMovesGUI();
                }
            }
        };
    }

    private Action requestBankTrade(){
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(canPerformActions()){
                    Set<Resource> possibleTradingResources = getPossibleTradingResources();

                    if(possibleTradingResources.size() > 0){
                        for(Resource resource : Resource.values()){

                            if(possibleTradingResources.contains(resource)){
                                enableButton(invResourceButtonMap.get(resource));
                            }
                        }

                        currentState = GUIState.BANK_TRADE_PUT;
                        reloadPossibleMovesGUI();
                    }
                }
            }
        };
    }

    private Action requestPlayerTrade(){
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(canPerformActions()){
                    currentState = GUIState.PLAYER_TRADE;
                    enableButtons(resourceButtonMap.keySet());
                    setTradeDirection(false);

                    enablePlayerTradingGUIElements(true);

                    reloadPossibleMovesGUI();
                }
            }
        };
    }



    //Buttons

    /**
     * Whenever a hex button is clicked
     * @param hex the hex corresponding to the button that was clicked
     * @return
     */
    private ActionListener hexButtonClickedAction(Hex hex){

        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentState == GUIState.THIEF || currentState == GUIState.KNIGHT){
                    thiefRequestSpot = hex;
                    HashSet<Vertex> potentialRobberySpots = getOccupiedAdjacentVertices(hex,false);

                    //Does the stealing mechanic
                    if(potentialRobberySpots.size() > 0){
                        enableSpecifiedButtons(vertexButtonMap.keySet(),vertexButtonMap,potentialRobberySpots);
                    }else{
                        finishThiefMove(hex.getVertices()[0]);
                    }
                }

                disableButtons(hexButtonMap.keySet());
                focusFrame();
                reloadPossibleMovesGUI();
            }
        };
    }

    /**
     * Whenever a vertex button is clicked
     * @param vertex the vertex corresponding to the button that was clicked
     * @return
     */
    private ActionListener vertexButtonClickedAction(Vertex vertex){

        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentState == GUIState.SETTLEMENT) {
                    main.buildSettlement(player,vertex);
                    lastSettlementSpot = vertex;
                    currentState = GUIState.NONE;

                    if(!mainPhase){
                        requestRoadPlacementSP();
                    }
                }else if(currentState == GUIState.CITY){
                    main.buildCity(player,vertex);
                    currentState = GUIState.NONE;
                }else if(currentState == GUIState.THIEF || currentState == GUIState.KNIGHT){
                    finishThiefMove(vertex);
                }
                disableButtons(vertexButtonMap.keySet());
                focusFrame();
                reloadPossibleMovesGUI();
            }
        };
    }

    /**
     * Whenever an edge button is clicked
     * @param edge the edge corresponding to the button that was clicked
     * @return
     */
    private ActionListener edgeButtonClickedAction(Edge edge){

        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentState == GUIState.ROAD){
                    lastRoadSpot = edge;
                    currentState = GUIState.NONE;
                    disableButtons(edgeButtonMap.keySet());

                    //If the road is not placed in the main phase, it is a turn ending move
                    if(!mainPhase){
                        thisPlayerHasTurn = false;
                    }

                    //It is important that this is executed after currentState is set to NONE, because this will end a settlement turn
                    main.buildRoad(player,edge);
                }else if(currentState == GUIState.ROAD_BUILDING_FIRST){
                    roadBuildingRequest[0] = edge;
                    disableButtons(edgeButtonMap.keySet());

                    Set<Edge> availableSpots = main.getAvailableRoadSpotsGivenEdge(player,edge);
                    enableSpecifiedButtons(edgeButtonMap.keySet(),edgeButtonMap,availableSpots);
                    currentState = GUIState.ROAD_BUILDING_SECOND;
                }else if(currentState == GUIState.ROAD_BUILDING_SECOND){
                    roadBuildingRequest[1] = edge;
                    disableButtons(edgeButtonMap.keySet());

                    main.playRoadBuilding(player,roadBuildingRequest[0],roadBuildingRequest[1]);
                    currentState = GUIState.NONE;
                }
                focusFrame();
                reloadPossibleMovesGUI();
            }
        };
    }

    /**
     * Whenever a resource button is clicked
     * @param resource the resource corresponding to the button that was clicked
     * @return
     */
    private ActionListener resourceButtonClickedAction(Resource resource){
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentState == GUIState.DISCARD){
                    HashMap<Resource,Integer> toRemove = new HashMap<>();
                    toRemove.put(resource,1);

                    main.playerDiscardedCard(player,resource);

                    if(player.getCardNumber() <= targetResourceAmount){
                        currentState = GUIState.NONE;
                        disableButtons(resourceButtonMap.keySet());
                    }else if(player.getResources().get(resource) == 0){
                        disableButton(invResourceButtonMap.get(resource));
                    }
                }else if(currentState == GUIState.YEAR_OF_PLENTY_FIRST) {
                    currentState = GUIState.YEAR_OF_PLENTY_SECOND;
                    yearOfPlentyRequest[0] = resource;

                    reloadPossibleMovesGUI();
                }else if(currentState == GUIState.YEAR_OF_PLENTY_SECOND){
                    currentState = GUIState.NONE;
                    yearOfPlentyRequest[1] = resource;
                    main.playYearOfPlenty(player, yearOfPlentyRequest[0], yearOfPlentyRequest[1]);

                    disableButtons(resourceButtonMap.keySet());
                    reloadPossibleMovesGUI();
                }else if(currentState == GUIState.MONOPOLY){
                    currentState = GUIState.NONE;
                    main.playMonopoly(player,resource);

                    disableButtons(resourceButtonMap.keySet());
                    reloadPossibleMovesGUI();
                }else if(currentState == GUIState.BANK_TRADE_PUT){
                    currentState = GUIState.BANK_TRADE_TAKE;
                    bankTradeRequest[0] = resource;

                    disableButtons(resourceButtonMap.keySet());
                    //Enables every resource button except the resource button corresponding to the button clicked
                    for(JButton button : resourceButtonMap.keySet()){
                        if(resourceButtonMap.get(button) != resource){
                            enableButton(button);
                        }
                    }

                    reloadPossibleMovesGUI();
                }else if(currentState == GUIState.BANK_TRADE_TAKE){
                    currentState = GUIState.NONE;
                    bankTradeRequest[1] = resource;

                    main.trade(player,bankTradeRequest[0],bankTradeRequest[1]);
                    disableButtons(resourceButtonMap.keySet());
                    reloadPossibleMovesGUI();
                }else if(currentState == GUIState.PLAYER_TRADE){
                    if(tradeDirectionGiving && -playerTradeRequest.get(resource) < player.getResources().get(resource)) {
                        playerTradeRequest.put(resource,playerTradeRequest.get(resource) - 1);
                    }else if(!tradeDirectionGiving){
                        playerTradeRequest.put(resource,playerTradeRequest.get(resource) + 1);
                    }

                    JTextField resourceText = tradeTextMap.get(resource);
                    int resourceQuantity = playerTradeRequest.get(resource);

                    resourceText.setText("" + Math.abs(resourceQuantity));

                    if(resourceQuantity < 0){
                        resourceText.setFont(defaultFontRed);
                    }else if(resourceQuantity > 0){
                        resourceText.setFont(defaultFontGreen);
                    }else{
                        resourceText.setFont(defaultFontBlue);
                    }
                }
                focusFrame();
            }
        };
    }

    private ActionListener playerButtonClickedAction(Player plr){
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentState == GUIState.PLAYER_TRADE){
                    if(playerTradeRequestReceivers.contains(plr)){
                        playerTradeRequestReceivers.remove(plr);
                        playerSelectionLabelMap.get(plr).setVisible(false);
                    }else{
                        playerTradeRequestReceivers.add(plr);
                        playerSelectionLabelMap.get(plr).setVisible(true);
                    }
                }
            }
        };
    }

    /**
     * Whenever the trade direction button is clicked
     * @return
     */
    private ActionListener tradeDirectionButtonClickedAction(){
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tradeDirectionGiving){
                    setTradeDirection(false);
                }else{
                    setTradeDirection(true);
                }
                focusFrame();
            }
        };
    }
}


enum GUIState{
    NONE(true),
    ROAD(true),
    SETTLEMENT(true),
    CITY(true),
    KNIGHT(true),
    THIEF(false),
    DISCARD(false),
    ROAD_BUILDING_FIRST(true),
    ROAD_BUILDING_SECOND(true),
    YEAR_OF_PLENTY_FIRST(true),
    YEAR_OF_PLENTY_SECOND(true),
    MONOPOLY(true),
    BANK_TRADE_PUT(true),
    BANK_TRADE_TAKE(true),
    PLAYER_TRADE(true),
    PLAYER_TRADE_REQUEST(true),
    PLAYER_TRADE_PENDING(false);

    GUIState(boolean cancelable){
        this.cancelable = cancelable;
    }
    private final boolean cancelable;

    boolean isCancelable(){
        return this.cancelable;
    }
}

enum Move{
    PASS,
    CANCEL,
    CONFIRM,
    ROAD,
    SETTLEMENT,
    CITY,
    DEVELOPMENT_CARD,
    KNIGHT,
    YEAR_OF_PLENTY,
    ROAD_BUILDING,
    MONOPOLY,
    TRADE_BANK,
    TRADE_PLAYER;
}

class MoveDescription{
    Map<GUIState,String> descriptionMap;
    String defaultDescription;
    String key;

    MoveDescription(String key, String defaultDescription, Map<GUIState,String> descriptionMap){
        this.key = key;
        this.defaultDescription = defaultDescription;
        this.descriptionMap = descriptionMap;
    }

    String getDescription(GUIState state){
        if(descriptionMap == null || !descriptionMap.containsKey(state)){
            return defaultDescription;
        }else{
            return descriptionMap.get(state);
        }
    }

    String getKey(){return key;}
}