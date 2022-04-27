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

}
