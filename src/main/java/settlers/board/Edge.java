package settlers.board;

import settlers.Player;

public interface Edge {

    /**
     *
     * @return the player who owns the road, or null if there is no road
     */
    Player getPlayer();

    /**
     * @param player that owns this Road
     * @throws IllegalArgumentException if a different player already has this road
     */
    void setPlayer(Player player);

}