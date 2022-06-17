package settlers.gui;

import settlers.Player;
import settlers.board.*;
import settlers.card.DevelopmentCard;
import settlers.card.Resource;

import java.util.Map;
import java.util.Set;

public class GUIMainDummyImpl implements GUIMain {

    @Override
    public boolean canBuildRoad(Player player) {
        return false;
    }

    @Override
    public boolean canBuildSettlement(Player player) {
        return false;
    }

    @Override
    public boolean canBuildCity(Player player) {
        return false;
    }

    @Override
    public boolean canBuyDevelopmentCard(Player player) {
        return false;
    }

    @Override
    public boolean canPlayDevelopmentCard(Player player, DevelopmentCard card) {
        return false;
    }

    @Override
    public void buildRoad(Player player, Edge edge) {

    }

    @Override
    public void buildSettlement(Player player, Vertex vertex) {

    }

    @Override
    public void buildCity(Player player, Vertex vertex) {

    }

    @Override
    public void buildDevelopmentCard(Player player) {

    }

    @Override
    public boolean playKnight(Player stealer, Vertex settlement, Hex location) {
        return false;
    }

    @Override
    public boolean playYearOfPlenty(Player player, Resource firstResource, Resource secondResource) {
        return false;
    }

    @Override
    public boolean playMonopoly(Player player, Resource resource) {
        return false;
    }

    @Override
    public boolean playRoadBuilding(Player player, Edge firstLocation, Edge secondLocation) {
        return false;
    }

    @Override
    public Set<Vertex> getAvailableSettlementSpots(Player player) {
        return null;
    }

    @Override
    public Set<Edge> getAvailableRoadSpots(Player player) {
        return null;
    }

    @Override
    public Set<Edge> getAvailableRoadSpotsGivenEdge(Player player, Edge roadToBuild) {
        return null;
    }

    @Override
    public Set<Vertex> getAvailableCitySpots(Player player) {
        return null;
    }

    /**
     * Starts a turn. Called by Main, updates resources and die number in GUIPlayers
     *
     * @param player  the player whose turn it is
     * @param dieRoll the sum of both die rolls
     */
    @Override
    public void startTurn(Player player, int dieRoll) {

    }

    /**
     * Called by Main. Has the player built a settlement and road during setup
     *
     * @param player     whose turn it is
     * @param validSpots where a settlement can be built during setup
     * @return the Vertex where the player built a Settlement
     */
    @Override
    public Vertex startSetupTurn(Player player, Set<Vertex> validSpots) {
        return null;
    }

    @Override
    public void moveThief(Player player, Vertex vertex, Hex position) {

    }

    @Override
    public Set<Hex> getAvailableThiefSpots() {
        return null;
    }

    @Override
    public void playerHasTargetResources(Player player) {

    }

    @Override
    public boolean canTrade(Player player, Resource resourceGiven) {
        return false;
    }

    @Override
    public void trade(Player player, Resource resourceGiven, Resource resourceGotten) {

    }

    @Override
    public void trade(Player player, Map<Resource, Integer> resourcesExchanged, Set<Player> sendTo) {

    }

}