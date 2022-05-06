package settlers;

import settlers.board.Edge;
import settlers.board.Vertex;
import settlers.card.DevelopmentCard;
import settlers.card.Resource;

import java.util.*;

public class PlayerImpl implements Player {

    private Map<Resource, Integer> resources;
    private int resourceCount; // defaults to 0, number of total resources
    private Map<DevelopmentCard, Integer> vellies;
    private Set<Resource> ports;
    private Set<Vertex> settlements;
    private Set<Vertex> cities;
    private Set<Edge> roads;
    private int id;

    public PlayerImpl() {
        setUpEmptyResources(); // sets up the resources to have 0 of each type
        vellies = new HashMap<>();
        ports = new HashSet<>();
        settlements = new HashSet<>();
        cities = new HashSet<>();
        roads = new HashSet<>();
        id = (int) (Math.random() * 100);
    }

    /**
     * Sets up the player's resources to have 0 of every type, for aid of testing
     */
    private void setUpEmptyResources() {
        resources = new HashMap<>();
        for (Resource resource : Resource.values()) {
            if (resource != Resource.MISC) {
                resources.put(resource, 0);
            }
        }
    }

    /**
     * @return an unmodifiable Map containing the player's resource cards and quantity of each
     */
    @Override
    public Map<Resource, Integer> getResources() {
        return Collections.unmodifiableMap(resources);
    }

    /**
     * @param resource to give to the player
     * @throws IllegalArgumentException if resource is MISC
     */
    @Override
    public void addResource(Resource resource) {
        if (resource == Resource.MISC) {
            throw new IllegalArgumentException("Players cannot have MISC resources");
        }
        // The following should update resources by 1
        resources.put(resource, resources.getOrDefault(resource, 0) + 1);
        resourceCount++;
    }

    /**
     * @param resources to be removed from the player's hand
     * @return true if the removal was successful, false if it was not
     * If the removal is not successful for one resource, the player's hand will not be changed
     */
    @Override
    public boolean removeResources(Map<Resource, Integer> resources) {
        for (Resource resource : resources.keySet()) {
            if (this.resources.getOrDefault(resource, 0) < resources.get(resource)) {
                // if the player has fewer than what is being removed
                return false; // without actually removing anything
            }
        }
        for (Resource resource : resources.keySet()) {
            // Subtracts the number being removed from the number the player has
            this.resources.put(resource, this.resources.getOrDefault(resource, 0) - resources.get(resource));
            resourceCount -= resources.get(resource); // decreases number of cards in hand
        }
        return true;
    }

    /**
     * @return true if the player has more than 7 resource cards, false otherwise
     */
    @Override
    public boolean hasMoreThan7Cards() {
        return resourceCount > 7;
    }

    /**
     * @return an unmodifiable Map containing the player's development cards and quantity of each
     */
    @Override
    public Map<DevelopmentCard, Integer> getDevelopmentCards() {
        return Collections.unmodifiableMap(vellies);
    }

    /**
     * @param development card being added to the player's hand
     */
    @Override
    public void addDevelopmentCard(DevelopmentCard development) {
        vellies.put(development, vellies.getOrDefault(development, 0) + 1);
    }

    /**
     * @param development card being removed from the player's hand
     * @return true if the removal was successful, false if he never had the card or a point card
     */
    @Override
    public boolean removeDevelopmentCard(DevelopmentCard development) {
        if (vellies.getOrDefault(development, 0) == 0 || development == DevelopmentCard.VICTORY_POINT) {
            // if the player does not have or never did have the card, or if this is a point card
            return false;
        }
        vellies.put(development, vellies.get(development) - 1);
        return true;
    }

    /**
     * @return an unmodifiable set of resources the player has 2:1 ports for
     * MISC means the player has a 3:1 port
     */
    @Override
    public Set<Resource> getPorts() {
        return Collections.unmodifiableSet(ports);
    }

    /**
     * @param resource of the 2:1 port being added, or MISC if it is a 3:1 port
     * @return true if the port was added, false if the player already had it
     */
    @Override
    public boolean addPort(Resource resource) {
        return ports.add(resource);
    }

    /**
     * @return all the player's settlements
     */
    @Override
    public Set<Vertex> getSettlements() {
        return settlements;
    }

    /**
     * @return all the player's cities
     */
    @Override
    public Set<Vertex> getCities() {
        return cities;
    }

    /**
     * @return all the player's roads
     */
    @Override
    public Set<Edge> getRoads() {
        return roads;
    }

    /**
     * @param road to be added to the player's collection
     */
    @Override
    public void addRoad(Edge road) {
        if (roads.size() < 15) {
            roads.add(road);
        }
    }

    /**
     * @param settlement to be added to the player's collection
     */
    @Override
    public void addSettlement(Vertex settlement) {
        if (settlements.size() < 5) {
            settlements.add(settlement);
        }
    }

    /**
     * @param city to be removed from the player's settlement collection and added to its city collection
     */
    @Override
    public void upgradeSettlement(Vertex city) {
        if (cities.size() < 4) {
            settlements.remove(city);
            cities.add(city);
        }
    }

    @Override
    public String toString() {
        return "Player: " + id;
    }
}
