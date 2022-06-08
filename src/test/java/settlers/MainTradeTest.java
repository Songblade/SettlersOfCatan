package settlers;

import org.junit.jupiter.api.Test;
import settlers.card.Resource;
import settlers.gui.GUIMainDummyImpl;

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
}
