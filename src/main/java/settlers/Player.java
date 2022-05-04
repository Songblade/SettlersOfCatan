package settlers;

import java.util.Map;
import java.util.Set;

import settlers.board.Edge;
import settlers.board.Vertex;
import settlers.card.*;

public interface Player {

    /**
     * @return an unmodifiable Map containing the player's resource cards and quantity of each
     */
    Map<Resource, Integer> getResources();

    /**
     * @param resource to give to the player
     * @throws IllegalArgumentException if resource is MISC
     */
    void addResource(Resource resource);

    /**
     * @param resources to be removed from the player's hand
     * @return true if the removal was successful, false if it was not
     * If the removal is not successful for one resource, the player's hand will not be changed
     */
    boolean removeResources(Map<Resource, Integer> resources);

    /**
     * @return true if the player has more than 7 resource cards, false otherwise
     */
    boolean hasMoreThan7Cards();

    /**
     * @return an unmodifiable Map containing the player's development cards and quantity of each
     */
    Map<DevelopmentCard, Integer> getDevelopmentCards();

    /**
     * @param development card being added to the player's hand
     */
    void addDevelopmentCard(DevelopmentCard development);

    /**
     * @param development card being removed from the player's hand
     * @return true if the removal was successful, false if he never had the card or a point card
     */
    boolean removeDevelopmentCard(DevelopmentCard development);

    /**
     * @return an unmodifiable set of resources the player has 2:1 ports for
     * MISC means the player has a 3:1 port
     */
    Set<Resource> getPorts();

    /**
     * @param resource of the 2:1 port being added, or MISC if it is a 3:1 port
     * @return true if the port was added, false if the player already had it
     */
    boolean addPort(Resource resource);

    /**
     * @return all the player's settlements
     */
    Set<Vertex> getSettlements();

    /**
     * @return all the player's cities
     */
    Set<Vertex> getCities();

    /**
     * @return all the player's roads
     */
    Set<Edge> getRoads();

    /**
     * @param road to be added to the player's collection
     */
    void addRoad(Edge road);

    /**
     * @param settlement to be added to the player's collection
     */
    void addSettlement(Vertex settlement);

    /**
     * @param city to be removed from the player's settlement collection and added to its city collection
     */
    void upgradeSettlement(Vertex city);
}