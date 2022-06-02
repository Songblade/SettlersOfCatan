package settlers;

import org.junit.jupiter.api.Test;
import settlers.board.*;
import settlers.card.Resource;
import settlers.gui.GUIMainDummyImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    private final MainImpl main;

    public MainTest() {
        main = new MainImpl(4, new GUIMainDummyImpl());
    }

    // start by testing playerCanBuild
    // test that works if player has exactly right elements for every project, road
    // and that doesn't work if player has fewer
    @Test
    public void canBuildRoad() {
        Player player = new PlayerImpl();
        main.buildRoad(player, main.getBoard().getVertices()[0].getEdges()[1]);
        assertFalse(main.playerCanBuild(player, Building.ROAD));

        player.addResource(Resource.WOOD);
        assertFalse(main.playerCanBuild(player, Building.ROAD));

        player.addResource(Resource.BRICK);
        assertTrue(main.playerCanBuild(player, Building.ROAD));
    }

    // settlement
    @Test
    public void canBuildSettlement() {
        Player player = new PlayerImpl();
        assertFalse(main.playerCanBuild(player, Building.SETTLEMENT));
        player.addResource(Resource.WOOD);
        assertFalse(main.playerCanBuild(player, Building.SETTLEMENT));
        player.addResource(Resource.BRICK);
        assertFalse(main.playerCanBuild(player, Building.SETTLEMENT));
        player.addResource(Resource.WHEAT);
        assertFalse(main.playerCanBuild(player, Building.SETTLEMENT));
        player.addResource(Resource.SHEEP);
        assertTrue(main.playerCanBuild(player, Building.SETTLEMENT));
    }

    // city
    @Test
    public void canBuildCity() {
        Player player = new PlayerImpl();
        main.buildSettlement(player, main.getBoard().getVertices()[0]);
        assertFalse(main.playerCanBuild(player, Building.CITY));

        player.addResource(Resource.WHEAT);
        assertFalse(main.playerCanBuild(player, Building.CITY));
        player.addResource(Resource.WHEAT);
        assertFalse(main.playerCanBuild(player, Building.CITY));
        player.addResource(Resource.ORE);
        assertFalse(main.playerCanBuild(player, Building.CITY));
        player.addResource(Resource.ORE);
        assertFalse(main.playerCanBuild(player, Building.CITY));
        player.addResource(Resource.ORE);
        assertTrue(main.playerCanBuild(player, Building.CITY));
    }

    // development card
    @Test
    public void canBuyVelly() {
        Player player = new PlayerImpl();
        assertFalse(main.playerCanBuild(player, Building.DEVELOPMENT_CARD));
        player.addResource(Resource.WHEAT);
        assertFalse(main.playerCanBuild(player, Building.DEVELOPMENT_CARD));
        player.addResource(Resource.SHEEP);
        assertFalse(main.playerCanBuild(player, Building.DEVELOPMENT_CARD));
        player.addResource(Resource.ORE);
        assertTrue(main.playerCanBuild(player, Building.DEVELOPMENT_CARD));
    }

    // test that works if player has more than necessary
    @Test
    public void worksIfMoreThanNecessary() {
        Player player = new PlayerImpl();
        main.buildRoad(player, main.getBoard().getVertices()[0].getEdges()[1]);

        player.addResource(Resource.WOOD);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.WOOD);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.WHEAT);
        player.addResource(Resource.SHEEP);
        player.addResource(Resource.ORE);
        assertTrue(main.playerCanBuild(player, Building.ROAD));
    }

    // test that if a player has 5 settlements, it can't build another. I am just assuming it will work equally for the others
        // even though the player has the right resources
    @Test
    public void cantBuildSettlementIfHasMax() {
        Player player = new PlayerImpl();
        player.addResource(Resource.WOOD);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.WHEAT);
        player.addResource(Resource.SHEEP);

        Vertex[] vertices = main.getBoard().getVertices();
        for (int i = 0; i < 50; i += 10) { // builds 5, spread out settlements
            assertTrue(main.playerCanBuild(player, Building.SETTLEMENT));
            main.buildSettlement(player, vertices[i]); // this is setup, so no resource cost
        }
        assertFalse(main.playerCanBuild(player, Building.SETTLEMENT));
    }

    // getAvailableSettlementSpots
    // test that includes every vertex if it's the setup phase and every vertex is unoccupied
    @Test
    public void includesEveryVertexSetupSettlement() {
        Vertex[] verticesCopy = main.getBoard().getVertices();
        List<Player> playerCopy = main.getPlayers();
        assertEquals(new HashSet<>(Arrays.asList(verticesCopy)), main.getAvailableSettlementSpots(playerCopy.get(0)));
    }

    // test that includes every vertex but 4 if setup phase and one player has a settlement
    @Test
    public void excludesVerticesOtherPlayersHaveSetupSettlement() {
        List<Vertex> verticesCopy = new ArrayList<>(Arrays.asList(main.getBoard().getVertices()));
        List<Player> playerCopy = main.getPlayers();
        Vertex settlement = verticesCopy.get(4);
        addPlayer(settlement, playerCopy.get(1));
        verticesCopy.remove(settlement);
        verticesCopy.removeAll(Arrays.asList(settlement.getAdjacentVertices()));
        assertEquals(new HashSet<>(verticesCopy), main.getAvailableSettlementSpots(playerCopy.get(0)));
    }

    // same as above, but it is your settlement
    @Test
    public void excludesVerticesThisPlayerHasSetupSettlement() {
        List<Vertex> verticesCopy = new ArrayList<>(Arrays.asList(main.getBoard().getVertices()));
        List<Player> playerCopy = main.getPlayers();
        Vertex settlement = verticesCopy.get(4);
        addPlayer(settlement, playerCopy.get(0)); // give this player a settlement
        verticesCopy.remove(settlement);
        verticesCopy.removeAll(Arrays.asList(settlement.getAdjacentVertices()));
        assertEquals(new HashSet<>(verticesCopy), main.getAvailableSettlementSpots(playerCopy.get(0)));
    }

    // test that if 2 settlements 2 apart, only 7 spots are removed
    @Test
    public void setupSettlementWorksWhen2CloseSettlements() {
        List<Vertex> verticesCopy = new ArrayList<>(Arrays.asList(main.getBoard().getVertices()));
        Hex verticesSource = main.getBoard().getHexes()[9];
        List<Player> playerCopy = main.getPlayers();
        Vertex settlement0 = verticesSource.getVertices()[0];
        addPlayer(settlement0, playerCopy.get(0)); // give this player a settlement
        verticesCopy.remove(settlement0);
        verticesCopy.removeAll(Arrays.asList(settlement0.getAdjacentVertices()));
        Vertex settlement1 = verticesSource.getVertices()[2];
        addPlayer(settlement1, playerCopy.get(1)); // give another player a settlement
        verticesCopy.remove(settlement1);
        verticesCopy.removeAll(Arrays.asList(settlement1.getAdjacentVertices()));
        assertEquals(new HashSet<>(verticesCopy), main.getAvailableSettlementSpots(playerCopy.get(0)));
    }

    // test that during game phase, only includes spots that are next to your roads
    @Test
    public void gameSettlementOnlyNextToRoads() {
        List<Vertex> verticesCopy = new ArrayList<>();
        Hex verticesSource = main.getBoard().getHexes()[9];
        List<Player> playerCopy = main.getPlayers();
        main.setPhase(true);

        Vertex firstVertex = verticesSource.getVertices()[0];
        Edge firstRoad = firstVertex.getEdges()[0];
        firstRoad.setPlayer(playerCopy.get(0));
        verticesCopy.add(firstVertex);
        verticesCopy.add(firstVertex.getAdjacentVertices()[0]);

        Vertex secondVertex = verticesSource.getVertices()[3];
        Edge secondRoad = secondVertex.getEdges()[0];
        secondRoad.setPlayer(playerCopy.get(0));
        verticesCopy.add(secondVertex);
        verticesCopy.add(secondVertex.getAdjacentVertices()[0]);

        assertEquals(new HashSet<>(verticesCopy), main.getAvailableSettlementSpots(playerCopy.get(0)));
    }

    /**
     * Builds a settlement properly
     * @param vertex where settlement is built
     * @param player where settlement is updated
     */
    private void addPlayer(Vertex vertex, Player player) {
        vertex.setPlayer(player);
        player.addSettlement(vertex);
        main.getBoard().removeSettlement(vertex);
    }

    /**
     * Builds a road properly
     * @param edge where road is built
     * @param player where road is updated
     */
    private void addPlayer(Edge edge, Player player) {
        edge.setPlayer(player);
        player.addRoad(edge);
        main.getBoard().removeRoad(edge);
    }

    /**
     * Upgrades a settlement to a city properly
     * @param vertex where settlement is built
     * @param player where settlement is updated
     */
    private void upgradePlayer(Vertex vertex, Player player) {
        vertex.makeCity();
        player.upgradeSettlement(vertex);
    }

    // and excludes spots that have settlements, yours or not
    // test that excludes spots adjacent to settlements
    @Test
    public void gameSettlementNotInOrNearSettlement() {
        Hex verticesSource = main.getBoard().getHexes()[9];
        List<Player> playerCopy = main.getPlayers();
        main.setPhase(true);

        Vertex firstVertex = verticesSource.getVertices()[0];
        Edge firstRoad = firstVertex.getEdges()[0];
        addPlayer(firstRoad, playerCopy.get(0));
        addPlayer(firstVertex, playerCopy.get(0));

        Vertex secondVertex = verticesSource.getVertices()[3];
        Edge secondRoad = secondVertex.getEdges()[0];
        addPlayer(secondRoad, playerCopy.get(1));
        addPlayer(secondVertex, playerCopy.get(1));

        // should be an empty set, since all the roads the player has are next to settlements
        assertEquals(new HashSet<>(), main.getAvailableSettlementSpots(playerCopy.get(0)));
    }

    // getAvailableRoadSpots
    // I will assume that this method is not being used during the setup phase, as there, I just need to
        // give the three roads next to the settlement, which are guaranteed to be good
        // that also means that I don't have to check things next to settlements, only next to roads
    // test that includes the edges next to your roads and settlements
    @Test
    public void roadNextToRoadAndSettlement() {
        Hex verticesSource = main.getBoard().getHexes()[9];
        List<Player> playerCopy = main.getPlayers();
        main.setPhase(true);

        Vertex firstVertex = verticesSource.getVertices()[0];
        Edge firstRoad = firstVertex.getEdges()[0];
        addPlayer(firstRoad, playerCopy.get(0));
        addPlayer(firstVertex, playerCopy.get(0));

        // must test that has the 4 locations on each side of the road
        Set<Edge> result = new HashSet<>();
        result.add(firstVertex.getEdges()[1]);
        result.add(firstVertex.getEdges()[2]);
        result.add(firstVertex.getAdjacentVertices()[0].getEdges()[0]);
        result.add(firstVertex.getAdjacentVertices()[0].getEdges()[2]);
        assertEquals(result, main.getAvailableRoadSpots(playerCopy.get(0)));
    }

    // test that does not include edges that have a road already, whether yours or someone else's
    // test that does include edges next to roads but not settlements
    @Test
    public void roadNextToOtherPlayersRoad() {
        Hex verticesSource = main.getBoard().getHexes()[9];
        List<Player> playerCopy = main.getPlayers();
        main.setPhase(true);

        Vertex firstVertex = verticesSource.getVertices()[0];
        Edge firstRoad = firstVertex.getEdges()[0];
        addPlayer(firstRoad, playerCopy.get(0));
        addPlayer(firstVertex.getEdges()[1], playerCopy.get(1)); // give the other player an adjacent road

        // must test that has the 3 locations on each side of the road, ignoring other player
        Set<Edge> result = new HashSet<>();
        result.add(firstVertex.getEdges()[2]);
        result.add(firstVertex.getAdjacentVertices()[0].getEdges()[0]);
        result.add(firstVertex.getAdjacentVertices()[0].getEdges()[2]);
        assertEquals(result, main.getAvailableRoadSpots(playerCopy.get(0)));
    }

    // test that includes edges where it goes through someone else's settlement
    @Test
    public void roadNextToOtherPlayersSettlement() {
        Hex verticesSource = main.getBoard().getHexes()[9];
        List<Player> playerCopy = main.getPlayers();
        main.setPhase(true);

        Vertex firstVertex = verticesSource.getVertices()[0];
        Edge firstRoad = firstVertex.getEdges()[0];
        addPlayer(firstRoad, playerCopy.get(0));
        addPlayer(firstVertex, playerCopy.get(1));

        // must test that has the 4 locations on each side of the road
        Set<Edge> result = new HashSet<>();
        result.add(firstVertex.getEdges()[1]);
        result.add(firstVertex.getEdges()[2]);
        result.add(firstVertex.getAdjacentVertices()[0].getEdges()[0]);
        result.add(firstVertex.getAdjacentVertices()[0].getEdges()[2]);
        assertEquals(result, main.getAvailableRoadSpots(playerCopy.get(0)));
    }

    // test that works when the edge is on the edge
    @Test
    public void roadEdgeOfBoard() {
        Vertex firstVertex = main.getBoard().getVertices()[0];
        List<Player> playerCopy = main.getPlayers();
        main.setPhase(true);

        Edge firstRoad = firstVertex.getEdges()[2];
        addPlayer(firstRoad, playerCopy.get(0));
        addPlayer(firstVertex, playerCopy.get(1));

        // must test that has the 2 locations on each side of the road, the ones that aren't ocean
        Set<Edge> result = new HashSet<>();
        result.add(firstVertex.getEdges()[1]);
        result.add(firstVertex.getAdjacentVertices()[2].getEdges()[2]);
        assertEquals(result, main.getAvailableRoadSpots(playerCopy.get(0)));
    }

    // buildSettlement

    private Player setUpPlayer(Building building) {
        Player player = main.getPlayers().get(0);
        for (Resource resource : building.getResources().keySet()) {
            for (int i = 0; i < building.getResources().get(resource); i++) {
                player.addResource(resource);
            }
        }
        return player;
    }

    // Make sure that the settlement actually gets built
    // Make sure can no longer build here or adjacent to here
    @Test
    public void testSettlementIsBuilt() {
        Player player = setUpPlayer(Building.SETTLEMENT);
        Vertex vertex = main.getBoard().getVertices()[4];
        main.setPhase(true);

        main.buildSettlement(player, vertex);

        assertEquals(player, vertex.getPlayer());
        assertFalse(vertex.isCity());
        assertTrue(player.getSettlements().contains(vertex));
        assertFalse(player.getCities().contains(vertex));

        // this part makes sure that the vertex is no longer available for this person or anyone else to build
        Set<Vertex> result = main.getAvailableSettlementSpots(player);
        assertFalse(result.contains(vertex));
        assertFalse(result.contains(vertex.getAdjacentVertices()[0]));

        result = main.getAvailableSettlementSpots(main.getPlayers().get(1));
        assertFalse(result.contains(vertex));
        assertFalse(result.contains(vertex.getAdjacentVertices()[0]));
    }

    // Make sure that if there is a port, the player has the port now
    @Test
    public void testPlayerHasPort() {
        Player player = setUpPlayer(Building.SETTLEMENT);
        Vertex vertex = main.getBoard().getHexes()[0].getVertices()[0]; // a vertex with a port
        main.setPhase(true);

        main.buildSettlement(player, vertex);

        assertTrue(player.getPorts().contains(vertex.getPort())); // tests that the player has that port's
            // resource now in its port
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

    // Make sure that the player's resources are decreased if game phase
    @Test
    public void testPlayerLosesAllResourcesSettlement() {
        Player player = setUpPlayer(Building.SETTLEMENT);
        Vertex vertex = main.getBoard().getHexes()[0].getVertices()[0]; // a vertex with a port
        main.setPhase(true);

        main.buildSettlement(player, vertex);

        assertEquals(emptyResourceMap(), player.getResources());
    }

    // Make sure that the player's resources are decreased to above 0 if has extra resources
    @Test
    public void testPlayerLosesSomeResourcesSettlement() {
        Player player = setUpPlayer(Building.SETTLEMENT);
        player.addResource(Resource.WOOD);
        player.addResource(Resource.ORE);

        Vertex vertex = main.getBoard().getHexes()[0].getVertices()[0];
        main.setPhase(true);

        main.buildSettlement(player, vertex);

        HashMap<Resource, Integer> result = emptyResourceMap();
        result.put(Resource.WOOD, 1);
        result.put(Resource.ORE, 1);

        assertEquals(result, player.getResources());
    }

    // Make sure player resources not decreased if setup
    @Test
    public void testPlayerLosesNoResourcesSettlementSetup() {
        Player player = setUpPlayer(Building.SETTLEMENT);
        Vertex vertex = main.getBoard().getHexes()[0].getVertices()[0];

        main.buildSettlement(player, vertex);

        HashMap<Resource, Integer> result = emptyResourceMap();
        result.put(Resource.WOOD, 1);
        result.put(Resource.BRICK, 1);
        result.put(Resource.WHEAT, 1);
        result.put(Resource.SHEEP, 1);

        assertEquals(result, player.getResources());
    }

    // CRITICAL: Once you implement the method that checks if you can build a city, add a test that
        // building a settlement lets you upgrade it to a city
    @Test
    public void buildSettlementLetsBuildCity() {
        Player player = setUpPlayer(Building.SETTLEMENT);
        Vertex vertex = main.getBoard().getHexes()[0].getVertices()[0]; // a vertex with a port
        main.setPhase(true);

        main.buildSettlement(player, vertex);

        HashSet<Vertex> result = new HashSet<>();
        result.add(vertex);

        assertEquals(result, main.getAvailableCitySpots(player));
    }

    // I will not implement any exceptions, to aid in testing

    // buildRoad
    // Make sure that the road is actually built now
    // Make sure can no longer build here
    @Test
    public void testRoadIsBuilt() {
        Player player = setUpPlayer(Building.ROAD);
        Vertex vertex = main.getBoard().getVertices()[4];
        Edge edge = vertex.getEdges()[0];
        main.setPhase(true);

        main.buildRoad(player, edge);

        assertEquals(player, edge.getPlayer());
        assertTrue(player.getRoads().contains(edge));

        // this part makes sure that the edge is no longer available for this person or anyone else
        Set<Edge> result = main.getAvailableRoadSpots(player);
        assertFalse(result.contains(edge));

        result = main.getAvailableRoadSpots(main.getPlayers().get(1));
        assertFalse(result.contains(edge));
    }

    // Make sure the player's resources are decreased if game phase
    @Test
    public void testRoadBringsResourcesTo0() {
        Player player = setUpPlayer(Building.ROAD);
        Vertex vertex = main.getBoard().getVertices()[4];
        Edge edge = vertex.getEdges()[0];
        main.setPhase(true);

        main.buildRoad(player, edge);

        assertEquals(emptyResourceMap(), player.getResources());
    }

    // Make sure not to 0 if has more than that
    @Test
    public void testRoadBringsResourcesNotTo0() {
        Player player = setUpPlayer(Building.ROAD);
        player.addResource(Resource.WOOD);
        player.addResource(Resource.ORE);

        Vertex vertex = main.getBoard().getVertices()[4];
        Edge edge = vertex.getEdges()[0];
        main.setPhase(true);

        main.buildRoad(player, edge);

        HashMap<Resource, Integer> result = emptyResourceMap();
        result.put(Resource.WOOD, 1);
        result.put(Resource.ORE, 1);

        assertEquals(result, player.getResources());
    }

    // Make sure player resources not decreased if setup
    @Test
    public void testRoadIgnoresResourcesSetup() {
        Player player = setUpPlayer(Building.ROAD);

        Vertex vertex = main.getBoard().getVertices()[4];
        Edge edge = vertex.getEdges()[0];

        main.buildRoad(player, edge);

        HashMap<Resource, Integer> result = emptyResourceMap();
        result.put(Resource.WOOD, 1);
        result.put(Resource.BRICK, 1);

        assertEquals(result, player.getResources());
    }

    // Make sure can now build beyond here, both road and settlement
    @Test
    public void testCanBuildRoadBeyond() {
        Player player = setUpPlayer(Building.ROAD);
        Vertex vertex = main.getBoard().getVertices()[4];
        Edge edge = vertex.getEdges()[0];
        main.setPhase(true);

        main.buildRoad(player, edge);

        Set<Edge> result = main.getAvailableRoadSpots(player);
        assertTrue(result.contains(vertex.getAdjacentVertices()[0].getEdges()[2]));

        Set<Vertex> vertexResult = main.getAvailableSettlementSpots(player);
        assertTrue(vertexResult.contains(vertex.getAdjacentVertices()[0]));
    }

    // tests for getAvailableCitySpots
    // test that a player with 2 settlements can build a settlement in both places, and nowhere else
    @Test
    public void availableCitySpotsGivesSettlements() {
        List<Vertex> verticesCopy = new ArrayList<>(Arrays.asList(main.getBoard().getVertices()));
        List<Player> playerCopy = main.getPlayers();

        Vertex vertex0 = verticesCopy.get(4);
        Vertex vertex1 = verticesCopy.get(5);
        Vertex trickVertex = verticesCopy.get(6);
        addPlayer(vertex0, playerCopy.get(0)); // give this player a settlement
        addPlayer(vertex1, playerCopy.get(0)); // give this player a settlement
        addPlayer(trickVertex, playerCopy.get(1)); // give another player a settlement, should be ignored

        HashSet<Vertex> result = new HashSet<>();
        result.add(vertex0);
        result.add(vertex1);
        assertEquals(result, main.getAvailableCitySpots(playerCopy.get(0)));
    }

    // tests that cities are not included in available spots
    @Test
    public void cantBuildCitiesOnCities() {
        List<Vertex> verticesCopy = new ArrayList<>(Arrays.asList(main.getBoard().getVertices()));
        List<Player> playerCopy = main.getPlayers();

        Vertex vertex0 = verticesCopy.get(4);
        Vertex vertex1 = verticesCopy.get(5);
        addPlayer(vertex0, playerCopy.get(0)); // give this player a settlement
        addPlayer(vertex1, playerCopy.get(0)); // give this player a settlement
        upgradePlayer(vertex0, playerCopy.get(0)); // make one settlement a city

        HashSet<Vertex> result = new HashSet<>();
        result.add(vertex1); // only include the regular settlement
        assertEquals(result, main.getAvailableCitySpots(playerCopy.get(0)));
    }

    // buildCity
    // test that the settlement is now considered a city, and is included in the player's city list
        // and not its settlement list
    @Test
    public void testCityIsBuilt() {
        Player player = setUpPlayer(Building.CITY);
        Vertex vertex = main.getBoard().getVertices()[4];
        main.buildSettlement(player, vertex); // in setup phase, so no resources used
        main.setPhase(true);

        main.buildCity(player, vertex);

        assertEquals(player, vertex.getPlayer());
        assertTrue(vertex.isCity());
        assertFalse(player.getSettlements().contains(vertex));
        assertTrue(player.getCities().contains(vertex));
    }

    // test that can't build another settlement or city here
    @Test
    public void testCantBuildSomethingElseHere() {
        Player player = setUpPlayer(Building.CITY);
        Vertex vertex = main.getBoard().getVertices()[4];
        main.buildSettlement(player, vertex); // in setup phase, so no resources used
        main.setPhase(true);

        main.buildCity(player, vertex);

        // this part makes sure that the vertex is no longer available for this person or anyone else to build
        Set<Vertex> result = main.getAvailableSettlementSpots(player);
        assertFalse(result.contains(vertex));
        assertFalse(result.contains(vertex.getAdjacentVertices()[0]));

        result = main.getAvailableSettlementSpots(main.getPlayers().get(1));
        assertFalse(result.contains(vertex));
        assertFalse(result.contains(vertex.getAdjacentVertices()[0]));

        // this person can't build another city here
        result = main.getAvailableCitySpots(main.getPlayers().get(0));
        assertFalse(result.contains(vertex));

        result = main.getAvailableCitySpots(main.getPlayers().get(1));
        assertFalse(result.contains(vertex));
    }

    // test that the player's resources were removed to 0
    @Test
    public void testCityResourcesTo0() {
        Player player = setUpPlayer(Building.CITY);
        Vertex vertex = main.getBoard().getVertices()[4];
        main.buildSettlement(player, vertex); // in setup phase, so no resources used
        main.setPhase(true);

        main.buildCity(player, vertex);

        assertEquals(emptyResourceMap(), player.getResources());
    }

    // test that if the player had other resources, it does not have 0 now
    @Test
    public void testCityResourcesToMoreThan0() {
        Player player = setUpPlayer(Building.CITY);
        player.addResource(Resource.WOOD);
        player.addResource(Resource.ORE);

        Vertex vertex = main.getBoard().getVertices()[4];
        main.buildSettlement(player, vertex); // in setup phase, so no resources used
        main.setPhase(true);

        main.buildCity(player, vertex);

        HashMap<Resource, Integer> result = emptyResourceMap();
        result.put(Resource.WOOD, 1);
        result.put(Resource.ORE, 1);

        assertEquals(result, player.getResources());
    }

    // tests for getAvailableThiefSpots
    // test that includes the right things
    @Test
    public void getAvailableThiefSpotsWorks() {
        Hex thiefIsHere = null;
        for (Hex hex : main.getBoard().getHexes()) {
            if (hex.getNumber() == 1) {
                thiefIsHere = hex;
                break;
            }
        }
        Set<Hex> result = new HashSet<>(Arrays.asList(main.getBoard().getHexes()));
        result.remove(thiefIsHere);
        assertEquals(result, main.getAvailableThiefSpots());
    }

    // test that includes the right things after the thief is moved
    // this is also a test for moveThief, that the thief is now moved
    @Test
    public void getAvailableThiefSpotsWorksTwice() {
        Player player = main.getPlayers().get(0);
        Hex thiefIsHere = main.getBoard().getHexes()[0];
        main.moveThief(player, thiefIsHere.getVertices()[0], thiefIsHere);
        Set<Hex> result = new HashSet<>(Arrays.asList(main.getBoard().getHexes()));
        result.remove(thiefIsHere);
        assertEquals(result, main.getAvailableThiefSpots());
    }

    // tests for moveThief
    // tests that this works even if the thief was already moved
    @Test
    public void moveThiefWorksTwice() {
        Player player = main.getPlayers().get(0);

        Hex thiefIsHere = main.getBoard().getHexes()[0];
        main.moveThief(player, thiefIsHere.getVertices()[0], thiefIsHere);
        thiefIsHere = main.getBoard().getHexes()[1];
        main.moveThief(player, thiefIsHere.getVertices()[0], thiefIsHere);

        Set<Hex> result = new HashSet<>(Arrays.asList(main.getBoard().getHexes()));
        result.remove(thiefIsHere);
        assertEquals(result, main.getAvailableThiefSpots());
    }

    // tests that the first player gets a resource, and second one loses it
    @Test
    public void moveThiefMovesResource() {
        Hex thiefIsHere = main.getBoard().getHexes()[0];

        Player badGuy = main.getPlayers().get(0);
        badGuy.addResource(Resource.WOOD);
        badGuy.addResource(Resource.ORE);

        Player victim = main.getPlayers().get(1);
        victim.addResource(Resource.BRICK);
        main.buildSettlement(victim, thiefIsHere.getVertices()[0]);

        main.moveThief(badGuy, thiefIsHere.getVertices()[0], thiefIsHere);


        HashMap<Resource, Integer> result = emptyResourceMap();
        result.put(Resource.WOOD, 1);
        result.put(Resource.ORE, 1);
        result.put(Resource.BRICK, 1);
        assertEquals(result, badGuy.getResources());
        assertEquals(emptyResourceMap(), victim.getResources());
    }

    // test that stealing works even if other player has many resources, using first with 7 and second with 8
    @Test
    public void moveThiefMovesResourceFromRich() {
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

        main.moveThief(badGuy, thiefIsHere.getVertices()[0], thiefIsHere);

        assertTrue(badGuy.hasMoreThan7Cards());
        assertFalse(victim.hasMoreThan7Cards());
    }

    // don't forget to add a test that you don't get resources from a robbed place
    // also don't forget to make sure that a player can't build something if that player has already built the max

    // now we are testing protected methods that are used in the main loop

    // getNumOfPlayers (note this is a static method)

    /**
     * Creates file for tests
     * @param fileContents the string to be put in the file
     * @return the file containing what I want
     * @throws IOException so I can use files
     */
    private FileInputStream testFileStream(String fileContents) throws IOException {
        File file = File.createTempFile("numPlayerInput", "txt");
        FileWriter writeFile = new FileWriter(file);
        writeFile.write(fileContents);
        writeFile.close();
        return new FileInputStream(file);
    }
    // test that if I just give a number that is good, it works
    @Test
    public void getNumOfPlayersWorksImmediately() throws IOException {
        assertEquals(3, MainImpl.getNumOfPlayers(testFileStream("3")));
    }

    // test that if I give a number with other stuff after it, it still works if there is a space
    @Test
    public void getNumOfPlayersWorksWhenGarbageAfterSpace() throws IOException {
        assertEquals(3, MainImpl.getNumOfPlayers(testFileStream("3 abcdef")));
    }

    // test that if I give something not a number, it will keep asking until I give a number
    @Test
    public void getNumOfPlayersContinuesUntilInt() throws IOException {
        assertEquals(2, MainImpl.getNumOfPlayers(testFileStream("food\ntwo\n2")));
    }

    // test that if I give a number less than 2, will keep asking until I give something big enough
    @Test
    public void getNumOfPlayersContinuesUntilBig() throws IOException {
        assertEquals(4, MainImpl.getNumOfPlayers(testFileStream("zero\n0\n1\n4")));
    }

    // test that if I give a number more than 4, will keep asking until I give something small enough
    @Test
    public void getNumOfPlayersContinuesUntilSmall() throws IOException {
        assertEquals(2, MainImpl.getNumOfPlayers(testFileStream("a million\n1000000\n10\n7\n6\n5\n2")));
    }

    // testing turnOrder
    // I will make 3 tests, one for 2 players, one for 3, and one for 4
    // in each, I will make the main, and test its turnOrder() 1000 times
    // each time, I will test that all the applicable numbers are there, and the length is correct
    // I will also make sure that I have at least 100 of each number going first overall
    // (that is probably the wrong number to check, but I haven't taken Stat yet)
    private void turnOrderTest(int playerNumber) {
        MainImpl main = new MainImpl(playerNumber, new GUIMainDummyImpl());
        int[] numberFirst = new int[playerNumber]; // how many times each number has been first
        for (int i = 0; i < 1000; i++) {
            List<Integer> turnOrder = main.turnOrder();
            assertEquals(playerNumber, turnOrder.size());
            numberFirst[turnOrder.get(0)]++;
            for (int j = 0; j < playerNumber; j++) {
                assertTrue(turnOrder.contains(j));
            }
        }
        for (int j = 0; j < playerNumber; j++) {
            assertTrue(numberFirst[j] > 100);
        }
    }

    @Test
    public void turnOrderTest2() {
        turnOrderTest(2);
    }

    @Test
    public void turnOrderTest3() {
        turnOrderTest(3);
    }

    @Test
    public void turnOrderTest4() {
        turnOrderTest(4);
    }

    // applyDice

    /**
     * Get one of a number's hexes
     * @param num I want the hex for, probably 1, 2, or 12
     * @return the array index of the hex with the number
     */
    private int findHexOfNum(int num) {
        Hex[] allHexes = main.getBoard().getHexes();
        for (int i = 0; i < allHexes.length; i++) {
            if (allHexes[i].getNumber() == num) {
                return i;
            }
        }
        return 0; // should never happen
    }

    /**
     * Gets both hexes for a number
     * @param num I want the hexes for, from 3-6 or 8-11
     * @return a list of the array indices of both hexes
     */
    private List<Integer> findHexOfNums(int num) {
        List<Integer> hexIndices = new ArrayList<>();
        Hex[] allHexes = main.getBoard().getHexes();
        for (int i = 0; i < allHexes.length; i++) {
            if (allHexes[i].getNumber() == num) {
                hexIndices.add(i);
            }
        }
        return hexIndices;
    }

    // test that if no one has any settlements, no one gets anything
    @Test
    public void noSettlementsNothingHappensDieRoll() {
        Hex[] hexes = main.getBoard().getHexes();
        int hexIndex = findHexOfNum(2);
        // builds a settlement on a hex that should not be bordering this one, I hope
        main.buildSettlement(main.getPlayers().get(0), hexes[(hexIndex + 2) % hexes.length].getVertices()[0]); // no resources, because setup phase
        main.applyDice(2);
        for (Player player : main.getPlayers()) { // make sure no players got any resources
            assertEquals(emptyResourceMap(), player.getResources());
        }
    }

    // test that if someone has a settlement by the hex, he gets the resource
    @Test
    public void dieRollOneSettlement() {
        Hex[] hexes = main.getBoard().getHexes();
        int hexIndex = findHexOfNum(2);
        // other player builds a settlement on a hex that should not be bordering this one, I hope
        main.buildSettlement(main.getPlayers().get(1), hexes[(hexIndex + 2) % hexes.length].getVertices()[1]);
        main.buildSettlement(main.getPlayers().get(0), hexes[hexIndex].getVertices()[0]); // no resources, because setup phase

        main.applyDice(2);

        HashMap<Resource, Integer> result = emptyResourceMap();
        result.put(hexes[hexIndex].getResource(), 1); // the 1 resource that the player gets

        for (Player player : main.getPlayers()) { // make sure no players got any resources
            if (player == main.getPlayers().get(0)) {
                assertEquals(result, player.getResources());
            } else {
                assertEquals(emptyResourceMap(), player.getResources());
            }
        }
    }

    // test that if two people have settlements by the hex, they each get the resource
    @Test
    public void dieRollTwoPeople() {
        Hex[] hexes = main.getBoard().getHexes();
        int hexIndex = findHexOfNum(2);
        // other player builds a settlement on a hex that should not be bordering this one, I hope
        main.buildSettlement(main.getPlayers().get(1), hexes[(hexIndex + 2) % hexes.length].getVertices()[1]);
        main.buildSettlement(main.getPlayers().get(0), hexes[hexIndex].getVertices()[0]); // no resources, because setup phase
        main.buildSettlement(main.getPlayers().get(2), hexes[hexIndex].getVertices()[2]);

        main.applyDice(2);

        HashMap<Resource, Integer> result = emptyResourceMap();
        result.put(hexes[hexIndex].getResource(), 1); // the 1 resource that each player gets

        for (Player player : main.getPlayers()) { // make sure no players got any resources
            if (player == main.getPlayers().get(0) || player == main.getPlayers().get(2)) {
                assertEquals(result, player.getResources());
            } else {
                assertEquals(emptyResourceMap(), player.getResources());
            }
        }
    }

    // test that if one person has 2 settlements by the hex, he gets the resource twice
    @Test
    public void dieRollTwoSettlements() {
        Hex[] hexes = main.getBoard().getHexes();
        int hexIndex = findHexOfNum(2);
        // other player builds a settlement on a hex that should not be bordering this one, I hope
        main.buildSettlement(main.getPlayers().get(1), hexes[(hexIndex + 2) % hexes.length].getVertices()[1]);
        main.buildSettlement(main.getPlayers().get(0), hexes[hexIndex].getVertices()[0]); // no resources, because setup phase
        main.buildSettlement(main.getPlayers().get(0), hexes[hexIndex].getVertices()[2]); // player 0's second settlement

        main.applyDice(2);

        HashMap<Resource, Integer> result = emptyResourceMap();
        result.put(hexes[hexIndex].getResource(), 2); // the 2 resources that the player gets

        for (Player player : main.getPlayers()) { // make sure no players got any resources
            if (player == main.getPlayers().get(0)) {
                assertEquals(result, player.getResources());
            } else {
                assertEquals(emptyResourceMap(), player.getResources());
            }
        }
    }

    // test that if one person has a city by the hex, he gets the resource twice
    @Test
    public void dieRollCity() {
        Hex[] hexes = main.getBoard().getHexes();
        int hexIndex = findHexOfNum(2);
        // other player builds a settlement on a hex that should not be bordering this one, I hope
        main.buildSettlement(main.getPlayers().get(1), hexes[(hexIndex + 2) % hexes.length].getVertices()[1]);
        main.buildSettlement(main.getPlayers().get(0), hexes[hexIndex].getVertices()[0]); // no resources, because setup phase
        main.buildCity(main.getPlayers().get(0), hexes[hexIndex].getVertices()[0]); // I made sure this would cost no resources in setup

        main.applyDice(2);

        HashMap<Resource, Integer> result = emptyResourceMap();
        result.put(hexes[hexIndex].getResource(), 2); // the 2 resources that the player gets

        for (Player player : main.getPlayers()) { // make sure no players got any resources
            if (player == main.getPlayers().get(0)) {
                assertEquals(result, player.getResources());
            } else {
                assertEquals(emptyResourceMap(), player.getResources());
            }
        }
    }

    // test that if a hex has the robber, it doesn't give resources
    @Test
    public void dieRollNoResourceRobbed() {
        Hex[] hexes = main.getBoard().getHexes();
        int hexIndex = findHexOfNum(2);
        // other player builds a settlement on a hex that should not be bordering this one, though it doesn't matter if it is
        main.buildSettlement(main.getPlayers().get(1), hexes[(hexIndex + 2) % hexes.length].getVertices()[1]);
        main.buildSettlement(main.getPlayers().get(0), hexes[hexIndex].getVertices()[0]); // no resources, because setup phase

        main.moveThief(main.getPlayers().get(2), hexes[hexIndex].getVertices()[1], hexes[hexIndex]);

        main.applyDice(2);

        for (Player player : main.getPlayers()) { // make sure no players got any resources, since the robber was there
            assertEquals(emptyResourceMap(), player.getResources());
        }
    }

    // I just realized that I can't test the ones with different hexes because I have no way of knowing if they are adjacent
    /*
    // test that if one person has settlements by both hexes, he gets the resources of both, whether the same
    @Test
    public void dieRollTwoSettlementsTwoHexesSameResource() {
        Hex[] hexes = main.getBoard().getHexes();
        List<Integer> hexIndices = findHexOfNums(6);
        if (hexes[hexIndices.get(0)].getResource() != hexes[hexIndices.get(0)].getResource()) { // if they have different resources
            main = new MainImpl(4, new GUIMainDummyImpl());
            dieRollTwoSettlementsTwoHexesSameResource(); // try again with a different board
            return;
        }

        // if this board has its 6's on the same resource
        main.buildSettlement(main.getPlayers().get(0), hexes[hexIndices.get(0)].getVertices()[0]); // no resources, because setup phase
        main.buildSettlement(main.getPlayers().get(0), hexes[hexIndices.get(1)].getVertices()[0]);

        main.applyDice(2, main.getPlayers().get(0));

        HashMap<Resource, Integer> result = emptyResourceMap();
        result.put(hexes[hexIndices.get(0)].getResource(), 2); // the 2 resources that the player gets

        for (Player player : main.getPlayers()) { // make sure no players got any resources
            if (player == main.getPlayers().get(0)) {
                assertEquals(result, player.getResources());
            } else {
                assertEquals(emptyResourceMap(), player.getResources());
            }
        }
    }

    // or different type
    @Test
    public void dieRollTwoSettlementsTwoHexesDifResource() {
        Hex[] hexes = main.getBoard().getHexes();
        List<Integer> hexIndices = findHexOfNums(6);
        if (hexes[hexIndices.get(0)].getResource() == hexes[hexIndices.get(0)].getResource()) { // if they have the same resources
            main = new MainImpl(4, new GUIMainDummyImpl());
            dieRollTwoSettlementsTwoHexesDifResource(); // try again with a different board
            return;
        }

        // if this board has its 6's on different resources
        main.buildSettlement(main.getPlayers().get(0), hexes[hexIndices.get(0)].getVertices()[0]); // no resources, because setup phase
        main.buildSettlement(main.getPlayers().get(0), hexes[hexIndices.get(1)].getVertices()[0]);

        main.applyDice(2, main.getPlayers().get(0));

        HashMap<Resource, Integer> result = emptyResourceMap();
        result.put(hexes[hexIndices.get(0)].getResource(), 2); // the 2 resources that the player gets

        for (Player player : main.getPlayers()) { // make sure no players got any resources
            if (player == main.getPlayers().get(0)) {
                assertEquals(result, player.getResources());
            } else {
                assertEquals(emptyResourceMap(), player.getResources());
            }
        }
    }*/

    // test that if two people each have a settlement by the two hexes, they both get the resource of their hex

    // tests for getAvailableRoadSpotsGivenEdge
    // test that it returns all available spots from getAvailableRoadSpots besides the given edge
    @Test
    public void returnsAllFromNormalMethodBesidesGivenEdge() {
        Player player = main.getPlayers().get(0);
        Vertex vertex = main.getBoard().getVertices()[13];
        addPlayer(vertex.getEdges()[0], player);

        Set<Edge> normalResultWithoutEdge = main.getAvailableRoadSpots(player);
        normalResultWithoutEdge.remove(vertex.getEdges()[1]); // since that one won't be available anymore
        assertTrue(main.getAvailableRoadSpotsGivenEdge(player, vertex.getEdges()[1]).containsAll(normalResultWithoutEdge));
    }

    /**
     * @param edge you are looking for edges that connect with
     * @param vertexWeHave you already know
     * @return the other 2 edges that connect to this edge but not the vertex
     */
    private Set<Edge> getOtherAdjacentEdges(Edge edge, Vertex vertexWeHave) {
        for (Vertex vertex : main.getBoard().getVertices()) {
            for (Edge otherEdge : vertex.getEdges()) {
                if (otherEdge == edge) {
                    Set<Edge> edges = new HashSet<>(Arrays.asList(vertex.getEdges()));
                    edges.removeAll(Arrays.asList(vertexWeHave.getEdges()));
                    // removes this edge and the 2 next to it in that vertex
                    return edges;
                }
            }
        }
        return new HashSet<>();
    }

    // tests that it returns all the new spots given from that edge
    @Test
    public void returnsAllNewAdjacentEdges() {
        Player player = main.getPlayers().get(0);
        Vertex vertex = main.getBoard().getVertices()[13];
        addPlayer(vertex.getEdges()[0], player);

        //13's 0 is already occupied, and its 2 was already available beforehand
        // so we only care about the 2 on the other side

        Set<Edge> newEdges = getOtherAdjacentEdges(vertex.getEdges()[0], vertex);
        assertTrue(main.getAvailableRoadSpotsGivenEdge(player, vertex.getEdges()[1]).containsAll(newEdges));
    }

    // tests that it doesn't return the edge we are looking at
    @Test
    public void getAllWithEdgeDoesNotReturnThatEdge() {
        Player player = main.getPlayers().get(0);
        Vertex vertex = main.getBoard().getVertices()[13];
        addPlayer(vertex.getEdges()[0], player);

        assertFalse(main.getAvailableRoadSpotsGivenEdge(player, vertex.getEdges()[1]).contains(vertex.getEdges()[1]));
    }

    // tests that it does not return spots adjacent to that edge where there are already roads
    @Test
    public void getAllWithEdgeDoesNotReturnPlayerRoad() {
        Player player = main.getPlayers().get(0);
        Vertex vertex = main.getBoard().getVertices()[13];
        addPlayer(vertex.getEdges()[0], player);

        assertFalse(main.getAvailableRoadSpotsGivenEdge(player, vertex.getEdges()[1]).contains(vertex.getEdges()[0]));
    }

    // tests that it doesn't return spots adjacent to that edge where other players have roads
    @Test
    public void getAllWithEdgeDoesNotReturnOtherRoad() {
        Player player = main.getPlayers().get(0);
        Vertex vertex = main.getBoard().getVertices()[13];
        addPlayer(vertex.getEdges()[0], player);
        addPlayer(vertex.getEdges()[2], main.getPlayers().get(1));

        assertFalse(main.getAvailableRoadSpotsGivenEdge(player, vertex.getEdges()[1]).contains(vertex.getEdges()[2]));
    }

}
