//SIMON '22.

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.Callable;

public class Tournament {

    private static final int maxTime = 10;//10    // number of seconds allowed per card.
    private static final int overTime = 60;//60   // number of seconds before player forfeits all hands.
    private static final int numMatches = 50; // number of hands between each player pair.
    private static final Player LOSER = LoserP.getPlayer();

    private static class Contestant implements Comparable<Contestant> {
        Player player;
        String name;
        int wins;
        int matchWins;

        public Contestant(Player player, String name) {
            this.player = player; this.name = name; wins = 0; matchWins = 0;
        }
        public int compareTo(Contestant other) { return other.wins - wins; }
        public void won() { wins++; matchWins++; }
        public void reset() { matchWins = 0; }
        public String toString() { return name + "(" + matchWins + ":" + wins + ")"; }
    }

    private static final Contestant[] contestants = {
            //new Contestant(SmartyP.getPlayer(), "SmartyP"),
            new Contestant(StratyG.getPlayer(), "StratyG"),
            new Contestant(CharlesS.getPlayer(),"CharlesS"),
            //new Contestant(RandyP.getPlayer(), "RandyP"),

            // new Contestant(DonaldS.getPlayer(), "DonaldS"),
            // new Contestant(AmandaS.getPlayer(),"Amanda"),
            // new Contestant(BrianP.getPlayer(),"Brian"),
            // new Contestant(CameronB.getPlayer(),"Cameron"),
            // new Contestant(CassieD.getPlayer(),"Cassie"),
            // new Contestant(EdwardP.getPlayer(),"Edward"),
            // new Contestant(EricG.getPlayer(),"Eric"),
            // new Contestant(HenryM.getPlayer(),"Henry"),
            // new Contestant(IbrokhimkhujaS.getPlayer(),"Ibrokhimkhuja"),
            // new Contestant(JaimeB.getPlayer(),"Jaime"),
            // new Contestant(JosephM.getPlayer(),"Joseph"),
            // new Contestant(KevinC.getPlayer(),"Kevin"),
            // new Contestant(LoganV.getPlayer(),"Logan"),
            // new Contestant(LukasS.getPlayer(),"Lukas"),
            // new Contestant(MaximilianHo.getPlayer(),"Maximilian"),
            // new Contestant(MaximilianHr.getPlayer(),"Maximilian"),
            // new Contestant(OmonC.getPlayer(),"Omon"),
            // new Contestant(PavelB.getPlayer(),"Pavel"),
            // new Contestant(ZacharyM.getPlayer(),"Zachary"),
    };

    public static class PlayerCallable implements Callable<Card> {
        private Player player;
        private ArrayList<Card> hand;
        private ArrayList<Card> playedCards;
        private Card trump;
        private int tricks1, tricks2;
        private Card firstCard;

        public PlayerCallable(Player player, ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump,
                              int tricks1, int tricks2, Card firstCard) {
            this.player = player;
            this.hand = hand;
            this.playedCards = playedCards;
            this.trump = trump;
            this.tricks1 = tricks1;
            this.tricks2 = tricks2;
            this.firstCard = firstCard;
        }

        public Card call() {
            return firstCard == null
                    ? player.playFirstCard(hand, playedCards, trump, tricks1, tricks2)
                    : player.playSecondCard(hand, playedCards, trump, tricks1, tricks2);
        }
    }

