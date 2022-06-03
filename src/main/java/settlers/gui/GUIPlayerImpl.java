package settlers.gui;

import settlers.Player;
import settlers.PlayerImpl;
import settlers.board.*;
import settlers.card.Resource;

import javax.swing.*;
import javax.swing.plaf.basic.BasicListUI;
import javax.swing.plaf.basic.BasicMenuUI;
import java.awt.*;
import java.awt.event.*;
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
    private HashMap<JButton, Resource> resourceButtonMap = new HashMap<>();
    private HashMap<JButton, Player> playerButtonMap = new HashMap<>();

    //Label maps
    private HashMap<Vertex,JLabel> vertexLabelMap = new HashMap<>();
    private HashMap<Edge,JLabel> edgeLabelMap = new HashMap<>();
    private HashMap<Resource,JTextField> resourceTextMap = new HashMap<>();
    private HashMap<Player,JTextField> playerTextMap = new HashMap<>();
    private HashMap<Move,JTextField> moveTextMap = new HashMap<>();

    //Other maps
    private HashMap<Edge, String> edgeDirectionMap = new HashMap<>();

    //Render Ordering Maps
    private HashMap<Component,Integer> paintLayerMap = new HashMap<>();

    //Possible Moves Map
    private HashMap<Move,Boolean> possibleMoves = new HashMap();

    //Frame and image
    private JFrame frame;
    private final Toolkit toolkit = Toolkit.getDefaultToolkit();

    //Thief and die
    private JLabel thiefImage;
    private JLabel dieCounter;
    private JLabel dieCounterOutline;

    //Functional
    private boolean thisPlayerHasTurn = false;
    private boolean canPass = false;
    private boolean mainPhase = false;
    private boolean stealPreformed = false;
    private int targetResourceAmount = 0;
    private Vertex lastSettlementSpot = null;
    private Edge lastRoadSpot = null;
    private Hex thiefRequestSpot = null;
    private GUISTate currentState = GUISTate.NONE;


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

        //Adds the thief
        thiefImage = createLabel("src/main/java/settlers/gui/textures/hexes/Thief.png",0,0,1);

        //Adds the hexes
        putHexes();

        //Adds the vertices
        putVerticesAndRoads();

        //Adds the ports
        putPorts();

        //Adds the possible move text fields
        putPossibleMoveFields();

        //Adds other elements
        putOtherElements();

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
        field.setFont(new Font(Font.DIALOG,Font.BOLD,24));

        //Sets the frame's Z order
        paintLayerMap.put(field,zOrder);

        return field;
    }


    private JTextField createMoveText(Move move, String keybind, String description){
        JTextField field = createText(keybind + " - " + description, 1152, 0, 2);
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
            hexButton.addActionListener(hexButtonClickedAction(hexButton));

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
        String portPath = basePath + "Port.png";

        int[][] portImageLocations = {{0,-92,44,3},{1,-92,220,4},{10,0,352,4},{11,92,-44,2},{26,184,440,5},{33,276,-44,2},{42,368,352,0},{47,460,44,1},{49,460,220,0}};

        for(int i = 0; i < portImageLocations.length; i++) {
            int offsetX = boardOffsetX + portImageLocations[i][1];
            int offsetY =  boardOffsetY + portImageLocations[i][2];

            JLabel port = createLabel(portPath, offsetX, offsetY, 2);
            JLabel portDirection = createLabel(getPortDirectionImage(portImageLocations[i][3]),offsetX,offsetY,0);

            JLabel portImage = createLabel("", offsetX, offsetY, 1);
            portImage.setIcon(new ImageIcon(getResourceImage(board.getVertices()[portImageLocations[i][0]].getPort()).getScaledInstance(64,64,0)));
        }
    }

    /**
     * Puts all possible move text fields onto the GUI
     */
    private void putPossibleMoveFields(){
        createMoveText(Move.PASS,"Space","Pass the turn");
        createMoveText(Move.ROAD,"1","Build a road");
        createMoveText(Move.SETTLEMENT,"2","Build a settlement");
        createMoveText(Move.CITY,"3","Build a city");
        createMoveText(Move.DEVELOPMENT_CARD,"4","Buy a development card");
        createMoveText(Move.CANCEL,"Backspace","Cancel move");
    }

    private void putPlayerLabel(){
        //Places the label for this player
        JLabel thisPlayerLabel = createLabel("",50,550,1);
        thisPlayerLabel.setIcon(new ImageIcon(getConstructionImage(player.getID(),2).getScaledInstance(256,256,0)));
    }

    private void putPlayerResourceLabels(){
        //Places the resources to the side of the player label
        int currentXOffset = 140;
        int xOffsetIncrement = 80;
        for(Resource resource : Resource.values()){
            if(resource != Resource.MISC){
                JLabel thisPlayerResourceLabel = createLabel("",currentXOffset,550,2);
                thisPlayerResourceLabel.setIcon(new ImageIcon(getResourceImage(resource).getScaledInstance(64,64,0)));
                currentXOffset += xOffsetIncrement;

                JTextField thisPlayerResourceCountLabel = createText("0",currentXOffset - 24,500,1);
                resourceTextMap.put(resource,thisPlayerResourceCountLabel);

                //Creates a button for the resource
                JButton thisPlayerResourceButton = createButton(currentXOffset - xOffsetIncrement,550);
                thisPlayerResourceButton.addActionListener(resourceButtonClickedAction(thisPlayerResourceButton));
                resourceButtonMap.put(thisPlayerResourceButton,resource);
            }
        }
    }

    private void putOtherPlayerLabels(){
        //Places the labels for other players
        int currentYOffset = 240;
        int yOffsetIncrement = 60;
        for(Player plr : players){
            if(plr.getID() != player.getID()) {
                //Places the player labels for other players
                JLabel playerLabel = createLabel("", 35, currentYOffset + 60, 1);
                playerLabel.setIcon(new ImageIcon(getConstructionImage(plr.getID(),2).getScaledInstance(128, 128, 0)));
                currentYOffset += yOffsetIncrement;

                //Places the resource labels for other players
                JLabel playerResourceLabel = createLabel("",80,currentYOffset,1);
                playerResourceLabel.setIcon(new ImageIcon(getResourceImage(Resource.MISC).getScaledInstance(56,56,0)));

                JTextField playerResourceText = createText("0",175,currentYOffset,1);
                playerTextMap.put(plr,playerResourceText);

                //Places a button for the player
                JButton playerButton = createButton(35,currentYOffset);
                playerButtonMap.put(playerButton,plr);
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
         putPlayerResourceLabels();
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

    private Image getConstructionImage(int plrId, int constructionId){
        return getConstructionImage(plrId,constructionId,null);
    }

    /**
     * Updates the die counter. Disables the die counter outline
     * @param roll the number which die counter should display
     */
    public void updateDieCounter(int roll){
        dieCounter.setIcon(new ImageIcon(getNumberImage(roll).getScaledInstance(dieCounterSize,dieCounterSize,0)));
        dieCounterOutline.setIcon(new ImageIcon(getImage("")));
    }

    /**
     * Updates resource counters of all players
     */
    public void updateResourceCounters(){
        //Sets this player's resource count
        for(Resource resource : resourceTextMap.keySet()){
            resourceTextMap.get(resource).setText("" + player.getResources().get(resource));
        }

        for(Player plr : players){
            if(plr != player){
                playerTextMap.get(plr).setText("" + plr.getCardNumber());
            }
        }
    }

    /**
     * Enables the die counter outline
     * @param roll the number whose color the die counter outline should display
     */
    private void enableDieCounterOutline(int roll){
        dieCounterOutline.setIcon(new ImageIcon(getNumberOutlineImage(roll).getScaledInstance(dieCounterSize,dieCounterSize,0)));
    }

    /**
     * Starts Player's turn
     */
    @Override
    public void startTurn(int roll){
        enableDieCounterOutline(roll);
        focusFrame();

        if(roll == 7){
            thisPlayerHasTurn = true;
            canPass = false;
            stealPreformed = false;

            currentState = GUISTate.THIEF;
            Set<Hex> availableThiefSpots = main.getAvailableThiefSpots();
            for(JButton button : hexButtonMap.keySet()){
                if(availableThiefSpots.contains(hexButtonMap.get(button))){
                    enableButton(button);
                }
            }

            while(!stealPreformed){
                try {
                    Thread.sleep(1);
                }catch (InterruptedException e){
                    throw new IllegalStateException("InterruptedException was thrown. Exception: " + e);
                }
            }
        }

        thisPlayerHasTurn = true;
        canPass = true;
        mainPhase = true;

        reloadPossibleMovesGUI();

        while(thisPlayerHasTurn){
            try {
                Thread.sleep(1);
            }catch (InterruptedException e){
                throw new IllegalStateException("InterruptedException was thrown. Exception: " + e);
            }
        }
    }

    public Vertex startSettlementTurn(Set<Vertex> availableSpots){
        lastSettlementSpot = null;
        lastRoadSpot = null;
        thisPlayerHasTurn = true;
        canPass = false;

        //Requests to place a settlement in settlement phase
        requestSettlementPlacementSP(availableSpots);

        while(lastSettlementSpot == null){
            try {
                Thread.sleep(1);
            }catch (InterruptedException e){
                throw new IllegalStateException("InterruptedException was thrown. Exception: " + e);
            }
        }

        //Requests to place a road neat to that settlement
        requestRoadPlacementSP();

        while(lastRoadSpot == null){
            try {
                Thread.sleep(1);
            }catch (InterruptedException e){
                throw new IllegalStateException("InterruptedException was thrown. Exception: " + e);
            }
        }

        thisPlayerHasTurn = false;

        return lastSettlementSpot;
    }

    /**
     * Forces player to discard  until @target
     * @param target the amount the player must discard until
     */
    public void discardUntil(int target){
        focusFrame();
        canPass = false;
        targetResourceAmount = target;
        currentState = GUISTate.DISCARD;
        enableButtons(resourceButtonMap.keySet());
    }

    /**
     * Maps an action to a key
     * @param keyId the ASCII identifier of the key
     * @param action the action to be performed whenever the key is pressed
     */
    private void mapAction(int keyId, ActionListener action){
        frame.getRootPane().registerKeyboardAction(action,KeyStroke.getKeyStroke((char) keyId),JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Maps all of the frame's actions
     */
    private void mapActions(){
        mapAction(8,cancelMove());
        mapAction(32,passTurn());
        mapAction(49,requestRoadPlacement());
        mapAction(50,requestSettlementPlacement());
        mapAction(51,requestCityPlacement());
        mapAction(52,buildDevelopmentCard());
        mapAction(113,requestKnight());
        mapAction(119,requestYearOfPlenty());
        mapAction(101,requestRoadBuilding());
        mapAction(114,requestMonopoly());
    }

    /**
     * @param vertex
     * @return the id of the player who owns two roads next to this vertex. Defaults to -1
     */
    private int getOwnerlessVertexColorId(Vertex vertex){
        HashSet<Integer> knownIds = new HashSet<>();
        for(Edge edge : vertex.getEdges()){
            if(edge.getPlayer() != null) {
                if(knownIds.contains(edge.getPlayer().getID())) return edge.getPlayer().getID();
                knownIds.add(edge.getPlayer().getID());
            }
        }
        return -1;
    }

    /**
     * Repaints all verticies
     */
    private void repaintVertices(){
        for(Vertex vertex: vertexLabelMap.keySet()){
            JLabel label = vertexLabelMap.get(vertex);

            int ownerid = -1;
            int constructionid = 0;

            Player owner = vertex.getPlayer();

            if(owner != null){
                ownerid = owner.getID();
                constructionid = vertex.isCity() ? 2 : 1;
                label.setIcon(new ImageIcon(getConstructionImage(ownerid,constructionid).getScaledInstance(standardObjectSize,standardObjectSize,0)));
            }else if(getOwnerlessVertexColorId(vertex) != -1){
                label.setIcon(new ImageIcon(getConstructionImage(getOwnerlessVertexColorId(vertex),constructionid).getScaledInstance(standardObjectSize,standardObjectSize,0)));
            }
        }

        frame.repaint();
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
        for(JButton button : vertexButtonMap.keySet()){
            if(availableSpots.contains(vertexButtonMap.get(button))){
                enableButton(button);
            }
        }
        currentState = GUISTate.SETTLEMENT;
    }

    private void requestRoadPlacementSP(){
        for(Edge edge : lastSettlementSpot.getEdges()){
            for(JButton button : edgeButtonMap.keySet()){
                if(edgeButtonMap.get(button).equals(edge)){
                    enableButton(button);
                }
            }
        }
        currentState = GUISTate.ROAD;
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
     * Gets the total resource count of a player
     * @param plr the player
     * @return the total resource count of plr
     */
    private int getPlayerResourceCount(Player plr){
        int total = 0;
        for(Resource resource : plr.getResources().keySet()){
            total += plr.getResources().get(resource);
        }
        return total;
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
        main.moveThief(player,location, thiefRequestSpot);
        stealPreformed = true;
        currentState = GUISTate.NONE;
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

        //Moves can only be preformed when it's your turn
        if(thisPlayerHasTurn) {
            //Asks if the player can pass
            if (checkMoveListForMove(toCheck, Move.PASS) && canPass) {
                moves.add(Move.PASS);
            }

            //Asks if the player can build a road
            if (checkMoveListForMove(toCheck, Move.ROAD) && main.canBuildRoad(player) && canMakePurchases()) {
                moves.add(Move.ROAD);
            }

            //Asks if the player can build a settlement
            if (checkMoveListForMove(toCheck, Move.SETTLEMENT) && main.canBuildSettlement(player) && canMakePurchases()) {
                moves.add(Move.SETTLEMENT);
            }

            //Asks if the player can build a city
            if (checkMoveListForMove(toCheck, Move.CITY) && main.canBuildCity(player) && canMakePurchases()) {
                moves.add(Move.CITY);
            }

            //Asks if the player can build a development card
            if (checkMoveListForMove(toCheck, Move.DEVELOPMENT_CARD) && main.canBuyDevelopmentCard(player) && canMakePurchases()) {
                moves.add(Move.DEVELOPMENT_CARD);
            }

            //Asks if the player can cancel a move
            if (checkMoveListForMove(toCheck, Move.CANCEL) && canPass && currentState.isCancelable()) {
                moves.add(Move.CANCEL);
            }
        }

        return moves;
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

        for(Move move : moveTextMap.keySet()){
            if(possibleMoves.get(move)){
                JTextField field = moveTextMap.get(move);
                field.setVisible(true);
                field.setBounds(currentXOffset,currentYOffset,384,128);
                currentYOffset += yIncrement;
            }else{
                JTextField field = moveTextMap.get(move);
                field.setVisible(false);
            }
        }
    }

    private boolean canMakePurchases(){return mainPhase && thisPlayerHasTurn && currentState == GUISTate.NONE;}

    private void reloadPossibleMovesGUI(){
        reloadPossibleMovesGUI(null);
    }

    //Actions

    /**
     * Passes the turn
     * @return
     */
    private ActionListener passTurn(){
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(canPass){
                    disableAllButtons();
                    currentState = GUISTate.NONE;
                    thisPlayerHasTurn = false;
                    reloadPossibleMovesGUI();
                }
            }
        };
    }

    /**
     * Cancels your move
     * @return
     */
    private ActionListener cancelMove(){
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(canPass && currentState.isCancelable()){
                    disableAllButtons();
                    currentState = GUISTate.NONE;
                    reloadPossibleMovesGUI();
                }
            }
        };
    }

    //Keybinds
    /**
     * Proscesses settlement building requests
     * @return
     */
    private ActionListener requestCityPlacement() {
        return new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("City placement requested by: " + player.getID() + ", GUIState is: " + currentState + ", turn: " + thisPlayerHasTurn + ", main phase: " + mainPhase + ", can build: " + main.canBuildCity(player));
                //Checks if player can preform action
                if(canMakePurchases() && main.canBuildCity(player)){
                    currentState = GUISTate.CITY;
                    Set<Vertex> availableSpots = main.getAvailableCitySpots(player);

                    //Makes the right buttons visible
                    for(JButton button : vertexButtonMap.keySet()){
                        if(availableSpots.contains(vertexButtonMap.get(button))){
                            enableButton(button);
                        }
                    }

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
    private ActionListener requestSettlementPlacement() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Settlement placement requested by: " + player.getID() + ", GUIState is: " + currentState + ", turn: " + thisPlayerHasTurn + ", main phase: " + mainPhase + ", can build: " + main.canBuildSettlement(player));
                //Checks if player can preform action
                if(canMakePurchases() && main.canBuildSettlement(player)){
                    currentState = GUISTate.SETTLEMENT;
                    Set<Vertex> availableSpots = main.getAvailableSettlementSpots(player);

                    //Makes the right buttons visible
                    for(JButton button : vertexButtonMap.keySet()){
                        if(availableSpots.contains(vertexButtonMap.get(button))){
                            enableButton(button);
                        }
                    }

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
    private ActionListener requestRoadPlacement() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Road placement requested by: " + player.getID() + ", GUIState is: " + currentState + ", turn: " + thisPlayerHasTurn + ", main phase: " + mainPhase + ", can build: " + main.canBuildRoad(player));
                //Checks if player can preform action
                if(canMakePurchases() && main.canBuildRoad(player)){
                    currentState = GUISTate.ROAD;
                    Set<Edge> availableSpots = main.getAvailableRoadSpots(player);

                    //Makes the right buttons visible
                    for(JButton button : edgeButtonMap.keySet()){
                        if(availableSpots.contains(edgeButtonMap.get(button))){
                            enableButton(button);
                        }
                    }

                    //Reloads the possible moves
                    reloadPossibleMovesGUI();
                }
            }
        };
    }

    private ActionListener buildDevelopmentCard() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(canMakePurchases() && main.canBuyDevelopmentCard(player)){
                    main.buildDevelopmentCard(player);
                }
            }
        };
    }

    private ActionListener requestKnight() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };
    }

    private ActionListener requestYearOfPlenty() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };
    }

    private ActionListener requestRoadBuilding() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };
    }

    private ActionListener requestMonopoly() {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };
    }
    //Buttons
    /**
     * Whenever a vertex button is clicked
     * @param vertex the vertex whose button was clicked
     * @return
     */
    private ActionListener vertexButtonClickedAction(Vertex vertex){
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentState == GUISTate.SETTLEMENT) {
                    main.buildSettlement(player,vertex);
                    lastSettlementSpot = vertex;
                    currentState = GUISTate.NONE;
                }else if(currentState == GUISTate.CITY){
                    main.buildCity(player,vertex);
                    currentState = GUISTate.NONE;
                }else if(currentState == GUISTate.THIEF){
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
     * @param edge the edge whose button was clicked
     * @return
     */
    private ActionListener edgeButtonClickedAction(Edge edge){
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentState == GUISTate.ROAD){
                    main.buildRoad(player,edge);
                    lastRoadSpot = edge;
                    currentState = GUISTate.NONE;
                }
                disableButtons(edgeButtonMap.keySet());
                focusFrame();
                reloadPossibleMovesGUI();
            }
        };
    }

    private ActionListener hexButtonClickedAction(JButton button){
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentState == GUISTate.THIEF){
                    Hex hex = hexButtonMap.get(button);
                    thiefRequestSpot = hex;
                    HashSet<Vertex> potentialRobberySpots = getOccupiedAdjacentVertices(hex,false);

                    //Does the stealing mechanic
                    if(potentialRobberySpots.size() > 0){
                        //Enables adjacent vertex buttons which have settlements/cities on them
                        for(JButton vertexButton : vertexButtonMap.keySet()){
                            if(potentialRobberySpots.contains(vertexButtonMap.get(vertexButton))){
                                enableButton(vertexButton);
                            }
                        }
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

    private ActionListener resourceButtonClickedAction(JButton button){
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean reEnableButtons = true;
                disableButtons(resourceButtonMap.keySet());

                Resource resource = resourceButtonMap.get(button);
                if(currentState == GUISTate.DISCARD){
                    HashMap<Resource,Integer> toRemove = new HashMap<>();
                    toRemove.put(resource,1);

                    player.removeResources(toRemove);

                    if(getPlayerResourceCount(player) <= targetResourceAmount){
                        main.playerHasTargetResources(player);
                        currentState = GUISTate.NONE;
                        reEnableButtons = false;
                    }

                }

                if(reEnableButtons)enableButtons(resourceButtonMap.keySet());
                focusFrame();
            }
        };
    }
}


enum GUISTate{
    NONE(false),
    ROAD(true),
    SETTLEMENT(true),
    CITY(true),
    THIEF(false),
    DISCARD(false),
    ROADBUILDING(false),
    YEAROFPLENTY(false),
    MONOPOLY(false);

    GUISTate(boolean cancelable){
        this.cancelable = cancelable;
    }
    private final boolean cancelable;

    boolean isCancelable(){
        return this.cancelable;
    }
}

enum Move{
    PASS,ROAD,SETTLEMENT,CITY,DEVELOPMENT_CARD,KNIGHT,YEAR_OF_PLENTY,ROAD_BUILDING,MONOPOLY,CANCEL
}