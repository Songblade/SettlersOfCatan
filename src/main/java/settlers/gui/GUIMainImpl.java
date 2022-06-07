package settlers.gui;

import settlers.*;
import settlers.card.DevelopmentCard;
import settlers.card.Resource;
import settlers.board.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GUIMainImpl implements GUIMain {

    private Main main;
    private HashMap<Player,GUIPlayer> playerGUIs;

    private boolean unlimitedResources = true;

    //Functional
    HashSet<Player> playersWhoHaveNotDiscarded = new HashSet<>();

    public GUIMainImpl(Main main) {
        this.main = main;
        playerGUIs = new HashMap();
        for(int i = 0; i < main.getPlayers().size(); i++){
            Player player = main.getPlayers().get(i);
            playerGUIs.put(player, new GUIPlayerImpl(this,main.getBoard(),player,main.getPlayers()));
        }
        //for(GUIPlayer gui : playerGUIs){
        //    gui.startSettlementTurn(main.getAvailableSettlementSpots(main.getPlayers().get(0)));
        //}
    }


    public boolean canBuildRoad(Player player){
        return main.getAvailableRoadSpots(player).size() > 0 && (main.playerCanBuild(player, Building.ROAD) || unlimitedResources);
    }

    public boolean canBuildSettlement(Player player){
        return main.getAvailableSettlementSpots(player).size() > 0 && (main.playerCanBuild(player, Building.SETTLEMENT) || unlimitedResources);
    }

    public boolean canBuildCity(Player player){
        return main.getAvailableCitySpots(player).size() > 0 && (main.playerCanBuild(player, Building.CITY) || unlimitedResources);
    }

    public boolean canBuyDevelopmentCard(Player player){
        return main.playerCanBuild(player,Building.DEVELOPMENT_CARD) || unlimitedResources;
    }

    @Override
    public boolean canPlayDevelopmentCard(Player player, DevelopmentCard card){
        return main.canPlay(player,card);
    }


    /**
     * Tells main and all GUIPlayers that a @player built a road at @edge
     * @param player the builder of the road
     * @param edge the location of the road
     */
    @Override
    public void buildRoad(Player player, Edge edge){
        main.buildRoad(player,edge);

        for(GUIPlayer gui : playerGUIs.values()){
            gui.setRoad(player,edge);
        }

        updateResourceCounters();
    }

    /**
     * Tells main and all GUIPlayers that a @player built a settlement at @vertex
     * @param player the builder of the settlement
     * @param vertex the location of the settlement
     */
    @Override
    public void buildSettlement(Player player, Vertex vertex){
        main.buildSettlement(player,vertex);

        for(GUIPlayer gui : playerGUIs.values()){
            gui.setSettlement(player,vertex);
        }

        updateResourceCounters();
    }

    /**
     * Tells main and all GUIPlayers that a @player built a city at @vertex
     * @param player the builder of the city
     * @param vertex the location of the city
     */
    @Override
    public void buildCity(Player player, Vertex vertex){
        main.buildCity(player,vertex);

        for(GUIPlayer gui : playerGUIs.values()){
            gui.setCity(player,vertex);
        }

        updateResourceCounters();
    }

    /**
     * Tells main to purchase a development card for the player
     * @param player
     */
    @Override
    public void buildDevelopmentCard(Player player){
        main.buildDevelopmentCard(player);
        updateResourceCounters();
    }

    @Override
    public boolean playKnight(Player stealer, Vertex settlement, Hex location){
        boolean toReturn = main.playKnight(stealer,settlement,location);
        updateGUISAfterThiefMove(location);
        return toReturn;
    }

    @Override
    public boolean playYearOfPlenty(Player player, Resource firstResource, Resource secondResource){
        boolean toReturn = main.playYearOfPlenty(player,firstResource,secondResource);
        updateResourceCounters();
        return toReturn;
    }

    @Override
    public boolean playMonopoly(Player player, Resource resource){
        return main.playMonopoly(player,resource);
    }

    @Override
    public boolean playRoadBuilding(Player player, Edge firstLocation, Edge secondLocation){
        return main.playRoadBuilding(player,firstLocation,secondLocation);
    }

    /**
     * Calls respective method in Main
     * @param player building the settlement
     * @return a Set of Vertices where this player could build
     */
    @Override
    public Set<Vertex> getAvailableSettlementSpots(Player player){
        return main.getAvailableSettlementSpots(player);
    }

    /**
     * Calls respective method in Main
     * @param player building the road
     * @return a Set of Edges where this player could build
     */
    @Override
    public Set<Edge> getAvailableRoadSpots(Player player){
        return main.getAvailableRoadSpots(player);
    }

    /**
     * Calls respective method in Main
     * @param player building the city
     * @return a Set of Vertices where this player could build
     */
    @Override
    public Set<Vertex> getAvailableCitySpots(Player player){
        return main.getAvailableCitySpots(player);
    }

    /**
     * Updates all player's resource counters
     */
    private void updateResourceCounters(){
        for(GUIPlayer gui : playerGUIs.values()){
            gui.updateResourceCounters();
        }
    }

    /**
     * Updates all player's resource counters and die
     * @param dieRoll the number on the dice
     */
    private void updateResourceCounters(int dieRoll){
        for(GUIPlayer gui : playerGUIs.values()){
            gui.updateDieCounter(dieRoll);
            gui.updateResourceCounters();
        }
    }

    private void forcePlayersToDiscardHalfOfHand(){

        for(Player plr : main.getPlayers()) {
            if (plr.hasMoreThan7Cards()) {
                playerGUIs.get(plr).discardUntil(plr.getCardNumber() / 2 + plr.getCardNumber() % 2);
                playersWhoHaveNotDiscarded.add(plr);
            }
        }

        while (playersWhoHaveNotDiscarded.size() > 0){
            try {
                Thread.sleep(1);
                updateResourceCounters();
            }catch (InterruptedException e){
                throw new IllegalStateException("InterrupterException was thrown: " + e);
            }
        }
    }

    /**
     * Starts a turn. Called by Main, updates resources and die number in GUIPlayers
     * @param player the player whose turn it is
     * @param dieRoll the sum of both die rolls
     */
    @Override
    public void startTurn(Player player, int dieRoll){
        //Updates dice and resources for all players
        updateResourceCounters(dieRoll);

        //Forces players to discard half of their hand if they have more than 7 cards. Buggy so commented out
        if(dieRoll == 7){
            forcePlayersToDiscardHalfOfHand();
        }

        //Starts player's turn
        playerGUIs.get(player).startTurn(dieRoll);
    }

    public Set<Hex> getAvailableThiefSpots(){
        return main.getAvailableThiefSpots();
    }

    /**
     * Updates the thief's position to thiefPosition and updates all players' resource counters
     * @param thiefPosition the thief's position
     */
    private void updateGUISAfterThiefMove(Hex thiefPosition){
        for(Player plr : main.getPlayers()){
            playerGUIs.get(plr).moveThiefImage(thiefPosition);
        }

        updateResourceCounters();
    }

    /**
     * Moves the thief
     * @param player the player who moved the thief
     * @param location the settlement which @player is stealing from
     * @param position where the thief is being moved to
     */
    public void moveThief(Player player, Vertex location, Hex position){
        main.moveThief(player, location, position);
        updateGUISAfterThiefMove(position);
    }

    /**
     * Called by Main. Has the player built a settlement and road during setup
     * @param player whose turn it is
     * @param validSpots where a settlement can be built during setup
     * @return the Vertex where the player built a Settlement
     */
    @Override
    public Vertex startSetupTurn(Player player, Set<Vertex> validSpots) {
        //Update the resources for all players
        updateResourceCounters();

        return playerGUIs.get(player).startSettlementTurn(validSpots);
    }

    @Override
    public void playerHasTargetResources(Player player) {
        if(playersWhoHaveNotDiscarded.contains(player)) {
            playersWhoHaveNotDiscarded.remove(player);
        }else{
            throw new IllegalStateException("Tried to remove unlisted player from playersWhoHaveNotDiscarded");
        }
    }
}