package settlers;

import org.junit.jupiter.api.Test;
import settlers.board.HexImpl;
import settlers.board.VertexImpl;
import settlers.card.Resource;
import settlers.gui.GUIMainDummyImpl;

import static org.junit.jupiter.api.Assertions.*;

public class Main2PointCardTest {
    // I'm not really sure how to test this
    // because it depends on so many disparate things working together
    // so if I manually alter something with a protected method in one place, it might not change things elsewhere

    // Let's start by listing changes that I know I need to make
    // I need to make the player know when it gets largest army, so as to increase its victory points by 2
    // Wait, this won't work, let's do this in the order of what happens
    // Whenever a player plays a knight card, its knight counter goes up by 1
    // If that Knight counter reaches 3 and no one else has 3, that player has largest army and gets 2 points
    // If another player plays more knights and gets a counter to 4, that player now gets 2 points and the
        // previous player loses 2 points

    // It looks like I will have to do everything from Main
    // because only Main will be able to see who has what
    // so Main will increase each player's Knight counter
    // if the new counter is greater than what Main recorded as the longest, Main will have to give the player
        // largest army and take from the old player the largest army card
    // this means that Main will need access in Player to check its victory point total, and also to alter it
    // it will also need to record which player has the largest army, and how big that army is
    // let's start altering interfaces

    // Let's start writing tests

    private final Main main;
    private final Player player;
    private final Player player2;

    public Main2PointCardTest() {
        main = new MainImpl(4, new GUIMainDummyImpl());
        player = main.getPlayers().get(0);
        player2 = main.getPlayers().get(2);
    }

    private void playXKnights(Player knightPlayer, int number) {
        for (int i = 0; i < number; i++) {
            // since this is in test mode, I can play a Knight card without expending a resource
            // the following shouldn't have a problem that it is using imaginary locations
            main.playKnight(knightPlayer, new VertexImpl(), new HexImpl(Resource.MISC));
        }
    }

    // I need to have players play Knights, and then check if they now have the largest army
    @Test
    public void need3KnightsLargestArmy() {
        assertEquals(0, player.getVictoryPoints());
        playXKnights(player, 1);
        assertEquals(0, player.getVictoryPoints());
        playXKnights(player, 1);
        assertEquals(0, player.getVictoryPoints());
        playXKnights(player, 1);
        // now that we have played 3 knights, we should have Largest Army
        assertEquals(2, player.getVictoryPoints());
    }

    // test that having knights isn't enough to get the army if they aren't played
    @Test
    public void knightsMustBePlayedForArmy() {
        // I will give the player 14 cards, to ensure that at least 3 are knights
        for (int i = 0; i < 14; i++) {
            main.buildDevelopmentCard(player);
        }
        assertEquals(0, player.getVictoryPoints());
    }

    // test that you can get the army even if another player played 2 knights
    @Test
    public void armyNotBlockedBy2Knights() {
        playXKnights(player2, 2);
        playXKnights(player, 3);

        assertEquals(2, player.getVictoryPoints());
        assertEquals(0, player2.getVictoryPoints());
    }

    // test that if another player already played 3 knights, this one playing 3 doesn't change anything
    @Test
    public void if3Knights3MoreChangesNothing() {
        playXKnights(player2, 3);
        playXKnights(player, 3);

        assertEquals(0, player.getVictoryPoints());
        assertEquals(2, player2.getVictoryPoints());
    }

    // test that if another player played 3 knights, this player playing 4 moves the army
    @Test
    public void if3Knights4Steals() {
        playXKnights(player2, 3);
        playXKnights(player, 2);

        assertEquals(0, player.getVictoryPoints());
        assertEquals(2, player2.getVictoryPoints());
        playXKnights(player, 2);

        assertEquals(2, player.getVictoryPoints());
        assertEquals(0, player2.getVictoryPoints());
    }

    // test that if another player played 4 knights, 5 knights are needed to steal the army
    @Test
    public void if4Knights5Steals() {
        playXKnights(player2, 4);
        playXKnights(player, 4);

        assertEquals(0, player.getVictoryPoints());
        assertEquals(2, player2.getVictoryPoints());
        playXKnights(player, 1);

        assertEquals(2, player.getVictoryPoints());
        assertEquals(0, player2.getVictoryPoints());
    }

    // test that if a player has 7 points and plays 3 knights, that player does not win
    @Test
    public void largestArmyWith7DoesNotWin() {
        player.increaseVictoryPoints(7);
        playXKnights(player, 3);
        // if the method ends, that is a fail state. I have no idea how to actually show that, though
    }

    // I can't test the last 2, because they cause a system.exit
    // test that if a player has 8 points and plays 3 knights, that player wins
    //@Test
    public void largestArmyWith8Wins() {
        player.increaseVictoryPoints(8);
        playXKnights(player, 3);
        fail("The method should have ended. 8 + largest army should end the game");
    }

    // test that if a player has 9 points and plays 3 knights, that player wins
    //@Test
    public void largestArmyWith9Wins() {
        player.increaseVictoryPoints(9);
        playXKnights(player, 3);
        fail("The method should have ended. 9 + largest army should end the game");
    }
}
