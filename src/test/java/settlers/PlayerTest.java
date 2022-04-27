package settlers;

import org.junit.jupiter.api.Test;
import settlers.card.Resource;

import java.util.HashMap;

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
        assertEquals(new HashMap<Resource, Integer>(), player.getResources());
        assertFalse(player.hasMoreThan7Cards());
    }

    // make sure that can return when we add one card
    @Test
    public void resourceWorksOne() {
        player.addResource(Resource.WOOD);
        HashMap<Resource, Integer> result = new HashMap<>();
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


}
