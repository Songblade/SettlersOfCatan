package settlers;

import org.junit.jupiter.api.Test;
import settlers.card.Resource;
import settlers.gui.GUIMainDummyImpl;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MainTradeTest {

    private final MainImpl main;
    private final Player player;
    private final Player player2;

    public MainTradeTest() {
        main = new MainImpl(4, new GUIMainDummyImpl());
        player = main.getPlayers().get(0);
        player2 = main.getPlayers().get(2);
    }

    private void givePlayerResources(Resource resource, int resourceNumber) {
        giveResources(player, resource, resourceNumber);
    }

    private void giveResources(Player playerGiven, Resource resource, int resourceNumber) {
        for (int i = 0; i < resourceNumber; i++) {
            playerGiven.addResource(resource);
        }
    }

    // tests for canTrade
    // tests that returns true if 4 resources and no ports
    @Test
    public void canTradeTrue4() {
        givePlayerResources(Resource.WOOD, 4);
        main.setTurn(player, true);

        assertTrue(main.canTrade(player, Resource.WOOD));
    }

    // tests that returns true if 4 resources and ports for other resources (but not this)
    @Test
    public void canTradeTrue4WithOtherPorts() {
        givePlayerResources(Resource.WOOD, 4);
        player.addPort(Resource.BRICK);
        player.addPort(Resource.SHEEP);
        main.setTurn(player, true);

        assertTrue(main.canTrade(player, Resource.WOOD));
    }

    // tests that works for other resources
    @Test
    public void canTradeTrue4OtherResources() {
        givePlayerResources(Resource.ORE, 4);
        main.setTurn(player, true);

        assertTrue(main.canTrade(player, Resource.ORE));
    }

    // add test that works when more than the max number of resources
    @Test
    public void canTradeTrue4Has5() {
        givePlayerResources(Resource.WOOD, 5);
        main.setTurn(player, true);

        assertTrue(main.canTrade(player, Resource.WOOD));
    }

    // tests that returns false if 4 resources but only has 3
    @Test
    public void canTradeFalse4Has3() {
        givePlayerResources(Resource.WOOD, 3);
        main.setTurn(player, true);

        assertFalse(main.canTrade(player, Resource.WOOD));
    }

    // tests that returns true if 3 resources and has 3-1 port
    @Test
    public void canTradeTrue3() {
        givePlayerResources(Resource.WOOD, 3);
        player.addPort(Resource.MISC);
        main.setTurn(player, true);

        assertTrue(main.canTrade(player, Resource.WOOD));
    }

    // tests that returns false if has 3-1 port but not 3 resources
    @Test
    public void canTradeFalse3Has2() {
        givePlayerResources(Resource.WOOD, 2);
        player.addPort(Resource.MISC);
        main.setTurn(player, true);

        assertFalse(main.canTrade(player, Resource.WOOD));
    }

    // tests that returns true if 2 resources and has 2-1 port
    @Test
    public void canTradeTrue2() {
        givePlayerResources(Resource.WOOD, 2);
        player.addPort(Resource.WOOD);
        main.setTurn(player, true);

        assertTrue(main.canTrade(player, Resource.WOOD));
    }

    // tests that returns false if 2 port from other resource
    @Test
    public void canTradeFalse2WrongPort() {
        givePlayerResources(Resource.WOOD, 2);
        player.addPort(Resource.BRICK);
        main.setTurn(player, true);

        assertFalse(main.canTrade(player, Resource.WOOD));
    }

    // tests that returns false if only 1 resource
    @Test
    public void canTradeFalse2Has1() {
        givePlayerResources(Resource.WOOD, 1);
        player.addPort(Resource.WOOD);
        main.setTurn(player, true);

        assertFalse(main.canTrade(player, Resource.WOOD));
    }

    // tests that returns false if not the player's turn
    @Test
    public void canTradeFalseWrongTurn() {
        givePlayerResources(Resource.WOOD, 4);
        main.setTurn(main.getPlayers().get(1), true);

        assertFalse(main.canTrade(player, Resource.WOOD));
    }

    // tests for trade
    // tests that in a 4 for 1 trade, loses the 4 resources and gains the 1
    @Test
    public void tradeWorks4() {
        givePlayerResources(Resource.WOOD, 4);
        main.setTurn(player, true);

        main.trade(player, Resource.WOOD, Resource.ORE);

        Map<Resource, Integer> newResources = player.getResources();
        assertEquals(0, newResources.get(Resource.WOOD));
        assertEquals(1, newResources.get(Resource.ORE));
    }

    // same but for 3-1
    @Test
    public void tradeWorks3() {
        givePlayerResources(Resource.WOOD, 3);
        player.addPort(Resource.MISC);
        main.setTurn(player, true);

        main.trade(player, Resource.WOOD, Resource.ORE);

        Map<Resource, Integer> newResources = player.getResources();
        assertEquals(0, newResources.get(Resource.WOOD));
        assertEquals(1, newResources.get(Resource.ORE));
    }

    // same but for 2-1
    @Test
    public void tradeWorks2() {
        givePlayerResources(Resource.WOOD, 2);
        player.addPort(Resource.WOOD);
        main.setTurn(player, true);

        main.trade(player, Resource.WOOD, Resource.ORE);

        Map<Resource, Integer> newResources = player.getResources();
        assertEquals(0, newResources.get(Resource.WOOD));
        assertEquals(1, newResources.get(Resource.ORE));
    }

    // same but for another resource
    @Test
    public void tradeWorksOtherResource() {
        givePlayerResources(Resource.SHEEP, 4);
        main.setTurn(player, true);

        main.trade(player, Resource.SHEEP, Resource.BRICK);

        Map<Resource, Integer> newResources = player.getResources();
        assertEquals(0, newResources.get(Resource.SHEEP));
        assertEquals(1, newResources.get(Resource.BRICK));
    }

    // check that still has 1 left if started with 5 for giving
    @Test
    public void tradeWorks4When5Give() {
        givePlayerResources(Resource.WOOD, 5);
        main.setTurn(player, true);

        main.trade(player, Resource.WOOD, Resource.ORE);

        Map<Resource, Integer> newResources = player.getResources();
        assertEquals(1, newResources.get(Resource.WOOD));
        assertEquals(1, newResources.get(Resource.ORE));
    }

    // check that now has 2 if started with 1 for getting
    @Test
    public void tradeWorks4When1HasOfGiven() {
        givePlayerResources(Resource.WOOD, 4);
        player.addResource(Resource.ORE);
        main.setTurn(player, true);

        main.trade(player, Resource.WOOD, Resource.ORE);

        Map<Resource, Integer> newResources = player.getResources();
        assertEquals(0, newResources.get(Resource.WOOD));
        assertEquals(2, newResources.get(Resource.ORE));
    }

    // tests for canTrade for 2-player trade
    // tests that canTrade is true if this player has the 1 resource demanded
    @Test
    public void canTradeP2PTrue1Has1() {
        player.addResource(Resource.ORE);
        main.setTurn(player, true);

        Map<Resource, Integer> givings = new HashMap<>();
        givings.put(Resource.ORE, 1);
        assertTrue(main.canTrade(player, givings));
    }

    // tests true if more than 1 resource
    @Test
    public void canTradeP2PTrue1HasMore() {
        givePlayerResources(Resource.ORE, 2);
        player.addResource(Resource.WOOD);
        main.setTurn(player, true);

        Map<Resource, Integer> givings = new HashMap<>();
        givings.put(Resource.ORE, 1);
        assertTrue(main.canTrade(player, givings));
    }

    // tests false if needs 1 and has 0 or wrong
    @Test
    public void canTradeP2PFalse1Has0OrWrong() {
        main.setTurn(player, true);

        Map<Resource, Integer> givings = new HashMap<>();
        givings.put(Resource.ORE, 1);
        assertFalse(main.canTrade(player, givings));

        player.addResource(Resource.WOOD);

        assertFalse(main.canTrade(player, givings));
    }

    // tests true if demands 2 and has 2
    @Test
    public void canTradeP2PTrue2Has2() {
        givePlayerResources(Resource.ORE, 2);
        main.setTurn(player, true);

        Map<Resource, Integer> givings = new HashMap<>();
        givings.put(Resource.ORE, 2);
        assertTrue(main.canTrade(player, givings));
    }

    // tests false if demands 2 and has 1
    @Test
    public void canTradeP2PFalse2Has1() {
        givePlayerResources(Resource.ORE, 1);
        player.addResource(Resource.WOOD);
        main.setTurn(player, true);

        Map<Resource, Integer> givings = new HashMap<>();
        givings.put(Resource.ORE, 2);
        assertFalse(main.canTrade(player, givings));
    }

    // tests true if demands 1 and 1 and has both
    @Test
    public void canTradeP2PTrue1And1HasBoth() {
        givePlayerResources(Resource.ORE, 1);
        player.addResource(Resource.WOOD);
        main.setTurn(player, true);

        Map<Resource, Integer> givings = new HashMap<>();
        givings.put(Resource.ORE, 1);
        givings.put(Resource.WOOD, 1);
        assertTrue(main.canTrade(player, givings));
    }

    // tests true if demands 2 and 1 and has more
    @Test
    public void canTradeP2PTrue2And1HasMore() {
        givePlayerResources(Resource.ORE, 2);
        givePlayerResources(Resource.WOOD, 2);
        player.addResource(Resource.BRICK);
        main.setTurn(player, true);

        Map<Resource, Integer> givings = new HashMap<>();
        givings.put(Resource.ORE, 2);
        givings.put(Resource.WOOD, 1);
        assertTrue(main.canTrade(player, givings));
    }

    // tests false if only has 1 of the resources
    @Test
    public void canTradeP2PFalse1And1Has1() {
        givePlayerResources(Resource.ORE, 1);
        main.setTurn(player, true);

        Map<Resource, Integer> givings = new HashMap<>();
        givings.put(Resource.ORE, 1);
        givings.put(Resource.WOOD, 1);
        assertFalse(main.canTrade(player, givings));
    }

    // tests false if has both, but 1 resource not enough
    @Test
    public void canTradeP2PTrue2And1Has1And1() {
        givePlayerResources(Resource.ORE, 1);
        givePlayerResources(Resource.WOOD, 2);
        player.addResource(Resource.BRICK);
        main.setTurn(player, true);

        Map<Resource, Integer> givings = new HashMap<>();
        givings.put(Resource.ORE, 2);
        givings.put(Resource.WOOD, 1);
        assertFalse(main.canTrade(player, givings));
    }

    // tests true if has resources but not their turn
    @Test
    public void canTradeP2PTrueWrongTurn() {
        player.addResource(Resource.ORE);
        main.setTurn(player2, true);

        Map<Resource, Integer> givings = new HashMap<>();
        givings.put(Resource.ORE, 1);
        assertTrue(main.canTrade(player, givings));
    }

    // tests for trade p2p
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

    // tests that if each give 1, they now each have each other's stuff
    @Test
    public void tradeP2PWorks1For1() {
        player.addResource(Resource.WOOD);
        player2.addResource(Resource.ORE);

        Map<Resource, Integer> exchange = new HashMap<>();
        exchange.put(Resource.WOOD, -1);
        exchange.put(Resource.ORE, 1);

        main.trade(player, exchange, player2);

        Map<Resource, Integer> result0 = emptyResourceMap();
        result0.put(Resource.ORE, 1);

        Map<Resource, Integer> result2 = emptyResourceMap();
        result2.put(Resource.WOOD, 1);

        assertEquals(result0, player.getResources());
        assertEquals(result2, player2.getResources());
    }

    // tests that whatever is given is in addition to what they have before
    @Test
    public void tradeP2PIsInAdditionalToPreviousResources() {
        givePlayerResources(Resource.WOOD, 2);
        givePlayerResources(Resource.ORE, 2);
        givePlayerResources(Resource.WHEAT, 2);

        giveResources(player2, Resource.WOOD, 1);
        giveResources(player2, Resource.ORE, 1);

        Map<Resource, Integer> exchange = new HashMap<>();
        exchange.put(Resource.WOOD, -1);
        exchange.put(Resource.ORE, 1);

        main.trade(player, exchange, player2);

        Map<Resource, Integer> result0 = emptyResourceMap();
        result0.put(Resource.WOOD, 1);
        result0.put(Resource.ORE, 3);
        result0.put(Resource.WHEAT, 2);

        Map<Resource, Integer> result2 = emptyResourceMap();
        result2.put(Resource.WOOD, 2);

        assertEquals(result0, player.getResources());
        assertEquals(result2, player2.getResources());
    }

    // tests that can trade 2 for 1
    @Test
    public void tradeP2PWorks2For1() {
        givePlayerResources(Resource.WOOD, 2);
        player2.addResource(Resource.ORE);

        Map<Resource, Integer> exchange = new HashMap<>();
        exchange.put(Resource.WOOD, -2);
        exchange.put(Resource.ORE, 1);

        main.trade(player, exchange, player2);

        Map<Resource, Integer> result0 = emptyResourceMap();
        result0.put(Resource.ORE, 1);

        Map<Resource, Integer> result2 = emptyResourceMap();
        result2.put(Resource.WOOD, 2);

        assertEquals(result0, player.getResources());
        assertEquals(result2, player2.getResources());
    }

    // tests that can trade 2 for 2
    @Test
    public void tradeP2PWorks2For2() {
        givePlayerResources(Resource.WOOD, 2);
        giveResources(player2, Resource.ORE, 2);

        Map<Resource, Integer> exchange = new HashMap<>();
        exchange.put(Resource.WOOD, -2);
        exchange.put(Resource.ORE, 2);

        main.trade(player, exchange, player2);

        Map<Resource, Integer> result0 = emptyResourceMap();
        result0.put(Resource.ORE, 2);

        Map<Resource, Integer> result2 = emptyResourceMap();
        result2.put(Resource.WOOD, 2);

        assertEquals(result0, player.getResources());
        assertEquals(result2, player2.getResources());
    }

    // tests that can trade 1 and 1 for 1
    @Test
    public void tradeP2PWorks1And1For1() {
        givePlayerResources(Resource.WOOD, 1);
        player.addResource(Resource.BRICK);
        player2.addResource(Resource.ORE);

        Map<Resource, Integer> exchange = new HashMap<>();
        exchange.put(Resource.WOOD, -1);
        exchange.put(Resource.BRICK, -1);
        exchange.put(Resource.ORE, 1);

        main.trade(player, exchange, player2);

        Map<Resource, Integer> result0 = emptyResourceMap();
        result0.put(Resource.ORE, 1);

        Map<Resource, Integer> result2 = emptyResourceMap();
        result2.put(Resource.WOOD, 1);
        result2.put(Resource.BRICK, 1);

        assertEquals(result0, player.getResources());
        assertEquals(result2, player2.getResources());
    }

    // tests that can trade 2 and 1 for 1 and 1
    @Test
    public void tradeP2PWorks2And1For1And1() {
        givePlayerResources(Resource.WOOD, 2);
        player.addResource(Resource.BRICK);
        player2.addResource(Resource.ORE);
        player2.addResource(Resource.WHEAT);

        Map<Resource, Integer> exchange = new HashMap<>();
        exchange.put(Resource.WOOD, -2);
        exchange.put(Resource.BRICK, -1);
        exchange.put(Resource.ORE, 1);
        exchange.put(Resource.WHEAT, 1);

        main.trade(player, exchange, player2);

        Map<Resource, Integer> result0 = emptyResourceMap();
        result0.put(Resource.ORE, 1);
        result0.put(Resource.WHEAT, 1);

        Map<Resource, Integer> result2 = emptyResourceMap();
        result2.put(Resource.WOOD, 2);
        result2.put(Resource.BRICK, 1);

        assertEquals(result0, player.getResources());
        assertEquals(result2, player2.getResources());
    }

}
