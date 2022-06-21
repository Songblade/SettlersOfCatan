package settlers.gui;

import settlers.*;
import settlers.card.DevelopmentCard;
import settlers.card.Resource;
import settlers.board.*;

import java.util.*;

public class GUIMainImpl implements GUIMain {

    private Main main;
    private HashMap<Player,GUIPlayer> playerGUIs;

    private GUIThreadManager threadManager;

    private boolean unlimitedResources = true;

    //Functional

    //Discard on 7 related
    //(playersWhoHaveNotDiscarded stores <The player who has not discarded, The quantity of cards he must have in order to move on>)
    private Map<Player,Integer> playersWhoHaveNotDiscarded = new HashMap<>();

    //Trade related

    private Map<Resource,Integer> currentTradeRequest = new HashMap<>();
    private Set<Player> playersWithTradeRequests = new HashSet<>();
    private Player playerRequestingTrade = null;

    //Other

    //This (mainPhase) is important so we can identify when to and not to end turns after a road was placed
    private boolean mainPhase = false;

    public GUIMainImpl(Main main) {
        this.main = main;

        playerGUIs = new HashMap();

        for(int i = 0; i < main.getPlayers().size(); i++){
            Player player = main.getPlayers().get(i);
            playerGUIs.put(player, new GUIPlayerImpl(this,main.getBoard(),player,main.getPlayers()));
        }

        threadManager = new GUIThreadManagerImpl();

        //for(GUIPlayer gui : playerGUIs){
        //    gui.startSettlementTurn(main.getAvailableSettlementSpots(main.getPlayers().get(0)));
        //}
    }

