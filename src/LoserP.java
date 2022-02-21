//SIMON '22

import java.util.Random;
import java.util.ArrayList;

public class LoserP {

    public static Player getPlayer() { return new LoserPlayer(); }

    public static class LoserPlayer implements Player {


        public Card playFirstCard(ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump, int tricks1, int tricks2) {
            return null;
        }

        public Card playSecondCard(ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump,int tricks1, int tricks2) {
            return null;
        }
    }
}