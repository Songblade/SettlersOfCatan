package settlers;

import settlers.card.Resource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Building {
    ROAD(15,1, 1, 0, 0, 0),
    SETTLEMENT(5,1, 1, 1, 0, 1),
    CITY(4,0, 0, 2, 3, 0),
    DEVELOPMENT_CARD(25,0, 0, 1, 1, 1);

    Building(int maxNumber, int brickNumber, int woodNumber, int wheatNumber, int oreNumber, int sheepNumber) {
        this.maxNumber = maxNumber;
        resources = new HashMap<>();
        resources.put(Resource.BRICK, brickNumber);
        resources.put(Resource.WOOD, woodNumber);
        resources.put(Resource.WHEAT, wheatNumber);
        resources.put(Resource.ORE, oreNumber);
        resources.put(Resource.SHEEP, sheepNumber);
    }

    private final Map<Resource, Integer> resources;
    private final int maxNumber;

    public Map<Resource, Integer> getResources() {
        return Collections.unmodifiableMap(resources);
    }
    public int getMax() {
        return maxNumber;
    }


}