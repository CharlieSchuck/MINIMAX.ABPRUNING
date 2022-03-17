//Charles Schuck
//2/25/22 --
//tested against EricG, as well as some non-minimax, non ai methods he wrote for playing the game...
//(they're a lot better at playing the game than you'd think).
//Uses Minimax with AB pruning to play the card game outlined in assignment 4

import java.util.ArrayList;
import java.util.Collections;

public class CharlesS {
    public static Player getPlayer() { return new CharlesSchuck(); }

    public static class CharlesSchuck implements Player{
        final int allTime = 9995; //maximum thinking time. (9995)
        int alpha;
        int beta;

        public Card playFirstCard(ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump, int tricks1, int tricks2) {
            double time = System.currentTimeMillis();
            alpha = -9999;
            beta = 9999;

            Node state = new Node(hand, playedCards, trump, tricks1, tricks2);
            state.player1Sort();
            Node resultN = alphaBetaParkingLot(state, true, alpha, beta, time);

            return resultN.getPlay();
        }

        public Card playSecondCard(ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump, int tricks1, int tricks2) {
            double time = System.currentTimeMillis();
            alpha = -9999;
            beta = 9999;

            Node state = new Node(hand, playedCards, trump, tricks1, tricks2);
            state.player2Sort();
            Node resultN = alphaBetaParkingLot(state, false, alpha, beta, time);

            return resultN.getPlay();
        }

        public Node alphaBetaParkingLot(Node state, boolean player1, int alpha, int beta, double time){
            int bestEvaluation;
            Node bestPlay = new Node();
            if((state.remainingCards.isEmpty() && player1) || System.currentTimeMillis() > time + allTime){ //the exit scenario... If you're player1 though, you're not finished.
                return state;
            }

            if(player1){
                bestEvaluation = -9999;

                for(int i = 0; i < state.hand.size(); i++){ //for each card in our hand.
                    ArrayList<Card> childHand = (ArrayList<Card>) state.hand.clone();
                    ArrayList<Card> childPlayedCards = (ArrayList<Card>) state.playedCards.clone();
                    Card childsPlay = childHand.remove(i);
                    childPlayedCards.add(childsPlay);

                    Node child = new Node((ArrayList<Card>) state.remainingCards.clone(), childPlayedCards, state.trump, state.tricks1, state.tricks2, childHand);
                    child.assume();
                    //our sim of P2's hand will be this state's remaining cards... similarly, its remaining cards will be our new hand

                    //the child of a player1 is a player2.
                    if(child.hand.size() > 1){
                        child.player2Sort();
                    }

                    int tempEvaluation = alphaBetaParkingLot(child, false, alpha, beta, time).evaluate();

                    if(tempEvaluation > bestEvaluation){
                        bestEvaluation = tempEvaluation;
                        bestPlay = child;
                    }
                    if(tempEvaluation > alpha){
                        alpha = tempEvaluation;
                    }
                    if(beta <= alpha){
                        break;
                    }

                }
            }
            else{
                bestEvaluation = 9999;

                for(int i = 0; i < state.hand.size(); i++){
                    ArrayList<Card> childHand = (ArrayList<Card>) state.hand.clone();
                    ArrayList<Card> childPlayedCards = (ArrayList<Card>) state.playedCards.clone();
                    Card previousPlay = childPlayedCards.get(childPlayedCards.size() - 1);
                    Card childsPlay = childHand.remove(i);
                    childPlayedCards.add(childsPlay);

                    if(childsPlay.suit == previousPlay.suit || childsPlay.suit == state.trump.suit || i == state.hand.size() - 1){ //if this play matches either of the suits, or if it is the last card in our hand.
                        //this, assuming our hand has been sorted in terms of utility, suceeds in tossing off the lowest card... it will nearly always be the case that this play fails to beat the previous play.
                        Node child = new Node((ArrayList<Card>) state.remainingCards.clone(), childPlayedCards, state.trump, state.tricks1, state.tricks2, childHand);
                        child.assume();

                        if(child.hand.size() > 1){
                            child.player1Sort();
                        }

                        if(previousPlay.greater(childsPlay, state.trump)){
                            child.addTricks1();
                        }
                        else{
                            child.addTricks2();
                        }

                        int tempEvaluation = alphaBetaParkingLot(child, true, alpha, beta, time).evaluate();

                        if(tempEvaluation < bestEvaluation){
                            bestEvaluation = tempEvaluation;
                            bestPlay = child;
                        }
                        if(tempEvaluation < beta){
                            beta = tempEvaluation;
                        }
                        if(beta <= alpha){
                            break;
                        }
                    }
                }
            }
            return bestPlay;
        }
    }

    static class Node{
        public ArrayList<Card> hand;
        public ArrayList<Card> playedCards;
        public Card trump;
        public int tricks1;
        public int tricks2;
        public ArrayList<Card> remainingCards;

