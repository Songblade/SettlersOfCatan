package settlers.board;

import org.junit.jupiter.api.Test;
import settlers.card.Resource;

import static org.junit.jupiter.api.Assertions.*;

public class HexTest {

    // testing getResource and constructor
    // tests that getResource works on a normal resource
    @Test
    public void getResourceWorks() {
        Hex hex = new HexImpl(Resource.WOOD);
        assertEquals(Resource.WOOD, hex.getResource());
    }

    // tests that getResource works for MISC
    @Test
    public void getResourceWorksMISC() {
        Hex hex = new HexImpl(Resource.MISC);
        assertEquals(Resource.MISC, hex.getResource());
    }

    // tests that can't create a null hex
    @Test
    public void constructorThrows() {
        assertThrows(IllegalArgumentException.class, () -> new HexImpl(null));
    }

    // Tests for set and get number
    // makes sure get number throws when not set yet
    @Test
    public void getNumberThrowsUnset() {
        Hex hex = new HexImpl(Resource.WOOD);
        assertThrows(IllegalStateException.class, hex::getNumber);
    }

    // makes sure set number throws when number is out of bounds
    @Test
    public void setNumberThrowsOutOfBounds() {
        Hex hex = new HexImpl(Resource.WOOD);
        assertThrows(IllegalArgumentException.class, () -> hex.setNumber(7));
        assertThrows(IllegalArgumentException.class, () -> hex.setNumber(0));
        assertThrows(IllegalArgumentException.class, () -> hex.setNumber(-1));
        assertThrows(IllegalArgumentException.class, () -> hex.setNumber(13));
    }

    // makes sure get number works when sets to something, including 7
    @Test
    public void setNumberWorks() {
        for (int i = 1; i < 12; i++) {
            if (i != 7) {
                Hex hex = new HexImpl(Resource.WOOD);
                hex.setNumber(i);
                assertEquals(i, hex.getNumber());
            }
        }
    }

    // makes sure setNumber throws when number already set
    @Test
    public void setNumberThrowsAlreadySet() {
        Hex hex = new HexImpl(Resource.WOOD);
        hex.setNumber(3);
        assertThrows(IllegalStateException.class, () -> hex.setNumber(4));
    }

    // now testing vertex methods
    // test that returns array of nulls if Vertices not set yet
    @Test
    public void getVerticesReturnsNullIfEmpty() {
        Hex hex = new HexImpl(Resource.WOOD);
        assertArrayEquals(new Vertex[6], hex.getVertices());
    }

    // test that returns array with some set if some Vertices set
    @Test
    public void getVerticesReturnsWithRightVertices() {
        Hex hex = new HexImpl(Resource.WOOD);
        Vertex[] result = {new VertexImpl(), new VertexImpl(), null, new VertexImpl(), null, null};
        hex.setVertex(result[0], 0);
        hex.setVertex(result[1], 1);
        hex.setVertex(result[3], 3);
        assertArrayEquals(result, hex.getVertices());
    }

    // test that returns full array if all vertices set
    @Test
    public void getVerticesReturnsFull() {
        Hex hex = new HexImpl(Resource.WOOD);
        Vertex[] result = new Vertex[6];
        for (int i = 0; i < 6; i++) {
            result[i] = new VertexImpl();
            hex.setVertex(result[i], i);
        }
        assertArrayEquals(result, hex.getVertices());
    }

    // test that throws iae if position out of bounds
    @Test
    public void getVerticesThrowsOutOfBounds() {
        Hex hex = new HexImpl(Resource.WOOD);
        assertThrows(IllegalArgumentException.class, ()->hex.setVertex(new VertexImpl(), -1));
        assertThrows(IllegalArgumentException.class, ()->hex.setVertex(new VertexImpl(), 6));
    }

    // test that throws ise if position already occupied, even if not all occupied
    @Test
    public void getVerticesThrowsOccupied() {
        Hex hex = new HexImpl(Resource.WOOD);
        Vertex[] result = {new VertexImpl(), new VertexImpl(), null, new VertexImpl(), null, null};
        hex.setVertex(result[0], 0);
        hex.setVertex(result[1], 1);
        hex.setVertex(result[3], 3);
        assertThrows(IllegalStateException.class, ()->hex.setVertex(new VertexImpl(), 1));
    }

    // thief tests
    // tests that starts as false
    @Test
    public void hasThiefStartsFalse() {
        Hex hex = new HexImpl(Resource.WOOD);
        assertFalse(hex.hasThief());
        hex = new HexImpl(Resource.MISC);
        assertFalse(hex.hasThief());
    }

    // tests that can set it to true
    @Test
    public void setThiefWorksTrue() {
        Hex hex = new HexImpl(Resource.WOOD);
        hex.setThief(true);
        assertTrue(hex.hasThief());
    }

    // tests that can set it back to false
    @Test
    public void setThiefWorksFalse() {
        Hex hex = new HexImpl(Resource.WOOD);
        hex.setThief(true);
        hex.setThief(false);
        assertFalse(hex.hasThief());
        hex.setThief(true);
        assertTrue(hex.hasThief());
    }

}
