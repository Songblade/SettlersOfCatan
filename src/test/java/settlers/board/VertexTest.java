package settlers.board;

import org.junit.jupiter.api.Test;
import settlers.Player;
import settlers.card.Resource;

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

}

// this class is so I can distinguish between the different edges on each side
class TestEdge implements Edge {

    private int id;

    TestEdge(int id) {
        this.id = id;
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
