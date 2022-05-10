package settlers;

import settlers.board.*;
import settlers.card.DevelopmentCard;
import settlers.card.Resource;
import settlers.gui.GUIMain;
import settlers.gui.GUIMainDummyImpl;
import settlers.gui.GUIMainImpl;

import java.io.InputStream;
import java.util.*;

public class MainImpl implements Main {

    private Board board;
    private Player[] players;
    private GUIMain gui;
    private boolean isMainPhase; // starts automatically as false
    private Hex thiefIsHere; // so we don't have to look for it
    private List<Integer> turnOrder; // where the players are ordered by their array number in the turn order

    public MainImpl(int numberOfPlayers) {
        this(numberOfPlayers, new GUIMainDummyImpl());
        gui = new GUIMainImpl(this);
    }

    /**
     * A version of the constructor to be used for testing, giving whatever testGUI you want
     * Always gives 4 players
     */
    protected MainImpl(int numberOfPlayers, GUIMain testGUI) {
        players = new Player[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            players[i] = new PlayerImpl(i);
        }
        turnOrder = turnOrder(); // creating the turn order
        board = new BoardImpl();
        // now we find the thief, the only time we need to do it this way
        for (Hex hex : board.getHexes()) {
            if (hex.getNumber() == 1) {
                thiefIsHere = hex;
                break;
            }
        }
        // a dummy testGUI that doesn't actually show a board
        gui = testGUI;
    }

    /**
     * Creates a turn order for the game
     * @return an array containing the player array numbers in a random order
     */
    protected List<Integer> turnOrder() {
        Random generator = new Random();
        List<Integer> turnOrder = new ArrayList<>(players.length);
        List<Integer> numberSource = new ArrayList<>(Arrays.asList(0, 1, 2, 3)); // later numbers won't be used if not applicable
        for (int i = players.length; i > 0; i--) {
            // we add a number chosen randomly from the numberSource
            // as we remove numbers from the number source, it shrinks in line with i
            // the generator gets a random index from what the List has left
            turnOrder.add(numberSource.remove(generator.nextInt(i)));
        }
        return turnOrder;
    }

    /*
    Here is where I will write the main() method
    First, the method needs to set up the board and the players
    Then, it needs to set the order of the turns
    Then, it needs to go through the turns once, offering each player a choice of settlement and road
    Then, it needs to do the same thing, but in reverse order. This time, each player also gets up to 3
        resources per settlement they take
    Then, we start the actual loop
    This is the loop:
        We start by rolling the dice, telling the player what the roll was, and giving each player
            the resources from that roll
        If the player rolled a 7, all players with more than 7 cards must choose half to discard
        We then let this player move the thief, and they get a random card from a player of
            their choice at the new location
        Then, the player has a choice of building (as many times as they want) or passing, since we are not
            yet adding trading or development cards
        When the player passes, the next player rolls the dice
        When all players go, the loop restarts
        If any player ever gets 10 victory points, the game ends, and that player is declared the winner
            That is checked whenever the player does something that could get a victory point
     */
    public static void main(String[] args) {
        // because this is static, I have to create a separate variable for this
        // should also set up board, players, and GUI
        MainImpl main = new MainImpl(getNumOfPlayers(System.in));
        // now it is time for the setup loop
        // in a separate method, to more easily access private and protected methods
        main.setupLoop();
        // now we play the actual game
        main.mainLoop();
    }

    /**
     * Gets the number of players for the next game
     * This method is called during the Main creation process, so it has to be static
     * It is protected so I can test it from outside
     * @param stream where the input is coming from. Should be System.in, but for testing, I will use a file
     * @return the number of players the user wants
     */
    protected static int getNumOfPlayers(InputStream stream) {
        System.out.println("How many players are playing this game of Settlers of Catan? 2 to 4 are supported");
        Scanner playerNumInput = new Scanner(stream);
        int numPlayers = 0;
        while (numPlayers < 2 || numPlayers > 4) {
            if (!playerNumInput.hasNextInt()) {
                System.out.println("That's not an int, I need a number from 2 to 4");
                playerNumInput.nextLine();
                continue; // don't check the numPlayers, won't have changed
            }
            numPlayers = playerNumInput.nextInt();
            if (numPlayers == 1) {
                System.out.println("Sorry, silly, you can't play Catan by yourself. I need a bigger number");
            } else if (numPlayers < 1){
                System.out.println("I'm sorry, but " + numPlayers + " is not a valid input. Give me a number from 2 to 4!");
            } else if (numPlayers > 6) {
                System.out.println("I don't care if you want " + numPlayers + " people at your birthday party, Catan only supports 2 to 4 players");
            } else if (numPlayers > 4) {
                System.out.println("5-6 player expansion is not supported, and must be purchased/programmed separately. Give me a number from 2 to 4");
            }
            // now we repeat to get better data
        }
        System.out.println("Loading a game with " + numPlayers);
        return numPlayers;
    }

