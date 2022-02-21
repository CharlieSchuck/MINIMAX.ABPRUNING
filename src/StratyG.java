//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class StratyG implements Player {
    private StratyG.CardComparator comp;
    private ArrayList<Card.Suit> magicSuits;

    public StratyG() {
    }

    public static Player getPlayer() {
        return new StratyG();
    }

    public Card playFirstCard(ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump, int tricks1, int tricks2) {
        this.check_reset(hand, playedCards, trump, tricks1, tricks2);
        Card card;
        if (playedCards.size() >= 2) {
            card = (Card)playedCards.get(playedCards.size() - 1);
            Card myLast = (Card)playedCards.get(playedCards.size() - 2);
            if (!follows(card, myLast, trump) && !this.magicSuits.contains(myLast.suit)) {
                this.magicSuits.add(myLast.suit);
            }
        }

        hand.sort(this.comp);
        Iterator var8 = hand.iterator();

        while(var8.hasNext()) {
            card = (Card)var8.next();
            if (this.magicSuits.contains(card.suit)) {
                return card;
            }
        }

        return selectLast(hand);
    }

    public Card playSecondCard(ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump, int tricks1, int tricks2) {
        this.check_reset(hand, playedCards, trump, tricks1, tricks2);
        ArrayList<Card> playable = getPlayable(hand, playedCards, trump);
        playable.sort(this.comp);
        hand.sort(this.comp);
        if (playable.isEmpty()) {
            return selectFirst(hand);
        } else {
            Card lastPlayed = selectLast(playedCards);
            Iterator var9 = playable.iterator();

            while(var9.hasNext()) {
                Card card = (Card)var9.next();
                if (this.comp.compare(card, lastPlayed) > 0) {
                    return card;
                }
            }

            return selectFirst(playable);
        }
    }

    private void check_reset(ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump, int tricks1, int tricks2) {
        if (tricks1 + tricks2 == 0) {
            this.comp = new StratyG.CardComparator(trump);
            this.magicSuits = new ArrayList();
        }

    }

    private static Card selectFirst(ArrayList<Card> cards) {
        return (Card)cards.get(0);
    }

    private static Card selectLast(ArrayList<Card> cards) {
        return (Card)cards.get(cards.size() - 1);
    }

    private static boolean follows(Card card, Card last, Card trump) {
        return card.suit == last.suit || card.suit == trump.suit;
    }

    private static ArrayList<Card> getPlayable(ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump) {
        Card last = selectLast(playedCards);
        ArrayList<Card> playable = new ArrayList();
        Iterator var6 = hand.iterator();

        while(var6.hasNext()) {
            Card card = (Card)var6.next();
            if (follows(card, last, trump)) {
                playable.add(card);
            }
        }

        return playable;
    }

    private static class CardComparator implements Comparator<Card> {
        private Card trump;

        public CardComparator(Card t) {
            this.trump = t;
        }

        public int compare(Card a, Card b) {
            int av = a.value.ordinal() + (a.suit == this.trump.suit ? 10 : 0);
            int bv = b.value.ordinal() + (b.suit == this.trump.suit ? 10 : 0);
            return av - bv;
        }
    }
}
