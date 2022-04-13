package settlers.board;

import settlers.Player;

public class EdgeImpl implements Edge{

    private Player player;

    // I don't implement a constructor, because the default one will set player to null, as it should start

    /**
     * @return the player who owns the road, or null if there is no road
     */
    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * @param player that owns this Road
     * @throws IllegalArgumentException if a different player already has this road
     */
    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }
}
