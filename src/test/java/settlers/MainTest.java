package settlers;

import org.junit.jupiter.api.Test;
import settlers.card.Resource;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    private Main main;

    public MainTest() {
        main = new MainImpl(4);
    }

    // start by testing playerElementsFor
    // test that works if player has exactly right elements for every project, road
    // and that doesn't work if player has fewer
    @Test
    public void canBuildRoad() {
        Player player = new PlayerImpl();
        assertFalse(main.playerElementsFor(player, Building.ROAD));
        player.addResource(Resource.WOOD);
        assertFalse(main.playerElementsFor(player, Building.ROAD));
        player.addResource(Resource.BRICK);
        assertTrue(main.playerElementsFor(player, Building.ROAD));
    }

    // settlement
    @Test
    public void canBuildSettlement() {
        Player player = new PlayerImpl();
        assertFalse(main.playerElementsFor(player, Building.SETTLEMENT));
        player.addResource(Resource.WOOD);
        assertFalse(main.playerElementsFor(player, Building.SETTLEMENT));
        player.addResource(Resource.BRICK);
        assertFalse(main.playerElementsFor(player, Building.SETTLEMENT));
        player.addResource(Resource.WHEAT);
        assertFalse(main.playerElementsFor(player, Building.SETTLEMENT));
        player.addResource(Resource.SHEEP);
        assertTrue(main.playerElementsFor(player, Building.SETTLEMENT));
    }

    // city
    @Test
    public void canBuildCity() {
        Player player = new PlayerImpl();
        assertFalse(main.playerElementsFor(player, Building.CITY));
        player.addResource(Resource.WHEAT);
        assertFalse(main.playerElementsFor(player, Building.CITY));
        player.addResource(Resource.WHEAT);
        assertFalse(main.playerElementsFor(player, Building.CITY));
        player.addResource(Resource.ORE);
        assertFalse(main.playerElementsFor(player, Building.CITY));
        player.addResource(Resource.ORE);
        assertFalse(main.playerElementsFor(player, Building.CITY));
        player.addResource(Resource.ORE);
        assertTrue(main.playerElementsFor(player, Building.CITY));
    }

    // development card
    @Test
    public void canBuyVelly() {
        Player player = new PlayerImpl();
        assertFalse(main.playerElementsFor(player, Building.DEVELOPMENT_CARD));
        player.addResource(Resource.WHEAT);
        assertFalse(main.playerElementsFor(player, Building.DEVELOPMENT_CARD));
        player.addResource(Resource.SHEEP);
        assertFalse(main.playerElementsFor(player, Building.DEVELOPMENT_CARD));
        player.addResource(Resource.ORE);
        assertTrue(main.playerElementsFor(player, Building.DEVELOPMENT_CARD));
    }

    // test that works if player has more than necessary
    @Test
    public void worksIfMoreThanNecessary() {
        Player player = new PlayerImpl();
        player.addResource(Resource.WOOD);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.WOOD);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.WHEAT);
        player.addResource(Resource.SHEEP);
        player.addResource(Resource.ORE);
        assertTrue(main.playerElementsFor(player, Building.ROAD));
    }

    //

}