    /**
     * Does the main gameplay loop
     */
    private void setupLoop() {
        // we loop through the players twice, once forward and once backwards
        // for info on each turn, see that method
        // after both loops, we change the phase to main phase
        for (int turnNumber : turnOrder) { // the forward loop
            setupTurn(turnNumber, false);
        }
        for (int i = players.length - 1; i >= 0; i--) { // the backwards loop
            setupTurn(turnOrder.get(i), true);
        }
        isMainPhase = true; // now it is main phase
    }

    /**
     * Goes through a setup turn
     * @param turnNumber index of the player whose turn it is
     * CRITICAL: This method is not complete. I need Aryeh to answer some questions before I can do so
     * Completing this method will just require uncommenting some lines and changing some method names
     */
    private void setupTurn(int turnNumber, boolean isSecondLoop) {
        Player player = players[turnNumber];
        // we ask gui where the player wants to put the settlement
        // gui will then call buildSettlement on the appropriate spot to actually build the settlement
        // It will also find out what road to build, and build that too
        Vertex settlement = gui.startSetupTurn(player, getAvailableSettlementSpots(player));
        // now the player does its setup turn over in guiland
        if (isSecondLoop) {
            givePlayerSettlementResources(player, settlement);
        }
    }

    /**
     * Gives a player the resources that can be gotten from his settlement
     * @param player who is getting the resources
     * @param settlement that was built second, which the player gets resources from
     */
    private void givePlayerSettlementResources(Player player, Vertex settlement) {
        // this is inefficient, but I only have to do it once per player
        for (Hex hex : board.getHexes()) { // look at each hex's vertices
            if (hex.getResource() != Resource.MISC) {
                for (Vertex vertex : hex.getVertices()) {
                    if (vertex == settlement) { // if they are the same vertex
                        player.addResource(hex.getResource()); // give the player that hex's resource
                    }
                }
            }
        }
    }

    /**
     * Goes through the main loop until the game ends
     */
    private void mainLoop() {
        //We start by rolling the dice, telling the player what the roll was, and giving each player
            //the resources from that roll
        while (true) { // goes until I call break when the player ends the game
            for (int turnNumber : turnOrder) {
                mainTurn(turnNumber); // rolls the dice, and gives players resources
                // if a 7 is rolled, it deals with players discarding and robbing
                // it also calls the method that gives control over to the gui to actually do the turn
                // I don't need to call about victory points, the player already knows about it
            }
        }
    }

    private void mainTurn(int turnNumber) {
        Player player = players[turnNumber]; // the player whose turn it is
        Random dieRoller = new Random();
        // gets the value of the dice being rolled
        // since nextInt(6) gives 0 to 5, I then add 1 per die to make each die 1 to 6
        int dieValue = 2 + dieRoller.nextInt(6) + dieRoller.nextInt(6);
        // gui.insertMethodNameHere(dieValue); // displays the die value
        applyDice(dieValue); // changes values and stuff
        gui.startTurn(player, dieValue); // tells GUI to get input and call the methods, when they end,
            // this method will end, and it will be the next player's turn
            // updates number and resources and also does 7 stuff, so I don't worry about it
    }

    /**
     * Uses the result of the die roll to give resources
     * No longer deals with 7, that is now done by GUI
     * This is protected and a separate method so I can test it
     * @param dieValue  a number from 2 to 12
     */
    protected void applyDice(int dieValue) {
        if (dieValue != 7) {
            for (Hex hex : board.getHexes()) {
                if (hex.getNumber() == dieValue && !hex.hasThief()) {
                    // if this hex has the right number and not the thief, we give its players the right resource
                    for (Vertex vertex : hex.getVertices()) {
                        if (vertex.getPlayer() != null) { // if this vertex has a player, give him a resource
                            vertex.getPlayer().addResource(hex.getResource());
                            if (vertex.isCity()) {
                                // if this is a city, give the player a second resource
                                vertex.getPlayer().addResource(hex.getResource());
                            }
                        }
                    }
                }
            }
        }
        // if we get a 7, no resources are given, and instead gui will deal with the thief and the like later
    }

    /**
     * @return this game's board
     */
    @Override
    public Board getBoard() {
        return board; // already unmodifiable, yay
    }

    /**
     * @return a list of the players in this game
     */
    @Override
    public List<Player> getPlayers() {
        return Arrays.asList(players);
    }


