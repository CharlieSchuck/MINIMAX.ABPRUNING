//SIMON '22.

import java.util.ArrayList;

public interface Player {

    public Card playFirstCard(ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump, int tricks1, int tricks2);
    // hand is your hand. playedCards are the cards that have already been played (in order of play).
    // trumpCard is the trump card. tricks1 (tricks2) is the number of tricks player1 (player2) has already taken.
    // returns the card that the first player in a round will play.

    public Card playSecondCard(ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump, int tricks1, int tricks2);
    // returns the card that the second player in a round will play.
}