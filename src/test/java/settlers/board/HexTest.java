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

}
