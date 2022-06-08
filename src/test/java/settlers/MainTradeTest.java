package settlers;

import org.junit.jupiter.api.Test;
import settlers.card.Resource;
import settlers.gui.GUIMainDummyImpl;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MainTradeTest {

    private final MainImpl main;
    private final Player player;

    public MainTradeTest() {
        main = new MainImpl(4, new GUIMainDummyImpl());
        player = main.getPlayers().get(0);
    }

    private void givePlayerResources(Resource resource, int resourceNumber) {
        for (int i = 0; i < resourceNumber; i++) {
            player.addResource(resource);
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

    // I feel like I am missing something
}