    /**
     * Returns whether or not the player has enough resources to build the project
     * And also whether or not the player has reached the maximum number of that project
     * The maximum numbers are 15 roads, 5 settlements, and 4 cities
     * @param player  that wants to build
     * @param project that the player wants to build
     * @return true if the player has enough resources, false otherwise
     */
    @Override
    public boolean playerElementsFor(Player player, Building project) {
        Map<Resource, Integer> requirements = project.getResources();
        for (Resource resource : requirements.keySet()) {
            if (player.getResources().getOrDefault(resource, 0) < requirements.get(resource)) {
                // if the player doesn't have enough of that resource
                return false;
            }
        }
        // this next part makes sure that if
        int projectNumber;
        switch (project) {
            case ROAD:
                projectNumber = player.getRoads().size();
                break;
            case SETTLEMENT:
                projectNumber = player.getSettlements().size();
                break;
            case CITY:
                projectNumber = player.getCities().size();
                break;
            default:
                projectNumber = 0; // so there will be no problems
        }
        return projectNumber < project.getMax();
    }

    /**
     * @return spots where the thief can be moved to
     */
    @Override
    public Set<Hex> getAvailableThiefSpots() {
        Set<Hex> hexes = new HashSet<>(Arrays.asList(board.getHexes()));
        hexes.remove(thiefIsHere);
        return hexes;
    }

    /**
     * Gets the locations where this player can build a settlement
     *
     * @param player building the settlement
     * @return a Set of Vertices where this player could build
     */
    @Override
    public Set<Vertex> getAvailableSettlementSpots(Player player) {
        // I check the game phase, then call the appropriate method
        if (!isMainPhase) {
            return board.getOpenVertices(); // since all vertices not occupied or next to one occupied are
                // fair game here
        }
        return getSettleSpotsGame(player);
    }

    /**
     * Gets the settle spots during the game
     * @param player building a settlement
     * @return the available settlement spots for that player
     */
    private Set<Vertex> getSettleSpotsGame(Player player) {
        // First, I get a list of all open settle spots
        // I then traverse each of them
        // If it has a road of this player, I add it to a second list
        // I return the second list
        Set<Vertex> openSpots = board.getOpenVertices();
        Set<Vertex> settleSpots = new HashSet<>();
        for (Vertex spot : openSpots) {
            for (Edge edge : spot.getEdges()) {
                if (edge != null && player.equals(edge.getPlayer())) {
                    settleSpots.add(spot);
                }
            }
        }
        return settleSpots;
    }

    /**
     * Gets the locations where this player can build a road
     *
     * @param player building the road
     * @return a Set of Edges where this player could build
     */
    @Override
    public Set<Edge> getAvailableRoadSpots(Player player) {
        // we will start with an empty set
        // then, we will go through all the vertices
        // if one belongs to this player, add the other two
        // Then at the end, I will take all the viable roads, and retainAll
        // This is frankly too inefficient, but I am not bothering to find something better
        Set<Edge> roadSpots = new HashSet<>();
        for (Vertex vertex : board.getVertices()) {
            for (Edge edge : vertex.getEdges()) {
                if (edge != null && player.equals(edge.getPlayer())) {
                    roadSpots.addAll(Arrays.asList(vertex.getEdges()));
                }
            }
        }
        roadSpots.retainAll(board.getEmptyEdges());
        roadSpots.remove(null);
        return roadSpots;
    }

    /**
     * Gets the settlements that this player could upgrade into cities
     *
     * @param player building the city
     * @return a Set of Vertices where this player could build
     */
    @Override
    public Set<Vertex> getAvailableCitySpots(Player player) {
        return player.getSettlements();
    }

    /**
     * Moves the thief and steals a resource
     *
     * @param stealer    player who is stealing
     * @param settlement that is being robbed, can be an empty vertex if no one is being stolen from
     * @param location   that is being robbed, resources can't be gotten there until the thief is moved
     */
    @Override
    public void moveThief(Player stealer, Vertex settlement, Hex location) {
        // moves the robber
        thiefIsHere.setThief(false);
        thiefIsHere = location;
        location.setThief(true);
        if (settlement.getPlayer() == null) {
            return; // because there is no robbing being done
        }
        Player victim = settlement.getPlayer();
        List<Resource> victimResources = getVictimResources(victim);
        if (victimResources.isEmpty()) {
            return; // since the victim has nothing for the stealer to rob
        }
        Random resourceChooser = new Random();
        Resource robbedResource = victimResources.get(resourceChooser.nextInt(victimResources.size())); // chooses the random to be removed
        Map<Resource, Integer> removedMap = new HashMap<>(); // this is to use the syntax to remove from the victim
        removedMap.put(robbedResource, 1);
        victim.removeResources(removedMap); // removes one random resource
        stealer.addResource(robbedResource); // gives that resource to the thief
    }

