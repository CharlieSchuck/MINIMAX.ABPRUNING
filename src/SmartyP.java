//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class SmartyP {
    public SmartyP() {
    }

    public static Player getPlayer() {
        return new SmartyP.SmartPlayer();
    }

    public static class SmartPlayer implements Player {
        private static final ArrayList<Card> FULL_DECK = createDeck();

        public SmartPlayer() {
        }

        private static ArrayList<Card> playableCards(ArrayList<Card> var0, ArrayList<Card> var1, Card var2) {
            Card var3 = (Card)var1.get(var1.size() - 1);
            ArrayList var4 = new ArrayList();
            Iterator var5 = var0.iterator();

            while(var5.hasNext()) {
                Card var6 = (Card)var5.next();
                if (canFollow(var6, var3, var2)) {
                    var4.add(var6);
                }
            }

            return var4;
        }

        private static boolean canFollow(Card var0, Card var1, Card var2) {
            return var0.suit == var2.suit || var0.suit == var1.suit;
        }

        private static Card selectFirst(ArrayList<Card> var0) {
            return (Card)var0.get(0);
        }

        private static Card selectLast(ArrayList<Card> var0) {
            return (Card)var0.get(var0.size() - 1);
        }

        private static ArrayList<Card> createDeck() {
            ArrayList var0 = new ArrayList(30);

            for(int var1 = 0; var1 < 10; ++var1) {
                for(int var2 = 0; var2 < 3; ++var2) {
                    var0.add(new Card(var1, var2));
                }
            }

            return var0;
        }

        private static ArrayList<Card> getUnplayed(ArrayList<Card> var0, ArrayList<Card> var1, Card var2) {
            ArrayList var3 = new ArrayList(FULL_DECK);
            var3.remove(var2);
            var3.removeAll(var0);
            var3.removeAll(var1);
            return var3;
        }

        public Card playFirstCard(ArrayList<Card> var1, ArrayList<Card> var2, Card var3, int var4, int var5) {
            SmartyP.SmartPlayer.CardComparator var6 = new SmartyP.SmartPlayer.CardComparator(var3);
            Collections.sort(var1, var6);
            getUnplayed(var1, var2, var3);
            return selectLast(var1);
        }

        public Card playSecondCard(ArrayList<Card> var1, ArrayList<Card> var2, Card var3, int var4, int var5) {
            SmartyP.SmartPlayer.CardComparator var6 = new SmartyP.SmartPlayer.CardComparator(var3);
            Collections.sort(var1, var6);
            ArrayList var7 = playableCards(var1, var2, var3);
            Collections.sort(var7, var6);
            getUnplayed(var1, var2, var3);
            if (var7.isEmpty()) {
                return selectFirst(var1);
            } else {
                Card var9 = selectLast(var2);
                Iterator var10 = var7.iterator();

                Card var11;
                do {
                    if (!var10.hasNext()) {
                        return selectFirst(var7);
                    }

                    var11 = (Card)var10.next();
                } while(var6.compare(var11, var9) <= 0);

                return var11;
            }
        }

        private static class CardComparator implements Comparator<Card> {
            private Card trump;

            public CardComparator(Card var1) {
                this.trump = var1;
            }

            public int compare(Card var1, Card var2) {
                int var3 = var1.value.ordinal() + (var1.suit == this.trump.suit ? 14 : 0);
                int var4 = var2.value.ordinal() + (var2.suit == this.trump.suit ? 14 : 0);
                return var3 - var4;
            }
        }
    }
}
