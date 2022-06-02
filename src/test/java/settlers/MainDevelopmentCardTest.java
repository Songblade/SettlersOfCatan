package settlers;

import org.junit.jupiter.api.Test;
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
        main.setPhase(true);
        player.addResource(Resource.ORE);
        player.addResource(Resource.SHEEP);
        player.addResource(Resource.WHEAT);

        main.buildDevelopmentCard(player);

        assertEquals(emptyResourceMap(), player.getResources());
    }

    // tests for the same, when the player has extra resources
    @Test
    public void playerReduceResourceWhenHasMore() {
        main.setPhase(true);
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

    // tests for playKnight
    // makes sure that all the tests I made for moveThief also work here
    @Test
    public void getAvailableThiefSpotsWorksAfterKnight() {
        Player player = main.getPlayers().get(0);
        Hex thiefIsHere = main.getBoard().getHexes()[0];
        main.playKnight(player, thiefIsHere.getVertices()[0], thiefIsHere);
        Set<Hex> result = new HashSet<>(Arrays.asList(main.getBoard().getHexes()));
        result.remove(thiefIsHere);
        assertEquals(result, main.getAvailableThiefSpots());
    }

    // tests that this works even if the thief was already moved by moveThief
    @Test
    public void playKnightWorksAfterMoveThief() {
        Player player = main.getPlayers().get(0);

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
        Player player = main.getPlayers().get(0);

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

    /**
     * @return an empty hand of development cards
     */
    private Map<DevelopmentCard, Integer> emptyDevelopmentMap() {
        HashMap<DevelopmentCard, Integer> emptyMap = new HashMap<>();
        for (DevelopmentCard resource : DevelopmentCard.values()) {
            emptyMap.put(resource, 0);
        }
        return emptyMap;
    }

    // makes sure that if main phase, the player has no more knight cards
    @Test
    public void playKnightBringsCardsTo0() {
        Player player = main.getPlayers().get(0);
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        main.setPhase(true);

        Hex thiefIsHere = main.getBoard().getHexes()[0];
        main.moveThief(player, thiefIsHere.getVertices()[0], thiefIsHere);

        assertEquals(emptyDevelopmentMap(), player.getDevelopmentCards());
    }

    // makes sure that if main phase, the player has 1 fewer knight card
    @Test
    public void playKnightBringsCards1Less() {
        Player player = main.getPlayers().get(0);
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        player.addDevelopmentCard(DevelopmentCard.YEAR_OF_PLENTY);
        main.setPhase(true);

        Hex thiefIsHere = main.getBoard().getHexes()[0];
        main.moveThief(player, thiefIsHere.getVertices()[0], thiefIsHere);

        Map<DevelopmentCard, Integer> result = emptyDevelopmentMap();
        result.put(DevelopmentCard.KNIGHT, 1);
        result.put(DevelopmentCard.YEAR_OF_PLENTY, 1);
        assertEquals(result, player.getDevelopmentCards());
    }

}