    @Override
    public void pass(){
        threadManager.stopHold();
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

        if(!mainPhase){
            threadManager.stopHold();
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
        updateDevelopmentCounters(player);
    }

    @Override
    public boolean playKnight(Player stealer, Vertex settlement, Hex location){
        boolean toReturn = main.playKnight(stealer,settlement,location);
        updateGUISAfterThiefMove(location);
        updateDevelopmentCounters(stealer);
        updateKnightCounters(stealer);
        return toReturn;
    }

    @Override
    public boolean playYearOfPlenty(Player player, Resource firstResource, Resource secondResource){
        boolean toReturn = main.playYearOfPlenty(player,firstResource,secondResource);
        updateResourceCounters();
        updateDevelopmentCounters(player);
        return toReturn;
    }

    @Override
    public boolean playMonopoly(Player player, Resource resource){
        boolean toReturn = main.playMonopoly(player,resource);
        updateResourceCounters();
        updateDevelopmentCounters(player);
        return toReturn;
    }

    @Override
    public boolean playRoadBuilding(Player player, Edge firstLocation, Edge secondLocation){
        boolean toReturn = main.playRoadBuilding(player,firstLocation,secondLocation);

        for(GUIPlayer gui : playerGUIs.values()){
            gui.setRoad(player,firstLocation);
            gui.setRoad(player,secondLocation);
        }

        updateDevelopmentCounters(player);

        return toReturn;
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
     * Gets the locations where this player can build a road, given that the player is going to build
     * on this edge first. Used to find the second edge for playRoadBuilding
     * @param player building the road
     * @param roadToBuild where the player will build a road, but hasn't yet
     * @return a Set of Edges where this player could build once they build roadToBuild
     */
    @Override
    public Set<Edge> getAvailableRoadSpotsGivenEdge(Player player, Edge roadToBuild){
        return main.getAvailableRoadSpotsGivenEdge(player,roadToBuild);
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

    /**
     * Updates all player's development card counters
     * @param initiater the player who triggered this method
     */
    private void updateDevelopmentCounters(Player initiater){
        for(Player player : playerGUIs.keySet()){
            playerGUIs.get(player).updateDevelopmentCounters(initiater);
        }
    }

    /**
     * Updates all player's played knights counters
     * @param initiater the player who triggered this method
     */
    private void updateKnightCounters(Player initiater){
        for(Player player : playerGUIs.keySet()){
            playerGUIs.get(player).updateKnightCounters(initiater);
        }
    }

    private void startMainPhase(){
        for(Player player : playerGUIs.keySet()){
            playerGUIs.get(player).startMainPhase();
        }
    }

    private void forcePlayersToDiscardHalfOfHand(){

        for(Player plr : main.getPlayers()) {
            if (plr.hasMoreThan7Cards()) {
                int targetResourceQuantity = plr.getCardNumber() / 2 + plr.getCardNumber() % 2;
                playerGUIs.get(plr).discardUntil(targetResourceQuantity);
                playersWhoHaveNotDiscarded.put(plr,targetResourceQuantity);
            }
        }

        if(playersWhoHaveNotDiscarded.size() > 0) {
            threadManager.startHold();
        }
    }

    /**
     * Starts a turn. Called by Main, updates resources and die number in GUIPlayers
     * @param player the player whose turn it is
     * @param dieRoll the sum of both die rolls
     */
    @Override
    public void startTurn(Player player, int dieRoll){
        //Updates mainPhase if needed
        if(!mainPhase){
            mainPhase = true;
            startMainPhase();
        }

        //Updates dice and resources for all players
        updateResourceCounters(dieRoll);

        //Forces players to discard half of their hand if they have more than 7 cards. Buggy so commented out
        if(dieRoll == 7){
            forcePlayersToDiscardHalfOfHand();
        }

        //Starts player's turn
        playerGUIs.get(player).startTurn(dieRoll);

        //Starts holding the thread
        threadManager.startHold();
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

        //Starts the settlement turn
        playerGUIs.get(player).startSettlementTurn(validSpots);

        threadManager.startHold();

        return playerGUIs.get(player).getLastSettlementSpot();
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

    @Override
    public void playerDiscardedCard(Player player) {
        if(playersWhoHaveNotDiscarded.get(player) == player.getCardNumber()) {
            if (playersWhoHaveNotDiscarded.containsKey(player)) {
                playersWhoHaveNotDiscarded.remove(player);
            } else {
                throw new IllegalStateException("Player tried to discard resources when they couldn't");
            }

            //If everyone has discarded the appropriate quantity of resources, stop the hold on threadManager
            if (playersWhoHaveNotDiscarded.size() == 0) {
                threadManager.stopHold();
            }
        }

        updateResourceCounters();
    }

    @Override
    public boolean canTrade(Player player, Resource resourceGiven) {
        return main.canTrade(player, resourceGiven);
    }

    @Override
    public void trade(Player player, Resource resourceGiven, Resource resourceGotten) {
        main.trade(player,resourceGiven,resourceGotten);

        updateResourceCounters();
    }

    /**
     * Clones a set of players
     * @param set the set of players
     * @return the clone of set
     */
    private Set<Player> clonePlayerSet(Set<Player> set){
        Set<Player> output = new HashSet<>();

        for(Player plr : set){
            output.add(plr);
        }

        return set;
    }

    @Override
    public void trade(Player player, Map<Resource, Integer> resourcesExchanged, Set<Player> sendTo) {
        if(true){
            playerRequestingTrade = player;
            playersWithTradeRequests = clonePlayerSet(sendTo);
            currentTradeRequest = resourcesExchanged;

            //Looks at all players in sendTo and checks if they can make the proposed trade.
            //If they can, send them the trade request.
            //If they can't, remove them from playersWithTradeRequests
            for(Player plr : sendTo){
                if(main.canTrade(plr,resourcesExchanged,false)) {
                    playerGUIs.get(plr).receiveTradeRequest(player, resourcesExchanged);
                }else{
                    playersWithTradeRequests.remove(plr);
                }
            }

            if(playersWithTradeRequests.size() == 0){
                playerGUIs.get(playerRequestingTrade).tradeRequestResponseReceived(null);
            }
        }
    }

    @Override
    public void playerDeclinedTrade(Player player) {
        if(playersWithTradeRequests.contains(player)){
            playersWithTradeRequests.remove(player);
        }else{
            throw new IllegalStateException("Player declined trade request he didn't have");
        }

        if(playersWithTradeRequests.size() == 0){
            playerGUIs.get(playerRequestingTrade).tradeRequestResponseReceived(null);
        }
    }

    @Override
    public void playerAcceptedTrade(Player player) {
        if(playersWithTradeRequests.contains(player)){
            main.trade(playerRequestingTrade,currentTradeRequest,player);
            updateResourceCounters();
            playerGUIs.get(playerRequestingTrade).tradeRequestResponseReceived(player);
        }else{
            throw new IllegalStateException("Player accepted trade request he didn't have");
        }
    }
}