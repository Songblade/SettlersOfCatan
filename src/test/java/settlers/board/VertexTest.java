package settlers.board;

import org.junit.jupiter.api.Test;
import settlers.Player;
import settlers.card.DevelopmentCard;
import settlers.card.Resource;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class VertexTest {

    // get and set vertices methods
    // set and get vertices work
    @Test
    public void getAdjacentVerticesWorks() {
        Vertex vertex = new VertexImpl();
        Vertex[] result = new Vertex[3];
        Vertex woodVert = new VertexImpl();
        woodVert.setPort(Resource.WOOD);
        Vertex oreVert = new VertexImpl();
        oreVert.setPort(Resource.ORE);
        Vertex brickVert = new VertexImpl();
        brickVert.setPort(Resource.BRICK);
        assertArrayEquals(result, vertex.getAdjacentVertices());
        result[0] = woodVert;
        vertex.setVertex(woodVert, 0);
        assertArrayEquals(result, vertex.getAdjacentVertices());
        result[1] = oreVert;
        vertex.setVertex(oreVert, 1);
        assertArrayEquals(result, vertex.getAdjacentVertices());
        result[2] = brickVert;
        vertex.setVertex(brickVert, 2);
        assertArrayEquals(result, vertex.getAdjacentVertices());
    }

    // get vertices throws when we already have 3 vertices
    @Test
    public void getAdjacentVerticesThrowsWhenHave3() {
        Vertex vertex = new VertexImpl();
        Vertex woodVert = new VertexImpl();
        woodVert.setPort(Resource.WOOD);
        Vertex oreVert = new VertexImpl();
        oreVert.setPort(Resource.ORE);
        Vertex brickVert = new VertexImpl();
        brickVert.setPort(Resource.BRICK);
        vertex.setVertex(woodVert, 0);
        vertex.setVertex(oreVert, 1);
        vertex.setVertex(brickVert, 2);
        assertThrows(IllegalStateException.class, () -> vertex.setVertex(new VertexImpl(), 0));
        assertThrows(IllegalStateException.class, () -> vertex.setVertex(new VertexImpl(), 1));
        assertThrows(IllegalStateException.class, () -> vertex.setVertex(new VertexImpl(), 2));
    }

    // get vertices throws when we have < 3, but already added to this position
    @Test
    public void getAdjacentVerticesThrowsWhenLessThan3() {
        Vertex vertex = new VertexImpl();
        Vertex woodVert = new VertexImpl();
        woodVert.setPort(Resource.WOOD);
        vertex.setVertex(woodVert, 0);
        assertThrows(IllegalStateException.class, () -> vertex.setVertex(new VertexImpl(), 0));
    }

    // Can add vertices in any order
    @Test
    public void setVertexWorksAnyOrder() {
        Vertex vertex = new VertexImpl();
        Vertex[] result = new Vertex[3];
        Vertex woodVert = new VertexImpl();
        woodVert.setPort(Resource.WOOD);
        Vertex oreVert = new VertexImpl();
        oreVert.setPort(Resource.ORE);
        Vertex brickVert = new VertexImpl();
        brickVert.setPort(Resource.BRICK);
        result[1] = oreVert;
        vertex.setVertex(oreVert, 1);
        assertArrayEquals(result, vertex.getAdjacentVertices());
        assertThrows(IllegalStateException.class, () -> vertex.setVertex(new VertexImpl(), 1));
    }

    // set and get edges methods
    // set and get edges work
    @Test
    public void getEdgesWorks() {
        Vertex vertex = new VertexImpl();
        Edge[] result = new Edge[3];
        TestEdge edge0 = new TestEdge(0);
        TestEdge edge1 = new TestEdge(1);
        TestEdge edge2 = new TestEdge(2);
        assertArrayEquals(result, vertex.getEdges());
        result[0] = edge0;
        vertex.setEdge(edge0, 0);
        assertArrayEquals(result, vertex.getEdges());
        result[1] = edge1;
        vertex.setEdge(edge1, 1);
        assertArrayEquals(result, vertex.getEdges());
        result[2] = edge2;
        vertex.setEdge(edge2, 2);
        assertArrayEquals(result, vertex.getEdges());
    }

    // get edges throws when we already have 3 edges
    @Test
    public void getEdgesThrowsWhen3() {
        Vertex vertex = new VertexImpl();
        TestEdge edge0 = new TestEdge(0);
        TestEdge edge1 = new TestEdge(1);
        TestEdge edge2 = new TestEdge(2);
        vertex.setEdge(edge0, 0);
        vertex.setEdge(edge1, 1);
        vertex.setEdge(edge2, 2);
        assertThrows(IllegalStateException.class, () -> vertex.setEdge(new TestEdge(3), 0));
        assertThrows(IllegalStateException.class, () -> vertex.setEdge(new TestEdge(3), 1));
        assertThrows(IllegalStateException.class, () -> vertex.setEdge(new TestEdge(3), 2));
    }

    // get edges throws when we have < 3, but already added to this position
    @Test
    public void getEdgesThrowsWhenLessThan3() {
        Vertex vertex = new VertexImpl();
        TestEdge edge0 = new TestEdge(0);
        vertex.setEdge(edge0, 0);
        assertThrows(IllegalStateException.class, () -> vertex.setEdge(new TestEdge(3), 0));
    }

    // Can add edges in any order
    @Test
    public void setEdgesWorksAnyOrder() {
        Vertex vertex = new VertexImpl();
        Edge[] result = new Edge[3];
        TestEdge edge1 = new TestEdge(1);
        result[1] = edge1;
        vertex.setEdge(edge1, 1);
        assertArrayEquals(result, vertex.getEdges());
        assertThrows(IllegalStateException.class, () -> vertex.setEdge(new TestEdge(3), 1));
    }

    // make sure getPort works correctly, with both null and regular
    @Test
    public void getPortWorks() {
        Vertex vertex = new VertexImpl();
        assertNull(vertex.getPort());
        vertex.setPort(Resource.WOOD);
        assertEquals(Resource.WOOD, vertex.getPort());
    }

    // get port works with MISC
    @Test
    public void getPortWorksMISC() {
        Vertex vertex = new VertexImpl();
        assertNull(vertex.getPort());
        vertex.setPort(Resource.MISC);
        assertEquals(Resource.MISC, vertex.getPort());
    }

    // setPort throws when already a port
    @Test
    public void setPortThrows() {
        Vertex vertex = new VertexImpl();
        assertNull(vertex.getPort());
        vertex.setPort(Resource.MISC);
        assertThrows(IllegalStateException.class, () -> vertex.setPort(Resource.WOOD));
        assertThrows(IllegalStateException.class, () -> vertex.setPort(Resource.MISC));
    }

    // Vertex player methods
    // check that getPlayer works, and that starts null
    @Test
    public void getPlayerWorks() {
        Vertex vertex = new VertexImpl();
        assertNull(vertex.getPlayer());
        Player example = new TestPlayer();
        vertex.setPlayer(example);
        assertEquals(example, vertex.getPlayer());
    }

    // check that setPlayer throws when already a player there
    @Test
    public void getPlayerThrows() {
        Vertex vertex = new VertexImpl();
        assertNull(vertex.getPlayer());
        Player example = new TestPlayer();
        vertex.setPlayer(example);
        assertThrows(IllegalStateException.class, () -> vertex.setPlayer(new TestPlayer()));
    }

    // city methods
    // tests that isCity returns false when empty or settlement, and true when city
    @Test
    public void isCityWorks() {
        Vertex vertex = new VertexImpl();
        assertFalse(vertex.isCity());
        Player example = new TestPlayer();
        vertex.setPlayer(example);
        assertFalse(vertex.isCity());
        vertex.makeCity();
        assertTrue(vertex.isCity());
    }

    // makeCity throws when empty
    @Test
    public void makeCityThrowsEmpty() {
        Vertex vertex = new VertexImpl();
        assertFalse(vertex.isCity());
        assertThrows(IllegalStateException.class, vertex::makeCity);
    }

    // makeCity throws when already a city
    @Test
    public void makeCityThrowsCity() {
        Vertex vertex = new VertexImpl();
        vertex.setPlayer(new TestPlayer());
        vertex.makeCity();
        assertThrows(IllegalStateException.class, vertex::makeCity);
    }

    // this class is so I can distinguish between the different edges on each side
    private static class TestEdge implements Edge {

        private final int id;

        TestEdge(int id) {
            this.id = id;
        }

        /**
         * @return a length 3 array containing the adjacent vertices, where 0 is up, increasing clockwise
         */
        @Override
        public List<Edge> getAdjacentEdges() {
            return null;
        }

        /**
         * @param edge     being set adjacent to this Edge
         * @param position from 0 to 3, where the edge is set, where 0 is the upper left, increasing clockwise
         * @throws IllegalStateException if this position's vertex is already set
         */
        @Override
        public void setEdge(Edge edge, int position) {

        }

        /**
         * @return the player who owns the road, or null if there is no road
         */
        @Override
        public Player getPlayer() {
            return null;
        }

        /**
         * @param player that owns this Road
         * @throws IllegalArgumentException if a different player already has this road
         */
        @Override
        public void setPlayer(Player player) {

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestEdge edge = (TestEdge) o;
            return this.id == edge.id;
        }
    }

    private static class TestPlayer implements Player {

        /**
         * @return an unmodifiable Map containing the player's resource cards and quantity of each
         */
        @Override
        public Map<Resource, Integer> getResources() {
            return null;
        }

        /**
         * @param resource to give to the player
         */
        @Override
        public void addResource(Resource resource) {

        }

        /**
         * @param resources to be removed from the player's hand
         * @return true if the removal was successful, false if it was not
         * If the removal is not successful for one resource, the player's hand will not be changed
         */
        @Override
        public boolean removeResources(Map<Resource, Integer> resources) {
            return false;
        }

        /**
         * @return true if the player has more than 7 resource cards, false otherwise
         */
        @Override
        public boolean hasMoreThan7Cards() {
            return false;
        }

        /**
         * @return the number of resource cards this player has
         */
        @Override
        public int getCardNumber() {
            return 0;
        }

        /**
         * @return an unmodifiable Map containing the player's development cards and quantity of each
         */
        @Override
        public Map<DevelopmentCard, Integer> getDevelopmentCards() {
            return null;
        }

        /**
         * @param development card being added to the player's hand
         */
        @Override
        public boolean addDevelopmentCard(DevelopmentCard development) {
            return false;
        }

        /**
         * @param development card being removed from the player's hand
         * @return true if the removal was successful, false if he never had the card
         * @throws IllegalArgumentException if the card is a VICTORY_POINT
         */
        @Override
        public boolean removeDevelopmentCard(DevelopmentCard development) {
            return false;
        }

        /**
         * @return the number of development cards the player has
         */
        @Override
        public int getDevelopmentCardCount() {
            return 0;
        }

        /**
         * @return number of times the player has used a knight
         */
        @Override
        public int getKnightNumber() {
            return 0;
        }

        /**
         * @return an unmodifiable list of resources the player has 2:1 ports for
         * MISC means the player has a 3:1 port
         */
        @Override
        public Set<Resource> getPorts() {
            return null;
        }

        /**
         * @param resource of the 2:1 port being added, or MISC if it is a 3:1 port
         * @return true if the port was added, false if the player already had it
         */
        @Override
        public boolean addPort(Resource resource) {
            return false;
        }

        /**
         * @return all the player's settlements
         */
        @Override
        public Set<Vertex> getSettlements() {
            return null;
        }

        /**
         * @return all the player's cities
         */
        @Override
        public Set<Vertex> getCities() {
            return null;
        }

        /**
         * @return all the player's roads
         */
        @Override
        public Set<Edge> getRoads() {
            return null;
        }

        /**
         * @param road to be added to the player's collection
         */
        @Override
        public void addRoad(Edge road) {

        }

        /**
         * @param settlement to be added to the player's collection
         */
        @Override
        public boolean addSettlement(Vertex settlement) {
            return false;
        }

        /**
         * @param city to be removed from the player's settlement collection and added to its city collection
         */
        @Override
        public boolean upgradeSettlement(Vertex city) {
            return false;
        }

        /**
         * Increases the player's victory points
         * Outside of Player, should only be used by Main for Longest Road and Largest Army
         *
         * @param num should be 1, but should be 2 if this is Longest Road or Largest Army
         *            If the player loses longest road or largest army, it should be -2
         * @return true if the player now has >= 10 points, false otherwise
         */
        @Override
        public boolean increaseVictoryPoints(int num) {
            return false;
        }

        /**
         * @return the player's current number of victory points
         */
        @Override
        public int getVictoryPoints() {
            return 0;
        }

        /**
         * @return the player's ID, which if set up properly will be from 0 to 3
         */
        @Override
        public int getID() {
            return 0;
        }

    }

}
