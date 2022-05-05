package settlers;

import org.junit.jupiter.api.Test;
import settlers.board.Edge;
import settlers.board.Hex;
import settlers.board.Vertex;
import settlers.card.Resource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    private MainImpl main;

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
    // Make sure that the settlement actually gets built
    @Test
    public void testSettlementIsBuilt() {
        Player player = main.getPlayers().get(0);
        player.addResource(Resource.WOOD);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.SHEEP);
        player.addResource(Resource.WHEAT);

        Vertex vertex = main.getBoard().getVertices()[4];
        main.setPhase(true);

        main.buildSettlement(player, vertex);

        assertEquals(player, vertex.getPlayer());

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
        Player player = main.getPlayers().get(0);
        player.addResource(Resource.WOOD);
        player.addResource(Resource.BRICK);
        player.addResource(Resource.SHEEP);
        player.addResource(Resource.WHEAT);

        Vertex vertex = main.getBoard().getVertices()[4];
        main.setPhase(true);

        main.buildSettlement(player, vertex);

        assertEquals(player, vertex.getPlayer());

        // this part makes sure that the vertex is no longer available for this person or anyone else to build
        Set<Vertex> result = main.getAvailableSettlementSpots(player);
        assertFalse(result.contains(vertex));
        assertFalse(result.contains(vertex.getAdjacentVertices()[0]));

        result = main.getAvailableSettlementSpots(main.getPlayers().get(1));
        assertFalse(result.contains(vertex));
        assertFalse(result.contains(vertex.getAdjacentVertices()[0]));
    }

    // Make sure that the player's resources are decreased if game phase
    // Make sure player resources not decreased otherwise
    // Make sure can no longer build here or adjacent to here
    // I will not implement any exceptions, to aid in testing

    // buildRoad
    // Make sure that the road is actually built now
    // Make sure the player's resources are decreased if game phase
    // Make sure player resources not decreased otherwise
    // Make sure can no longer build here
    // Make sure can now build beyond here, both road and settlement

}
