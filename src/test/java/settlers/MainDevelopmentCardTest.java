package settlers;

import org.junit.jupiter.api.Test;
import settlers.card.DevelopmentCard;
import settlers.card.Resource;
import settlers.gui.GUIMainDummyImpl;

import java.util.HashMap;
import java.util.Map;

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
}
