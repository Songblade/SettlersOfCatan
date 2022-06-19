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

    private final Board board;
    private final Player[] players;
    private GUIMain gui;

    private boolean isMainPhase; // starts automatically as false
    private Hex thiefIsHere; // so we don't have to look for it

    private final List<Integer> turnOrder; // where the players are ordered by their array number in the turn order
    private Player currentTurn; // a link to the player whose turn it currently is

    private Player largestArmyHolder; // a link to the player who has largest army
    private Player longestRoadHolder; // a link to the player who has longest road

    private final Queue<DevelopmentCard> vellyDeck; // where all the vellies are kept
    private final Map<DevelopmentCard, Integer> newCards; // this stores all the cards that the player just bought
        // this turn
    // the player cannot play any cards in this map
    // at the end of the turn, this is emptied
    // I made it a queue, because we only ever take from the top

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
        assert thiefIsHere != null;
        // a dummy testGUI that doesn't actually show a board
        gui = testGUI;
        // now we set up the vellyDeck
        vellyDeck = shuffleVellyDeck();
        newCards = new HashMap<>();
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

    private Queue<DevelopmentCard> shuffleVellyDeck() {
        // first I add all the cards to an unshuffled deck
        List<DevelopmentCard> unshuffledDeck = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            unshuffledDeck.add(DevelopmentCard.KNIGHT);
        }
        for (int i = 0; i < 5; i++) {
            unshuffledDeck.add(DevelopmentCard.VICTORY_POINT);
        }
        unshuffledDeck.add(DevelopmentCard.MONOPOLY);
        unshuffledDeck.add(DevelopmentCard.MONOPOLY);
        unshuffledDeck.add(DevelopmentCard.YEAR_OF_PLENTY);
        unshuffledDeck.add(DevelopmentCard.YEAR_OF_PLENTY);
        unshuffledDeck.add(DevelopmentCard.ROAD_BUILDING);
        unshuffledDeck.add(DevelopmentCard.ROAD_BUILDING);

        // now we shuffle the cards into a new deck in a random order
        Random random = new Random();
        Queue<DevelopmentCard> shuffledDeck = new LinkedList<>();
        for (int i = 25; i > 0; i--) { // should add all the cards in randomly
            shuffledDeck.add(unshuffledDeck.remove(random.nextInt(i)));
        }

        return shuffledDeck;
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
        currentTurn = players[turnNumber];
        // we ask gui where the player wants to put the settlement
        // gui will then call buildSettlement on the appropriate spot to actually build the settlement
        // It will also find out what road to build, and build that too
        Vertex settlement = gui.startSetupTurn(currentTurn, getAvailableSettlementSpots(currentTurn));
        // now the player does its setup turn over in guiland
        if (isSecondLoop) {
            givePlayerSettlementResources(currentTurn, settlement);
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
        currentTurn = players[turnNumber]; // the player whose turn it is
        Random dieRoller = new Random();
        // gets the value of the dice being rolled
        // since nextInt(6) gives 0 to 5, I then add 1 per die to make each die 1 to 6
        int dieValue = 2 + dieRoller.nextInt(6) + dieRoller.nextInt(6);
        // gui.insertMethodNameHere(dieValue); // displays the die value
        applyDice(dieValue); // changes values and stuff
        gui.startTurn(currentTurn, dieValue); // tells GUI to get input and call the methods, when they end,
            // this method will end, and it will be the next player's turn
            // updates number and resources and also does 7 stuff, so I don't worry about it
        newCards.clear(); // so that future turns will not have to deal with being unable to use the new development
            // cards that were purchased
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
     * I am going to refactor this method, call it canBuild, and make it check if the player has the resources
     * And if they have reached the maximum number they can build
     * And also check if they have anywhere to put it
     * And also if it is their turn
     * The maximum numbers are 15 roads, 5 settlements, and 4 cities
     * @param player  that wants to build
     * @param project that the player wants to build
     * @return true if the player has enough resources and it is that player's turn, false otherwise
     */
    @Override
    public boolean playerCanBuild(Player player, Building project) {
        if (currentTurn != player) { // if it is not this player's turn
            return false;
        }
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
                if (getAvailableRoadSpots(player).isEmpty()) {
                    return false;
                }
                break;
            case SETTLEMENT:
                projectNumber = player.getSettlements().size();
                if (getAvailableSettlementSpots(player).isEmpty()) {
                    return false;
                }
                break;
            case CITY:
                projectNumber = player.getCities().size();
                if (getAvailableCitySpots(player).isEmpty()) {
                    return false;
                }
                break;
            case DEVELOPMENT_CARD:
                projectNumber = 25 - vellyDeck.size();
                // I do 25 -, so that it will start with 0, much less than the max, but will reach the
                    // max of 25 when all vellies are purchased
                // since it isn't put down anywhere, we don't need to check that the board is good
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
     * Gets the locations where this player can build a road, given that the player is going to build
     * on this edge first. Used to find the second edge for playRoadBuilding
     *
     * @param player      building the road
     * @param roadToBuild where the player will build a road, but hasn't yet
     * @return a Set of Edges where this player could build once they build roadToBuild
     */
    @Override
    public Set<Edge> getAvailableRoadSpotsGivenEdge(Player player, Edge roadToBuild) {
        // first we take the normal list
        Set<Edge> roadSpots = new HashSet<>();
        // then we remove roadToBuild
        // then we add what is adjacent to it
        for (Vertex vertex : board.getVertices()) {
            for (Edge edge : vertex.getEdges()) {
                if (edge != null && (player.equals(edge.getPlayer()) || edge == roadToBuild)) {
                    // if this edge is this player's road or will be soon
                    roadSpots.addAll(Arrays.asList(vertex.getEdges()));
                }
            }
        }
        roadSpots.retainAll(board.getEmptyEdges());
        roadSpots.remove(null);
        roadSpots.remove(roadToBuild); // because you can't build here, that was your first road
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
        removeMultipleOfOneResource(victim, robbedResource, 1);
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
        // figure out if a player lost his longest road, and maybe we need to recalculate things
        // and if so, do that
        // I put this after endGame, just in case it matters that the player whose turn it is gets first chance
            // to win
        determineInterruption(player, location);
    }

    // now I need to write the rules for interrupting the longest road
    // recalculation should only need to occur when the new settlement is connected to 2 roads that are of the
        // player with the longest road, and this is not that player
    // then I will have to recalculate the longest road player's longest road, and find the new value
        // that will require calling the method on every one of their roads
    // then I will need to figure out who has the longest road, and give them the longest road

    /**
     * This method determines if the newly built settlement interrupts a different player's road
     * If it does, it recalculates that player's longest road, and potentially changes who has the longest road
     * @param builder who just built a settlement
     * @param settlement that was just built
     */
    private void determineInterruption(Player builder, Vertex settlement) {
        // I need to recalculate even if the player being interrupted does not have the longest road
        // That way, if an interruption does happen later, they can still have longest road
        // First, I need to figure out which player was interrupted, if any
        Player interrupted = getInterruptedPlayer(builder, settlement);
        if (interrupted == null) { // if no player was interrupted, we are done her, and no recalculation is necessary
            return;
        } // from here on, we have an interrupted player who needs to be recalculated
        // so we recalculate the road length, and set the new length
        interrupted.setRoadLength(recalculateLongestRoad(interrupted));
        // if the player also had the longest road, we now need to figure out who has that now
        if (interrupted == longestRoadHolder) {
            redetermineLongestRoad();
        }
    }

    /**
     * @param builder who just built a settlement, and so cannot be interrupted
     * @param settlement that was just built
     * @return which player was just interrupted, or null if none were
     */
    private Player getInterruptedPlayer(Player builder, Vertex settlement) {
        Player interruptedPlayer = null;
        for (Edge edge : settlement.getEdges()) {
            // we check each edge
            if (edge != null && edge.getPlayer() != builder) {
                // if this is another player's road, we might have interrupted someone
                if (interruptedPlayer == null) {
                    // if this is the first other road we find, we store it
                    interruptedPlayer = edge.getPlayer();
                } else if (interruptedPlayer == edge.getPlayer()) {
                    // if this is the second, and of the first type as the first, we are interrupting that player
                    // and so must return it
                    return interruptedPlayer;
                }
                // we don't have to worry about getting an interrupted player of one type, and then overlooking another
                // because we know that at least 1 road belongs to this player
                // unless this is setup phase, in which case we can't interrupt anyone anyway
                // so of the remaining two, they either must be the same type as each other, and are interrupted
                // or they are of different types, in which case it doesn't matter what we ignore
                    // because they can't be interrupted anyway, only blocked
            }
        }
        return null; // we didn't find an interrupted player, so no player was interrupted
    }

    /**
     * This method is called either because the player was blocked by someone's settlement
     * Or because this player has a loop, and we need to figure out which way of calculating gives us
     * the longest road whenever we add a road
     *
     * This method does not actually change the longest road, because what needs to be done there
     * depends on which method called this one in the first place.
     *
     * This method is inefficient, at O(n^2), because it needs to calculate the road length at every road
     * in order to find the longest path through the loop, or check every branch for the longest road
     *
     * @param player whose longest road length needs to be recalculated
     */
    private int recalculateLongestRoad(Player player) {
        int longestRoad = 0;
        for (Edge edge : player.getRoads()) {
            int roadNumber = calculateRoadLength(player, edge);
            if (roadNumber > longestRoad) {
                longestRoad = roadNumber;
            }
        }
        return longestRoad;
    }

    /**
     * This method is called after someone interrupts the path of someone who has longest road
     * We now need to figure out who really has the longest road
     * These are the rules:
     *  If the person with longest road before still has or is tied for the longest road, they keep the points
     *  If someone new now has the longest road, they get the points
     *  If two new people are now tied for longest road, no one gets the points
     *  If no one remaining has a road of length 5 or more (even if the previous holder has the longest),
     *      no one gets the points
     */
    private void redetermineLongestRoad() {
        Player newLongestPlayer = longestRoadHolder.getRoadLength() >= 5 ? longestRoadHolder : null;
        // we only start planning to give the longest road holder longest road if he is still eligible
        int longestRoadLength = longestRoadHolder.getRoadLength();
        for (Player player : players) {
            if (player.getRoadLength() > longestRoadHolder.getRoadLength() && player.getRoadLength() >= 5) {
                // if no player is greater than the longest road holder, that holder must either have or
                    // be tied for longest road. Either way, he keeps it
                if (player.getRoadLength() > longestRoadLength) {
                    // if this player is greater, it is the greatest we have seen so far
                    // so that player becomes the one we give longest road to
                    newLongestPlayer = player;
                    longestRoadLength = player.getRoadLength();
                } else if (player.getRoadLength() == longestRoadLength) {
                    // if this player is equal, then there is a tied for longest road, and one not involving
                        // the previous holder
                    // as a result, we get rid of the player, but keep the longestRoadLength as a requirement
                        // to beat if the last player wants longest road
                    newLongestPlayer = null;
                }
            }
        }
        //no if statement needed, we know there was a previous holder
        longestRoadHolder.increaseVictoryPoints(-2);
        // give this player longest road
        if (newLongestPlayer != null) {
            // if someone gets the longest road now, give them the points
            newLongestPlayer.increaseVictoryPoints(2);
        }
        // update in Main
        longestRoadHolder = newLongestPlayer;
        // if the redetermination caused a new victor, we should end the game now
        if (longestRoadHolder != null && longestRoadHolder.getVictoryPoints() >= 10) {
            endGame(longestRoadHolder);
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
        // recalculate the longest road, see if this player has it now
        determineLongestRoad(player, location);
        // no need to report to GUI that the action is done, it already knows
    }

    /**
     * This method, called when a road is placed, determines whether this road gives this player longest road
     * If it does, it removes longest road from the previous player and gives it to this one
     * @param player who just put down a road
     * @param road that was just put down
     */
    private void determineLongestRoad(Player player, Edge road) {
        int roadLength = calculateRoadLength(player, road);
        if (roadLength > player.getRoadLength()) { // if this is the player's new record, change their length
            player.setRoadLength(roadLength);
        }
        if ((longestRoadHolder == null || roadLength > longestRoadHolder.getRoadLength())
                // so that if there is no holder, we don't need to check if this road is bigger
                && roadLength >= 5) { // the player's road is bigger than the holder's
            // remove longest road from previous player, if there is one
            if (longestRoadHolder != null) {
                longestRoadHolder.increaseVictoryPoints(-2);
            }
            // give this player longest road
            player.increaseVictoryPoints(2);
            // update in Main
            longestRoadHolder = player;
            if (player.getVictoryPoints() >= 10) {
                endGame(player);
            }
        }
    }

    /**
     * The base method for calculating road length. Returns the length of this player's road, starting from
     * this edge
     * @param player who built a road
     * @param road that was just built
     * @return the length of the player's road
     */
    private int calculateRoadLength(Player player, Edge road) {
        // we calculate the road on each side, and then add together the 2 parts
        List<Vertex> adjVertices = getVerticesAdjacentToRoad(road);
        Set<Edge> duplicateSet = new HashSet<>(player.getRoads());
        int firstLength = calculateRoadLength(player, adjVertices.get(0), road, duplicateSet);
        int secondLength = calculateRoadLength(player, adjVertices.get(1), road, duplicateSet);
        //System.out.println("Calculating length of " + (firstLength + secondLength - 1) + " for " + player + " during turn of " + currentTurn);
        return firstLength + secondLength - 1; // the longest path on each side
        // both paths count this road, so we have to remove the second copy
    }

    /**
     * The recursive method for calculating road length
     * @param player whose roads we are following
     * @param vertex we are currently looking at
     * @param road that we last visited
     * @param dupSet the set of Roads that the player has, removed from whenever a road is looked at
     *               Used to prevent loops from causing a StackOverflowError
     * @return the length of the road from the other end until here
     */
    private int calculateRoadLength(Player player, Vertex vertex, Edge road, Set<Edge> dupSet) {
        // If this vertex belongs to another player, we return 0
        if (vertex.getPlayer() != null && vertex.getPlayer() != player) {
            // if there is a settlement here, and it doesn't belong to this player
            return 0;
        }
        // Otherwise, we look at each edge
        dupSet.remove(road); // we already looked at it, we shouldn't look at it again if there
            // is a loop
        int laterRoadLength = 0;
        for (int i = 0; i < 3; i++) {
            Edge edge = vertex.getEdges()[i];
            // If the edge does not belong to this player, it is ignored
            // Or if it is the road we are looking at now
            if (edge != null && dupSet.contains(edge)) {
                // the contains makes sure that (a) we haven't looked at it yet, and (b) this player
                    // owns it
                // If it does, we recursively call the method on that edge and the vertex beyond it,
                // keeping its return value, because we want everything beyond this road plus this road
                int lengthFromThisEdge = calculateRoadLength(player, vertex.getAdjacentVertices()[i], edge, dupSet);
                // the +1 accounts for this road, which we can't lose
                if (lengthFromThisEdge > laterRoadLength) {
                    // We then return the greater value of the two,
                    // since that is the value of the road beyond the given one
                    laterRoadLength = lengthFromThisEdge;
                }
            }
        }
        return laterRoadLength + 1; // to account for this road
    }

    /**
     * This is inefficient, but right now, Edges have no idea where Vertices or other edges are
     * @param road we are looking for its vertices
     * @return the vertices next to the road
     */
    private List<Vertex> getVerticesAdjacentToRoad(Edge road) {
        List<Vertex> adjVertices = new ArrayList<>();
        for (Vertex vertex : board.getVertices()) {
            for (Edge edge : vertex.getEdges()) {
                if (edge == road) {
                    adjVertices.add(vertex);
                }
            }
        }
        assert adjVertices.size() == 2;
        return adjVertices;
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
        // removes a card from the deck and gives it to the player
        // isWinner is true if this point card made the player win the game
        DevelopmentCard card = vellyDeck.remove();
        boolean isWinner = player.addDevelopmentCard(card);
        // remove expended resources, only in main phase to help testing
        if (isMainPhase) {
            player.removeResources(Building.DEVELOPMENT_CARD.getResources());
            newCards.put(card, newCards.getOrDefault(card, 0) + 1);
        }
        // no need to report to GUI, it will know from the method ending
        if (isWinner) { // if the player has won, end the game
            endGame(player);
        }
    }

    /**
     * Does this player have this development
     *
     * @param player who wants to play the card
     * @param card   the player wants to play
     * @return false if it is VICTORY_POINT or the player doesn't have it, true otherwise
     */
    @Override
    public boolean canPlay(Player player, DevelopmentCard card) {
        if (player == null || card == null) {
            throw new IllegalArgumentException("One or more inputs are null");
        }
        if (currentTurn != player // if it is not this player's turn
                || card == DevelopmentCard.VICTORY_POINT // or is a point card, which you can never play
                || player.getDevelopmentCards().getOrDefault(card, 0) <= newCards.getOrDefault(card, 0)) {
                // or this is a new card, which the player can't play yet
            return false;
        }
        return player.getDevelopmentCards().getOrDefault(card, 0) > 0;
        // returns true if the player has the card, false otherwise
    }

    /**
     * Plays the player's Knight development card, lets them move the robber and steals a resource
     *
     * @param stealer    playing the knight card
     * @param settlement being stolen from
     * @param location   hexagon being blocked, adjacent to the settlement
     * @return true if the card was successfully played, false if the player didn't have the card
     */
    @Override
    public boolean playKnight(Player stealer, Vertex settlement, Hex location) {
        if (!isMainPhase) { // that way, I can call removeDevelopmentCard and increase the Knight number
            stealer.addDevelopmentCard(DevelopmentCard.KNIGHT);
        }
        if (!stealer.removeDevelopmentCard(DevelopmentCard.KNIGHT)) {
            // that removes the development card if there is one
            // also increases the Knight counter
            // if it returns false, it means that the player never had one
            return false;
        }
        moveThief(stealer, settlement, location);
        if (hasLargestArmy(stealer)) {
            if (largestArmyHolder != null) { // if someone is losing Largest Army
                largestArmyHolder.increaseVictoryPoints(-2); // they lose the 2 points
            }
            boolean isWinner = stealer.increaseVictoryPoints(2); // give the 2 points
            largestArmyHolder = stealer; // make Main know you have it
            if (isWinner) { // if the new points make you win
                endGame(stealer); // you won!
            }
        }
        return true; // because the stealing was successful
    }

    /**
     * @param player being examined
     * @return true if the player now has the largest army, false otherwise
     */
    private boolean hasLargestArmy(Player player) {
        if (player.getKnightNumber() < 3) {
            return false;
        } // from here on, the player is eligible for largest army
        if (largestArmyHolder == null) {
            return true; // no one else has largest army, so this player should
        }
        return player.getKnightNumber() > largestArmyHolder.getKnightNumber(); // does this player have a
            // bigger army?
    }

    /**
     * Plays the player's Year of Plenty development card, gives them 2 resources of their choice
     *
     * @param player         playing the card
     * @param firstResource  the player receives
     * @param secondResource the player receives
     * @return true if the card was successfully played, false if the player didn't have the card
     */
    @Override
    public boolean playYearOfPlenty(Player player, Resource firstResource, Resource secondResource) {
        if (firstResource == Resource.MISC || secondResource == Resource.MISC) {
            throw new IllegalArgumentException("players can't get MISC resources");
        }
        if (isMainPhase && !player.removeDevelopmentCard(DevelopmentCard.YEAR_OF_PLENTY)) {
            // for testing, in setup phase, you don't need the card
            // the if statement removes the development card if there is one
            // if it returns false, it means that the player never had one
            return false;
        }
        player.addResource(firstResource);
        player.addResource(secondResource);
        return true;
    }

    /**
     * Plays the player's Monopoly development card, stealing every copy of that resource from all other players
     *
     * @param player   playing the card
     * @param resource the player steals from all other players
     * @return true if the card was successfully played, false if the player didn't have the card
     */
    @Override
    public boolean playMonopoly(Player player, Resource resource) {
        if (resource == Resource.MISC) {
            throw new IllegalArgumentException("players can't get MISC resources");
        }
        if (isMainPhase && !player.removeDevelopmentCard(DevelopmentCard.MONOPOLY)) {
            // for testing, in setup phase, you don't need the card
            // the if statement removes the development card if there is one
            // if it returns false, it means that the player never had one
            return false;
        }
        int numberOfCards = 0;
        for (Player otherPlayer : players) {
            if (otherPlayer != player) { // no point in removing cards from this player
                int playerResourceNum = otherPlayer.getResources().get(resource);
                // the number of this resource that that player has
                numberOfCards += playerResourceNum;
                removeMultipleOfOneResource(otherPlayer, resource, playerResourceNum);
                // that should remove all of that player's resources of that kind
            }
        }
        // now we give all the new resources to the player who played the card
        for (int i = 0; i < numberOfCards; i++) {
            player.addResource(resource);
        }
        return true;
    }

    /**
     * Plays the player's Road Building development card, letting them place 2 roads
     *
     * @param player         playing the card
     * @param firstLocation  an empty edge where this player can build
     * @param secondLocation an empty edge where this player can build after building firstLocation
     *                       Can be null if the player only has space to build one road
     * @return true if the card was successfully played, false if the player didn't have the card
     */
    @Override
    public boolean playRoadBuilding(Player player, Edge firstLocation, Edge secondLocation) {
        if (isMainPhase && !player.removeDevelopmentCard(DevelopmentCard.ROAD_BUILDING)) {
            // for testing, in setup phase, you don't need the card
            // the if statement removes the development card if there is one
            // if it returns false, it means that the player never had one
            return false;
        }
        buildRoadWithoutResources(player, firstLocation);
        if (secondLocation != null) {
            buildRoadWithoutResources(player, secondLocation);
        }
        return true;
    }

    private void buildRoadWithoutResources(Player player, Edge location) {
        if (isMainPhase) { // so that in the main phase, doesn't have any net loss of resources
            // not doing this in test phase, because won't lose any either way
            player.addResource(Resource.WOOD);
            player.addResource(Resource.BRICK);
        }
        buildRoad(player, location);
    }

    /**
     * Checks if this trade with the bank would work
     *
     * @param player         considering the trade
     * @param resourceGiven  resource type that would be given
     * @return true if the player can make this trade, false if the player lacks the port or resources
     */
    @Override
    public boolean canTrade(Player player, Resource resourceGiven) {
        if (player == null || resourceGiven == null) {
            throw new IllegalArgumentException("null values not permitted");
        }
        if (resourceGiven == Resource.MISC) {
            return false;
        }
        if (currentTurn != player) {
            return false; // you can only trade with the bank on your turn
        }
        int resourceNumber = getPlayerTradeNumber(player, resourceGiven);
        return player.getResources().get(resourceGiven) >= resourceNumber;
    }

    /**
     * Checks if this player can trade these resources to another player
     * Does not check if it is this player's turn, because this is also used to check if this player
     * can be traded with.
     *
     * @param player         trading or being traded with
     * @param resourcesGiven that this player would have to give as part of the trade
     * @param isRequestingPlayer if this player is requesting the trade or accepting it
     *                           If the player is requesting the trade, resourcesGiven values should
     *                              be negative.
     *                           Otherwise, they should be positive.
     * @return true if the player can make this trade, false if the player lacks the resources
     * Also returns false if the resources are empty, because you cannot donate resources
     */
    @Override
    public boolean canTrade(Player player, Map<Resource, Integer> resourcesGiven, boolean isRequestingPlayer) {
        if (player == null || resourcesGiven == null) {
            throw new IllegalArgumentException("null values not permitted");
        }
        if (resourcesGiven.isEmpty() || resourcesGiven.containsKey(Resource.MISC)) {
            // since we can't trade MISC
            // and you have to give something
            return false;
        }
        Map<Resource, Integer> playerResources = player.getResources();
        for (Resource resource : resourcesGiven.keySet()) { // make sure that has enough for each
                // resource
            if (!isRequestingPlayer && (playerResources.get(resource) < resourcesGiven.get(resource))
            || isRequestingPlayer && (playerResources.get(resource) * -1 < resourcesGiven.get(resource))) {
                return false;
            }
        }
        return true;
    }

    private int getPlayerTradeNumber(Player player, Resource resource) {
        int tradeNumber = 4;
        if (player.getPorts().contains(Resource.MISC)) {
            tradeNumber = 3;
        }
        if (player.getPorts().contains(resource)) {
            tradeNumber = 2;
        }
        return tradeNumber;
    }

    /**
     * This simulates a trade with the bank, updating the Player appropriately
     * Precondition: canTrade returns true
     *
     * @param player         doing the trade
     * @param resourceGiven  resource type being given
     * @param resourceGotten resource type being received
     */
    @Override
    public void trade(Player player, Resource resourceGiven, Resource resourceGotten) {
        if (player == null || resourceGiven == null || resourceGotten == null) {
            throw new IllegalArgumentException("null values not permitted");
        }
        if (resourceGiven == Resource.MISC || resourceGotten == Resource.MISC) {
            throw new IllegalArgumentException("Players can't have MISC resources");
        }
        int resourceNumber = getPlayerTradeNumber(player, resourceGiven);
        // now we remove the old resources and add the new ones
        removeMultipleOfOneResource(player, resourceGiven, resourceNumber);
        player.addResource(resourceGotten);
    }

    /**
     * This simulates a trade between 2 players, updating each accordingly
     *
     * @param player1            who is initiating the trade, whose turn it is
     * @param resourcesExchanged where negative values are given by player1 and received by player2
     *                           While positive values are given by player2 and received by player1
     * @param player2            who is on the other end of the trade
     */
    @Override
    public void trade(Player player1, Map<Resource, Integer> resourcesExchanged, Player player2) {
        // the new way of doing the resources is significantly more annoying for me, but that is what
        // Aryeh wanted
        Map<Resource, Integer> resourcesGiven = new HashMap<>();
        Map<Resource, Integer> resourcesReceived = new HashMap<>();
        for (Resource resource : resourcesExchanged.keySet()) {
            int resourceNumber = resourcesExchanged.get(resource);
            if (resourceNumber < 0) { // if this is being moved from player1 to player2
                resourcesGiven.put(resource, -1 * resourceNumber);
                for (int i = 0; i > resourceNumber; i--) {
                    player2.addResource(resource);
                }
            } else { // if this is being moved from player2 to player1
                resourcesReceived.put(resource, resourceNumber);
                for (int i = 0; i < resourceNumber; i++) {
                    player1.addResource(resource);
                }
            }
        }
        // now we remove the resources that have been given
        player1.removeResources(resourcesGiven);
        player2.removeResources(resourcesReceived);
    }

    /**
     * Removes multiple of the same resource from a player in one line
     * @param player losing resources
     * @param resource being removed
     * @param number of the resource being removed
     */
    private void removeMultipleOfOneResource(Player player, Resource resource, int number) {
        Map<Resource, Integer> resourcesRemoved = new HashMap<>();
        resourcesRemoved.put(resource, number);
        player.removeResources(resourcesRemoved);
    }

    /**
     * Deprecated: Use setTurn instead
     * This is used only for testing purposes, so I can change the stage from MainTest
     * @param changingToMain whether or not we are changing to main
     */
    @Deprecated
    protected void setPhase(boolean changingToMain) {
        isMainPhase = changingToMain;
    }

    /**
     * Changes to main phase if necessary and makes it this player's turn
     * Must be implemented
     * @param player whose turn it now is
     */
    protected void setTurn(Player player, boolean turnToMain) {
        isMainPhase = turnToMain;
        currentTurn = player;
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
