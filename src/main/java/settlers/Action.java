package settlers;

import settlers.board.Hex;
import settlers.board.Vertex;
import settlers.board.Edge;

/**
 * An action type which can be preformed by players
 */
public class Action {

    public enum ActionType{
        PASS,ROAD,CITY,SETTLEMENT,THIEF
    }

    public Hex hex;
    public Vertex vertex;
    public Edge road;
    public Player otherPlayer;
    public final Player player;

    public final ActionType type;

    public Action(Player player, ActionType type){
        this.player = player;
        this.type = type;
    }
}