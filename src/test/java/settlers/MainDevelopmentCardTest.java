package settlers;

import org.junit.jupiter.api.Test;
import settlers.board.Edge;
import settlers.board.Hex;
import settlers.card.DevelopmentCard;
import settlers.card.Resource;
import settlers.gui.GUIMainDummyImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MainDevelopmentCardTest {

    private final MainImpl main;
    private final Player player;

    public MainDevelopmentCardTest() {
        main = new MainImpl(4, new GUIMainDummyImpl());
        player = main.getPlayers().get(0);
    }

    // tests for playerCanBuild
    // tests that it returns false if all the development cards have been taken by one player
    @Test
    public void playerCantBuildWhenTakesAllVellies() {
        player.addResource(Resource.ORE);
        player.addResource(Resource.SHEEP);
        player.addResource(Resource.WHEAT);

        for (int i = 0; i < 25; i++) {
            main.buildDevelopmentCard(player);
        }
        assertFalse(main.playerCanBuild(player, Building.DEVELOPMENT_CARD));
    }

    // tests that it returns false if the players together take all the cards
    @Test
    public void playerCantBuildWhenAllTakeAllVellies() {
        player.addResource(Resource.ORE);
        player.addResource(Resource.SHEEP);
        player.addResource(Resource.WHEAT);

        for (Player player : main.getPlayers()) {
            main.buildDevelopmentCard(player);
        }
        for (int i = 0; i < 21; i++) {
            main.buildDevelopmentCard(player);
        }
        assertFalse(main.playerCanBuild(player, Building.DEVELOPMENT_CARD));
    }

    // tests for build development card
    // tests that the player now has one more velly than he did before
    @Test
    public void buildDevelopmentCardGivesPlayerACard() {
        main.buildDevelopmentCard(player);
        for (DevelopmentCard card : player.getDevelopmentCards().keySet()) {
            if (player.getDevelopmentCards().get(card) == 1) {
                return;
            }
        }
        fail("The player has no development cards");
    }

    // tests that if we buy the development card every time possible, we get all the right numbers of cards
    @Test
    public void playerWithAllCardsHasRightOnes() {
        for (int i = 0; i < 25; i++) {
            main.buildDevelopmentCard(player);
        }
        Map<DevelopmentCard, Integer> playerDeck = player.getDevelopmentCards();
        assertEquals(14, playerDeck.get(DevelopmentCard.KNIGHT));
        assertEquals(5, playerDeck.get(DevelopmentCard.VICTORY_POINT));
        assertEquals(2, playerDeck.get(DevelopmentCard.MONOPOLY));
        assertEquals(2, playerDeck.get(DevelopmentCard.YEAR_OF_PLENTY));
        assertEquals(2, playerDeck.get(DevelopmentCard.ROAD_BUILDING));
    }

    /**
     * @return a map containing 0 of each resource
     */
    private HashMap<Resource, Integer> emptyResourceMap() {
        HashMap<Resource, Integer> emptyMap = new HashMap<>();
        for (Resource resource : Resource.values()) {
            if (resource != Resource.MISC) {
                emptyMap.put(resource, 0);
            }
        }
        return emptyMap;
    }

    // tests that if we are in the main phase, player resources are reduced by the right number
    @Test
    public void playerResourcesTo0() {
        main.setTurn(player, true);
        player.addResource(Resource.ORE);
        player.addResource(Resource.SHEEP);
        player.addResource(Resource.WHEAT);

        main.buildDevelopmentCard(player);

        assertEquals(emptyResourceMap(), player.getResources());
    }

    // tests for the same, when the player has extra resources
    @Test
    public void playerReduceResourceWhenHasMore() {
        main.setTurn(player, true);
        player.addResource(Resource.ORE);
        player.addResource(Resource.SHEEP);
        player.addResource(Resource.WHEAT);
        player.addResource(Resource.SHEEP);
        player.addResource(Resource.WOOD);

        main.buildDevelopmentCard(player);

        Map<Resource, Integer> result = emptyResourceMap();
        result.put(Resource.SHEEP, 1);
        result.put(Resource.WOOD, 1);
        assertEquals(result, player.getResources());
    }

    // tests for canPlay
    // make sure returns true if is player's turn and has card
    @Test
    public void canPlayReturnsTrueIfHasCardAndTurn() {
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        main.setTurn(player, true);
        assertTrue(main.canPlay(player, DevelopmentCard.KNIGHT));
    }

    // makes sure returns false if doesn't have card, even if is player's turn
    @Test
    public void canPlayReturnsFalseIfNoCard() {
        main.setTurn(player, true);
        assertFalse(main.canPlay(player, DevelopmentCard.KNIGHT));
    }

    @Test
    public void canPlayReturnsFalseIfNotTurn() {
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        main.setTurn(main.getPlayers().get(1), true);
        assertFalse(main.canPlay(player, DevelopmentCard.KNIGHT));
    }

    // tests for playKnight
    // makes sure that all the tests I made for moveThief also work here
    @Test
    public void getAvailableThiefSpotsWorksAfterKnight() {
        Hex thiefIsHere = main.getBoard().getHexes()[0];
        main.playKnight(player, thiefIsHere.getVertices()[0], thiefIsHere);
        Set<Hex> result = new HashSet<>(Arrays.asList(main.getBoard().getHexes()));
        result.remove(thiefIsHere);
        assertEquals(result, main.getAvailableThiefSpots());
    }

    // tests that this works even if the thief was already moved by moveThief
    @Test
    public void playKnightWorksAfterMoveThief() {
        Hex thiefIsHere = main.getBoard().getHexes()[0];
        main.moveThief(player, thiefIsHere.getVertices()[0], thiefIsHere);
        thiefIsHere = main.getBoard().getHexes()[1];
        main.playKnight(player, thiefIsHere.getVertices()[0], thiefIsHere);

        Set<Hex> result = new HashSet<>(Arrays.asList(main.getBoard().getHexes()));
        result.remove(thiefIsHere);
        assertEquals(result, main.getAvailableThiefSpots());
    }

    // tests that this works even if the thief was already moved by playKnight
    @Test
    public void playKnightWorksTwice() {
        Hex thiefIsHere = main.getBoard().getHexes()[0];
        main.playKnight(player, thiefIsHere.getVertices()[0], thiefIsHere);
        thiefIsHere = main.getBoard().getHexes()[1];
        main.playKnight(player, thiefIsHere.getVertices()[0], thiefIsHere);

        Set<Hex> result = new HashSet<>(Arrays.asList(main.getBoard().getHexes()));
        result.remove(thiefIsHere);
        assertEquals(result, main.getAvailableThiefSpots());
    }

    // tests that the first player gets a resource, and second one loses it
    @Test
    public void playKnightMovesResource() {
        Hex thiefIsHere = main.getBoard().getHexes()[0];

        Player badGuy = main.getPlayers().get(0);
        badGuy.addResource(Resource.WOOD);
        badGuy.addResource(Resource.ORE);

        Player victim = main.getPlayers().get(1);
        victim.addResource(Resource.BRICK);
        main.buildSettlement(victim, thiefIsHere.getVertices()[0]);

        main.playKnight(badGuy, thiefIsHere.getVertices()[0], thiefIsHere);

        HashMap<Resource, Integer> result = emptyResourceMap();
        result.put(Resource.WOOD, 1);
        result.put(Resource.ORE, 1);
        result.put(Resource.BRICK, 1);
        assertEquals(result, badGuy.getResources());
        assertEquals(emptyResourceMap(), victim.getResources());
    }

    // test that stealing works even if other player has many resources, using first with 7 and second with 8
    @Test
    public void playKnightMovesResourceFromRich() {
        Hex thiefIsHere = main.getBoard().getHexes()[0];

        Player badGuy = main.getPlayers().get(0);
        badGuy.addResource(Resource.WOOD);
        badGuy.addResource(Resource.ORE);
        badGuy.addResource(Resource.WOOD);
        badGuy.addResource(Resource.ORE);
        badGuy.addResource(Resource.BRICK);
        badGuy.addResource(Resource.SHEEP);
        badGuy.addResource(Resource.WHEAT);

        Player victim = main.getPlayers().get(1);
        victim.addResource(Resource.WOOD);
        victim.addResource(Resource.ORE);
        victim.addResource(Resource.WOOD);
        victim.addResource(Resource.ORE);
        victim.addResource(Resource.BRICK);
        victim.addResource(Resource.SHEEP);
        victim.addResource(Resource.WHEAT);
        victim.addResource(Resource.WHEAT);
        main.buildSettlement(victim, thiefIsHere.getVertices()[0]);

        main.playKnight(badGuy, thiefIsHere.getVertices()[0], thiefIsHere);

        assertTrue(badGuy.hasMoreThan7Cards());
        assertFalse(victim.hasMoreThan7Cards());
    }

    // makes sure that if main phase, the player has no more knight cards
    @Test
    public void playKnightBringsCardsTo0() {
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        main.setTurn(player, true);

        Hex thiefIsHere = main.getBoard().getHexes()[0];
        main.playKnight(player, thiefIsHere.getVertices()[0], thiefIsHere);

        Map<DevelopmentCard, Integer> result = new HashMap<>();
        result.put(DevelopmentCard.KNIGHT, 0);
        assertEquals(result, player.getDevelopmentCards());
    }

    // makes sure that if main phase, the player has 1 fewer knight card
    @Test
    public void playKnightBringsCards1Less() {
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        player.addDevelopmentCard(DevelopmentCard.YEAR_OF_PLENTY);
        main.setTurn(player, true);

        Hex thiefIsHere = main.getBoard().getHexes()[0];
        main.playKnight(player, thiefIsHere.getVertices()[0], thiefIsHere);

        Map<DevelopmentCard, Integer> result = new HashMap<>();
        result.put(DevelopmentCard.KNIGHT, 1);
        result.put(DevelopmentCard.YEAR_OF_PLENTY, 1);
        assertEquals(result, player.getDevelopmentCards());
    }

    // also add tests that it returns true if the player has a knight, in main phase
    @Test
    public void playKnightReturnsTrueIfHasCard() {
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        main.setTurn(player, true);

        Hex thiefIsHere = main.getBoard().getHexes()[0];
        assertTrue(main.playKnight(player, thiefIsHere.getVertices()[0], thiefIsHere));
    }

    // and false if he has no development cards
    @Test
    public void playKnightReturnsFalseIfNoCards() {
        main.setTurn(player, true);

        Hex thiefIsHere = main.getBoard().getHexes()[0];

        boolean skipTest = thiefIsHere.hasThief();
        // if the thief is already there, then we don't want to test that it wasn't moved here

        assertFalse(main.playKnight(player, thiefIsHere.getVertices()[0], thiefIsHere));
        if (!skipTest) {
            assertFalse(thiefIsHere.hasThief());
        }
    }

    // or has the wrong type of development cards
    @Test
    public void playKnightReturnsFalseIfWrongCards() {
        player.addDevelopmentCard(DevelopmentCard.VICTORY_POINT);
        player.addDevelopmentCard(DevelopmentCard.ROAD_BUILDING);
        main.setTurn(player, true);

        Hex thiefIsHere = main.getBoard().getHexes()[0];
        assertFalse(main.playKnight(player, thiefIsHere.getVertices()[0], thiefIsHere));
    }

    // tests for playYearOfPlenty
    // tests that the player can get 2 of the same card
    @Test
    public void playYearOfPlentyGives2OfSameCard() {
        main.playYearOfPlenty(player, Resource.WOOD, Resource.WOOD);

        Map<Resource, Integer> result = emptyResourceMap();
        result.put(Resource.WOOD, 2);
        assertEquals(result, player.getResources());
    }

    // tests that the player can get 2 different cards
    @Test
    public void playYearOfPlentyGives2DifCards() {
        main.playYearOfPlenty(player, Resource.WOOD, Resource.BRICK);

        Map<Resource, Integer> result = emptyResourceMap();
        result.put(Resource.WOOD, 1);
        result.put(Resource.BRICK, 1);
        assertEquals(result, player.getResources());
    }

    // tests that this stacks with cars the player already has
    @Test
    public void playYearOfPlentyResourcesStack() {
        player.addResource(Resource.WOOD);
        player.addResource(Resource.SHEEP);

        main.playYearOfPlenty(player, Resource.WOOD, Resource.BRICK);

        Map<Resource, Integer> result = emptyResourceMap();
        result.put(Resource.WOOD, 2);
        result.put(Resource.BRICK, 1);
        result.put(Resource.SHEEP, 1);
        assertEquals(result, player.getResources());
    }

    // tests that reduces number of yop cards to 0 if main
    @Test
    public void playYearOfPlentyReducesCardTo0() {
        player.addDevelopmentCard(DevelopmentCard.YEAR_OF_PLENTY);
        main.setTurn(player, true);

        main.playYearOfPlenty(player, Resource.WOOD, Resource.BRICK);

        Map<DevelopmentCard, Integer> result = new HashMap<>();
        result.put(DevelopmentCard.YEAR_OF_PLENTY, 0);
        assertEquals(result, player.getDevelopmentCards());
    }

    // tests that reduces number of yop cards to 1 if had 2, and ignores other cards
    @Test
    public void playYearOfPlentyReducesCardTo1() {
        player.addDevelopmentCard(DevelopmentCard.YEAR_OF_PLENTY);
        player.addDevelopmentCard(DevelopmentCard.YEAR_OF_PLENTY);
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        main.setTurn(player, true);

        main.playYearOfPlenty(player, Resource.WOOD, Resource.BRICK);

        Map<DevelopmentCard, Integer> result = new HashMap<>();
        result.put(DevelopmentCard.YEAR_OF_PLENTY, 1);
        result.put(DevelopmentCard.KNIGHT, 1);
        assertEquals(result, player.getDevelopmentCards());
    }

    // tests that returns true in main phase if the player had the card
    @Test
    public void playYearOfPlentyReturnsTrueIfHasCard() {
        player.addDevelopmentCard(DevelopmentCard.YEAR_OF_PLENTY);
        main.setTurn(player, true);

        assertTrue(main.playYearOfPlenty(player, Resource.WOOD, Resource.BRICK));
    }

    // tests that return false in main phase if the player didn't have any cards
    @Test
    public void playYearOfPlentyReturnsFalseIfNoCards() {
        main.setTurn(player, true);

        assertFalse(main.playYearOfPlenty(player, Resource.WOOD, Resource.BRICK));
    }

    // tests that return false in main phase if the player had the wrong cards
    @Test
    public void playYearOfPlentyReturnsFalseIfWrongCards() {
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        player.addDevelopmentCard(DevelopmentCard.VICTORY_POINT);
        main.setTurn(player, true);

        assertFalse(main.playYearOfPlenty(player, Resource.WOOD, Resource.BRICK));
    }

    // tests that in main phase, if it returns false, the player gains no resources
    @Test
    public void playYearOfPlentyFalseGivesNoResources() {
        main.setTurn(player, true);

        assertFalse(main.playYearOfPlenty(player, Resource.WOOD, Resource.BRICK));
        assertEquals(emptyResourceMap(), player.getResources());
    }

    // tests for playMonopoly
    // tests that if one player has 2 cards, the that player loses both and this gains both
    @Test
    public void playMonopolyMoves2From1() {
        List<Player> players = main.getPlayers();
        players.get(1).addResource(Resource.WOOD);
        players.get(1).addResource(Resource.WOOD);

        main.playMonopoly(player, Resource.WOOD);

        Map<Resource, Integer> result = emptyResourceMap();
        result.put(Resource.WOOD, 2);
        assertEquals(result, player.getResources());
        assertEquals(emptyResourceMap(), players.get(1).getResources());
    }

    // tests that if 2 players have 3 cards, this player gets all three and they lose all three
    @Test
    public void playMonopolyMoves3From2() {
        List<Player> players = main.getPlayers();
        players.get(1).addResource(Resource.WOOD);
        players.get(1).addResource(Resource.WOOD);
        players.get(2).addResource(Resource.WOOD);

        main.playMonopoly(player, Resource.WOOD);

        Map<Resource, Integer> result = emptyResourceMap();
        result.put(Resource.WOOD, 3);
        assertEquals(result, player.getResources());
        assertEquals(emptyResourceMap(), players.get(1).getResources());
        assertEquals(emptyResourceMap(), players.get(2).getResources());
    }

    // tests that cards from monopoly stack with what the player already had
    @Test
    public void playMonopolyNewCardsStackWithOld() {
        List<Player> players = main.getPlayers();
        player.addResource(Resource.WOOD);
        player.addResource(Resource.ORE);

        players.get(1).addResource(Resource.WOOD);
        players.get(1).addResource(Resource.WOOD);
        players.get(2).addResource(Resource.WOOD);

        main.playMonopoly(player, Resource.WOOD);

        Map<Resource, Integer> result = emptyResourceMap();
        result.put(Resource.WOOD, 4);
        result.put(Resource.ORE, 1);
        assertEquals(result, player.getResources());
        assertEquals(emptyResourceMap(), players.get(1).getResources());
        assertEquals(emptyResourceMap(), players.get(2).getResources());
    }

    // tests that monopoly doesn't steal cards besides the selected card
    @Test
    public void playMonopolyOnlyStealsSelectedCard() {
        List<Player> players = main.getPlayers();
        players.get(1).addResource(Resource.WOOD);
        players.get(1).addResource(Resource.ORE);
        players.get(2).addResource(Resource.WHEAT);

        main.playMonopoly(player, Resource.WOOD);

        Map<Resource, Integer> result = emptyResourceMap();
        result.put(Resource.WOOD, 1);
        assertEquals(result, player.getResources());

        // now for player 1
        result.put(Resource.WOOD, 0);
        result.put(Resource.ORE, 1);
        assertEquals(result, players.get(1).getResources());

        // now for player 2
        result.put(Resource.ORE, 0);
        result.put(Resource.WHEAT, 1);
        assertEquals(result, players.get(2).getResources());
    }

    // tests that if no one has the resource, nothing happens
    @Test
    public void playMonopolyDoesNothingIfNoOneHas() {
        List<Player> players = main.getPlayers();
        player.addResource(Resource.WOOD);
        players.get(1).addResource(Resource.ORE);
        players.get(2).addResource(Resource.WHEAT);

        main.playMonopoly(player, Resource.WOOD);

        Map<Resource, Integer> result = emptyResourceMap();
        result.put(Resource.WOOD, 1);
        assertEquals(result, player.getResources());

        // now for player 1
        result.put(Resource.WOOD, 0);
        result.put(Resource.ORE, 1);
        assertEquals(result, players.get(1).getResources());

        // now for player 2
        result.put(Resource.ORE, 0);
        result.put(Resource.WHEAT, 1);
        assertEquals(result, players.get(2).getResources());
    }

    // tests that reduces number of monopoly cards to 0 in main phase
    @Test
    public void playMonopolyReducesCardTo0() {
        player.addDevelopmentCard(DevelopmentCard.MONOPOLY);
        main.setTurn(player, true);

        main.playMonopoly(player, Resource.BRICK);

        Map<DevelopmentCard, Integer> result = new HashMap<>();
        result.put(DevelopmentCard.MONOPOLY, 0);
        assertEquals(result, player.getDevelopmentCards());
    }

    // tests that reduces 2 to 1 and ignores other cards in main phase
    @Test
    public void playMonopolyReducesCardTo1() {
        player.addDevelopmentCard(DevelopmentCard.MONOPOLY);
        player.addDevelopmentCard(DevelopmentCard.MONOPOLY);
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        main.setTurn(player, true);

        main.playMonopoly(player, Resource.WOOD);

        Map<DevelopmentCard, Integer> result = new HashMap<>();
        result.put(DevelopmentCard.MONOPOLY, 1);
        result.put(DevelopmentCard.KNIGHT, 1);
        assertEquals(result, player.getDevelopmentCards());
    }

    // tests that returns true in main phase if the player had the card
    @Test
    public void playMonopolyReturnsTrueIfHasCard() {
        player.addDevelopmentCard(DevelopmentCard.MONOPOLY);
        main.setTurn(player, true);

        assertTrue(main.playMonopoly(player, Resource.BRICK));
    }

    // tests that return false in main phase if the player didn't have any cards or had wrong ones
    @Test
    public void playMonopolyReturnsFalseIfNoCardsOrWrong() {
        main.setTurn(player, true);

        assertFalse(main.playMonopoly(player, Resource.BRICK));

        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        player.addDevelopmentCard(DevelopmentCard.VICTORY_POINT);

        assertFalse(main.playMonopoly(player, Resource.BRICK));
    }

    // tests that in main phase, if it returns false, the player gains no resources and no players lose
    @Test
    public void playMonopolyFalseMovesNoResources() {
        List<Player> players = main.getPlayers();
        players.get(1).addResource(Resource.WOOD);
        players.get(2).addResource(Resource.WOOD);
        main.setTurn(player, true);

        assertFalse(main.playMonopoly(player, Resource.BRICK));
        assertEquals(emptyResourceMap(), player.getResources());

        Map<Resource, Integer> result = emptyResourceMap();
        result.put(Resource.WOOD, 1);

        assertEquals(result, players.get(1).getResources());
        assertEquals(result, players.get(2).getResources());
    }

    // tests for playRoadBuilding
    // tests that both locations now have roads
    // I will assume that these roads act as normal roads, even though maybe I shouldn't
    @Test
    public void playRoadBuildingPlacesRoads() {
        Edge edge1 = main.getBoard().getVertices()[0].getEdges()[1];
        Edge edge2 = main.getBoard().getVertices()[0].getEdges()[2];
        main.playRoadBuilding(player, edge1, edge2);

        assertEquals(player, edge1.getPlayer());
        assertEquals(player, edge2.getPlayer());
    }

    // tests that can make the second edge null with no problems
    @Test
    public void playRoadBuildingCanHaveSecondEdgeNull() {
        Edge edge1 = main.getBoard().getVertices()[0].getEdges()[1];
        main.playRoadBuilding(player, edge1, null);

        assertEquals(player, edge1.getPlayer());
    }

    // tests that the player has not lost any resources when he never had any
    @Test
    public void playRoadBuildingDoesntTakeResourcesEmpty() {
        Edge edge1 = main.getBoard().getVertices()[0].getEdges()[1];
        Edge edge2 = main.getBoard().getVertices()[0].getEdges()[2];
        player.addDevelopmentCard(DevelopmentCard.ROAD_BUILDING);
        main.setTurn(player, true);

        main.playRoadBuilding(player, edge1, edge2);

        assertEquals(emptyResourceMap(), player.getResources());
    }

    // tests that the player has not lost any resources when he had enough to build both roads
    @Test
    public void playRoadBuildingDoesntTakeResourcesWhenHas() {
        Edge edge1 = main.getBoard().getVertices()[0].getEdges()[1];
        Edge edge2 = main.getBoard().getVertices()[0].getEdges()[2];

        player.addDevelopmentCard(DevelopmentCard.ROAD_BUILDING);
        player.addResource(Resource.WOOD);
        player.addResource(Resource.WOOD);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.BRICK);

        main.setTurn(player, true);

        main.playRoadBuilding(player, edge1, edge2);

        Map<Resource, Integer> result = emptyResourceMap();
        result.put(Resource.WOOD, 2);
        result.put(Resource.BRICK, 2);

        assertEquals(result, player.getResources());
    }

    // tests that in main, the player lost the card when playing it
    @Test
    public void playRoadBuildingMovesCardTo0() {
        Edge edge1 = main.getBoard().getVertices()[0].getEdges()[1];
        Edge edge2 = main.getBoard().getVertices()[0].getEdges()[2];
        player.addDevelopmentCard(DevelopmentCard.ROAD_BUILDING);
        main.setTurn(player, true);

        main.playRoadBuilding(player, edge1, edge2);

        Map<DevelopmentCard, Integer> result = new HashMap<>();
        result.put(DevelopmentCard.ROAD_BUILDING, 0);

        assertEquals(result, player.getDevelopmentCards());
    }

    // tests that reduces card to 1 and ignores other cards when many vellies
    @Test
    public void playRoadBuildingMovesCardTo1() {
        Edge edge1 = main.getBoard().getVertices()[0].getEdges()[1];
        Edge edge2 = main.getBoard().getVertices()[0].getEdges()[2];
        player.addDevelopmentCard(DevelopmentCard.ROAD_BUILDING);
        player.addDevelopmentCard(DevelopmentCard.ROAD_BUILDING);
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);

        main.setTurn(player, true);

        main.playRoadBuilding(player, edge1, edge2);

        Map<DevelopmentCard, Integer> result = new HashMap<>();
        result.put(DevelopmentCard.ROAD_BUILDING, 1);
        result.put(DevelopmentCard.KNIGHT, 1);

        assertEquals(result, player.getDevelopmentCards());
    }

    // tests that returns true if player has the card in main
    @Test
    public void playRoadBuildingReturnsTrueIfHasCard() {
        Edge edge1 = main.getBoard().getVertices()[0].getEdges()[1];
        Edge edge2 = main.getBoard().getVertices()[0].getEdges()[2];
        player.addDevelopmentCard(DevelopmentCard.ROAD_BUILDING);
        main.setTurn(player, true);

        assertTrue(main.playRoadBuilding(player, edge1, edge2));
    }

    // tests that if player doesn't have the card or has wrong one, returns false
    @Test
    public void playRoadBuilderReturnsFalseIfNoCardsOrWrong() {
        Edge edge1 = main.getBoard().getVertices()[0].getEdges()[1];
        Edge edge2 = main.getBoard().getVertices()[0].getEdges()[2];
        main.setTurn(player, true);

        assertFalse(main.playRoadBuilding(player, edge1, edge2));

        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        player.addDevelopmentCard(DevelopmentCard.VICTORY_POINT);

        assertFalse(main.playRoadBuilding(player, edge1, edge2));
    }

    // tests that if returns false, no roads are built
    @Test
    public void playRoadBuildingFalsePlacesNoRoads() {
        Edge edge1 = main.getBoard().getVertices()[0].getEdges()[1];
        Edge edge2 = main.getBoard().getVertices()[0].getEdges()[2];
        main.setTurn(player, true);

        main.playRoadBuilding(player, edge1, edge2);

        assertNull(edge1.getPlayer());
        assertNull(edge2.getPlayer());
    }

    // the following tests will be for the mechanic that prevents you from buying a development card on that turn
    // since this will interact with the main loop, I can't test it with unit tests
    // instead, I will write down all the tests I need to do with the GUI
    // all these tests must be in finite resource mode, since this should be opperating with the canPlay method

    // I need to test that you can't play a development card on the turn you get it (!)

    // I need to test that you can play the development card on the next turn (!)

    // I need to test that if one player has one card and then another player gets the same card, the first player
        // can play it immediately but the second one must wait (!)

    // I need to test that if a player buys 2 of the same card, he can't play either until the next turn (!)

    // I need to test that if a player buys a card when he already has one of the same card, the player can
        // play the card once, but not twice (!)

    // make sure that victory point cards can win you the game on the turn you buy them (!)
}