    /**
     * @param player who is being robbed
     * @return the player's resources in list form
     */
    private List<Resource> getVictimResources(Player player) {
        List<Resource> resources = new ArrayList<>();
        for (Resource resource : player.getResources().keySet()) {
            for (int i = 0; i < player.getResources().get(resource); i++) {
                resources.add(resource);
            }
        }
        return resources;
    }

    /**
     * Builds a settlement, and updates the Player and Vertex accordingly
     *
     * @param player   who is building the settlement
     * @param location where the player builds the settlement
     * This and the following methods do not throw exceptions to aid with testing
     */
    @Override
    public void buildSettlement(Player player, Vertex location) {
        // I need to change the vertex's status
        location.setPlayer(player);
        // I need to add the vertex to the player's list
        boolean isWinner = player.addSettlement(location); // true if the player now has 10 victory points
        // I need to remove the vertex from the board's open vertex list
        // I need to remove the adjacent vertices from the board's open vertex list
        board.removeSettlement(location);
        // I need to add the port to the player's list, if applicable
        if (location.getPort() != null) {
            player.addPort(location.getPort());
        }
        // I need to remove resources from the player, if this is the right phase
        if (isMainPhase) {
            player.removeResources(Building.SETTLEMENT.getResources());
        }
        // we don't need to tell the GUI to update anything, because it already knows
        if (isWinner) { // if the player has won, end the game
            endGame(player);
        }
    }

    /**
     * Builds a road, and updates the Edge accordingly
     *
     * @param player   building the road
     * @param location where the road is being built
     */
    @Override
    public void buildRoad(Player player, Edge location) {
        // I need to change the edge's status
        location.setPlayer(player);
        // I need to add the edge to the player's list
        player.addRoad(location);
        // I need to remove the edge from the board's empty edge list
        board.removeRoad(location);
        // I need to remove resources from the player, if this is the right phase
        if (isMainPhase) {
            player.removeResources(Building.ROAD.getResources());
        }
        // no need to report to GUI that the action is done, it already knows
    }

    /**
     * Upgrades a settlement to a city, and updates the Vertex and Player accordingly
     *
     * @param player   who is building the city
     * @param location that the player is upgrading from a settlement to a city
     */
    @Override
    public void buildCity(Player player, Vertex location) {
        // I need to change the vertex's status
        location.makeCity();
        // I need to change which list the vertex is on
        boolean isWinner = player.upgradeSettlement(location);
        // I need to remove resources from the player, if this is the right phase
        // the phase doesn't really matter here, since you can't build a city during setup phase anyway
            // but I might want it for testing, so I will leave it in
        if (isMainPhase) {
            player.removeResources(Building.CITY.getResources());
        }
        // no need to report to GUI, it will know from the method ending
        if (isWinner) { // if the player has won, end the game
            endGame(player);
        }
    }

    /**
     * Gives the player a development card from the deck, and updates the Player and deck accordingly
     *
     * @param player buying the development card
     */
    @Override
    public void buildDevelopmentCard(Player player) {

    }

    /**
     * Makes the player use a development card
     *
     * @param player      playing the card
     * @param development card being played
     * @throws IllegalArgumentException if the development card is of type VICTORY_POINT
     * @throws IllegalStateException    if the player does not have that development card
     *                                  I may decide to make the effects of the card decided by the enum directly
     */
    @Override
    public void playDevelopmentCard(Player player, DevelopmentCard development) {

    }

    /**
     * Checks if this trade with the bank would work
     *
     * @param player         considering the trade
     * @param resourceGiven  resource type that would be given
     * @param resourceNumber number of resources that would be given
     * @return true if the player can make this trade, false if the player lacks the port or resources
     */
    @Override
    public boolean canTrade(Player player, Resource resourceGiven, int resourceNumber) {
        return false;
    }

    /**
     * This simulates a trade with the bank, updating the Player appropriately
     *
     * @param player         doing the trade
     * @param resourceGiven  resource type being given
     * @param resourceNumber number of that resource being given
     * @param resourceGotten resource type being received
     */
    @Override
    public void trade(Player player, Resource resourceGiven, int resourceNumber, Resource resourceGotten) {

    }

    /**
     * This is used only for testing purposes, so I can change the stage from MainTest
     * @param changingToMain whether or not we are changing to main
     */
    protected void setPhase(boolean changingToMain) {
        isMainPhase = changingToMain;
    }

    private void endGame(Player victor) {
        String color;
        switch (victor.getID()) {
            case 0:
                color = "RED";
                break;
            case 1:
                color = "BLUE";
                break;
            case 2:
                color = "WHITE";
                break;
            case 3:
                color = "ORANGE";
                break;
            default:
                color = "BLACK - wait, what?";
        }
        System.out.println("Congratulations, " + color + ", you have won the game! Woo!!!");
        System.exit(0);
    }

}
