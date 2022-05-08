package settlers;

import org.junit.jupiter.api.Test;
import settlers.card.DevelopmentCard;
import settlers.card.Resource;

import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;


public class PlayerTest {

    private Player player;

    public PlayerTest() {
        player = new PlayerImpl();
    }

    private HashMap<Resource, Integer> getEmptyHand() {
        HashMap<Resource, Integer> hand = new HashMap<>();
        for (Resource resource : Resource.values()) {
            if (resource != Resource.MISC) {
                hand.put(resource, 0);
            }
        }
        return hand;
    }

    // Test the resource methods (will check hand number each time)
    // make sure that can return an empty hand
    @Test
    public void resourceWorksEmpty() {
        assertEquals(getEmptyHand(), player.getResources());
        assertFalse(player.hasMoreThan7Cards());
    }

    // make sure that can return when we add one card
    @Test
    public void resourceWorksOne() {
        player.addResource(Resource.WOOD);
        HashMap<Resource, Integer> result = getEmptyHand();
        result.put(Resource.WOOD, 1);
        assertEquals(result, player.getResources());
        assertFalse(player.hasMoreThan7Cards());
    }

    // make sure that can return when add up to seven cards
    @Test
    public void resourceWorks7() {
        player.addResource(Resource.WOOD);
        player.addResource(Resource.ORE);
        player.addResource(Resource.WOOD);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.WHEAT);
        player.addResource(Resource.SHEEP);
        HashMap<Resource, Integer> result = new HashMap<>();
        result.put(Resource.WOOD, 2);
        result.put(Resource.ORE, 1);
        result.put(Resource.BRICK, 2);
        result.put(Resource.WHEAT, 1);
        result.put(Resource.SHEEP, 1);
        assertEquals(result, player.getResources());
        assertFalse(player.hasMoreThan7Cards());
    }

    // make sure that works when has more than 7 cards
    @Test
    public void resourceWorks8() {
        player.addResource(Resource.WOOD);
        player.addResource(Resource.ORE);
        player.addResource(Resource.WOOD);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.WHEAT);
        player.addResource(Resource.SHEEP);
        player.addResource(Resource.WOOD);
        HashMap<Resource, Integer> result = new HashMap<>();
        result.put(Resource.WOOD, 3);
        result.put(Resource.ORE, 1);
        result.put(Resource.BRICK, 2);
        result.put(Resource.WHEAT, 1);
        result.put(Resource.SHEEP, 1);
        assertEquals(result, player.getResources());
        assertTrue(player.hasMoreThan7Cards());
    }

    // make sure works when remove cards down to 0
    @Test
    public void resourceWorksRemoveTo0() {
        player.addResource(Resource.WOOD);
        player.addResource(Resource.ORE);
        player.addResource(Resource.WOOD);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.WHEAT);
        player.addResource(Resource.SHEEP);
        HashMap<Resource, Integer> result = new HashMap<>();
        result.put(Resource.WOOD, 2);
        result.put(Resource.ORE, 1);
        result.put(Resource.BRICK, 2);
        result.put(Resource.WHEAT, 1);
        result.put(Resource.SHEEP, 1);
        assertTrue(player.removeResources(result));
        assertEquals(getEmptyHand(), player.getResources());
        assertFalse(player.hasMoreThan7Cards());
    }

    // makes sure works when remove cards to more than 0
    @Test
    public void resourceWorksRemoveToLessThan0() {
        player.addResource(Resource.WOOD);
        player.addResource(Resource.ORE);
        player.addResource(Resource.WOOD);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.WHEAT);
        player.addResource(Resource.SHEEP);
        HashMap<Resource, Integer> result = new HashMap<>();
        result.put(Resource.WOOD, 2);
        result.put(Resource.ORE, 1);
        result.put(Resource.BRICK, 2);
        result.put(Resource.WHEAT, 0);
        result.put(Resource.SHEEP, 1);
        assertTrue(player.removeResources(result));
        result = getEmptyHand();
        result.put(Resource.WHEAT, 1);
        assertEquals(result, player.getResources());
        assertFalse(player.hasMoreThan7Cards());
    }

    // makes sure works when remove cards from more than 7 to 7
    @Test
    public void resourceWorks8To7() {
        player.addResource(Resource.WOOD);
        player.addResource(Resource.ORE);
        player.addResource(Resource.WOOD);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.WHEAT);
        player.addResource(Resource.SHEEP);
        player.addResource(Resource.WOOD);
        assertTrue(player.hasMoreThan7Cards());
        HashMap<Resource, Integer> result = new HashMap<>();
        result.put(Resource.WOOD, 1);
        assertTrue(player.removeResources(result));
        result.put(Resource.WOOD, 2);
        result.put(Resource.ORE, 1);
        result.put(Resource.BRICK, 2);
        result.put(Resource.WHEAT, 1);
        result.put(Resource.SHEEP, 1);
        assertEquals(result, player.getResources());
        assertFalse(player.hasMoreThan7Cards());
    }

    // tests that addResource throws if you try to add MISC
    @Test
    public void addResourceThrowsIfMISC() {
        assertThrows(IllegalArgumentException.class, () -> player.addResource(Resource.MISC));
    }

    // tests that removeResource is false doesn't work
    // tests that nothing has been removed if cannot remove something
    @Test
    public void removeResourceFalseIfFails() {
        player.addResource(Resource.WOOD);
        player.addResource(Resource.ORE);
        player.addResource(Resource.WOOD);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.WHEAT);
        player.addResource(Resource.SHEEP);
        HashMap<Resource, Integer> result = new HashMap<>();
        result.put(Resource.WOOD, 2);
        result.put(Resource.ORE, 2);
        result.put(Resource.BRICK, 2);
        result.put(Resource.WHEAT, 1);
        result.put(Resource.SHEEP, 1);
        assertFalse(player.removeResources(result));
        result.put(Resource.ORE, 1);
        assertEquals(result, player.getResources());
        assertFalse(player.hasMoreThan7Cards());
    }

    // testing development card methods (But not exception)

    // test can add multiple vellies, both same and different types
    @Test
    public void canAddVellies() {
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        HashMap<DevelopmentCard, Integer> result = new HashMap<>();
        result.put(DevelopmentCard.KNIGHT, 1);
        assertEquals(result, player.getDevelopmentCards());
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        result.put(DevelopmentCard.KNIGHT, 2);
        assertEquals(result, player.getDevelopmentCards());
        player.addDevelopmentCard(DevelopmentCard.MONOPOLY);
        result.put(DevelopmentCard.MONOPOLY, 1);
        assertEquals(result, player.getDevelopmentCards());
    }

    // test can remove a velly, and that returns true if you have it
    @Test
    public void canRemoveVellies() {
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        assertTrue(player.removeDevelopmentCard(DevelopmentCard.KNIGHT));
        HashMap<DevelopmentCard, Integer> result = new HashMap<>();
        result.put(DevelopmentCard.KNIGHT, 0);
        assertEquals(result, player.getDevelopmentCards());
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        player.addDevelopmentCard(DevelopmentCard.MONOPOLY);
        assertTrue(player.removeDevelopmentCard(DevelopmentCard.MONOPOLY));
        result.put(DevelopmentCard.KNIGHT, 1);
        result.put(DevelopmentCard.MONOPOLY, 0);
        assertEquals(result, player.getDevelopmentCards());
        assertTrue(player.removeDevelopmentCard(DevelopmentCard.KNIGHT));
        result.put(DevelopmentCard.KNIGHT, 0);
        assertEquals(result, player.getDevelopmentCards());
    }

    // test that can't remove a point card, and return false
    @Test
    public void cantRemovePointCards() {
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        player.addDevelopmentCard(DevelopmentCard.VICTORY_POINT);
        HashMap<DevelopmentCard, Integer> result = new HashMap<>();
        result.put(DevelopmentCard.KNIGHT, 1);
        result.put(DevelopmentCard.VICTORY_POINT, 1);
        assertEquals(result, player.getDevelopmentCards());
        assertFalse(player.removeDevelopmentCard(DevelopmentCard.VICTORY_POINT));
        assertEquals(result, player.getDevelopmentCards());
    }

    // tests that removing a card that you don't have returns false
    @Test
    public void cantRemoveAbsentCards() {
        assertFalse(player.removeDevelopmentCard(DevelopmentCard.ROAD_BUILDING));
        player.addDevelopmentCard(DevelopmentCard.KNIGHT);
        assertFalse(player.removeDevelopmentCard(DevelopmentCard.ROAD_BUILDING));
        player.addDevelopmentCard(DevelopmentCard.ROAD_BUILDING);
        assertTrue(player.removeDevelopmentCard(DevelopmentCard.ROAD_BUILDING));
    }

    // tests for port methods
    // test that can add ports, and multiple, and returns true when added successfully
    @Test
    public void canAddPorts() {
        HashSet<Resource> result = new HashSet<>();
        assertEquals(result, player.getPorts());
        assertTrue(player.addPort(Resource.WOOD));
        result.add(Resource.WOOD);
        assertEquals(result, player.getPorts());
        assertTrue(player.addPort(Resource.MISC));
        result.add(Resource.MISC);
        assertEquals(result, player.getPorts());
    }

    // tests that returns false when already had
    @Test
    public void getPortsReturnsFalseWhenAlreadyHave() {
        assertTrue(player.addPort(Resource.WOOD));
        assertFalse(player.addPort(Resource.WOOD));
        assertTrue(player.addPort(Resource.MISC));
        assertFalse(player.addPort(Resource.WOOD));
        assertFalse(player.addPort(Resource.MISC));
    }

}