        Node(){

        }

        Node(ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump, int tricks1, int tricks2){ //top constructor
            this.hand = hand;
            this.playedCards = playedCards;
            this.trump = trump;
            this.tricks1 = tricks1;
            this.tricks2 = tricks2;
            this.remainingCards = new ArrayList<Card>();

            generateRemainingCards();
        }

        Node(ArrayList<Card> hand, ArrayList<Card> playedCards, Card trump, int tricks1, int tricks2, ArrayList<Card> remainingCards){ //child constructor
            this.hand = hand;
            this.playedCards = playedCards;
            this.trump = trump;
            this.tricks1 = tricks1;
            this.tricks2 = tricks2;
            this.remainingCards = remainingCards;
        }

        public int evaluate(){
            return tricks1 - tricks2;
        }
        public void addTricks1(){
            this.tricks1++;
        }
        public void addTricks2(){
            this.tricks2++;
        }
        public Card getPlay(){
            return playedCards.get(playedCards.size() - 1);
        }
        public void generateRemainingCards(){
            for(int i = 0; i < 10; i++) {
                for (int j = 0; j < 3; j++) {
                    Card temp = new Card(i, j);
                    //so, if the card in question is not already represented, it is a remaining card.
                    if (!hand.contains(temp) && !playedCards.contains(temp) && !temp.equals(trump)) {
                        this.remainingCards.add(temp);
                    }
                }
            }
        }

        public void assume(){
            //let's some little assumptions, too.
            if(playedCards.size() > 2 && playedCards.size() % 2 == 0){ //if we are player 1 & at least one trick is completed.
                Card opponentsLastPlay = playedCards.get(playedCards.size() - 2);
                Card ourLastPlay = playedCards.get(playedCards.size() - 3);
                //because someone must follow suit if they can follow suit, if someone failed to follow suit we can assume they have no cards of those two suits.
                if(opponentsLastPlay.suit != ourLastPlay.suit && opponentsLastPlay.suit != trump.suit){ //if opponent did not follow suit
                    for(int i = 0; i < remainingCards.size(); i++){
                        if(remainingCards.get(i).suit == ourLastPlay.suit || remainingCards.get(i).suit == trump.suit){ //if the card in question would have enabled our opponent to follow suit
                            this.remainingCards.remove(remainingCards.get(i));
                        }
                    }
                }
            }
            //So far as I can tell, there are no admissable assumptions we can make about what our opponent is about to play if we are player2...
        }

        public void player1Sort(){ //sorted from card that beats the most remaining cards to the card that beats the least.
            int[] weights = new int[hand.size()];
            for(int i = 0; i < hand.size(); i++){
                int weight = 0;

                for(int j = 0; j < remainingCards.size(); j++){
                    if(hand.get(i).greater(remainingCards.get(j), trump)){
                        weight++;
                    }
                }
                weights[i] = weight;
            }
            quickSort(weights, 0, hand.size() - 1);
        }
        public void player2Sort(){ //cards that win, sorted by the number of remaining cards they beat (lowest to highest) followed by cards that lose sorted by the number of remaining cards they beat, highest to lowest.
            int[] weights = new int[hand.size()];
            for(int i = 0; i < hand.size(); i++){
                int weight = 0;

                if(hand.get(i).greater(playedCards.get(playedCards.size() - 1), trump)){
                    weight = 1000;

                    for(int j = 0; j < remainingCards.size(); j++){
                        if(hand.get(i).greater(remainingCards.get(j), trump)){
                            weight--;
                        }
                    }
                }
                else{
                    for(int j = 0; j < remainingCards.size(); j++){
                        if(hand.get(i).greater(remainingCards.get(j), trump)){
                            weight++;
                        }
                    }
                }
                weights[i] = weight;
            }
            quickSort(weights, 0, hand.size() - 1);
        }

        //I wrote quicksort because I couldn't add a 'weight' field to the cards themselves.-- So I sort my [] of weights and the AL of cards in the same breath.
        //Probably a better way... whatever.

        //https://www.geeksforgeeks.org/quick-sort/
        public void quickSort(int[] weights, int l, int h){
            if(l < h){
                int p = partition(weights, l, h);
                quickSort(weights, l, p - 1);
                quickSort(weights,p + 1, h);
            }
        }
        //https://www.geeksforgeeks.org/quick-sort/
        public int partition(int[] weights, int l, int h){
            int piv = weights[h];
            int i = l - 1;
            for(int j = l; j <= h - 1; j++){
                if(weights[j] > piv){
                    i++;
                    swap(weights, i, j);
                }
            }
            swap(weights, i + 1, h);
            return (i + 1);
        }
        //https://www.geeksforgeeks.org/quick-sort/
        public void swap(int[] weights, int i, int j){
            int temp = weights[i];
            weights[i] = weights[j];
            weights[j] = temp;

            Collections.swap(hand, i, j);
        }
    }
}