    public static Card timePlayer(Contestant contestant, ArrayList<Card> hand, ArrayList<Card> playedCards,
                                  Card trump, int tricks1, int tricks2, Card firstCard) {
        ArrayList<Card> handCopy = new ArrayList<Card>(); // copy in case play{First,Second}Card changes them.
        for(Card c : hand)
            handCopy.add(new Card(c));
        ArrayList<Card> playedCopy = new ArrayList<Card>();
        for(Card c : playedCards)
            playedCopy.add(new Card(c));
        Card trumpCopy = new Card(trump);
        Card card = null;
        final ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            final Future<Card> f = service.submit(new PlayerCallable(contestant.player, handCopy, playedCopy,
                    trumpCopy, tricks1, tricks2, firstCard));
            card = f.get(maxTime, TimeUnit.SECONDS);
            //System.out.println(contestant.name + "(" + (firstCard==null ? "first " : "second ") + hand.size() + ") took " +
            //((System.nanoTime() - startTime) / 1000000000.0) + " seconds.");

            if(card == null)
                System.out.println(contestant.name + " returned a null card.");
            else if(hand.indexOf(card) == -1) {
                System.out.println(contestant.name + " forfeits - no such card");
                card = null;
            }
            else if(firstCard != null) {
                boolean canFollow = false;
                for(Card c : hand)
                    if(c.suit == firstCard.suit)
                        canFollow = true;
                if(canFollow && card.suit != firstCard.suit && card.suit != trump.suit) {
                    //if(canFollow) System.out.println("CAN FOLLOW BUT");
                    //if(card.suit != firstCard.suit) System.out.println("VIOLATES SUIT OF CARD BEFORE: " + card.suit);
                    //if(card.suit != trump.suit) System.out.println("VIOLATES TRUMP SUIT: " + trump.suit);
                    System.out.println(contestant.name + " forfeits - illegal play");
                    card = null;
                }
            }
        }
        catch (final TimeoutException e) {
            System.out.println(contestant.name + " (playing " + (firstCard==null ? "first" : "second") + ") took too long.");
            card = null;

        }
        catch (final Exception e) {
            System.out.println(contestant.name + " caused an exception: " + e.getCause());
            card = null;
        }
        finally {
            service.shutdownNow();
            try {
                if(!service.awaitTermination(overTime, TimeUnit.SECONDS)) {
                    System.out.println(contestant.name + " has taken excessive time and forfeits all hands.");
                    contestant.player = LOSER;
                }
            }
            catch (InterruptedException e) {}
            return card;
        }
    }

    private static boolean playHand(Contestant c1, Contestant c2) {
        Deck deck = new Deck();
        deck.shuffle();
        int numCards = 7;  // deal two hands of 7 cards and trump card
        ArrayList<Card> hand1 = new ArrayList<Card>();
        ArrayList<Card> hand2 = new ArrayList<Card>();
        ArrayList<Card> playedCards = new ArrayList<Card>();
        for(int i=0; i<numCards; i++) {
            hand1.add(deck.get(2*i));
            hand2.add(deck.get(2*i+1));
        }
        Card trump = deck.get(2*numCards);
        int tricks1 = 0, tricks2 = 0;
        //System.out.println("Trump = " + trump + "\n" + c1.name + ": " + hand1 + "\n" + c2.name + ": " + hand2);
        while(!hand2.isEmpty()) {  // play hand
            Card card1 = timePlayer(c1, hand1, playedCards, trump, tricks1, tricks2, null);
            if(card1 == null) {
                tricks1 = 0; tricks2 = 7;
                break;
            }
            hand1.remove(card1);
            playedCards.add(card1);
            Card card2 = timePlayer(c2, hand2, playedCards, trump, tricks1, tricks2, card1);
            if(card2 == null) {
                tricks1 = 7; tricks2 = 0;
                break;
            }
            hand2.remove(card2);
            playedCards.add(card2);
            if(card1.greater(card2, trump))
                tricks1++;
            else
                tricks2++;
        }
        if(tricks1 > 3) {
            c1.won();
            //System.out.println(c1.name + " wins the hand.");
            return true;
        }
        else {
            c2.won();
            //System.out.println(c2.name + " wins the hand.");
            return false;
        }
    }

    public static void games(Contestant contestant1, Contestant contestant2) {
        int c1wins = 0, c2wins = 0;
        //System.out.println(contestant1.name + " vs. " + contestant2.name);
        for(int n=0; n<numMatches; n++) {
            System.out.print(".");
            //System.out.println((++k));
            if(playHand(contestant1, contestant2))
                c1wins++;
            else
                c2wins++;
            if(playHand(contestant2, contestant1))
                c2wins++;
            else
                c1wins++;
            //System.out.println(contestant1 + " " + contestant2);
        }
        System.out.println();
        System.out.println(contestant1 + " " + c1wins + " vs. " + contestant2 + " " + c2wins);
    }


    public static void main(String[] args) {
        int k = 0;
        for(int i=0; i<contestants.length; i++)
            for(int j=i+1; j<contestants.length; j++) {
                System.out.println(contestants[i].name + " vs. " + contestants[j].name);
                contestants[i].reset(); contestants[j].reset();
                for(int n=0; n<numMatches; n++) {
                    System.out.print((++k) + ": ");
                    playHand(contestants[i], contestants[j]);
                    playHand(contestants[j], contestants[i]);
                    System.out.println(contestants[i] + " " + contestants[j]);
                }
            }
        Arrays.sort(contestants);
        for(int i=0; i<contestants.length; i++){
            System.out.println(contestants[i]);
        }

    }
}